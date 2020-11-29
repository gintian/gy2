package com.hjsj.hrms.businessobject.hire;

import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.interfaces.report.ReportParseXml;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import javax.sql.RowSet;
import java.awt.*;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ExecuteExcel {
	private Connection conn = null;
	private String whl_sql="";
	private String operateObject="";  //操作对象 1：单表操作 2：多表操作
	private HashMap employerNameMap=new HashMap();    //针对面试安排信息 模块
	private HashMap planCountMap=new HashMap();       //针对招聘总结     模块
	private ReportParseVo paramtervo=null;
	private UserView userView=null;
	private HashMap numberMap=new HashMap();
	private String datetime=""; //日期
	private String times="";  //时间
	private int total=0;//总人数
	private String producer="";//制作人
	private int pageno=0;//页码
	private String tableName="";
	//导出的文件名
	private String fileName="";
	
	public ExecuteExcel(Connection conn)
	{
		this.conn=conn;
	}
	public ExecuteExcel(Connection conn,UserView userView,String tableName)
	{
		this.conn=conn;
		this.userView=userView;
		this.producer=userView.getUserFullName();
		this.tableName=tableName;
		setPagePrintStyle(userView.getUserName());
		initInfo();
	}
	
	public void SetParamtervo(String xmlContent, String  path)
	{
		ReportParseXml parseXml = new ReportParseXml();	 
		if(xmlContent!=null&&xmlContent.length()>1) {
            paramtervo = parseXml.ReadOutParseXml(xmlContent,path);
        }

	}
	
	
	/**
	 * 取得页面打印格式
	 * @param username
	 */
	public void  setPagePrintStyle(String username)
	{
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try
		{
			String xmlContent="";
			rowSet=dao.search("select * from constant where constant='SYS_OTH_PARAM'");
			if(rowSet.next()) {
                xmlContent=Sql_switcher.readMemo(rowSet,"str_value");
            }
			String xpath="/param/pageoptions/report[@name='"+username+"']";
			SetParamtervo(xmlContent,xpath);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void initInfo(){
		
		datetime=Calendar.getInstance().get(Calendar.YEAR)+"."+(Calendar.getInstance().get(Calendar.MONTH)+1)+"."
					+Calendar.getInstance().get(Calendar.DATE);
		times=Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+":"+Calendar.getInstance().get(Calendar.MINUTE)+":"
			+Calendar.getInstance().get(Calendar.SECOND);
		
	}
	
	public String createExcel(ArrayList a_fieldList,String a_whl_sql,String operateObject)
	{
		if(a_whl_sql.length()>8) {
            this.whl_sql=a_whl_sql;
        }
		this.operateObject=operateObject;
		pageno++;
		String outputFile = this.userView.getUserName() + "_" + tableName + ".xls";
//		String outputFile ="interviewExcel12345.xls";   //固定文件名，免得临时文件夹中产生的文件过多
		HSSFWorkbook workbook = null;
		FileOutputStream fileOut = null;
		try
		{
			
			ArrayList list=getTableField(a_fieldList);
			String[][] excelData=getExcelData(list);
			this.total=excelData.length-1;
			
			
			HashMap 	tableMap=getTableExtendInfo();  //标题，表头信息
			HashMap 	topHeadMap=getTopHeadExtendInfo(); 
			HashMap 	buttomHeadMap=getButtomHeadExtendInfo();  

			workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet();
			HSSFRow row=null;
			HSSFCell csCell=null;
			short n=executeTitelModify(tableMap,excelData,sheet,workbook);  //写上表头 和 标题
			int m=executeHeader(topHeadMap,excelData,sheet,workbook,n); 
			int k=m;
			HSSFCellStyle styletext = style(workbook,1);
			styletext.setWrapText(true);
			styletext.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));
			styletext.setAlignment(HorizontalAlignment.LEFT );

			for(int i=0;i<excelData.length;i++)
			{
//				row = sheet.createRow(i+m);	
				row = sheet.getRow(i+m);
				if(row==null) {
                    row = sheet.createRow(i+m);
                }

				for(short j=0;j<excelData[i].length;j++)
				{
					csCell =row.createCell(j);
//					csCell.setCellStyle(bodyCellStyle(workbook));
//					csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
//					csCell.setCellValue(excelData[i][j]);
					csCell.setCellStyle(styletext);
					HSSFRichTextString textstr = new HSSFRichTextString(excelData[i][j]);
					csCell.setCellValue(textstr);
				}
				k=i+m;
			}	
			executeHeader(buttomHeadMap,excelData,sheet,workbook,k+2);
			if(excelData!=null)
			{
				executeFoot(n,tableMap,excelData,sheet);
			}
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outputFile);
			workbook.write(fileOut);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally {
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(workbook);
		}
		return outputFile;
	}
	public String createTabExcelHt(ArrayList a_fieldList,String a_whl_sql,String operateObject){
		if(a_whl_sql.length()>8) {
            this.whl_sql=a_whl_sql;
        }
		this.operateObject=operateObject;
		pageno++;
		String outputFile =userView.getUserName()+"interviewExcel12345.xls";   //固定文件名，免得临时文件夹中产生的文件过多
		try
		{
			
		    if(StringUtils.isNotEmpty(this.fileName)) {
		        outputFile = this.fileName;
		    }
		        
			ArrayList list=getTableField(a_fieldList);
			String[][] excelData=getExcelData(list);
			ArrayList fileList = (ArrayList) list.get(6);
			
			
			HashMap 	tableMap=getTableExtendInfo();  //标题，表头信息
			HashMap 	topHeadMap=getTopHeadExtendInfo(); 
			HashMap 	buttomHeadMap=getButtomHeadExtendInfo();  

			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet();
			HSSFRow row=null;
			HSSFCell csCell=null;
			short n=0;  //写上表头 和 标题
			short m=0; 
			int k=m;
			
			HSSFCellStyle styletext = style(workbook,1);
			styletext.setWrapText(true);
			styletext.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));
			styletext.setAlignment(HorizontalAlignment.LEFT );
			
			HSSFCellStyle styleN = style(workbook,1);
			styleN.setAlignment(HorizontalAlignment.RIGHT );
			styleN.setWrapText(true);
			HSSFDataFormat df = workbook.createDataFormat();
			styleN.setDataFormat(df.getFormat(decimalwidth(0)));
			
			HSSFCellStyle styleF1 = style(workbook,1);
			styleF1.setAlignment(HorizontalAlignment.RIGHT );
			styleF1.setWrapText(true);
			HSSFDataFormat df1 = workbook.createDataFormat();
			styleF1.setDataFormat(df1.getFormat(decimalwidth(1)));
			
			HSSFCellStyle styleF2 = style(workbook,1);
			styleF2.setAlignment(HorizontalAlignment.RIGHT );
			styleF2.setWrapText(true);
			HSSFDataFormat df2 = workbook.createDataFormat();
			styleF2.setDataFormat(df2.getFormat(decimalwidth(2)));
			
			HSSFCellStyle styleF3 = style(workbook,1);
			styleF3.setAlignment(HorizontalAlignment.RIGHT );
			styleF3.setWrapText(true);
			HSSFDataFormat df3 = workbook.createDataFormat();
			styleF3.setDataFormat(df3.getFormat(decimalwidth(3)));
			
			HSSFCellStyle styleF4 = style(workbook,1);
			styleF4.setAlignment(HorizontalAlignment.RIGHT );
			styleF4.setWrapText(true);
			HSSFDataFormat df4 = workbook.createDataFormat();
			styleF4.setDataFormat(df4.getFormat(decimalwidth(4)));
			
			HSSFCellStyle styleF5 = style(workbook,1);
			styleF5.setAlignment(HorizontalAlignment.RIGHT );
			styleF5.setWrapText(true);
			HSSFDataFormat df5 = workbook.createDataFormat();
			styleF5.setDataFormat(df5.getFormat(decimalwidth(5)));

			for(int i=0;i<excelData.length;i++)
			{
//				row = sheet.createRow(i+m);	
				row = sheet.getRow(i+m);
				if(row==null) {
                    row = sheet.createRow(i+m);
                }
				if(i==0){
					for(short j=0;j<fileList.size();j++)
					{
						csCell =row.createCell(j);
//						csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
						csCell.setCellStyle(styletext);
						HSSFRichTextString textstr = new HSSFRichTextString(excelData[i][j]);
						csCell.setCellValue(textstr);
					}
				}else{
					for(short j=0;j<fileList.size();j++)
					{
						csCell =row.createCell(j);
//						csCell.setEncoding(HSSFCell.ENCODING_UTF_16);	
						FieldItem item= null;
						if(!"3".equals(this.operateObject)){
							if(j>0){
								item=(FieldItem)fileList.get(j); 
								if(item!=null&&item.getItemtype()!=null&& "N".equalsIgnoreCase(item.getItemtype())){
									if(item.getDecimalwidth()==0) {
                                        csCell.setCellStyle(styleN);
                                    } else if(item.getDecimalwidth()==1) {
                                        csCell.setCellStyle(styleF1);
                                    } else if(item.getDecimalwidth()==2) {
                                        csCell.setCellStyle(styleF2);
                                    } else if(item.getDecimalwidth()==3) {
                                        csCell.setCellStyle(styleF3);
                                    } else if(item.getDecimalwidth()==4) {
                                        csCell.setCellStyle(styleF4);
                                    } else if(item.getDecimalwidth()==5) {
                                        csCell.setCellStyle(styleF5);
                                    } else {
                                        csCell.setCellStyle(styleF5);
                                    }
									if(excelData[i][j]!=null&&excelData[i][j].trim().length()>0){
										double values = strToDouble(excelData[i][j]);
										if(values!=0) {
                                            csCell.setCellValue(values);
                                        } else{
											HSSFRichTextString textstr = new HSSFRichTextString(excelData[i][j]);
											csCell.setCellValue(textstr);
										}
									}else{
										HSSFRichTextString textstr = new HSSFRichTextString(excelData[i][j]);
										csCell.setCellValue(textstr);
									}
								}else{
									csCell.setCellStyle(styletext);
									HSSFRichTextString textstr = new HSSFRichTextString(excelData[i][j]);
									csCell.setCellValue(textstr);
								}
							}else{
								csCell.setCellStyle(styleN);
								double values = strToDouble(excelData[i][j]);
								if(values!=0) {
                                    csCell.setCellValue(values);
                                } else{
									HSSFRichTextString textstr = new HSSFRichTextString(excelData[i][j]);
									csCell.setCellValue(textstr);
								}
							}
						}else{
							item=(FieldItem)fileList.get(j);
							if(item!=null&&item.getItemtype()!=null&& "N".equalsIgnoreCase(item.getItemtype())){
								if(item.getDecimalwidth()==0) {
                                    csCell.setCellStyle(styleN);
                                } else if(item.getDecimalwidth()==1) {
                                    csCell.setCellStyle(styleF1);
                                } else if(item.getDecimalwidth()==2) {
                                    csCell.setCellStyle(styleF2);
                                } else if(item.getDecimalwidth()==3) {
                                    csCell.setCellStyle(styleF3);
                                } else if(item.getDecimalwidth()==4) {
                                    csCell.setCellStyle(styleF4);
                                } else if(item.getDecimalwidth()==5) {
                                    csCell.setCellStyle(styleF5);
                                } else {
                                    csCell.setCellStyle(styleF5);
                                }
								if(excelData[i][j]!=null&&excelData[i][j].trim().length()>0){
									double values = strToDouble(excelData[i][j]);
									if(values!=0) {
                                        csCell.setCellValue(values);
                                    } else{
										HSSFRichTextString textstr = new HSSFRichTextString(excelData[i][j]);
										csCell.setCellValue(textstr);
									}
								}else{
									HSSFRichTextString textstr = new HSSFRichTextString(excelData[i][j]);
									csCell.setCellValue(textstr);
								}
							}else{
								csCell.setCellStyle(styletext);
								HSSFRichTextString textstr = new HSSFRichTextString(excelData[i][j]);
								csCell.setCellValue(textstr);
							}
						}
					}
				}
				k=i+m;
			}	
			executeHeader(buttomHeadMap,excelData,sheet,workbook,k+2);
			if(excelData!=null)
			{
				executeFoot(n,tableMap,excelData,sheet);
			}
			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outputFile);
			workbook.write(fileOut);
			fileOut.close();	
			sheet=null;
			workbook=null;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return outputFile;
	}
	
	/**
	 * 看板管理导出excel
	 * @Title: createTabExcelHt2   
	 * @Description:    
	 * @param a_fieldList
	 * @param a_whl_sql
	 * @param operateObject
	 * @return
	 */
	public String createTabExcelHt2(ArrayList a_fieldList,String a_whl_sql,String operateObject){
		if(a_whl_sql.length()>8) {
            this.whl_sql=a_whl_sql;
        }
		this.operateObject=operateObject;
		pageno++;
		String outputFile ="kanban_" + userView.getUserName() + ".xls";   //固定文件名，免得临时文件夹中产生的文件过多
		try
		{
			
			ArrayList list = getTableField(a_fieldList);
			String[][] excelData = getExcelData(list);
			if (null == excelData) {
			    excelData = new String[0][0];
			}
			ArrayList fileList = (ArrayList) list.get(6);
			
			
			HashMap 	tableMap=getTableExtendInfo();  //标题，表头信息
			HashMap 	topHeadMap=getTopHeadExtendInfo(); 
			HashMap 	buttomHeadMap=getButtomHeadExtendInfo();  

			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet();
			HSSFRow row=null;
			HSSFCell csCell=null;
			short n=0;  //写上表头 和 标题
			short m=0; 
			int k=m;
			
			HSSFCellStyle styletext = style(workbook,1);
			styletext.setWrapText(true);
			styletext.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));
			styletext.setAlignment(HorizontalAlignment.LEFT );
			
			HSSFCellStyle styleN = style(workbook,1);
			styleN.setAlignment(HorizontalAlignment.RIGHT );
			styleN.setWrapText(true);
			HSSFDataFormat df = workbook.createDataFormat();
			styleN.setDataFormat(df.getFormat(decimalwidth(0)));
			
			HSSFCellStyle styleF1 = style(workbook,1);
			styleF1.setAlignment(HorizontalAlignment.RIGHT );
			styleF1.setWrapText(true);
			HSSFDataFormat df1 = workbook.createDataFormat();
			styleF1.setDataFormat(df1.getFormat(decimalwidth(1)));
			
			HSSFCellStyle styleF2 = style(workbook,1);
			styleF2.setAlignment(HorizontalAlignment.RIGHT );
			styleF2.setWrapText(true);
			HSSFDataFormat df2 = workbook.createDataFormat();
			styleF2.setDataFormat(df2.getFormat(decimalwidth(2)));
			
			HSSFCellStyle styleF3 = style(workbook,1);
			styleF3.setAlignment(HorizontalAlignment.RIGHT );
			styleF3.setWrapText(true);
			HSSFDataFormat df3 = workbook.createDataFormat();
			styleF3.setDataFormat(df3.getFormat(decimalwidth(3)));
			
			HSSFCellStyle styleF4 = style(workbook,1);
			styleF4.setAlignment(HorizontalAlignment.RIGHT );
			styleF4.setWrapText(true);
			HSSFDataFormat df4 = workbook.createDataFormat();
			styleF4.setDataFormat(df4.getFormat(decimalwidth(4)));
			
			HSSFCellStyle styleF5 = style(workbook,1);
			styleF5.setAlignment(HorizontalAlignment.RIGHT );
			styleF5.setWrapText(true);
			HSSFDataFormat df5 = workbook.createDataFormat();
			styleF5.setDataFormat(df5.getFormat(decimalwidth(5)));

			// 序号
			int index = 1;
			for(int i=0;i<excelData.length;i++)
			{
//				row = sheet.createRow(i+m);	
				row = sheet.getRow(i+m);
				if(row==null) {
                    row = sheet.createRow(i+m);
                }
				if(i==0){
					for(short j=0;j<fileList.size();j++)
					{
						FieldItem it = (FieldItem) fileList.get(j);
						csCell =row.createCell(j);
//						csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
						csCell.setCellStyle(styletext);
						HSSFRichTextString textstr = new HSSFRichTextString(it.getItemdesc());
						csCell.setCellValue(textstr);
					}
				}else{
					for(short j=0;j<fileList.size();j++)
					{
						csCell =row.createCell(j);
//						csCell.setEncoding(HSSFCell.ENCODING_UTF_16);	
						FieldItem item= null;
						if(!"3".equals(this.operateObject)){
							if(j>0){
								item=(FieldItem)fileList.get(j); 
								if(item!=null&&item.getItemtype()!=null&& "N".equalsIgnoreCase(item.getItemtype())){
									if(item.getDecimalwidth()==0) {
                                        csCell.setCellStyle(styleN);
                                    } else if(item.getDecimalwidth()==1) {
                                        csCell.setCellStyle(styleF1);
                                    } else if(item.getDecimalwidth()==2) {
                                        csCell.setCellStyle(styleF2);
                                    } else if(item.getDecimalwidth()==3) {
                                        csCell.setCellStyle(styleF3);
                                    } else if(item.getDecimalwidth()==4) {
                                        csCell.setCellStyle(styleF4);
                                    } else if(item.getDecimalwidth()==5) {
                                        csCell.setCellStyle(styleF5);
                                    } else {
                                        csCell.setCellStyle(styleF5);
                                    }
									if(excelData[i][j]!=null&&excelData[i][j].trim().length()>0){
										double values = strToDouble(excelData[i][j]);
										if(values!=0) {
                                            csCell.setCellValue(values);
                                        } else{
											HSSFRichTextString textstr = new HSSFRichTextString(excelData[i][j]);
											csCell.setCellValue(textstr);
										}
									}else{
										HSSFRichTextString textstr = new HSSFRichTextString(excelData[i][j]);
										csCell.setCellValue(textstr);
									}
								}else{
									csCell.setCellStyle(styletext);
									HSSFRichTextString textstr = new HSSFRichTextString(excelData[i][j]);
									csCell.setCellValue(textstr);
								}
							}else{
								csCell.setCellStyle(styleN);
								double values = strToDouble(excelData[i][j]);
								if(values!=0) {
                                    csCell.setCellValue(values);
                                } else{
									HSSFRichTextString textstr = new HSSFRichTextString(excelData[i][j]);
									csCell.setCellValue(textstr);
								}
							}
						}else{
							item=(FieldItem)fileList.get(j);
							if (j == 0) {
								csCell.setCellStyle(styletext);
								csCell.setCellValue(index);
								index ++;
							} else {
							if(item!=null&&item.getItemtype()!=null&& "N".equalsIgnoreCase(item.getItemtype())){
								if(item.getDecimalwidth()==0) {
                                    csCell.setCellStyle(styleN);
                                } else if(item.getDecimalwidth()==1) {
                                    csCell.setCellStyle(styleF1);
                                } else if(item.getDecimalwidth()==2) {
                                    csCell.setCellStyle(styleF2);
                                } else if(item.getDecimalwidth()==3) {
                                    csCell.setCellStyle(styleF3);
                                } else if(item.getDecimalwidth()==4) {
                                    csCell.setCellStyle(styleF4);
                                } else if(item.getDecimalwidth()==5) {
                                    csCell.setCellStyle(styleF5);
                                } else {
                                    csCell.setCellStyle(styleF5);
                                }
								if(excelData[i][j-1]!=null&&excelData[i][j-1].trim().length()>0){
									double values = strToDouble(excelData[i][j-1]);
									if(values!=0) {
                                        csCell.setCellValue(values);
                                    } else{
										HSSFRichTextString textstr = new HSSFRichTextString(excelData[i][j-1]);
										csCell.setCellValue(textstr);
									}
								}else{
									HSSFRichTextString textstr = new HSSFRichTextString(excelData[i][j-1]);
									csCell.setCellValue(textstr);
								}
							}else{
								csCell.setCellStyle(styletext);
								HSSFRichTextString textstr = new HSSFRichTextString(excelData[i][j-1]);
								csCell.setCellValue(textstr);
							}
						}
						}
						
					}
				}
				k=i+m;
			}	
			executeHeader(buttomHeadMap,excelData,sheet,workbook,k+2);
			if(excelData!=null)
			{
				executeFoot(n,tableMap,excelData,sheet);
			}
			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outputFile);
			workbook.write(fileOut);
			fileOut.close();	
			sheet=null;
			workbook=null;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return outputFile;
	}
	
	public String createTabExcel(ArrayList a_fieldList,String a_whl_sql,String operateObject)
	{
		if(a_whl_sql.length()>8) {
            this.whl_sql=a_whl_sql;
        }
		this.operateObject=operateObject;
		pageno++;
		//String outputFile =PubFunc.getStrg() + "_" + tableName + ".xls";
		String outputFile = this.userView.getUserName()+"_train.xls";   //固定文件名，免得临时文件夹中产生的文件过多
		HSSFWorkbook workbook = null;
		FileOutputStream fileOut = null;
		try
		{
			
			ArrayList list=getTableField(a_fieldList);
			String[][] excelData=getExcelData(list);
			
			
			HashMap 	tableMap=getTableExtendInfo();  //标题，表头信息
			HashMap 	topHeadMap=getTopHeadExtendInfo(); 
			HashMap 	buttomHeadMap=getButtomHeadExtendInfo();  

			workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet();
			HSSFRow row=null;
			HSSFCell csCell=null;
			short n=0;  //写上表头 和 标题
			short m=0; 
			int k=m;

			for(int i=0;i<excelData.length;i++)
			{
//				row = sheet.createRow(i+m);	
				row = sheet.getRow(i+m);
				if(row==null) {
                    row = sheet.createRow(i+m);
                }
				if(i==0){
					for(short j=0;j<excelData[i].length;j++)
					{
						csCell =row.createCell(j);
						csCell.setCellStyle(bodyCellStyle(workbook));
//						csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
						HSSFRichTextString textstr = new HSSFRichTextString(excelData[i][j]);
						csCell.setCellValue(textstr);
					}
				}else{
					for(short j=0;j<excelData[i].length;j++)
					{
						csCell =row.createCell(j);
//						csCell.setCellStyle(bodyCellStyle(workbook));
//						csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
						csCell.setCellValue(excelData[i][j]);
					}
				}
				k=i+m;
			}	
			executeHeader(buttomHeadMap,excelData,sheet,workbook,k+2);
			if(excelData!=null)
			{
				executeFoot(n,tableMap,excelData,sheet);
			}
			for (int i = 0; i < k; i++) {
				sheet.autoSizeColumn((short)i);
			}
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outputFile);
			workbook.write(fileOut);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(workbook);
		}
		return outputFile;
	}
