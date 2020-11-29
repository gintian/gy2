package com.hjsj.hrms.transaction.askinv;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.io.FileOutputStream;
import java.sql.Connection;

public class ExportTemplate extends IBusiness {
	private Connection conn = null;

	public ExportTemplate() {
		try {
			this.conn = AdminDb.getConnection();
		} catch (GeneralException e) {
			e.printStackTrace();
		}
	}

	public void execute() throws GeneralException {
		String outName = "";
		try {
			outName = this.creatExcel();
			outName = PubFunc.encrypt(outName.replace("#", ".xls"));//此处先加密后再转码
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			this.getFormHM().put("outName", SafeCode.decode(outName));
		}
	}

	private String creatExcel() throws Exception {
		HSSFWorkbook wb = new HSSFWorkbook(); // 创建新的Excel 工作簿
		HSSFSheet sheet = wb.createSheet();
		// sheet.setProtect(true);

		HSSFFont font1 = wb.createFont(); //设置样式
		font1.setFontHeightInPoints((short) 20);
		font1.setBold(true);
		font1.setColor(HSSFFont.COLOR_NORMAL);
		HSSFCellStyle style2 = wb.createCellStyle();
		style2.setFont(font1);
		style2.setAlignment(HorizontalAlignment.CENTER);
		style2.setVerticalAlignment(VerticalAlignment.CENTER);
		style2.setWrapText(true);
		style2.setBorderLeft(BorderStyle.valueOf((short)1)); //设置左边框   
		style2.setBorderRight(BorderStyle.valueOf((short)1)); //设置有边框   
		style2.setBorderTop(BorderStyle.valueOf((short)1)); //设置下边框 
		style2.setBorderBottom(BorderStyle.valueOf((short)1));
		// style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		// style2.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

		HSSFFont font2 = wb.createFont();
		font2.setFontHeightInPoints((short) 10);
		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setFont(font2);
		style1.setAlignment(HorizontalAlignment.CENTER);
		style1.setVerticalAlignment(VerticalAlignment.CENTER);
		style1.setWrapText(true);
		style1.setBorderLeft(BorderStyle.valueOf((short)1)); //设置左边框   
		style1.setBorderRight(BorderStyle.valueOf((short)1)); //设置有边框   
		style1.setBorderTop(BorderStyle.valueOf((short)1)); //设置下边框 
		style1.setBorderBottom(BorderStyle.valueOf((short)1));
		//style1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		//style1.setFillBackgroundColor(HSSFColor.LIGHT_GREEN.index);
		// style1.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式

		

		HSSFPatriarch patr = sheet.createDrawingPatriarch();

		HSSFRow row = sheet.getRow(0);
		if (row == null) {
			row = sheet.createRow(0);
		}
		//row.setHeight((short) 1000);
		HSSFCell cell = null;

		sheet.setColumnWidth((0), 15 * 500); //设置列宽
		sheet.setColumnWidth((1), 15 * 500); //设置列宽
		// 设置第一行的数据
		cell = row.getCell(0);
		if(cell == null){
			cell = row.createCell(0);
		}
		cell.setCellValue("问卷名称");
		
		cell = row.getCell(1);
		if(cell == null){
			cell = row.createCell(1);
		}
		cell.setCellValue("请在这里填写问卷名称");

		row = sheet.getRow(1);
		if(row == null){
			row = sheet.createRow(1);
		}
		cell = row.getCell(0);
		if(cell == null){
			cell = row.createCell(0);
		}
		cell.setCellValue("填表说明");
		
		cell = row.getCell(1);
		if(cell == null){
			cell = row.createCell(1);
		}
		cell.setCellValue("请在这里填写填表说明");
		
		row = sheet.getRow(2);
		if(row == null){
			row = sheet.createRow(2);
		}
		cell = row.getCell(0);
		if(cell == null){
			cell = row.createCell(0);
		}
		cell.setCellValue("单选");
		
		cell = row.getCell(1);
		if(cell == null){
			cell = row.createCell(1);
		}
		cell.setCellValue("请在这里填写单选题目名称");
		
		row = sheet.getRow(3);
		if(row == null){
			row = sheet.createRow(3);
		}
		cell = row.getCell(0);
		if(cell == null){
			cell = row.createCell(0);
		}
		cell.setCellValue("");
		
		cell = row.getCell(1);
		if(cell == null){
			cell = row.createCell(1);
		}
		cell.setCellValue("请在这里填写单选选项");
		
		row = sheet.getRow(4);
		if(row == null){
			row = sheet.createRow(4);
		}
		cell = row.getCell(0);
		if(cell == null){
			cell = row.createCell(0);
		}
		cell.setCellValue("");
		
		cell = row.getCell(1);
		if(cell == null){
			cell = row.createCell(1);
		}
		cell.setCellValue("请在这里填写单选选项");
		
		
		row = sheet.getRow(5);
		if(row == null){
			row = sheet.createRow(5);
		}
		cell = row.getCell(0);
		if(cell == null){
			cell = row.createCell(0);
		}
		cell.setCellValue("");
		
		cell = row.getCell(1);
		if(cell == null){
			cell = row.createCell(1);
		}
		cell.setCellValue("请在这里填写单选选项");
		
		row = sheet.getRow(6);
		if(row == null){
			row = sheet.createRow(6);
		}
		cell = row.getCell(0);
		if(cell == null){
			cell = row.createCell(0);
		}
		cell.setCellValue("");
		
		cell = row.getCell(1);
		if(cell == null){
			cell = row.createCell(1);
		}
		cell.setCellValue("请在这里填写单选选项");
		
		row = sheet.getRow(7);
		if(row == null){
			row = sheet.createRow(7);
		}
		cell = row.getCell(0);
		if(cell == null){
			cell = row.createCell(0);
		}
		cell.setCellValue("多选");
		
		cell = row.getCell(1);
		if(cell == null){
			cell = row.createCell(1);
		}
		cell.setCellValue("请在这里填写多选题目名称");
		
		row = sheet.getRow(8);
		if(row == null){
			row = sheet.createRow(8);
		}
		cell = row.getCell(0);
		if(cell == null){
			cell = row.createCell(0);
		}
		cell.setCellValue("");
		
		cell = row.getCell(1);
		if(cell == null){
			cell = row.createCell(1);
		}
		cell.setCellValue("请在这里填写多选选项");
		
		row = sheet.getRow(9);
		if(row == null){
			row = sheet.createRow(9);
		}
		cell = row.getCell(0);
		if(cell == null){
			cell = row.createCell(0);
		}
		cell.setCellValue("");
		
		cell = row.getCell(1);
		if(cell == null){
			cell = row.createCell(1);
		}
		cell.setCellValue("请在这里填写多选选项");
		
		row = sheet.getRow(10);
		if(row == null){
			row = sheet.createRow(10);
		}
		cell = row.getCell(0);
		if(cell == null){
			cell = row.createCell(0);
		}
		cell.setCellValue("");
		
		cell = row.getCell(1);
		if(cell == null){
			cell = row.createCell(1);
		}
		cell.setCellValue("请在这里填写多选选项");
		
		row = sheet.getRow(11);
		if(row == null){
			row = sheet.createRow(11);
		}
		cell = row.getCell(0);
		if(cell == null){
			cell = row.createCell(0);
		}
		cell.setCellValue("");
		
		cell = row.getCell(1);
		if(cell == null){
			cell = row.createCell(1);
		}
		cell.setCellValue("请在这里填写多选选项");
		
		row = sheet.getRow(12);
		if(row == null){
			row = sheet.createRow(12);
		}
		cell = row.getCell(0);
		if(cell == null){
			cell = row.createCell(0);
		}
		cell.setCellValue("简答");
		
		cell = row.getCell(1);
		if(cell == null){
			cell = row.createCell(1);
		}
		cell.setCellValue("请在这里填写简答题目名称");
		try {
		
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (conn != null) {
				conn.close();
			}
		}

		String outName = this.userView.getUserName() + "_topic_templete" + PubFunc.getStrg() + ".xls";
		FileOutputStream fileOut=null;
		try {
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outName);
			wb.write(fileOut);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeResource(fileOut);
		}
		outName = outName.replace(".xls", "#");
		sheet = null;
		wb = null;
		return outName;
	}

	public HSSFRichTextString cellStr(String context) {
		HSSFRichTextString textstr = new HSSFRichTextString(context);
		return textstr;
	}

	public String decimalwidth(int len) {

		StringBuffer decimal = new StringBuffer("0");
		if (len > 0)
			decimal.append(".");
		for (int i = 0; i < len; i++) {
			decimal.append("0");
		}
		decimal.append("_ ");
		return decimal.toString();
	}

	public HSSFCellStyle dataStyle(HSSFWorkbook workbook) {
		HSSFCellStyle style = workbook.createCellStyle();
		// style.setVerticalAlignment(VerticalAlignment.CENTER);
		return style;
	}
}
