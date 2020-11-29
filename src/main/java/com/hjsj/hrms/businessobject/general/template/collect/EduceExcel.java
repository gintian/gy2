package com.hjsj.hrms.businessobject.general.template.collect;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class EduceExcel {
    
	private Connection conn;
	private UserView userView;
	public EduceExcel(){}
	public EduceExcel(Connection conn)
	{
		this.conn=conn;
	}
	/**
	 * 建立Excel
	 * @param column
	 * @param where
	 * @param filedList
	 * @return
	 */
    public String creatExcel(String columns,String where,String nbase,String subset,ArrayList filedList)
    {
    	String excel_filename="goabroad_collectstat_123456.xls";
    	if (userView != null) {
            excel_filename = "gb_" + userView.getUserName() + ".xls";
        }
    	HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
		HSSFRow row=null;
		HSSFCell csCell=null;
		ArrayList list=getTableField(filedList);
		String subsetTable=nbase+subset;
		String[][] excelData=getExcelDataInfo(subsetTable,columns,where,list);
		String tableTitle=getTableExtendInfo(subset);
		short n=executeTitel(tableTitle,excelData,sheet,workbook);  //写上表头 和 标题		
		for(int i=0;i<excelData.length;i++)
		{
			row = sheet.createRow(i+n);				
			for(short j=0;j<excelData[i].length;j++)
			{
				csCell =row.createCell(j);
//				csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
				csCell.setCellValue(excelData[i][j]);
			}
			
		}
		try
		{
			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+excel_filename);
			workbook.write(fileOut);
			fileOut.close();
			sheet=null;
			workbook=null;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
    	return excel_filename;
    }
    
    /**
	 * 建立Excel
	 * @param column
	 * @param where
	 * @param filedList
	 * @return
	 */
    public String creatExcel(String columns,String where,String nbase,String subset,ArrayList filedList,String order)
    {
    	String excel_filename = "goabroad_collectstat_123456.xls";
    	if (userView != null) {
            excel_filename = this.userView.getUserName() + "_goabroad.xls";
        }
    	HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
		HSSFRow row=null;
		HSSFCell csCell=null;
		ArrayList list=getTableField(filedList);
		String subsetTable=nbase+subset;
		String[][] excelData=getExcelDataInfo(subsetTable,columns,where,list,order);
		String tableTitle=getTableExtendInfo(subset);
		// 数据列 日期型数值型居右
		if (list != null && list.size() > 0) {
			ArrayList fieldList = (ArrayList) list.get(0);
			HashMap fieldTypeMap = (HashMap) list.get(2);

			for (int i = 0; i < fieldList.size(); i++) {
				String filedname = (String) fieldList.get(i);
				if (!fieldTypeMap.containsKey(filedname)) {
					continue;
				}
				String type = (String) fieldTypeMap.get(filedname);
				if ("D".equalsIgnoreCase(type) || "N".equalsIgnoreCase(type)) {
					HSSFCellStyle style = sheet.getColumnStyle(i);
					if(style==null) {
						style=workbook.createCellStyle();
					}
					style.setAlignment(HorizontalAlignment.RIGHT);
					sheet.setDefaultColumnStyle(i, style);
				}
			}

		}
		short n=executeTitel(tableTitle,excelData,sheet,workbook);  //写上表头 和 标题
		for(int i=0;i<excelData.length;i++)
		{
			row = sheet.createRow(i+n);				
			for(short j=0;j<excelData[i].length;j++)
			{
				csCell =row.createCell(j);
//				csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
				csCell.setCellValue(excelData[i][j]);
			}
			
		}
		try
		{
			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+excel_filename);
			workbook.write(fileOut);
			fileOut.close();
			sheet=null;
			workbook=null;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
    	return excel_filename;
    }
    /**
     * 得到报表信息
     * @param subset
     * @return
     */
    public String getTableExtendInfo(String subset)
	{
		
		String  sql="select fieldSetDesc from fieldset where fieldSetId='"+subset+"'";
		String table_info="";
		try
		{
			RowSet rs=null;
			ContentDAO dao =new ContentDAO(this.conn);
			rs=dao.search(sql);
			if(rs.next())
			{
				table_info=rs.getString("fieldSetDesc");
			}
		}catch(Exception e)
		{
		   e.printStackTrace();	
		}
		return table_info;
	}
    private String[][] getExcelDataInfo(String subsetTable,String columns,String where,ArrayList list)
    {
    	ArrayList fieldList=(ArrayList)list.get(0);
		HashMap   fieldHzMap=(HashMap)list.get(1);
		HashMap   fieldTypeMap=(HashMap)list.get(2);
		HashMap   fieldCodeMap=(HashMap)list.get(3);
    	StringBuffer sql=new StringBuffer();
    	sql.append("select count("+subsetTable+".a0100) counts");
    	sql.append(" "+where);
    	int rs_num=0;
    	String[][] data=null;
    	int field_num=fieldList.size();
    	RowSet rs=null;
    	ContentDAO dao=new ContentDAO(this.conn);
    	try
    	{
    		/******得到纪录总数*******/
    		rs=dao.search(sql.toString());
    		if(rs.next())
    		{
    			rs_num=rs.getInt("counts");
    		}else
    		{
    			return null;
    		}
    		data=new String[rs_num+1][field_num];
    		for(int i=0;i<fieldList.size();i++)
			{
				data[0][i]=(String)fieldHzMap.get((String)fieldList.get(i));
			}
    		/*******得到纪录数据**********/
    		sql=new StringBuffer();
    		sql.append("select "+columns);
    		sql.append(" "+where); 
    		rs=dao.search(sql.toString());
    		int j=1;
    		while(rs.next())
    		{
    			for(int i=0;i<fieldList.size();i++)
    			{
    				String fieldName=(String)fieldList.get(i);
					if("recidx".equals(fieldName))
					{
						data[j][i]=String.valueOf(j);
					}else
					{
						if(rs.getString(fieldName)!=null)
						{
							if("A".equalsIgnoreCase((String)fieldTypeMap.get(fieldName))&& "0".equalsIgnoreCase((String)fieldCodeMap.get(fieldName)))
							{
								data[j][i]=rs.getString(fieldName);
							}else if("A".equalsIgnoreCase((String)fieldTypeMap.get(fieldName))&&!"0".equalsIgnoreCase((String)fieldCodeMap.get(fieldName)))
							{
								data[j][i]=AdminCode.getCode((String)fieldCodeMap.get(fieldName),rs.getString(fieldName))!=null?AdminCode.getCode((String)fieldCodeMap.get(fieldName),rs.getString(fieldName)).getCodename():"";
							}
							else if("N".equalsIgnoreCase((String)fieldTypeMap.get(fieldName)))
						    {
									if(rs.getFloat(fieldName)==0)
									{
										data[j][i]=" ";
									}
									else {
                                        data[j][i]=rs.getString(fieldName);
                                    }
							}
							else if("M".equalsIgnoreCase((String)fieldTypeMap.get(fieldName)))
							{
								data[j][i]=Sql_switcher.readMemo(rs,fieldName);
							}else if("D".equalsIgnoreCase((String)fieldTypeMap.get(fieldName)))
							{
								Date dd=rs.getDate(fieldName);
								if(dd!=null) {
                                    data[j][i]=DateUtils.format(dd,"yyyy.MM.dd");
                                } else {
                                    data[j][i]=" ";
                                }
							}
						}else {
                            data[j][i]=" ";
                        }
					
					}
    				
    			}
    			j++;
    		}
    		
    	}catch(Exception e)
    	{
    	   e.printStackTrace();	
    	}
        return data;	
    }
    
    private String[][] getExcelDataInfo(String subsetTable,String columns,String where,ArrayList list,String order)
    {
    	ArrayList fieldList=(ArrayList)list.get(0);
		HashMap   fieldHzMap=(HashMap)list.get(1);
		HashMap   fieldTypeMap=(HashMap)list.get(2);
		HashMap   fieldCodeMap=(HashMap)list.get(3);
    	StringBuffer sql=new StringBuffer();
    	sql.append("select count("+subsetTable+".a0100) counts");
    	sql.append(" "+where);
    	int rs_num=0;
    	String[][] data=null;
    	int field_num=fieldList.size();
    	RowSet rs=null;
    	ContentDAO dao=new ContentDAO(this.conn);
    	try
    	{
    		/******得到纪录总数*******/
    		rs=dao.search(sql.toString());
    		if(rs.next())
    		{
    			rs_num=rs.getInt("counts");
    		}else
    		{
    			return null;
    		}
    		data=new String[rs_num+1][field_num];
    		for(int i=0;i<fieldList.size();i++)
			{
				data[0][i]=(String)fieldHzMap.get((String)fieldList.get(i));
			}
    		/*******得到纪录数据**********/
    		sql=new StringBuffer();
    		sql.append("select "+columns);
    		sql.append(" "+where); 
    		sql.append(" ");
    		sql.append(order);
    		rs=dao.search(sql.toString());
    		int j=1;
    		while(rs.next())
    		{
    			for(int i=0;i<fieldList.size();i++)
    			{
    				String fieldName=(String)fieldList.get(i);
					if("recidx".equals(fieldName))
					{
						data[j][i]=String.valueOf(j);
					}else
					{
						if(rs.getObject(fieldName)!=null)//bug 39362 日期数据报错java.sql.Timestamp cannot be cast to java.sql.Date
						{
							if("A".equalsIgnoreCase((String)fieldTypeMap.get(fieldName))&& "0".equalsIgnoreCase((String)fieldCodeMap.get(fieldName)))
							{
								data[j][i]=rs.getString(fieldName);
							}else if("A".equalsIgnoreCase((String)fieldTypeMap.get(fieldName))&&!"0".equalsIgnoreCase((String)fieldCodeMap.get(fieldName)))
							{
								data[j][i]=AdminCode.getCode((String)fieldCodeMap.get(fieldName),rs.getString(fieldName))!=null?AdminCode.getCode((String)fieldCodeMap.get(fieldName),rs.getString(fieldName)).getCodename():"";
							}
							else if("N".equalsIgnoreCase((String)fieldTypeMap.get(fieldName)))
						    {
									if(rs.getFloat(fieldName)==0)
									{
										data[j][i]=" ";
									}
									else {
										FieldItem item=DataDictionary.getFieldItem(fieldName);
										if(item.getDecimalwidth()!=0) {//数值型指标保留小数位数修改
											float value=Float.parseFloat(rs.getString(fieldName));
											data[j][i]=String.format("%."+item.getDecimalwidth()+"f", value);
										}else {
											data[j][i]=rs.getString(fieldName);
										}
										
									}
							}
							else if("M".equalsIgnoreCase((String)fieldTypeMap.get(fieldName)))
							{
								data[j][i]=Sql_switcher.readMemo(rs,fieldName);
							}else if("D".equalsIgnoreCase((String)fieldTypeMap.get(fieldName)))
							{
								Date dd=rs.getDate(fieldName);
								if(dd!=null) {
                                    data[j][i]=DateUtils.format(dd,"yyyy.MM.dd");
                                } else {
                                    data[j][i]=" ";
                                }
							}
						}else {
                            data[j][i]=" ";
                        }
					
					}
    				
    			}
    			j++;
    		}
    		
    	}catch(Exception e)
    	{
    	   e.printStackTrace();	
    	}
        return data;	
    }
    
    public short executeTitel(String title,String[][] excelData,HSSFSheet sheet,HSSFWorkbook workbook)
    {
    	short n=0;
		HSSFRow row=null;
		HSSFCell csCell=null;
//		写标题
		if(title!=null&&excelData!=null&&excelData.length>0&&excelData[0].length>2)
		{
			HSSFFont font = workbook.createFont();			
			font.setColor(HSSFFont.COLOR_NORMAL);
			font.setBold(true);
			HSSFCellStyle cellStyle= workbook.createCellStyle();
			cellStyle.setFont(font);
			
			row=sheet.createRow(n);
			csCell=row.createCell(Short.parseShort(String.valueOf(excelData[0].length/2)));
			csCell.setCellStyle(cellStyle);
//			csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
			csCell.setCellValue(title);
			n++;
			n++;
		}
		return n;
    }
   
    /**
     * 对操作的字段进行处理
     * @param a_fieldList
     * @return
     */
    public ArrayList getTableField(ArrayList a_fieldList)
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
				if("a0100".equalsIgnoreCase(item.getItemid())|| "i9999".equalsIgnoreCase(item.getItemid())) {
                    continue;
                }
				field_name=item.getItemid();
				String colHz=item.getItemdesc();
				fieldHzMap.put(field_name,colHz);			
				fieldList.add(field_name);
				fieldTypeMap.put(field_name,item.getItemtype());
				fieldCodeMap.put(field_name,item.getCodesetid());
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
    public void setUserView(UserView userView) {
        this.userView = userView;
    }
    public UserView getUserView() {
        return userView;
    }
}
