package com.hjsj.hrms.businessobject.general.statics;

import com.hjsj.hrms.utils.PubFunc;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.List;

/**
 *<p>Title:ShowExcel.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 1, 2007</p> 
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
	public String creatExcel(int[][] getValues,List dlist,List hlist,String snameplay,int tolvalue,String excel_filename)
	{
		//liuy 2014-12-13 5940：哈药领导桌面：人力资源情况/用工形式岗位分布，二维常用统计，导出excel，名称不应固定 start
		//String excel_filename="ry_tj_123456.xls";
//		String excel_filename=/*userView.getUserFullName()+"_"+*/PubFunc.getStrg()+".xls";
		//liuy end
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
		HSSFRow row=null;
		HSSFCell csCell=null;
		short n=0;
		if(snameplay!=null&&!"".equals(snameplay)){
			HSSFFont font = workbook.createFont();			
			font.setColor(HSSFFont.COLOR_NORMAL);
			font.setBold(true);
			HSSFCellStyle cellStyle= workbook.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setAlignment(HorizontalAlignment.CENTER);
			//row=sheet.createRow(n);
			row = sheet.getRow(n);
			if(row==null) {
                row = sheet.createRow(n);
            }

			csCell=row.createCell(Short.parseShort(String.valueOf(dlist.size()/2)));
			csCell.setCellStyle(cellStyle);
			//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
			csCell.setCellValue(snameplay+" ("+tolvalue+")");
			n++;
			n++;
			
			cellStyle= workbook.createCellStyle();
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			
			//row=sheet.createRow(n);
			row = sheet.getRow(n);
			if(row==null) {
                row = sheet.createRow(n);
            }
			csCell =row.createCell((short)0);
			//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
			csCell.setCellValue("");
			csCell.setCellStyle(cellStyle);
			LazyDynaBean bean = new LazyDynaBean();
			for(int i=0;i<dlist.size();i++){
				bean = (LazyDynaBean)dlist.get(i);
				csCell = row.createCell((short)(i+1));
				csCell.setCellStyle(cellStyle);
				//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
				csCell.setCellValue(bean.get("legend").toString());
			}
			
			cellStyle= workbook.createCellStyle();
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			
			for(int i=0;i<getValues.length;i++){
				for(int j=0;j<getValues[i].length;j++){
					//row=sheet.createRow(n+j+1);
					row = sheet.getRow(n+j+1);
					if(row==null) {
                        row = sheet.createRow(n+j+1);
                    }

					csCell =row.createCell((short)(0));
					csCell.setCellStyle(cellStyle);
					bean = (LazyDynaBean)hlist.get(j);
					//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
					csCell.setCellValue(bean.get("legend").toString());
					csCell =row.createCell((short)(i+1));
					csCell.setCellStyle(cellStyle);
					int number =getValues[i][j];
					//csCell.setEncoding((short) HSSFCell.CELL_TYPE_NUMERIC);					
					csCell.setCellValue(number);
					
				}
			}
			FileOutputStream fileOut = null;
			try
			{
				fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+excel_filename);
				workbook.write(fileOut);
			}catch(Exception e)
			{
				e.printStackTrace();
			} finally{
				PubFunc.closeResource(fileOut);
				PubFunc.closeResource(workbook);
			}
			
			
		}
		
		return excel_filename;
	}
	public String creatExcel(double[][] getValues,List dlist,List hlist,String snameplay,double tolvalue,int decimalwidth,String excel_filename)
	{
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
		HSSFRow row=null;
		HSSFCell csCell=null;
		short n=0;
		if(snameplay!=null&&!"".equals(snameplay)){
			HSSFFont font = workbook.createFont();			
			font.setColor(HSSFFont.COLOR_NORMAL);
			font.setBold(true);
			HSSFCellStyle cellStyle= workbook.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setAlignment(HorizontalAlignment.CENTER);
			//row=sheet.createRow(n);
			row = sheet.getRow(n);
			if(row==null) {
                row = sheet.createRow(n);
            }

			csCell=row.createCell(Short.parseShort(String.valueOf(dlist.size()/2)));
			csCell.setCellStyle(cellStyle);
			//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
			csCell.setCellValue(snameplay+" ("+PubFunc.formatDecimals(tolvalue,decimalwidth)+")");
			n++;
			n++;
			
			cellStyle= workbook.createCellStyle();
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			
			//row=sheet.createRow(n);
			row = sheet.getRow(n);
			if(row==null) {
                row = sheet.createRow(n);
            }
			csCell =row.createCell((short)0);
			//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
			csCell.setCellValue("");
			csCell.setCellStyle(cellStyle);
			LazyDynaBean bean = new LazyDynaBean();
			for(int i=0;i<dlist.size();i++){
				bean = (LazyDynaBean)dlist.get(i);
				csCell = row.createCell((short)(i+1));
				csCell.setCellStyle(cellStyle);
				//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
				csCell.setCellValue(bean.get("legend").toString());
			}
			
			cellStyle= workbook.createCellStyle();
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			
			for(int i=0;i<getValues.length;i++){
				for(int j=0;j<getValues[i].length;j++){
					//row=sheet.createRow(n+j+1);
					row = sheet.getRow(n+j+1);
					if(row==null) {
                        row = sheet.createRow(n+j+1);
                    }

					csCell =row.createCell((short)(0));
					csCell.setCellStyle(cellStyle);
					bean = (LazyDynaBean)hlist.get(j);
					//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
					csCell.setCellValue(bean.get("legend").toString());
					csCell =row.createCell((short)(i+1));
					csCell.setCellStyle(cellStyle);
					double number =getValues[i][j];
					//csCell.setEncoding((short) HSSFCell.CELL_TYPE_NUMERIC);					
					csCell.setCellValue(PubFunc.formatDecimals( number,decimalwidth));
					
				}
			}
			FileOutputStream fileOut = null;
			try
			{
				fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+excel_filename);
				workbook.write(fileOut);
			}catch(Exception e)
			{
				e.printStackTrace();
			}finally {
				PubFunc.closeResource(fileOut);
				PubFunc.closeResource(workbook);
			}
			
			
		}
		
		return excel_filename;
	}
}
