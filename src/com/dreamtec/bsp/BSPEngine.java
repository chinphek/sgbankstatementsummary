package com.dreamtec.bsp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.dreamtec.bsp.bean.MonthlySummary;
import com.dreamtec.bsp.statement.BankStatementFactory;
import com.dreamtec.bsp.statement.IBankStatement;
import com.dreamtec.bsp.statement.Transaction;
import com.dreamtec.bsp.utils.ExcelUtil;

import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Bank Statement Parser Engine.<br>
 * 
 * @author chinphek
 */
public class BSPEngine {
    private final Workbook excel = new XSSFWorkbook();
    private final ExcelUtil excelUtil;
    private final List<IBankStatement> statements = new ArrayList<IBankStatement>();

    public BSPEngine() {
        excelUtil = new ExcelUtil(excel);
    }

    /**
     * Add file into engine.<br>
     * 
     * @param file
     * @return Class simple name if handler is found, else return null.
     */
    public String add(final File file) {
        if (file == null) {
            throw new IllegalArgumentException("Input 'file' cannot be null.");
        }

        final IBankStatement statement = BankStatementFactory.getHandler(file);
        if (statement != null) {
            statements.add(statement);
            return statement.getAccountName();
        }

        return null;
    }

    /**
     * Process all statement files added to the engine,
     * provided a handler is found for that statement file.<br>
     * <br>
     * Statement files are grouped together if they belong to the same account.
     * Transaction records in each statement file are parsed and converted into
     * a common Transaction Java object. Transactions from multiple files are
     * combined and overlapping transactions are discarded.
     */
    public void process() {
        // Sort the statements
        Collections.sort(statements);

        // Loop through all statements and group together if they belongs to the same account
        Map<String, List<IBankStatement>> mapAccounts = new LinkedHashMap<String, List<IBankStatement>>();
        for (IBankStatement s : statements) {
            String key = s.getAccountKey();
            if (mapAccounts.containsKey(key)) {
                mapAccounts.get(key).add(s);
            } else {
                List<IBankStatement> list = new ArrayList<IBankStatement>();
                list.add(s);
                mapAccounts.put(key, list);
            }
        }

        // List of monthly summaries
        List<MonthlySummary> listSummaries = new ArrayList<MonthlySummary>();

        // Loop through all accounts and get the list of transactions within.
        // Combine transactions if acccount has multiple statements.
        for (Entry<String, List<IBankStatement>> e : mapAccounts.entrySet()) {
            System.out.println("        Processing " + e.getKey());
            List<Transaction> transactions = null;

            List<IBankStatement> list = e.getValue();
            transactions = list.get(0).process();

            for(int i = 1; i < list.size(); i++) {
                List<Transaction> transactions2 = list.get(i).process();
                System.out.println("            Combining '" + (transactions.size() + transactions2.size()) + "' transactions");
                transactions = combineTransactions(transactions, transactions2);
            }

            List<MonthlySummary> s = addTransactionsToSheet(e.getKey(), transactions);
            listSummaries.addAll(s);
        }

        //Add summary sheet
        SummarySheetHelper.addSummarySheetToWorkbook(excel, excelUtil, listSummaries);
    }

