package com.hjsj.hrms.businessobject.general.template;

import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
 *<p>Title:ShowExcel.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Sep 26, 2008:2:23:37 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class ShowExcel {
	private Connection conn;
	public ShowExcel(){}
	public ShowExcel(Connection conn)
	{
		this.conn=conn;
	}
	public String creatExcel(ArrayList column,ArrayList infolist,ArrayList columnlist,String username)
	{
		String excel_filename="rw_jk_"+username+PubFunc.getStrg()+".xls";
		HSSFWorkbook workbook = new HSSFWorkbook();
		FileOutputStream fileOut = null;
		try {
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
			
			ExportExcelUtil.mergeCell(sheet, 0,(short)0,0,column.size());//指定合并区域
			row=sheet.createRow(n);
			csCell=row.createCell(Short.parseShort("0"));
			csCell.setCellStyle(cellStyle);
			HSSFRichTextString ss = new HSSFRichTextString("任务监控流程");
			csCell.setCellValue(ss);
			n++;n++;
			
			row=sheet.createRow(n);
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
			
			cellStyle= workbook.createCellStyle();
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			
			for(int i=0;i<infolist.size();i++){
				row=sheet.createRow(n+i+1);
				csCell =row.createCell((short)(0));
				csCell.setCellStyle(cellStyle);
				ss = new HSSFRichTextString(String.valueOf(i+1));
				csCell.setCellValue(ss);
				bean = (LazyDynaBean)infolist.get(i);
				
				for(int j=0;j<columnlist.size();j++){
					String name = (String)bean.get((String)columnlist.get(j));
					int  d=0;
					d= name.length();
					if(d>30) {
                        d=32;
                    }
					if("start_date".equalsIgnoreCase(columnlist.get(j).toString())|| "end_date".equalsIgnoreCase(columnlist.get(j).toString())) {
                        d=d*300;
                    } else {
                        d=d*900;
                    }
					if(d==0){//bug 36658 当没有数据时，d为0，列宽设置为0.
						d=column.get(j).toString().length()*900;
					}
					sheet.setColumnWidth(Short.parseShort(String.valueOf(j+1)),Short.parseShort(String.valueOf(d)));
					csCell =row.createCell((short)(j+1));
					csCell.setCellStyle(cellStyle);
					ss = new HSSFRichTextString(name);
					csCell.setCellValue(ss);
				}
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
	public String creatOpinionExcel(ArrayList column,ArrayList infolist,ArrayList columnlist,String username,String template_name,String usernames,String type)
	{
		//60732 VFS+UTF- 8+达梦：人事异动/业务处理/任务监控，查看审批过程，导出excel以及功能导航/导出excel，模板统一命名为： 登陆用户_相应信息
		String excel_filename=username+"_"+template_name+".xls";//spgc_su
		HSSFWorkbook workbook = new HSSFWorkbook();
		FileOutputStream fileOut = null;
		try {
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
			
			HSSFRichTextString ss=null;
			
			font = workbook.createFont();			
			font.setColor(HSSFFont.COLOR_NORMAL);
			font.setFontHeightInPoints((short)9);
			font.setBold(true); 
			cellStyle= workbook.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setAlignment(HorizontalAlignment.CENTER);//水平居中
			
			
			ExportExcelUtil.mergeCell(sheet, n,(short)0,n,Short.parseShort(String.valueOf(column.size())));//指定合并区域
			row=sheet.createRow(n);
			row.setHeight(Short.parseShort("600"));		
			csCell=row.createCell(Short.parseShort("0"));
			csCell.setCellStyle(cellStyle);
			ss = new HSSFRichTextString(template_name);
			csCell.setCellValue(ss);
			
			
			n++;
			cellStyle= workbook.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setAlignment(HorizontalAlignment.LEFT);//水平居左
			
			
			ExportExcelUtil.mergeCell(sheet, n,(short)1,n,Short.parseShort(String.valueOf(column.size())));//指定合并区域
			row=sheet.createRow(n);
			row.setHeight(Short.parseShort("600"));		
			csCell=row.createCell(Short.parseShort("0"));
			
			csCell.setCellStyle(cellStyle);
			
			if(type!=null&& "10".equals(type)){
				ss = new HSSFRichTextString("机构名称");
			}else if(type!=null&& "11".equals(type)){
				ss = new HSSFRichTextString("岗位名称");
			}else{
				ss = new HSSFRichTextString("姓名");
			}
			
			csCell.setCellValue(ss);
			
			csCell=row.createCell(Short.parseShort("1"));
			csCell.setCellStyle(cellStyle);
			if(usernames.length()>0) {
                usernames=usernames.substring(1);
            }
			ss = new HSSFRichTextString(usernames);
			csCell.setCellValue(ss);
			n++;
			
			row=sheet.createRow(n);
			row.setHeight(Short.parseShort("600"));		
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
			cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle.setAlignment(HorizontalAlignment.CENTER);//水平居中
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
			cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle.setAlignment(HorizontalAlignment.CENTER);//水平居中
			for(int i=0;i<column.size();i++){
				String name = (String)column.get(i);
				csCell = row.createCell((short)(i+1));
				csCell.setCellStyle(cellStyle);
				ss = new HSSFRichTextString(name);
				csCell.setCellValue(ss);
			}
			
			cellStyle= workbook.createCellStyle();
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			cellStyle.setWrapText(true);
			cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			HashMap mapwidth = new HashMap();
			for(int i=0;i<infolist.size();i++){
				row=sheet.createRow(n+i+1);
				csCell =row.createCell((short)(0));
				csCell.setCellStyle(cellStyle);
				ss = new HSSFRichTextString(String.valueOf(i+1));
				csCell.setCellValue(ss);
				bean = (LazyDynaBean)infolist.get(i);
				row.setHeight(Short.parseShort("600"));		
				for(int j=0;j<columnlist.size();j++){
					String name = (String)bean.get((String)columnlist.get(j));
					int  d=0;
					name=name.replace("<p>", "").replace("</p>", "\r\n");
					name=name.replace("&nbsp;", " ");
					d= name.length();
					if(d>30) {
                        d=32;
                    }
					if("start_date".equalsIgnoreCase(columnlist.get(j).toString())|| "end_date".equalsIgnoreCase(columnlist.get(j).toString())) {
                        d=d*300;
                    } else {
                        d=d*900;
                    }
					if(d>7000) {
                        d=7000;
                    }
					//如果备注没有内容，那么上一行的“备注”两个字会显示不开  郭峰
					if("content".equalsIgnoreCase(columnlist.get(j).toString()) && d<1800){
						d=3600;
					}
					if(mapwidth!=null&&mapwidth.get(j+1+"")!=null){
						if(d>Integer.parseInt(mapwidth.get(j+1+"").toString())) {
                            mapwidth.put(j+1+"", ""+d);
                        }
					}else{
						mapwidth.put(j+1+"", ""+d);
					}
					sheet.setColumnWidth(Short.parseShort(String.valueOf(j+1)),Short.parseShort(mapwidth.get(j+1+"").toString()));
					
					csCell =row.createCell((short)(j+1));
					csCell.setCellStyle(cellStyle);
					ss = new HSSFRichTextString(name);
					csCell.setCellValue(ss);
				} 
				
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
