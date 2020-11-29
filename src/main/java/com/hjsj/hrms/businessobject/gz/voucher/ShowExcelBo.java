package com.hjsj.hrms.businessobject.gz.voucher;

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
 *<p>Company:hjsj</p> 
 *@author xuchangshun
 *@version 1.0
 */
public class ShowExcelBo {
	private Connection conn;
	public ShowExcelBo(){}
	public ShowExcelBo(Connection conn)
	{
		this.conn=conn;
	}
	public String creatExcel(ArrayList column,ArrayList infolist,ArrayList columnlist)
	{
		String excel_filename="财务凭证"+PubFunc.getStrg()+".xls";
		HSSFWorkbook workbook = new HSSFWorkbook();
		FileOutputStream fileOut = null;
		try {
			HSSFSheet sheet = workbook.createSheet("sheet1");
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
			row=sheet.createRow(n);
			csCell=row.createCell(Short.parseShort("0"));
			csCell.setCellStyle(cellStyle);
			HSSFRichTextString ss = new HSSFRichTextString("财务凭证");
			csCell.setCellValue(ss);
			n++;n++;
			
			row=sheet.createRow(n);
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
				csCell = row.createCell((short)(i));
				csCell.setCellStyle(cellStyle);
				ss = new HSSFRichTextString(name);
				csCell.setCellValue(ss);
				if("摘要".equalsIgnoreCase(name)|| "科目".equalsIgnoreCase(name)|| "分录名称".equalsIgnoreCase(name)){
					sheet.setColumnWidth(i, 55*160);
				}else{
					sheet.setColumnWidth(i, 20*160);
				}
			}
			
			cellStyle= workbook.createCellStyle();
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			
			for(int i=0;i<infolist.size();i++){
				row=sheet.createRow(n+i+1);
				
				bean = (LazyDynaBean)infolist.get(i);
				for(int j=0;j<columnlist.size();j++){
					csCell =row.createCell((short)(j));
					csCell.setCellStyle(cellStyle);
					String name = (String)bean.get((String)columnlist.get(j));
					ss = new HSSFRichTextString(name);
					csCell.setCellValue(ss);
				}
				if(i==24999)
					break;
			}
			int index = 2;
			int rows=0;
			for(int i=25000;i<infolist.size();i++){
				if(i%25000==0){
					sheet = workbook.createSheet("sheet"+index);
					index++;
					rows = 1;
					for(int j=0;j<column.size();j++){
						row=sheet.createRow(0);
						if(j==0)
						{
							csCell=row.createCell((short)(0));
							csCell.setCellStyle(cellStyle);
							ss = new HSSFRichTextString("序号");
							csCell.setCellValue(ss);
						}
						String name = (String)column.get(j);
						csCell = row.createCell((short)(j+1));
						csCell.setCellStyle(cellStyle);
						ss = new HSSFRichTextString(name);
						csCell.setCellValue(ss);
					}
				}
				row=sheet.createRow(rows);
				csCell =row.createCell((short)(0));
				csCell.setCellStyle(cellStyle);
				ss = new HSSFRichTextString(String.valueOf(rows+1));
				csCell.setCellValue(ss);
				bean = (LazyDynaBean)infolist.get(i);
				for(int j=0;j<columnlist.size();j++){
					csCell =row.createCell((short)(j+1));
					csCell.setCellStyle(cellStyle);
					String name = (String)bean.get((String)columnlist.get(j));
					ss = new HSSFRichTextString(name);
					csCell.setCellValue(ss);
				}
				rows++;
			}
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+excel_filename);
			workbook.write(fileOut);
			System.gc();
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(workbook);
		}
		
		
		return excel_filename;
	}
}

