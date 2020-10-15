package com.dreamtec.bsp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.dreamtec.bsp.statement.BankStatementFactory;
import com.dreamtec.bsp.statement.IBankStatement;
import com.dreamtec.bsp.statement.Transaction;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
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
    private final CellStyle dateStyle = excel.createCellStyle();
    private final CellStyle monthStyle = excel.createCellStyle();
    private final List<IBankStatement> statements = new ArrayList<IBankStatement>();

    public BSPEngine() {
        CreationHelper createHelper = excel.getCreationHelper();
        dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MMM-yyyy"));
        monthStyle.setDataFormat(createHelper.createDataFormat().getFormat("MMM-yy"));
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

    public void process() {
        // Sort the statements
        Collections.sort(statements);

        // Handle same accounts with multiple statements
        Map<String, List<IBankStatement>> mapAccounts = new HashMap<String, List<IBankStatement>>();
        for (IBankStatement s : statements) {
            String key = s.getAccountShortName() + " " + s.getAccountNumber();
            if (mapAccounts.containsKey(key)) {
                mapAccounts.get(key).add(s);
            } else {
                List<IBankStatement> list = new ArrayList<IBankStatement>();
                list.add(s);
                mapAccounts.put(key, list);
            }
        }

        for (Entry<String, List<IBankStatement>> e : mapAccounts.entrySet()) {
            System.out.println("        Processing " + e.getKey());
            List<Transaction> transactions = null;

            List<IBankStatement> list = e.getValue();
            transactions = list.get(0).process();

            for(int i = 1; i < list.size(); i++) {
                List<Transaction> transactions2 = list.get(i).process();
                System.out.println("            Combining transactions");
                transactions = combineTransactions(transactions, transactions2);
            }

            addTransactionsToSheet(e.getKey(), transactions);
        }
    }

    public void save(String filename) throws FileNotFoundException {
        FileOutputStream outputStream = new FileOutputStream(filename);
        try {
            excel.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
                System.out.println("                WARN: Transactions for same date '" + date.toString() + "' found in multple statements. Please avoid overlapping statements.");
                
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
                        System.out.println("                ERROR: Inconsistent overlapping transactions on '" + date.toString() + "'. Please check manually.");
                    }
                    System.out.println("                WARN: Discarding '" + daylist2.size() + "' overlapping transactions on '" + date.toString() + "'. Please check manually.");
                    t.addAll(daylist1);
                } else if(daylist1.size() > daylist2.size()) {
                    System.out.println("                ERROR: Inconsistent overlapping transactions on '" + date.toString() + "'. Please check manually.");
                    System.out.println("                WARN: Discarding '" + daylist2.size() + "' overlapping transactions on '" + date.toString() + "'. Please check manually.");
                    t.addAll(daylist1);
                } else {
                    System.out.println("                ERROR: Inconsistent overlapping transactions on '" + date.toString() + "'. Please check manually.");
                    System.out.println("                WARN: Discarding '" + daylist1.size() + "' overlapping transactions on '" + date.toString() + "'. Please check manually.");
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

    private void addTransactionsToSheet(String sheetName, List<Transaction> transactions) {
        System.out.println("            Adding '" + transactions.size() + "' transaction to sheet '" + sheetName + "'.");
        Sheet sheet = excel.createSheet(sheetName);
        Row header = sheet.createRow(0);
        setCellStringValue(header, 0, "Month");
        setCellStringValue(header, 1, "Day");
        setCellStringValue(header, 2, "Date");
        setCellStringValue(header, 3, "Description");
        setCellStringValue(header, 4, "In");
        setCellStringValue(header, 5, "Out");
        setCellStringValue(header, 6, "Balance");

        if (transactions != null) {
            Collections.sort(transactions);

            for (int i = 0; i < transactions.size(); i++) {
                Transaction t = transactions.get(i);
                Row row = sheet.createRow(i + 1);

                setCellMonthValue(row, 0, t.getDate());
                setCellNumericValue(row, 1, t.getDate().getDayOfMonth());
                setCellDateValue(row, 2, t.getDate());
                setCellStringValue(row, 3, t.getDescription());
                setCellNumericValue(row, 4, t.getIn());
                setCellNumericValue(row, 5, t.getOut());
                setCellNumericValue(row, 6, t.getBalance());
            }
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);
    }

    private void setCellMonthValue(Row row, int col, LocalDate value) {
        Cell cell = row.createCell(col, CellType.STRING);
        cell.setCellValue(value.withDayOfMonth(1));
        cell.setCellStyle(monthStyle);
    }

    private void setCellDateValue(Row row, int col, LocalDate value) {
        Cell cell = row.createCell(col, CellType.STRING);
        cell.setCellValue(value);
        cell.setCellStyle(dateStyle);
    }

    private void setCellStringValue(Row row, int col, String value) {
        Cell cell = row.createCell(col, CellType.STRING);
        cell.setCellValue(value);
    }

    private void setCellNumericValue(Row row, int col, double value) {
        Cell cell = row.createCell(col, CellType.NUMERIC);
        cell.setCellValue(value);
    }

}
