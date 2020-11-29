package com.hjsj.hrms.businessobject.lawbase;

import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.struts.admin.OnlineUserView;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Random;
/**
 * 
 *<p>Title:LawbaseExcel.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Jul 19, 2008:2:52:54 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class LawbaseExcel {
	private Connection conn;
	private UserView userView;
	private static Random r = new Random();
	public LawbaseExcel(UserView userView){
		this.userView=userView;
	}
	public LawbaseExcel(){}
	public LawbaseExcel(Connection conn)
	{
		this.conn=conn;
	}
	public String creatExcel(ArrayList column,ArrayList infolist,ArrayList columnlist)
	{
		//zhangh 2020-1-6 【56214】发文管理和制度政策中，涉及到导出的（pdf、word、excel、zip等等）命名，请统一成： 登陆用户_相应信息
		String excel_filename=this.userView.getUserName()+ "_" + com.hjsj.hrms.utils.PubFunc.getStrg() +".xls";
		HSSFWorkbook workbook = null;
		FileOutputStream fileOut = null;
		try
		{
			workbook = new HSSFWorkbook();
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
			
			ExportExcelUtil.mergeCell(sheet, 0,(short)0,0,Short.parseShort(String.valueOf(column.size())));//指定合并区域
			row=sheet.createRow(n);
			csCell=row.createCell(Short.parseShort("0"));
			csCell.setCellStyle(cellStyle);
			HSSFRichTextString ss = new HSSFRichTextString("文档管理");
			csCell.setCellValue(ss);
			n++;
			
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
					csCell =row.createCell((short)(j+1));
					csCell.setCellStyle(cellStyle);
					String name = (String)bean.get((String)columnlist.get(j));
					ss = new HSSFRichTextString(name);
					csCell.setCellValue(ss);
				}
			}
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+excel_filename);
			workbook.write(fileOut);
		}catch(Exception e)
		{
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(workbook);
		}
		return excel_filename;
	}
	public String creatExcel(ArrayList column,ArrayList infolist,EncryptLockClient lockclient)
	{
		String excel_filename=r.nextInt(10000)+"file.xls";
		HSSFWorkbook workbook = null;
		FileOutputStream fileOut = null;
		try
		{
			workbook = new HSSFWorkbook();
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
			sheet.setColumnWidth(6, 6000);
			ExportExcelUtil.mergeCell(sheet, 0,(short)0,0,Short.parseShort(String.valueOf(column.size())));//指定合并区域
			row=sheet.createRow(n);
			csCell=row.createCell(Short.parseShort("0"));
			csCell.setCellStyle(cellStyle);
			HSSFRichTextString ss = new HSSFRichTextString(ResourceFactory.getProperty("column.sys.loginusers"));
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
				OnlineUserView bean = (OnlineUserView)infolist.get(i);
				
				csCell =row.createCell((short)(1));
				csCell.setCellStyle(cellStyle);
				String orgname = AdminCode.getCodeName("UN", bean.getOrgname());
				ss = new HSSFRichTextString(orgname);
				csCell.setCellValue(ss);
				
				csCell =row.createCell((short)(2));
				csCell.setCellStyle(cellStyle);
				String dept = AdminCode.getCodeName("UM", bean.getDept());
				ss = new HSSFRichTextString(dept);
				csCell.setCellValue(ss);
				
				csCell =row.createCell((short)(3));
				csCell.setCellStyle(cellStyle);
				String pos = AdminCode.getCodeName("@K", bean.getPos());
				ss = new HSSFRichTextString(pos);
				csCell.setCellValue(ss);
				
				csCell =row.createCell((short)(4));
				csCell.setCellStyle(cellStyle);
				String username = bean.getUsername();
				String sss=lockclient.getTheUserAccessModule(bean.getUserId());
				if(sss.length()>0)
				{
					sss="("+sss+")";
				}
				ss = new HSSFRichTextString(username+sss);
				csCell.setCellValue(ss);
				
				csCell =row.createCell((short)(5));
				csCell.setCellStyle(cellStyle);
				String ip_addr = bean.getIp_addr();
				ss = new HSSFRichTextString(ip_addr);
				csCell.setCellValue(ss);
				
				csCell =row.createCell((short)(6));
				csCell.setCellStyle(cellStyle);
				String login_date = bean.getLogin_date();
				ss = new HSSFRichTextString(login_date);
				csCell.setCellValue(ss);
				
			}
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+excel_filename);
			workbook.write(fileOut);
		}catch(Exception e)
		{
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(workbook);
		}
		return excel_filename;
	}
	//导出的excel文件名称为 用户名_随机号       jingq  add  2014.5.7
	public String creatExcel(ArrayList column,ArrayList infolist,EncryptLockClient lockclient,String uname)
	{
		String excel_filename=uname+"_"+PubFunc.getStrg()+".xls";
		HSSFWorkbook workbook = null;
		FileOutputStream fileOut = null;
		try
		{
			workbook = new HSSFWorkbook();
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
			sheet.setColumnWidth(6, 6000);
			ExportExcelUtil.mergeCell(sheet, 0,(short)0,0,Short.parseShort(String.valueOf(column.size())));//指定合并区域
			row=sheet.createRow(n);
			csCell=row.createCell(Short.parseShort("0"));
			csCell.setCellStyle(cellStyle);
			HSSFRichTextString ss = new HSSFRichTextString(ResourceFactory.getProperty("column.sys.loginusers"));
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
				OnlineUserView bean = (OnlineUserView)infolist.get(i);
				
				csCell =row.createCell((short)(1));
				csCell.setCellStyle(cellStyle);
				String orgname = AdminCode.getCodeName("UN", bean.getOrgname());
				ss = new HSSFRichTextString(orgname);
				csCell.setCellValue(ss);
				
				csCell =row.createCell((short)(2));
				csCell.setCellStyle(cellStyle);
				String dept = AdminCode.getCodeName("UM", bean.getDept());
				ss = new HSSFRichTextString(dept);
				csCell.setCellValue(ss);
				
				csCell =row.createCell((short)(3));
				csCell.setCellStyle(cellStyle);
				String pos = AdminCode.getCodeName("@K", bean.getPos());
				ss = new HSSFRichTextString(pos);
				csCell.setCellValue(ss);
				
				csCell =row.createCell((short)(4));
				csCell.setCellStyle(cellStyle);
				String username = bean.getUsername();
				String sss=lockclient.getTheUserAccessModule(bean.getUserId());
				if(sss.length()>0)
				{
					sss="("+sss+")";
				}
				ss = new HSSFRichTextString(username+sss);
				csCell.setCellValue(ss);
				
				csCell =row.createCell((short)(5));
				csCell.setCellStyle(cellStyle);
				String ip_addr = bean.getIp_addr();
				ss = new HSSFRichTextString(ip_addr);
				csCell.setCellValue(ss);
				
				csCell =row.createCell((short)(6));
				csCell.setCellStyle(cellStyle);
				String login_date = bean.getLogin_date();
				ss = new HSSFRichTextString(login_date);
				csCell.setCellValue(ss);
				
			}
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+excel_filename);
			workbook.write(fileOut);
		}catch(Exception e)
		{
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(workbook);
		}
		return excel_filename;
	}
	public String creatExcel1(ArrayList column,ArrayList infolist,ArrayList columnlist)
	{	
		String excel_filename=this.userView.getUserName()+"_"+PubFunc.getStrg()+".xls";
		HSSFWorkbook workbook = null;
		FileOutputStream fileOut = null;
		try {
			workbook = new HSSFWorkbook();
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
			
			ExportExcelUtil.mergeCell(sheet, 0,(short)0,0,Short.parseShort(String.valueOf(column.size())));//指定合并区域
			row=sheet.createRow(n);
			csCell=row.createCell(Short.parseShort("0"));
			csCell.setCellStyle(cellStyle);
			HSSFRichTextString ss = new HSSFRichTextString("汇总表");
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
				//ss = new HSSFRichTextString(String.valueOf(i+1));
				//csCell.setCellValue(ss);
				csCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				csCell.setCellValue(Double.parseDouble(String.valueOf(i+1)));
				
				
				bean = (LazyDynaBean)infolist.get(i);
				for(int j=0;j<columnlist.size();j++){
					csCell =row.createCell((short)(j+1));
					csCell.setCellStyle(cellStyle);
					String name = (String)bean.get((String)columnlist.get(j));
					if(!("b0110".equalsIgnoreCase((String)columnlist.get(j))|| "sp_flag".equalsIgnoreCase((String)columnlist.get(j))))
					{	
						csCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						csCell.setCellValue(Double.parseDouble(name));
					}
					else
					{
						ss = new HSSFRichTextString(name);
						csCell.setCellValue(ss);
					}
				}
			}
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+excel_filename);
			workbook.write(fileOut);
		}catch(Exception e)
		{
			e.printStackTrace();
		} finally{
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(workbook);
		}
		return excel_filename;
	}
}
