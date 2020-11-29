package com.hjsj.hrms.businessobject.gz.templateset;

import com.hjsj.hrms.businessobject.general.muster.ExecuteExcel;
import com.hjsj.hrms.businessobject.gz.ReportPageOptionsBo;
import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class GzExcelBo {
	private Connection conn=null;
	private HSSFWorkbook wb=null;
	private HSSFSheet sheet=null;
	private HSSFCellStyle style=null;
	private HSSFCellStyle style_l=null;
	private HSSFCellStyle style_r=null;
	private HSSFCellStyle style_title=null;
	private HSSFCellStyle style_thead=null;
	HSSFDataFormat dataformat=null;
	private HSSFCellStyle style_r_1=null;
	private HSSFCellStyle style_r_2=null;
	private HSSFCellStyle style_r_3=null;
	private HSSFCellStyle style_r_4=null;
	
	private HSSFPatriarch patr=null;
	short rowNum=1;
	
	private Workbook wb2=null;
	private Sheet sheet2=null;
	
	public GzExcelBo(Connection con)
	{
		this.conn=con;
	}
	
	private int nums=0;

	/**
	 * 工资报表数据导出
	 * @param rsid 表类id
	 * @param tableHeadList 表头列表 总有”签名“，没有序号
	 * @param datalist  数据列表
	 * @param isShowHead 是否显示列标题
	 * @param isShowSeria 是否显示序号
	 * @param isSign 是否显示签名
	 * @return
	 */
	public String executeGzReportExcel(String rsid,ArrayList tableHeadList,ArrayList datalist,String isShowHead,String isShowSeria,String isSign,UserView view,String rsdtlid,HashMap groupMap,String recordNums)
	{
		String fileName="gz_"+PubFunc.getStrg()+".xls";
		RowSet rs = null;
		String url=System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+fileName;
		try
		{
			ReportPageOptionsBo rpob = new ReportPageOptionsBo(this.conn,view,rsid,rsdtlid);
			rpob.init();
			ReportParseVo rpv = rpob.analyse(2);
			 this.wb = new HSSFWorkbook();
			 this.sheet = wb.createSheet();
			 dataformat = wb.createDataFormat();
			 this.style = getStyle("c",wb);
			 this.style_l = getStyle("l",wb);
			 this.style_r = getStyle("r",wb);
			// this.style_r.setDataFormat(dataformat.getFormat("0"));
			 this.style_r_1 = getStyle("r",wb);
			 this.style_r_1.setDataFormat(dataformat.getFormat("0.0"));
			 this.style_r_2 = getStyle("r",wb);
			 this.style_r_2.setDataFormat(dataformat.getFormat("0.00"));
			 this.style_r_3 = getStyle("r",wb);
			 this.style_r_3.setDataFormat(dataformat.getFormat("0.000"));
			 this.style_r_4 = getStyle("r",wb);
			 this.style_r_4.setDataFormat(dataformat.getFormat("0.0000"));
			 this.style_thead=getStyle("c",wb);
			 this.rowNum=0;
			 this.nums=Integer.parseInt(recordNums);
			 String t_fontfamilyname=ResourceFactory.getProperty("gz.gz_acounting.m.font");
		     String t_fontEffect="0";
		     int    t_fontSize=11;  
		     String  t_underLine="#fu[0]";
		     if(rpv.getBody_fn().length()>0)
	        		t_fontfamilyname=rpv.getBody_fn();
		     t_fontEffect=getFontEffect(rpv,4);
		     if(rpv.getBody_fz().length()>0)
		       	t_fontSize=Integer.parseInt(rpv.getBody_fz());      	 
		    if(rpv.getBody_fu()!=null&&rpv.getBody_fu().length()>0)
		        t_underLine=rpv.getBody_fu();
		    HSSFFont font = this.wb.createFont();	
		    font.setFontName(t_fontfamilyname);//设置字体种类
			if("2".equals(t_fontEffect)){
				font.setBold(true); //设置字体
			}else if("4".equals(t_fontEffect)){
				font.setItalic( true ); // 是否使用斜体
			}else if("3".equals(t_fontEffect)){
				font.setBold(true); //设置字体
				font.setItalic( true ); // 是否使用斜体
			}
			if("#fu[1]".equals(t_underLine)){
				font.setUnderline(HSSFFont.U_SINGLE);
			}
			font.setFontHeightInPoints((short)t_fontSize);//设置字体大小
			if(rpv.getBody_fc().length()>0)
			{
				String color = rpv.getBody_fc();
				if(color!=null&&color.startsWith("#"))
				   color = color.substring(1);
				int rgbC=0;
				if(color.trim().length()>0)
				{
					font.setColor(HSSFColor.GREY_50_PERCENT.index);
				}
			    if(color.trim().length()>0)
			    {
			    	 String r_str = color.substring(0, 2);
					 String g_str = color.substring(2, 4);
					 String b_str = color.substring(4, 6);
					 short r = (short)Integer.parseInt(r_str,16);
					 short g = (short)Integer.parseInt(g_str, 16);
					 short b = (short)Integer.parseInt(b_str, 16);
					 HSSFPalette palette = this.wb.getCustomPalette();
					 palette.setColorAtIndex(HSSFColor.GREY_50_PERCENT.index, (byte)r, (byte)g, (byte)b);
					 
			    } 
			}
			
			 String thead_fontfamilyname=ResourceFactory.getProperty("gz.gz_acounting.m.font");
		     String thead_fontEffect="0";
		     int    thead_fontSize=11;  
		     String  thead_underLine="#fu[0]";
		     if(rpv.getThead_fn().length()>0)
		    	 thead_fontfamilyname=rpv.getThead_fn();
		     thead_fontEffect=getFontEffect(rpv,5);
		     if(rpv.getThead_fz().length()>0)
		    	 thead_fontSize=Integer.parseInt(rpv.getThead_fz());      	 
		    if(rpv.getThead_fu()!=null&&rpv.getThead_fu().length()>0)
		    	thead_underLine=rpv.getThead_fu();
		    HSSFFont thead_font = this.wb.createFont();	
		    thead_font.setFontName(thead_fontfamilyname);//设置字体种类
			if("2".equals(thead_fontEffect)){
				thead_font.setBold(true); //设置字体
			}else if("4".equals(thead_fontEffect)){
				thead_font.setItalic( true ); // 是否使用斜体
			}else if("3".equals(thead_fontEffect)){
				thead_font.setBold(true); //设置字体
				thead_font.setItalic( true ); // 是否使用斜体
			}
			if("#fu[1]".equals(thead_underLine)){
				thead_font.setUnderline(HSSFFont.U_SINGLE);
			}
			thead_font.setFontHeightInPoints((short)thead_fontSize);//设置字体大小
			if(rpv.getThead_fc().length()>0)
			{
				String color = rpv.getThead_fc();
				if(color!=null&&color.startsWith("#"))
				   color = color.substring(1);
				int rgbC=0;
				if(color.trim().length()>0)
				{
					thead_font.setColor(HSSFColor.ROSE.index);
				}
			    if(color.trim().length()>0)
			    {
			    	 String r_str = color.substring(0, 2);
					 String g_str = color.substring(2, 4);
					 String b_str = color.substring(4, 6);
					 short r = (short)Integer.parseInt(r_str,16);
					 short g = (short)Integer.parseInt(g_str, 16);
					 short b = (short)Integer.parseInt(b_str, 16);
					 HSSFPalette palette = this.wb.getCustomPalette();
					 palette.setColorAtIndex(HSSFColor.ROSE.index, (byte)r, (byte)g, (byte)b); 
			    } 
			}
			style_thead.setFont(thead_font);
			style.setFont(font);
			style_l.setFont(font);
			style_r.setFont(font);
			style_r_1.setFont(font);
			style_r_2.setFont(font);
			style_r_3.setFont(font);
			style_r_4.setFont(font);
			 this.rowNum=this.executeTitle(rsid, rsdtlid, tableHeadList, isShowSeria, isSign, view, rowNum, sheet, row, wb,"0");
			 row = sheet.getRow(this.rowNum);
			 if(row==null)
				 row = sheet.createRow(this.rowNum);
			 row.setHeight((short)400);
			 int dataNumber=0;
			 /**是否显示列标题*/
			 if(isShowHead==null|| "1".equals(isShowHead))
			 {
				 if(isShowSeria==null|| "1".equals(isShowSeria))
				 {
	        		 executeCell3((short)0,"序号","T","A","6");
	        		 dataNumber=1;
				 }
	    		 for(int i=0;i<tableHeadList.size();i++)
	    		 {
		    		 LazyDynaBean bean=(LazyDynaBean)tableHeadList.get(i);
		    		 String itemdesc=(String)bean.get("itemdesc");
		    		 String itemtype=(String)bean.get("itemtype");
		    		 String width="4";
					 if(bean.get("nwidth")!=null)
						 width = (String)bean.get("nwidth");
		    		 if("2".equals(rsid)|| "3".equals(rsid)|| "12".equals(rsid)|| "13".equals(rsid))
		    		 {
		    			 if(!(isSign==null|| "1".equals(isSign)))
		    			 {
		    				 if("签名".equalsIgnoreCase(itemdesc))
		    				 {
		    					 dataNumber=0;
		    					 continue;
		    				 }
		    			 }
		    		 }
			    	 short col=Short.parseShort(String.valueOf((i+dataNumber)));
			    	 if(itemdesc.length()>Integer.parseInt(width))
			    	      width=itemdesc.length()+"";
			    	 executeCell3(col,itemdesc,"T",itemtype,width); 
		    	 }
	    		 if(("2".equals(rsid)|| "12".equals(rsid))&&(isShowSeria==null|| "1".equals(isShowSeria)))
				 {
	        		   executeCell3((short)(tableHeadList.size()+dataNumber),"序号","T","A","6");
				 }
	    		 row = sheet.getRow(this.rowNum);
				 if(row==null)
					 row = sheet.createRow(this.rowNum);
				 row.setHeight((short)400);   
			 }
			 this.rowNum++;
			 boolean isPrintByGroup = rpob.isGroupPrint(rsid, rsdtlid, 0);
			 for(int i=0;i<datalist.size();i++)
			 {
				 if(isPrintByGroup&&groupMap.get(i+"")!=null)
				 {
					 sheet.setRowBreak(rowNum);
				 }
				 dataNumber=0;
				 ArrayList recordList=(ArrayList)datalist.get(i);
				 if("1".equals(rsid)&&(i+1)%3==0)
				 {
					 executeCell(this.rowNum,(short)0,this.rowNum,Short.parseShort(String.valueOf(recordList.size()-1)),"","no_c",(short)400);
				 }
				 else
				 {
					 String value="";
					 /**加序号*/
					if(isShowSeria==null|| "1".equals(isShowSeria))
					{
			    		 value=(String)recordList.get(0);
				    	 executeCell3(Short.parseShort(String.valueOf(0)),value.trim(),"c","N","6");
				    	 dataNumber=1;
					}
					 for(int j=0;j<tableHeadList.size();j++)
					 {
						 LazyDynaBean bean=(LazyDynaBean)tableHeadList.get(j);
						 String itemdesc=(String)bean.get("itemdesc");
						 String itemtype=(String)bean.get("itemtype");
						 String width="15";
						 if(bean.get("nwidth")!=null)
							 width = (String)bean.get("nwidth");
						 if(itemdesc.length()>Integer.parseInt(width))
						      width=itemdesc.length()+"";
						 if("人数".equalsIgnoreCase(itemdesc))
							 itemtype="N";
						 if("2".equals(rsid)|| "3".equals(rsid)|| "12".equals(rsid)|| "13".equals(rsid))
						 {
							 if(!(isSign==null|| "1".equals(isSign)))
			    			 {
			    				 if("签名".equalsIgnoreCase(itemdesc))
			    				 {
			    					 continue;
			    				 }
			    			 }
						 }
						 if(bean.get("itemtype")!=null&& "N".equals((String)bean.get("itemtype")))
						 {
							 /**显示序号*/
							 if(isShowSeria==null|| "1".equals(isShowSeria))
							 {
						    	 value=(String)recordList.get(j+1);
							 }
							 else
							 {
								 value=(String)recordList.get(j);
							 }
							 if("2".equals(rsid)|| "3".equals(rsid)|| "12".equals(rsid)|| "13".equals(rsid))
				    		 {
				    			 if(!(isSign==null|| "1".equals(isSign)))
				    			 {
				    				 if("签名".equalsIgnoreCase(itemdesc))
				    				 {
				    					 dataNumber=0;
				    					 continue;
				    				 }
				    			 }
				    		 }
							 executeCell3(Short.parseShort(String.valueOf(j+dataNumber)),value,"r",itemtype,width);		 
						 }
						 else
						 {
							 if(isShowSeria==null|| "1".equals(isShowSeria))
							 {
						    	 value=(String)recordList.get(j+1);
							 }
							 else
							 {
								 value=(String)recordList.get(j);
							 }
							 if("2".equals(rsid)|| "3".equals(rsid)|| "12".equals(rsid)|| "13".equals(rsid))
				    		 {
				    			 if(!(isSign==null|| "1".equals(isSign)))
				    			 {
				    				 if("签名".equalsIgnoreCase(itemdesc))
				    				 {
				    					 dataNumber=0;
				    					 continue;
				    				 }
				    			 }
				    		 }
							 executeCell3(Short.parseShort(String.valueOf((j+dataNumber))),value,"l",itemtype,width);
						 }
						 if(("2".equals(rsid)|| "12".equals(rsid))&&(isShowSeria==null|| "1".equals(isShowSeria)))
						{
					    	value=(String)recordList.get(0);
					        executeCell3(Short.parseShort(String.valueOf((recordList.size()-1))),value.trim(),"c","N","6"); 
						}
					 }
				 }				 
				 HSSFRow row = sheet.getRow(this.rowNum);
				 if(row==null)
					 row = sheet.createRow(this.rowNum);
				 row.setHeight((short)400);
				/* if(this.rowNum%20==0)
				     sheet.setRowBreak(rowNum);*/
				 this.rowNum++;
			 }
			 this.executeTail(rsid, rsdtlid, tableHeadList, isShowSeria, isSign, view, rowNum, sheet, row, wb);
			 HSSFPrintSetup printSetup=this.sheet.getPrintSetup();
			 if(rpv.getPagetype()!=null&&!"".equals(rpv.getPagetype().trim()))
			 {
				/* System.out.println("ENVELOPE_10_PAPERSIZE="+HSSFPrintSetup.ENVELOPE_10_PAPERSIZE);
				 System.out.println("ENVELOPE_CS_PAPERSIZE="+HSSFPrintSetup.ENVELOPE_CS_PAPERSIZE);
				 System.out.println("ENVELOPE_DL_PAPERSIZE="+HSSFPrintSetup.ENVELOPE_DL_PAPERSIZE);
				 System.out.println("ENVELOPE_MONARCH_PAPERSIZE="+HSSFPrintSetup.ENVELOPE_MONARCH_PAPERSIZE);//ENVELOPE信封的意思
				 System.out.println("EXECUTIVE_PAPERSIZE="+HSSFPrintSetup.EXECUTIVE_PAPERSIZE);
				 System.out.println("LEGAL_PAPERSIZE="+HSSFPrintSetup.LEGAL_PAPERSIZE);//合法的
				 System.out.println("LETTER_PAPERSIZE="+HSSFPrintSetup.LETTER_PAPERSIZE);//字母
				 */				 
				 if("A4".equalsIgnoreCase(rpv.getPagetype()))
					 printSetup.setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);
				 else if("A5".equalsIgnoreCase(rpv.getPagetype()))
					 printSetup.setPaperSize(HSSFPrintSetup.A5_PAPERSIZE);
			 }
			 if(rpv.getHeight()!=null&&!"".equals(rpv.getHeight()))
			 {
				 String gh=PubFunc.round(rpv.getHeight(), 0);
				 printSetup.setFitHeight(Short.parseShort(gh));
			 }
			 if(rpv.getWidth()!=null&&!"".equals(rpv.getWidth()))
			 {
				 String gh=PubFunc.round(rpv.getWidth(), 0);
				 printSetup.setFitWidth(Short.parseShort(gh));
			 }
			 if("1".equals(rpv.getOrientation()))//横向
			 {
				 printSetup.setLandscape(true);
			 }
			 else
			 {
				 printSetup.setLandscape(false);
			 }
            if(rpv.getTop()!=null&&!"".equals(rpv.getTop().trim()))
             {
            	 this.sheet.setMargin(HSSFSheet.TopMargin,Double.parseDouble(PubFunc.round(String.valueOf(Double.parseDouble(rpv.getTop())*2.5),1)));
             }
             if(rpv.getBottom()!=null&&!"".equals(rpv.getBottom().trim()))
             {
            	 this.sheet.setMargin(HSSFSheet.BottomMargin, Double.parseDouble(PubFunc.round(String.valueOf(Double.parseDouble(rpv.getBottom())*2.5),1)));
             }
             if(rpv.getLeft()!=null&&!"".equals(rpv.getLeft().trim()))
             {
            	 this.sheet.setMargin(HSSFSheet.LeftMargin, Double.parseDouble(PubFunc.round(String.valueOf(Double.parseDouble(rpv.getLeft())*2.5),1)));
             }
             if(rpv.getRight()!=null&&!"".equals(rpv.getRight().trim()))
             {
            	 this.sheet.setMargin(HSSFSheet.RightMargin, Double.parseDouble(PubFunc.round(String.valueOf(Double.parseDouble(rpv.getRight())*2.5),1)));
             }
			 FileOutputStream fileOut = new FileOutputStream(url);
			 this.wb.write(fileOut);
			 fileOut.close();	 
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(this.row!=null)
				this.row=null;
			if(this.sheet!=null)
				this.sheet=null;
			if(this.wb!=null)
				this.wb=null;
		}
		return fileName;
	}
	public short executeTail(String rsid,String rsdtlid,ArrayList tableHeadList,String isShowSeria,String isSign,UserView view,short rowNum,HSSFSheet sheet,HSSFRow row,HSSFWorkbook wb)
	{
		short rowN = rowNum;
		try
		{
			ReportPageOptionsBo rpob = new ReportPageOptionsBo(this.conn,view,rsid,rsdtlid);
			rpob.init();
			ReportParseVo rpv = rpob.analyse(2);
			 ExecuteExcel ee = new ExecuteExcel(this.conn);
			 int listSize = tableHeadList.size();
			 if(isShowSeria==null|| "1".equals(isShowSeria))
			 {
				 if("7".equals(rsid))
					 listSize++;
			 }
			 else
			 {
				 listSize--;
				 if("11".equals(rsid))
				 {
					 listSize--;
				 }
			 }
			 if(isSign==null|| "1".equals(isSign))
			 {
				 
			 }
			 else
			 {
				 listSize--;
			 }
			 if("10".equals(rsid))
			 {
				 listSize=3+(tableHeadList.size()-2)*4;
			 }
    		 if((rpv.getTile_flw()!=null&&rpv.getTile_flw().trim().length()>0)||(rpv.getTile_fmw()!=null&&rpv.getTile_fmw().trim().length()>0)||(rpv.getTile_frw()!=null&&rpv.getTile_frw().trim().length()>0))
			 {
				 row = sheet.getRow(rowN);
				 if(row==null)
					 row = sheet.createRow(rowN);
				 row.setHeight((short)500);
				 HSSFCellStyle titleStyle = ee.style(wb, 5);
		    	 String t_fontfamilyname=ResourceFactory.getProperty("gz.gz_acounting.m.font");
			     String t_fontEffect="0";
			     int    t_fontSize=11;  
			     String  t_underLine="#fu[0]";
			     String  t_strikethru="#fs[0]";
			     if(rpv.getTile_fn().length()>0)
		        		t_fontfamilyname=rpv.getHead_fn();
			     t_fontEffect=getFontEffect(rpv,4);
			     if(rpv.getTile_fz().length()>0)
			       	t_fontSize=Integer.parseInt(rpv.getTile_fz());      	 
			    if(rpv.getTile_fu()!=null&&rpv.getTile_fu().length()>0)
			        t_underLine=rpv.getTile_fu();
			    if(rpv.getTile_fs()!=null&&rpv.getTile_fs().length()>0)
			       		t_strikethru=rpv.getTile_fs();
		    	 
			    HSSFFont font = wb.createFont();	
			    font.setFontName(t_fontfamilyname);//设置字体种类
				if("2".equals(t_fontEffect)){
					font.setBold(true); //设置字体
				}else if("4".equals(t_fontEffect)){
					font.setItalic( true ); // 是否使用斜体
				}else if("3".equals(t_fontEffect)){
					font.setBold(true); //设置字体
					font.setItalic( true ); // 是否使用斜体
				}
				if("#fu[1]".equals(t_underLine)){
					font.setUnderline(HSSFFont.U_SINGLE);
				}
				if("#fs[1]".equals(t_strikethru)){
					font.setStrikeout(true);
				}
				font.setFontHeightInPoints((short)t_fontSize);//设置字体大小
				titleStyle.setAlignment(HorizontalAlignment.CENTER );
				titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
				titleStyle.setWrapText(true);
				titleStyle.setFont(font);
				if(rpv.getTile_fc().length()>0)
				{
					String color = rpv.getTile_fc();
					if(color!=null&&color.startsWith("#"))
					   color = color.substring(1);
					int rgbC=0;
					if(color.trim().length()>0)
					{
						font.setColor(HSSFColor.GREY_80_PERCENT.index);
					}
				    if(color.trim().length()>0)
				    {
				    	 String r_str = color.substring(0, 2);
						 String g_str = color.substring(2, 4);
						 String b_str = color.substring(4, 6);
						 short r = (short)Integer.parseInt(r_str,16);
						 short g = (short)Integer.parseInt(g_str, 16);
						 short b = (short)Integer.parseInt(b_str, 16);
						 HSSFPalette palette = wb.getCustomPalette();
						 palette.setColorAtIndex(HSSFColor.GREY_80_PERCENT.index, (byte)r, (byte)g, (byte)b);
				    } 
				}
				if(rpv.getTile_flw()!=null&&rpv.getTile_flw().trim().length()>0)
				{
					HSSFCell cell = row.createCell(0);
					cell.setCellStyle(titleStyle);
					cell.setCellValue(this.getRealcontent(rpv.getTile_flw(), view, this.nums+"",""));
				}  
				 /*private String head_flw;//左边内容
			    private String head_fmw;//中间内容
			    private String head_frw;//右边内容*/	
				if(rpv.getTile_fmw()!=null&&rpv.getTile_fmw().trim().length()>0)
				{
					HSSFCell cell = row.createCell((short)(listSize/2));
					cell.setCellStyle(titleStyle);
					cell.setCellValue(this.getRealcontent(rpv.getTile_fmw(), view, this.nums+"",""));
				}
				if(rpv.getTile_frw()!=null&&rpv.getTile_frw().trim().length()>0)
				{
					HSSFCell cell = row.createCell((short)(listSize));
					cell.setCellStyle(titleStyle);
					cell.setCellValue(this.getRealcontent(rpv.getTile_frw(), view, this.nums+"",""));
				}
				rowN++;
		 }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return rowN;
	}
	public short executeTitle(String rsid,String rsdtlid,ArrayList tableHeadList,String isShowSeria,String isSign,UserView view,short rowNum,HSSFSheet sheet,HSSFRow row,HSSFWorkbook wb,String isShowHead)
	{
		RowSet rs = null;
		short returnValue=rowNum;
		try
		{
			 ReportPageOptionsBo rpob = new ReportPageOptionsBo(this.conn,view,rsid,rsdtlid);
			 rpob.init();
			 ReportParseVo rpv = rpob.analyse(2);
			 ContentDAO dao = new ContentDAO(this.conn);
			 ExecuteExcel ee = new ExecuteExcel(this.conn);
			 int listSize = tableHeadList.size();
			 if("9".equals(rsid)){
				 listSize++;
			 }
			 if(isShowSeria==null|| "1".equals(isShowSeria))
			 {
				 if("7".equals(rsid))
					 listSize++;
			 }
			 else
			 {
				 listSize--;
				 if("11".equals(rsid))
				 {
					 listSize--;
				 }
			 }
			 if(isSign==null|| "1".equals(isSign))
			 {
				 
			 }
			 else
			 {
				 listSize--;
			 }
			 if("10".equals(rsid))
			 {
				 listSize=3+(tableHeadList.size()-2)*4;
			 }
			 HSSFCellStyle titleStyle = ee.style(wb, 5);
	    	 String t_fontfamilyname=ResourceFactory.getProperty("gz.gz_acounting.m.font");
		     String t_fontEffect="0";
		     int    t_fontSize=11;  
		     String t_color="#000000";       
		     String  t_underLine="#fu[0]";
		     String  t_strikethru="#fs[0]";
		     if(rpv.getTitle_fn().length()>0)
	        		t_fontfamilyname=rpv.getTitle_fn();
		     t_fontEffect=getFontEffect(rpv,4);
		     if(rpv.getTitle_fz().length()>0)
		       	t_fontSize=Integer.parseInt(rpv.getTitle_fz());
		    if(rpv.getTitle_fc().length()>0)
		       	t_color=rpv.getTitle_fc();	       	 
		    if(rpv.getTitle_fu()!=null&&rpv.getTitle_fu().length()>0)
		        t_underLine=rpv.getTitle_fu();
		    if(rpv.getTitle_fs()!=null&&rpv.getTitle_fs().length()>0)
		       		t_strikethru=rpv.getTitle_fs();
		    HSSFFont font = wb.createFont();	
		    font.setFontName(t_fontfamilyname);//设置字体种类
			if("2".equals(t_fontEffect)){
				font.setBold(true); //设置字体
			}else if("4".equals(t_fontEffect)){
				font.setItalic( true ); // 是否使用斜体
			}else if("3".equals(t_fontEffect)){
				font.setBold(true); //设置字体
				font.setItalic( true ); // 是否使用斜体
			}
			if("#fu[1]".equals(t_underLine)){
				font.setUnderline(HSSFFont.U_SINGLE);
			}
			if("#fs[1]".equals(t_strikethru)){
				font.setStrikeout(true);
			}
			font.setFontHeightInPoints((short)t_fontSize);//设置字体大小
			titleStyle.setAlignment(HorizontalAlignment.CENTER );
			titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			titleStyle.setWrapText(true);
			//titleStyle.setFillForegroundColor(HSSFColor.LIME.index);
			//titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			String color = rpv.getTitle_fc();
			if(color!=null&&color.startsWith("#"))
			   color = color.substring(1);
			if(color.trim().length()>0)
			{
				font.setColor(HSSFColor.GREY_25_PERCENT.index);
			}
		    if(color.trim().length()>0)
		    {
		    	 String r_str = color.substring(0, 2);
				 String g_str = color.substring(2, 4);
				 String b_str = color.substring(4, 6);
				 short r = (short)Integer.parseInt(r_str,16);
				 short g = (short)Integer.parseInt(g_str, 16);
				 short b = (short)Integer.parseInt(b_str, 16);
				 HSSFPalette palette = wb.getCustomPalette();
				 palette.setColorAtIndex(HSSFColor.GREY_25_PERCENT.index, (byte)r, (byte)g, (byte)b);
		    }
		    titleStyle.setFont(font);
		    String sql = "select rsdtlname from REPORTDETAIL where rsid="+rsid;
	    	 if(!"4".equals(rsid))
	    		 sql+=" and rsdtlid="+rsdtlid;
		     if("4".equals(rsid))
		    	 sql = " select rsname from reportstyle where rsid=4";
	    	 rs=dao.search(sql);
	    	 String name="";
	    	 while(rs.next())
	    	 {
	    		 name=rs.getString(1);
   		     }
			 if(rpv.getTitle_fw()==null|| "".equals(rpv.getTitle_fw().trim()))
			 {
		     	
		      	 row= sheet.createRow(returnValue);
		    	 HSSFCell cell = row.createCell(0);
		    	 cell.setCellStyle(titleStyle);
			     cell.setCellValue(name);
	     		 ExportExcelUtil.mergeCell(sheet, 0, (short)0, 0, (short)(listSize));
	    		 for(int i=1;i<=listSize;i++)
		    	 {
		    		 cell = row.createCell(i);
		    		 cell.setCellStyle(titleStyle);
	    		 }
			 }
			 else
			 {
				 row= sheet.createRow(returnValue);
		    	 HSSFCell cell = row.createCell(0);
		    	
		    	cell.setCellStyle(titleStyle);
			    cell.setCellValue(this.getRealcontent(rpv.getTitle_fw(), view, this.nums+"",name));
			   
	     	    ExportExcelUtil.mergeCell(sheet, 0, (short)0, 0, (short)(listSize));
	    	    for(int i=1;i<listSize;i++)
		        {
		    	    cell = row.createCell(i);
		    	    cell.setCellStyle(titleStyle);
	         	}
			}
			 row = sheet.getRow(returnValue);
			 if(row==null)
				 row = sheet.createRow(returnValue);
			 row.setHeight((short)1000);
			 if((rpv.getHead_flw()!=null&&rpv.getHead_flw().trim().length()>0)||(rpv.getHead_fmw()!=null&&rpv.getHead_fmw().trim().length()>0)||(rpv.getHead_frw()!=null&&rpv.getHead_frw().trim().length()>0))
			 {
				 returnValue++;
				 row = sheet.getRow(returnValue);
				 if(row==null)
					 row = sheet.createRow(returnValue);
				 row.setHeight((short)500);
				 HSSFCellStyle atitleStyle = ee.style(wb, 5);
		    	 String at_fontfamilyname=ResourceFactory.getProperty("gz.gz_acounting.m.font");
			     String at_fontEffect="0";
			     int    at_fontSize=11;  
			     String  at_underLine="#fu[0]";
			     String  at_strikethru="#fs[0]";
			     if(rpv.getHead_fn().length()>0)
		        		at_fontfamilyname=rpv.getHead_fn();
			     at_fontEffect=getFontEffect(rpv,4);
			     if(rpv.getHead_fz().length()>0)
			       	at_fontSize=Integer.parseInt(rpv.getHead_fz());      	 
			    if(rpv.getHead_fu()!=null&&rpv.getHead_fu().length()>0)
			        at_underLine=rpv.getHead_fu();
			    if(rpv.getHead_fs()!=null&&rpv.getHead_fs().length()>0)
			       		at_strikethru=rpv.getHead_fs();
		    	 
			    HSSFFont afont = wb.createFont();	
			    afont.setFontName(at_fontfamilyname);//设置字体种类
				if("2".equals(at_fontEffect)){
					afont.setBold(true); //设置字体
				}else if("4".equals(at_fontEffect)){
					afont.setItalic( true ); // 是否使用斜体
				}else if("3".equals(at_fontEffect)){
					afont.setBold(true); //设置字体
					afont.setItalic( true ); // 是否使用斜体
				}
				if("#fu[1]".equals(at_underLine)){
					afont.setUnderline(HSSFFont.U_SINGLE);
				}
				if("#fs[1]".equals(at_strikethru)){
					afont.setStrikeout(true);
				}
				afont.setFontHeightInPoints((short)at_fontSize);//设置字体大小
				atitleStyle.setAlignment(HorizontalAlignment.CENTER );
				atitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
				atitleStyle.setWrapText(true);
				atitleStyle.setFont(afont);
				if(rpv.getHead_fc().length()>0)
				{
					String acolor = rpv.getHead_fc();
					if(acolor!=null&&acolor.startsWith("#"))
					   acolor = acolor.substring(1);
					int rgbC=0;
					if(acolor.trim().length()>0)
					{
						afont.setColor(HSSFColor.GREY_40_PERCENT.index);
					}
				    if(acolor.trim().length()>0)
				    {
				    	 String r_str = acolor.substring(0, 2);
						 String g_str = acolor.substring(2, 4);
						 String b_str = acolor.substring(4, 6);
						 short r = (short)Integer.parseInt(r_str,16);
						 short g = (short)Integer.parseInt(g_str, 16);
						 short b = (short)Integer.parseInt(b_str, 16);
						 HSSFPalette palette = wb.getCustomPalette();
						 palette.setColorAtIndex(HSSFColor.GREY_40_PERCENT.index, (byte)r, (byte)g, (byte)b);
				    } 
				}
				if(rpv.getHead_flw()!=null&&rpv.getHead_flw().trim().length()>0)
				{
					
					HSSFCell cell = row.createCell(0);
					cell.setCellStyle(atitleStyle);
					cell.setCellValue(this.getRealcontent(rpv.getHead_flw(), view, this.nums+"",""));
				}  
				if(rpv.getHead_fmw()!=null&&rpv.getHead_fmw().trim().length()>0)
				{
					HSSFCell cell = row.createCell((short)(listSize/2));
					cell.setCellStyle(atitleStyle);
					cell.setCellValue(this.getRealcontent(rpv.getHead_fmw(), view, this.nums+"",""));
				}
				if(rpv.getHead_frw()!=null&&rpv.getHead_frw().trim().length()>0)
				{
					HSSFCell cell = row.createCell((short)(listSize));
					cell.setCellStyle(atitleStyle);
					cell.setCellValue(this.getRealcontent(rpv.getHead_frw(), view, this.nums+"",""));
				}
		 }
			 returnValue++;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(rs!=null)
			{
				try
				{
					rs.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return returnValue;
	}
	
//	取得字形
	// 1：页头  2页尾 3：正文字体 4:标题 5:表头
	public String getFontEffect(ReportParseVo rpv,int flag)
	{
		String fontEffect="0";
		if(rpv!=null)
		{   if(flag==3)
			{
				if("#fi[1]".equals(rpv.getBody_fi())&& "#fb[1]".equals(rpv.getBody_fb()))
					fontEffect="4";  //粗斜体
				else if("#fb[1]".equals(rpv.getBody_fb()))
					fontEffect="2";  //粗体
				else if("#fi[1]".equals(rpv.getBody_fi()))
					fontEffect="3";  //斜体
			}
			else if(flag==4)
			{
				if("#fi[1]".equals(rpv.getTitle_fi())&& "#fb[1]".equals(rpv.getTitle_fb()))
					fontEffect="4";  //粗斜体
				else if("#fb[1]".equals(rpv.getTitle_fi()))
					fontEffect="2";  //粗体
				else if("#fi[1]".equals(rpv.getTitle_fb()))
					fontEffect="3";  //斜体
			}
			else if(flag==1)
			{
				if("#fi[1]".equals(rpv.getHead_fi())&& "#fb[1]".equals(rpv.getHead_fb()))
					fontEffect="4";  //粗斜体
				else if("#fb[1]".equals(rpv.getHead_fb()))
					fontEffect="2";  //粗体
				else if("#fi[1]".equals(rpv.getHead_fi()))
					fontEffect="3";  //斜体
			}
			else if(flag==2)
			{
				if("#fi[1]".equals(rpv.getTile_fi())&& "#fb[1]".equals(rpv.getTile_fb()))
					fontEffect="4";  //粗斜体
				else if("#fb[1]".equals(rpv.getTile_fb()))
					fontEffect="2";  //粗体
				else if("#fi[1]".equals(rpv.getTile_fi()))
					fontEffect="3";  //斜体
			}else if(flag==5)
			{
				if("#fi[1]".equals(rpv.getThead_fi())&& "#fb[1]".equals(rpv.getThead_fb()))
					fontEffect="4";  //粗斜体
				else if("#fb[1]".equals(rpv.getThead_fb()))
					fontEffect="2";  //粗体
				else if("#fi[1]".equals(rpv.getThead_fi()))
					fontEffect="3";  //斜体
			}
			
		
		
		
		
		}
		return fontEffect;
	}
	
	
	public void exportGzData(String fileName,ArrayList dataList,ArrayList salaryItemList)
	{
		String url=System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")
		+fileName;
		try
		{
			 this.wb = new HSSFWorkbook();
			 
		//	 this.wb.setSheetName(0,"党组织情况统计表",HSSFWorkbook.ENCODING_UTF_16);
			 this.style = getStyle("c",wb);
			 this.style_l = getStyle("l",wb);
			 this.style_r = getStyle("r",wb);
			 this.style_title=getStyle("title",wb);
			 this.style_r = getStyle("r",wb);
			 int page=1;
			 int nrows =20000;
			 Hashtable dataBean=null;
			 LazyDynaBean bean=null;
			 HashMap totalData=new HashMap();
			 HashMap styleMap = new HashMap();
			 HSSFDataFormat df = wb.createDataFormat();
			 for(int i=0;i<dataList.size();i++)
			 {
				 if(i==0||(i!=1&&i%nrows==1))
				 {
					 this.sheet = wb.createSheet(page+"");
					 page++;
					 if(i==0)
					 {
						 this.rowNum=1;
					 }else
					 {
						 this.rowNum=0;
					 }
					 for(int index=0;index<salaryItemList.size();index++)
					 {
						 LazyDynaBean headbean=(LazyDynaBean)salaryItemList.get(index);
						 String itemdesc=(String)headbean.get("itemdesc");
						 executeCell2(Short.parseShort(String.valueOf(index)),itemdesc,"title");
					 }
					 this.rowNum++;
				 }
				 dataBean=(Hashtable)dataList.get(i);
				 for(int j=0;j<salaryItemList.size();j++)
				 {
					 bean=(LazyDynaBean)salaryItemList.get(j);
					 String itemid=(String)bean.get("itemid");
					 String type=(String)bean.get("itemtype");
					 if("N".equals(type))
					 {
						 String deciwidth=(String)bean.get("decwidth");
						 if(deciwidth==null|| "".equals(deciwidth.trim()))
							 deciwidth="0";
						 HSSFCellStyle style=null;
						 if(styleMap.get(deciwidth)!=null){
							 style=(HSSFCellStyle)styleMap.get(deciwidth);
						 }else{
							 
							 int scale=Integer.parseInt(deciwidth);
							 StringBuffer buf = new StringBuffer();
							 for(int k=0;k<scale;k++)
							 {
								buf.append("0");
							 }
							 style= getStyle("r",wb);
							 String format="";
							 if(scale>0){
								 format="0."+buf.toString()+"_ ";
								 style.setDataFormat(df.getFormat(format));	
							 }
							 
							 
							 styleMap.put(deciwidth,style);
						 }
						 executeCellN(Short.parseShort(String.valueOf(j)),(String)dataBean.get(itemid),style,Integer.parseInt(deciwidth));
						 String data = (String)dataBean.get(itemid);
						 data= "".equals(data)?"0.0":data;
						 double d = new Double(data).doubleValue();
						 if(totalData.get(j+"")!=null)
						 {
							 Double temp = (Double)totalData.get(j+"");
							 d = d+temp.doubleValue();
						 }
						 totalData.put(j+"", new Double(d));
					 }
					 else
					 {
						 executeCell2(Short.parseShort(String.valueOf(j)),(String)dataBean.get(itemid),"L","A");
					 }
				}
				 this.rowNum++;
			 }
			 this.sheet=this.wb.getSheetAt(0);
			 this.rowNum=0;//写总计行 只针对数值型指标 发放次数和归属次数除外
			 for(int i=0;i<salaryItemList.size();i++)
			 {
				 bean=(LazyDynaBean)salaryItemList.get(i);
				 String itemdesc=(String)bean.get("itemdesc");
				 String itemid=(String)bean.get("itemid");
				 String type=(String)bean.get("itemtype");
				 String value="";
				 if(!"A00Z3".equalsIgnoreCase(itemid)&&!"A00Z1".equalsIgnoreCase(itemid)&&i==0)
				 {
					 executeCell2(Short.parseShort(String.valueOf(i)),"总计","C");
					 continue;
				 }
				 if("N".equals(type))
				 {
					 Double temp = (Double)totalData.get(i+"");
					 value = temp.toString();
				 }
				 if("A00Z3".equalsIgnoreCase(itemid) || "A00Z1".equalsIgnoreCase(itemid)){
					 executeCell2(Short.parseShort(String.valueOf(i)),"","C");
				 }else{
					 if("N".equalsIgnoreCase(type)){
			    		 String deciwidth=(String)bean.get("decwidth");
				     	 if(deciwidth==null|| "".equals(deciwidth.trim()))
				    		 deciwidth="0";
				    	 HSSFCellStyle style=null;
				    	 if(styleMap.get(deciwidth)!=null){
							 style=(HSSFCellStyle)styleMap.get(deciwidth);
						 }else{
							 
							 int scale=Integer.parseInt(deciwidth);
							 StringBuffer buf = new StringBuffer();
							 for(int k=0;k<scale;k++)
							 {
								buf.append("0");
							 }
							 String format="0."+buf.toString()+"_ ";
							 style= getStyle("r",wb);
							 style.setDataFormat(df.getFormat(format));	
							 styleMap.put(deciwidth,style);
						 }
						 executeCellN(Short.parseShort(String.valueOf(i)),value,style,Integer.parseInt(deciwidth));
					 }else{
						 executeCell2(Short.parseShort(String.valueOf(i)),value,"R","N");
					 }
				 }
			 }			 
			 
			 FileOutputStream fileOut = new FileOutputStream(url);
			 this.wb.write(fileOut);
			 fileOut.close();	 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 取得多张工资标准excel
	 * @return
	 */
	public String getMultipleGzStandardExcel(String pkg_id,ArrayList standardids,String username,String validIDs)
	{
//		String fileName="gzStandardExcel.xls";
		String fileName=username+"_"+PubFunc.getStrg()+".xls";
		String url=System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")
		+fileName;
		try
		{
			 this.wb = new HSSFWorkbook();
			 StringBuffer ids=new StringBuffer("");
			 for(int i=0;i<standardids.size();i++)
			 {
				 String id=(String)standardids.get(i);
				 if(validIDs.indexOf(","+id+",")!=-1)
				 {
					 ids.append(","+(String)standardids.get(i));
				 }
			 }
			 
			 ContentDAO dao=new ContentDAO(this.conn);
			 RowSet roeSet=dao.search("select * from gz_stand_history where id in ("+ids.substring(1)+") and pkg_id="+pkg_id);
			 int i=1;
			 HashMap map = new HashMap();
			 while(roeSet.next())
			 {
				String   id=roeSet.getString("id");
				String	 hfactor=roeSet.getString("hfactor");
				String	 s_hfactor=roeSet.getString("s_hfactor");
				String	 vfactor=roeSet.getString("vfactor");
				String	 s_vfactor=roeSet.getString("s_vfactor");
				String	 item=roeSet.getString("item");
				String	 hcontent=roeSet.getString("hcontent");
				String	 vcontent=roeSet.getString("vcontent");
				String	 gzStandardName=roeSet.getString("name");
			 
				GzStandardItemBo bo=new GzStandardItemBo(this.conn,hfactor,s_hfactor,hcontent,vfactor,s_vfactor,vcontent,item,id,"edit",pkg_id);
				bo.init();
				GzStandardItemVo vo=bo.getGzStandardItemVo();
				this.rowNum=0;
				if(map.get(gzStandardName.toUpperCase())==null){
		    		executeSingleGzStandardSheet(vo,gzStandardName,i,false);
		    		map.put(gzStandardName.toUpperCase(), "1");
				}else{
					int No = Integer.parseInt((String)map.get(gzStandardName.toUpperCase()));
					executeSingleGzStandardSheet(vo,gzStandardName+No,i,false);
					No++;
					map.put(gzStandardName.toUpperCase(), No+"");
				}
				i++;
			 }
		
			
			 FileOutputStream fileOut = new FileOutputStream(url);
			 this.wb.write(fileOut);
			 fileOut.close();	 
			 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return fileName;
	}
	
	
	
	
	/**
	 * 取得单张工资标准excel
	 * @param vo
	 * @param sheetName
	 * @return
	 */
	public String getSingleGzStandardExcel(GzStandardItemVo vo,String sheetName,int i,String username)throws GeneralException 
	{
//		 String fileName="gzStandardExcel.xls";
		 String fileName=username+"_"+PubFunc.getStrg()+".xls";
		 String url=System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")
			+fileName;
		 try
		 {
				 this.wb = new HSSFWorkbook();
				 executeSingleGzStandardSheet(vo,sheetName,i,true);
				 FileOutputStream fileOut = new FileOutputStream(url);
				 this.wb.write(fileOut);
				 fileOut.close();	 
		 }
		 catch(Exception e)
		 {
				e.printStackTrace();
				 throw GeneralExceptionHandler.Handle(e);
		 }
		 return fileName;
	}
	
	
	/**
	 * 
	 * @param vo
	 * @param sheetName
	 * @param num
	 * @param type true为单张导出 false为多张导出
	 * @throws GeneralException
	 */
	public void  executeSingleGzStandardSheet(GzStandardItemVo vo,String sheetName,int num,Boolean type)throws GeneralException 
	{
		try
		{
			 this.sheet = wb.createSheet();

			 if(sheetName.indexOf("/")!=-1){
				 sheetName = sheetName.replaceAll("/", "");
			 }
			 this.wb.setSheetName(num-1,sheetName);
			 
			// this.sheet.setColumnWidth((short)1,(short)5000);
             this.style = getStyle("c",wb);
			 this.style_l=getStyle("l",wb);
			 
		 
			int h_bottomColumn_num=vo.getH_bottomColumn_num();
			String resultItemType=vo.getResultItemType();   // N ; C
			String codesetid=vo.getCodesetid();
            ArrayList h_List=vo.getH_List();
			ArrayList v_List=vo.getV_List();
			boolean is_h2=getIsSubItem(h_List);
			boolean is_v2=getIsSubItem(v_List);
			if(h_List.size()==0)   //如果没有设置横向指标
				h_bottomColumn_num=1;
			
			executeCell2((short)1, sheetName);
			this.rowNum++;
			executeTitle(is_h2,is_v2,h_List,vo,type);
			
			String [] codeSetItemDesc=null;
			//如果是代码类 且为单表导出，则在excel结果数据中填充下拉列表
			if(!StringUtils.isBlank(codesetid)&&type){
				
				
				ArrayList<CodeItem> codeItems=AdminCode.getCodeItemList(codesetid);
				if(codeItems!=null){
					
					codeSetItemDesc=new String[codeItems.size()];//此处单位 部门 需要特殊处理，待修改。
					for(int i=0;i<codeItems.size();i++){
						CodeItem code=codeItems.get(i);
						codeSetItemDesc[i]=code.getCodename();
					}
					int rSnum=0,cSnum=0,rFnum=3,cFnum=2;//c列 r行 s结束 f开始

					if(is_h2)
						rFnum++;
					if(is_v2)
						cFnum++;
					rSnum=rFnum+(vo.getV_bottomColumn_num()==0?1:vo.getV_bottomColumn_num())-1;

					
					cSnum=cFnum+h_bottomColumn_num-1;
					CellRangeAddressList regions = new CellRangeAddressList(rFnum, rSnum, cFnum, cSnum);// 起始行 终止行 起始列 终止列
					DVConstraint constraint = DVConstraint.createExplicitListConstraint(codeSetItemDesc);
					HSSFDataValidation data_validation_list = new HSSFDataValidation(regions, constraint); 
					this.sheet.addValidationData(data_validation_list);
				}
			}
			
			//写表体		
			int index = 0;
			if (v_List.size() == 0) {
				String itemid = vo.getItem();
				FieldItem item = DataDictionary.getFieldItem(itemid);
				String itemdesc = "";
				if (item != null) {
					itemdesc = item.getItemdesc();
				}
				
				executeCell(rowNum, (short) 1, rowNum, (short) 1, itemdesc, "C");

				for (int j = 0; j < h_bottomColumn_num; j++) {

					if ("N".equals(resultItemType)) {
						String value = (String) ((LazyDynaBean) vo
								.getGzItemList().get(index)).get("value");
						executeCell(rowNum, Short.parseShort(String
								.valueOf(j + 2)), rowNum, Short
								.parseShort(String.valueOf(j + 2)), value, "C");
					} else if ("C".equals(resultItemType)) {
						String viewvalue = (String) ((LazyDynaBean) vo
								.getGzItemList().get(index)).get("viewvalue");
						executeCell(rowNum, Short.parseShort(String
								.valueOf(j + 2)), rowNum, Short
								.parseShort(String.valueOf(j + 2)), viewvalue,
								"C");

					}
					index++;
				}
				this.rowNum++;

			} else {
				for (int i = 0; i < v_List.size(); i++) {

					LazyDynaBean v_abean = (LazyDynaBean) v_List.get(i);
					String name = (String) v_abean.get("name").toString();
					String childNum = (String) v_abean.get("childNum");
					String id=(String) v_abean.get("id").toString();
					if (!is_v2 || ("0".equals(childNum) && is_v2)) {
						int columnIndex = 0;
						if (is_v2 && "0".equals(childNum)) {
							executeCell(rowNum, (short) 1, rowNum, (short) 2,
									name, "C",id,type);
							columnIndex++;
						} else {
							executeCell(rowNum, (short) 1, rowNum, (short) 1,
									name, "C",id,type);
						}
						for (int j = 0; j < h_bottomColumn_num; j++) {

							if ("N".equals(resultItemType)) {
								String value = (String) ((LazyDynaBean) vo
										.getGzItemList().get(index))
										.get("value");
								executeCell(rowNum, Short.parseShort(String
										.valueOf(j + 2 + columnIndex)), rowNum,
										Short.parseShort(String.valueOf(j + 2
												+ columnIndex)), value, "C");
							} else if ("C".equals(resultItemType)) {

								String viewvalue = (String) ((LazyDynaBean) vo
										.getGzItemList().get(index))
										.get("viewvalue");
								executeCell(rowNum, Short.parseShort(String
										.valueOf(j + 2 + columnIndex)), rowNum,
										Short.parseShort(String.valueOf(j + 2
												+ columnIndex)), viewvalue, "C");

							}
							index++;
						}
						this.rowNum++;
					} else {
						
						
						ArrayList s_factor_list = (ArrayList) v_abean
								.get("s_factor_list");
						short rowNum2=Short.parseShort(String
								.valueOf(rowNum + s_factor_list.size()-1));
						executeCell(rowNum, (short) 1,rowNum2,
								(short) 1, name, "C",id,type);
						for (int j = 0; j < s_factor_list.size(); j++) {
							LazyDynaBean s_v_abean = (LazyDynaBean) s_factor_list
									.get(j);
							String a_name = (String) s_v_abean.get("name")
									.toString();
							String a_id = (String) s_v_abean.get("id")
									.toString();

							if (j != 0) {
								rowNum++;
							}
							executeCell(rowNum, (short) 2, rowNum, (short) 2,
									a_name, "C",a_id,type);
							for (int e = 0; e < h_bottomColumn_num; e++) {

								if ("N".equals(resultItemType)) {
									String value = (String) ((LazyDynaBean) vo
											.getGzItemList().get(index))
											.get("value");
									executeCell(rowNum, Short.parseShort(String
											.valueOf(e + 3)), rowNum, Short
											.parseShort(String.valueOf(e + 3)),
											value, "C");
								} else if ("C".equals(resultItemType)) {
									String viewvalue = (String) ((LazyDynaBean) vo
											.getGzItemList().get(index))
											.get("viewvalue");
									executeCell(rowNum, Short.parseShort(String
											.valueOf(e + 3)), rowNum, Short
											.parseShort(String.valueOf(e + 3)),
											viewvalue, "C");
								}
								index++;
							}

						} 
						this.rowNum++;
					}
				}

			}
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		 }
		
	}
	
	
	/**
	 * 生成表头
	 * @param is_h2
	 * @param is_v2
	 * @param h_List
	 * @param vo
	 */
	public void executeTitle(boolean is_h2,boolean is_v2,ArrayList h_List,GzStandardItemVo vo ,Boolean type)
	{

		short columnNum = 0;
		if (is_h2 && is_v2) {
			executeCell(rowNum, (short) 1, rowNum + 1, (short) 2, "", "C");
			columnNum = 2;
		} else if (is_v2) {
			executeCell(rowNum, (short) 1, rowNum, (short) 2, "", "C");
			columnNum = 2;
		} else if (is_h2) {
			executeCell(rowNum, (short) 1, rowNum + 1, (short) 1, "", "C");
			columnNum = 1;
		} else {
			executeCell(rowNum, (short) 1, rowNum, (short) 1, "", "C");
			columnNum = 1;
		}
		if (h_List.size() == 0) // 如果没选横栏
		{
			String itemid = vo.getItem();
			String comment=itemid;
			FieldItem item = DataDictionary.getFieldItem(itemid);
			String itemdesc = "";
			
			if (item != null) {
				itemdesc = item.getItemdesc();
			}
			executeCell(rowNum,
					Short.parseShort(String.valueOf(columnNum + 1)), rowNum,
					Short.parseShort(String.valueOf(columnNum + 1)), itemdesc,
					"C",comment,type);
		} else {
			short a_columnNum = columnNum;
			for (int i = 0; i < h_List.size(); i++) {
				LazyDynaBean h_abean = (LazyDynaBean) h_List.get(i);
				String childNum = (String) h_abean.get("childNum");
				String name = (String) h_abean.get("name").toString();
				String id=(String) h_abean.get("id");
				if (is_h2) {
					if ("0".equals(childNum)) {
						executeCell(rowNum, Short.parseShort(String
								.valueOf((i + 1) + a_columnNum)), rowNum + 1,
								Short.parseShort(String.valueOf((i + 1)
										+ a_columnNum)), name, "C",id,type);
					} else {
						executeCell(rowNum, Short.parseShort(String
								.valueOf((i + 1) + a_columnNum)), rowNum, Short
								.parseShort(String.valueOf((i) + a_columnNum
										+ Integer.parseInt(childNum))), name,
								"C",id,type);
						a_columnNum = Short.parseShort(String.valueOf(Integer
								.parseInt(childNum)
								+ a_columnNum - 1));
					}
				} else {
					executeCell(rowNum, Short.parseShort(String.valueOf((i + 1)
							+ a_columnNum)), rowNum, Short.parseShort(String
							.valueOf((i + 1) + a_columnNum)), name, "C",id,type);
				}

			}
		}
		rowNum++;
		if (is_h2) {
			int num = 0;
			for (int i = 0; i < h_List.size(); i++) {
				LazyDynaBean h_abean = (LazyDynaBean) h_List.get(i);
				ArrayList s_factor_list = (ArrayList) h_abean.get("s_factor_list");
				//String id=(String) h_abean.get("id");
				for (int j = 0; j < s_factor_list.size(); j++) {

					LazyDynaBean s_h_abean = (LazyDynaBean) s_factor_list.get(j);
					String name = (String) s_h_abean.get("name").toString();
					String id=(String) s_h_abean.get("id");
					executeCell(rowNum, Short.parseShort(String
							.valueOf((num + 1) + columnNum)), rowNum, Short
							.parseShort(String.valueOf((num + 1) + columnNum)),
							name, "C",id,type);
					num++;
				}
			}
			rowNum++;
		}
	}
	
	
	
	
	public boolean getIsSubItem(ArrayList list)
	{
		boolean flag=false;
		for(int i=0;i<list.size();i++)
		{
			LazyDynaBean abean=(LazyDynaBean)list.get(i);
			String childNum=(String)abean.get("childNum");
			if(!"0".equals(childNum))
			{	flag=true;
				break;
			}
		}
		
		return flag;
	}
	
	
	
	
	public HSSFCellStyle getStyle(String align,HSSFWorkbook wb)
	{
		HSSFCellStyle a_style=wb.createCellStyle();
		a_style.setBorderBottom(BorderStyle.THIN);
		a_style.setBottomBorderColor(HSSFColor.BLACK.index);
		a_style.setBorderLeft(BorderStyle.THIN);
		a_style.setLeftBorderColor(HSSFColor.BLACK.index);
		a_style.setBorderRight(BorderStyle.THIN);
		a_style.setRightBorderColor(HSSFColor.BLACK.index);
		a_style.setBorderTop(BorderStyle.THIN);
		a_style.setTopBorderColor(HSSFColor.BLACK.index);
		a_style.setVerticalAlignment(VerticalAlignment.CENTER);
		
		if("c".equals(align))
			a_style.setAlignment(HorizontalAlignment.CENTER);
		else if("l".equals(align))
			a_style.setAlignment(HorizontalAlignment.LEFT);
		else if("r".equals(align))
			a_style.setAlignment(HorizontalAlignment.RIGHT);
		else if("title".equals(align))
		{
			a_style.setAlignment(HorizontalAlignment.CENTER);
			a_style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			a_style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
		}
		return a_style;
	}
	
	
	
	 public void executeCell2(short columnIndex,String value)
	 {
		 HSSFRow row = this.sheet.createRow( rowNum);
		 HSSFCell cell = row.createCell(columnIndex); 
		 
		 cell.setCellValue(value); 
	 }
	 
	 
	 public void executeCell2(short columnIndex,String value,String style)
	 {
		 HSSFRow row = this.sheet.getRow( rowNum);
		 if(row==null)
			 row = this.sheet.createRow( rowNum);
		 HSSFCell cell = row.getCell(columnIndex); 
		 if(cell==null)
			 cell = row.createCell(columnIndex);
		 if("c".equalsIgnoreCase(style))
			 cell.setCellStyle(this.style); 
		 else if("l".equalsIgnoreCase(style))
			 cell.setCellStyle(this.style_l);
		 else if("R".equalsIgnoreCase(style))
		 {
			 cell.setCellStyle(this.style_r);
		 }
		 else if("title".equalsIgnoreCase(style))
		 {
			 cell.setCellStyle(this.style_title);
		 }
	     cell.setCellValue(value); 
	 }
	 public void executeCell3(short columnIndex,String value,String style,String type,String width)
	 {
		 HSSFRow row = this.sheet.getRow( rowNum);
		 if(row==null)
			 row = this.sheet.createRow( rowNum);
		 
		 HSSFCell cell = row.getCell(columnIndex); 
		 if(cell==null)
		 {
			 cell = row.createCell(columnIndex);	 
		 }
		 sheet.setColumnWidth((int)columnIndex, Integer.parseInt(width)*500);
		 String macth="[-+]?[0-9]+(.[0-9]+)?";//判断是否为数字，前面可以加正负号。
		 if("c".equalsIgnoreCase(style))
			 cell.setCellStyle(this.style); 
		 else if("l".equalsIgnoreCase(style))
			 cell.setCellStyle(this.style_l);
		 else if("R".equalsIgnoreCase(style))
		 {
			 if(value==null||value.trim().length()==0||value.indexOf(".")==-1){
				 this.style_r.setDataFormat(dataformat.getFormat("0"));
		    	 cell.setCellStyle(this.style_r);
			 } else
			 {
				 String str=value.substring(value.indexOf(".")+1);
				 if(str.length()==1)
			    	 cell.setCellStyle(this.style_r_1);
				 else if(str.length()==2)
			    	 cell.setCellStyle(this.style_r_2);
				 if(str.length()==3)
			    	 cell.setCellStyle(this.style_r_3);
				 if(str.length()==4)
			    	 cell.setCellStyle(this.style_r_4);
			 }
		 }
		 else if("T".equalsIgnoreCase(style))
		 {
			 cell.setCellStyle(style_thead);
		 }
		 if(value!=null&&value.indexOf("%")!=-1)
		 {
			 String avalue=value.substring(0,value.length()-1);
			 if(avalue.matches(macth))
			 {
				 cell.setCellStyle(this.style_r);
		    	 cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			     cell.setCellValue(Double.parseDouble(avalue));
			 }
			 else
			 {
				 cell.setCellValue(value); 
			 }
		 }
		 else if(type!=null&& "N".equalsIgnoreCase(type)&&value!=null&&value.trim().length()>0)
		 {
			 if(value==null|| "".equals(value.trim()))
				 value="0.00";
			 if(".0".equals(value)|| ".00".equals(value)|| ".000".equals(value)|| ".0000".equals(value))
					value="0.00";
			 if(value.matches(macth))
			 {
				 cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				//org.apache.poi.hssf.usermodel.HSSFDataFormat;
				//org.apache.poi.hssf.usermodel.HSSFDataFormatter;
				//org.apache.poi.hssf.usermodel.HSSFDataValidation;
				cell.setCellValue(Double.parseDouble(value));
			 }
			 else
			 {
		    	 if(value==null|| "".equals(value.trim()))
		    	      	value="";
		         cell.setCellValue(value); 
			 } 
		 }
		 else
		 {	
			if(type!=null&& "N".equalsIgnoreCase(type)&&(value==null|| "".equals(value.trim())))
				value="0";
			else if(value==null|| "".equals(value.trim()))
	    	      	value="";
	        cell.setCellValue(value); 
		 }
	 }
	 HSSFRichTextString richTextString =null; 
	 HSSFRow row=null;
	 HSSFCell cell=null;
	 public void executeCell2(short columnIndex,String value,String style,String type)
	 {
		 row = this.sheet.getRow(rowNum);
		 if(row==null)
			 row = this.sheet.createRow(rowNum);
		 cell = row.createCell(columnIndex); 
		 if("c".equalsIgnoreCase(style))
			 cell.setCellStyle(this.style); 
		 else if("l".equalsIgnoreCase(style))
			 cell.setCellStyle(this.style_l);
		 else if("R".equalsIgnoreCase(style))
			 cell.setCellStyle(this.style_r);
		 if(value!=null&&value.trim().length()>0&& "N".equalsIgnoreCase(type))
		 {
			 cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			 cell.setCellValue(Double.parseDouble(value));
		 }
		 else
		 {
			 if(value==null)
				 value="";
			 richTextString=new HSSFRichTextString(value);
			 cell.setCellValue(richTextString);
		 }
	 }
	 public void executeCellN(short columnIndex,String value,HSSFCellStyle style,int scale)
	 {
		 row = this.sheet.getRow(rowNum);
		 if(row==null)
			 row = this.sheet.createRow(rowNum);
		 cell = row.createCell(columnIndex); 
		 cell.setCellStyle(style);	
		 if(value==null|| "".equals(value.trim()))
			 value="0";
		 cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
		 BigDecimal bd = new BigDecimal(value);
		 BigDecimal bd2=bd.setScale(scale, bd.ROUND_HALF_UP);
		 cell.setCellValue(bd2.doubleValue());	 
	 }
	 public void executeCell(int a,short b,int c,short d,String content,String style,short num)
	 {
		 try {
			 HSSFRow row = sheet.getRow(a);
			 if(row==null)
				 row = sheet.createRow(a);
			 HSSFCell cell = row.getCell(b);
			 if(cell==null)
				 cell = row.createCell(b);
			 if("h".equals(style))
				 row.setHeight((short)num);	
			 else if("no_c".equalsIgnoreCase(style))
			 {
				 HSSFCellStyle a_style=wb.createCellStyle();
				 a_style.setAlignment(HorizontalAlignment.CENTER);
				 a_style.setVerticalAlignment(VerticalAlignment.JUSTIFY);
				 row.setHeight((short)num);	
				 cell.setCellStyle(a_style);
			 }
			 else if("no_l".equalsIgnoreCase(style))
			 {
				 HSSFCellStyle a_style=wb.createCellStyle();
				 a_style.setAlignment(HorizontalAlignment.LEFT);
				 a_style.setVerticalAlignment(VerticalAlignment.JUSTIFY);
				 row.setHeight((short)num);	
				 cell.setCellStyle(a_style);
			 }
			 else
				 cell.setCellStyle(this.style_l);
			 cell.setCellValue(content);
			 short b1=b;
			 while(++b1<=d)
			 {
				 cell = row.getCell(b1);
				 if(cell==null)
					 cell = row.createCell(b1);
				 
				 if(!"no_c".equals(style)&&!"no_l".equals(style))
					 cell.setCellStyle(this.style); 
			 }
			 
			 for(int a1=a+1;a1<=c;a1++)
			 {
				 row = sheet.getRow(a1);
				 if(row==null)
					 row = sheet.createRow(a1);
				 b1=b;
				 while(b1<=d)
				 {
					 cell = row.getCell(b1);
					 if(cell==null)
						 cell = row.createCell(b1);
					 if(!"no_c".equals(style)&&!"no_l".equals(style))
						 cell.setCellStyle(this.style);
					 b1++;
				 }
			 }
			 
			 ExportExcelUtil.mergeCell(sheet, a,b,c,d);
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
	 }
	 
	 public void executeCell(int a,short b,int c,short d,String content,String style)
	 {
		 try {
			 HSSFRow row = sheet.getRow(a);	
			 if(row==null)
				 row = sheet.createRow(a);
			 
			 row.setHeight((short)400);
			 HSSFCell cell = row.getCell(b);
			 if(cell==null)
				 cell = row.createCell(b);
			 
			 if("c".equalsIgnoreCase(style))
				 cell.setCellStyle(this.style); 
			 else if("l".equalsIgnoreCase(style))
				 cell.setCellStyle(this.style_l);
			 else if("R".equalsIgnoreCase(style))
				 cell.setCellStyle(this.style_r);
			 cell.setCellValue(content);
			 
			 short b1=b;
			 while(++b1<=d)
			 {
				 cell = row.getCell(b1);
				 if(cell==null)
					 cell = row.createCell(b1);
				 
				 cell.setCellStyle(this.style); 
			 }
			 
			 for(int a1=a+1;a1<=c;a1++)
			 {
				 row = sheet.createRow(a1);
				 if(row==null)
					 row = sheet.createRow(a1);
				 b1=b;
				 while(b1<=d)
				 {
					 cell = row.getCell(b1);
					 if(cell==null)
						 cell = row.createCell(b1);
					 
					 cell.setCellStyle(this.style);
					 b1++;
				 }
			 }
			 
			 ExportExcelUtil.mergeCell(sheet, a,b,c,d);
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
	 }
	 
	 public void executeCell(int a,short b,int c,short d,String content,String style,String comment,Boolean type)
	 {
		 try {
			 HSSFRow row = sheet.getRow(a);	
			 if(patr==null)
				 patr = sheet.createDrawingPatriarch();
			 if(row==null)
				 row = sheet.createRow(a);
			 
			 row.setHeight((short)400);
			 HSSFCell cell = row.getCell(b);
			 if(cell==null)
				 cell = row.createCell(b);
			 
			 if("c".equalsIgnoreCase(style))
				 cell.setCellStyle(this.style); 
			 else if("l".equalsIgnoreCase(style))
				 cell.setCellStyle(this.style_l);
			 else if("R".equalsIgnoreCase(style))
				 cell.setCellStyle(this.style_r);
			 
			 if(StringUtils.isNotBlank(comment)&&type){//当注释不为空时
				 HSSFComment comm = patr.createCellComment(new HSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 5,3));
				 comm.setString(new HSSFRichTextString(comment));
				 cell.setCellComment(comm);
			 }
			 
			 cell.setCellValue(content);
			 
			 
			 short b1=b;
			 while(++b1<=d)
			 {
				 cell = row.getCell(b1);
				 if(cell==null)
					 cell = row.createCell(b1);
				 
				 cell.setCellStyle(this.style); 
			 }
			 
			 for(int a1=a+1;a1<=c;a1++)
			 {
				 row = sheet.createRow(a1);
				 if(row==null)
					 row = sheet.createRow(a1);
				 b1=b;
				 while(b1<=d)
				 {
					 cell = row.getCell(b1);
					 if(cell==null)
						 cell = row.createCell(b1);
					 
					 cell.setCellStyle(this.style);
					 b1++;
				 }
			 }
			 
			 ExportExcelUtil.mergeCell(sheet, a,b,c,d);
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
	 }
	 
	 
	 /**
	  * 初始化 导入文件信息
	  * @param inputStream
	  */
	 public void getSelfAttribute(InputStream inputStream)
	 {
		 try
		 {
//			 this.wb= new HSSFWorkbook(inputStream);
//			 this.sheet=this.wb.getSheetAt(0);	
			 
			 this.wb2= WorkbookFactory.create(inputStream);
			 this.sheet2=this.wb2.getSheetAt(0);			 
		 }
		 catch (Exception e) {
				System.out.println(e);
		}
	 }
	 
	 /**
	  * 取得 导入excel表里 rowNum行所有的信息
	  * @param rowNum
	  * @return
	  */
	public ArrayList getRowAllInfo(int rowNum)throws GeneralException
	{
		ArrayList list=new ArrayList();
//		int rows = this.sheet.getPhysicalNumberOfRows();
		int rows = this.sheet2.getPhysicalNumberOfRows();
		if(rowNum>rows)
			return list;
//		HSSFRow row = sheet.getRow(rowNum);
		Row row = sheet2.getRow(rowNum);
		if (row != null) 
		{
			int cells = row.getPhysicalNumberOfCells();
			for (short c = 0; c < cells; c++) 
			{
				String value = "";
				Cell cell = row.getCell(c);
				if (cell != null) 
				{
					switch (cell.getCellType()) 
					{
						case Cell.CELL_TYPE_FORMULA:
							//   
							break;
						case Cell.CELL_TYPE_NUMERIC:
							value= String.valueOf((long) cell.getNumericCellValue());
							break;
						case Cell.CELL_TYPE_STRING:
							value= ToDBC(cell.getStringCellValue());
							break;
						default:
							value= "";
					}
				}
				if(c==0)
				{
					if("".equals(value))
						throw GeneralExceptionHandler.Handle(new Exception("文件格式不正确，第一行为导入的文件列名，不能为空"));
				}
				if("".equals(value))
					continue;
//					throw GeneralExceptionHandler.Handle(new Exception("文件格式不正确，第一行为导入的文件列名，不能为空"));
				list.add(new CommonData(value.toUpperCase(),value.toUpperCase()));
			}
		}
		return list;
	}
	//移除小数点后面的零 是零就不显示了
	  public String moveZero(String number)
	    {

		DecimalFormat df = new DecimalFormat("###############.##########");
		if (number == null || number.length() == 0)
		    return "";
		if (Float.parseFloat(number) == 0)
		    return "0";
		return df.format(Double.parseDouble(number));
	    }
	/**
	 * 取得 指定的某行至某行的数据
	 * @param fromRow
	 * @param toRow
	 * @param columnDataList
	 * @return
	 */
	  public ArrayList getDefineData(int fromRow ,int toRow,ArrayList columnDataList)
		{
			ArrayList list=new ArrayList();
			try
			{
				LazyDynaBean abean=null;
				CommonData data=null;
				int rowBlankNum = 0;
				SimpleDateFormat dateformat=new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
				for (int r = fromRow; r <= toRow; r++) {
//					HSSFRow row = sheet.getRow(r);
					Row row = sheet2.getRow(r);
					if (!rowIsBlank(row)) {
						abean = new LazyDynaBean();
						for (short c = 0; c < columnDataList.size(); c++) {
							data = (CommonData) columnDataList.get(c);
							String columnName = data.getDataValue().trim();
						
							String value = "";
							Cell cell = row.getCell(c);
							if (cell != null) {
								switch (cell.getCellType()) {
								case Cell.CELL_TYPE_FORMULA:
									//   
									break;
								case Cell.CELL_TYPE_NUMERIC:	
									if (HSSFDateUtil.isCellDateFormatted(cell)) { //判断是日期类型 统一转换成yyyy-MM-dd格式入库  zhaoxg add 2013-12-4
										
										Date dt = HSSFDateUtil.getJavaDate(cell.getNumericCellValue());//获取成DATE类型 
										value = dateformat.format(dt); 
									}else{
										value = String.valueOf((double) cell
												.getNumericCellValue());
										value = moveZero(value);
									}
									break;
								case Cell.CELL_TYPE_STRING:
									if(isValidDate(cell.getStringCellValue())){//判断是日期类型 统一转换成yyyy-MM-dd格式入库  zhaoxg add 2013-12-4
										value = dateformat.format(dateformat.parse(cell.getStringCellValue())); 
									}else{
										value = cell.getStringCellValue();
									}
									break;
								default:
									value = "";
								}
							}
							abean.set(ToDBC(columnName), value.trim());
						}
						list.add(abean);
					}else
						rowBlankNum++;
					if(rowBlankNum > 30)//如果连续超过10行为空行，则结束导入，lis, 2015-11-30
						break;
				}
			}
			catch (Exception e) {
				System.out.println(e);
			}
			return list;
		}
	  	/**
	  	 * 全角字符转半角
	  	 * @param input
	  	 * @return 半角字符
	  	 */
	  	private static String ToDBC(String input) {
			char c[] = input.toCharArray();
			for (int i = 0; i < c.length; i++) {
				if (c[i] == '\u3000') {
					c[i] = ' ';
				} else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
					c[i] = (char) (c[i] - 65248);
				}
			}
			String returnString = new String(c);
			return returnString;
	  	}
		/**
		 * @param row
		 * @author lis
		 * @deprecated 当前行是否是空行
		 * @return
		 */
		private boolean rowIsBlank(Row row){
			String rowStr = "";
			try {
				if(row == null)
					return true;
				Iterator<Cell> ite = row.cellIterator();
				Cell cell = null;
				while (ite.hasNext())
				  {
					cell = (Cell) ite.next();
					rowStr += cell.toString();
				  }
			} catch (Exception e) {
				e.printStackTrace();
				GeneralExceptionHandler.Handle(e);
			}
			return StringUtils.isBlank(rowStr);
		}
	/**
	 * 判断字符串是否是日期格式 zhaoxg add 2013-12-4
	 * @param s
	 * @return
	 */
	public static boolean isValidDate(String s)
	{
		try{
			SimpleDateFormat dateformat=new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
			dateformat.parse(s);
			return true;
			}
		catch (Exception e){
			// 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
			return false;
			}
	}
	
	/**
	 * 取得excel 数据的行数
	 * @return
	 */
	public int getTotalDataRows()
	{
		return this.sheet.getPhysicalNumberOfRows()-1;
	}
	 
	public int getTotalDataRows2()
	{
		return this.sheet2.getPhysicalNumberOfRows()-1;
	}
	
	 public static void main(String argv[]) {
		try {
			

		} catch (Exception e) {
			System.out.println(e);
		}
	}   
public String getRealcontent(String content,UserView view,String totalNum,String name)
{
	String str=content;
	RowSet rs = null;
	try
	{
		if(str.indexOf("&[页码]")!=-1)
			str=str.replaceAll("&\\[页码\\]","1");
	    if(str.indexOf("&[制作人]")!=-1  )
			str=str.replaceAll("&\\[制作人\\]",view.getUserFullName());
		if(str.indexOf("&[日期]")!=-1)
		{
			SimpleDateFormat d=new SimpleDateFormat("yyyy-MM-dd");
			str=str.replaceAll("&\\[日期\\]",d.format(new java.util.Date()));
		}
	    if(str.indexOf("&[时间]")!=-1)
		{
			SimpleDateFormat   formatter   =   new   java.text.SimpleDateFormat("hh:mm:ss");   
			str=str.replaceAll("&\\[时间\\]",formatter.format(new java.util.Date()));
		}
		if(str.indexOf("&[总人数]")!=-1)
		{
			str=str.replaceAll("&\\[总人数\\]",String.valueOf(totalNum));
		}
		if(str.indexOf("&[YYYY年YY月]")!=-1)
		{
			SimpleDateFormat d=new SimpleDateFormat("yyyy-MM");
			String ss = d.format(new Date());
			str = str.replaceAll("&\\[YYYY年YY月\\]", ss.substring(0,4)+"年"+ss.substring(5,7)+"月");
		}
		if(str.indexOf("&[年月]")!=-1)
		{
			SimpleDateFormat d=new SimpleDateFormat("yyyy-MM");
			str=str.replaceAll("&\\[年月\\]",d.format(new java.util.Date()));
		}
		if(str.indexOf("&[单位名称]")!=-1)
		{
			ContentDAO dao  = new ContentDAO(this.conn);
			if(view.getA0100()!=null&&view.getA0100().trim().length()>0)
			{
		    	rs =dao.search("select b0110 from "+view.getDbname()+"A01 where a0100='"+view.getA0100()+"'");
		     	while(rs.next())
		     	{
		     		String b0110="";
		     		if(rs.getString("b0110")!=null)
		     			b0110=AdminCode.getCodeName("UN",rs.getString("b0110"));
	    	    	str=str.replaceAll("&\\[单位名称\\]",b0110);
		     	}
			}else{
				str=str.replaceAll("&\\[单位名称\\]","");
			}
		}
		if(str.indexOf("&[报表名称]")!=-1&&name!=null&&name.trim().length()>0)
		{
			str=str.replaceAll("&\\[报表名称\\]",name);
		}
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	finally
	{
		if(rs!=null)
		{
			try
			{
				rs.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	return str;
}
/**
 * 取得需要的数据格式串，如0.0000，如果scale为-1，根据value的小数位取
 * @param value
 * @param scale
 * @return
 */
public static String getDataFormat(String value,int scale)
{
	String format="";
	if((value==null|| "".equals(value.trim()))&&scale==-1)
	{
		return "0";
	}
	else if(scale==-1)
	{
		if(value.indexOf(".")!=-1)
		{
			//itemfmt
			String str = value.substring(value.indexOf(".")+1);
			StringBuffer sb = new StringBuffer("");
			for(int i=0;i<str.length();i++)
			{
				sb.append("0");
			}
			format="0"+(sb.toString().trim().length()>0?("."+sb.toString()):"");
		}
		else{
			format="0";
		}
	}else{
		StringBuffer sb = new StringBuffer("");
		for(int i=0;i<scale;i++)
		{
			sb.append("0");
		}
		format="0"+(sb.toString().trim().length()>0?("."+sb.toString()):"");
	}
	return format;
	
}
public int getNums() {
	return nums;
}
public void setNums(int nums) {
	this.nums = nums;
}
	 
	 
	 
}
