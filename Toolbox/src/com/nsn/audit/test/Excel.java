package com.nsn.audit.test;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.nsn.audit.dataset.NE;
import com.nsn.audit.dataset.Param;

public class Excel {

	public static void printAllExcel(String outputFile,HashMap<String, ArrayList<NE>> ringsNEList){

		try {
			FileOutputStream out = new FileOutputStream(new File(outputFile));
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Configuration");
			HSSFFont font = workbook.createFont();
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			HSSFCellStyle correctConfigStyle = workbook.createCellStyle();
			correctConfigStyle.setFont(font);
			correctConfigStyle.setFillForegroundColor(new HSSFColor.GREEN().getIndex());
			correctConfigStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
			HSSFCellStyle wrongConfigStyle = workbook.createCellStyle();
			wrongConfigStyle.setFont(font);
			wrongConfigStyle.setFillForegroundColor(new HSSFColor.RED().getIndex());
			wrongConfigStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);

			int rownum = 0;
			Row row = sheet.createRow(rownum++);
			Cell cell = row.createCell(0);
			cell.setCellValue("Ring Name");
			cell = row.createCell(1);
			cell.setCellValue("Location");
			cell = row.createCell(2);
			cell.setCellValue("Name");
			cell = row.createCell(3);
			cell.setCellValue("IP");
			cell = row.createCell(4);
			cell.setCellValue("Version");
			cell = row.createCell(5);
			cell.setCellValue("Type");
			for (Map.Entry<String, ArrayList<NE>> ring : ringsNEList.entrySet()) {
				String ringName = ring.getKey();
				ArrayList<NE> neList = ring.getValue();
				Iterator<NE> itr = neList.iterator();
				while (itr.hasNext()) {
					NE ne = itr.next();
					row = sheet.createRow(rownum++);
					cell = row.createCell(0);
					cell.setCellValue(ringName);
					cell = row.createCell(1);
					cell.setCellValue(ne.getLocation());
					cell = row.createCell(2);
					cell.setCellValue(ne.getName());
					cell = row.createCell(3);
					cell.setCellValue(ne.getIP());
					cell = row.createCell(4);
					cell.setCellValue(ne.getSvr());
					cell = row.createCell(5);
					cell.setCellValue(ne.getType());
					Iterator<Entry<String, Param>> it = ne.getParams().entrySet().iterator();
					int i = 5;
					while (it.hasNext()) {
						Map.Entry<String, Param> pairs = it.next();
						String paramName = pairs.getKey();
						Param param = pairs.getValue();
						cell = row.createCell(i++);
						if (param.isCompliant()) {
							cell.setCellStyle(correctConfigStyle);
							cell.setCellValue(paramName);
						}
						else {
							cell.setCellStyle(wrongConfigStyle);
							cell.setCellValue(paramName+" :"+param.toString());

						}
					}
				}
			}
			workbook.write(out);
			out.close();
			System.out.println("Excel written successfully..");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
} 

