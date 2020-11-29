package com.hjsj.hrms.module.certificate.manage.businessobject;

import com.hjsj.hrms.utils.PubFunc;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class ExportCertificateTemplateBo {
	public ExportCertificateTemplateBo() {

	}

	public void creatSheet(String fileName, ArrayList<LazyDynaBean> fieldList, HashMap<String, ArrayList<String>> codeitemMap)
			throws Exception {
		HSSFWorkbook wb = new HSSFWorkbook();
		FileOutputStream fileOut = null;
		try {
			HashMap<Integer, ArrayList<String>> codeMap = new HashMap<Integer, ArrayList<String>>();
			HSSFSheet sheet = wb.createSheet();
			HSSFPatriarch patr = sheet.createDrawingPatriarch();
			HSSFRow row = sheet.getRow(0);
			if (row == null)
				row = sheet.createRow(0);
			
			HSSFCell cell = null;
			HSSFComment comm = null;
			HSSFDataFormat df = wb.createDataFormat();
			HSSFCellStyle headStyle = wb.createCellStyle();
			headStyle.setAlignment(HorizontalAlignment.CENTER);
			headStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			headStyle.setWrapText(true);
			headStyle.setBorderBottom(BorderStyle.THIN);
			headStyle.setBorderLeft(BorderStyle.THIN);
			headStyle.setBorderRight(BorderStyle.THIN);
			headStyle.setBorderTop(BorderStyle.THIN);
			headStyle.setBottomBorderColor((short) 8);
			headStyle.setLeftBorderColor((short) 8);
			headStyle.setRightBorderColor((short) 8);
			headStyle.setTopBorderColor((short) 8);
			headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			headStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
			ArrayList<String> codeCols = new ArrayList<String>();
			for (int i = 0; i < fieldList.size(); i++) {
				LazyDynaBean field = fieldList.get(i);
				String itemid = (String) field.get("itemid");
				String itemDesc = (String) field.get("content");
				String codesetId = (String) field.get("codesetid");
				String itemType = (String) field.get("colType");
				int colLength = Integer.valueOf((String) field.get("colLength"));
				int decwidth = Integer.valueOf((String) field.get("decwidth"));
				int w = Integer.valueOf((String) field.get("displayWidth"));
				if (w == 0)
					w = 8;
				
				if (w > 186)
					w = 186;
				sheet.setColumnWidth((i), w * 50);
				cell = row.getCell(i);
				if (cell == null)
					cell = row.createCell(i);
				
				cell.setCellValue(itemDesc);
				cell.setCellStyle(headStyle);
				comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) (i + 1), 0, (short) (i + 2), 1));
				comm.setString(new HSSFRichTextString(itemid));
				cell.setCellComment(comm);
				if ("A".equalsIgnoreCase(itemType) && !"0".equals(codesetId))
					codeCols.add(itemDesc + ":" + new Integer(i).toString());
				
				HSSFCellStyle style = wb.createCellStyle();
				style.setVerticalAlignment(VerticalAlignment.CENTER);
				style.setWrapText(true);
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);
				style.setBottomBorderColor((short) 8);
				style.setLeftBorderColor((short) 8);
				style.setRightBorderColor((short) 8);
				style.setTopBorderColor((short) 8);
				String format = "@";
				if("N".equalsIgnoreCase(itemType)) {
					format = "#,##0";
					for(int m = 0; m < decwidth; m++) {
						if(m == 0)
							format += ".";
						
						format += "0";
					}
					style.setDataFormat(HSSFDataFormat.getBuiltinFormat(format));//保留两位小数点
					style.setAlignment(HorizontalAlignment.RIGHT);
				} else if("D".equalsIgnoreCase(itemType)) {
					format = "yyyy-MM-dd";
					if(colLength == 4)
						format = "yyyy";
					else if(colLength == 7)
						format = "yyyy-MM";
					else if(colLength == 16)
						format = "yyyy-MM-dd hh:mm";
					else if(colLength >= 18)
						format = "yyyy-MM-dd hh:mm:ss";
						
					style.setDataFormat(df.getFormat(format));
					style.setAlignment(HorizontalAlignment.LEFT);
				} else {
					style.setDataFormat(HSSFDataFormat.getBuiltinFormat(format));
					style.setAlignment(HorizontalAlignment.LEFT);
				}
				
				sheet.setDefaultColumnStyle(i, style);
				
				if ("A".equalsIgnoreCase(itemType) && !"0".equals(codesetId))
					codeMap.put(i, codeitemMap.get(itemid));
			}
			
			int index = 110;
			//遍历map中的键 
			for (int key : codeMap.keySet()) { 
				ArrayList<String> codeitemList = codeMap.get(key);
				// 48382 筛选出空的下拉集合，防止导出excel报错
				if(null==codeitemList || codeitemList.size()<1) 
					continue;
				
				if (codeitemList != null && codeitemList.size() > 500)
                    continue;
				
				int m = 20001;
				for(String itemDesc : codeitemList) {
					row = sheet.getRow(m);
					if (row == null)
						row = sheet.createRow(m);
					
					cell = row.createCell(index);
					cell.setCellValue(new HSSFRichTextString(itemDesc));
					
					m++;
				}
				// 放到单独页签
				String strFormula = "$" + indexToColumn(index) + "$20001:$"
						+ indexToColumn(index) + "$" + Integer.toString(m);
				CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, key, key);
				DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(strFormula);
				HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
				dataValidation.setSuppressDropDownArrow(false);
				sheet.addValidationData(dataValidation);
				index++;
			} 

				String url = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + fileName;
				fileOut = new FileOutputStream(url);
				wb.write(fileOut);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeIoResource(fileOut);
			PubFunc.closeIoResource(wb);
			wb = null;
		}
	}

	/**
	 * 通过列标识（A、B... AA等）计算列index（number）
	 * 
	 * @author guodd 2015-04-14
	 * @param column，范围为A-IV，再大就超出excel范围了
	 * @return int
	 */
	private String indexToColumn(int index) {
	    String column = "";
        String[] columnUpper = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
                "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
        
        if(index < 26)
            column = columnUpper[index];
        else
            column = columnUpper[(index - 26) / 26] + columnUpper[index % 26];
		
		return column;
	}
}
