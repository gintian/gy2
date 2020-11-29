package com.hjsj.hrms.businessobject.kq.feast_manage;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import org.apache.poi.hssf.usermodel.*;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ExcelHols {
    private Connection conn;
    // 导出数据的类型
    private String[][] dataType;
	
	public ExcelHols(){}
	public ExcelHols(Connection conn)
	{
		this.conn=conn;
	}
	public String creatExcel(String sqlstr,ArrayList filedList)
	{
//		String excel_filename="kq_hols_123456.xls";
		//防止并发导出excel 出现数据串行问题，增加随机数
		String excel_filename="kq_hols_"+PubFunc.getStrg()+".xls";
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
		HSSFRow row=null;
		HSSFCell csCell=null;
		ArrayList list=getTableField(filedList);
		String[][] excelData=getExcelDataInfo(sqlstr,list);	
		short n=executeTitel("假期管理数据",excelData,sheet,workbook);  //写上表头 和 标题
		
		//设置数字两位小数
		HSSFDataFormat format = workbook.createDataFormat(); 
		HSSFCellStyle cStyle = workbook.createCellStyle();		
		short forma = format.getFormat("0.00");
		cStyle.setDataFormat(forma);
		for(int i=0;i<excelData.length;i++)
		{
			row = sheet.createRow(i+n);				
			for(short j=0;j<excelData[i].length;j++)
			{
				csCell =row.createCell(j);
//				csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
				if (excelData[i][j] != null && excelData[i][j].length() > 0) {
					if ("N".equalsIgnoreCase(dataType[i][j])) {
						float f = Float.parseFloat(excelData[i][j]);
						csCell.setCellStyle(cStyle);
						csCell.setCellValue(f);
						
					} else {
						csCell.setCellValue(excelData[i][j]);
					}
				} else {
					continue;
					
				}
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
		HashMap   fieldDeciMap=new HashMap();
		ArrayList itemList = new ArrayList();
		try
		{
			fieldList.add("recidx");
			fieldHzMap.put("recidx",ResourceFactory.getProperty("recidx.label"));
			fieldTypeMap.put("recidex","A");
			fieldCodeMap.put("recidx","0");
			itemList.add("");
			
			String field_name="";
			for(int i=0;i<a_fieldList.size();i++)
			{
				FieldItem item=(FieldItem)a_fieldList.get(i);				
				if("a0100".equalsIgnoreCase(item.getItemid())|| "status".equalsIgnoreCase(item.getItemid())) {
                    continue;
                }
				field_name=item.getItemid();
				if (!"f1".equalsIgnoreCase(item.getItemid()))
				{
					if(!"1".equals(item.getState())) {
                        continue;
                    }
					fieldDeciMap.put(field_name,item.getDecimalwidth()+"");
				}
				else {
					fieldDeciMap.put(field_name,0+"");
				}
				
				
				String colHz=item.getItemdesc();
				fieldHzMap.put(field_name,colHz);		//nbase，	
				fieldList.add(field_name);
				fieldTypeMap.put(field_name,item.getItemtype());
				fieldCodeMap.put(field_name,item.getCodesetid());
				
				itemList.add(item);
			}
			list.add(fieldList);
			list.add(fieldHzMap);
			list.add(fieldTypeMap);
			list.add(fieldCodeMap);
			list.add(fieldDeciMap);
			list.add(itemList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
    private String[][] getExcelDataInfo(String sqlstr,ArrayList list)
    {
    	ArrayList fieldList=(ArrayList)list.get(0);
		HashMap   fieldHzMap=(HashMap)list.get(1);
		HashMap   fieldTypeMap=(HashMap)list.get(2);
		HashMap   fieldCodeMap=(HashMap)list.get(3);
		HashMap   fieldDeciMap=(HashMap)list.get(4);
		List itemList = (List) list.get(5);
		int rs_num=0;
    	String[][] data=null;
    	int field_num=fieldList.size();
    	RowSet rs=null;
    	ContentDAO dao=new ContentDAO(this.conn);
    	sqlstr=sqlstr+" order by i,b0110,e0122,e01a1,a0100";
    	try
    	{
    		/******得到纪录总数*******/
    		rs=dao.search(sqlstr);    		
    		while(rs.next())
    		{
    			rs_num++;
    		}
    		data=new String[rs_num+1][field_num];
    		dataType = new String[rs_num+1][field_num];
    		for(int i=0;i<fieldList.size();i++)
			{
				data[0][i]=(String)fieldHzMap.get((String)fieldList.get(i));
				dataType[0][i] = "A";
			}
    		/*******得到纪录数据**********/
    		rs=dao.search(sqlstr.toString());
    		int j=1;
    		while(rs.next())
    		{
    			for(int i=0;i<fieldList.size();i++)
    			{
    				String fieldName=(String)fieldList.get(i);
					if("recidx".equals(fieldName))
					{
						data[j][i]=String.valueOf(j);
						dataType[j][i] = "A";
						
					}else
					{
						//ora库中时间类型 rs.getString(fieldName)!=null 出现问题；所以改为判断时间在处理 0017887 
						if("D".equalsIgnoreCase((String)fieldTypeMap.get(fieldName)))
						{
							Date date = rs.getDate(fieldName);
							if(date!=null)
							{	FieldItem item = (FieldItem) itemList.get(i);
								if (item.getItemlength() == 10) {
									data[j][i]=DateUtils.format(date, "yyyy-MM-dd");
								} else if (item.getItemlength() == 16) {
									data[j][i]=DateUtils.format(date, "yyyy-MM-dd HH:mm");
								} else if (item.getItemlength() == 18) {
									data[j][i]=DateUtils.format(date, "yyyy-MM-dd HH:mm:ss");
								} else {
									data[j][i]=DateUtils.format(date, "yyyy-MM-dd HH:mm");
								}
								dataType[j][i] = "D";
							}else
							{
								data[j][i]=" ";
								dataType[j][i] = "D";
							}
						}else
						{
							if(rs.getString(fieldName)!=null)
							{
								if("A".equalsIgnoreCase((String)fieldTypeMap.get(fieldName))&& "0".equalsIgnoreCase((String)fieldCodeMap.get(fieldName)))
								{
									data[j][i]=rs.getString(fieldName);
									dataType[j][i] = "A";
								}else if("A".equalsIgnoreCase((String)fieldTypeMap.get(fieldName))&&!"0".equalsIgnoreCase((String)fieldCodeMap.get(fieldName)))
								{
									if ("e0122".equalsIgnoreCase(fieldName)) {
										Sys_Oth_Parameter sys = new Sys_Oth_Parameter(this.conn);
										String uplevel = sys.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
										if (uplevel == null || uplevel.length() <= 0) {
											uplevel = "0";
										}
										CodeItem codeItem = AdminCode.getCode((String)fieldCodeMap.get(fieldName),rs.getString(fieldName),Integer.parseInt(uplevel));										
										String value = codeItem == null ? "" : codeItem.getCodename();
										data[j][i] = value;
										dataType[j][i] = "A";
									} else {
										data[j][i]=AdminCode.getCode((String)fieldCodeMap.get(fieldName),rs.getString(fieldName))!=null?AdminCode.getCode((String)fieldCodeMap.get(fieldName),rs.getString(fieldName)).getCodename():"";
										dataType[j][i] = "A";
									}
								}
								else if("N".equalsIgnoreCase((String)fieldTypeMap.get(fieldName)))
							    {
										if(rs.getFloat(fieldName)==0)
										{
											data[j][i]="0.00";
											dataType[j][i] = "N";
										}
										else
										{
											String deic=(String)fieldDeciMap.get(fieldName);		
											if(deic==null||deic.length()<=0) {
                                                deic="0";
                                            }
											data[j][i]= PubFunc.DoFormatDecimal(rs.getString(fieldName),Integer.parseInt(deic));
											dataType[j][i] = "N";
											
										}
											
								}
								else if("M".equalsIgnoreCase((String)fieldTypeMap.get(fieldName)))
								{
									data[j][i]=Sql_switcher.readMemo(rs,fieldName);
									dataType[j][i] = "M";
								}
								//修改：找到开始时间与结束时间用"D"来判断
								else if ("D".equalsIgnoreCase((String)fieldTypeMap.get(fieldName)))
								{
//									data[j][i]=Sql_switcher.readMemo(rs,fieldName);
									Date date = rs.getDate(fieldName);
									FieldItem item = (FieldItem) itemList.get(i);
									if (item.getItemlength() == 10) {
										data[j][i]=DateUtils.format(date, "yyyy-MM-dd");
									} else if (item.getItemlength() == 16) {
										data[j][i]=DateUtils.format(date, "yyyy-MM-dd HH:mm");
									} else if (item.getItemlength() == 18) {
										data[j][i]=DateUtils.format(date, "yyyy-MM-dd HH:mm:ss");
									} else {
										data[j][i]=DateUtils.format(date, "yyyy-MM-dd HH:mm");
									}
									dataType[j][i] = "D";
								}
							}else{
								data[j][i]="";
								dataType[j][i] = "";
							}
						}
					}
    				
    			}
    			j++;
    		}
    		
    		
    	}catch(Exception e)
    	{
    	   e.printStackTrace();	
    	}finally
		{
			if(rs!=null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
        return data;	
    }
}
