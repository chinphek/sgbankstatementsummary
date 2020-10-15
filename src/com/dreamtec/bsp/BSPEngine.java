package com.dreamtec.bsp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        Collections.sort(statements);
        for (IBankStatement s : statements) {
            Sheet sheet = excel.createSheet(s.getAccountShortName());
            Row header = sheet.createRow(0);
            setCellStringValue(header, 0, "Month");
            setCellStringValue(header, 1, "Day");
            setCellStringValue(header, 2, "Date");
            setCellStringValue(header, 3, "Description");
            setCellStringValue(header, 4, "In");
            setCellStringValue(header, 5, "Out");
            setCellStringValue(header, 6, "Balance");
            
            List<Transaction> transactions = s.process();
            Collections.sort(transactions);
            
            for(int i = 0; i < transactions.size(); i++) {
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

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);
            sheet.autoSizeColumn(5);
            sheet.autoSizeColumn(6);
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