//===工作计划模块调用此方法生成excel：fuj=================================================================================
	public String createExcel(String[][] excelData,String name)
	{
		
		String outputFile =name+".xls";
		HSSFWorkbook workbook = null;
		FileOutputStream fileOut = null;
		try
		{
			workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet();
			HSSFRow row=null;
			HSSFCell csCell=null;
			short n=0;  //写上表头 和 标题
			short m=0; 
			int k=m;

			for(int i=0;i<excelData.length;i++)
			{
				row = sheet.getRow(i+m);
				if(row==null) {
                    row = sheet.createRow(i+m);
                }
				if(i==0){
					for(short j=0;j<excelData[i].length;j++)
					{
						csCell =row.createCell(j);
						csCell.setCellStyle(bodyCellStyle(workbook));
						HSSFRichTextString textstr = new HSSFRichTextString(excelData[i][j]);
						csCell.setCellValue(textstr);
					}
				}else{
					for(short j=0;j<excelData[i].length;j++)
					{
						csCell =row.createCell(j);
						csCell.setCellValue(excelData[i][j]);
					}
				}
				k=i+m;
			}	
			for (int i = 0; i < k; i++) {
				sheet.autoSizeColumn((short)i);
			}
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outputFile);
			workbook.write(fileOut);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(workbook);
		}
		return outputFile;
	}
