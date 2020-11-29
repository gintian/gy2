package com.hjsj.hrms.module.certificate.manage.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.io.FileOutputStream;

/**
 * 下载报错信息
 * 
 * @Title: ExcelMsgTrans.java
 * @Description: 用于下载报错信息的交易类
 * @Company: hjsj
 * @Create time: 2019年7月24日 下午19:36:28
 * @author chenxg
 * @version 1.0
 */
public class ExportExcelMsgTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		String outName = "";
		try {
			String msgJson = (String) this.getFormHM().get("msgJson");
			if (StringUtils.isEmpty(msgJson))
				return;
			
			outName = this.creatExcel(msgJson);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			this.getFormHM().put("fileName", SafeCode.encode(PubFunc.encrypt(outName)));
		}

	}

	/**
	 * 生成Excel文件
	 * 
	 * @param msgJson
	 *            错误信息
	 * @return
	 * @throws Exception
	 */
	private String creatExcel(String msgJson) throws Exception {
		String outName = this.userView.getUserName() + "_cf_msgOut.xls";
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		FileOutputStream fileOut = null;
		try {
			HSSFFont font2 = wb.createFont();
			font2.setFontHeightInPoints((short) 10);
			HSSFCellStyle style2 = wb.createCellStyle();
			style2.setFont(font2);
			style2.setAlignment(HorizontalAlignment.CENTER);
			style2.setVerticalAlignment(VerticalAlignment.CENTER);
			style2.setWrapText(true);
			style2.setBorderBottom(BorderStyle.THIN);
			style2.setBorderLeft(BorderStyle.THIN);
			style2.setBorderRight(BorderStyle.THIN);
			style2.setBorderTop(BorderStyle.THIN);
			style2.setBottomBorderColor((short) 8);
			style2.setLeftBorderColor((short) 8);
			style2.setRightBorderColor((short) 8);
			style2.setTopBorderColor((short) 8);
			style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			style2.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

			HSSFCellStyle style1 = wb.createCellStyle();
			style1.setFont(font2);
			style1.setAlignment(HorizontalAlignment.LEFT);
			style1.setVerticalAlignment(VerticalAlignment.CENTER);
			style1.setWrapText(true);
			style1.setBorderBottom(BorderStyle.THIN);
			style1.setBorderLeft(BorderStyle.THIN);
			style1.setBorderRight(BorderStyle.THIN);
			style1.setBorderTop(BorderStyle.THIN);
			style1.setBottomBorderColor((short) 8);
			style1.setLeftBorderColor((short) 8);
			style1.setRightBorderColor((short) 8);
			style1.setTopBorderColor((short) 8);
			// 文本格式
			style1.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));

			HSSFCellStyle styleCol0 = dataStyle(wb);
			HSSFFont font0 = wb.createFont();
			font0.setFontHeightInPoints((short) 5);
			styleCol0.setFont(font0);
			// 文本格式
			styleCol0.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));
			styleCol0.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			styleCol0.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

			HSSFRow row = sheet.getRow(0);
			if (row == null) {
				row = sheet.createRow(0);
			}
			HSSFCell cell = null;
			sheet.setColumnWidth((0), 7000);
			cell = row.getCell(0);
			if (cell == null)
				cell = row.createCell(0);
			cell.setCellValue(new HSSFRichTextString(ResourceFactory.getProperty("certificate.info.primaryField.desc")));
			cell.setCellStyle(style2);
			sheet.setColumnWidth((1), 15000);
			cell = row.getCell(1);
			if (cell == null)
				cell = row.createCell(1);
			
			cell.setCellValue(new HSSFRichTextString(ResourceFactory.getProperty("certificate.info.error.msg")));
			cell.setCellStyle(style2);

			int rowCount = 1;
			JSONArray jsArray = JSONArray.fromObject(msgJson);
			for (int i = 0; i < jsArray.size(); i++) {
				JSONObject jsObject = (JSONObject) jsArray.get(i);
				String keyid = (String) jsObject.get("primaryKey");
				keyid = keyid.replace("&nbsp;", "");
				String content = (String) jsObject.get("message");
				content = SafeCode.decode(content);
				content = content.replace("&nbsp;", "");
				content = content.replace("<br>", "\n");
				row = sheet.getRow(rowCount);
				if (row == null) {
					row = sheet.createRow(rowCount);
				}
				cell = row.getCell(0);
				if (cell == null)
					cell = row.createCell(0);
				
				cell.setCellStyle(style1);
				cell.setCellValue(SafeCode.decode(keyid));
				cell = row.getCell(1);
				if (cell == null)
					cell = row.createCell(1);
				
				cell.setCellStyle(style1);
				cell.setCellValue(content);
				rowCount++;
			}

			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outName);
			wb.write(fileOut);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(wb);
			sheet = null;
			wb = null;
		}
		return outName;
	}
	/**
	 * 创建excel样式
	 * @param workbook
	 * @return
	 */
	public HSSFCellStyle dataStyle(HSSFWorkbook workbook) {
		HSSFCellStyle style = workbook.createCellStyle();
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setBottomBorderColor((short) 8);
		style.setLeftBorderColor((short) 8);
		style.setRightBorderColor((short) 8);
		style.setTopBorderColor((short) 8);
		return style;
	}
}
