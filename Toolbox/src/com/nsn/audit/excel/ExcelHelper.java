package com.nsn.audit.excel;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * Helper for Excel objects
 *
 *
 * @author Philip Wu
 */
public class ExcelHelper {

    /**
     * A Helper method to create an excel worksheet
     * @param workbook
     * @param sheetName
     * @param columns
     * @return
     */
    public static HSSFSheet createSheet(HSSFWorkbook workbook, String sheetName, String[] columns) {
        
        HSSFSheet sheet = workbook.createSheet(sheetName);
        
        HSSFRow row = sheet.createRow(0);
        
        createHeaders(workbook, row, columns);
        
        return sheet;
    }
    
    /**
     * Creates headers for the row using the following columns
     * @param workbook
     * @param row
     * @param columns
     */
    public static void createHeaders(HSSFWorkbook workbook, HSSFRow row, Object[] columns) {
        
        HSSFFont bold = workbook.createFont();
        bold.setBoldweight(org.apache.poi.hssf.usermodel.HSSFFont.BOLDWEIGHT_BOLD);
        
        for (int i=0 ; i < columns.length; i++) {
            Object fieldHeader = columns[i];
            HSSFRichTextString headerFieldValue = new HSSFRichTextString(fieldHeader.toString());
            headerFieldValue.applyFont(bold);
            
            row.createCell(i).setCellValue(headerFieldValue);
        }
    }
    
    public static void addColumn(HSSFRow row, int index, String value) {
        
        addColumn(row, index, value, null);
    }    
    
    /**
     * Helper method to add a column
     * @param row
     * @param index
     * @param value
     * @param defaultValue    The default value to use if value is null
     */
    public static void addColumnDefault(HSSFRow row, int index, String value, String defaultValue) {
        String setValue = defaultValue;
        if (value != null)
            setValue = value;
        addColumn(row, index, setValue);
    }
    
    /**
     * Adds a column to the row at the specified index with the given value applying
     * a font to the text if it exists.
     * @param row
     * @param index
     * @param value
     * @param font
     */
    public static void addColumn(HSSFRow row, int index, String value, HSSFFont font) {
        
        if (value == null)
            return;
        
        // Without replacing \r characters, excel produces square boxes
        value = value.replaceAll("\r", "");
        value = value.replaceAll("\n", "");
        
        HSSFRichTextString excelValue = new HSSFRichTextString(value);    
        if (font != null)
            excelValue.applyFont(font);
        
        row.createCell(index).setCellValue(excelValue);
    }        
    
}