//====================================================================================	
	public HSSFCellStyle bodyCellStyle(HSSFWorkbook workbook){
		HSSFCellStyle cellStyle= workbook.createCellStyle();
		String fontfamilyname=ResourceFactory.getProperty("hmuster.label.fontSt");
        String fontEffect="0";
        int    fontSize=12;
        String color="#000000";
        String underLine="#fu[0]";
        if(this.paramtervo!=null)
        {
        	 if(this.paramtervo.getBody_fn().length()>0) {
                 fontfamilyname=this.paramtervo.getBody_fn();
             }
        	 fontEffect=getFontEffect(3);
        	 if(this.paramtervo.getBody_fz().length()>0) {
                 fontSize=Integer.parseInt(this.paramtervo.getBody_fz());
             }
        	 if(this.paramtervo.getBody_fc().length()>0) {
                 color=this.paramtervo.getBody_fc();
             }
        	 if(this.paramtervo.getBody_fu()!=null&&this.paramtervo.getBody_fu().length()>0) {
                 underLine=this.paramtervo.getBody_fu();
             }
        }
        Color corlor = new Color(Integer.parseInt(color.substring(1,3),16),Integer.parseInt(color.substring(3,5),16),Integer.parseInt(color.substring(5,7),16));
        HSSFFont font = workbook.createFont();			
		font.setColor((short)corlor.getRGB());
		font.setFontName(fontfamilyname);
		if("2".equals(fontEffect)) {
            font.setBold(true);
        }
		if("3".equals(fontEffect)) {
            font.setItalic(true);
        }
		if("4".equals(fontEffect)){
			font.setBold(true);
			font.setItalic(true);
		}
		if("#fu[1]".equals(underLine)) {
            font.setUnderline((byte)1);
        }
		font.setFontHeightInPoints(Short.parseShort(fontSize+""));
		cellStyle.setFont(font);
		
       return cellStyle;
	}
	private HashMap subSetMap;
	public void setSubSetMap(HashMap subSetMap)
	{
		this.subSetMap=subSetMap;
	}
	
	public String createExcel(String username,String tableName,ArrayList a_fieldList,String a_whl_sql,String operateObject,UserView view)
	{
		setPagePrintStyle(username);
		initInfo();
		this.producer=username;
		this.pageno++;
		
		this.tableName=tableName;
		if(a_whl_sql.length()>8) {
            this.whl_sql=a_whl_sql;
        }
		this.operateObject=operateObject;
		String outputFile = username + "_" + tableName + ".xls";
//		String outputFile ="interviewExcel12345"+view.getUserName()+".xls";   //固定文件名，免得临时文件夹中产生的文件过多
		try
		{
			ArrayList list=getTableField(a_fieldList);
			String[][] excelData=getExcelData(list);
			this.total=excelData.length-1;
			
			HashMap tableMap=getTableExtendInfo();  //标题，表头信息
			
			
			HashMap 	topHeadMap=getTopHeadExtendInfo(); 
			HashMap 	buttomHeadMap=getButtomHeadExtendInfo();  
			
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet();
			HSSFRow row=null;
			HSSFCell csCell=null;
			short n=executeTitelModify(tableMap,excelData,sheet,workbook);  //写上表头 和 标题
			int m=executeHeader(topHeadMap,excelData,sheet,workbook,n); 
			int k=m;
			String macth="(-?\\d*)(\\.\\d+)?";
			HSSFCellStyle cellStyle=workbook.createCellStyle();
			cellStyle.setWrapText(true);
			cellStyle.setAlignment(HorizontalAlignment.CENTER);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setBorderTop(BorderStyle.THIN);
			cellStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
			
			
			/**********************************************/// zzk 2014/1/20
			String t_fontfamilyname=ResourceFactory.getProperty("gz.gz_acounting.m.font");
	        String t_fontEffect="0";
	        int    t_fontSize=17;  
	        String t_color="#000000";       
	        String  t_underLine="#fu[0]";
	        String  t_strikethru="#fs[0]";
	        if(this.paramtervo!=null)
	        {
	        	 if(this.paramtervo.getBody_fn().length()>0) {
                     t_fontfamilyname=this.paramtervo.getBody_fn();
                 }
	        	 t_fontEffect=getFontEffect(3);
	        	 if(this.paramtervo.getBody_fz().length()>0) {
                     t_fontSize=Integer.parseInt(this.paramtervo.getBody_fz());
                 }
	        	 if(this.paramtervo.getBody_fc().length()>0) {
                     t_color=this.paramtervo.getBody_fc();
                 }
	        	 if(this.paramtervo.getBody_fu()!=null&&this.paramtervo.getBody_fu().length()>0) {
                     t_underLine=this.paramtervo.getBody_fu();
                 }

	        }
//	        if(this.paramtervo!=null){
//	        	if(this.paramtervo.getTitle_fn().length()>0)
//	        		t_fontfamilyname=this.paramtervo.getTitle_fn();
//		       	 t_fontEffect=getFontEffect(4);
//		       	 if(this.paramtervo.getTitle_fz().length()>0)
//		       		 t_fontSize=Integer.parseInt(this.paramtervo.getTitle_fz());
//		       	 if(this.paramtervo.getTitle_fc().length()>0)
//		       		 t_color=this.paramtervo.getTitle_fc();	       	 
//		       	 if(this.paramtervo.getTitle_fu()!=null&&this.paramtervo.getTitle_fu().length()>0)
//		       		 t_underLine=this.paramtervo.getTitle_fu();
//		       	if(this.paramtervo.getTitle_fs()!=null&&this.paramtervo.getTitle_fs().length()>0)
//		       		t_strikethru=this.paramtervo.getTitle_fs();
//
//	        }
			HSSFFont font = workbook.createFont();
			HSSFPalette palette=workbook.getCustomPalette();
			Color corlor = new Color(Integer.parseInt(t_color.substring(1,3),16),Integer.parseInt(t_color.substring(3,5),16),Integer.parseInt(t_color.substring(5,7),16));		
			HSSFColor  hssffColor=palette.findColor((byte)corlor.getRed(), (byte)corlor.getGreen(), (byte)corlor.getBlue());
			font.setColor(hssffColor.getIndex());
//			font.setColor(HSSFFont.COLOR_RED);//设置字体颜色
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
			cellStyle.setFont(font);
			
			
			for(int i=0;i<excelData.length;i++)
			{
				row = sheet.getRow(i+m+1);
				if(row==null) {
                    row = sheet.createRow(i+m+1);
                }
				for(short j=0;j<excelData[i].length;j++)
				{
					csCell =row.createCell(j);
					if("resume".equalsIgnoreCase(this.tableName)) {
                        csCell.setCellStyle(cellStyle);
                    }
					String value=excelData[i][j];
					if(value==null) {
                        value="";
                    }
					
					String hzName = excelData[0][j];
					String itemType = this.getFieldTypeByHzName(a_fieldList, hzName);
					
					/**当是数字，并且长度大于15的话，认为是身份证号，平时应该很少有大于15位的数字，而且这个导出文件的程序有很多地方再用，不好改，lizhenwei*/
					if(!"".equals(value)&&value.matches(macth)&&"N".equalsIgnoreCase(itemType))//value.length()<15&&!value.equals("-")
					{
						csCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						csCell.setCellValue(Double.parseDouble(value));
					}
					else{		
						if("resume".equalsIgnoreCase(this.tableName)){
					    	HSSFRichTextString taxt = new HSSFRichTextString(value);
					    	csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
	     	  		    	csCell.setCellValue(taxt);
						}else{
							csCell.setCellValue(value);
						}
					}
				}
				k=i+m;
			}		
			
			executeHeader(buttomHeadMap,excelData,sheet,workbook,k+2);
			if(excelData!=null)
			{
				executeFoot(n,tableMap,excelData,sheet);
			}
			if("resume".equalsIgnoreCase(this.tableName))
			{
				for(int i = 0; i <=excelData[0].length; i++)
				{   
					String number = (String)numberMap.get(String.valueOf(i));
					if((String)numberMap.get(String.valueOf(i))==null) {
                        number ="2";
                    }
					 int xx=Integer.parseInt(number);
					//sheet.autoSizeColumn((short)i);
					sheet.setColumnWidth(Short.parseShort(String.valueOf(i)),(short)(475*xx+350));
					
				}
				for(int i = 0; i <=excelData.length+m; i++)
				{   
				
					int xx =0;
					if(i>5){
//					String number = (String)numberMap.get(i);
//					 xx=Integer.parseInt(number);
					 //System.out.println(xx+"---"+i);
					}
				
//				    row = sheet.getRow(i);
//					if(row==null)
//						 row = sheet.createRow(i);
				    //row.setHeight((short)(600+400*xx));
					//sheet.autoSizeColumn((short)i);
				}
			}
			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outputFile);
			workbook.write(fileOut);
			fileOut.close();	
			sheet=null;
			workbook=null;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return outputFile;
	}
	
	private String getFieldTypeByHzName(ArrayList fieldList, String hzName) {
		for (int i = 0; i < fieldList.size(); i++) {
			FieldItem item = (FieldItem)fieldList.get(i);
			if (hzName.equalsIgnoreCase(item.getItemdesc())) {
				return item.getItemtype();
			}
		}
		
		return "A";
	}
	
	public void executeFoot(short n,HashMap tableMap,String[][] excelData,HSSFSheet sheet)
	{
//		写下表头
		HSSFRow row=null;
		HSSFCell csCell=null;
		if(excelData!=null&&excelData.length>0&&excelData[0].length>2)
		{
//			row=sheet.createRow(n+Short.parseShort(String.valueOf(excelData.length)));
			row = sheet.getRow(n+excelData.length);
			if(row==null) {
                row=sheet.createRow(n+excelData.length);
            }
			if(tableMap.get("lfoot")!=null)
			{
				csCell=row.createCell(Short.parseShort("0"));
//				csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
				csCell.setCellValue((String)tableMap.get("lfoot"));
			}
			if(tableMap.get("mfoot")!=null)
			{
				csCell=row.createCell(Short.parseShort(String.valueOf(excelData[0].length/2)));
//				csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
				csCell.setCellValue((String)tableMap.get("mfoot"));
			}
			if(tableMap.get("rfoot")!=null)
			{
				csCell=row.createCell(Short.parseShort(String.valueOf(excelData[0].length-1)));
//				csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
				csCell.setCellValue((String)tableMap.get("rfoot"));
			}			
		}
	}
	
	

	public short executeTitelModify(HashMap tableMap,String[][] excelData,HSSFSheet sheet,HSSFWorkbook workbook)
	{  
		short n=0;
		try {
			HSSFRow row=null;
			HSSFCell csCell=null;
//		写标题
			if(tableMap.get("title")!=null&&excelData!=null&&excelData.length>0&&excelData[0].length>2)
			{
//			写标题
				String t_fontfamilyname=ResourceFactory.getProperty("gz.gz_acounting.m.font");
				String t_fontEffect="0";
				int    t_fontSize=17;  
				String t_color="#000000";       
				String  t_underLine="#fu[0]";
				String  t_strikethru="#fs[0]";
				if(this.paramtervo!=null){
					if(this.paramtervo.getTitle_fn().length()>0) {
                        t_fontfamilyname=this.paramtervo.getTitle_fn();
                    }
					t_fontEffect=getFontEffect(4);
					if(this.paramtervo.getTitle_fz().length()>0) {
                        t_fontSize=Integer.parseInt(this.paramtervo.getTitle_fz());
                    }
					if(this.paramtervo.getTitle_fc().length()>0) {
                        t_color=this.paramtervo.getTitle_fc();
                    }
					if(this.paramtervo.getTitle_fu()!=null&&this.paramtervo.getTitle_fu().length()>0) {
                        t_underLine=this.paramtervo.getTitle_fu();
                    }
					if(this.paramtervo.getTitle_fs()!=null&&this.paramtervo.getTitle_fs().length()>0) {
                        t_strikethru=this.paramtervo.getTitle_fs();
                    }
					
				}
				HSSFFont font = workbook.createFont();
				HSSFPalette palette=workbook.getCustomPalette();
				Color corlor = new Color(Integer.parseInt(t_color.substring(1,3),16),Integer.parseInt(t_color.substring(3,5),16),Integer.parseInt(t_color.substring(5,7),16));		
				HSSFColor  hssffColor=palette.findColor((byte)corlor.getRed(), (byte)corlor.getGreen(), (byte)corlor.getBlue());
				if(hssffColor!=null){
					font.setColor(hssffColor.getIndex());// zzk 2014/1/20
				}
//			font.setColor(HSSFFont.COLOR_RED);//设置字体颜色
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
				HSSFCellStyle cellStyle= style(workbook,5);
				cellStyle.setAlignment(HorizontalAlignment.CENTER );
				cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
				cellStyle.setBorderBottom(BorderStyle.NONE);
				cellStyle.setWrapText(true);
				cellStyle.setFont(font);
				
				ExportExcelUtil.mergeCell(sheet, 0, (short)0,0,(short)(Integer.parseInt(String.valueOf(excelData[0].length))-1));
				row=sheet.createRow(n);
				row.setHeight((short)1000);
				csCell=row.createCell((short)0);
				csCell.setCellStyle(cellStyle);
				//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
				csCell.setCellValue((String)tableMap.get("title"));
				n++;
			}
			//写上表头
			HSSFCellStyle titlestyle = style(workbook,5);
			titlestyle.setWrapText(true);
			if(excelData!=null&&excelData.length>0&&excelData[0].length>2)
			{
				row=sheet.createRow(n);
				row = sheet.getRow(n);
				if(row==null) {
                    row=sheet.createRow(n);
                }
				if(tableMap.get("lhead")!=null)
				{
					csCell=row.createCell(Short.parseShort("0"));
//				csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
					csCell.setCellValue((String)tableMap.get("lhead"));
				}
				if(tableMap.get("mhead")!=null)
				{
					csCell=row.createCell(Short.parseShort(String.valueOf(excelData[0].length/2)));
//				csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
					csCell.setCellValue((String)tableMap.get("mhead"));
				}
				if(tableMap.get("rhead")!=null)
				{
					csCell=row.createCell(Short.parseShort(String.valueOf(excelData[0].length-1)));
//				csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
					csCell.setCellValue((String)tableMap.get("rhead"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return n;
	}
	
	public short executeTitel(HashMap tableMap,String[][] excelData,HSSFSheet sheet,HSSFWorkbook workbook)
	{  
		short n=0;
		HSSFRow row=null;
		HSSFCell csCell=null;
		
		String titleStr = (String)tableMap.get("title");
		titleStr=titleStr!=null&&titleStr.trim().length()>0?titleStr:"";
		
//		写标题
		if(titleStr.trim().length()>0&&excelData!=null&&excelData.length>0&&excelData[0].length>2)
		{
			String orgcolor=(String)tableMap.get("color");
			HSSFFont font = workbook.createFont();	
			if(orgcolor!=null)
			{
		    	Color corlor = new Color(Integer.parseInt(orgcolor.substring(1,3),16),Integer.parseInt(orgcolor.substring(3,5),16),Integer.parseInt(orgcolor.substring(5,7),16));
	    		font.setColor((short)corlor.getRGB());
			}
			if(tableMap.get("fontfamilyname")!=null) {
                font.setFontName((String)tableMap.get("fontfamilyname"));
            }
			if(tableMap.get("fontEffect")!=null)
			{
			if("2".equals(tableMap.get("fontEffect").toString())) {
                font.setBold(true);
            }
			if("3".equals(tableMap.get("fontEffect").toString())) {
                font.setItalic(true);
            }
			if("4".equals(tableMap.get("fontEffect").toString())){
				font.setBold(true);
				font.setItalic(true);
			}
			}
			if(tableMap.get("underLine")!=null)
				
			{
		    if("#fu[1]".equals(tableMap.get("underLine").toString())) {
                font.setUnderline((byte)1);
            }
			}
			if(tableMap.get("strikethru")!=null)
			{
		     if("#fs[1]".equals(tableMap.get("strikethru").toString())) {
                 font.setStrikeout(true);
             }
			}
            if(tableMap.get("fontSize")!=null) {
                font.setFontHeightInPoints(Short.parseShort((String)tableMap.get("fontSize")));
            }
			HSSFCellStyle cellStyle= workbook.createCellStyle();
			cellStyle.setFont(font);

//			row=sheet.createRow(n);
			row = sheet.getRow(n);
			if(row==null) {
                row=sheet.createRow(n);
            }
			csCell=row.createCell(Short.parseShort(String.valueOf(excelData[0].length/2)));
			csCell.setCellStyle(cellStyle);
//			csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
			csCell.setCellValue((String)tableMap.get("title"));
//			n++;
//			n++;
		}
		//写上表头
		if(excelData!=null&&excelData.length>0&&excelData[0].length>2)
		{
//			row=sheet.createRow(n);
			row = sheet.getRow(n);
			if(row==null) {
                row=sheet.createRow(n);
            }
			if(tableMap.get("lhead")!=null)
			{
				csCell=row.createCell(Short.parseShort("0"));
//				csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
				csCell.setCellValue((String)tableMap.get("lhead"));
			}
			if(tableMap.get("mhead")!=null)
			{
				csCell=row.createCell(Short.parseShort(String.valueOf(excelData[0].length/2)));
//				csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
				csCell.setCellValue((String)tableMap.get("mhead"));
			}
			if(tableMap.get("rhead")!=null)
			{
				csCell=row.createCell(Short.parseShort(String.valueOf(excelData[0].length-1)));
//				csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
				csCell.setCellValue((String)tableMap.get("rhead"));
			}
			n++;
		}
		
		return n;
		
	}
	public double strToDouble(String str){
		double values = 0;
		try{
			values = Double.parseDouble(str);
		}catch(Exception e){
			values = 0;
		}
		return values;
	}
	public int executeHeader(HashMap tableMap,String[][] excelData,HSSFSheet sheet,HSSFWorkbook workbook,int m)
	{  
		HSSFRow row=null;
		HSSFCell csCell=null;
		if(excelData!=null&&excelData.length>0&&excelData[0].length>2)
		{	
			if(tableMap!=null&&tableMap.size()!=0){//dml 2011-04-07 页面格式设置的时候如果初始化就会是数据库中 constant='SYS_OTH_PARAM'查询出的字段为空值，兼容一下
				String orgcolor=(String)tableMap.get("color");
				//Color corlor = new Color(Integer.parseInt(orgcolor.substring(1,3),16),Integer.parseInt(orgcolor.substring(3,5),16),Integer.parseInt(orgcolor.substring(5,7),16));
				HSSFFont font = workbook.createFont();			
				//font.setColor((short)corlor.getRGB());
				HSSFPalette palette=workbook.getCustomPalette();
				Color corlor = new Color(Integer.parseInt(orgcolor.substring(1,3),16),Integer.parseInt(orgcolor.substring(3,5),16),Integer.parseInt(orgcolor.substring(5,7),16));		
				HSSFColor  hssffColor=palette.findColor((byte)corlor.getRed(), (byte)corlor.getGreen(), (byte)corlor.getBlue());
				if(hssffColor!=null){
				    font.setColor(hssffColor.getIndex());// zzk 2014/1/20
				}
				font.setFontName((String)tableMap.get("fontfamilyname"));
				if("2".equals(tableMap.get("fontEffect").toString())) {
                    font.setBold(true);
                }
				if("3".equals(tableMap.get("fontEffect").toString())) {
                    font.setItalic(true);
                }
				if("4".equals(tableMap.get("fontEffect").toString())){
					font.setBold(true);
					font.setItalic(true);
				}
				if("#fu[1]".equals(tableMap.get("underLine").toString())) {
                    font.setUnderline((byte)1);
                }
				if("#fs[1]".equals(tableMap.get("strikethru").toString())) {
                    font.setStrikeout(true);
                }
				font.setFontHeightInPoints(Short.parseShort((String)tableMap.get("fontSize")));
				HSSFCellStyle cellStyle= workbook.createCellStyle();
				cellStyle.setFont(font);
				
	//			row=sheet.createRow(n);
				row = sheet.getRow(m);
				if(row==null) {
                    row=sheet.createRow(m);
                }
				csCell=row.createCell((short)0);
				csCell.setCellStyle(cellStyle);
	//			csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
				csCell.setCellValue((String)tableMap.get("lefthead"));
				
				csCell=row.createCell(Short.parseShort(String.valueOf(excelData[0].length/2)));
				csCell.setCellStyle(cellStyle);
	//			csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
				csCell.setCellValue((String)tableMap.get("centerhead"));
				
				csCell=row.createCell(Short.parseShort(String.valueOf(excelData[0].length-1)));
				csCell.setCellStyle(cellStyle);
	//			csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
				csCell.setCellValue((String)tableMap.get("righthead"));
	//		n++;
			}
		}
		return m;
	}
	
	/**
     * 设置excel表格效果
     * @param styles 设置不同的效果
     * @param workbook 新建的表格
     */
	public HSSFCellStyle style(HSSFWorkbook workbook,int styles){
		HSSFCellStyle style = workbook.createCellStyle();
		
		
		switch(styles){
		case 0:
				HSSFFont fonttitle = fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.black.font"),15);
				fonttitle.setBold(false);//加粗 
				style.setFont(fonttitle);
				style.setAlignment(HorizontalAlignment.LEFT );
		        break;			
		case 1:
				style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),10));
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);
				style.setVerticalAlignment(VerticalAlignment.CENTER);
				style.setAlignment(HorizontalAlignment.CENTER );
				break;
		case 2:
				style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),12));
				style.setAlignment(HorizontalAlignment.LEFT );
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);	                    	
				break;
		case 3:
				style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),12));
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);
				style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);               	
				break;		
		case 4:
				style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),12));
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);
				style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				style.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
			  break;
		default:		
				style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),12));
				style.setAlignment(HorizontalAlignment.LEFT );
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);	  
				break;
		}
		return style;
	}
	/**
     * 设置excel字体效果
     * @param fonts 设置不同的字体
     * @param size 设置字体的大小
     * @param workbook 新建的表格
     */
	public HSSFFont fonts(HSSFWorkbook workbook,String fonts,int size){
		HSSFFont font = workbook.createFont();
		font.setFontHeightInPoints((short)size);
		font.setFontName(fonts);
		return font;
	}
	//得到excel数据域
	public String[][] getExcelData(ArrayList list)
	{
		ArrayList fieldList=(ArrayList)list.get(0);
		HashMap   fieldHzMap=(HashMap)list.get(1);
		HashMap   fieldTypeMap=(HashMap)list.get(2);
		HashMap   fieldCodeMap=(HashMap)list.get(3);
		HashMap   fieldLenMap=(HashMap)list.get(4);
		HashMap   fieldDwMap=(HashMap)list.get(5);
		
		String[][] data=null;
	
		/*Connection a_conn =null;	
		ResultSet resultSet=null;
		Statement stmt=null;*/
		RowSet resultSet=null;
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			HashMap namemap=this.getDbname();
			
			String hireMajor="";
			boolean hireMajorIsCode=false;
			FieldItem hireMajoritem=null;
			if("zp_test_template".equalsIgnoreCase(tableName) || "personnelEmploy".equalsIgnoreCase(tableName)) {
    			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.conn,"1");
    			HashMap map=parameterXMLBo.getAttributeValues();
    			if(map.get("hireMajor")!=null) {
                    hireMajor=(String)map.get("hireMajor");  //招聘专业指标
                }
    			if(hireMajor.length()>0)
    			{
    				hireMajoritem=DataDictionary.getFieldItem(hireMajor.toLowerCase());
    				if(hireMajoritem.getCodesetid().length()>0&&!"0".equals(hireMajoritem.getCodesetid())) {
                        hireMajorIsCode=true;
                    }
    			}
			}
			
			int rows=0;
			String sql="";
			if("1".equalsIgnoreCase(this.operateObject)) {
                sql="select count(z0301) counts from "+tableName;
            } else
			{
				if(!"z05".equalsIgnoreCase(tableName)&&!"zp_test_template".equalsIgnoreCase(tableName)
						&&!"P05".equalsIgnoreCase(tableName)&&!"P01".equalsIgnoreCase(tableName)
						&&!"r07".equalsIgnoreCase(tableName)&&!"r04".equalsIgnoreCase(tableName))
				{
					String sqss="";
					if("r31".equalsIgnoreCase(tableName)){
						sqss=this.whl_sql.substring(this.whl_sql.lastIndexOf(" from"));		
					}else{
						sqss=this.whl_sql.substring(this.whl_sql.indexOf(" from"));		
					}
					if(sqss.indexOf(" order ")!=-1)
					{
						sql="select count(*) counts "+sqss.substring(0,sqss.indexOf(" order "));
					}
					else {
                        sql="select count(*) counts "+sqss;
                    }
				}else if("r07".equalsIgnoreCase(tableName)|| "r04".equalsIgnoreCase(tableName)){
					String sqss="";
					if("r07".equalsIgnoreCase(tableName)) {
                        sqss=this.whl_sql.substring(this.whl_sql.indexOf(" from r07"));
                    } else if("r04".equalsIgnoreCase(tableName)) {
                        sqss=this.whl_sql.substring(this.whl_sql.indexOf(" from r04"));
                    }
					if(sqss.indexOf("order")!=-1) {
                        sql="select count(*) counts "+sqss.substring(0,sqss.indexOf(" order "));
                    } else {
                        sql="select count(*) counts "+sqss;
                    }
				}else
				{
					String sqss=this.whl_sql.substring(this.whl_sql.indexOf(" from"));	
					if(sqss.indexOf("order")!=-1) {
                        sql="select count(*) counts "+sqss.substring(0,sqss.indexOf(" order "));
                    } else {
                        sql="select count(*) counts "+sqss;
                    }
				}
			}	
			resultSet=dao.search(sql);
			if(resultSet.next()) {
                rows=resultSet.getInt("counts");
            }
			data=new String[rows+1][fieldList.size()];
			for(int i=0;i<fieldList.size();i++)
			{
				data[0][i]=(String)fieldHzMap.get((String)fieldList.get(i));
				String str3 = (String)fieldHzMap.get((String)fieldList.get(i));
				int longs = str3.length();
				numberMap.put(String.valueOf(i),String.valueOf(longs));
			}
			if("1".equalsIgnoreCase(this.operateObject)) {
                resultSet=dao.search("select * from "+tableName+" "+this.whl_sql);
            } else {
                resultSet=dao.search(this.whl_sql);
            }
			int j=1;
			SimpleDateFormat ss=new SimpleDateFormat("yyyy-MM-dd");
			while(resultSet.next())
			{
				for(int i=0;i<fieldList.size();i++)
				{
					String fieldName=(String)fieldList.get(i);
					if("resume".equalsIgnoreCase(tableName))
					{
						String a0100=resultSet.getString("a0100");
						String key = (a0100+fieldName).toUpperCase();
						if(this.subSetMap.get(key)!=null)
						{
							data[j][i]=(String)this.subSetMap.get(key);
							String str2=(String)this.subSetMap.get(key);
							str2 = StringUtils.isEmpty(str2) ? "" : str2;
							String[] sss = str2.split("\n");
							
							String aa = "";
							int aal =0;
							int aas =0;
							for(int z=0;z<sss.length;z++)
							{
								 aa = sss[z];	
								  aal=aa.length();
								  if(aas<aal) {
                                      aas =aal;
                                  }
							}
							data[j][i]=str2;
							int longs = str2.length();
							if(numberMap.get(String.valueOf(i))!=null&&Integer.parseInt((String)numberMap.get(String.valueOf(i)))<aas)
							{numberMap.put(String.valueOf(i),String.valueOf(aas));}
							continue;
						}
					}
					if("recidx".equals(fieldName))
					{  
						data[j][i]=String.valueOf(j);
						int longs = (int)String.valueOf(j).length();
						if(numberMap.get(String.valueOf(i))!=null&&Integer.parseInt((String)numberMap.get(String.valueOf(i)))<longs)
						{numberMap.put(String.valueOf(i),String.valueOf(longs));}
					}
					else
					{
						boolean  isNotNull=false;
						if(!"sendmail".equalsIgnoreCase(fieldName))
						{
							
							if((!"D".equalsIgnoreCase((String)fieldTypeMap.get(fieldName))&&!"M".equalsIgnoreCase((String)fieldTypeMap.get(fieldName)))&&!"employedcount".equals(fieldName)&&resultSet.getString(fieldName)!=null)
							{
								isNotNull=true;
							}
							else if("D".equalsIgnoreCase((String)fieldTypeMap.get(fieldName)))
							{
								if("z03".equals(fieldName.substring(0,3))&&resultSet.getDate(fieldName)!=null)
								{
									isNotNull=true;
								}
								else if(resultSet.getDate(fieldName)!=null) {
                                    isNotNull=true;
                                }
							}
							else if("M".equalsIgnoreCase((String)fieldTypeMap.get(fieldName)))
							{
								isNotNull=true;
							}
						}
						
						if("employedcount".equals(fieldName)) {
                            isNotNull=true;
                        }
						
						if(isNotNull)
						{
							if("A".equalsIgnoreCase((String)fieldTypeMap.get(fieldName))&&!"0".equalsIgnoreCase((String)fieldCodeMap.get(fieldName)))
							{
									data[j][i]=AdminCode.getCode((String)fieldCodeMap.get(fieldName),resultSet.getString(fieldName))!=null?AdminCode.getCode((String)fieldCodeMap.get(fieldName),resultSet.getString(fieldName)).getCodename():"";
									String str=AdminCode.getCode((String)fieldCodeMap.get(fieldName),resultSet.getString(fieldName))!=null?AdminCode.getCode((String)fieldCodeMap.get(fieldName),resultSet.getString(fieldName)).getCodename():"";
									str = StringUtils.isEmpty(str) ? "" : str;
									String[] sss = str.split("\n");
									String aa = "";
									int aal =0;
									int aas =0;
									for(int z=0;z<sss.length;z++)
									{
										 aa = sss[z];	
										  aal=aa.length();
										  if(aas<aal) {
                                              aas =aal;
                                          }
									}
									data[j][i]=str;
									int longs = str.length();
									if(numberMap.get(String.valueOf(i))!=null&&Integer.parseInt((String)numberMap.get(String.valueOf(i)))<aas)
									{numberMap.put(String.valueOf(i),String.valueOf(aas));}
									if("zp_test_template".equalsIgnoreCase(tableName)&& "zp_pos_id".equalsIgnoreCase(fieldName)&&hireMajor!=null&&hireMajor.length()>0&&resultSet.getString("Z0336")!=null&& "01".equals(resultSet.getString("Z0336")))
									{
										String value="";
										if(hireMajorIsCode) {
                                            value=AdminCode.getCodeName(hireMajoritem.getCodesetid(),resultSet.getString(fieldName));
                                        } else {
                                            value=resultSet.getString(fieldName);
                                        }
										data[j][i]=value;
									}
									if("personnelEmploy".equalsIgnoreCase(tableName)&& "z0311".equalsIgnoreCase(fieldName)&&hireMajor!=null&&hireMajor.length()>0&&resultSet.getString("Z0336")!=null&& "01".equals(resultSet.getString("Z0336")))
									{
										String value="";
										if(hireMajorIsCode) {
                                            value=AdminCode.getCodeName(hireMajoritem.getCodesetid(),resultSet.getString(fieldName));
                                        } else {
                                            value=resultSet.getString(fieldName);
                                        }
										data[j][i]=value;
									}
									
									if("r01,r10,r04,r07,r11,r13".indexOf(this.tableName)!=-1)//培训体系到处Excel时候，当单位为公共资源，这样处理，否则到处为空单元格。fzg修改
									{									    
									    if("b0110".equalsIgnoreCase(fieldName) && "HJSJ".equalsIgnoreCase(resultSet.getString(fieldName))) {
                                            data[j][i]="公共资源";
                                        }
									}
							}
							else
							{
								if("N".equalsIgnoreCase((String)fieldTypeMap.get(fieldName)))
								{
									
									if("engagePlan".equalsIgnoreCase(tableName)&& "z0117".equalsIgnoreCase(fieldName))  //实招人数 指标
									{
										data[j][i]=(String)this.planCountMap.get(resultSet.getString("z0101"));
									}
									else if("posList".equalsIgnoreCase(tableName)&& "employedcount".equalsIgnoreCase(fieldName))  //实招人数 指标
									{
										data[j][i]=(String)this.planCountMap.get(resultSet.getString("z0301"));
									}
									else
									{
										if(resultSet.getFloat(fieldName)==0)
										{
											data[j][i]=" ";
										}
										else{
											String valuestr = resultSet.getString(fieldName);
											String dwstr = fieldDwMap.get(fieldName).toString();
											if(dwstr!=null&&dwstr.trim().length()>0){
												int dw = Integer.parseInt(dwstr);
												if(dw>0){
													BigDecimal b = new BigDecimal(valuestr);   
													BigDecimal values = new BigDecimal("1");
													data[j][i]=b.divide(values,dw,BigDecimal.ROUND_HALF_UP).doubleValue()+"";
												}else{
													if(valuestr.indexOf(".")!=-1) {
                                                        data[j][i]=valuestr.substring(0,valuestr.indexOf("."));
                                                    } else{
														data[j][i]=valuestr;
													}
												}
											}else{
												data[j][i]=valuestr;
											}
										}	
									}	
								}
								else if("M".equalsIgnoreCase((String)fieldTypeMap.get(fieldName)))
								{
									String str = Sql_switcher.readMemo(resultSet,fieldName);
									str=str!=null?str:"";
									str=str.replaceAll("%26lt;","<").replaceAll("%26gt;",">");
									//if(!tableName.equalsIgnoreCase("z05"))
									if("p0509".equalsIgnoreCase(fieldName))
									{
										
										str=str.replaceAll("<span style=\"color:#3333cc\">", "");
										str=str.replaceAll("</span>", "").replaceAll("<br>", "\n");
										str=str.replaceAll("&nbsp;", "");
										data[j][i]=str;
									}else{
										if("P05".equalsIgnoreCase(tableName)){
											str=str.replaceAll("<br>","\n");
										}
										
										String[] sss = str.split("\n");
					
										String aa = "";
										int aal =0;
										int aas =0;
										for(int z=0;z<sss.length;z++)
										{
											 aa = sss[z];	
											  aal=aa.length();
											  if(aas<aal) {
                                                  aas =aal;
                                              }
										}
										data[j][i]=str;
										int longs = str.length();
										if(numberMap.get(String.valueOf(i))!=null&&Integer.parseInt((String)numberMap.get(String.valueOf(i)))<aas)
										{numberMap.put(String.valueOf(i),String.valueOf(aas));}
									}
								}
								else
								{
									if(!"z05".equalsIgnoreCase(tableName))
									{
										if(!"D".equalsIgnoreCase((String)fieldTypeMap.get(fieldName))){
											String str = resultSet.getString(fieldName);
											str=str!=null?str:"";
											str=str.replaceAll("%26lt;","<").replaceAll("%26gt;",">");
											data[j][i]=str;
											int longs = str.length();
											if(numberMap.get(String.valueOf(i))!=null&&Integer.parseInt((String)numberMap.get(String.valueOf(i)))<longs)
											{numberMap.put(String.valueOf(i),String.valueOf(longs));}
										}else
										{
											if("z03".equals(fieldName.substring(0,3))&& "z0307".equalsIgnoreCase(fieldName))
											{
												if(resultSet.getDate(fieldName)!=null) {
                                                    data[j][i]=ss.format(resultSet.getDate(fieldName));
                                                } else {
                                                    data[j][i]="";
                                                }
											}
											else
											{
												
												data[j][i]=ss.format(resultSet.getDate(fieldName));
												String str=ss.format(resultSet.getDate(fieldName));
												int longs = str.length();
												if(numberMap.get(String.valueOf(i))!=null&&Integer.parseInt((String)numberMap.get(String.valueOf(i)))<longs)
												{numberMap.put(String.valueOf(i),String.valueOf(longs));}
											}
										}
										
									}								
									else
									{
										if("z0509".equalsIgnoreCase(fieldName))
										{
											SimpleDateFormat ssd=new SimpleDateFormat("yyyy-MM-dd HH:mm");
											data[j][i]=ssd.format(resultSet.getDate(fieldName));
											
										}
									    else if("z0505".equalsIgnoreCase(fieldName)|| "z0507".equalsIgnoreCase(fieldName))
										{
											StringBuffer z0505=new StringBuffer("");
											StringBuffer z0507=new StringBuffer("");
											String a_z0505=resultSet.getString("z0505");
											String a_z0507=resultSet.getString("z0507");
											if(a_z0505!=null&&a_z0505.trim().length()>0)
											{
												String[] aa_z0505=a_z0505.split(",");
												int a=0;
												while(a<aa_z0505.length)
												{
													if(namemap.get(aa_z0505[a].substring(0,3).toUpperCase())!=null) {
                                                        z0505.append(","+(String)this.employerNameMap.get(aa_z0505[a].toUpperCase()));
                                                    } else{
														z0505.append(","+(String)this.employerNameMap.get("USR"+aa_z0505[a].toUpperCase()));
													}
													a++;
												}
											}
											if(a_z0507!=null&&a_z0507.trim().length()>0)
											{
												String[] aa_z0507=a_z0507.split(",");
												int a=0;
												while(a<aa_z0507.length)
												{
													if(namemap.get(aa_z0507[a].substring(0,3).toUpperCase())!=null) {
                                                        z0507.append(","+(String)this.employerNameMap.get(aa_z0507[a].toUpperCase()));
                                                    } else{
														z0507.append(","+(String)this.employerNameMap.get("USR"+aa_z0507[a].toUpperCase()));
													}
													a++;
												}
											}	
											
											if("z0505".equalsIgnoreCase(fieldName))
											{
												if(z0505.length()>1) {
                                                    data[j][i]=z0505.substring(1);
                                                } else {
                                                    data[j][i]=z0505.toString();
                                                }
											}
											else if("z0507".equalsIgnoreCase(fieldName))
											{
												if(z0507.length()>1) {
                                                    data[j][i]=z0507.substring(1);
                                                } else {
                                                    data[j][i]=z0507.toString();
                                                }
											}
										}else if("z0511".equalsIgnoreCase(fieldName))
										{
											String context="";
											if(resultSet.getString("z0511")!=null)
											{
												String tt=resultSet.getString("z0511");
												
												if ("`".equals(tt)) {
                                                    tt = "";
                                                }
												
												if(tt.indexOf("`")!=-1)
												{
													context = this.getName(tt.substring(0,3).toUpperCase(),tt.substring(4));
												
												}
												else
												{
													context=tt;
												}
											}
											data[j][i]=context;		
										}
										else {
                                            data[j][i]=resultSet.getString(fieldName);
                                        }
									}
									
								}
							}
						
						}
						else
						{
							if(fieldCodeMap.get(fieldName)!=null&& "36".equals((String)fieldCodeMap.get(fieldName)))
							{
								data[j][i]="未选";
							}
							else {
                                data[j][i]=" ";
                            }
						}
					}

				}
				j++;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		/*finally
		{
			
			try
			{
				if(resultSet!=null)
				{
					resultSet.close();
					resultSet=null;
				}
				if(stmt!=null)
				{
					stmt.close();
					stmt=null;
				}
				if(a_conn!=null)
				{
					a_conn.close();
					a_conn=null;
				}
				System.gc();
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}*/
		return data;
	}
	
	public String getName(String pre,String a0100)
	{
		String name="";
		try
		{
			String sql = " select a0101 from "+pre+"a01 where a0100='"+a0100+"'";
			ContentDAO dao= new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				name=rs.getString("a0101")==null?"":rs.getString("a0101");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return name;
	}
	
	//得到花名册的标题 上表头 下表头信息
	public HashMap getTableExtendInfo()
	{
		HashMap musterInfo=new HashMap();
		String title="";
		if(this.paramtervo==null||this.paramtervo.getTitle_fw()==null)
		{
			if("Z03".equalsIgnoreCase(tableName)) {
                title=ResourceFactory.getProperty("lable.hiremanage.positionRequest");
            }
			if("zp_pos_tache".equalsIgnoreCase(tableName)) {
                title=ResourceFactory.getProperty("hire.fileout.personnelFilterList");
            }
			if("z05".equalsIgnoreCase(tableName)) {
                title=ResourceFactory.getProperty("hire.fileout.interviewArrangeInfo");
            }
			if("zp_test_template".equalsIgnoreCase(tableName)) {
                title=ResourceFactory.getProperty("hire.fileout.interviewExamInfo");
            }
			if("personnelEmploy".equalsIgnoreCase(tableName)) {
                title=ResourceFactory.getProperty("hire.fileout.personnelEmployList");
            }
			if("engagePlan".equalsIgnoreCase(tableName)) {
                title=ResourceFactory.getProperty("hire.fileout.engagePlanList");
            }
			if("r31".equalsIgnoreCase(tableName)) {
                title="培训班";
            }
			if("resume".equalsIgnoreCase(tableName)) {
                title="人员简历信息";
            }
			if("r01".equalsIgnoreCase(tableName)) {
                title="培训机构";
            } else if("r04".equalsIgnoreCase(tableName)) {
                title="培训教师";
            } else if("r10".equalsIgnoreCase(tableName)) {
                title="培训场所";
            } else if("r11".equalsIgnoreCase(tableName)) {
                title="培训设施";
            } else if("r07".equalsIgnoreCase(tableName)) {
                title="培训资料";
            } else if("r25".equalsIgnoreCase(tableName)) {
                title="培训计划";
            }
			    
		}else{
			title=this.paramtervo.getTitle_fw();
			
			if(title==null||title.trim().length()==0)
			{
				if("Z03".equalsIgnoreCase(tableName)) {
                    title=ResourceFactory.getProperty("lable.hiremanage.positionRequest");
                }
				if("zp_pos_tache".equalsIgnoreCase(tableName)) {
                    title=ResourceFactory.getProperty("hire.fileout.personnelFilterList");
                }
				if("z05".equalsIgnoreCase(tableName)) {
                    title=ResourceFactory.getProperty("hire.fileout.interviewArrangeInfo");
                }
				if("zp_test_template".equalsIgnoreCase(tableName)) {
                    title=ResourceFactory.getProperty("hire.fileout.interviewExamInfo");
                }
				if("personnelEmploy".equalsIgnoreCase(tableName)) {
                    title=ResourceFactory.getProperty("hire.fileout.personnelEmployList");
                }
				if("engagePlan".equalsIgnoreCase(tableName)) {
                    title=ResourceFactory.getProperty("hire.fileout.engagePlanList");
                }
				if("r31".equalsIgnoreCase(tableName)) {
                    title="培训班";
                }
				if("resume".equalsIgnoreCase(tableName)) {
                    title="人员简历信息";
                }
				if("r01".equalsIgnoreCase(tableName)) {
                    title="培训机构";
                } else if("r04".equalsIgnoreCase(tableName)) {
                    title="培训教师";
                } else if("r10".equalsIgnoreCase(tableName)) {
                    title="培训场所";
                } else if("r11".equalsIgnoreCase(tableName)) {
                    title="培训设施";
                } else if("r07".equalsIgnoreCase(tableName)) {
                    title="培训资料";
                } else if("r25".equalsIgnoreCase(tableName)) {
                    title="培训计划";
                }
			}
			
			String t_fontfamilyname=ResourceFactory.getProperty("hmuster.label.fontSt");
	        String t_fontEffect="0";
	        int    t_fontSize=17;  
	        String t_color="#000000";       
	        String  t_underLine="#fu[0]";
	        String  t_strikethru="#fs[0]";
	        
	        if(this.paramtervo!=null)
	        {
	        	if(this.paramtervo.getTitle_fn().length()>0) {
                    t_fontfamilyname=this.paramtervo.getTitle_fn();
                }
		       	 t_fontEffect=getFontEffect(4);
		       	 if(this.paramtervo.getTitle_fz().length()>0) {
                     t_fontSize=Integer.parseInt(this.paramtervo.getTitle_fz());
                 }
		       	 if(this.paramtervo.getTitle_fc().length()>0) {
                     t_color=this.paramtervo.getTitle_fc();
                 }
		       	 if(this.paramtervo.getTitle_fu()!=null&&this.paramtervo.getTitle_fu().length()>0) {
                     t_underLine=this.paramtervo.getTitle_fu();
                 }
		       	if(this.paramtervo.getTile_fs()!=null&&this.paramtervo.getTile_fs().length()>0) {
                    t_strikethru=this.paramtervo.getTile_fs();
                }

	        }
	        musterInfo.put("fontfamilyname",t_fontfamilyname);
	        musterInfo.put("fontEffect",t_fontEffect);
	        musterInfo.put("fontSize",t_fontSize+"");
	        musterInfo.put("color",t_color);
	        musterInfo.put("underLine",t_underLine);
	        musterInfo.put("strikethru",t_strikethru);
		}
		musterInfo.put("title",title);
		return musterInfo;
	}
	
	
	public HashMap getTopHeadExtendInfo(){
		HashMap musterInfo=new HashMap();
		if(this.paramtervo!=null){//dml 2011-04-07
			if(this.paramtervo.getHead_flw()!=null&&this.paramtervo.getHead_flw().length()>1){
				musterInfo.put("lefthead",codeChange(this.paramtervo.getHead_flw()));
			}
			if(this.paramtervo.getHead_fmw()!=null&&this.paramtervo.getHead_fmw().length()>1){
				musterInfo.put("centerhead",codeChange(this.paramtervo.getHead_fmw()));
			}
			
		   if(this.paramtervo.getHead_frw()!=null&&this.paramtervo.getHead_frw().length()>1){
			   musterInfo.put("righthead",codeChange(this.paramtervo.getHead_frw()));
			}
		   try {
			String datestr = musterInfo.get("lefthead").toString();
			   if("&[YYYY年YY月]".equalsIgnoreCase(datestr)){
				   SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");
				   datestr = sdf.format(new Date());
				   musterInfo.put("lefthead", datestr);
			   }
			   datestr = musterInfo.get("centerhead").toString();
			   if("&[YYYY年YY月]".equalsIgnoreCase(datestr)){
				   SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");
				   datestr = sdf.format(new Date());
				   musterInfo.put("centerhead", datestr);
			   }
			   datestr = musterInfo.get("righthead").toString();
			   if("&[YYYY年YY月]".equalsIgnoreCase(datestr)){
				   SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");
				   datestr = sdf.format(new Date());
				   musterInfo.put("righthead", datestr);
			   }
		} catch (Exception e) {
		}
		   String h_fontfamilyname=ResourceFactory.getProperty("hmuster.label.fontSt");
	       String h_fontEffect="0";
	       int    h_fontSize=8;    
	       String h_color="#000000";
	       String  h_underLine="#fu[0]";
	       String  h_strikethru="#fs[0]";
	       int totalWidth=0;
	       if(this.paramtervo!=null){
				if(this.paramtervo.getHead_fn().length()>0) {
                    h_fontfamilyname=this.paramtervo.getHead_fn();
                }
		       	 h_fontEffect=getFontEffect(1);
		       	if(this.paramtervo.getHead_fz().length()>0) {
                    h_fontSize=Integer.parseInt(this.paramtervo.getHead_fz());
                }
		       	if(this.paramtervo.getHead_fc().length()>0) {
                    h_color=this.paramtervo.getHead_fc();
                }
		       	if(this.paramtervo.getHead_fu()!=null&&this.paramtervo.getHead_fu().length()>0) {
                    h_underLine=this.paramtervo.getHead_fu();
                }
		       	if(this.paramtervo.getHead_fs()!=null&&this.paramtervo.getHead_fs().length()>0) {
                    h_strikethru=this.paramtervo.getHead_fs();
                }
		        if(this.paramtervo.getHead_flw()!=null&&this.paramtervo.getHead_flw().length()>1) {
                    totalWidth+=this.paramtervo.getHead_flw().getBytes().length/2*(h_fontSize+4);
                }
		      	if(this.paramtervo.getHead_fmw()!=null&&this.paramtervo.getHead_fmw().length()>1) {
                    totalWidth+=this.paramtervo.getHead_fmw().getBytes().length/2*(h_fontSize+4);
                }
		      	if(this.paramtervo.getHead_frw()!=null&&this.paramtervo.getHead_frw().length()>1) {
                    totalWidth+=this.paramtervo.getHead_frw().getBytes().length/2*(h_fontSize+4);
                }
	       }
	      
	       musterInfo.put("fontfamilyname",h_fontfamilyname);
	       musterInfo.put("fontEffect",h_fontEffect);
	       musterInfo.put("fontSize",h_fontSize+"");
	       musterInfo.put("color",h_color);
	       musterInfo.put("underLine",h_underLine);
	       musterInfo.put("strikethru",h_strikethru);
	       musterInfo.put("totalWidth",totalWidth+"");
		}
       return musterInfo;
	}
	public HashMap getButtomHeadExtendInfo(){
		HashMap musterInfo=new HashMap();
		if(this.paramtervo!=null){//dml 2011-04-07
			if(this.paramtervo.getTile_flw()!=null&&this.paramtervo.getTile_flw().length()>1)
			{
				musterInfo.put("lefthead",codeChange(this.paramtervo.getTile_flw()));
			}
			if(this.paramtervo.getTile_fmw()!=null&&this.paramtervo.getTile_fmw().length()>1)
			{
				musterInfo.put("centerhead",codeChange(this.paramtervo.getTile_fmw()));
			}
		    if(this.paramtervo.getTile_frw()!=null&&this.paramtervo.getTile_frw().length()>1)
			{
		    	musterInfo.put("righthead",codeChange(this.paramtervo.getTile_frw()));
			}
		    try {
				String datestr = musterInfo.get("lefthead").toString();
				if ("&[YYYY年YY月]".equalsIgnoreCase(datestr)) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");
					datestr = sdf.format(new Date());
					musterInfo.put("lefthead", datestr);
				}
				datestr = musterInfo.get("centerhead").toString();
				   if("&[YYYY年YY月]".equalsIgnoreCase(datestr)){
					   SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");
					   datestr = sdf.format(new Date());
					   musterInfo.put("centerhead", datestr);
				   }
				   datestr = musterInfo.get("righthead").toString();
				   if("&[YYYY年YY月]".equalsIgnoreCase(datestr)){
					   SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");
					   datestr = sdf.format(new Date());
					   musterInfo.put("righthead", datestr);
				   }
			} catch (Exception e) {
			}
			//	  写页尾
			String f_fontfamilyname="宋体";
	        String f_fontEffect="0";
	        int    f_fontSize=8;  
	        int    h_fontSize=8; 
	        String f_color="#000000";
	        String  f_underLine="#fu[0]";
	        String  f_strikethru="#fs[0]";
	        int totalWidth=0;
			if(this.paramtervo!=null)
	        {
				if(this.paramtervo.getTile_fn().length()>0) {
                    f_fontfamilyname=this.paramtervo.getTile_fn();
                }
		       	 f_fontEffect=getFontEffect(2);
		       	 if(this.paramtervo.getTile_fz().length()>0) {
                     f_fontSize=Integer.parseInt(this.paramtervo.getTile_fz());
                 }
		       	if(this.paramtervo.getHead_fz().length()>0) {
                    h_fontSize=Integer.parseInt(this.paramtervo.getHead_fz());
                }
		       	 if(this.paramtervo.getTile_fc().length()>0) {
                     f_color=this.paramtervo.getTile_fc();
                 }
		    	if(this.paramtervo.getTile_fu()!=null&&this.paramtervo.getTile_fu().length()>0) {
                    f_underLine=this.paramtervo.getTile_fu();
                }
		       	if(this.paramtervo.getTile_fs()!=null&&this.paramtervo.getTile_fs().length()>0) {
                    f_strikethru=this.paramtervo.getTile_fs();
                }
		       	
		       	if(this.paramtervo.getTile_flw()!=null&&this.paramtervo.getTile_flw().length()>1)
		       	{
		       		totalWidth+=this.paramtervo.getTile_flw().getBytes().length/2*(h_fontSize+4);
		       	}
		       	if(this.paramtervo.getTile_fmw()!=null&&this.paramtervo.getTile_fmw().length()>1)
				{
		       		totalWidth+=this.paramtervo.getTile_fmw().getBytes().length/2*(h_fontSize+4);
				}
		       	if(this.paramtervo.getTile_frw()!=null&&this.paramtervo.getTile_frw().length()>1)
				{
		        	totalWidth+=this.paramtervo.getTile_frw().getBytes().length/2*(h_fontSize+4);
				}
	        }
			musterInfo.put("fontfamilyname",f_fontfamilyname);
		       musterInfo.put("fontEffect",f_fontEffect);
		       musterInfo.put("fontSize",f_fontSize+"");
		       musterInfo.put("color",f_color);
		       musterInfo.put("underLine",f_underLine);
		       musterInfo.put("strikethru",f_strikethru);
		       musterInfo.put("totalWidth",totalWidth+"");
		}
		return musterInfo;
	}
	
	/*
	public RecordVo getLnameInfo(String tabid)
	{
		ContentDAO dao = new ContentDAO(this.conn);
		RecordVo vo=null;
		RowSet rowSet = null;
		try
		{ 
			rowSet=dao.search("select * from lname where tabid="+tabid);
			if(rowSet.next())
			{
				vo=new RecordVo("lname");
				vo.setInt("tabid",Integer.parseInt(tabid));
				vo.setString("hzname",rowSet.getString("hzname"));
				vo.setString("title",rowSet.getString("title"));
				vo.setString("lhead",rowSet.getString("lhead"));
				vo.setString("mhead",rowSet.getString("mhead"));
				vo.setString("rhead",rowSet.getString("rhead"));
				vo.setString("lfoot",rowSet.getString("lfoot"));
				vo.setString("mfoot",rowSet.getString("mfoot"));
				vo.setString("rfoot",rowSet.getString("rfoot"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return vo;
	}*/
	
	
	
	
	
	/**
	 * 取得花名册的列信息
	 * @param tabid
	 * @return
	 */
	public ArrayList getTableField(ArrayList a_fieldList)
	{
		ArrayList list=new ArrayList();
		ArrayList fieldList=new ArrayList();
		HashMap   fieldHzMap=new HashMap();
		HashMap   fieldTypeMap=new HashMap();
		HashMap   fieldCodeMap=new HashMap();
		HashMap   fieldLenMap=new HashMap();
		HashMap   fieldDwMap=new HashMap();
		ArrayList fileList=new ArrayList();
		FieldItem fileitem = new FieldItem();
		fileitem.setItemtype("A");
		fileitem.setItemdesc("序列");
		fileList.add(fileitem);

		try
		{
			if(!"3".equals(this.operateObject)){
				fieldList.add("recidx");
				fieldHzMap.put("recidx",ResourceFactory.getProperty("recidx.label"));
				fieldTypeMap.put("recidex","A");
				fieldCodeMap.put("recidx","0");
				fieldLenMap.put("recidx","10");
				fieldDwMap.put("recidx","0");
			}
			
			String field_name="";
			for(int i=0;i<a_fieldList.size();i++)
			{
				FieldItem item=(FieldItem)a_fieldList.get(i);
				Field field=(Field)item.cloneField();
				//if(item.getItemid().equalsIgnoreCase("z0301")||item.getItemid().equalsIgnoreCase("z0101"))
					//continue;
				//去掉计划表审批方式一列
				if("r2512".equalsIgnoreCase(item.getItemid())|| "R2512".equalsIgnoreCase(item.getItemid())) {
                    continue;
                }
				
				field_name=item.getItemid();
				String colHz=item.getItemdesc();
				fieldHzMap.put(field_name,colHz);			
				fieldList.add(field_name);
				fieldTypeMap.put(field_name,item.getItemtype());
				fieldCodeMap.put(field_name,item.getCodesetid());
				fieldLenMap.put(field_name,item.getItemlength()+"");
				fieldDwMap.put(field_name,item.getDecimalwidth()+"");
				fileList.add(item);
			}
			list.add(fieldList);
			list.add(fieldHzMap);
			list.add(fieldTypeMap);
			list.add(fieldCodeMap);
			list.add(fieldLenMap);
			list.add(fieldDwMap);
			list.add(fileList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	
	public static void main(String[] args)
	{
		FileOutputStream fileOut = null;
		HSSFWorkbook wb = null;
		 try {
		 	wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("new sheet");
			HSSFRow row = sheet.getRow((short) 1);
			if(row==null) {
                row = sheet.createRow((short) 1);
            }

			HSSFCell cell = row.createCell((short) 1);
			cell.setCellValue("This is a test of merging");

			HSSFCell csCell =row.createCell((short)5);
			csCell.setCellValue("fffff");


			HSSFCellStyle style = wb.createCellStyle();
			style.setBorderBottom(BorderStyle.THIN);
			style.setBottomBorderColor(HSSFColor.BLACK.index);
			style.setBorderLeft(BorderStyle.THIN);
			style.setLeftBorderColor(HSSFColor.GREEN.index);
			style.setBorderRight(BorderStyle.THIN);
			style.setRightBorderColor(HSSFColor.BLUE.index);
			style.setBorderTop(BorderStyle.MEDIUM_DASHED);
			style.setTopBorderColor(HSSFColor.BLACK.index);
			cell.setCellStyle(style);
			cell = row.createCell((short)2);
			cell.setCellStyle(style);

			ExportExcelUtil.mergeCell(sheet, 1,(short)5,3,(short)5);
			ExportExcelUtil.mergeCell(sheet, 1,(short)1,1,(short)4);

			// Write the output to a file
			fileOut = new FileOutputStream("d:\\workbook2.xls");
			wb.write(fileOut);
			fileOut.close();
		 }
		 catch (Exception e) {
			 System.out.println("已运行 xlCreate() : " + e);
		 }
		 finally {
		 	PubFunc.closeResource(fileOut);
		 	PubFunc.closeResource(wb);
		 }
	
	
	}

	public String codeChange(String code) {
	    code = StringUtils.isEmpty(code) ? "" : code;
		String[] code1 = code.split("&");
		String codeitem = "";
		for (int i = 0; i < code1.length; i++) {
			String codeitem1 = "";
			if ("[页码]".equalsIgnoreCase(code1[i])) {
				codeitem1 = "第" + pageno + "页";
			} else if ("[总人数]".equalsIgnoreCase(code1[i])) {
				codeitem1 = "总共" + total + "人";
			} else if ("[制作人]".equalsIgnoreCase(code1[i])) {
				codeitem1 = producer + "";
			} else if ("[日期]".equalsIgnoreCase(code1[i])) {
				codeitem1 = datetime;
			} else if ("[时间]".equalsIgnoreCase(code1[i])) {
				codeitem1 = times;
			} else if ("[YYYY年YY月]".equalsIgnoreCase(code1[i])) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");
				codeitem1 = sdf.format(new Date());
			} else {
				codeitem1 = code1[i];
			}
			codeitem += codeitem1;
		}
		return codeitem;
	}
	//取得字形
	// 1：页头  2页尾 3：正文字体 4:标题 
	public String getFontEffect(int flag)
	{
		String fontEffect="0";
		if(this.paramtervo!=null)
		{   if(flag==3)
			{
				if("#fi[1]".equals(this.paramtervo.getBody_fi())&& "#fb[1]".equals(this.paramtervo.getBody_fb())) {
                    fontEffect="4";  //粗斜体
                } else if("#fb[1]".equals(this.paramtervo.getBody_fb())) {
                    fontEffect="2";  //粗体
                } else if("#fi[1]".equals(this.paramtervo.getBody_fi())) {
                    fontEffect="3";  //斜体
                }
			}
			else if(flag==4)
			{
				if("#fi[1]".equals(this.paramtervo.getTitle_fi())&& "#fb[1]".equals(this.paramtervo.getTitle_fb())) {
                    fontEffect="4";  //粗斜体
                } else if("#fb[1]".equals(this.paramtervo.getTitle_fi())) {
                    fontEffect="2";  //粗体
                } else if("#fi[1]".equals(this.paramtervo.getTitle_fb())) {
                    fontEffect="3";  //斜体
                }
			}
			else if(flag==1)
			{
				if("#fi[1]".equals(this.paramtervo.getHead_fi())&& "#fb[1]".equals(this.paramtervo.getHead_fb())) {
                    fontEffect="4";  //粗斜体
                } else if("#fb[1]".equals(this.paramtervo.getHead_fb())) {
                    fontEffect="2";  //粗体
                } else if("#fi[1]".equals(this.paramtervo.getHead_fi())) {
                    fontEffect="3";  //斜体
                }
			}
			else if(flag==2)
			{
				if("#fi[1]".equals(this.paramtervo.getTile_fi())&& "#fb[1]".equals(this.paramtervo.getTile_fb())) {
                    fontEffect="4";  //粗斜体
                } else if("#fb[1]".equals(this.paramtervo.getTile_fb())) {
                    fontEffect="2";  //粗体
                } else if("#fi[1]".equals(this.paramtervo.getTile_fi())) {
                    fontEffect="3";  //斜体
                }
			}
			
		
		
		
		
		}
		return fontEffect;
	}
	
	public String exportResumeFormPosition(UserView view,String records)
	{   
		String outName = view.getUserName();
		String fileName = outName+".xls";
		RowSet rs = null;
		RowSet subRs=null;
		HSSFWorkbook workbook = null;
		FileOutputStream fileOut = null;
		try
		{
			EmployResumeBo bo=new EmployResumeBo(this.conn,view);
			ArrayList fieldList=bo.getFieldList();
			if(fieldList==null||fieldList.size()==0)
			{
				fileName="-1";
				return fileName;
			}
			RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
			String dbName=vo.getString("str_value");
			workbook = new HSSFWorkbook();
    		HSSFSheet sheet = null;
    		HSSFRow row=null;
     		HSSFCell csCell=null;
     		
			if(dbName!=null&&dbName.trim().length()>0&&records.length()>0)
			{
				HSSFFont font1 = workbook.createFont();
				HSSFFont font2 = workbook.createFont();
				font2.setFontName(ResourceFactory.getProperty("gz.gz_acounting.m.font"));
				font1.setFontName(ResourceFactory.getProperty("gz.gz_acounting.m.font"));
				font1.setBold(true);
				font1.setFontHeightInPoints((short)12);
				font2.setFontHeightInPoints((short)11);
				HSSFCellStyle cell_title = workbook.createCellStyle();
				HSSFCellStyle data_title = workbook.createCellStyle();
				cell_title.setFont(font1);
				cell_title.setAlignment(HorizontalAlignment.CENTER);
				cell_title.setBorderBottom(BorderStyle.THIN);
				cell_title.setBorderLeft(BorderStyle.THIN);
				cell_title.setBorderRight(BorderStyle.THIN);
				cell_title.setBorderTop(BorderStyle.THIN);
				cell_title.setVerticalAlignment(VerticalAlignment.CENTER);
				cell_title.setAlignment(HorizontalAlignment.CENTER);
				data_title.setFont(font2);
				data_title.setWrapText(true);
				data_title.setAlignment(HorizontalAlignment.CENTER);
				data_title.setBorderBottom(BorderStyle.THIN);
				data_title.setBorderLeft(BorderStyle.THIN);
				data_title.setBorderRight(BorderStyle.THIN);
				data_title.setBorderTop(BorderStyle.THIN);
				data_title.setVerticalAlignment(VerticalAlignment.BOTTOM);
				cell_title.setAlignment(HorizontalAlignment.CENTER);
				StringBuffer select_str=new StringBuffer("select "+dbName+"A01.A0100");
				StringBuffer from_str=new StringBuffer(" from "+dbName+"A01 ");	
				StringBuffer subSetBuf = new StringBuffer(" from "+dbName+"A01 ");	
				HashMap fieldSetMap=new HashMap();
				fieldSetMap.put("A01","1");
				String tempName = "";
				for(int i=0;i<fieldList.size();i++)
				{
					FieldItem item=(FieldItem)fieldList.get(i);
					String itemid=item.getItemid();
					String itemtype=item.getItemtype();
					String fieldsetid=item.getFieldsetid();
					String codesetid=item.getCodesetid();
					
					 tempName=dbName+fieldsetid;
					if("OthZ03".equalsIgnoreCase(tempName)) {
                        tempName="Z03";
                    }
					if("M".equals(itemtype)) {
                        continue;
                    }
					
					if(fieldSetMap.get(fieldsetid)==null&&!"Z03".equalsIgnoreCase(tempName))
					{
						
						StringBuffer viewSql=new StringBuffer("");
						viewSql.append("(SELECT * FROM ");
						viewSql.append(tempName);
						
						viewSql.append(" A WHERE A.I9999 =(SELECT MAX(B.I9999) FROM ");
						viewSql.append(tempName);
						viewSql.append(" B WHERE ");
						viewSql.append(" A.A0100=B.A0100  )) ");
						viewSql.append(tempName);

						from_str.append(" left join "+viewSql.toString()+" on "+dbName+"A01.a0100="+tempName+".a0100 ");
						subSetBuf.append(" left join "+viewSql.toString()+" on "+dbName+"A01.a0100="+tempName+".a0100 ");
						fieldSetMap.put(fieldsetid.toUpperCase(),"1");
					}
					 
					String column=tempName+"."+itemid;
						select_str.append(","+column);
			  
				}
				if("Z03".equalsIgnoreCase(tempName)) {
                    from_str.append(" left join (select z03.*,zp.A0100 from zp_pos_tache zp left join z03 on z03.z0301 = zp.zp_pos_id) Z03 on Z03.a0100 = OthA01.a0100");
                }
				ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.conn,"1");
				HashMap map=parameterXMLBo.getAttributeValues();
				String active_field="";
				if(map!=null&&map.get("active_field")!=null&&!"".equals((String)map.get("active_field")))
				{
					active_field=(String)map.get("active_field");
				}
				String hireMajor="";
				if(map.get("hireMajor")!=null) {
                    hireMajor=(String)map.get("hireMajor");  //招聘专业指标
                }
				FieldItem hireMajoritem=null;
				if(hireMajor.length()>0)
				{
					hireMajoritem=DataDictionary.getFieldItem(hireMajor.toLowerCase());
				}
		    	ContentDAO dao  = new ContentDAO(this.conn);
	    		rs = dao.search("select z03.* from z03 where z0301 in ('"+records.replaceAll(",", "','")+"')");
	    		int rowNum=0;
	    		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	    		HashMap amap = new HashMap();
	    		int sheetCount=1;
	    		while(rs.next())
	    		{
	    			String sheetName="";
	    			String z0336=rs.getString("z0336")==null?"":rs.getString("z0336");
	    			/**校园招聘，取专业值*/
	    			if("01".equals(z0336)&&hireMajor.length()>0)
	    			{
	    				sheetName = rs.getString(hireMajor);
	    				if(hireMajoritem!=null&&hireMajoritem.isCode()) {
                            sheetName=AdminCode.getCodeName(hireMajoritem.getCodesetid(), sheetName);
                        }
	    			}
	    			else
	    			{
	    				sheetName = AdminCode.getCodeName("@K",rs.getString("z0311"));
	    			}
	    			if(sheetName==null|| "".equals(sheetName.trim()))
	    			{
	    				sheetName=sheetCount+"";
	    				sheetCount++;
	    			}else{
	    	    		if(amap.get(sheetName.toLowerCase())==null)
	    	    		{
	    		    		amap.put(sheetName.toLowerCase(), "1");
	    		    	}
	    	    		else
	    	    		{
	    		    		String num=(String)amap.get(sheetName.toLowerCase());
	    		    		amap.put(sheetName.toLowerCase(), (Integer.parseInt(num)+1)+"");
	    		    		sheetName=num+"-"+sheetName;
	    	    		}
	    			}
	    			rowNum=0;
	    			sheet=workbook.createSheet(sheetName.replaceAll("/","-"));
	    			row=sheet.createRow(rowNum);
	    			csCell=row.createCell(0);
	    			csCell.setCellStyle(cell_title);
	    			csCell.setCellValue("人员简历信息");
	    			ExportExcelUtil.mergeCell(sheet, 0, (short)(0), 0, (short)(fieldList.size()));
	    			rowNum++;
	    			row=sheet.createRow(rowNum);
	    			rowNum++;
	    			int j=0;
	    			csCell=row.createCell(j);
	    			csCell.setCellStyle(cell_title);
	    			csCell.setCellValue("序号");
	    			j++;
	    			for(int i=0;i<fieldList.size();i++)
    				{
    					FieldItem item=(FieldItem)fieldList.get(i);
    					String itemid=item.getItemid();
    					String itemtype=item.getItemtype();
    					String fieldsetid=item.getFieldsetid();
    					String codesetid=item.getCodesetid();
    					
    					 tempName=dbName+fieldsetid;
    					if("M".equals(itemtype)) {
                            continue;
                        }
    					csCell = row.createCell(j);
    					csCell.setCellStyle(cell_title);
    					if(hireMajor.length()>0&& "e01a1".equalsIgnoreCase(itemid)) {
                            csCell.setCellValue("岗位(专业)名称");
                        } else {
                            csCell.setCellValue(item.getItemdesc());
                        }
    					j++;
    				}
	    			String z0301 = rs.getString("z0301");
	    			StringBuffer sb= new StringBuffer("");
	    			StringBuffer subBuf = new StringBuffer();
	    			sb.append(select_str);
	    			sb.append("  ");
	    			sb.append(from_str);
	    			sb.append(" where "+dbName+"A01.A0100 in ( select a0100 from zp_pos_tache where zp_pos_id='"+z0301+"')");
	    			subBuf.append(" select "+dbName+"A01.a0100 ");
	    			subBuf.append("  ");
	    			subBuf.append(subSetBuf);
	    			subBuf.append(" where "+dbName+"A01.A0100 in ( select a0100 from zp_pos_tache where zp_pos_id='"+z0301+"')");
	    			if(active_field!=null&&!"".equals(active_field.trim()))
	    			{
	    				sb.append(" and ("+dbName+"a01."+active_field+"='1' or "+dbName+"A01."+active_field+" is null)");
	    				subBuf.append(" and ("+dbName+"a01."+active_field+"='1' or "+dbName+"A01."+active_field+" is null)");
	    				if(!(view.isSuper_admin()|| "1".equals(view.getGroupId())))
	    				{
	    						if(view.getUnitIdByBusi("7")!=null&&!"".equals(view.getUnitIdByBusi("7")))
	    						{
	    							String unitid=view.getUnitIdByBusi("7");
	    							if(unitid==null|| "".equals(unitid)){
	    								
	    								sb.append(" and 1=2 ");
	    								subBuf.append(" and 1=2 ");
	    							}else if("UN`".equalsIgnoreCase(unitid))
	    							{
	    								//全部，现在处理为都可以看见
	    							}
	    							else
	    							{
	    								String[] unit_arr=view.getUnitIdByBusi("7").split("`");
	    								for(int i=0;i<unit_arr.length;i++)
	    								{
	    									if(unit_arr[i]==null|| "".equals(unit_arr[i])) {
                                                continue;
                                            }
	    									String org=unit_arr[i].substring(2);
	    									sb.append(" and "+dbName+"a01.b0110 not like '"+org+"%'");
	    									subBuf.append(" and "+dbName+"a01.b0110 not like '"+org+"%'");
	    								}
	    							}
	    						}
	    						else if(view.getStatus()==4)
	    						{
	    				        	String org=view.getUserOrgId();
	    				        	if(org==null|| "".equals(org)){
	    				    	    	sb.append(" and 1=2 ");
	    				    	    	subBuf.append(" and 1=2 ");
	    				        	}else{
	    				            	sb.append(" and "+dbName+"a01.b0110 not like '"+org+"%'");
	    				            	subBuf.append(" and "+dbName+"a01.b0110 not like '"+org+"%'");
	    				        	}
	    						}
	    				}
	    			}
	    			subRs = dao.search(sb.toString());
	    			HashMap subMap = bo.getSubSetMap(subBuf.toString(), fieldList, dbName);
	    			int seq=1;
	    			while(subRs.next())
	    			{
	    				row = sheet.createRow(rowNum);
	    				csCell = row.createCell(0);
	    				csCell.setCellStyle(data_title);
	    				csCell.setCellValue(String.valueOf(seq));
	    				int index=1;
	    				for(int i=0;i<fieldList.size();i++)
	    				{
	    					FieldItem item=(FieldItem)fieldList.get(i);
	    					String itemid=item.getItemid();
	    					String itemtype=item.getItemtype();
	    					String fieldsetid=item.getFieldsetid();
	    					String codesetid=item.getCodesetid();
	    					csCell = row.createCell(index);
	    					csCell.setCellStyle(data_title);
	    					if("M".equals(itemtype)) {
                                continue;
                            }
	    					String a0100=subRs.getString("a0100");
	    					String key=(a0100+itemid).toUpperCase();
	    					if(subMap.get(key)!=null)
	    					{
	    						HSSFRichTextString taxt = new HSSFRichTextString((String)subMap.get(key));
						    	csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
		     	  		    	csCell.setCellValue(taxt);
		     	  		    	index++;	
	    						continue;
	    					}
	    				
	    					if("N".equalsIgnoreCase(itemtype))
	    					{
	    						int deci = item.getDecimalwidth();
	    						if(subRs.getString(itemid)==null) {
                                    csCell.setCellValue("");
                                } else
	    						{
	    							csCell.setCellValue(PubFunc.round(subRs.getString(itemid), deci));
	    						}
	    						
	    					}
	    					else if("D".equalsIgnoreCase(itemtype))
	    					{
	    						if(subRs.getDate(itemid)==null) {
                                    csCell.setCellValue("");
                                } else {
                                    csCell.setCellValue(format.format(subRs.getDate(itemid)));
                                }
	    					}
	    					else if("A".equalsIgnoreCase(itemtype))
	    					{
	    						if(subRs.getString(itemid)==null) {
                                    csCell.setCellValue("");
                                } else
	    						{
	    							if(!"0".equals(codesetid))
	    							{
	    								csCell.setCellValue(AdminCode.getCodeName(codesetid, subRs.getString(itemid)));
	    							}
	    							else
	    							{
	    								csCell.setCellValue(subRs.getString(itemid));
	    							}
	    						}
	    					}
	    					else
	    					{
	    						if(subRs.getString(itemid)==null) {
                                    csCell.setCellValue("");
                                } else
	    						{
	    							csCell.setCellValue(subRs.getString(itemid));
	    						}
	    					}
	    					index++;	
	    				}
	    				seq++;
	    				rowNum++;
	    			}
	    			for(int var=0;var<=rowNum;var++)
	    			{
	    				if(sheet.getRow(var)!=null){
	    		    		//sheet.getRow(var).setHeight((short)600);不设置行高 使行高随内容而增加
	    				}	
	    			}
	    			for(int var=0;var<=fieldList.size();var++)
	    			{
	    				sheet.setColumnWidth(var, 6000);
	    			}
	    		}
			}
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+fileName);
			workbook.write(fileOut);
			fileOut.close();	
			sheet=null;
			workbook=null;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{

			PubFunc.closeResource(rs);
			PubFunc.closeResource(subRs);
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(workbook);
		}
		return fileName;
	}
	public String decimalwidth(int len){
		StringBuffer decimal= new StringBuffer("0");
		if(len>0) {
            decimal.append(".");
        }
		for(int i=0;i<len;i++){
			decimal.append("0");
		}
		decimal.append("_ ");
		return decimal.toString();
	}

	public HashMap getEmployerNameMap() {
		return employerNameMap;
	}


	public void setEmployerNameMap(HashMap employerNameMap) {
		this.employerNameMap = employerNameMap;
	}


	public HashMap getPlanCountMap() {
		return planCountMap;
	}


	public void setPlanCountMap(HashMap planCountMap) {
		this.planCountMap = planCountMap;
	}


	public ReportParseVo getParamtervo() {
		return paramtervo;
	}


	public void setParamtervo(ReportParseVo paramtervo) {
		this.paramtervo = paramtervo;
	}
	public UserView getUserView() {
		return userView;
	}
	public void setUserView(UserView userView) {
		this.userView = userView;
	}
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public int getPageno() {
		return pageno;
	}
	public void setPageno(int pageno) {
		this.pageno = pageno;
	}
	public String getProducer() {
		return producer;
	}
	public void setProducer(String producer) {
		this.producer = producer;
	}
	public String getTimes() {
		return times;
	}
	public void setTimes(String times) {
		this.times = times;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public HashMap getDbname(){
		RowSet rowset=null;
		HashMap tmap = new HashMap();
		ContentDAO dao=new ContentDAO(this.conn);
		try {
			rowset=dao.search("select pre from dbname");		
			while(rowset.next())
			{
				tmap.put(rowset.getString(1).toUpperCase(), "1");
			}
		} catch (SQLException e) {
		
			e.printStackTrace();
		}finally{
			
				try {
					if(rowset!=null){
					rowset.close();
					}
				} catch (SQLException e) {
				
					e.printStackTrace();
				}
			}
		
		return tmap;
	}
	public HashMap getNumberMap() {
		return numberMap;
	}
	public void setNumberMap(HashMap numberMap) {
		this.numberMap = numberMap;
	}
		
	public void setFileName(String fileName) {
        this.fileName = fileName;
    }	

	
	
	
	
	
	
}
