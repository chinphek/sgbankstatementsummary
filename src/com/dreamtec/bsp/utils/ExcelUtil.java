package com.dreamtec.bsp.utils;

import java.time.LocalDate;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellUtil;

public class ExcelUtil {
    public static void setCellMonthValue(Row row, int col, LocalDate value) {
        //Set value
        Cell cell = row.createCell(col, CellType.STRING);
        cell.setCellValue(value.withDayOfMonth(1));
        
        //Set style
        Workbook excel = row.getSheet().getWorkbook();
        short dataFormat = excel.createDataFormat().getFormat("MMM-yy");
        CellUtil.setCellStyleProperty(cell, CellUtil.DATA_FORMAT, dataFormat);
    }

    public static void setCellDateValue(Row row, int col, LocalDate value) {
        //Set value
        Cell cell = row.createCell(col, CellType.STRING);
        cell.setCellValue(value);

        //Set style
        Workbook excel = row.getSheet().getWorkbook();
        short dataFormat = excel.createDataFormat().getFormat("dd-MMM-yyyy");
        CellUtil.setCellStyleProperty(cell, CellUtil.DATA_FORMAT, dataFormat);
    }

    public static void setCellStringValue(Row row, int col, String value) {
        Cell cell = row.createCell(col, CellType.STRING);
        cell.setCellValue(value);
    }

    public static void setCellNumericValue(Row row, int col, double value) {
        Cell cell = row.createCell(col, CellType.NUMERIC);
        cell.setCellValue(value);
    }

    public static void setCellFormula(Row row, int col, String formula) {
        Cell cell = row.createCell(col, CellType.FORMULA);
        cell.setCellFormula(formula);

        // Evaluate formula
        FormulaEvaluator e = row.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
        e.evaluate(cell);
    }

    public static void setSummaryRowBorders(Row row, int count) {
        for(int i = 0; i < count; i++) {
            Cell cell = row.getCell(i);
            CellUtil.setCellStyleProperty(cell, CellUtil.BORDER_TOP, BorderStyle.THIN);
            CellUtil.setCellStyleProperty(cell, CellUtil.BORDER_BOTTOM, BorderStyle.THIN);
        }
    }
    
}