    /**
     * Save workbook to a file.<br>
     * @param filename
     * @throws FileNotFoundException
     */
    public void save(String filename) throws FileNotFoundException {
        FileOutputStream outputStream = new FileOutputStream(filename);
        try {
            excel.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Combine transactions from 2 different list.<br>
     * <br>
     * Transactions are combined in chronological order with the earlist transaction first.
     * One use case is for statements that are downloaded on a monthly basis.
     * The algorithm works easily when there are no overlapping transactions. In the event of overlapping
     * transactions, it is handled on a day to to day basis. Usually when transactions are exported
     * from the online banking website, it will contain all transactions for that day. Hence, even
     * when transactions are overlapping in multiple statements, the same transactions for that day
     * is expected to appear in each statement. Informtion is provided on the number of overlapping 
     * transaction discarded. In the event that transactions for that day are inconsistent, a warning
     * will be provided. In either cases, it is recommended for users to check the transactions manually
     * 
     * @param list1
     * @param list2
     * @return
     */
    private List<Transaction> combineTransactions(List<Transaction> list1, List<Transaction> list2) {
        //If either list is empty, no need to combine.
        if(list1.size() == 0) {
            return list2;
        } else if(list2.size() == 0) {
            return list1;
        }

        //Sort by ascending date
        Collections.sort(list1);
        Collections.sort(list2);

        List<Transaction> t = new ArrayList<Transaction>();
        int i1 = 0;
        int i2 = 0;

        Transaction t1 = list1.get(i1);
        Transaction t2 = list2.get(i2);

        while(i1 < list1.size() && i2 < list2.size()) {
            if(t1.getDate().isBefore(t2.getDate())) {
                t.add(t1);
                i1++;
                t1 = (i1 < list1.size()) ? list1.get(i1) : null;
            } else if(t2.getDate().isBefore(t1.getDate())) {
                t.add(t2);
                i2++;
                t2 = (i2 < list2.size()) ? list2.get(i2) : null;
            } else {
                LocalDate date = t1.getDate();
                List<Transaction> daylist1 = new ArrayList<Transaction>();
                while(t1 != null && t1.getDate().isEqual(date)) {
                    daylist1.add(t1);
                    i1++;
                    t1 = (i1 < list1.size()) ? list1.get(i1) : null;
                }

                List<Transaction> daylist2 = new ArrayList<Transaction>();
                while(t2 != null && t2.getDate().isEqual(date)) {
                    daylist2.add(t2);
                    i2++;
                    t2 = (i2 < list2.size()) ? list2.get(i2) : null;
                }

                if(daylist1.size() == daylist2.size()) {
                    boolean same = true;
                    for(int i = 0; i < daylist1.size(); i++) {
                        if(!daylist1.get(i).equals(daylist2.get(i))) {
                            same = false;
                            break;
                        }
                    }
                    if(!same) {
                        System.out.println("                WARN: Discarded '" + daylist2.size() + "' inconsistent overlapping transaction(s) on '" + date.toString() + "'. Please check manually.");
                    } else {
                        System.out.println("                Discarded '" + daylist2.size() + "' overlapping transaction(s) on '" + date.toString() + "'.");
                    }
                    t.addAll(daylist1);
                } else if(daylist1.size() > daylist2.size()) {
                    System.out.println("                WARN: Discarded '" + daylist2.size() + "' inconsistent overlapping transaction(s) on '" + date.toString() + "'. Please check manually.");
                    t.addAll(daylist1);
                } else {
                    System.out.println("                WARN: Discarded '" + daylist1.size() + "' inconsistent overlapping transaction(s) on '" + date.toString() + "'. Please check manually.");
                    t.addAll(daylist2);
                }
            }
        }

        for(int i = i1; i < list1.size(); i++) {
            t.add(list1.get(i));
        }

        for(int i = i2; i < list2.size(); i++) {
            t.add(list2.get(i));
        }

        return t;
    }

    /**
     * Add transactions to a workbook sheet.<br>
     * @param sheetName
     * @param transactions
     * @return list of monthly summaries
     */
    private List<MonthlySummary> addTransactionsToSheet(String sheetName, List<Transaction> transactions) {
        System.out.println("            Adding '" + transactions.size() + "' transaction(s) to sheet '" + sheetName + "'.");

        List<MonthlySummary> listSummaries = new ArrayList<MonthlySummary>();

        Sheet sheet = excel.createSheet(sheetName);

        Row header = sheet.createRow(0);
        excelUtil.setCellStringValueCenter(header, 0, "Date");
        excelUtil.setCellStringValueCenter(header, 1, "Month");
        excelUtil.setCellStringValueCenter(header, 2, "Day");
        excelUtil.setCellStringValueCenter(header, 3, "Type");
        excelUtil.setCellStringValue(header, 4, "Description");
        excelUtil.setCellStringValue(header, 5, "Out");
        excelUtil.setCellStringValue(header, 6, "In");
        excelUtil.setCellStringValue(header, 7, "Balance");

        if (transactions != null) {
            Collections.sort(transactions);

            int rowIndex = 1;

            // Add empty row
            Row row = sheet.createRow(rowIndex);
            excelUtil.setEmptyRowStyle(row, 8);
            excelUtil.setCellStringValue(row, 4, "Balance from previous statements. Replace 0 with amount, in the box to the right ===>");
            excelUtil.setCellAmountValue(row, 7, Double.valueOf(0));
            rowIndex ++;

            // Add empty row
            row = sheet.createRow(rowIndex);
            excelUtil.setEmptyRowStyle(row, 8);
            rowIndex ++;

            LocalDate curMonth = null;
            int rowIndexCurMonth = -1;
            for (int i = 0; i < transactions.size(); i++) {
                Transaction t = transactions.get(i);

                LocalDate month = t.getDate().withDayOfMonth(1);

                if(curMonth == null) {
                    curMonth = month;
                    rowIndexCurMonth = rowIndex + 1;
                } else if(!curMonth.isEqual(month)) {
                    //Summarizes the transactions for the current month.
                    MonthlySummary s = addSummayToSheet(sheet, curMonth, rowIndexCurMonth, rowIndex);
                    listSummaries.add(s);
                    rowIndex++;

                    // Add empty row
                    row = sheet.createRow(rowIndex);
                    excelUtil.setEmptyRowStyle(row, 8);
                    rowIndex ++;
                    
                    curMonth = month;
                    rowIndexCurMonth = rowIndex + 1;
                }

                // Add 1 row of transaction
                row = sheet.createRow(rowIndex);
                rowIndex++;
                excelUtil.setCellDateValue(row, 0, t.getDate());
                excelUtil.setCellMonthValue(row, 1, t.getDate());
                excelUtil.setCellDayValue(row, 2, t.getDate().getDayOfMonth());
                excelUtil.setCellStringValueCenter(row, 3, "");
                excelUtil.setCellStringValue(row, 4, t.getDescription());
                excelUtil.setCellAmountValue(row, 5, t.getOut());
                excelUtil.setCellAmountValue(row, 6, t.getIn());
                if(t.getBalance() == null) {
                    if(rowIndex == rowIndexCurMonth) {
                        excelUtil.setCellFormula(row, 7, "H" + (rowIndex - 2) + "+G" + rowIndex + "-F" + rowIndex);
                    } else {
                        excelUtil.setCellFormula(row, 7, "H" + (rowIndex - 1) + "+G" + rowIndex + "-F" + rowIndex);
                    }
                } else {
                    excelUtil.setCellAmountValue(row, 7, t.getBalance());
                }
            }

            //Summarizes the transactions for the current month.
            MonthlySummary s = addSummayToSheet(sheet, curMonth, rowIndexCurMonth, rowIndex);
            listSummaries.add(s);
        }

        FormulaEvaluator e = excel.getCreationHelper().createFormulaEvaluator();
        e.evaluateAll();

        sheet.setZoom(150);

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);
        sheet.autoSizeColumn(7);

        return listSummaries;
    }

    private MonthlySummary addSummayToSheet(Sheet sheet, LocalDate month, int rowStart, int rowEnd) {
        // Add summary to excel sheet
        Row row = sheet.createRow(rowEnd);
        excelUtil.setCellStringValueCenter(row, 0, "");
        excelUtil.setCellMonthValue(row, 1, month);
        excelUtil.setCellStringValueCenter(row, 2, "");
        excelUtil.setCellStringValueCenter(row, 3, "");
        excelUtil.setCellStringValue(row, 4, "");
        excelUtil.setCellFormula(row, 5, "sum(F" + rowStart + ":F" + rowEnd + ")");
        excelUtil.setCellFormula(row, 6, "sum(G" + rowStart + ":G" + rowEnd + ")");
        excelUtil.setCellFormula(row, 7, "H" + (rowStart - 2) + "-F" + (rowEnd + 1) + "+G" + (rowEnd + 1));
        excelUtil.setSummaryRowBorders(row, 8);

        // return MonthlySummary object that contains reference to the above summary
        MonthlySummary s = new MonthlySummary();
        s.setAccountKey(sheet.getSheetName());
        s.setMonth(month);
        s.setRefOut("'" + sheet.getSheetName() + "'!F" + (rowEnd + 1));
        s.setRefIn("'" + sheet.getSheetName() + "'!G" + (rowEnd + 1));
        s.setRefBalance("'" + sheet.getSheetName() + "'!H" + (rowEnd + 1));
        return s;
    }
}
