package com.dreamtec.bsp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.dreamtec.bsp.bean.MonthlySummary;
import com.dreamtec.bsp.utils.ExcelUtil;

import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Helper to create a summary sheet for workbook using list of MonthlySummary.<br>
 */
public class SummarySheetHelper {

    public static void addSummarySheetToWorkbook(Workbook excel, ExcelUtil excelUtil,  List<MonthlySummary> list) {
        System.out.print("        Preparing summary sheet");

        // Create the 'Summary' sheet
        Sheet sheet = excel.createSheet("Summary");
        excel.setSheetOrder("Summary", 0);
        excel.setActiveSheet(0);
        excel.setSelectedTab(0);

        // Add headers
        Row header = sheet.createRow(0);
        excelUtil.setCellStringValue(header, 1, "OUT");
        excelUtil.setCellStringValue(header, 2, "IN");
        excelUtil.setCellStringValue(header, 3, "BALANCE");

        // Sort the list chronologically
        Collections.sort(list);

        // Get list of all accounts in summary list
        List<String> accounts = getAccountList(list);
        System.out.println(" for '" + accounts.size() + "' accounts.");

        // Prepare data for the summary
        Map<LocalDate, Map<String, MonthlySummary>> summary = prepareSummaryData(accounts, list);
        
        // Output the summary
        int rowIndex = 2;
        for(Entry<LocalDate, Map<String, MonthlySummary>> e1 : summary.entrySet()) {
            for(Entry<String, MonthlySummary> e2 : e1.getValue().entrySet()) {
                Row row = sheet.createRow(rowIndex++);
                excelUtil.setCellStringValue(row, 0, e2.getKey());

                MonthlySummary s = e2.getValue();
                if (s != null) {
                    excelUtil.setCellFormula(row, 1, s.getRefOut());
                    excelUtil.setCellFormula(row, 2, s.getRefIn());
                    excelUtil.setCellFormula(row, 3, s.getRefBalance());
                }
            }

            Row row = sheet.createRow(rowIndex++);
            excelUtil.setCellMonthValue(row, 0, e1.getKey());
            excelUtil.setCellFormula(row, 1, "sum(B" + (rowIndex - accounts.size()) + ":B" + (rowIndex - 1) + ")");
            excelUtil.setCellFormula(row, 2, "sum(C" + (rowIndex - accounts.size()) + ":C" + (rowIndex - 1) + ")");
            excelUtil.setCellFormula(row, 3, "sum(D" + (rowIndex - accounts.size()) + ":D" + (rowIndex - 1) + ")");
            excelUtil.setSummaryRowBorders(row, 4);

            rowIndex++;
        }

        FormulaEvaluator e = excel.getCreationHelper().createFormulaEvaluator();
        e.evaluateAll();

        sheet.setZoom(150);

        sheet.autoSizeColumn(0);
        sheet.setColumnWidth(1, 12 * 256);
        sheet.setColumnWidth(2, 12 * 256);
        sheet.setColumnWidth(3, 12 * 256);
    }

    /**
     * Get list of accounts in the list of summary.<br>
     * @param list
     * @return
     */
    private static List<String> getAccountList(List<MonthlySummary> list) {
        List<String> accounts = new ArrayList<String>();
        for(MonthlySummary s : list) {
            if(!accounts.contains(s.getAccountKey())) {
                accounts.add(s.getAccountKey());
            }
        }
        Collections.sort(accounts);
        return accounts;
    }

    private static Map<LocalDate, Map<String, MonthlySummary>> prepareSummaryData(List<String> accounts, List<MonthlySummary> list) {
        Map<LocalDate, Map<String, MonthlySummary>> res = new LinkedHashMap<LocalDate, Map<String, MonthlySummary>>();
        for(MonthlySummary s : list) {
            LocalDate month = s.getMonth();
            if(!res.containsKey(month)) {
                Map<String, MonthlySummary> map = new TreeMap<String, MonthlySummary>();
                for(String a : accounts) {
                    map.put(a, null);
                }
                res.put(month, map);
            }
            Map<String, MonthlySummary> map = res.get(month);
            map.put(s.getAccountKey(), s);
        }

        return res;
    }
    
}
