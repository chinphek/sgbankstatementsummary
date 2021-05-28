package com.dreamtec.bsp.utils;

import java.time.LocalDate;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellUtil;

public class ExcelUtil {
    private Font font = null;
    private CellStyle styleMonth = null;
    private CellStyle styleDay = null;
    private CellStyle styleDate = null;
    private CellStyle styleAmount = null;
    private CellStyle styleString = null;
    private CellStyle styleStringCenter = null;

    public ExcelUtil(Workbook excel) {
        font = getFont2(excel);
        styleMonth = getStyleMonth(excel);
        styleDay = getStyleDay(excel);
        styleDate = getStyleDate(excel);
        styleAmount = getStyleAmount(excel);
        styleString = getStyleString(excel);
        styleStringCenter = getStyleStringCenter(excel);
    }

    private Font getFont2(Workbook excel) {
        Font f = excel.createFont();
        f.setBold(false);
        f.setColor(Font.COLOR_NORMAL);
        f.setFontHeight((short)(8*20));
        f.setFontName("Courier New");
        f.setItalic(false);
        f.setStrikeout(false);
        f.setTypeOffset(Font.SS_NONE);
        f.setUnderline(Font.U_NONE);
        return f;
    }

    private CellStyle getStyleMonth(Workbook excel) {
        CellStyle s = excel.createCellStyle();
        s.setFont(font);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setDataFormat(excel.createDataFormat().getFormat("MMM-yy"));
        return s;
    }

    private CellStyle getStyleDay(Workbook excel) {
        CellStyle s = excel.createCellStyle();
        s.setFont(font);
        s.setAlignment(HorizontalAlignment.CENTER);
        return s;
    }

    private CellStyle getStyleDate(Workbook excel) {
        CellStyle s = excel.createCellStyle();
        s.setFont(font);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setDataFormat(excel.createDataFormat().getFormat("dd-MMM-yyyy"));
        return s;
    }

    private CellStyle getStyleAmount(Workbook excel) {
        CellStyle s = excel.createCellStyle();
        s.setFont(font);
        s.setDataFormat(excel.createDataFormat().getFormat("0.00"));
        return s;
    }

    private CellStyle getStyleString(Workbook excel) {
        CellStyle s = excel.createCellStyle();
        s.setFont(font);
        return s;
    }

    private CellStyle getStyleStringCenter(Workbook excel) {
        CellStyle s = excel.createCellStyle();
        s.setFont(font);
        s.setAlignment(HorizontalAlignment.CENTER);
        return s;
    }

    public void setCellMonthValue(Row row, int col, LocalDate value) {
        //Set value
        Cell cell = row.createCell(col, CellType.STRING);
        cell.setCellValue(value.withDayOfMonth(1));
        
        //Set style
        cell.setCellStyle(styleMonth);
    }

    public void setCellDayValue(Row row, int col, int value) {
        //Set value
        Cell cell = row.createCell(col, CellType.NUMERIC);
        cell.setCellValue(value);

        //Set style
        cell.setCellStyle(styleDay);
    }

    public void setCellDateValue(Row row, int col, LocalDate value) {
        //Set value
        Cell cell = row.createCell(col, CellType.STRING);
        cell.setCellValue(value);

        //Set style
        cell.setCellStyle(styleDate);
    }

    public void setCellStringValue(Row row, int col, String value) {
        //Set value
        Cell cell = row.createCell(col, CellType.STRING);
        cell.setCellValue(value);

        //Set style
        cell.setCellStyle(styleString);
    }

    public void setCellStringValueCenter(Row row, int col, String value) {
        //Set value
        Cell cell = row.createCell(col, CellType.STRING);
        cell.setCellValue(value);

        //Set style
        cell.setCellStyle(styleStringCenter);
    }

    public void setCellAmountValue(Row row, int col, Double value) {
        //Set value
        Cell cell = row.createCell(col, CellType.NUMERIC);
        if(value == null) {
            cell.setCellValue("");
        } else {
            cell.setCellValue(value);
        }

        //Set style
        cell.setCellStyle(styleAmount);
    }

    public void setCellFormula(Row row, int col, String formula) {
        Cell cell = row.createCell(col, CellType.FORMULA);
        cell.setCellFormula(formula);

        //Set style
        cell.setCellStyle(styleAmount);
    }

    public void setSummaryRowBorders(Row row, int count) {
        for(int i = 0; i < count; i++) {
            Cell cell = row.getCell(i);
            CellUtil.setCellStyleProperty(cell, CellUtil.BORDER_TOP, BorderStyle.THIN);
            CellUtil.setCellStyleProperty(cell, CellUtil.BORDER_BOTTOM, BorderStyle.THIN);
            CellUtil.setFont(cell, font);
        }
    }

    public void setEmptyRowStyle(Row row, int count) {
        for(int i = 0; i < count; i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(styleString);
        }
    }
    
}
