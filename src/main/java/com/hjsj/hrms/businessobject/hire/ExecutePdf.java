package com.hjsj.hrms.businessobject.hire;

import com.hjsj.hrms.constant.FontFamilyType;
import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.interfaces.report.ReportParseXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import javax.sql.RowSet;
import java.awt.*;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExecutePdf {
	private Connection conn = null;
	private ReportParseVo paramtervo=null;
	private float proportion=0.24f;
	private float t_margin=0;
	private float b_margin=0;
	private float l_margin=0;
	
	private float r_margin=0;
	private float page_width=0;
	private float page_height=0;
	private float body_width=0;
	private float body_height=0;
	private float banlance=10;     //表头行与表体行的差值（像素）
	private float rowHeigth=15;    //行高
	private int page_rows=0;     //每页的行数
	private String tableName="";
	private String whl_sql="";
	private String operateObject="";
	private String datetime=""; //日期
	private String times="";  //时间
	private int total=0;//总人数
	private String producer="";//制作人
	private int pageno=0;//页码
	private UserView userView=null;
	
	private HashMap employerNameMap=new HashMap();    //针对面试安排信息 模块
	private HashMap planCountMap=new HashMap();       //针对招聘总结     模块
	

	
	public ExecutePdf(Connection conn)
	{
		this.conn=conn;
	}

	
	public ExecutePdf(Connection conn,String tableName,String userName)
	{
		this.conn=conn;
		this.tableName=tableName;
		this.producer=userName;
		setPagePrintStyle(userName);
		initInfo();
	}
	public ExecutePdf(Connection conn,String tableName,UserView userView)
	{
		this.conn=conn;
		this.tableName=tableName;
		this.userView=userView;
		this.producer=userView.getUserFullName();
		setPagePrintStyle(userView.getUserName());
		initInfo();
	}
	
	public void SetParamtervo(String xmlContent, String  path)
	{
		ReportParseXml parseXml = new ReportParseXml();	 
		if(xmlContent!=null&&xmlContent.length()>1)
			this.paramtervo = parseXml.ReadOutParseXml(xmlContent,path);
		try {
			String dateHead = paramtervo.getHead_flw();
			if ("&[YYYY年YY月]".equalsIgnoreCase(dateHead)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");
				dateHead = sdf.format(new Date());
				paramtervo.setHead_flw(dateHead);
			}
			dateHead = paramtervo.getHead_fmw();
			if ("&[YYYY年YY月]".equalsIgnoreCase(dateHead)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");
				dateHead = sdf.format(new Date());
				paramtervo.setHead_fmw(dateHead);
			}
			dateHead = paramtervo.getHead_frw();
			if ("&[YYYY年YY月]".equalsIgnoreCase(dateHead)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");
				dateHead = sdf.format(new Date());
				paramtervo.setHead_frw(dateHead);
			}
			String dateTile = paramtervo.getTile_flw();
			if ("&[YYYY年YY月]".equalsIgnoreCase(dateTile)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");
				dateTile = sdf.format(new Date());
				paramtervo.setTile_flw(dateTile);
			}
			dateTile = paramtervo.getTile_frw();
			if ("&[YYYY年YY月]".equalsIgnoreCase(dateTile)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");
				dateTile = sdf.format(new Date());
				paramtervo.setTile_frw(dateTile);
			}
			dateTile = paramtervo.getTile_fmw();
			if ("&[YYYY年YY月]".equalsIgnoreCase(dateTile)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");
				dateTile = sdf.format(new Date());
				paramtervo.setTile_fmw(dateTile);
			}
		} catch (Exception e) {
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
			if(rowSet.next())
				xmlContent=Sql_switcher.readMemo(rowSet,"str_value");
			String xpath="/param/pageoptions/report[@name='"+username+"']";
			SetParamtervo(xmlContent,xpath);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		} finally {
		    PubFunc.closeResource(rowSet);
		}
	}
	

	
	/**
	 * 
	 * @param output_stream
	 * @param fieldWidths 列宽 （像素）
	 * @param tablename  表名
	 * @param operateObject  1:单表操作  2：多表操作
	 */
	public String createPdf(String userName,String fieldWidths,String tablename,ArrayList a_fieldList,String whl_sql,String operateObject)
	{
		if(whl_sql.length()>8)
			this.whl_sql=whl_sql;
		this.operateObject=operateObject;
		Document document =getDocument();							
		String    pdfName="";
		FileOutputStream _fos = null;
		PdfWriter writer = null;
		try {
			pdfName = tablename + "_" + userName + ".pdf";
//			pdfName="interviewOutFileName_Pdf_12345"+userName+".pdf";  //固定文件名，免得临时文件夹中产生的文件过多
			_fos = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator")+ pdfName);
			writer = PdfWriter.getInstance(document, _fos);
			document.open();
			String[] field_widths=null;
			if(fieldWidths.indexOf("/")!=-1)
				field_widths=fieldWidths.split("/");
			else{
				field_widths=new String[1];
				field_widths[0]=fieldWidths;
			}
		
			//缩小比列
			for(int i=0;i<field_widths.length;i++)
			{
				if(!"undefined".equals(field_widths[i]))
					field_widths[i]=String.valueOf((Float.parseFloat(field_widths[i]))*1);
				else 
					field_widths[i]="10";
			}
			
			
			
			PdfPTable datatable = new PdfPTable(field_widths.length);
			ArrayList list=getTableField(tablename,a_fieldList);
			ArrayList fieldList=(ArrayList)list.get(0);
			HashMap   fieldHzMap=(HashMap)list.get(1);
			HashMap   fieldTypeMap=(HashMap)list.get(2);
			HashMap   fieldCodeMap=(HashMap)list.get(3);
	
			setTable(writer,document,field_widths,fieldList,fieldHzMap,tablename,fieldTypeMap,fieldCodeMap);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		} finally {
		    PubFunc.closeIoResource(document);
			PubFunc.closeIoResource(writer);
			PubFunc.closeIoResource(_fos);
		}
		return pdfName;
	}
	
	/**
	 * 
	 * @param output_stream
	 * @param fieldWidths 列宽 （像素）
	 * @param tablename  表名
	 * @param operateObject  1:单表操作  2：多表操作
	 */
	public String createTabPdf(String userName,String fieldWidths,String tablename,ArrayList a_fieldList,String whl_sql,String operateObject)
	{
		if(whl_sql.length()>8)
			this.whl_sql=whl_sql;
		this.operateObject=operateObject;
		Document document =getDocument();							
		String    pdfName="";
		FileOutputStream fos = null;
		PdfWriter writer  =null;
		try {
			fos = new FileOutputStream(System.getProperty("java.io.tmpdir")
					+ System.getProperty("file.separator")+ pdfName);
			pdfName= "resume_" + this.userView.getUserName()+ ".pdf";  //固定文件名，免得临时文件夹中产生的文件过多
			writer = PdfWriter.getInstance(document,fos);
			document.open();
			String[] field_widths=null;
			if(fieldWidths.indexOf("/")!=-1)
				field_widths=fieldWidths.split("/");
			else{
				field_widths=new String[1];
				field_widths[0]=fieldWidths;
			}
		
			//缩小比列
			for(int i=0;i<field_widths.length;i++)
			{
				if(!"undefined".equals(field_widths[i]))
					field_widths[i]=String.valueOf((Float.parseFloat(field_widths[i]))*1);
				else 
					field_widths[i]="10";
			}
			
			
			
			PdfPTable datatable = new PdfPTable(field_widths.length);
			ArrayList list=getTableField(tablename,a_fieldList);
			ArrayList fieldList=(ArrayList)list.get(0);
			HashMap   fieldHzMap=(HashMap)list.get(1);
			HashMap   fieldTypeMap=(HashMap)list.get(2);
			HashMap   fieldCodeMap=(HashMap)list.get(3);
	
			setTabTable(writer,document,field_widths,fieldList,fieldHzMap,tablename,fieldTypeMap,fieldCodeMap);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
		    PubFunc.closeResource(document);
		    PubFunc.closeResource(writer);
			PubFunc.closeResource(fos);
		}
		return pdfName;
	}
	
	
	/**
	 * 写表体
	 * @param fieldList
	 * @param datatable
	 * @param tableName
	 * @param fieldTypeMap
	 */
	public void setTable(PdfWriter writer,Document document,String[] field_widths,ArrayList fieldList,HashMap fieldHzMap,String tableName,HashMap fieldTypeMap,HashMap fieldCodeMap)
	{
		
		ArrayList tableHeaderList=getTableHeaderList(field_widths,fieldList,fieldHzMap,fieldTypeMap);
		boolean   isOverTop=tableHeaderList.size()>1;               //横向是否超过一页
		PdfPTable table =null;
		PdfPCell cell = null;
		HashMap namemap=this.getDbname();
		String fontfamilyname=ResourceFactory.getProperty("hmuster.label.fontSt");
        String fontEffect="0";
        int    fontSize=5;
        String color="#000000";
        String underLine="#fu[0]";
        if(this.paramtervo!=null)
        {
        	 if(this.paramtervo.getBody_fn().length()>0)
        		 fontfamilyname=this.paramtervo.getBody_fn();
        	 fontEffect=getFontEffect(3);
        	 if(this.paramtervo.getBody_fz().length()>0)
        	 	fontSize=Integer.parseInt(this.paramtervo.getBody_fz());
        	 if(this.paramtervo.getBody_fc().length()>0)
        		 color=this.paramtervo.getBody_fc();
        	 if(this.paramtervo.getBody_fu()!=null&&this.paramtervo.getBody_fu().length()>0)
        		 underLine=this.paramtervo.getBody_fu();
        	 
        }
        Font font = FontFamilyType.getFont(fontfamilyname,fontEffect,fontSize); // 生成字体样式
 		font.setColor(new Color(Integer.parseInt(color.substring(1,3),16),Integer.parseInt(color.substring(3,5),16),Integer.parseInt(color.substring(5,7),16)));
 		if("#fu[1]".equals(underLine))
 			font.setStyle(Font.UNDERLINE);

 	//	Connection a_conn =null;	
	//	ResultSet resultSet=null;
	//	Statement stmt=null;
		RowSet resultSet=null;
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
		    String hireMajor="";
		    boolean hireMajorIsCode=false;
		    FieldItem hireMajoritem=null;
		    //zxj 20170714 除这两个外，其它模块的表和招聘无关
		    if("zp_test_template".equalsIgnoreCase(tableName) || "personnelEmploy".equalsIgnoreCase(tableName)) {
    			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.conn,"1");
    			HashMap map=parameterXMLBo.getAttributeValues();
    			if(map.get("hireMajor")!=null)
    				hireMajor=(String)map.get("hireMajor");  //招聘专业指标
    			if(hireMajor.length()>0)
    			{
    				hireMajoritem=DataDictionary.getFieldItem(hireMajor.toLowerCase());
    				if(hireMajoritem.getCodesetid().length()>0&&!"0".equals(hireMajoritem.getCodesetid()))
    					hireMajorIsCode=true;
    			}
		    }
		    
			if("1".equals(this.operateObject))
				resultSet=dao.search("select * from "+tableName+" "+whl_sql);			
			else
				resultSet=dao.search(whl_sql);	
					
			
			int i=0;  												//行数
			int flag=1;
		
			ArrayList dataList=new ArrayList();
			while(resultSet.next())
			{
				i++;
				this.total++;
				HashMap dataMap=new HashMap();
				for(Iterator t=fieldList.iterator();t.hasNext();)
				{
					String fieldName=(String)t.next();
					if("recidx".equals(fieldName))
						dataMap.put(fieldName,String.valueOf(i));
					else
					{
						
						boolean  isNotNull=false;		
						if(!"sendmail".equalsIgnoreCase(fieldName))
						{
							if((!"D".equalsIgnoreCase((String)fieldTypeMap.get(fieldName))
									&&!"M".equalsIgnoreCase((String)fieldTypeMap.get(fieldName)))
									&&!"employedcount".equals(fieldName)&&resultSet.getString(fieldName)!=null)
							{
								isNotNull=true;
							}
							else if("D".equalsIgnoreCase((String)fieldTypeMap.get(fieldName)))
							{
								if("z03".equals(fieldName.substring(0,3))&&resultSet.getDate(fieldName)!=null)
								{
									isNotNull=true;
								}
								else if(resultSet.getDate(fieldName)!=null)
									isNotNull=true;
							}
							else if("M".equalsIgnoreCase((String)fieldTypeMap.get(fieldName)))
							{
								isNotNull=true;
							}
						}
						if("employedcount".equals(fieldName))
							isNotNull=true;
						
						if(isNotNull)
						{
							if("A".equalsIgnoreCase((String)fieldTypeMap.get(fieldName))&&!"0".equalsIgnoreCase((String)fieldCodeMap.get(fieldName)))
							{
								
								
								if("zp_test_template".equalsIgnoreCase(tableName)&& "zp_pos_id".equalsIgnoreCase(fieldName)&&hireMajor!=null&&hireMajor.length()>0&&resultSet.getString("Z0336")!=null&& "01".equals(resultSet.getString("Z0336")))
								{
									String value="";
									if(hireMajorIsCode)											
										value=AdminCode.getCodeName(hireMajoritem.getCodesetid(),resultSet.getString(fieldName));
									else
										value=resultSet.getString(fieldName);
									dataMap.put(fieldName,value);
								}
								else if("personnelEmploy".equalsIgnoreCase(tableName)&& "z0311".equalsIgnoreCase(fieldName)&&hireMajor!=null&&hireMajor.length()>0&&resultSet.getString("Z0336")!=null&& "01".equals(resultSet.getString("Z0336")))
								{
									String value="";
									if(hireMajorIsCode)											
										value=AdminCode.getCodeName(hireMajoritem.getCodesetid(),resultSet.getString(fieldName));
									else
										value=resultSet.getString(fieldName);
									dataMap.put(fieldName,value);
								}
								else
									dataMap.put(fieldName,AdminCode.getCode((String)fieldCodeMap.get(fieldName),resultSet.getString(fieldName))!=null?AdminCode.getCode((String)fieldCodeMap.get(fieldName),resultSet.getString(fieldName)).getCodename():"");
							}
							else
							{
								if("N".equalsIgnoreCase((String)fieldTypeMap.get(fieldName)))
								{
									if("engagePlan".equalsIgnoreCase(this.tableName)&& "z0117".equalsIgnoreCase(fieldName))  //实招人数 指标
									{
										dataMap.put(fieldName,(String)this.planCountMap.get(resultSet.getString("z0101")));
									}
									else if("posList".equalsIgnoreCase(tableName)&& "employedcount".equalsIgnoreCase(fieldName))  //实招人数 指标
									{
										dataMap.put(fieldName,(String)this.planCountMap.get(resultSet.getString("z0301")));
									}
									else
									{
										if(resultSet.getFloat(fieldName)==0)
										{
											dataMap.put(fieldName," ");
										}
										else
										{			
											if("score".equalsIgnoreCase(fieldName))
											{
												String value=PubFunc.round(resultSet.getString(fieldName),2);
												dataMap.put(fieldName,value);
											}
											else
												dataMap.put(fieldName,resultSet.getString(fieldName));
										}
									}
								}
								else if("M".equalsIgnoreCase((String)fieldTypeMap.get(fieldName)))
								{
									
									//if(!this.tableName.equalsIgnoreCase("z05"))
//									{
										String values = Sql_switcher.readMemo(resultSet,fieldName);
										values=values!=null?values:"";
										values=values.replaceAll("%26lt;","<").replaceAll("%26gt;",">");
										
										dataMap.put(fieldName,values);
//									}
									
									
								}
								else if("D".equalsIgnoreCase((String)fieldTypeMap.get(fieldName)))
								{
									
									if(resultSet.getDate(fieldName)!=null)
									{
										if("Z0509".equalsIgnoreCase(fieldName))
										{
											Date date=resultSet.getDate(fieldName);	
											SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
											dataMap.put(fieldName,bartDateFormat.format(date));
										}
										else
										{
											if("z03".equalsIgnoreCase(this.tableName)&& "z0307".equalsIgnoreCase(fieldName))
											{
												SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd");
												dataMap.put(fieldName,bartDateFormat.format(resultSet.getDate(fieldName)));
											}
											else
											{
											
													Date date=resultSet.getDate(fieldName);	
													SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd");
													dataMap.put(fieldName,bartDateFormat.format(date));
												
											}
										}
									}
									else
										dataMap.put(fieldName,"");
									
								}
								else
								{
										
									if(!"z05".equalsIgnoreCase(this.tableName))
									{
										String values = resultSet.getString(fieldName);
										values=values!=null?values:"";
										values=values.replaceAll("%26lt;","<").replaceAll("%26gt;",">");
										
										dataMap.put(fieldName,values);
									}
									else
									{
										
										 if("z0505".equalsIgnoreCase(fieldName)|| "z0507".equalsIgnoreCase(fieldName))
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
													if(namemap.get(aa_z0505[a].substring(0,3).toUpperCase())!=null)
														z0505.append(","+(String)this.employerNameMap.get(aa_z0505[a].toUpperCase()));
													else{
														z0505.append(","+(String)this.employerNameMap.get("USR"+aa_z0505[a]));
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
													if(namemap.get(aa_z0507[a].substring(0,3).toUpperCase())!=null)
														z0507.append(","+(String)this.employerNameMap.get(aa_z0507[a].toUpperCase()));
													else{
														z0507.append(","+(String)this.employerNameMap.get("USR"+aa_z0507[a]));
													}
													a++;
												}
											}		
											if(z0505.length()>1)
												dataMap.put("z0505",z0505.substring(1));
											else
												dataMap.put("z0505",z0505.toString());
											if(z0507.length()>1)
												dataMap.put("z0507",z0507.substring(1));
											else
												dataMap.put("z0507",z0507.toString());
										}
										 else if("z0511".equalsIgnoreCase(fieldName))
										 {
											 String context="";
											if(resultSet.getString("z0511")!=null)
											{
												String tt=resultSet.getString("z0511");
												if(tt.indexOf("`")!=-1)
												{
													context = this.getName(tt.substring(0,3).toUpperCase(),tt.substring(4));
													
												}
												else
												{
													context=tt;
												}
											}
											dataMap.put(fieldName,context);
										 }
										else
											dataMap.put(fieldName,resultSet.getString(fieldName));
									}
								
								}
							}
						
						}
						else
						{
							if(fieldCodeMap.get(fieldName)!=null&& "36".equals((String)fieldCodeMap.get(fieldName)))
								dataMap.put(fieldName,"未选");
							else
								dataMap.put(fieldName," ");
						}
					}
				}
				dataList.add(dataMap);
				if(i%this.page_rows==0)
				{
					pageno++;
					writePage(document,writer,dataList,tableHeaderList,fieldTypeMap,fieldHzMap,flag,font);
					dataList.clear();
					flag++;
				}				
			}
			if(i<this.page_rows||i>this.page_rows)
				pageno++;
				writePage(document,writer,dataList,tableHeaderList,fieldTypeMap,fieldHzMap,flag,font);
			
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
		
		

	}
	public String getName(String pre,String a0100)
	{
		String name="";
		RowSet rs = null;
		try
		{
			String sql = " select a0101 from "+pre+"a01 where a0100='"+a0100+"'";
			ContentDAO dao= new ContentDAO(this.conn);
			rs = dao.search(sql);
			while(rs.next())
			{
				name=rs.getString("a0101")==null?"":rs.getString("a0101");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
		    PubFunc.closeDbObj(rs);
		}
		return name;
	}
	
	
	/**
	 * 写表体
	 * @param fieldList
	 * @param datatable
	 * @param tableName
	 * @param fieldTypeMap
	 */
	public void setTabTable(PdfWriter writer,Document document,String[] field_widths,
			ArrayList fieldList,HashMap fieldHzMap,String tableName,HashMap fieldTypeMap,HashMap fieldCodeMap)
	{
		
		ArrayList tableHeaderList=new ArrayList();;
		boolean   isOverTop=tableHeaderList.size()>1;               //横向是否超过一页
		PdfPTable table =null;
		PdfPCell cell = null;
		
		String fontfamilyname=ResourceFactory.getProperty("hmuster.label.fontSt");
        String fontEffect="0";
        int    fontSize=5;
        String color="#000000";
        String underLine="#fu[0]";
        if(this.paramtervo!=null)
        {
        	 if(this.paramtervo.getBody_fn().length()>0)
        		 fontfamilyname=this.paramtervo.getBody_fn();
        	 fontEffect=getFontEffect(3);
        	 if(this.paramtervo.getBody_fz().length()>0)
        	 	fontSize=Integer.parseInt(this.paramtervo.getBody_fz());
        	 if(this.paramtervo.getBody_fc().length()>0)
        		 color=this.paramtervo.getBody_fc();
        	 if(this.paramtervo.getBody_fu()!=null&&this.paramtervo.getBody_fu().length()>0)
        		 underLine=this.paramtervo.getBody_fu();
        	 
        }
        Font font = FontFamilyType.getFont(fontfamilyname,fontEffect,fontSize); // 生成字体样式
 		font.setColor(new Color(Integer.parseInt(color.substring(1,3),16),Integer.parseInt(color.substring(3,5),16),Integer.parseInt(color.substring(5,7),16)));
 		if("#fu[1]".equals(underLine))
 			font.setStyle(Font.UNDERLINE);

		RowSet resultSet=null;
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			if("1".equals(this.operateObject))
				resultSet=dao.search("select * from "+tableName+" "+whl_sql);			
			else
				resultSet=dao.search(whl_sql);	
					
			
			int i=0;  												//行数
			int flag=1;
		
			ArrayList dataList=new ArrayList();
			while(resultSet.next())
			{
				i++;
				this.total++;
				HashMap dataMap=new HashMap();
				for(Iterator t=fieldList.iterator();t.hasNext();)
				{
					String fieldName=(String)t.next();
					if("recidx".equals(fieldName))
						dataMap.put(fieldName,String.valueOf(i));
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
								else if(resultSet.getDate(fieldName)!=null)
									isNotNull=true;
							}
							else if("M".equalsIgnoreCase((String)fieldTypeMap.get(fieldName)))
							{
								isNotNull=true;
							}
						}
						if("employedcount".equals(fieldName))
							isNotNull=true;
						
						if(isNotNull)
						{
							if("A".equalsIgnoreCase((String)fieldTypeMap.get(fieldName))&&!"0".equalsIgnoreCase((String)fieldCodeMap.get(fieldName)))
							{
								dataMap.put(fieldName,AdminCode.getCode((String)fieldCodeMap.get(fieldName),resultSet.getString(fieldName))!=null?AdminCode.getCode((String)fieldCodeMap.get(fieldName),resultSet.getString(fieldName)).getCodename():"");
							}
							else
							{
								if("N".equalsIgnoreCase((String)fieldTypeMap.get(fieldName)))
								{
									if("engagePlan".equalsIgnoreCase(this.tableName)&& "z0117".equalsIgnoreCase(fieldName))  //实招人数 指标
									{
										dataMap.put(fieldName,(String)this.planCountMap.get(resultSet.getString("z0101")));
									}
									else if("posList".equalsIgnoreCase(tableName)&& "employedcount".equalsIgnoreCase(fieldName))  //实招人数 指标
									{
										dataMap.put(fieldName,(String)this.planCountMap.get(resultSet.getString("z0301")));
									}
									else
									{
										if(resultSet.getFloat(fieldName)==0)
										{
											dataMap.put(fieldName," ");
										}
										else
										{			
												dataMap.put(fieldName,resultSet.getString(fieldName));
										}
									}
								}
								else if("M".equalsIgnoreCase((String)fieldTypeMap.get(fieldName)))
								{
									
									//if(!this.tableName.equalsIgnoreCase("z05"))
									{
										dataMap.put(fieldName,Sql_switcher.readMemo(resultSet,fieldName));
									}
									
									
								}
								else if("D".equalsIgnoreCase((String)fieldTypeMap.get(fieldName)))
								{
									
									if(resultSet.getDate(fieldName)!=null)
									{
										if("Z0509".equalsIgnoreCase(fieldName))
										{
											Date date=resultSet.getDate(fieldName);	
											SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
											dataMap.put(fieldName,bartDateFormat.format(date));
										}
										else
										{
											if("z03".equalsIgnoreCase(this.tableName)&& "z0307".equalsIgnoreCase(fieldName))
											{
												SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd");
												dataMap.put(fieldName,bartDateFormat.format(resultSet.getDate(fieldName)));
											}
											else
											{
											
													Date date=resultSet.getDate(fieldName);	
													SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd");
													dataMap.put(fieldName,bartDateFormat.format(date));
												
											}
										}
									}
									else
										dataMap.put(fieldName,"");
									
								}
								else
								{
										
									if(!"z05".equalsIgnoreCase(this.tableName))
									{
										dataMap.put(fieldName,resultSet.getString(fieldName));
									}
									else
									{
										
										 if("z0505".equalsIgnoreCase(fieldName)|| "z0507".equalsIgnoreCase(fieldName))
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
													z0505.append(","+(String)this.employerNameMap.get(aa_z0505[a]));
													a++;
												}
											}
											if(a_z0507!=null&&a_z0507.trim().length()>0)
											{
												String[] aa_z0507=a_z0507.split(",");
												int a=0;
												while(a<aa_z0507.length)
												{
													z0507.append(","+(String)this.employerNameMap.get(aa_z0507[a]));
													a++;
												}
											}		
											if(z0505.length()>1)
												dataMap.put("z0505",z0505.substring(1));
											else
												dataMap.put("z0505",z0505.toString());
											if(z0507.length()>1)
												dataMap.put("z0507",z0507.substring(1));
											else
												dataMap.put("z0507",z0507.toString());
										}
										else
											dataMap.put(fieldName,resultSet.getString(fieldName));
									}
								
								}
							}
						
						}
						else
						{
							if(fieldCodeMap.get(fieldName)!=null&& "36".equals((String)fieldCodeMap.get(fieldName)))
								dataMap.put(fieldName,"未选");
							else
								dataMap.put(fieldName," ");
						}
					}
				}
				dataList.add(dataMap);
				if(i%this.page_rows==0)
				{
					pageno++;
					writePage(document,writer,dataList,tableHeaderList,fieldTypeMap,fieldHzMap,flag,font);
					dataList.clear();
					flag++;
				}				
			}
			if(i<this.page_rows||i>this.page_rows)
				pageno++;
				writePage(document,writer,dataList,tableHeaderList,fieldTypeMap,fieldHzMap,flag,font);
			
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
		
		

	}
	
	
	public void writePage(Document document,PdfWriter writer,ArrayList pageDataList,ArrayList tableHeaderList,HashMap fieldTypeMap,HashMap fieldHzMap,int flag,Font font)
	{
		try
		{
			
			PdfPCell cell=null;
			for(int i=0;i<tableHeaderList.size();i++)
			{
				ArrayList a_headerList=(ArrayList)tableHeaderList.get(i);				
				
				if(flag>1)
					document.newPage();
				
				float[] columnsWidth=new float[a_headerList.size()];
				float tableWidth=0f;
				for(int j=0;j<a_headerList.size();j++)
				{
					HashMap tempMap=(HashMap)a_headerList.get(j);
					columnsWidth[j]=((Float)tempMap.get("width")).floatValue();
					tableWidth+=((Float)tempMap.get("width")).floatValue();
				}
				setTableTitle(writer,tableWidth);
				PdfPTable table = new PdfPTable(columnsWidth);
				table.setTotalWidth(tableWidth);
				table.setLockedWidth(true);

				setTableHeaders(table,a_headerList,fieldHzMap,font);
				for(Iterator t=pageDataList.iterator();t.hasNext();)
				{
					HashMap dataMap=(HashMap)t.next();
					for(Iterator t1=a_headerList.iterator();t1.hasNext();)
					{
						HashMap headerMap=(HashMap)t1.next();
						String fieldName=(String)headerMap.get("fieldName");
						float  width=((Float)headerMap.get("width")).floatValue();
						String context=(String)dataMap.get(fieldName);
						Paragraph paragraph = getParagraph(context,font);
						cell = new PdfPCell(paragraph);
						cell.setMinimumHeight(this.rowHeigth); // 设置单元格的最小高度
						if("recidx".equals(fieldName)|| "N".equals((String)fieldTypeMap.get(fieldName)))
						{
							cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						}
						else
						{
							cell.setHorizontalAlignment(Element.ALIGN_LEFT);
							cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						}
						table.addCell(cell);
					}	
				}
				//document.add(table);
				table.writeSelectedRows(0, -1,this.l_margin,this.page_height-this.t_margin, writer.getDirectContent());
				flag++;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	
	
	/**
	 * 设置表头
	 * @param fieldList     列信息
	 * @param fieldHzMap
	 */
	public void setTableHeaders(PdfPTable table,ArrayList fieldList,HashMap fieldHzMap,Font font)
	{
		try
		{
			 PdfPCell cell=null;
	       	 for(Iterator t=fieldList.iterator();t.hasNext();)
	         {
	        	 HashMap headerMap=(HashMap)t.next();
				 String fieldName=(String)headerMap.get("fieldName");
	        	 Paragraph  paragrah=getParagraph((String)fieldHzMap.get(fieldName),font);
	        	 cell = new PdfPCell(paragrah);
	        	 cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				 cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
	        	 cell.setMinimumHeight(this.rowHeigth+this.banlance); // 设置单元格的最小高度
	        	 table.addCell(cell);
	         }        
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 得到表头所跨页数及每页的表头信息
	 * @param field_widths 列宽
	 * @param fieldList    列集合
	 * @param fieldHzMap   列名对应map
	 * @return
	 */
	public ArrayList getTableHeaderList(String[] field_widths,ArrayList fieldList,HashMap fieldHzMap,HashMap fieldTypeMap)
	{
		ArrayList list=new ArrayList();
		float[] field_width=new float[field_widths.length];
		for(int a=0;a<field_widths.length;a++)
			field_width[a]=Float.parseFloat(field_widths[a]);
		boolean flag=false;  
		int i=0;
	    while(i<fieldList.size())
		{
			ArrayList tempList=new ArrayList();
			float width=0f;
			
			//如果是第2页，需添加序列
			if(flag==true)
			{
				HashMap map=new HashMap();
				map.put("fieldName",(String)fieldList.get(0));
				map.put("fieldHzName",(String)fieldHzMap.get((String)fieldList.get(0)));
				map.put("width",new Float(field_width[0]));
				tempList.add(map);
			}
			while(i<fieldList.size())
			{
				float awidth=0f;
				if(i<field_width.length)
					awidth=field_width[i];
				else 
					awidth=80;
				
				width+=awidth;	
				if(width<=this.body_width)
				{
					HashMap map=new HashMap();
					map.put("fieldName",(String)fieldList.get(i));
					map.put("fieldHzName",(String)fieldHzMap.get((String)fieldList.get(i)));
					map.put("width",new Float(awidth));
					tempList.add(map);
				}
				else
				{
					flag=true;
					break;
				}
				i++;
			}
			list.add(tempList);
		}
		return list;
	}

	
	//取得字形
	// 1：页头  2页尾 3：正文字体 4:标题 
	public String getFontEffect(int flag)
	{
		String fontEffect="0";
		if(this.paramtervo!=null)
		{   if(flag==3)
			{
				if("#fi[1]".equals(this.paramtervo.getBody_fi())&& "#fb[1]".equals(this.paramtervo.getBody_fb()))
					fontEffect="4";  //粗斜体
				else if("#fb[1]".equals(this.paramtervo.getBody_fb()))
					fontEffect="2";  //粗体
				else if("#fi[1]".equals(this.paramtervo.getBody_fi()))
					fontEffect="3";  //斜体
			}
			else if(flag==4)
			{
				if("#fi[1]".equals(this.paramtervo.getTitle_fi())&& "#fb[1]".equals(this.paramtervo.getTitle_fb()))
					fontEffect="4";  //粗斜体
				else if("#fb[1]".equals(this.paramtervo.getTitle_fi()))
					fontEffect="2";  //粗体
				else if("#fi[1]".equals(this.paramtervo.getTitle_fb()))
					fontEffect="3";  //斜体
			}
			else if(flag==1)
			{
				if("#fi[1]".equals(this.paramtervo.getHead_fi())&& "#fb[1]".equals(this.paramtervo.getHead_fb()))
					fontEffect="4";  //粗斜体
				else if("#fb[1]".equals(this.paramtervo.getHead_fb()))
					fontEffect="2";  //粗体
				else if("#fi[1]".equals(this.paramtervo.getHead_fi()))
					fontEffect="3";  //斜体
			}
			else if(flag==2)
			{
				if("#fi[1]".equals(this.paramtervo.getTile_fi())&& "#fb[1]".equals(this.paramtervo.getTile_fb()))
					fontEffect="4";  //粗斜体
				else if("#fb[1]".equals(this.paramtervo.getTile_fb()))
					fontEffect="2";  //粗体
				else if("#fi[1]".equals(this.paramtervo.getTile_fi()))
					fontEffect="3";  //斜体
			}
			
		
		
		
		
		}
		return fontEffect;
	}
	
	
	
	public Paragraph getParagraph(String context,Font font)
	{
		Paragraph paragraph = null;
		paragraph = new Paragraph(context, font);
		return paragraph;
	}

	/**得到列宽比例*/
	public float[] getColumnsPercentages(String[] field_widths)
	{
		float[] width=new float[field_widths.length];
		float sum=0f;
		for(int i=0;i<field_widths.length;i++)
			sum+=Float.parseFloat(field_widths[i]);
		for(int i=0;i<field_widths.length;i++)
		{
			if(sum!=0) {
				width[i] = Float.parseFloat(field_widths[i]) / sum;
			} else {
				width[i] = 0;
			}
		}
		return width;
	}
	
	/**
	 * 取得表的列信息
	 * @param tabname
	 * @param a_fieldList
	 * @return
	 */
	public ArrayList getTableField(String tabname,ArrayList a_fieldList)
	{
		ArrayList list=new ArrayList();
		ArrayList fieldList=new ArrayList();
		HashMap   fieldHzMap=new HashMap();
		HashMap   fieldTypeMap=new HashMap();
		HashMap   fieldCodeMap=new HashMap();
		try
		{
			fieldList.add("recidx");
			fieldHzMap.put("recidx",ResourceFactory.getProperty("recidx.label"));
			fieldTypeMap.put("recidex","A");
			fieldCodeMap.put("recidx","0");
			
			String field_name="";
			for(int i=0;i<a_fieldList.size();i++)
			{
				FieldItem item=(FieldItem)a_fieldList.get(i);
				if("z0301".equalsIgnoreCase(item.getItemid())||(!"posList".equals(this.tableName)&& "z0101".equalsIgnoreCase(item.getItemid())))
					continue;
				//去掉计划表审批方式一列
				if("r2512".equalsIgnoreCase(item.getItemid())|| "R2512".equalsIgnoreCase(item.getItemid()))
					continue;
			
				field_name=item.getItemid();
				String colHz=item.getItemdesc();
				fieldHzMap.put(field_name,colHz);
				fieldTypeMap.put(field_name,item.getItemtype());
				fieldCodeMap.put(field_name,item.getCodesetid());
				fieldList.add(field_name);
			}
			list.add(fieldList);
			list.add(fieldHzMap);
			list.add(fieldTypeMap);
			list.add(fieldCodeMap);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}

	
	//初始化
	public void initInfo()
	{
		t_margin=Float.parseFloat(PubFunc.round(String.valueOf(30/proportion),0));
		b_margin=Float.parseFloat(PubFunc.round(String.valueOf(20/proportion),0));
		l_margin=Float.parseFloat(PubFunc.round(String.valueOf(20/proportion),0));
		r_margin=Float.parseFloat(PubFunc.round(String.valueOf(20/proportion),0));
		page_width=Float.parseFloat(PubFunc.round(String.valueOf(210/proportion),0));
		page_height=Float.parseFloat(PubFunc.round(String.valueOf(297/proportion),0));
		datetime=Calendar.getInstance().get(Calendar.YEAR)+"."+(Calendar.getInstance().get(Calendar.MONTH)+1)+"."
					+Calendar.getInstance().get(Calendar.DATE);
		times=Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+":"+Calendar.getInstance().get(Calendar.MINUTE)+":"
			+Calendar.getInstance().get(Calendar.SECOND);
		if(this.paramtervo!=null)
		{
			
			if(this.paramtervo.getTop().length()>0)
				t_margin=Float.parseFloat(PubFunc.round(String.valueOf(Float.parseFloat(this.paramtervo.getTop())/proportion),0));
			if(this.paramtervo.getBottom().length()>0)
				b_margin=Float.parseFloat(PubFunc.round(String.valueOf(Float.parseFloat(this.paramtervo.getBottom())/proportion),0));
			if(this.paramtervo.getLeft().length()>0)
				l_margin=Float.parseFloat(PubFunc.round(String.valueOf(Float.parseFloat(this.paramtervo.getLeft())/proportion),0));
			if(this.paramtervo.getRight().length()>0)
				r_margin=Float.parseFloat(PubFunc.round(String.valueOf(Float.parseFloat(this.paramtervo.getRight())/proportion),0));
//			纵向
			
			if (this.paramtervo.getOrientation()!=null&&this.paramtervo.getOrientation().length()>0&& "0".equals(this.paramtervo.getOrientation()))
			{
				
				if(this.paramtervo.getWidth()!=null&&this.paramtervo.getWidth().length()>0)
					page_width=Float.parseFloat(PubFunc.round(String.valueOf(Float.parseFloat(this.paramtervo.getWidth())/proportion),0));
				if(this.paramtervo.getHeight()!=null&&this.paramtervo.getHeight().length()>0)
					page_height=Float.parseFloat(PubFunc.round(String.valueOf(Float.parseFloat(this.paramtervo.getHeight())/proportion),0));			
			}
			else
			{
				if(this.paramtervo.getWidth()!=null&&this.paramtervo.getWidth().length()>0)
					page_height=Float.parseFloat(PubFunc.round(String.valueOf(Float.parseFloat(this.paramtervo.getWidth())/proportion),0));
				if(this.paramtervo.getHeight()!=null&&this.paramtervo.getHeight().length()>0)	
					page_width=Float.parseFloat(PubFunc.round(String.valueOf(Float.parseFloat(this.paramtervo.getHeight())/proportion),0));		
			}
			
		}
		this.body_height=this.page_height-this.t_margin-this.b_margin;
		this.body_width=this.page_width-this.l_margin-this.r_margin;
		String t_rows=String.valueOf((this.body_height-(this.rowHeigth+this.banlance))/this.rowHeigth);
		this.page_rows=Integer.parseInt(t_rows.substring(0,t_rows.indexOf(".")));
	}

	//get Document
	public Document getDocument()
	{
		Document document = null;
		document=new Document(new Rectangle( page_width,page_height),
					 l_margin, r_margin,t_margin, b_margin);		
		return document;
	}
	
	
	
	
	//设置表题，页头、页尾
	public void setTableTitle(PdfWriter writer,float tablewidth)
	{
		String titleName="";
		if(this.paramtervo==null||this.paramtervo.getTitle_fw()==null)
		{
				if("Z03".equalsIgnoreCase(this.tableName))
					titleName=ResourceFactory.getProperty("lable.hiremanage.positionRequest");
				if("zp_pos_tache".equalsIgnoreCase(this.tableName))
					titleName=ResourceFactory.getProperty("hire.fileout.personnelFilterList");
				if("z05".equalsIgnoreCase(this.tableName))
					titleName=ResourceFactory.getProperty("hire.fileout.interviewArrangeInfo");
				if("zp_test_template".equalsIgnoreCase(this.tableName))
					titleName=ResourceFactory.getProperty("hire.fileout.interviewExamInfo");
				if("personnelEmploy".equalsIgnoreCase(this.tableName))
					titleName=ResourceFactory.getProperty("hire.fileout.personnelEmployList");
				if("engagePlan".equalsIgnoreCase(this.tableName))
					titleName=ResourceFactory.getProperty("hire.fileout.engagePlanList");
				if("r31".equalsIgnoreCase(this.tableName))
					titleName=ResourceFactory.getProperty("sys.res.trainjob");
		}
		else
		{
			titleName=this.paramtervo.getTitle_fw();
			if(titleName==null||titleName.trim().length()==0)
			{
				if("Z03".equalsIgnoreCase(this.tableName))
					titleName=ResourceFactory.getProperty("lable.hiremanage.positionRequest");
				if("zp_pos_tache".equalsIgnoreCase(this.tableName))
					titleName=ResourceFactory.getProperty("hire.fileout.personnelFilterList");
				if("z05".equalsIgnoreCase(this.tableName))
					titleName=ResourceFactory.getProperty("hire.fileout.interviewArrangeInfo");
				if("zp_test_template".equalsIgnoreCase(this.tableName))
					titleName=ResourceFactory.getProperty("hire.fileout.interviewExamInfo");
				if("personnelEmploy".equalsIgnoreCase(this.tableName))
					titleName=ResourceFactory.getProperty("hire.fileout.personnelEmployList");
				if("engagePlan".equalsIgnoreCase(this.tableName))
					titleName=ResourceFactory.getProperty("hire.fileout.engagePlanList");
				if("r31".equalsIgnoreCase(this.tableName))
					titleName=ResourceFactory.getProperty("sys.res.trainjob");
			}
			
		}
		//写标题
		String t_fontfamilyname=ResourceFactory.getProperty("hmuster.label.fontSt");
        String t_fontEffect="0";
        int    t_fontSize=17;  
        String t_color="#000000";       
        String  t_underLine="#fu[0]";
        String  t_strikethru="#fs[0]";
        
        if(this.paramtervo!=null)
        {
        	if(this.paramtervo.getTitle_fn().length()>0)
        		t_fontfamilyname=this.paramtervo.getTitle_fn();
	       	 t_fontEffect=getFontEffect(4);
	       	 if(this.paramtervo.getTitle_fz().length()>0)
	       		 t_fontSize=Integer.parseInt(this.paramtervo.getTitle_fz());
	       	 if(this.paramtervo.getTitle_fc().length()>0)
	       		 t_color=this.paramtervo.getTitle_fc();	       	 
	       	 if(this.paramtervo.getTitle_fu()!=null&&this.paramtervo.getTitle_fu().length()>0)
	       		 t_underLine=this.paramtervo.getTitle_fu();
	       	if(this.paramtervo.getTile_fs()!=null&&this.paramtervo.getTile_fs().length()>0)
	       		t_strikethru=this.paramtervo.getTile_fs();

        }
        float left=tablewidth/2+this.l_margin-titleName.getBytes().length/2*(t_fontSize+2)/2;
        if(left<0)
        	left=this.page_width/2-titleName.getBytes().length/2*(t_fontSize+2)/2;
		excecute(titleName,left,this.page_height-20,titleName.length()*t_fontSize,
				20f,t_fontEffect,t_fontSize,t_fontfamilyname,t_color,t_underLine,t_strikethru,
				writer);
		
		//写页头
		String h_fontfamilyname=ResourceFactory.getProperty("hmuster.label.fontSt");
        String h_fontEffect="0";
        int    h_fontSize=6;    
        String h_color="#000000";
        String  h_underLine="#fu[0]";
        String  h_strikethru="#fs[0]";
        int totalWidth=0;
		if(this.paramtervo!=null)
        {
			if(this.paramtervo.getHead_fn().length()>0)
				h_fontfamilyname=this.paramtervo.getHead_fn();
	       	 h_fontEffect=getFontEffect(1);
	       	if(this.paramtervo.getHead_fz().length()>0) 
	       		h_fontSize=Integer.parseInt(this.paramtervo.getHead_fz());
	       	if(this.paramtervo.getHead_fc().length()>0)
	       		h_color=this.paramtervo.getHead_fc();	       	
	       	if(this.paramtervo.getHead_fu()!=null&&this.paramtervo.getHead_fu().length()>0)
	       		 h_underLine=this.paramtervo.getHead_fu();
	       	if(this.paramtervo.getHead_fs()!=null&&this.paramtervo.getHead_fs().length()>0)
	       		h_strikethru=this.paramtervo.getHead_fs();
	       	
	       	
	       	if(this.paramtervo.getHead_flw()!=null&&this.paramtervo.getHead_flw().length()>1)
	       	{
	       		totalWidth+=this.paramtervo.getHead_flw().getBytes().length/2*(h_fontSize+4);
	       	}
	       	if(this.paramtervo.getHead_fmw()!=null&&this.paramtervo.getHead_fmw().length()>1)
			{
	       		totalWidth+=this.paramtervo.getHead_fmw().getBytes().length/2*(h_fontSize+4);
			}
	        if(this.paramtervo.getHead_frw()!=null&&this.paramtervo.getHead_frw().length()>1)
			{
	        	totalWidth+=this.paramtervo.getHead_frw().getBytes().length/2*(h_fontSize+4);
			}
        }	
		
		if(totalWidth<tablewidth)
		{
		
			if(this.paramtervo!=null)
			{
				if(this.paramtervo.getHead_flw()!=null&&this.paramtervo.getHead_flw().length()>1)
				{
					excecute(codeChange(this.paramtervo.getHead_flw()),this.l_margin,this.page_height-(this.t_margin-25),30,
							20f,h_fontEffect,h_fontSize,h_fontfamilyname,h_color,h_underLine,h_strikethru,
							writer);
				}
				if(this.paramtervo.getHead_fmw()!=null&&this.paramtervo.getHead_fmw().length()>1)
				{
					excecute(codeChange(this.paramtervo.getHead_fmw()),tablewidth/2-this.paramtervo.getHead_fmw().getBytes().length/2*(t_fontSize+2)/2+this.l_margin,this.page_height-(this.t_margin-25),30f,
							20f,h_fontEffect,h_fontSize,h_fontfamilyname,h_color,h_underLine,h_strikethru,
							writer);
				}
				
			   if(this.paramtervo.getHead_frw()!=null&&this.paramtervo.getHead_frw().length()>1)
				{
					excecute(codeChange(this.paramtervo.getHead_frw()),tablewidth+this.l_margin-this.paramtervo.getHead_frw().getBytes().length/2*(t_fontSize-2),this.page_height-(this.t_margin-25),30f,
							20f,h_fontEffect,h_fontSize,h_fontfamilyname,h_color,h_underLine,h_strikethru,
							writer);
				}
			}
		}

		//写页尾
		String f_fontfamilyname="宋体";
        String f_fontEffect="0";
        int    f_fontSize=6;     
        String f_color="#000000";
        String  f_underLine="#fu[0]";
        String  f_strikethru="#fs[0]";
        totalWidth=0;
		if(this.paramtervo!=null)
        {
			if(this.paramtervo.getTile_fn().length()>0)
				f_fontfamilyname=this.paramtervo.getTile_fn();
	       	 f_fontEffect=getFontEffect(2);
	       	 if(this.paramtervo.getTile_fz().length()>0)
	       		 f_fontSize=Integer.parseInt(this.paramtervo.getTile_fz());
	       	 if(this.paramtervo.getTile_fc().length()>0)
	       		 f_color=this.paramtervo.getTile_fc();	       
	    	if(this.paramtervo.getTile_fu()!=null&&this.paramtervo.getTile_fu().length()>0)
	       		 f_underLine=this.paramtervo.getTile_fu();
	       	if(this.paramtervo.getTile_fs()!=null&&this.paramtervo.getTile_fs().length()>0)
	       		 f_strikethru=this.paramtervo.getTile_fs();
	       	
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
		if(totalWidth<tablewidth)
		{
			if(this.paramtervo!=null)
	        {
				if(this.paramtervo.getTile_flw()!=null&&this.paramtervo.getTile_flw().length()>1)
				{
					excecute(codeChange(this.paramtervo.getTile_flw()),this.l_margin,this.page_height-(this.t_margin+this.body_height+5),30,
							20f,f_fontEffect,f_fontSize,f_fontfamilyname,f_color,f_underLine,f_strikethru,
							writer);
				}
				if(this.paramtervo.getTile_fmw()!=null&&this.paramtervo.getTile_fmw().length()>1)
				{
					excecute(codeChange(this.paramtervo.getTile_fmw()),tablewidth/2-this.paramtervo.getTile_fmw().getBytes().length/2*(t_fontSize+2)/2+this.l_margin,this.page_height-(this.t_margin+this.body_height+5),30f,
							20f,f_fontEffect,f_fontSize,f_fontfamilyname,f_color,f_underLine,f_strikethru,
							writer);
				}
			    if(this.paramtervo.getTile_frw()!=null&&this.paramtervo.getTile_frw().length()>1)
				{
					excecute(codeChange(this.paramtervo.getTile_frw()),tablewidth+this.l_margin-this.paramtervo.getTile_frw().getBytes().length/2*(t_fontSize-2),this.page_height-(this.t_margin+this.body_height+5),30f,
							20f,f_fontEffect,f_fontSize,f_fontfamilyname,f_color,f_underLine,f_strikethru,
							writer);
				}
			}
		}
	
	}
	
	
	
	
	
	
	/**
	 * 生成绝对定位的table(每个table表示一个单元格)
	 * 
	 * @param context
	 *            内容
	 * @param leftP
	 *            左边坐标（像素）
	 * @param top
	 *            顶端坐标（像素：从下往上数）
	 * @param width
	 *            表体宽度
	 * @param height
	 *            单元格高度
	 * @param align
	 *            单元格内容的排列方式
	 * @param fontEffect
	 *            字体效果
	 * @param fontSize
	 *            字体大小
	 * @param writer
	 */
	public void excecute(String context, float leftP, float top, float width,
			float height, String fontEffect, int fontSize,String fontfamilyname,String orgcolor,String underLine,String strikethru,
			PdfWriter writer) {
		
		Font font = FontFamilyType.getFont(fontfamilyname,fontEffect,fontSize); // 生成字体样式
		font.setColor(new Color(Integer.parseInt(orgcolor.substring(1,3),16),Integer.parseInt(orgcolor.substring(3,5),16),Integer.parseInt(orgcolor.substring(5,7),16)));
		if("#fu[1]".equals(underLine))
			font.setStyle(Font.UNDERLINE);
		if("#fs[1]".equals(strikethru))
			font.setStyle(Font.STRIKETHRU);
		PdfPTable table = new PdfPTable(1);
		if(width<=0)
			width=1f;
		table.setTotalWidth(width); // 设置表的总体宽度		
		Paragraph paragraph = null;
		try {
			paragraph = new Paragraph(context, font);

			PdfPCell cell = new PdfPCell(paragraph);
			cell.setBorder(0);
			cell.setNoWrap(true);
			cell.setMinimumHeight(height); // 设置单元格的最小高度
			cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);			
			table.addCell(cell);
			table.writeSelectedRows(0, 1, leftP, top, writer
							.getDirectContent()); // 固定坐标
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String codeChange(String code) {
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


	public String getDatetime() {
		return datetime;
	}


	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}


	public String getTimes() {
		return times;
	}


	public void setTimes(String times) {
		this.times = times;
	}


	public String getProducer() {
		return producer;
	}


	public void setProducer(String producer) {
		this.producer = producer;
	}


	public int getTotal() {
		return total;
	}


	public void setTotal(int total) {
		this.total = total;
	}


	public UserView getUserView() {
		return userView;
	}


	public void setUserView(UserView userView) {
		this.userView = userView;
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
		    PubFunc.closeDbObj(rowset);
					}
		
		return tmap;
	}
}
