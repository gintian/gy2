package com.hjsj.hrms.businessobject.general.muster;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.interfaces.report.ReportParseXml;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.*;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class ExecuteExcel {
	private Connection conn = null;
	private String userName="";
	private int    totalNum=0;
	public ReportParseVo paramtervo=null;
	private HSSFCellStyle centerstyle=null;
	private UserView userview=null;	
	private String infor_Flag="";
	private String dbpre="";
	private String tabid="";
	//liuy 2014-11-4 新增花名册控制excel页面设置参数 start
	private static short A3_PAPERSIZE = 8;
    private static short A4_PAPERSIZE = HSSFPrintSetup.A4_PAPERSIZE;
    private static short A5_PAPERSIZE = HSSFPrintSetup.A5_PAPERSIZE;
    private static short B5_PAPERSIZE = 13;
    //liuy end
	public ExecuteExcel(Connection conn)
	{
		this.conn=conn;
	}
	
	public void SetParamtervo(String xmlContent, String  path)
	{
		ReportParseXml parseXml = new ReportParseXml();	 
		if(xmlContent!=null&&xmlContent.length()>1) {
            this.paramtervo = parseXml.ReadOutParseXml(xmlContent,path);
        }

	}
	
	/**
	 * 取得页面打印格式
	 * @param tabid
	 */
	public void  setPagePrintStyle(String tabid)
	{
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try
		{
			String xmlContent="";
			rowSet=dao.search(" select xml_style from  lname where tabid="+tabid);
			if(rowSet.next()) {
                xmlContent=rowSet.getString("xml_style");
            }
			SetParamtervo(xmlContent,"/report");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 简单花名册重构新增方法 zyh 2019-03-07
	 * @param userName
	 * @param map
	 * @return
	 */
	public String createExcel(String userName,HashMap<String, Object>  map){
		this.userName=userName;		
		//String outputFile =map.get("musterName")+"_"+(String)map.get("userName")+".xls";
		String outputFile =(String)map.get("userName")+"_"+map.get("musterName")+".xls";
	    String tabid = (String) map.get("tabid");
		setPagePrintStyle(tabid);
		ArrayList list=new ArrayList();
		ArrayList<String> itemList = new ArrayList<String>();
		HashMap<String, String> itemMap = new HashMap<String, String>();
		String itemids = (String) map.get("itemids");
		if (StringUtils.isNotEmpty(itemids)) {
            itemList.add("recidx");
            itemMap.put("recidx", "序号");
		    String[] itemidArray = itemids.split(",");
	        for (int i = 0; i < itemidArray.length; i++) {
	            String itemid = itemidArray[i];
	            if ("XUHAO".equals(itemid)) {
	                itemList.add("recidx");
	                itemMap.put("recidx", "序号");
	                continue ;
                }
	            itemList.add(itemid);
	            FieldItem fieldItem = DataDictionary.getFieldItem(itemid);
	            String itemname = fieldItem.getItemdesc();
	            itemMap.put(itemid, itemname);
	        }
        }
	    list.add(itemList);
        list.add(itemMap);
		String[][] excelData=getExcelData(list,map);
		this.totalNum=excelData.length-1;
		RecordVo lnameVo=getLnameInfo(tabid);//获取表格上下内容
		HashMap  musterMap=getMusterExtendInfo(lnameVo);  //标题，表头信息
		ArrayList fieldList=(ArrayList)list.get(0);
		if(fieldList.size()<200){
			this.exportXlsExcel(excelData,musterMap,fieldList,outputFile);
		}else{
			outputFile=map.get("musterName")+"_"+(String)map.get("userName")+".xlsx";
			this.exportXlsxExcel(excelData,musterMap,fieldList,outputFile);
		}
		return outputFile;
    }
	public String createExcel(String userName,String musterName)
	{
		this.userName=userName;		
		String outputFile =this.userName +"_train.xls";
		
			String    tabid=musterName.substring(1,musterName.indexOf("_"));
			setPagePrintStyle(tabid);
			
			ArrayList list=getMusterField(tabid);
			String[][] excelData=getExcelData(list,musterName);
			this.totalNum=excelData.length-1;
			RecordVo lnameVo=getLnameInfo(tabid);
			HashMap  musterMap=getMusterExtendInfo(lnameVo);  //标题，表头信息
			
			ArrayList fieldList=(ArrayList)list.get(0);
			
			if(fieldList.size()<200){
				this.exportXlsExcel(excelData,musterMap,fieldList,outputFile);
			}else{
				outputFile=this.userName +"_train.xlsx";
				this.exportXlsxExcel(excelData,musterMap,fieldList,outputFile);
			}
			
		return outputFile;
	}
	/**
	 * 导出未超出255列 格式为xls
	 * */
	public void exportXlsExcel(String[][] excelData,HashMap  musterMap,ArrayList fieldList,String outputFile){
		    try{
				HSSFWorkbook workbook = new HSSFWorkbook();
				HSSFSheet sheet = null;
				HSSFRow row=null;
				HSSFCell csCell=null;
				short n=0;
				HSSFCellStyle styletext = style(workbook,1);
				styletext.setWrapText(true);
				styletext.setFont(getFont(workbook, 3));
		//		styletext.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));
				styletext.setAlignment(HorizontalAlignment.LEFT );//bug号： 39683 导出excel，字符型指标内容应该居左对齐。
				
				HSSFCellStyle styleN = style(workbook,1);
				styleN.setAlignment(HorizontalAlignment.RIGHT );
				styleN.setWrapText(true);
				styleN.setFont(getFont(workbook, 3));
				HSSFDataFormat df = workbook.createDataFormat();
				styleN.setDataFormat(df.getFormat(decimalwidth(0)));
				/**根据指标的小数位数，来确定导出数据的小数，由于poi设置数据格式问题，现在只支持到6位小数*/
				HSSFCellStyle styleF1 = style(workbook,1);
				styleF1.setAlignment(HorizontalAlignment.RIGHT );
				styleF1.setWrapText(true);
				HSSFDataFormat df1 = workbook.createDataFormat();
				styleF1.setDataFormat(df1.getFormat(decimalwidth(1)));
				styleF1.setFont(getFont(workbook, 3));
				
				HSSFCellStyle styleF2 = style(workbook,1);
				styleF2.setAlignment(HorizontalAlignment.RIGHT );
				styleF2.setWrapText(true);
				HSSFDataFormat df2 = workbook.createDataFormat();
				styleF2.setDataFormat(df2.getFormat(decimalwidth(2)));
				styleF2.setFont(getFont(workbook, 3));
				
				HSSFCellStyle styleF3 = style(workbook,1);
				styleF3.setAlignment(HorizontalAlignment.RIGHT );
				styleF3.setWrapText(true);
				HSSFDataFormat df3 = workbook.createDataFormat();
				styleF3.setDataFormat(df3.getFormat(decimalwidth(3)));
				styleF3.setFont(getFont(workbook, 3));
				
				HSSFCellStyle styleF4 = style(workbook,1);
				styleF4.setAlignment(HorizontalAlignment.RIGHT );
				styleF4.setWrapText(true);
				HSSFDataFormat df4 = workbook.createDataFormat();
				styleF4.setDataFormat(df4.getFormat(decimalwidth(4)));
				styleF4.setFont(getFont(workbook, 3));
				
				HSSFCellStyle styleF5 = style(workbook,1);
				styleF5.setAlignment(HorizontalAlignment.RIGHT );
				styleF5.setWrapText(true);
				HSSFDataFormat df5 = workbook.createDataFormat();
				styleF5.setDataFormat(df5.getFormat(decimalwidth(5)));
				styleF5.setFont(getFont(workbook, 3));
		
				HSSFCellStyle styleF6 = style(workbook,1);
				styleF6.setAlignment(HorizontalAlignment.RIGHT );
				styleF6.setWrapText(true);
				HSSFDataFormat df6 = workbook.createDataFormat();
				styleF6.setDataFormat(df6.getFormat(decimalwidth(6)));
				styleF6.setFont(getFont(workbook, 3));
				
				HSSFCellStyle styleD = style(workbook,1);
				styleD.setWrapText(true);
				styleD.setFont(getFont(workbook, 3));
		//		styleD.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
		//		styleD.setAlignment(HorizontalAlignment.LEFT );
				
				String excel_rows = SystemConfig.getPropertyValue("excel_rows");
				/* 将每页20000改为每页5000 xiaoyun 2014-7-4 start */
				excel_rows=excel_rows!=null&&excel_rows.trim().length()>0?excel_rows:"5000";
				/* 将每页20000改为每页5000 xiaoyun 2014-7-4 end */
				
				int nrows = Integer.parseInt(excel_rows);
				HashMap map = new HashMap();
				int m =0;
				for(int i=0;i<excelData.length;i++)
				{
					m = i%nrows;
					if(m==0){
						if(i!=0)
						{
							executeFoot((short)(n+1),musterMap,excelData,sheet,style(workbook,5));
						}
						sheet = workbook.createSheet("第"+((i/nrows) + 1)+"页");
						n=executeTitel(musterMap,excelData,sheet,workbook);  //写上表头 和 标题
						//liuy 2014-11-4 新增花名册控制excel页面设置方法 start
						if(this.paramtervo!=null){				
							//设置打印参数
					        sheet.setMargin(HSSFSheet.TopMargin, mmToInches(Double.parseDouble(this.paramtervo.getTop())));//页边距（上）    
					        sheet.setMargin(HSSFSheet.BottomMargin, mmToInches(Double.parseDouble(this.paramtervo.getBottom())));//页边距（下）    
					        sheet.setMargin(HSSFSheet.LeftMargin, mmToInches(Double.parseDouble(this.paramtervo.getLeft())));//页边距（左）    
					        sheet.setMargin(HSSFSheet.RightMargin, mmToInches(Double.parseDouble(this.paramtervo.getRight())));//页边距（右）    
					        //打印方向，true：横向，false：纵向(默认)
					        HSSFPrintSetup ps = sheet.getPrintSetup();
					        ps.setLandscape("1".equals(this.paramtervo.getOrientation()));
					        short paper = 0;
					        if(this.paramtervo.getPagetype()!=null){
					        	paper = getPaperSize(this.paramtervo.getPagetype());
					        }
					        if(paper != 0){		        	
					        	ps.setPaperSize(paper);//纸张类型 
					        }
						}
						//liuy end
					}
					
					if(i==0||m==0){
						/**当换页时，写表头*/
						//row = sheet.createRow(m+n);//xuj 升级poi改动
						row = sheet.getRow(n);//xuj 升级poi改动
						if(row==null) {
                            row = sheet.createRow(n);
                        }
						row.setHeight((short) 450);
						for(short j=0;j<excelData[0].length;j++)
						{
							csCell =row.createCell(j);
							//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
							HSSFRichTextString textstr = new HSSFRichTextString(excelData[0][j]);
							csCell.setCellValue(textstr);
							csCell.setCellStyle(styletext);
						}
						n++;
						
					}
		             if(i!=0){
						//row = sheet.createRow(m+n);
						row = sheet.getRow(n);//xuj 升级poi改动
						if(row==null) {
                            row = sheet.createRow(n);
                        }
		
						row.setHeight((short) 400);
						
						for(short j=0;j<excelData[i].length;j++)
						{
							csCell =row.createCell(j);
							//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);		
							
		
							String fieldName=(String)fieldList.get(j);
							int width=0;
							if("recidx".equals(fieldName)){
								csCell.setCellStyle(styleN);
								csCell.setCellValue(Integer.parseInt(excelData[i][j]));
							}else{
								FieldItem fielditem = DataDictionary.getFieldItem(fieldName.toLowerCase());
								if(fielditem==null) {
                                    fielditem = DataDictionary.getFieldItem(fieldName.toUpperCase());
                                }
								if(fielditem!=null){
							    	byte[] desc=fielditem.getItemdesc().getBytes();
							    	HSSFRichTextString atextstr = new HSSFRichTextString(excelData[i][j]);
							    	byte[] data=atextstr.toString().getBytes();
							    	int length=data.length==0?1:data.length;
							    	if(desc.length>length) {
                                        length=desc.length;
                                    }
							    	if(length>width) {
                                        width=length;
                                    }
						 	    	if(map.get(j+"")!=null)
							    	{
							     		int in=Integer.parseInt((String)map.get(j+""));
							    		if(width>=in)
						    			{
							    			map.put(j+"", width+"");
							    			sheet.setColumnWidth(Short.parseShort(String.valueOf(j)),(short)(width*300));
							    		}
							    		else{
							    			sheet.setColumnWidth(Short.parseShort(String.valueOf(j)),(short)(in*300));
							    		}
							    	}
							    	else
							    	{
							    		map.put(j+"", width+"");
						    			sheet.setColumnWidth(Short.parseShort(String.valueOf(j)),(short)(width*300));
						    		}
								
									if("N".equalsIgnoreCase(fielditem.getItemtype())){
										if(fielditem.getDecimalwidth()==0) {
                                            csCell.setCellStyle(styleN);
                                        } else if(fielditem.getDecimalwidth()==1) {
                                            csCell.setCellStyle(styleF1);
                                        } else if(fielditem.getDecimalwidth()==2) {
                                            csCell.setCellStyle(styleF2);
                                        } else if(fielditem.getDecimalwidth()==3) {
                                            csCell.setCellStyle(styleF3);
                                        } else if(fielditem.getDecimalwidth()==4) {
                                            csCell.setCellStyle(styleF4);
                                        } else if(fielditem.getDecimalwidth()==5) {
                                            csCell.setCellStyle(styleF5);
                                        } else if(fielditem.getDecimalwidth()>=6) {
                                            csCell.setCellStyle(styleF6);
                                        } else {
                                            csCell.setCellStyle(styleN);
                                        }
										if(excelData[i][j]!=null&&excelData[i][j].trim().length()>0){
											csCell.setCellValue(Double.parseDouble(excelData[i][j]));
										}else{
											HSSFRichTextString textstr = new HSSFRichTextString(excelData[i][j]);
											csCell.setCellValue(textstr);
										}
		
									}else if("D".equalsIgnoreCase(fielditem.getItemtype())){
										String dateValue=excelData[i][j];
										if(dateValue==null) {
                                            dateValue="";
                                        }
										if(fielditem.getItemlength()==10)
										{
											if(dateValue.length()>fielditem.getItemlength()) {
                                                dateValue=dateValue.substring(0,10);
                                            }
										}
										else if(fielditem.getItemlength()==7)
										{
											if(dateValue.length()>fielditem.getItemlength()) {
                                                dateValue=dateValue.substring(0,7);
                                            }
										}
										else if(fielditem.getItemlength()==4)
										{
											if(dateValue.length()>fielditem.getItemlength()) {
                                                dateValue=dateValue.substring(0,4);
                                            }
										}
										HSSFRichTextString textstr = new HSSFRichTextString(dateValue);
										csCell.setCellValue(textstr);
										csCell.setCellStyle(styleD);
									}else{
										HSSFRichTextString textstr = new HSSFRichTextString(excelData[i][j]);
										csCell.setCellValue(textstr);
										csCell.setCellStyle(styletext);
									}
								}else{
									HSSFRichTextString textstr = new HSSFRichTextString(excelData[i][j]);
									csCell.setCellValue(textstr);
									csCell.setCellStyle(styletext);
								}
							}
						}
						n++;
					}
				}
				
			     	/*for(short j=0;j<=excelData[0].length;j++)
			    	{
			     		csCell =row.createCell(j);
			     		int width=5;
			     		if(map.get(""+j)!=null)
			     		{
			     			width=Integer.parseInt((String)map.get(j+""));
			     		}
			     		sheet.setColumnWidth(Short.parseShort(String.valueOf(j)),(short)(width*300));
			    	}*/
			
				if(excelData!=null)
				{
					executeFoot((short)(n+1),musterMap,excelData,sheet,style(workbook,5));
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
   }
	
	/****
	 * 导出超出255列时 格式为XLSX
	 * */
	public void exportXlsxExcel(String[][] excelData,HashMap  musterMap,ArrayList fieldList,String outputFile){
		try {
			XSSFWorkbook workbook=new XSSFWorkbook();
			XSSFSheet sheet=null;
			XSSFRow row=null;
			XSSFCell csCell=null;
			short n=0;
			XSSFCellStyle styletext=styleXlsx(workbook,1);
			styletext.setWrapText(true);
			styletext.setFont(getXlsxFont(workbook, 3));
			
			XSSFCellStyle styleN = styleXlsx(workbook,1);
			styleN.setAlignment(HorizontalAlignment.RIGHT );
			styleN.setWrapText(true);
			styleN.setFont(getXlsxFont(workbook, 3));
			XSSFDataFormat df = workbook.createDataFormat();
			styleN.setDataFormat(df.getFormat(decimalwidth(0)));
			/**根据指标的小数位数，来确定导出数据的小数，由于poi设置数据格式问题，现在只支持到6位小数*/
			XSSFCellStyle styleF1 = styleXlsx(workbook,1);
			styleF1.setAlignment(HorizontalAlignment.RIGHT );
			styleF1.setWrapText(true);
			XSSFDataFormat df1 = workbook.createDataFormat();
			styleF1.setDataFormat(df1.getFormat(decimalwidth(1)));
			styleF1.setFont(getXlsxFont(workbook, 3));
			
			XSSFCellStyle styleF2 = styleXlsx(workbook,1);
			styleF2.setAlignment(HorizontalAlignment.RIGHT );
			styleF2.setWrapText(true);
			XSSFDataFormat df2 = workbook.createDataFormat();
			styleF2.setDataFormat(df2.getFormat(decimalwidth(2)));
			styleF2.setFont(getXlsxFont(workbook, 3));
			
			XSSFCellStyle styleF3 = styleXlsx(workbook,1);
			styleF3.setAlignment(HorizontalAlignment.RIGHT );
			styleF3.setWrapText(true);
			XSSFDataFormat df3 = workbook.createDataFormat();
			styleF3.setDataFormat(df3.getFormat(decimalwidth(3)));
			styleF3.setFont(getXlsxFont(workbook, 3));
			
			XSSFCellStyle styleF4 = styleXlsx(workbook,1);
			styleF4.setAlignment(HorizontalAlignment.RIGHT );
			styleF4.setWrapText(true);
			XSSFDataFormat df4 = workbook.createDataFormat();
			styleF4.setDataFormat(df4.getFormat(decimalwidth(4)));
			styleF4.setFont(getXlsxFont(workbook, 3));
			
			XSSFCellStyle styleF5 = styleXlsx(workbook,1);
			styleF5.setAlignment(HorizontalAlignment.RIGHT );
			styleF5.setWrapText(true);
			XSSFDataFormat df5 = workbook.createDataFormat();
			styleF5.setDataFormat(df5.getFormat(decimalwidth(5)));
			styleF5.setFont(getXlsxFont(workbook, 3));
	
			XSSFCellStyle styleF6 = styleXlsx(workbook,1);
			styleF6.setAlignment(HorizontalAlignment.RIGHT );
			styleF6.setWrapText(true);
			XSSFDataFormat df6 = workbook.createDataFormat();
			styleF6.setDataFormat(df6.getFormat(decimalwidth(6)));
			styleF6.setFont(getXlsxFont(workbook, 3));
			
			XSSFCellStyle styleD = styleXlsx(workbook,1);
			styleD.setWrapText(true);
			styleD.setFont(getXlsxFont(workbook, 3));
	//		styleD.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy"));
	//		styleD.setAlignment(HorizontalAlignment.LEFT );
			
			String excel_rows = SystemConfig.getPropertyValue("excel_rows");
			/* 将每页20000改为每页5000 xiaoyun 2014-7-4 start */
			excel_rows=excel_rows!=null&&excel_rows.trim().length()>0?excel_rows:"5000";
			/* 将每页20000改为每页5000 xiaoyun 2014-7-4 end */
			
			int nrows = Integer.parseInt(excel_rows);
			HashMap map = new HashMap();
			int m =0;
			for(int i=0;i<excelData.length;i++)
			{
				m = i%nrows;
				if(m==0){
					if(i!=0)
					{
						executeFootXlsx((short)(n+1),musterMap,excelData,sheet,styleXlsx(workbook,5));
					}
					sheet = workbook.createSheet("第"+((i/nrows) + 1)+"页");
					n=executeXlsxTitle(musterMap,excelData,sheet,workbook);  //写上表头 和 标题
					//liuy 2014-11-4 新增花名册控制excel页面设置方法 start
					if(this.paramtervo!=null){				
						//设置打印参数
				        sheet.setMargin(HSSFSheet.TopMargin, mmToInches(Double.parseDouble(this.paramtervo.getTop())));//页边距（上）    
				        sheet.setMargin(HSSFSheet.BottomMargin, mmToInches(Double.parseDouble(this.paramtervo.getBottom())));//页边距（下）    
				        sheet.setMargin(HSSFSheet.LeftMargin, mmToInches(Double.parseDouble(this.paramtervo.getLeft())));//页边距（左）    
				        sheet.setMargin(HSSFSheet.RightMargin, mmToInches(Double.parseDouble(this.paramtervo.getRight())));//页边距（右）    
				        //打印方向，true：横向，false：纵向(默认)
				        XSSFPrintSetup ps = sheet.getPrintSetup();
				        ps.setLandscape("1".equals(this.paramtervo.getOrientation()));
				        short paper = 0;
				        if(this.paramtervo.getPagetype()!=null){
				        	paper = getPaperSize(this.paramtervo.getPagetype());
				        }
				        if(paper != 0){		        	
				        	ps.setPaperSize(paper);//纸张类型 
				        }
					}
					//liuy end
				}
				
				if(i==0||m==0){
					/**当换页时，写表头*/
					//row = sheet.createRow(m+n);//xuj 升级poi改动
					row = sheet.getRow(n);//xuj 升级poi改动
					if(row==null) {
                        row = sheet.createRow(n);
                    }
					row.setHeight((short) 450);
					for(short j=0;j<excelData[0].length;j++)
					{
						csCell =row.createCell(j);
						XSSFRichTextString textstr = new XSSFRichTextString(excelData[0][j]);
						csCell.setCellValue(textstr);
						csCell.setCellStyle(styletext);
					}
					n++;
					
				}
	             if(i!=0){
					row = sheet.getRow(n);//xuj 升级poi改动
					if(row==null) {
                        row = sheet.createRow(n);
                    }
	
					row.setHeight((short) 400);
					
					for(short j=0;j<excelData[i].length;j++)
					{
						csCell =row.createCell(j);
						//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);		
						
	
						String fieldName=(String)fieldList.get(j);
						int width=0;
						if("recidx".equals(fieldName)){
							csCell.setCellStyle(styleN);
							csCell.setCellValue(Integer.parseInt(excelData[i][j]));
						}else{
							FieldItem fielditem = DataDictionary.getFieldItem(fieldName.toLowerCase());
							if(fielditem==null) {
                                fielditem = DataDictionary.getFieldItem(fieldName.toUpperCase());
                            }
							if(fielditem!=null){
						    	byte[] desc=fielditem.getItemdesc().getBytes();
						    	HSSFRichTextString atextstr = new HSSFRichTextString(excelData[i][j]);
						    	byte[] data=atextstr.toString().getBytes();
						    	int length=data.length==0?1:data.length;
						    	if(desc.length>length) {
                                    length=desc.length;
                                }
						    	if(length>width) {
                                    width=length;
                                }
					 	    	if(map.get(j+"")!=null)
						    	{
						     		int in=Integer.parseInt((String)map.get(j+""));
						    		if(width>=in)
					    			{
						    			map.put(j+"", width+"");
						    			sheet.setColumnWidth(Short.parseShort(String.valueOf(j)),(short)(width*300));
						    		}
						    		else{
						    			sheet.setColumnWidth(Short.parseShort(String.valueOf(j)),(short)(in*300));
						    		}
						    	}
						    	else
						    	{
						    		map.put(j+"", width+"");
					    			sheet.setColumnWidth(Short.parseShort(String.valueOf(j)),(short)(width*300));
					    		}
							
								if("N".equalsIgnoreCase(fielditem.getItemtype())){
									if(fielditem.getDecimalwidth()==0) {
                                        csCell.setCellStyle(styleN);
                                    } else if(fielditem.getDecimalwidth()==1) {
                                        csCell.setCellStyle(styleF1);
                                    } else if(fielditem.getDecimalwidth()==2) {
                                        csCell.setCellStyle(styleF2);
                                    } else if(fielditem.getDecimalwidth()==3) {
                                        csCell.setCellStyle(styleF3);
                                    } else if(fielditem.getDecimalwidth()==4) {
                                        csCell.setCellStyle(styleF4);
                                    } else if(fielditem.getDecimalwidth()==5) {
                                        csCell.setCellStyle(styleF5);
                                    } else if(fielditem.getDecimalwidth()>=6) {
                                        csCell.setCellStyle(styleF6);
                                    } else {
                                        csCell.setCellStyle(styleN);
                                    }
									if(excelData[i][j]!=null&&excelData[i][j].trim().length()>0){
										csCell.setCellValue(Double.parseDouble(excelData[i][j]));
									}else{
										XSSFRichTextString textstr = new XSSFRichTextString(excelData[i][j]);
										csCell.setCellValue(textstr);
									}
	
								}else if("D".equalsIgnoreCase(fielditem.getItemtype())){
									String dateValue=excelData[i][j];
									if(dateValue==null) {
                                        dateValue="";
                                    }
									if(fielditem.getItemlength()==10)
									{
										if(dateValue.length()>fielditem.getItemlength()) {
                                            dateValue=dateValue.substring(0,10);
                                        }
									}
									else if(fielditem.getItemlength()==7)
									{
										if(dateValue.length()>fielditem.getItemlength()) {
                                            dateValue=dateValue.substring(0,7);
                                        }
									}
									else if(fielditem.getItemlength()==4)
									{
										if(dateValue.length()>fielditem.getItemlength()) {
                                            dateValue=dateValue.substring(0,4);
                                        }
									}
									XSSFRichTextString textstr = new XSSFRichTextString(dateValue);
									csCell.setCellValue(textstr);
									csCell.setCellStyle(styleD);
								}else{
									XSSFRichTextString textstr = new XSSFRichTextString(excelData[i][j]);
									csCell.setCellValue(textstr);
									csCell.setCellStyle(styletext);
								}
							}else{
								XSSFRichTextString textstr = new XSSFRichTextString(excelData[i][j]);
								csCell.setCellValue(textstr);
								csCell.setCellStyle(styletext);
							}
						}
					}
					n++;
				}
			}
			
		     	
		
			if(excelData!=null)
			{
				executeFootXlsx((short)(n+1),musterMap,excelData,sheet,styleXlsx(workbook,5));
			}
			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outputFile);
			workbook.write(fileOut);
			fileOut.close();	
			sheet=null;
			workbook=null;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	/**
	 * 毫米转英寸
	 * 将设置的毫米单位转换为excel适用的英寸
	 * @author liuy
	 * @param mm
	 * @return
	 */
	private double mmToInches(double mm) {
        return mm * 0.03937;
    }
	
	/**
	 * 设置打印纸张类型
	 * @author liuy
	 * @param paperType
	 * @return
	 */
	private short getPaperSize(String paperType) {
        short paperSize = 0;
        if("A3".equals(paperType)){
        	paperSize=A3_PAPERSIZE;
		}else if("A4".equals(paperType)){
			paperSize=A4_PAPERSIZE;
		}else if("A5".equals(paperType)){
			paperSize=A5_PAPERSIZE;
		}else if("B5".equals(paperType)){
			paperSize=B5_PAPERSIZE;
		}
        return paperSize;
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
	
	public void createExcel(OutputStream output_stream,String musterName)
	{
		try
		{
			String    tabid=musterName.substring(1,musterName.indexOf("_"));
			ArrayList list=getMusterField(tabid);
			String[][] excelData=getExcelData(list,musterName);
			
			RecordVo lnameVo=getLnameInfo(tabid);
			HashMap  musterMap=getMusterExtendInfo(lnameVo);  //标题，表头信息
			
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = null;
			HSSFRow row=null;
			HSSFCell csCell=null;
			short n=0;
			HSSFCellStyle centerstyle = style(workbook,1);
			
			for(short i=0;i<excelData.length;i++)
			{
				int m = i%3000;
				if(m==0){
					sheet = workbook.createSheet("第"+((i/3000)+1)+"页");
					n=executeTitel(musterMap,excelData,sheet,workbook);  //写上表头 和 标题
				}
				if(m==0&&i!=0){
					//row = sheet.createRow(m+n-1);	//xuj 升级poi改动
					row = sheet.getRow(m+n-1);
					if(row==null) {
                        row = sheet.createRow(m+n-1);
                    }

					sheet.setColumnWidth((short)m,(short)3000);
					for(short j=0;j<excelData[i].length;j++)
					{
						
						csCell =row.createCell(j);
						//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
						HSSFRichTextString textstr = new HSSFRichTextString(excelData[0][j]);
						csCell.setCellValue(textstr);
						csCell.setCellStyle(centerstyle);
					}
					
				}
				row = sheet.createRow(m+n);
				for(short j=0;j<excelData[i].length;j++)
				{
					csCell =row.createCell(j);
					//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
					HSSFRichTextString textstr = new HSSFRichTextString(excelData[i][j]);
					csCell.setCellValue(textstr);
					csCell.setCellStyle(centerstyle);
				}
			}		
			if(excelData!=null)
			{
				executeFoot(n,musterMap,excelData,sheet,centerstyle);
			}
			 workbook.write(output_stream);
			 output_stream.close();	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	public void executeFootXlsx(short n,HashMap musterMap,String[][] excelData,XSSFSheet sheet,XSSFCellStyle styletext){
//		写下页脚
		XSSFRow row=null;
		XSSFCell csCell=null;
		if(excelData!=null&&excelData.length>0&&excelData[0].length>2)
		{
			row=sheet.createRow(n);
			styletext.setFont(getXlsxFont(sheet.getWorkbook(), 2));
			if(musterMap.get("lfoot")!=null)
			{
				csCell=row.createCell(Short.parseShort("0"));
				csCell.setCellValue(getTitleVale((String)musterMap.get("lfoot")));
				csCell.setCellStyle(styletext);
			}
			if(musterMap.get("mfoot")!=null)
			{
				csCell=row.createCell(Short.parseShort(String.valueOf(excelData[0].length/2)));
				csCell.setCellValue(getTitleVale((String)musterMap.get("mfoot")));
				csCell.setCellStyle(styletext);
			}
			if(musterMap.get("rfoot")!=null)
			{
				csCell=row.createCell(Short.parseShort(String.valueOf(excelData[0].length-1)));
				//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
				csCell.setCellValue(getTitleVale((String)musterMap.get("rfoot")));
				csCell.setCellStyle(styletext);
			}			
		}
	
	}
	
	
	public void executeFoot(short n,HashMap musterMap,String[][] excelData,HSSFSheet sheet,HSSFCellStyle styletext)
	{
//		写下页脚
		HSSFRow row=null;
		HSSFCell csCell=null;
		if(excelData!=null&&excelData.length>0&&excelData[0].length>2)
		{
			row=sheet.createRow(n);
			styletext.setFont(getFont(sheet.getWorkbook(), 2));
			if(musterMap.get("lfoot")!=null)
			{
				csCell=row.createCell(Short.parseShort("0"));
				//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
				csCell.setCellValue(getTitleVale((String)musterMap.get("lfoot")));
				csCell.setCellStyle(styletext);
			}
			if(musterMap.get("mfoot")!=null)
			{
				csCell=row.createCell(Short.parseShort(String.valueOf(excelData[0].length/2)));
				//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
				csCell.setCellValue(getTitleVale((String)musterMap.get("mfoot")));
				csCell.setCellStyle(styletext);
			}
			if(musterMap.get("rfoot")!=null)
			{
				csCell=row.createCell(Short.parseShort(String.valueOf(excelData[0].length-1)));
				//csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
				csCell.setCellValue(getTitleVale((String)musterMap.get("rfoot")));
				csCell.setCellStyle(styletext);
			}			
		}
	}
	
	
	public short executeXlsxTitle(HashMap musterMap,String[][] excelData,XSSFSheet sheet,XSSFWorkbook workbook){
		short n=0;
		try {
			XSSFRow row=null;
			XSSFCell csCell=null;
			//		写标题
			if(musterMap.get("title")!=null&&excelData!=null&&excelData.length>0&&excelData[0].length>2)
			{
				
				XSSFCellStyle cellStyle= styleXlsx(workbook,5);
				cellStyle.setAlignment(HorizontalAlignment.CENTER );
				cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
				cellStyle.setWrapText(true);
				cellStyle.setFont(getXlsxFont(workbook, 4));
				ExportExcelUtil.mergeCell(sheet, 0, (short)0,0,(short)(Integer.parseInt(String.valueOf(excelData[0].length))-1));
				row=sheet.createRow(n);
				row.setHeight((short)1000);
				csCell=row.createCell((short)0);
				csCell.setCellStyle(cellStyle);
				csCell.setCellValue((String)musterMap.get("title"));
				//n++;//liuy 2015-3-16 8042：组织机构和员工管理中的花名册，输出Excel时标题和内容间都多出两行空行
				n++;
			}
			//写上表头
			XSSFCellStyle titlestyle = styleXlsx(workbook,5);
			titlestyle.setWrapText(true);
			titlestyle.setFont(getXlsxFont(workbook, 1));
			//liuy 2015-3-16 8042：组织机构和员工管理中的花名册，输出Excel时标题和内容间都多出两行空行
			if(musterMap.get("lhead")!=null||musterMap.get("mhead")!=null||musterMap.get("rhead")!=null) {
                if(excelData!=null&&excelData.length>0&&excelData[0].length>2)
                {
                    row=sheet.createRow(n);
                    if(musterMap.get("lhead")!=null)
                    {
                        csCell=row.createCell(Short.parseShort("0"));
                        //csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
                        csCell.setCellValue(getTitleVale((String)musterMap.get("lhead")));
                        csCell.setCellStyle(titlestyle);
                    }
                    if(musterMap.get("mhead")!=null)
                    {
                        csCell=row.createCell(Short.parseShort(String.valueOf(excelData[0].length/2)));
                        //csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
                        csCell.setCellValue(getTitleVale((String)musterMap.get("mhead")));
                        csCell.setCellStyle(titlestyle);
                    }
                    if(musterMap.get("rhead")!=null)
                    {
                        csCell=row.createCell(Short.parseShort(String.valueOf(excelData[0].length-1)));
                        //csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
                        csCell.setCellValue(getTitleVale((String)musterMap.get("rhead")));
                        csCell.setCellStyle(titlestyle);
                    }
                    n++;
                }
            }
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return n;
		
	
	}
	
	
	public short executeTitel(HashMap musterMap,String[][] excelData,HSSFSheet sheet,HSSFWorkbook workbook) {  
		short n=0;
		try {
			HSSFRow row=null;
			HSSFCell csCell=null;
//		写标题
			if(musterMap.get("title")!=null&&excelData!=null&&excelData.length>0&&excelData[0].length>2)
			{
				
				HSSFCellStyle cellStyle= style(workbook,5);
				cellStyle.setAlignment(HorizontalAlignment.CENTER );
				cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
				cellStyle.setWrapText(true);
				cellStyle.setFont(getFont(workbook, 4));
				
				ExportExcelUtil.mergeCell(sheet, 0, (short)0,0,(short)(Integer.parseInt(String.valueOf(excelData[0].length))-1));
				row=sheet.createRow(n);
				row.setHeight((short)1000);
				csCell=row.createCell((short)0);
				csCell.setCellStyle(cellStyle);
				csCell.setCellValue((String)musterMap.get("title"));
				//n++;//liuy 2015-3-16 8042：组织机构和员工管理中的花名册，输出Excel时标题和内容间都多出两行空行
				n++;
			}
			//写上表头
			HSSFCellStyle titlestyle = style(workbook,5);
			titlestyle.setWrapText(true);
			titlestyle.setFont(getFont(workbook, 1));
			//liuy 2015-3-16 8042：组织机构和员工管理中的花名册，输出Excel时标题和内容间都多出两行空行
			if(musterMap.get("lhead")!=null||musterMap.get("mhead")!=null||musterMap.get("rhead")!=null) {
                if(excelData!=null&&excelData.length>0&&excelData[0].length>2)
                {
                    row=sheet.createRow(n);
                    if(musterMap.get("lhead")!=null)
                    {
                        csCell=row.createCell(Short.parseShort("0"));
                        //csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
                        csCell.setCellValue(getTitleVale((String)musterMap.get("lhead")));
                        csCell.setCellStyle(titlestyle);
                    }
                    if(musterMap.get("mhead")!=null)
                    {
                        csCell=row.createCell(Short.parseShort(String.valueOf(excelData[0].length/2)));
                        //csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
                        csCell.setCellValue(getTitleVale((String)musterMap.get("mhead")));
                        csCell.setCellStyle(titlestyle);
                    }
                    if(musterMap.get("rhead")!=null)
                    {
                        csCell=row.createCell(Short.parseShort(String.valueOf(excelData[0].length-1)));
                        //csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
                        csCell.setCellValue(getTitleVale((String)musterMap.get("rhead")));
                        csCell.setCellStyle(titlestyle);
                    }
                    n++;
                }
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return n;
		
	}
		
	/** 
	 * 取得字形
	 * @param flag 1页头, 2页尾, 3正文字体, 4标题
	 * 
	 */ 
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
                } else if("#fb[1]".equals(this.paramtervo.getTitle_fb())) {
                    fontEffect="2";  //粗体
                } else if("#fi[1]".equals(this.paramtervo.getTitle_fi())) {
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
	    /**
	     * 简单花名册新增方法 zyh 2019-03-07
	     * @param list
	     * @param map
	     * @return
	     */
		public String[][] getExcelData(ArrayList list,HashMap<String, Object> map){
			ArrayList fieldList=(ArrayList)list.get(0);
			HashMap   fieldHzMap=(HashMap)list.get(1);
			String[][] data=null;
			Connection a_conn =null;	
			ResultSet resultSet=null;
			try{
				a_conn = AdminDb.getConnection();	
				ContentDAO dao = new ContentDAO(a_conn);
				String musterType = (String) map.get("musterType");
				int totalCount=(Integer) map.get("totalCount");
				String showMusterSql=(String) map.get("showMusterSql");
				String orderBySql=(String) map.get("orderBySql");
				data=new String[totalCount+1][fieldList.size()];
				for(int i=0;i<fieldList.size();i++){
					data[0][i]=(String)fieldHzMap.get((String)fieldList.get(i));
				}
				StringBuffer sql = new StringBuffer();
				sql.append(showMusterSql);
				sql.append(" ");
				sql.append(orderBySql);
				resultSet=dao.search(sql.toString());	
				int j=1;
				//部门显示几级层级
				Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.conn);
				String uplevelStr = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
				if(uplevelStr==null||uplevelStr.length()==0){
					uplevelStr="0";
				}
				int upLevel = Integer.parseInt(uplevelStr);
				while(resultSet.next()){
				    data = insertData(data, j, resultSet, fieldList,upLevel);
					j++;
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				PubFunc.closeResource(resultSet);
				PubFunc.closeResource(a_conn);
			}
			return data;
		}
	/**
	 * 简单花名册新增方法将结果集数据插入进二维数组 zyh 2019-03-07
	 * @param data
	 * @param j
	 * @param resultSet
	 * @param fieldList
	 * @param upLevel
	 * @return
	 */
	private String[][] insertData(String[][] data, int j, ResultSet resultSet, ArrayList fieldList, int upLevel) {
		for(int i=0;i<fieldList.size();i++){
			String fieldName=(String)fieldList.get(i);
			if("recidx".equals(fieldName)){//序号
				data[j][i]=String.valueOf(j); 
			}else{
				FieldItem fielditem = DataDictionary.getFieldItem(fieldName);
				if(fielditem!=null){
					try{
					    String codesetid = fielditem.getCodesetid();
					    String type = fielditem.getItemtype();
						if ("A".equals(type)||"M".equals(type)) {
						    String codeitemid =resultSet.getString(fieldName)==null?"":resultSet.getString(fieldName); 
						    if (!"".equals(codeitemid)) {
	                            if (!"0".equals(codesetid)) {
	                                CodeItem codeitem = AdminCode.getCode(codesetid, codeitemid);
	                                if ("UN".equals(codesetid)&&null==codeitem) {
	                                    codeitem = AdminCode.getCode("UM", codeitemid);
	                                }
	                                if ("UM".equals(codesetid)&&null==codeitem) {
	                                    codeitem = AdminCode.getCode("UN", codeitemid);
	                                }
									//如果是部门，按设置显示部门层级
									ifUM:if("UM".equals(codesetid) && upLevel>0){
										codeitem = AdminCode.getCode(codesetid,codeitemid, upLevel);
										if(codeitem==null){
											break ifUM;
										}
									}
	                                data[j][i]=codeitem.getCodename();
	                            }else {
	                                data[j][i]=resultSet.getString(fieldName)==null?"":resultSet.getString(fieldName);
	                            }
	                        }else {
	                            data[j][i]= "";
	                        }
                        }else if ("N".equals(type)) {
                            if (resultSet.getString(fieldName)==null) {
                            	 data[j][i]="";
    						}else {
    							if (fielditem.getDecimalwidth()>0) {
    								double value = resultSet.getDouble(fieldName);
    								data[j][i]=new DecimalFormat("0.00").format(value);
    							}else {
    								String value = resultSet.getString(fieldName);
    								data[j][i]=value;
    							}
    						}
                        }else if ("D".equals(type)) {
                            Date dataVlue  = resultSet.getDate(fieldName);
                            data[j][i] = PubFunc.FormatDate(dataVlue);
                        }else {
                            data[j][i]=""; 
                        }
					}catch(Exception e){
						data[j][i]=""; 
					}
				}else{
					data[j][i]="";
				}
			}
		}
		return data;
	}
	//得到excel数据域
	public String[][] getExcelData(ArrayList list,String musterName)
	{
		ArrayList fieldList=(ArrayList)list.get(0);
        MusterBo musterbo=new MusterBo(this.conn,this.userview);
		ArrayList dblist=musterbo.getUserAllDBList();
		HashMap   fieldHzMap=(HashMap)list.get(1);
		String[][] data=null;
		Connection a_conn =null;	
		ResultSet resultSet=null;
		try
		{
			a_conn = AdminDb.getConnection();	
			ContentDAO dao = new ContentDAO(a_conn);
			int rows=0;
			if("1".equals(infor_Flag)&&"ALL".equals(dbpre)&&MusterBo.isHkyh()) {
                rows=musterbo.calcAllDBPersonCount(infor_Flag, dblist, tabid);
            } else {
			    resultSet=dao.search("select count(recidx) counts from "+musterName);	
			    if(resultSet.next()) {
                    rows=resultSet.getInt("counts");
                }
			}
			data=new String[rows+1][fieldList.size()];
			for(int i=0;i<fieldList.size();i++)
			{
				data[0][i]=(String)fieldHzMap.get((String)fieldList.get(i));
			}
			String sql="";
			if("1".equals(infor_Flag)&&"ALL".equals(dbpre)&&MusterBo.isHkyh()) {
                sql=musterbo.getDataSetAllDBSelectSql(infor_Flag, dblist, tabid);
            } else {
                sql="select * from "+musterName+" order by recidx ";
            }
			try {
				resultSet=dao.search(sql);	
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
			int j=1;
			while(resultSet.next()){
				for(int i=0;i<fieldList.size();i++){
					String fieldName=(String)fieldList.get(i);
					if("recidx".equals(fieldName)){
						data[j][i]=String.valueOf(j); 
					}else{
						FieldItem fielditem = DataDictionary.getFieldItem(fieldName);
						if(fielditem!=null){
							try{
								data[j][i]=resultSet.getString(fieldName)==null?"":resultSet.getString(fieldName); 
							}catch(Exception e){
								data[j][i]=""; 
							}
						}else{
							data[j][i]="";
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
		finally
		{
			PubFunc.closeResource(resultSet);
			PubFunc.closeResource(a_conn);
			System.gc();
		}
		return data;
	}
	
	public String getTitleVale(String value)
	{
		String a_value=value;
		if(a_value.indexOf("&[页码]")!=-1) {
            a_value=a_value.replaceAll("&\\[页码\\]","1");
        }
		if(a_value.indexOf("&[制作人]")!=-1  ) {
            a_value=a_value.replaceAll("&\\[制作人\\]",this.userName);
        }
		if(a_value.indexOf("&[日期]")!=-1)
		{
			SimpleDateFormat d=new SimpleDateFormat("yyyy-MM-dd");
			 a_value=a_value.replaceAll("&\\[日期\\]",d.format(new java.util.Date()));
		}
		if(a_value.indexOf("&[时间]")!=-1)
		{
			SimpleDateFormat   formatter   =   new   SimpleDateFormat("HH:mm:ss");
			a_value=a_value.replaceAll("&\\[时间\\]",formatter.format(new java.util.Date()));
		}
		if(a_value.indexOf("&[总人数]")!=-1)
		{
			a_value=a_value.replaceAll("&\\[总人数\\]",String.valueOf(this.totalNum));
		}
        if(a_value.indexOf("&[YYYY年YY月]")!=-1)
        {
            a_value=a_value.replaceAll("&\\[YYYY年YY月\\]", PubFunc.FormatDate(new java.util.Date(), "yyyy年MM月"));
        }
		//20191223 解决简单花名册--》页面设置--》页头、页尾内容中的 &[总行数] 显示问题
		if(a_value.indexOf("&["+ResourceFactory.getProperty("hmuster.label.totalRows")+"]") != -1){
			a_value=a_value.replaceAll("&\\["+ResourceFactory.getProperty("hmuster.label.totalRows")+"\\]",String.valueOf(this.totalNum));
		}
		return a_value;
	}
	
	//得到花名册的标题 上表头 下表头信息
	public HashMap getMusterExtendInfo(RecordVo lnameVo)
	{
		HashMap musterInfo=new HashMap();		
		
		
		String a_title=lnameVo.getString("hzname");
	    if(this.paramtervo!=null&&this.paramtervo.getTitle_fw().length()>0) {
            a_title=this.paramtervo.getTitle_fw();
        }
	    musterInfo.put("title",a_title);
	    
			if(lnameVo.getString("lhead")!=null&&lnameVo.getString("lhead").length()>1)
			{
				musterInfo.put("lhead",lnameVo.getString("lhead"));
			}
			if(lnameVo.getString("mhead")!=null&&lnameVo.getString("mhead").length()>1)
			{
				musterInfo.put("mhead",lnameVo.getString("mhead"));
			}
			if(lnameVo.getString("rhead")!=null&&lnameVo.getString("rhead").length()>1)
			{
				musterInfo.put("rhead",lnameVo.getString("rhead"));
			}			       
			if(lnameVo.getString("lfoot")!=null&&lnameVo.getString("lfoot").length()>1)
			{
				musterInfo.put("lfoot",lnameVo.getString("lfoot"));
			}
			if(lnameVo.getString("mfoot")!=null&&lnameVo.getString("mfoot").length()>1)
			{
				musterInfo.put("mfoot",lnameVo.getString("mfoot"));
			}
		    if(lnameVo.getString("rfoot")!=null&&lnameVo.getString("rfoot").length()>1)
			{
		    	musterInfo.put("rfoot",lnameVo.getString("rfoot"));
			}

		return musterInfo;
	}
	
	
	
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
	}
	
	
	
	
	
	/**
	 * 取得花名册的列信息
	 * @param tabid
	 * @return
	 */
	public ArrayList getMusterField(String tabid)
	{
		ArrayList list=new ArrayList();
		ArrayList fieldList=new ArrayList();
		HashMap   fieldHzMap=new HashMap();
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try
		{
			rowSet=dao.search("select Field_name,colHz,field_type from  lbase where tabid="+tabid+" and Width<>0 order by baseid");
			fieldList.add("recidx");
			fieldHzMap.put("recidx",ResourceFactory.getProperty("kh.field.seq"));
			while(rowSet.next())
			{
				
				String field_name = rowSet.getString("Field_name");
				String setname = field_name.substring(0, field_name.indexOf("."));
				field_name=field_name.substring(field_name.indexOf(".")+1);
				FieldSet set=DataDictionary.getFieldSetVo(setname);//zgd 2014-7-16确定表是否被已构库掉
				if(set==null) {
                    continue;
                }
				FieldItem item=DataDictionary.getFieldItem(field_name.substring(0,5), setname);//缺陷2374 zgd 2014-7-16 花名册只在指标体系中取指标，不在业务字典中选取。指定表名。
				if(item==null) {
                    continue;
                }
				String pri = userview.analyseFieldPriv(field_name);
				if(pri==null|| "0".equals(pri)) {
                    continue;
                }
				String colHz=rowSet.getString("colHz");
				fieldHzMap.put(field_name,colHz);
				fieldList.add(field_name);
			}
			list.add(fieldList);
			list.add(fieldHzMap);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	
	public static void main(String[] args)
	{
		HSSFWorkbook wb = null;
		try {
			 wb = new HSSFWorkbook();
			 //建立新HSSFWorkbook对象
			 HSSFSheet sheet = wb.createSheet("new sheet");
			 HSSFRow row = sheet.createRow((short)0);
			 //建立新行 
			 HSSFCell cell = row.createCell((short)0);
			 //建立新cell
			 cell.setCellValue(1);			
			 row.createCell((short)1).setCellValue(1.2);
			 //设置cell浮点类型的值
			 HSSFCell csCell =row.createCell((short)2);
			// csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
			//	csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
				csCell.setCellValue("你说什么");
			// row.createCell((short)2).setCellValue("test");
			 FileOutputStream fileOut = new FileOutputStream("d:\\workbook.xls");
			 wb.write(fileOut);
			 fileOut.close();			
			 System.out.println("文件生成..."); 
			 }
		catch (Exception e) {
			 System.out.println("已运行 xlCreate() : " + e);
		} finally {
			PubFunc.closeResource(wb);
		}
	}
	/**
     * 设置excel表格效果 xlsx 
     * @param styles 设置不同的效果
     * @param workbook 新建的表格
     */
	public XSSFCellStyle styleXlsx(XSSFWorkbook workbook,int styles){
		XSSFCellStyle style = workbook.createCellStyle();
		switch(styles){
		
		case 0:
				style.setAlignment(HorizontalAlignment.LEFT);
		        break;			
		case 1:
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);
				style.setVerticalAlignment(VerticalAlignment.CENTER);
				style.setAlignment(HorizontalAlignment.CENTER );
				break;
		case 2:
				style.setAlignment(HorizontalAlignment.LEFT);
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);	                    	
				break;
		case 3:
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);
				style.setFillPattern(FillPatternType.ALT_BARS);
				style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);               	
				break;		
		case 4:
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);
				style.setFillPattern(FillPatternType.ALT_BARS);
				style.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
			  break;
		case 5:
			style.setVerticalAlignment(VerticalAlignment.CENTER);
			style.setAlignment(HorizontalAlignment.CENTER );
			break;
		default:		
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
     * 设置excel表格效果
     * @param styles 设置不同的效果
     * @param workbook 新建的表格
     */
	public HSSFCellStyle style(HSSFWorkbook workbook,int styles){
		HSSFCellStyle style = workbook.createCellStyle();
		
		
		switch(styles){
		
		case 0:
				//HSSFFont fonttitle = fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.black.font"),15);
				//fonttitle.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);//加粗 
				//style.setFont(fonttitle);
				style.setAlignment(HorizontalAlignment.LEFT );
		        break;			
		case 1:
				//style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),10));
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);
				style.setVerticalAlignment(VerticalAlignment.CENTER);
				style.setAlignment(HorizontalAlignment.CENTER );
				break;
		case 2:
				//style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),12));
				style.setAlignment(HorizontalAlignment.LEFT );
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);	                    	
				break;
		case 3:
				//style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),12));
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);
				style.setFillPattern(FillPatternType.ALT_BARS);
				style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);               	
				break;		
		case 4:
				//style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),12));
				style.setBorderBottom(BorderStyle.THIN);
				style.setBorderLeft(BorderStyle.THIN);
				style.setBorderRight(BorderStyle.THIN);
				style.setBorderTop(BorderStyle.THIN);
				style.setFillPattern(FillPatternType.ALT_BARS);
				style.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
			  break;
		case 5:
			//style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),10));
			style.setVerticalAlignment(VerticalAlignment.CENTER);
			style.setAlignment(HorizontalAlignment.CENTER );
			break;
		default:		
				//style.setFont(fonts(workbook,ResourceFactory.getProperty("gz.gz_acounting.m.font"),12));
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
	
	
	private XSSFFont getXlsxFont(XSSFWorkbook workbook,int flag){

	    XSSFFont font = workbook.createFont();
        String t_fontfamilyname=ResourceFactory.getProperty("gz.gz_acounting.m.font");
        String t_fontEffect="0";
        int    t_fontSize=10;  
        String t_color="#000000";       
        String  t_underLine="#fu[0]";
        String  t_strikethru="#fs[0]";
        if(this.paramtervo!=null){
        	t_fontEffect=getFontEffect(flag);
            if (flag == 1) {
                if(this.paramtervo.getHead_fn().length()>0) {
                    t_fontfamilyname=this.paramtervo.getHead_fn();
                }
                if(this.paramtervo.getHead_fz().length()>0) {
                    t_fontSize=Integer.parseInt(this.paramtervo.getHead_fz());
                }
                if(this.paramtervo.getHead_fc().length()>0) {
                    t_color=this.paramtervo.getHead_fc();
                }
                if(this.paramtervo.getHead_fu()!=null&&this.paramtervo.getHead_fu().length()>0) {
                    t_underLine=this.paramtervo.getHead_fu();
                }
                if(this.paramtervo.getHead_fs()!=null&&this.paramtervo.getHead_fs().length()>0) {
                    t_strikethru=this.paramtervo.getHead_fs();
                }
            }
            else if (flag == 2) {
                if(this.paramtervo.getTile_fn().length()>0) {
                    t_fontfamilyname=this.paramtervo.getTile_fn();
                }
                if(this.paramtervo.getTile_fz().length()>0) {
                    t_fontSize=Integer.parseInt(this.paramtervo.getTile_fz());
                }
                if(this.paramtervo.getTile_fc().length()>0) {
                    t_color=this.paramtervo.getTile_fc();
                }
                if(this.paramtervo.getTile_fu()!=null&&this.paramtervo.getTile_fu().length()>0) {
                    t_underLine=this.paramtervo.getTile_fu();
                }
                if(this.paramtervo.getTile_fs()!=null&&this.paramtervo.getTile_fs().length()>0) {
                    t_strikethru=this.paramtervo.getTile_fs();
                }
            }
            else if (flag == 3) {
                if(this.paramtervo.getBody_fn().length()>0) {
                    t_fontfamilyname=this.paramtervo.getBody_fn();
                }
                if(this.paramtervo.getBody_fz().length()>0) {
                    t_fontSize=Integer.parseInt(this.paramtervo.getBody_fz());
                }
                if(this.paramtervo.getBody_fc().length()>0) {
                    t_color=this.paramtervo.getBody_fc();
                }
                if(this.paramtervo.getBody_fu()!=null&&this.paramtervo.getBody_fu().length()>0) {
                    t_underLine=this.paramtervo.getBody_fu();
                }
                if(this.paramtervo.getBody_fi()!=null&&this.paramtervo.getBody_fi().length()>0) {
                    t_strikethru=this.paramtervo.getBody_fi();
                }
            }
            else if (flag == 4) {
            	if(this.paramtervo.getTitle_fn().length()>0) {
                    t_fontfamilyname=this.paramtervo.getTitle_fn();
                }
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
        }
      //wangcq 2014-12-02 begin设置标题颜色
		//处理把颜色值转换成十六进制并放入一个数 
		int[] color=new int[3]; 
		color[0]=Integer.parseInt(t_color.substring(1, 3), 16); 
		color[1]=Integer.parseInt(t_color.substring(3, 5), 16); 
		color[2]=Integer.parseInt(t_color.substring(5, 7), 16); 
		//自定义颜色 
//		HSSFPalette palette = workbook.getCustomPalette(); 
		short index = (short)( 8);  //8为黑色代号，选从9到12分别为页头, 页尾, 正文字体, 标题代号
//		palette.setColorAtIndex(index,(byte)color[0], (byte)color[1], (byte)color[2]); 
		font.setColor(index);//设置字体颜色
		//wangcq 2014-12-02 end
        font.setFontName(t_fontfamilyname);//设置字体种类
        if("2".equals(t_fontEffect)){
            font.setBold(true); //设置字体
        }else if("3".equals(t_fontEffect)){
            font.setItalic( true ); // 是否使用斜体
        }else if("4".equals(t_fontEffect)){
            font.setBold(true); //设置字体
            font.setItalic( true ); // 是否使用斜体
        }
        if("#fu[1]".equals(t_underLine)){
            font.setUnderline(XSSFFont.U_SINGLE);
        }
        if("#fs[1]".equals(t_strikethru)){
            font.setStrikeout(true);
        }
        font.setFontHeightInPoints((short)t_fontSize);//设置字体大小

	    return font;
	
	}
	
	/**
	 * 取字体
	 * @param workbook
	 * @param flag 1页头, 2页尾, 3正文字体, 4标题
	 * @return
	 * @see #getFontEffect(int)
	 */
	private HSSFFont getFont(HSSFWorkbook workbook, int flag) {
	    HSSFFont font = workbook.createFont();
        String t_fontfamilyname=ResourceFactory.getProperty("gz.gz_acounting.m.font");
        String t_fontEffect="0";
        int    t_fontSize=10;  
        String t_color="#000000";       
        String  t_underLine="#fu[0]";
        String  t_strikethru="#fs[0]";
        if(this.paramtervo!=null){
        	t_fontEffect=getFontEffect(flag);
            if (flag == 1) {
                if(this.paramtervo.getHead_fn().length()>0) {
                    t_fontfamilyname=this.paramtervo.getHead_fn();
                }
                if(this.paramtervo.getHead_fz().length()>0) {
                    t_fontSize=Integer.parseInt(this.paramtervo.getHead_fz());
                }
                if(this.paramtervo.getHead_fc().length()>0) {
                    t_color=this.paramtervo.getHead_fc();
                }
                if(this.paramtervo.getHead_fu()!=null&&this.paramtervo.getHead_fu().length()>0) {
                    t_underLine=this.paramtervo.getHead_fu();
                }
                if(this.paramtervo.getHead_fs()!=null&&this.paramtervo.getHead_fs().length()>0) {
                    t_strikethru=this.paramtervo.getHead_fs();
                }
            }
            else if (flag == 2) {
                if(this.paramtervo.getTile_fn().length()>0) {
                    t_fontfamilyname=this.paramtervo.getTile_fn();
                }
                if(this.paramtervo.getTile_fz().length()>0) {
                    t_fontSize=Integer.parseInt(this.paramtervo.getTile_fz());
                }
                if(this.paramtervo.getTile_fc().length()>0) {
                    t_color=this.paramtervo.getTile_fc();
                }
                if(this.paramtervo.getTile_fu()!=null&&this.paramtervo.getTile_fu().length()>0) {
                    t_underLine=this.paramtervo.getTile_fu();
                }
                if(this.paramtervo.getTile_fs()!=null&&this.paramtervo.getTile_fs().length()>0) {
                    t_strikethru=this.paramtervo.getTile_fs();
                }
            }
            else if (flag == 3) {
                if(this.paramtervo.getBody_fn().length()>0) {
                    t_fontfamilyname=this.paramtervo.getBody_fn();
                }
                if(this.paramtervo.getBody_fz().length()>0) {
                    t_fontSize=Integer.parseInt(this.paramtervo.getBody_fz());
                }
                if(this.paramtervo.getBody_fc().length()>0) {
                    t_color=this.paramtervo.getBody_fc();
                }
                if(this.paramtervo.getBody_fu()!=null&&this.paramtervo.getBody_fu().length()>0) {
                    t_underLine=this.paramtervo.getBody_fu();
                }
                if(this.paramtervo.getBody_fi()!=null&&this.paramtervo.getBody_fi().length()>0) {
                    t_strikethru=this.paramtervo.getBody_fi();
                }
            }
            else if (flag == 4) {
            	if(this.paramtervo.getTitle_fn().length()>0) {
                    t_fontfamilyname=this.paramtervo.getTitle_fn();
                }
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
        }
      //wangcq 2014-12-02 begin设置标题颜色
		//处理把颜色值转换成十六进制并放入一个数 
		int[] color=new int[3]; 
		color[0]=Integer.parseInt(t_color.substring(1, 3), 16); 
		color[1]=Integer.parseInt(t_color.substring(3, 5), 16); 
		color[2]=Integer.parseInt(t_color.substring(5, 7), 16); 
		//自定义颜色 
		HSSFPalette palette = workbook.getCustomPalette(); 
		short index = (short)(flag + 8);  //8为黑色代号，选从9到12分别为页头, 页尾, 正文字体, 标题代号
		palette.setColorAtIndex(index,(byte)color[0], (byte)color[1], (byte)color[2]); 
		font.setColor(index);//设置字体颜色
		//wangcq 2014-12-02 end
        font.setFontName(t_fontfamilyname);//设置字体种类
        if("2".equals(t_fontEffect)){
            font.setBold(true); //设置字体
        }else if("3".equals(t_fontEffect)){
            font.setItalic( true ); // 是否使用斜体
        }else if("4".equals(t_fontEffect)){
            font.setBold(true); //设置字体
            font.setItalic(true); // 是否使用斜体
        }
        if("#fu[1]".equals(t_underLine)){
            font.setUnderline(HSSFFont.U_SINGLE);
        }
        if("#fs[1]".equals(t_strikethru)){
            font.setStrikeout(true);
        }
        font.setFontHeightInPoints((short)t_fontSize);//设置字体大小

	    return font;
	}

	public UserView getUserview() {
		return userview;
	}

	public void setUserview(UserView userview) {
		this.userview = userview;
	}

    public String getInfor_Flag() {
        return infor_Flag;
    }

    public void setInfor_Flag(String infor_Flag) {
        this.infor_Flag = infor_Flag;
    }

    public String getDbpre() {
        return dbpre;
    }

    public void setDbpre(String dbpre) {
        this.dbpre = dbpre;
    }

    public String getTabid() {
        return tabid;
    }

    public void setTabid(String tabid) {
        this.tabid = tabid;
    }	
}
