package com.dreamtec.bsp.utils;

import java.time.LocalDate;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellUtil;

public class ExcelUtil {
    /**
     * Get font object from excel Workbook.
     * Create font object if necessary.
     * This is necessary to prevent excel from repeatly creating and storing
     * the same font, which increases the filesize.
     * 
     * @param excel
     * @return
     */
    public static Font getFont(Workbook excel) {
        boolean bold = false;
        short color = Font.COLOR_NORMAL;
        short fontHeight = 8*20;
        String name = "Courier New";
        boolean italic = false;
        boolean strikeout = false;
        short typeOffset = Font.SS_NONE;
        byte underline = Font.U_NONE;
        Font font = excel.findFont(bold, color, fontHeight, name, italic, strikeout, typeOffset, underline);
        if(font == null) {
            font = excel.createFont();
            font.setBold(bold);
            font.setColor(color);
            font.setFontHeight(fontHeight);
            font.setFontName(name);
            font.setItalic(italic);
            font.setStrikeout(strikeout);
            font.setTypeOffset(typeOffset);
            font.setUnderline(underline);
        }

        return font;
    }

    public static void setCellMonthValue(Row row, int col, LocalDate value) {
        //Set value
        Cell cell = row.createCell(col, CellType.STRING);
        cell.setCellValue(value.withDayOfMonth(1));
        
        //Set style
        Workbook excel = row.getSheet().getWorkbook();
        short dataFormat = excel.createDataFormat().getFormat("MMM-yy");
        CellUtil.setCellStyleProperty(cell, CellUtil.DATA_FORMAT, dataFormat);
        CellUtil.setFont(cell, getFont(excel));
    }

    public static void setCellDateValue(Row row, int col, LocalDate value) {
        //Set value
        Cell cell = row.createCell(col, CellType.STRING);
        cell.setCellValue(value);

        //Set style
        Workbook excel = row.getSheet().getWorkbook();
        short dataFormat = excel.createDataFormat().getFormat("dd-MMM-yyyy");
        CellUtil.setCellStyleProperty(cell, CellUtil.DATA_FORMAT, dataFormat);
        CellUtil.setFont(cell, getFont(excel));
    }

    public static void setCellStringValue(Row row, int col, String value) {
        Cell cell = row.createCell(col, CellType.STRING);
        cell.setCellValue(value);

        Workbook excel = row.getSheet().getWorkbook();
        CellUtil.setFont(cell, getFont(excel));
    }

    public static void setCellNumericValue(Row row, int col, double value) {
        Cell cell = row.createCell(col, CellType.NUMERIC);
        cell.setCellValue(value);

        Workbook excel = row.getSheet().getWorkbook();
        CellUtil.setFont(cell, getFont(excel));
    }

    public static void setCellFormula(Row row, int col, String formula) {
        Cell cell = row.createCell(col, CellType.FORMULA);
        cell.setCellFormula(formula);
        Workbook excel = row.getSheet().getWorkbook();
        CellUtil.setFont(cell, getFont(excel));

        // Evaluate formula
        FormulaEvaluator e = row.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
        e.evaluate(cell);
    }

    public static void setSummaryRowBorders(Row row, int count) {
        Workbook excel = row.getSheet().getWorkbook();
            
        for(int i = 0; i < count; i++) {
            Cell cell = row.getCell(i);
            CellUtil.setCellStyleProperty(cell, CellUtil.BORDER_TOP, BorderStyle.THIN);
            CellUtil.setCellStyleProperty(cell, CellUtil.BORDER_BOTTOM, BorderStyle.THIN);

            CellUtil.setFont(cell, getFont(excel));
        }
    }

    public static void setEmptyRowStyle(Row row, int count) {
        Workbook excel = row.getSheet().getWorkbook();
        for(int i = 0; i < count; i++) {
            Cell cell = row.createCell(i);
            CellUtil.setFont(cell, getFont(excel));
        }
    }
    
}
