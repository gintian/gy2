package com.hjsj.hrms.businessobject.hire.jp_contest.personinfo;

import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.ArrayList;
/**
 * 
 *<p>Title:ShowExcel.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 22, 2007</p> 
 *@author huaitao
 *@version 4.0
 */
public class ShowExcel {
	private Connection conn;
	public ShowExcel(){}
	public ShowExcel(Connection conn)
	{
		this.conn=conn;
	}
	public String creatExcel(ArrayList column,ArrayList infolist,ArrayList columnlist)
	{
		String excel_filename="ry_tj_123456.xls";
		HSSFWorkbook workbook = new HSSFWorkbook();
		FileOutputStream fileOut = null;
		try
		{
			HSSFSheet sheet = workbook.createSheet();
			HSSFRow row=null;
			HSSFCell csCell=null;
			short n=0;
			HSSFFont font = workbook.createFont();			
			font.setColor(HSSFFont.COLOR_NORMAL);
			font.setBold(true);
			font.setFontHeightInPoints((short)11);
			HSSFCellStyle cellStyle= workbook.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setAlignment(HorizontalAlignment.CENTER);//水平居中
			
			ExportExcelUtil.mergeCell(sheet, 0,(short)0,0,Short.parseShort(String.valueOf(column.size()+1)));//指定合并区域
			//	row=sheet.createRow(n);
			row = sheet.getRow(n);
			if(row==null) {
                row=sheet.createRow(n);
            }
			
			csCell=row.createCell(Short.parseShort("0"));
			csCell.setCellStyle(cellStyle);
			HSSFRichTextString ss = new HSSFRichTextString("参选者名单");
			csCell.setCellValue(ss);
			n++;n++;
			
//		row=sheet.createRow(n);
			row = sheet.getRow(n);
			if(row==null) {
                row=sheet.createRow(n);
            }
			csCell =row.createCell((short)0);
			ss = new HSSFRichTextString("编号：");
			csCell.setCellValue(ss);
			csCell=row.createCell(Short.parseShort(String.valueOf((column.size()+1)/2)));
			ss = new HSSFRichTextString("管理处名称：");
			csCell.setCellValue(ss);
			csCell=row.createCell(Short.parseShort(String.valueOf(column.size()+1)));
			
			ss = new HSSFRichTextString("年    月    日");
			csCell.setCellValue(ss);
			n++;
//		row=sheet.createRow(n);
			row = sheet.getRow(n);
			if(row==null) {
                row=sheet.createRow(n);
            }
			csCell =row.createCell((short)0);
			font = workbook.createFont();
			font.setColor(HSSFFont.COLOR_NORMAL);
			font.setBold(true);
			cellStyle= workbook.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			csCell.setCellStyle(cellStyle);
			ss = new HSSFRichTextString("序号");
			csCell.setCellValue(ss);
			LazyDynaBean bean = new LazyDynaBean();
			
			font = workbook.createFont();
			font.setColor(HSSFFont.COLOR_NORMAL);
			font.setBold(true);
			cellStyle= workbook.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			for(int i=0;i<column.size();i++){
				String name = (String)column.get(i);
				csCell = row.createCell((short)(i+1));
				csCell.setCellStyle(cellStyle);
				ss = new HSSFRichTextString(name);
				csCell.setCellValue(ss);
			}
			csCell = row.createCell((short)(column.size()+1));
			csCell.setCellStyle(cellStyle);
			ss = new HSSFRichTextString("备注");
			csCell.setCellValue(ss);
			cellStyle= workbook.createCellStyle();
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			
			for(int i=0;i<infolist.size();i++){
//			row=sheet.createRow(n+i+1);
				row = sheet.getRow(n+i+1);
				if(row==null) {
                    row=sheet.createRow(n+i+1);
                }
				csCell =row.createCell((short)(0));
				csCell.setCellStyle(cellStyle);
				ss = new HSSFRichTextString(String.valueOf(i+1));
				csCell.setCellValue(ss);
				bean = (LazyDynaBean)infolist.get(i);
				for(int j=0;j<columnlist.size();j++){
					csCell =row.createCell((short)(j+1));
					csCell.setCellStyle(cellStyle);
					String name = (String)bean.get((String)columnlist.get(j));
					ss = new HSSFRichTextString(name);
					csCell.setCellValue(ss);
				}
				csCell =row.createCell((short)(columnlist.size()+1));
				csCell.setCellStyle(cellStyle);
				String name = "";
				ss = new HSSFRichTextString(name);
				csCell.setCellValue(ss);
			}
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+excel_filename);
			workbook.write(fileOut);
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(workbook);
		}
		
		
		return excel_filename;
	}
}
