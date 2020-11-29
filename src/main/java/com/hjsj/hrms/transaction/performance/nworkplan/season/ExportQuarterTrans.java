package com.hjsj.hrms.transaction.performance.nworkplan.season;

import com.hjsj.hrms.businessobject.performance.nworkplan.season.NewWorkPlanBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.io.FileOutputStream;

public class ExportQuarterTrans extends IBusiness{

	public void execute() throws GeneralException {
		
		String outName = "";
		
		String year = (String)this.getFormHM().get("year");
		String season = (String)this.getFormHM().get("season");
		String message = (String)this.getFormHM().get("message");
		message = SafeCode.decode(message);
		
		try {
			outName = this.creatExcel(year, season, message);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			this.getFormHM().put("outName", SafeCode.decode(outName));
		}
	}
	private String creatExcel(String year , String season , String message) throws Exception{
		HSSFWorkbook wb = new HSSFWorkbook(); // 创建新的Excel 工作簿
		HSSFSheet sheet = wb.createSheet();
		// sheet.setProtect(true);

		HSSFFont font1 = wb.createFont(); //设置样式
		font1.setFontHeightInPoints((short) 10);
		font1.setBold(true);
		font1.setColor(HSSFFont.COLOR_NORMAL);
		HSSFCellStyle style2 = wb.createCellStyle();
		style2.setFont(font1);
		style2.setAlignment(HorizontalAlignment.CENTER);
		style2.setVerticalAlignment(VerticalAlignment.CENTER);
		style2.setWrapText(true);
		style2.setBorderLeft(BorderStyle.valueOf((short)1));   //设置左边框   
        style2.setBorderRight(BorderStyle.valueOf((short)1));   //设置有边框   
        style2.setBorderTop(BorderStyle.valueOf((short)1));   //设置下边框 
        style2.setBorderBottom(BorderStyle.valueOf((short)1));
		// style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		// style2.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

		HSSFFont font2 = wb.createFont();
		font2.setFontHeightInPoints((short) 10);
		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setFont(font2);
		style1.setAlignment(HorizontalAlignment.LEFT);
		style1.setVerticalAlignment(VerticalAlignment.TOP);
		style1.setWrapText(true);
		style1.setBorderLeft(BorderStyle.valueOf((short)1));   //设置左边框   
        style1.setBorderRight(BorderStyle.valueOf((short)1));   //设置有边框   
        style1.setBorderTop(BorderStyle.valueOf((short)1));   //设置下边框 
        style1.setBorderBottom(BorderStyle.valueOf((short)1));
		// style1.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式

		HSSFCellStyle styleN = dataStyle(wb);
		styleN.setAlignment(HorizontalAlignment.RIGHT);
		styleN.setWrapText(true);
		HSSFDataFormat df = wb.createDataFormat();
		styleN.setDataFormat(df.getFormat(decimalwidth(0)));

		HSSFCellStyle styleCol0 = dataStyle(wb);
		HSSFFont font0 = wb.createFont();
		font0.setFontHeightInPoints((short) 5);
		styleCol0.setFont(font0);
		// styleCol0.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));//
		// 文本格式
		// styleCol0.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		// styleCol0.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

		HSSFCellStyle styleCol0_title = dataStyle(wb);
		styleCol0_title.setFont(font2);
		// styleCol0_title.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));//
		// 文本格式
		// styleCol0_title.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		// styleCol0_title.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

		HSSFCellStyle styleF1 = dataStyle(wb);
		styleF1.setAlignment(HorizontalAlignment.RIGHT);
		HSSFFont font3 = wb.createFont(); //设置样式
		font3.setFontHeightInPoints((short) 3);
		styleF1.setFont(font3);
		styleF1.setWrapText(true);
		HSSFDataFormat df1 = wb.createDataFormat();
		styleF1.setDataFormat(df1.getFormat(decimalwidth(1)));

		HSSFCellStyle styleF2 = dataStyle(wb);
		styleF2.setAlignment(HorizontalAlignment.RIGHT);
		styleF2.setFont(font3);
		styleF2.setWrapText(true);
		HSSFDataFormat df2 = wb.createDataFormat();
		styleF2.setDataFormat(df2.getFormat(decimalwidth(2)));

		HSSFCellStyle styleF3 = dataStyle(wb);
		styleF3.setAlignment(HorizontalAlignment.RIGHT);
		styleF3.setFont(font3);
		styleF3.setWrapText(true);
		HSSFDataFormat df3 = wb.createDataFormat();
		styleF3.setDataFormat(df3.getFormat(decimalwidth(3)));

		HSSFCellStyle styleF4 = dataStyle(wb);
		styleF4.setAlignment(HorizontalAlignment.RIGHT);
		styleF4.setFont(font3);
		styleF4.setWrapText(true);
		HSSFDataFormat df4 = wb.createDataFormat();
		styleF4.setDataFormat(df4.getFormat(decimalwidth(4)));

		HSSFCellStyle styleF5 = dataStyle(wb);
		styleF5.setAlignment(HorizontalAlignment.RIGHT);
		styleF5.setFont(font3);
		styleF5.setWrapText(true);
		HSSFDataFormat df5 = wb.createDataFormat();
		styleF5.setDataFormat(df5.getFormat(decimalwidth(5)));
		NewWorkPlanBo bo = new NewWorkPlanBo(this.frameconn , this.userView);
		sheet.setColumnWidth(Short.parseShort(String.valueOf(0)),(short)40000);
		HSSFRow row = sheet.getRow(0);
		if (row == null) {
			row = sheet.createRow(0);
		}
		HSSFCell cell = null;
		String months = bo.getMonthsBySeason(season);
		StringBuffer sb = new StringBuffer();
		if(row.getRowNum() == 0){//第一行
			String type = (String)this.getFormHM().get("type");
			if("1".equals(type)){
				if(!"".equals(months.trim())){				
					String [] month = months.split(",");
					sb.append(year + "年 第" + season +"季季度总结  (" + month[0] + "月 - " + month[1] + "月)");
				}else{
					sb.append(year + "年 第" + season +"季季度总结");
				}
			}else if("2".equals(type)){
					sb.append(year + "年 总结");
			}
			cell = row.getCell(0);
			if (cell == null) {
				cell = row.createCell(0);
			}
			cell.setCellValue(cellStr(sb.toString()));
			cell.setCellStyle(style2);
			row = sheet.createRow(1); 
		}
		row.setHeight((short) 4000);
		cell = row.getCell(0);
		if(cell == null){
			cell = row.createCell(0);
		}
		cell.setCellValue(message);
		cell.setCellStyle(style1);
		
		String outName =  "season"+ PubFunc.getStrg() + ".xls";
		FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream(System
					.getProperty("java.io.tmpdir")
					+ System.getProperty("file.separator") + outName);
			wb.write(fileOut);
			fileOut.close();
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
