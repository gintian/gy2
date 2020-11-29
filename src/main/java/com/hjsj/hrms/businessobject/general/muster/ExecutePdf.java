package com.hjsj.hrms.businessobject.general.muster;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.constant.FontFamilyType;
import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.interfaces.report.ReportParseXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPRow;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.text.DecimalFormat;
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
	private float arowHeigth=20;    //行高
	private int page_rows=0;     //每页的行数
	private String tabid="0";
	private String mustername="";
    private  int   pageNum=0;
	private UserView userview=null;
	private String userName="";
	private  int   totalNum=0;
    private String infor_Flag="";
    private String dbpre="";
    //简单花名册优化
    public ExecutePdf(Connection conn ,String tabid ,UserView userview)
    {
        this.conn=conn;
        this.tabid = tabid;
        this.userview = userview;
        setPagePrintStyle(tabid);
        initInfo();
    }
	public ExecutePdf(Connection conn )
	{
		this.conn=conn;
	}
		
	
	public ExecutePdf(Connection conn,String musterName)
	{
		
		this.conn=conn;
		tabid=musterName.substring(1,musterName.indexOf("_"));
		setPagePrintStyle(tabid);
		initInfo();
	}
	
	public ExecutePdf(Connection conn,String tabid,String tab)
	{
		
		this.conn=conn;
		setPagePrintStyle(tabid);
		initInfo();
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
	 * 简单花名册新增导出Pdf方法 zyh 2019 -03 -11
	 * @param userName
	 * @param map
	 * @return
	 */
	public String createPdf(String userName,HashMap<String, Object>  map) {
		Document document = null;
		String    pdfName="";
		FileOutputStream out = null;
		try {
			this.userName=userName;
			document =getDocument();//根据边距生成文档	
			String    tabid=this.tabid;
			setPagePrintStyle(tabid);
			RecordVo lnameVo=getLnameInfo(tabid);//获取文档上下内容 封装成对象。
			pdfName =(String)map.get("userName")+"_"+map.get("musterName")+".pdf";
			out = new FileOutputStream(System.getProperty("java.io.tmpdir")
					+ System.getProperty("file.separator")+ pdfName);
			PdfWriter writer = PdfWriter.getInstance(document,out);
			document.open();			
			
			ArrayList list=getMusterField(tabid,map);//pdf 的列头 设置列的宽度
			ArrayList fieldList=(ArrayList)list.get(0);
			HashMap   fieldHzMap=(HashMap)list.get(1);
			HashMap   fieldTypeMap=(HashMap)list.get(2);
			HashMap   fieldMaxFontNumMap=(HashMap)list.get(3);
			
			String[] field_widths=null;
			field_widths=new String[fieldList.size()];
			String bodyFontSize="5";			
			if(this.paramtervo!=null&&this.paramtervo.getBody_fz()!=null&&this.paramtervo.getBody_fz().trim().length()>0){
				bodyFontSize=this.paramtervo.getBody_fz();			
			}
			for(int i=0;i<fieldList.size();i++){
				String temp=(String)fieldList.get(i);
				String numvalue = (String)fieldMaxFontNumMap.get(temp);
				if(numvalue!=null&&numvalue.trim().length()>0){
					int num=Integer.parseInt((String)fieldMaxFontNumMap.get(temp));
					field_widths[i]=String.valueOf(num*(Integer.parseInt(bodyFontSize)+10));
				}
			}
			setTable(writer,document,field_widths,fieldList,fieldHzMap,map,fieldTypeMap,lnameVo);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
			    if (document!=null) {
			        document.close();
			        document = null;
                }
			}catch(Exception e1){
				e1.printStackTrace();
			}
			PubFunc.closeIoResource(out);
		}
		return pdfName;
	}
	
	
	/**
	 * 
	 * @param output_stream
	 * @param fieldWidths 列宽 （像素）
	 * @param musterName  表名
	 */
	public String createPdf(String userName,String fieldWidths,String musterName)
	{
		this.mustername=musterName;
		this.userName=userName;
		Document document =getDocument();	
		String    tabid=musterName.substring(1,musterName.indexOf("_"));
		RecordVo lnameVo=getLnameInfo(tabid);
		setPagePrintStyle(tabid);
		String    pdfName="";
		FileOutputStream out = null;
		try {
			pdfName =PubFunc.getStrg() + ".pdf";
			out = new FileOutputStream(System.getProperty("java.io.tmpdir")
					+ System.getProperty("file.separator")+ pdfName);
			PdfWriter writer = PdfWriter.getInstance(document,out);
			document.open();			
			
			ArrayList list=getMusterField(tabid);
			ArrayList fieldList=(ArrayList)list.get(0);
			HashMap   fieldHzMap=(HashMap)list.get(1);
			HashMap   fieldTypeMap=(HashMap)list.get(2);
			HashMap   fieldMaxFontNumMap=(HashMap)list.get(3);
			
			String[] field_widths=null;
			if(fieldWidths.indexOf("/")!=-1) {
                field_widths=fieldWidths.split("/");
            } else{
				field_widths=new String[fieldList.size()];
				for(int i=0;i<fieldList.size();i++){
					String fieldName = (String)fieldHzMap.get((String)fieldList.get(i));
					field_widths[i]=fieldName.length()*10*3/2+"";
				}
			}
			//缩小比列
			/*for(int i=0;i<field_widths.length;i++)
			{
				String rank="1";
				if(this.paramtervo.getBody_fz()!=null&&this.paramtervo.getBody_fz().trim().length()>0)
				{
					String bodyFontSize=this.paramtervo.getBody_fz();
					BigDecimal abodyFontSize=new BigDecimal(bodyFontSize);
					rank=abodyFontSize.divide(new BigDecimal("5"),1,BigDecimal.ROUND_HALF_UP).toString();
				}
				field_widths[i]=String.valueOf((Float.parseFloat(field_widths[i]))*0.7*Float.parseFloat(rank));
			}*/
			String bodyFontSize="5";			
			if(this.paramtervo!=null&&this.paramtervo.getBody_fz()!=null&&this.paramtervo.getBody_fz().trim().length()>0)
			{
				bodyFontSize=this.paramtervo.getBody_fz();			
			}
			for(int i=0;i<fieldList.size();i++)
			{
				String temp=(String)fieldList.get(i);
				String numvalue = (String)fieldMaxFontNumMap.get(temp);
				if(numvalue!=null&&numvalue.trim().length()>0){
					int num=Integer.parseInt((String)fieldMaxFontNumMap.get(temp));
					field_widths[i]=String.valueOf(num*(Integer.parseInt(bodyFontSize)+10));
					
				}
			}
			
			
			//PdfPTable datatable = new PdfPTable(field_widths.length);
			setTable(writer,document,field_widths,fieldList,fieldHzMap,musterName,fieldTypeMap,lnameVo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try{
				document.close();
			}catch(Exception e1){
				e1.printStackTrace();
			}
			PubFunc.closeIoResource(out);
		}
		return pdfName;
	}
	
	
	/**
	 * 
	 * @param output_stream
	 * @param fieldWidths 列宽 （像素）
	 * @param musterName  表名
	 */
	public void createPdf(OutputStream output_stream,String fieldWidths,String musterName)
	{
		Document document =getDocument();	
		String    tabid=musterName.substring(1,musterName.indexOf("_"));
		setPagePrintStyle(tabid);
		RecordVo lnameVo=getLnameInfo(tabid);
		String    pdfName="";
		try {
			
			PdfWriter writer = PdfWriter.getInstance(document,output_stream);
			document.open();
			String[] field_widths=null;
			if(fieldWidths.indexOf("/")!=-1) {
                field_widths=fieldWidths.split("/");
            } else{
				field_widths=new String[1];
				field_widths[0]=fieldWidths;
			}
			PdfPTable datatable = new PdfPTable(field_widths.length);
			ArrayList list=getMusterField(tabid);
			ArrayList fieldList=(ArrayList)list.get(0);
			
			HashMap   fieldHzMap=(HashMap)list.get(1);
			HashMap   fieldTypeMap=(HashMap)list.get(2);
			setTable(writer,document,field_widths,fieldList,fieldHzMap,musterName,fieldTypeMap,lnameVo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		document.close();	
	}
	
	
   /**
    * 简单花名册新增方法 zyh 写表体
    * @param writer
    * @param document
    * @param field_widths
    * @param fieldList
    * @param fieldHzMap
    * @param map
    * @param fieldTypeMap
    * @param lnameVo
    */
	public void setTable(PdfWriter writer,Document document,String[] field_widths,ArrayList fieldList,HashMap fieldHzMap,HashMap<String, Object> map,HashMap fieldTypeMap,RecordVo lnameVo){
		Connection a_conn =null;	
		ResultSet resultSet=null;
		try{
			ArrayList tableHeaderList=getTableHeaderList(field_widths,fieldList,fieldHzMap);
			String fontfamilyname=ResourceFactory.getProperty("gz.gz_acounting.m.font");
	        String fontEffect="0";
	        int    fontSize=5;
	        String color="#000000";
	        String underLine="#fu[0]";
	        if(this.paramtervo!=null){
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
	        Font font = FontFamilyType.getFont(fontfamilyname,fontEffect,fontSize); // 生成字体样式
	 		font.setColor(new Color(Integer.parseInt(color.substring(1,3),16),Integer.parseInt(color.substring(3,5),16),Integer.parseInt(color.substring(5,7),16)));
	 		if("#fu[1]".equals(underLine)) {
	 			font.setStyle(Font.UNDERLINE);
	 		}
			a_conn = AdminDb.getConnection();	
			ContentDAO dao = new ContentDAO(a_conn);
			int totalCount = (Integer) map.get("totalCount");
			this.totalNum=totalCount;
			String showMusterSql =  (String) map.get("showMusterSql");
			String orderBySql =  (String) map.get("orderBySql");
			String musterType = (String) map.get("musterType");
            StringBuffer sql= new StringBuffer("");
            sql.append(showMusterSql);
            sql.append(" ");
            sql.append(orderBySql);
            resultSet=dao.search(sql.toString());
			int i=0;  												//行数
			int flag=1;
			ArrayList dataList=new ArrayList();
			//按实际数据最大行高重新设置PDF的行高（20191227）
			this.arowHeigth = getMaxHeight(dataList,tableHeaderList,fieldTypeMap,fieldHzMap,font);
			String t_rows=String.valueOf((this.body_height-(this.arowHeigth+this.banlance))/(this.arowHeigth+3));
			this.page_rows=Integer.parseInt(t_rows.substring(0,t_rows.indexOf(".")));
			pageNum=0;
			while(resultSet.next()){
                HashMap dataMap=new HashMap();
                i++;
                dataMap = insertData(dataMap,i, fieldTypeMap, resultSet, fieldList);
                dataList.add(dataMap);
                if(i%this.page_rows==0){
                    pageNum++;
                    writePage(document,writer,dataList,tableHeaderList,fieldTypeMap,fieldHzMap,flag,lnameVo,font);
                    dataList.clear();
                    flag++;
                }   
            }
            if((i<this.page_rows||i>this.page_rows)&&(i%this.page_rows)!=0){//20191227 解决数据条数刚好占据整页，出现多打印一页问题
                pageNum++;
                writePage(document,writer,dataList,tableHeaderList,fieldTypeMap,fieldHzMap,flag,lnameVo,font);
            }
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				PubFunc.closeResource(resultSet);
				PubFunc.closeResource(a_conn);
				System.gc();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	
	
	/**
	 * 写表体
	 * @param fieldList
	 * @param datatable
	 * @param tableName
	 * @param fieldTypeMap
	 */
	public void setTable(PdfWriter writer,Document document,String[] field_widths,ArrayList fieldList,HashMap fieldHzMap,String tableName,HashMap fieldTypeMap,RecordVo lnameVo)
	{
		
		ArrayList tableHeaderList=getTableHeaderList(field_widths,fieldList,fieldHzMap);
//		boolean   isOverTop=tableHeaderList.size()>1;               //横向是否超过一页
//		PdfPTable table =null;
//		PdfPCell cell = null;
        MusterBo musterbo=new MusterBo(this.conn,this.userview);
        ArrayList dblist=musterbo.getUserAllDBList();
		
		String fontfamilyname=ResourceFactory.getProperty("gz.gz_acounting.m.font");
        String fontEffect="0";
        int    fontSize=5;
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
        Font font = FontFamilyType.getFont(fontfamilyname,fontEffect,fontSize); // 生成字体样式
 		font.setColor(new Color(Integer.parseInt(color.substring(1,3),16),Integer.parseInt(color.substring(3,5),16),Integer.parseInt(color.substring(5,7),16)));
 		if("#fu[1]".equals(underLine)) {
            font.setStyle(Font.UNDERLINE);
        }
		
 		
 		Connection a_conn =null;	
		ResultSet resultSet=null;
		
	//	ContentDAO dao = new ContentDAO(this.conn);
	//	RowSet rowSet = null;
		try
		{
			a_conn = AdminDb.getConnection();	
			ContentDAO dao = new ContentDAO(a_conn);
			
            if("1".equals(infor_Flag)&&"ALL".equals(dbpre)&&MusterBo.isHkyh()) {
                totalNum=musterbo.calcAllDBPersonCount(infor_Flag, dblist, tabid);
            } else{
            	resultSet=dao.search("select count(recidx) from "+tableName);
    			if(resultSet.next()) {
                    this.totalNum=resultSet.getInt(1);
                }
            }
			
            String sql="";
            if("1".equals(infor_Flag)&&"ALL".equals(dbpre)&&MusterBo.isHkyh()) {
                sql=musterbo.getDataSetAllDBSelectSql(infor_Flag, dblist, tabid);
            } else {
                sql="select * from "+tableName+" order by recidx ";
            }
            resultSet=dao.search(sql);
			//rowSet=dao.search("select * from "+tableName+" order by recidx");			
			int i=0;  												//行数
			int flag=1;
		
			ArrayList dataList=new ArrayList();
			pageNum=0;
			while(resultSet.next())
			{
				i++;
				
				HashMap dataMap=new HashMap();
				for(Iterator t=fieldList.iterator();t.hasNext();)
				{
					String fieldName=(String)t.next();
					if("recidx".equals(fieldName)) {
                        dataMap.put(fieldName,String.valueOf(i));
                    } else
					{
						if(resultSet.getString(fieldName)!=null)
						{
							if("D".equalsIgnoreCase((String)fieldTypeMap.get(fieldName)))
							{
								String dateValue=resultSet.getString(fieldName);
								FieldItem fielditem =DataDictionary.getFieldItem(fieldName);
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
								dataMap.put(fieldName,dateValue);
							}
							else
							{
								dataMap.put(fieldName,resultSet.getString(fieldName));
							}
							
						}
						else {
                            dataMap.put(fieldName," ");
                        }
					}
				}
				dataList.add(dataMap);
				if(i%this.page_rows==0)
				{
					pageNum++;
					writePage(document,writer,dataList,tableHeaderList,fieldTypeMap,fieldHzMap,flag,lnameVo,font);
					dataList.clear();
					flag++;
				}				
			}
			if(i<this.page_rows||i>this.page_rows)
			{
				pageNum++;
				writePage(document,writer,dataList,tableHeaderList,fieldTypeMap,fieldHzMap,flag,lnameVo,font);
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
	}

	/**
	 *	获取PDF文件最大行高（20191227）
	 * @param pageDataList
	 * @param tableHeaderList
	 * @param fieldTypeMap
	 * @param fieldHzMap
	 * @param font
	 * @return
	 */
	public float getMaxHeight(ArrayList pageDataList,ArrayList tableHeaderList,HashMap fieldTypeMap,HashMap fieldHzMap,Font font)
	{
		float maxHeight = 0;
		ArrayList maxHeightList = new ArrayList();
		try
		{
			ArrayList rowList = new ArrayList();
			PdfPCell cell=null;
			for(int i=0;i<tableHeaderList.size();i++)
			{
				ArrayList a_headerList=(ArrayList)tableHeaderList.get(i);

				float[] columnsWidth=new float[a_headerList.size()];
				float tableWidth=0f;
				for(int j=0;j<a_headerList.size();j++)
				{
					HashMap tempMap=(HashMap)a_headerList.get(j);
					columnsWidth[j]=((Float)tempMap.get("width")).floatValue();
					tableWidth+=((Float)tempMap.get("width")).floatValue();
				}
				//setMusterTitle(writer,lnameVo,tableWidth);
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
						cell.setMinimumHeight(this.arowHeigth); // 设置单元格的最小高度
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
				rowList.addAll(table.getRows());
			}
			for (int i=0;i<rowList.size();i++){
				PdfPRow pdfRow = (PdfPRow) rowList.get(i);
				maxHeightList.add(pdfRow.getMaxHeights());
			}
			maxHeight = (float) Collections.max(maxHeightList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return maxHeight;
	}
	
	
	
	public void writePage(Document document,PdfWriter writer,ArrayList pageDataList,ArrayList tableHeaderList,HashMap fieldTypeMap,HashMap fieldHzMap,int flag,RecordVo lnameVo,Font font)
	{
		try
		{
			
			PdfPCell cell=null;
			for(int i=0;i<tableHeaderList.size();i++)
			{
				ArrayList a_headerList=(ArrayList)tableHeaderList.get(i);
				if(flag>1) {
                    document.newPage();
                }
				
				float[] columnsWidth=new float[a_headerList.size()];
				float tableWidth=0f;
				for(int j=0;j<a_headerList.size();j++)
				{
					HashMap tempMap=(HashMap)a_headerList.get(j);
					columnsWidth[j]=((Float)tempMap.get("width")).floatValue();
					tableWidth+=((Float)tempMap.get("width")).floatValue();
				}
				setMusterTitle(writer,lnameVo,tableWidth);
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
						cell.setMinimumHeight(this.arowHeigth); // 设置单元格的最小高度
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
			//System.out.println("aff");
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
	        	 cell.setMinimumHeight(this.arowHeigth+this.banlance); // 设置单元格的最小高度
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
	public ArrayList getTableHeaderList(String[] field_widths,ArrayList fieldList,HashMap fieldHzMap){
		ArrayList list=new ArrayList();
		try {
			float[] field_width=new float[field_widths.length];
			for(int a=0;a<field_widths.length;a++){
				String numvalue=field_widths[a].toString();
				if(numvalue!=null&&numvalue.length()>0&&!"undefined".equalsIgnoreCase(numvalue)) {
                    field_width[a]=Float.parseFloat(field_widths[a]);
                }
			}
			boolean flag=false;  
			int i=0;
		    while(i<fieldList.size()){
				ArrayList tempList=new ArrayList();
				float width=50;
				//如果是第2页，需添加序列
				if(flag==true){
					HashMap map=new HashMap();
					String fieldName = (String)fieldList.get(0);
					if("recidx".equals(fieldName)) {
					    float field_width_value = field_width[0];
	                    width+=field_width_value;   
					    map.put("fieldName",fieldName);
	                    map.put("fieldHzName",(String)fieldHzMap.get((String)fieldList.get(0)));
	                    map.put("width",field_width_value);
	                    tempList.add(map);
					}
				}
				while(i<fieldList.size()){
					float field_width_value = field_width[i];
					if (field_width_value+50>this.body_width) {
						field_width_value = this.body_width-51;
						if (tempList.size() == 1) {
						    field_width_value = field_width_value-30;
                        }
					}
					width+=field_width_value;				
					if(width<=this.body_width){
						HashMap map=new HashMap();
						String fieldName = (String)fieldList.get(i);
						map.put("fieldName",fieldName);
						map.put("fieldHzName",(String)fieldHzMap.get((String)fieldList.get(i)));
						map.put("width",field_width_value);
						tempList.add(map);
					}else{
						flag=true;
						break;
					}
					i++;
				}
				list.add(tempList);
			}
		} catch (Exception e) {
			e.printStackTrace();
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
				//liuy 2014-12-22 5503：组织机构-单位管理-信息维护-常用花名册-文件（页面设置）-标题（设置如图）-输出pdf(设置的内容不能正确输出，粗体和斜体，一个勾选一个未勾选的情况下，会都没有效果) start
				if("#fi[1]".equals(this.paramtervo.getTitle_fi())&& "#fb[1]".equals(this.paramtervo.getTitle_fb())) {
                    fontEffect="4";  //粗斜体
                } else if("#fb[1]".equals(this.paramtervo.getTitle_fb())) {
                    fontEffect="2";  //粗体
                } else if("#fi[1]".equals(this.paramtervo.getTitle_fi())) {
                    fontEffect="3";  //斜体
                }
				//liuy end
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
		for(int i=0;i<field_widths.length;i++) {
			sum += Float.parseFloat(field_widths[i]);
		}
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
	 * 简单花名册新增方法 zyh 获得列头
	 * 取得花名册的列信息
	 * @param tabid
	 * @return
	 */
	public ArrayList getMusterField(String tabid,HashMap<String,Object> map){
		String itemids = (String) map.get("itemids");
		String showMusterSql = (String) map.get("showMusterSql");
		ArrayList list=new ArrayList();
		ArrayList fieldList=new ArrayList();
		HashMap   fieldHzMap=new HashMap();
		HashMap   fieldTypeMap=new HashMap();
		HashMap   fieldMaxFontNumMap=new HashMap();  //每个指标列最大的字数
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		RowSet rowSet2 = null;
		try{
            fieldList.add("recidx");
            fieldHzMap.put("recidx", "序号");
			String [] itemsArray = itemids.split(",");
			for (int i = 0; i < itemsArray.length; i++) {
                if (StringUtils.isNotBlank(itemsArray[i])) {
                    FieldItem fieldItem = DataDictionary.getFieldItem(itemsArray[i]);
                    fieldHzMap.put(itemsArray[i],fieldItem.getItemdesc());
                    fieldTypeMap.put(itemsArray[i],fieldItem.getItemtype());
                    fieldList.add(itemsArray[i]);
                }
            }
			list.add(fieldList);
			list.add(fieldHzMap);
			list.add(fieldTypeMap);
			StringBuffer sqll=new StringBuffer("");
			for(int i=0;i<fieldList.size();i++){	
				if("recidx".equals(fieldList.get(i))) {
				  continue;
				}
				String temp=(String)fieldList.get(i);
				sqll.append(","+Sql_switcher.isnull("max("
						+ Sql_switcher.datalength(temp)
						+ "/2)","0")+" "+temp);
			}
			String sql2 = "select * from ("+showMusterSql+") data";
			if (sqll.length()>0) {
			    sql2 = "select "+sqll.substring(1)+" from ("+showMusterSql+") data";
            }
			rowSet=dao.search(sql2);
			if(rowSet.next()){
				for(int i=0;i<fieldList.size();i++){	
					if("recidx".equals(fieldList.get(i))) {
					    fieldMaxFontNumMap.put("recidx","2");
						continue;
					}
					String temp=(String)fieldList.get(i);
					int a1=(int) (rowSet.getInt(temp)*1.0);
					String itemdesc = (String) fieldHzMap.get(temp);
					if (StringUtils.equals(itemdesc, "姓名")) {
                        a1=4;
                    }
					int a2=((String)fieldHzMap.get(temp)).length();
					if(a1>a2) {
                        fieldMaxFontNumMap.put(temp,String.valueOf(a1));
                    } else {
                        fieldMaxFontNumMap.put(temp,String.valueOf(a2));
                    }
				}
				
			}
			Iterator<Map.Entry<String, String>> entries = fieldMaxFontNumMap.entrySet().iterator(); 
			while (entries.hasNext()) { 
			  int a3 =0;
			  Map.Entry<String, String> entry = entries.next(); 
			  String itemid = entry.getKey();
              FieldItem fieldItem = DataDictionary.getFieldItem(itemid);
              if (null!=fieldItem) {
                  String itemtype = fieldItem.getItemtype();
                  String codesetid = fieldItem.getCodesetid();
                  if ("UN".equals(codesetid)||"UM".equals(codesetid)||"@K".equals(codesetid)) {
                      StringBuffer sql = new StringBuffer("select ");
                      sql.append(Sql_switcher.isnull("max("+ Sql_switcher.datalength("codeitemdesc")+ "/2)","0"));
                      sql.append(" desclength from ORGANIZATION where codesetid ='");
                      sql.append(codesetid);
                      sql.append("'");
                      rowSet2 = dao.search(sql.toString());
                      if (rowSet2.next()) {
                          a3=(int) (rowSet2.getInt("desclength")*1.0);
                      }
                      int a4 = Integer.parseInt(entry.getValue());
                      if (a3>a4) {
                          fieldMaxFontNumMap.put(itemid,String.valueOf(a3));
                      }
                  }else if (!"0".equals(codesetid)&&"A".equals(itemtype)) {
                      StringBuffer sql = new StringBuffer("select ");
                      sql.append(Sql_switcher.isnull("max("+Sql_switcher.datalength("codeitemdesc")+"/2)","0")+ " as desclength from codeitem where codesetid = '");
                      sql.append(codesetid);
                      sql.append("'");
                      try {
                          rowSet2 = dao.search(sql.toString());
                          if (rowSet2.next()) {
                              a3=(int) (rowSet2.getInt("desclength")*1.0);
                          }
                      } catch (Exception e) {
                          e.printStackTrace();
                      }finally {
                          PubFunc.closeResource(rowSet2);
                      }
                      int a4 = Integer.parseInt(entry.getValue());
                      if (a3>a4) {
                          fieldMaxFontNumMap.put(itemid,String.valueOf(a3));
                      }
                  }else if ("M".equals(itemtype)) {
                      fieldMaxFontNumMap.put(itemid,"15");
                  }else {
                      String size = (String) fieldMaxFontNumMap.get(itemid);
                      if (Integer.parseInt(size)>15) {
                          fieldMaxFontNumMap.put(itemid,"15");
                      }
                  }
              }
			}
			list.add(fieldMaxFontNumMap);
			
		}catch(Exception e){
			e.printStackTrace();
		}finally {
            PubFunc.closeResource(rowSet);
        }
		return list;
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
		HashMap   fieldTypeMap=new HashMap();
		HashMap   fieldMaxFontNumMap=new HashMap();  //每个指标列最大的字数
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try
		{
			RecordVo vo=new RecordVo(this.mustername.toLowerCase());
			rowSet=dao.search("select Field_name,colHz,field_type from  lbase where tabid="+tabid+" and Width<>0");
			fieldList.add("recidx");
			fieldHzMap.put("recidx",ResourceFactory.getProperty("kh.field.seq"));
			while(rowSet.next())
			{
				String field_name = rowSet.getString("Field_name");
				field_name=field_name.substring(field_name.indexOf(".")+1);
				String pri = userview.analyseFieldPriv(field_name);
				if(pri==null|| "0".equals(pri)) {
                    continue;
                }
				if(vo.hasAttribute(field_name.toLowerCase())){
					String colHz=rowSet.getString("colHz");
					fieldHzMap.put(field_name,colHz);
					fieldTypeMap.put(field_name,rowSet.getString("field_type"));
					fieldList.add(field_name);
				}
			}
			list.add(fieldList);
			list.add(fieldHzMap);
			list.add(fieldTypeMap);
			
			
			StringBuffer sqll=new StringBuffer("");
			for(int i=0;i<fieldList.size();i++)
			{
				String temp=(String)fieldList.get(i);
				sqll.append(","+Sql_switcher.isnull("max("
						+ Sql_switcher.datalength(temp)
						+ "/2)","0")+" "+temp);
			}
			
			rowSet=dao.search("select "+sqll.substring(1)+" from "+this.mustername);
			if(rowSet.next())
			{
				for(int i=0;i<fieldList.size();i++)
				{
					String temp=(String)fieldList.get(i);
					int a1=rowSet.getInt(temp);
					int a2=((String)fieldHzMap.get(temp)).length();
					if(a1>a2) {
                        fieldMaxFontNumMap.put(temp,String.valueOf(a1));
                    } else {
                        fieldMaxFontNumMap.put(temp,String.valueOf(a2));
                    }
				}
				
			}
			list.add(fieldMaxFontNumMap);
			
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
		if(this.paramtervo!=null)
		{
			
			if(this.paramtervo.getTop().length()>0) {
                t_margin=Float.parseFloat(PubFunc.round(String.valueOf(Float.parseFloat(this.paramtervo.getTop())/proportion),0));
            }
			if(this.paramtervo.getBottom().length()>0) {
                b_margin=Float.parseFloat(PubFunc.round(String.valueOf(Float.parseFloat(this.paramtervo.getBottom())/proportion),0));
            }
			if(this.paramtervo.getLeft().length()>0) {
                l_margin=Float.parseFloat(PubFunc.round(String.valueOf(Float.parseFloat(this.paramtervo.getLeft())/proportion),0));
            }
			if(this.paramtervo.getRight().length()>0) {
                r_margin=Float.parseFloat(PubFunc.round(String.valueOf(Float.parseFloat(this.paramtervo.getRight())/proportion),0));
            }
//			纵向
			if (this.paramtervo.getOrientation().length()>0&& "0".equals(this.paramtervo.getOrientation()))
			{
				
				if(this.paramtervo.getWidth().length()>0) {
                    page_width=Float.parseFloat(PubFunc.round(String.valueOf(Float.parseFloat(this.paramtervo.getWidth())/proportion),0));
                }
				if(this.paramtervo.getHeight().length()>0) {
                    page_height=Float.parseFloat(PubFunc.round(String.valueOf(Float.parseFloat(this.paramtervo.getHeight())/proportion),0));
                }
			}
			else
			{
				if(this.paramtervo.getWidth().length()>0) {
                    page_height=Float.parseFloat(PubFunc.round(String.valueOf(Float.parseFloat(this.paramtervo.getWidth())/proportion),0));
                }
				if(this.paramtervo.getHeight().length()>0) {
                    page_width=Float.parseFloat(PubFunc.round(String.valueOf(Float.parseFloat(this.paramtervo.getHeight())/proportion),0));
                }
			}
			
		}
		
		String rank="1";
		if(this.paramtervo!=null&&this.paramtervo.getBody_fz()!=null&&this.paramtervo.getBody_fz().trim().length()>0)
		{
		    String	bodyFontSize=this.paramtervo.getBody_fz();
			BigDecimal abodyFontSize=new BigDecimal(bodyFontSize);
			rank=abodyFontSize.divide(new BigDecimal("5"),1,BigDecimal.ROUND_HALF_UP).toString();
		}
		if(!"1".equals(rank)) {
            this.arowHeigth=this.arowHeigth*Float.parseFloat(rank)*0.5f;
        }
		
		
		this.body_height=this.page_height-this.t_margin-this.b_margin;
		this.body_width=this.page_width-this.l_margin-this.r_margin;
		String t_rows=String.valueOf((this.body_height-(this.arowHeigth+this.banlance))/(this.arowHeigth+3));
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
	
	
	public String getTitleVale(String value)
	{
		String a_value=value;
		if(a_value.indexOf("&["+ResourceFactory.getProperty("report.parse.p")+"]")!=-1) {
            a_value=a_value.replaceAll("&\\["+ResourceFactory.getProperty("report.parse.p")+"\\]",String.valueOf(this.pageNum));
        }
		if(a_value.indexOf("&["+ResourceFactory.getProperty("report.parse.e")+"]")!=-1  ) {
            a_value=a_value.replaceAll("&\\["+ResourceFactory.getProperty("report.parse.e")+"\\]",this.userName);
        }
		if(a_value.indexOf("&["+ResourceFactory.getProperty("report.parse.d")+"]")!=-1)
		{
			SimpleDateFormat d=new SimpleDateFormat("yyyy-MM-dd");
			 a_value=a_value.replaceAll("&\\["+ResourceFactory.getProperty("report.parse.d")+"\\]",d.format(new java.util.Date()));
		}
		if(a_value.indexOf("&["+ResourceFactory.getProperty("report.parse.t")+"]")!=-1)
		{
			SimpleDateFormat   formatter   =   new   SimpleDateFormat("HH:mm:ss");
			a_value=a_value.replaceAll("&\\["+ResourceFactory.getProperty("report.parse.t")+"\\]",formatter.format(new java.util.Date()));
		}
		if(a_value.indexOf("&["+ResourceFactory.getProperty("workdiary.message.total.person")+"]")!=-1)
		{
			a_value=a_value.replaceAll("&\\["+ResourceFactory.getProperty("workdiary.message.total.person")+"\\]",String.valueOf(this.totalNum));
		}
        if(a_value.indexOf("&[YYYY年YY月]")!=-1)
        {
        	SimpleDateFormat d=new SimpleDateFormat("yyyy年MM月"); //changxy 20160811 【12305】
            a_value=a_value.replaceAll("&\\[YYYY年YY月\\]", d.format(new java.util.Date()));
        }
        //20191223 解决简单花名册--》页面设置--》页头、页尾内容中的 &[总行数] 显示问题
        if(a_value.indexOf("&["+ResourceFactory.getProperty("hmuster.label.totalRows")+"]") != -1){
			a_value=a_value.replaceAll("&\\["+ResourceFactory.getProperty("hmuster.label.totalRows")+"\\]",String.valueOf(this.totalNum));
		}
		return a_value;
	}
	
	
	
	//设置表题，页头、页尾
	public void setMusterTitle(PdfWriter writer,RecordVo lnameVo,float tablewidth)
	{
		//写标题
		String t_fontfamilyname=ResourceFactory.getProperty("gz.gz_acounting.m.font");
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
	       	if(this.paramtervo.getTitle_fs()!=null&&this.paramtervo.getTitle_fs().length()>0) {
                t_strikethru=this.paramtervo.getTitle_fs();
            }

        }
        float left=tablewidth/2+this.l_margin-lnameVo.getString("title").getBytes().length/2*(t_fontSize+2)/2;
        if(left<0) {
            left=this.page_width/2-lnameVo.getString("title").getBytes().length/2*(t_fontSize+2)/2;
        }
        
        String a_title=lnameVo.getString("hzname");
        if(this.paramtervo!=null&&this.paramtervo.getTitle_fw().length()>0) {
            a_title=this.paramtervo.getTitle_fw();
        }
	
        excecute(a_title,left,this.page_height-20,a_title.length()*t_fontSize,
				20f,t_fontEffect,t_fontSize,t_fontfamilyname,t_color,t_underLine,t_strikethru,
				writer);
	
        
		//写页头
		String h_fontfamilyname=ResourceFactory.getProperty("gz.gz_acounting.m.font");
        String h_fontEffect="0";
        int    h_fontSize=6;    
        String h_color="#000000";
        String  h_underLine="#fu[0]";
        String  h_strikethru="#fs[0]";
        int totalWidth=0;
        
       
        
		if(this.paramtervo!=null)
        {
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
	       	if(lnameVo.getString("lhead")!=null&&lnameVo.getString("lhead").length()>1)
	       	{
	       		totalWidth+=lnameVo.getString("lhead").getBytes().length/2*(h_fontSize+4);
	       	}
	       	if(lnameVo.getString("mhead")!=null&&lnameVo.getString("mhead").length()>1)
			{
	       		totalWidth+=lnameVo.getString("mhead").getBytes().length/2*(h_fontSize+4);
			}
	        if(lnameVo.getString("rhead")!=null&&lnameVo.getString("rhead").length()>1)
			{
	        	totalWidth+=lnameVo.getString("rhead").getBytes().length/2*(h_fontSize+4);
			}
        }	
		
		if(totalWidth<tablewidth)
		{
		
			if(lnameVo.getString("lhead")!=null&&lnameVo.getString("lhead").length()>1)
			{
				excecute(getTitleVale(lnameVo.getString("lhead")),this.l_margin,this.page_height-(this.t_margin-25),30,
						20f,h_fontEffect,h_fontSize,h_fontfamilyname,h_color,h_underLine,h_strikethru,
						writer);
			}
			if(lnameVo.getString("mhead")!=null&&lnameVo.getString("mhead").length()>1)
			{
				excecute(getTitleVale(lnameVo.getString("mhead")),tablewidth/2-lnameVo.getString("mhead").getBytes().length/2*(t_fontSize+2)/2+this.l_margin,this.page_height-(this.t_margin-25),30f,
						20f,h_fontEffect,h_fontSize,h_fontfamilyname,h_color,h_underLine,h_strikethru,
						writer);
			}
			
		   if(lnameVo.getString("rhead")!=null&&lnameVo.getString("rhead").length()>1)
			{
				excecute(getTitleVale(lnameVo.getString("rhead")),tablewidth+this.l_margin-lnameVo.getString("rhead").getBytes().length/2*(t_fontSize-2),this.page_height-(this.t_margin-25),30f,
						20f,h_fontEffect,h_fontSize,h_fontfamilyname,h_color,h_underLine,h_strikethru,
						writer);
			}
		}

		//写页尾
		String f_fontfamilyname=ResourceFactory.getProperty("gz.gz_acounting.m.font");
        String f_fontEffect="0";
        int    f_fontSize=6;     
        String f_color="#000000";
        String  f_underLine="#fu[0]";
        String  f_strikethru="#fs[0]";
        totalWidth=0;
		if(this.paramtervo!=null)
        {
			if(this.paramtervo.getTile_fn().length()>0) {
                f_fontfamilyname=this.paramtervo.getTile_fn();
            }
	       	 f_fontEffect=getFontEffect(2);
	       	 if(this.paramtervo.getTile_fz().length()>0) {
                 f_fontSize=Integer.parseInt(this.paramtervo.getTile_fz());
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
	       	
	       	if(lnameVo.getString("lfoot")!=null&&lnameVo.getString("lfoot").length()>1)
	       	{
	       		totalWidth+=lnameVo.getString("lfoot").getBytes().length/2*(h_fontSize+4);
	       	}
	       	if(lnameVo.getString("mfoot")!=null&&lnameVo.getString("mfoot").length()>1)
			{
	       		totalWidth+=lnameVo.getString("mfoot").getBytes().length/2*(h_fontSize+4);
			}
	       	if(lnameVo.getString("rfoot")!=null&&lnameVo.getString("rfoot").length()>1)
			{
	        	totalWidth+=lnameVo.getString("rfoot").getBytes().length/2*(h_fontSize+4);
			}
        }
		if(totalWidth<tablewidth)
		{
			if(lnameVo.getString("lfoot")!=null&&lnameVo.getString("lfoot").length()>1)
			{
				excecute(getTitleVale(lnameVo.getString("lfoot")),this.l_margin,this.page_height-(this.t_margin+this.body_height+5),30,
						20f,f_fontEffect,f_fontSize,f_fontfamilyname,f_color,f_underLine,f_strikethru,
						writer);
			}
			if(lnameVo.getString("mfoot")!=null&&lnameVo.getString("mfoot").length()>1)
			{
				excecute(getTitleVale(lnameVo.getString("mfoot")),tablewidth/2-lnameVo.getString("mfoot").getBytes().length/2*(t_fontSize+2)/2+this.l_margin,this.page_height-(this.t_margin+this.body_height+5),30f,
						20f,f_fontEffect,f_fontSize,f_fontfamilyname,f_color,f_underLine,f_strikethru,
						writer);
			}
		    if(lnameVo.getString("rfoot")!=null&&lnameVo.getString("rfoot").length()>1)
			{
				excecute(getTitleVale(lnameVo.getString("rfoot")),tablewidth+this.l_margin-lnameVo.getString("rfoot").getBytes().length/2*(t_fontSize-2),this.page_height-(this.t_margin+this.body_height+5),30f,
						20f,f_fontEffect,f_fontSize,f_fontfamilyname,f_color,f_underLine,f_strikethru,
						writer);
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
		if("#fu[1]".equals(underLine)) {
            font.setStyle(Font.UNDERLINE);
        }
		if("#fs[1]".equals(strikethru)) {
            font.setStyle(Font.STRIKETHRU);
        }
		PdfPTable table = new PdfPTable(1);
	
		if(width==0) {
            return;
        }
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
	/**
	 * 简单花名册新增方法将结果集数据插入map zyh 2019-03-12
	 * @param data
	 * @param j
	 * @param resultSet
	 * @param fieldList
	 * @return
	 */
	private HashMap insertData(HashMap dataMap,int i,HashMap fieldTypeMap,ResultSet resultSet,ArrayList fieldList) {
		String fieldName = "";
		try {
			//部门显示几级层级
			Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.conn);
			String uplevelStr = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
			if(uplevelStr==null||uplevelStr.length()==0){
				uplevelStr="0";
			}
			int upLevel = Integer.parseInt(uplevelStr);

			for(Iterator t=fieldList.iterator();t.hasNext();){
				fieldName=(String)t.next();
				if("recidx".equals(fieldName)) {
					dataMap.put(fieldName,String.valueOf(i));
				}else{
				    FieldItem fielditem =DataDictionary.getFieldItem(fieldName);
					if( StringUtils.equals("D", fielditem.getItemtype())){
						Date date=resultSet.getDate(fieldName);
						if (null==date) {
						    dataMap.put(fieldName,"");
	                    }else {
	                        String dateValue = PubFunc.FormatDate(date);
	                        if(dateValue==null) {
                                dateValue="";
                            }
	                        if(fielditem.getItemlength()==10){
	                            if(dateValue.length()>fielditem.getItemlength()) {
                                    dateValue=dateValue.substring(0,10);
                                }
	                        }else if(fielditem.getItemlength()==7){
	                            if(dateValue.length()>fielditem.getItemlength()) {
                                    dateValue=dateValue.substring(0,7);
                                }
	                        }else if(fielditem.getItemlength()==4){
	                            if(dateValue.length()>fielditem.getItemlength()) {
                                    dateValue=dateValue.substring(0,4);
                                }
	                        }
	                        dataMap.put(fieldName,dateValue);
                        }
					}else if (StringUtils.equals("A", fielditem.getItemtype())) {
						String dateValue=resultSet.getString(fieldName);
						if (StringUtils.isNotBlank(dateValue)) {
						    if ("0".equals(fielditem.getCodesetid())) {
	                            dataMap.put(fieldName,dateValue);
	                        }else {
	                            CodeItem codeItem = AdminCode.getCode(fielditem.getCodesetid(), dateValue);
	                            if (codeItem==null&&"UN".equals(fielditem.getCodesetid())) {
	                                codeItem = AdminCode.getCode("UM", dateValue);
	                            }
								//如果是部门，按设置显示部门层级
								ifUM:if("UM".equals(fielditem.getCodesetid()) && upLevel>0){
									codeItem = AdminCode.getCode(fielditem.getCodesetid(),dateValue, upLevel);
									if(codeItem==null){
										break ifUM;
									}
								}
	                            if (codeItem==null) {
	                                dataMap.put(fieldName, "");
                                }else {
                                    dataMap.put(fieldName,codeItem.getCodename());
                                }
	                        }
                        }else {
                            dataMap.put(fieldName,"");
                        }
					}else if (StringUtils.equals("N", fielditem.getItemtype())) {
						if (resultSet.getString(fieldName)==null) {
							dataMap.put(fieldName,"");
						}else {
							if (fielditem.getDecimalwidth()>0) {
								double value = resultSet.getDouble(fieldName);
								dataMap.put(fieldName, new DecimalFormat(",##0.00").format(value));
							}else {
								int value = resultSet.getInt(fieldName);
								dataMap.put(fieldName, new DecimalFormat(",###").format(value));
							}
						}
                    }else{
						dataMap.put(fieldName,resultSet.getString(fieldName)==null?"":resultSet.getString(fieldName));
					}
				}
			}
		} catch (Exception e) {
			dataMap.put(fieldName,"");
		}
		return dataMap;
	}
	public UserView getUserview() {
		return userview;
	}


	public void setUserview(UserView userview) {
		this.userview = userview;
	}


    public String getTabid() {
        return tabid;
    }


    public void setTabid(String tabid) {
        this.tabid = tabid;
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
	
	
	
	
	
	
	
}
