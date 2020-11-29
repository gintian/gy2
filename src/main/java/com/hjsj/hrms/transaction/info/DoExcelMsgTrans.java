package com.hjsj.hrms.transaction.info;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.io.FileOutputStream;
import java.util.ArrayList;
/**
 * 导出提示报告到excel
 * @author xujian
 *Apr 26, 2010
 */
public class DoExcelMsgTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String outName="";
		try{
			//ArrayList arr = (ArrayList)this.getFormHM().get("arr");
			ArrayList arr=(ArrayList)this.getFormHM().get("msglist");
			if(arr==null)
				return;
			outName=this.creatExcel(arr);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			this.getFormHM().put("outName", PubFunc.encrypt(outName.substring(0, outName.length()-1)+".xls"));
		}
		
	}
	
	private String creatExcel(ArrayList list) throws Exception{
		
		HSSFWorkbook wb = new HSSFWorkbook(); // 创建新的Excel 工作簿
		HSSFSheet sheet = wb.createSheet();
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
		style1.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式


		HSSFCellStyle styleCol0 = dataStyle(wb);
		HSSFFont font0 = wb.createFont();
		font0.setFontHeightInPoints((short) 5);
		styleCol0.setFont(font0);
		styleCol0.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式
		styleCol0.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleCol0.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
		
		HSSFRow row =sheet.getRow(0);
		if(row==null){
			row=sheet.createRow(0);
		}
		HSSFCell cell = null;
		sheet.setColumnWidth((0), 7000);
		cell=row.getCell(0);
		if(cell==null)
			cell=row.createCell(0);
		cell.setCellValue(cellStr(ResourceFactory.getProperty("workbench.info.relationitem.lebal")));
		cell.setCellStyle(style2);
		sheet.setColumnWidth((1), 15000);
		cell=row.getCell(1);
		if(cell==null)
			cell=row.createCell(1);
		cell.setCellValue(cellStr(ResourceFactory.getProperty("workbench.info.content.lebal")));
		cell.setCellStyle(style2);

		try
		{
			int rowCount = 1;
			for(int i=0;i<list.size();i++)
			{
				LazyDynaBean ldb = (LazyDynaBean)list.get(i);
				String keyid=(String)ldb.get("keyid");
				String content=(String)ldb.get("content");
				content = SafeCode.decode(content);
				String m[]=content.split("<input");
				if(m.length==1){
					content = content.replace("&nbsp;","");
					content = content.replace("</br>","\n");
				}else{
					content=m[0];
					content = content.replace("&nbsp;","");
					content = content+"\n";
				}
				row =sheet.getRow(rowCount);
				if(row==null){
					row=sheet.createRow(rowCount);
				}
				cell = row.getCell(0);
				if(cell==null)
					cell=row.createCell(0);
				cell.setCellStyle(style1);
				cell.setCellValue(SafeCode.decode(keyid));
				cell = row.getCell(1);
				if(cell==null)
					cell=row.createCell(1);
				cell.setCellStyle(style1);
				cell.setCellValue(content);
				rowCount++;
			}
		} catch (Exception e1)
		{
			e1.printStackTrace();
		}

		String outName = this.userView.getUserName()+"msgOut"+ PubFunc.getStrg() + ".xls";

		try
		{
			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outName);
			wb.write(fileOut);
			fileOut.close();
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		outName = outName.replace(".xls", "#");
		sheet = null;
		wb = null;
		return outName;
	}

	public HSSFCellStyle dataStyle(HSSFWorkbook workbook)
	{

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
	
	public HSSFRichTextString cellStr(String context)
	{

		HSSFRichTextString textstr = new HSSFRichTextString(context);
		return textstr;
	}

}
