package com.hjsj.hrms.businessobject.kq.machine;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddressList;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class ExcelCard {
    
	private Connection conn;
	public ExcelCard(){}
	public ExcelCard(Connection conn)
	{
		this.conn=conn;
	}
	/**
	 * 建立Excel
	 * @param columns
	 * @param where
	 * @param filedList
	 * @return
	 */
    public String creatExcel(String columns,String where,ArrayList filedList,UserView userView)
    {
    	// 59250   统一命名为： 登陆用户_相应信息
    	String excel_filename = userView.getUserName() + "_" + ResourceFactory.getProperty("kq.init.ypsk").trim()+".xls";
		FileOutputStream fileOut = null;
		HSSFWorkbook workbook = new HSSFWorkbook();
		try
		{
			HSSFSheet sheet = workbook.createSheet();

			HSSFFont font2 = workbook.createFont();
			font2.setFontHeightInPoints((short) 11);
			HSSFCellStyle style2 = workbook.createCellStyle();
			style2.setFont(font2);
			style2.setAlignment(HorizontalAlignment.CENTER);
			style2.setVerticalAlignment(VerticalAlignment.CENTER);
			style2.setWrapText(true);
			style2.setBorderBottom(BorderStyle.THIN);
			style2.setBorderLeft(BorderStyle.THIN);
			style2.setBorderRight(BorderStyle.THIN);
			style2.setBorderTop(BorderStyle.THIN);
			style2.setBottomBorderColor((short) 8);
			style2.setLeftBorderColor((short) 8);
			style2.setRightBorderColor((short) 8);
			style2.setTopBorderColor((short) 8);
			style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			style2.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

			HSSFRow row=null;
			HSSFCell csCell=null;
			HSSFComment comm = null;
			HSSFPatriarch patr = sheet.createDrawingPatriarch();

			ArrayList list=getTableField(columns,filedList);
			String[][] excelData=getExcelDataInfo(columns,where,list);
			ArrayList fieldList1=(ArrayList)list.get(0);
			//short n=executeTitel("刷卡数据",excelData,sheet,workbook);  //写上表头 和 标题
			//写表头
			row = sheet.createRow(0);
			KqParameter para = new KqParameter(userView, "", this.conn);
			String g_no = para.getG_no();
			for(int j=0;j<excelData[0].length;j++)
			{
				csCell =row.createCell(j);
				csCell.setCellStyle(style2);
				String columnsRequired = ",card_no,inout_flag,sp_flag,oper_cause,"+g_no+",";
				String fieldId = (String) fieldList1.get(j);
				//加标注
				comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1,(short) (j), 0, (short) (j + 1), 1));
				comm.setString(new HSSFRichTextString(fieldId.toString().toLowerCase()));
				csCell.setCellComment(comm);
				if("work_date".equalsIgnoreCase(fieldId)){
					csCell.setCellValue(excelData[0][j]+"(必填)\n(格式:2016-01-01)");
					sheet.setColumnWidth(j, 18*255);
				}else if("work_time".equalsIgnoreCase(fieldId)){
					csCell.setCellValue(excelData[0][j]+"(必填)\n(格式:00:00)");
					sheet.setColumnWidth(j, 13*255);
				}else if("oper_time".equalsIgnoreCase(fieldId)){
					csCell.setCellValue(excelData[0][j]+"\n(格式:2016-01-01 00:00)");
					sheet.setColumnWidth(j, 20*255);
				}else if(columnsRequired.contains(","+fieldId+",")){
					csCell.setCellValue(excelData[0][j]+"(必填)");
					sheet.setColumnWidth(j, 12*255);
				}else{
					sheet.setColumnWidth(j, 12*255);
					csCell.setCellValue(excelData[0][j]);
				}
				//增加对应的下拉列表
				if("sp_flag".equalsIgnoreCase((String) fieldList1.get(j))){
					sp_flagSelect((short)(j), sheet);
				}
				if("inout_flag".equalsIgnoreCase((String) fieldList1.get(j))){
					inout_flagSelect((short)(j), sheet);
				}
				if("iscommon".equalsIgnoreCase((String) fieldList1.get(j))){
					iscommonSelect((short)(j), sheet);
				}
			}
			//插入数据
			for(int i=1;i<excelData.length;i++)
			{
				row = sheet.createRow(i);
				for(int j=0;j<excelData[i].length;j++)
				{
					csCell =row.createCell(j);
					csCell.setCellValue(excelData[i][j]);
				}

			}
			HSSFCellStyle cellStyle2 = workbook.createCellStyle();
			HSSFDataFormat format = workbook.createDataFormat();
			cellStyle2.setDataFormat(format.getFormat("@"));
			//设置工号列为文本格式
			for(int q =0;q<=500;q++){
				row = sheet.createRow(q+excelData.length);
				for (int i = 0; i < excelData[0].length; i++)
				{
					csCell = row.createCell(i);
					csCell.setCellStyle(cellStyle2);
				}
			}

			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+excel_filename);
			workbook.write(fileOut);
		}catch(Exception e)
		{
			e.printStackTrace();
		} finally{
			PubFunc.closeResource(workbook);
			PubFunc.closeResource(fileOut);
		}
		
    	return excel_filename;
    }  
    private String[][] getExcelDataInfo(String columns,String where,ArrayList list)
    {
    	ArrayList fieldList=(ArrayList)list.get(0);
		HashMap   fieldHzMap=(HashMap)list.get(1);
		HashMap   fieldTypeMap=(HashMap)list.get(2);
		HashMap   fieldCodeMap=(HashMap)list.get(3);
    	StringBuffer sql=new StringBuffer();
    	sql.append("select count(*) counts");
    	sql.append(" "+where);    	
    	int rs_num=0;
    	String[][] data=null;
    	int field_num=fieldList.size();
    	RowSet rs=null;
		/* -----------显示部门层数-------------------------------------------------- */
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.conn);       //
	    String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);         //
	    if(uplevel==null||uplevel.length()==0)                                     //
        {
            uplevel="0";                                                       //
        }
	    /* ------------显示部门层数------------------------------------------------- */
	    int iuplevel = Integer.parseInt(uplevel);
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
    		sql.append(" order by nbase,b0110,e0122,a0100,work_date");
    		rs=dao.search(sql.toString());
    		int j=1;
    		while(rs.next())
    		{
    			for(int i=0;i<fieldList.size();i++)
    			{
    				String fieldName=(String)fieldList.get(i);
					
						if(rs.getString(fieldName)!=null)
						{
							String itemType = (String)fieldTypeMap.get(fieldName);
							String codesetid = (String)fieldCodeMap.get(fieldName);
							String fieldValue = rs.getString(fieldName)==null?"":rs.getString(fieldName);
							if("A".equals(itemType)&& "0".equals(codesetid))
							{
								if("inout_flag".equalsIgnoreCase(fieldName))
								{
									String inout = rs.getString(fieldName);
									if("1".equals(inout))
									{
										data[j][i]="进";
									}else if("-1".equals(inout))
									{
										data[j][i]="出";
									}else
									{
										data[j][i]="不限";
									}
								}else if ("iscommon".equalsIgnoreCase(fieldName))
								{
									String inout = rs.getString(fieldName);
									if("0".equals(inout))
									{
										data[j][i]="否";
									}else 
									{
										data[j][i]="是";
									}
								}
								else
								{
									data[j][i]=fieldValue;
								}
								
							}else if("A".equals(itemType)&&!"0".equals(codesetid))
							{
								if("UM".equals((String)fieldCodeMap.get(fieldName))){
									data[j][i]=AdminCode.getCode(codesetid,fieldValue,iuplevel)!=null?AdminCode.getCode(codesetid,fieldValue,iuplevel).getCodename():"";
								} else {
									data[j][i]=AdminCode.getCode(codesetid,fieldValue)!=null?AdminCode.getCode(codesetid,fieldValue).getCodename():"";
								}
							}
							else if("N".equals(itemType))
						    {
									if(rs.getFloat(fieldName)==0)
									{
										data[j][i]=" ";
									}
									else {
                                        data[j][i]=fieldValue;
                                    }
							}
							else if("M".equals(itemType))
							{
								data[j][i]=Sql_switcher.readMemo(rs,fieldName);
							}else if("D".equals(itemType))
							{
//								if(rs.getDate(fieldName)!=null)
//								  data[j][i]=DateUtils.format(rs.getDate(fieldName),"yyyy.MM.dd HH:mm");
								if(rs.getString(fieldName)!=null)
								{
									String date12 = fieldValue;
									data[j][i]= date12;
								}
							}
						}else
						{
							data[j][i]="";
							//为null时正常考勤为是
							if ("iscommon".equalsIgnoreCase(fieldName))
							{
									data[j][i]="是";
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
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
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
     * @param columns 
     * @param a_fieldList
     * @return
     */
    public ArrayList getTableField(String columns, ArrayList a_fieldList)
	{
		ArrayList list=new ArrayList();
		ArrayList fieldList=new ArrayList();
		HashMap   fieldHzMap=new HashMap();
		HashMap   fieldTypeMap=new HashMap();
		HashMap   fieldCodeMap=new HashMap();

		try
		{
			//不添加‘序号’列
//			fieldList.add("recidx");
//			fieldHzMap.put("recidx",ResourceFactory.getProperty("recidx.label"));
//			fieldTypeMap.put("recidex","A");
//			fieldCodeMap.put("recidx","0");
			
			String field_name="";
			for(int i=0;i<a_fieldList.size();i++)
			{
				FieldItem item=(FieldItem)a_fieldList.get(i);				
				if("a0100".equalsIgnoreCase(item.getItemid())|| "status".equalsIgnoreCase(item.getItemid())||columns.indexOf(item.getItemid()) == -1) {
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
 
    //下拉列表
    private void sp_flagSelect(short cells, HSSFSheet sheet) {
        int x = 0;
        ArrayList<String> list = new ArrayList<String>();
        list.add("已报批");
        list.add("批准");
        list.add("驳回");
        HSSFRow row = null;
        HSSFCell csCell = null;
        short n = 999;// 
        short m = (short) (n + 1);// 去掉说明行
        short s = (short) (m + 1);
        try {
            x ++;
            row = sheet.getRow(m);
            if(row == null) {
                row = sheet.createRow(m);
            }
            csCell = row.createCell(210);  //在HC列生成数据
            csCell.setCellValue("");// 考勤班次号
            m++;
            
            for(int i=0;i<list.size();i++){
            	String value = list.get(i);
            	x ++;
                row = sheet.getRow(m);
                if(row == null) {
                    row = sheet.createRow(m);
                }
                csCell = row.createCell(210);  //在HC列生成数据
                csCell.setCellValue(value);// 考勤班次号
                m++;
            }
            sheet.setColumnHidden(210, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String strFormula = "$HC$" + s + ":$HC$" + (1050) + ""; // 表示HC列1001-1050行作为下拉列表来源数据
        CellRangeAddressList addressList = new CellRangeAddressList(1, n, cells, cells);
        DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(strFormula);
        HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
        dataValidation.setSuppressDropDownArrow(false);
        sheet.addValidationData(dataValidation);
    }
    private void inout_flagSelect(short cells, HSSFSheet sheet) {
        int x = 0;
        ArrayList<String> list = new ArrayList<String>();
        list.add("不限");
        list.add("进");
        list.add("出");
        HSSFRow row = null;
        HSSFCell csCell = null;
        short n = 999;// 
        short m = (short) (n + 1);// 去掉说明行
        short s = (short) (m + 1);
        try {
            x ++;
            row = sheet.getRow(m);
            if(row == null) {
                row = sheet.createRow(m);
            }
            csCell = row.createCell(211);  //在HD列生成数据
            csCell.setCellValue("");// 考勤班次号
            m++;
            
            for(int i=0;i<list.size();i++){
            	String value = list.get(i);
            	x ++;
                row = sheet.getRow(m);
                if(row == null) {
                    row = sheet.createRow(m);
                }
                csCell = row.createCell(211);  //在HD列生成数据
                csCell.setCellValue(value);// 考勤班次号
                m++;
            }
            sheet.setColumnHidden(211, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String strFormula = "$HD$" + s + ":$HD$" + (1050) + ""; // 表示HD列1001-1050行作为下拉列表来源数据
        CellRangeAddressList addressList = new CellRangeAddressList(1, n, cells, cells);
        DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(strFormula);
        HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
        dataValidation.setSuppressDropDownArrow(false);
        sheet.addValidationData(dataValidation);
    }
    private void iscommonSelect(short cells, HSSFSheet sheet) {
        int x = 0;
        ArrayList<String> list = new ArrayList<String>();
        list.add("是");
        list.add("否");
        HSSFRow row = null;
        HSSFCell csCell = null;
        short n = 999;// 
        short m = (short) (n + 1);// 去掉说明行
        short s = (short) (m + 1);
        try {
            x ++;
            row = sheet.getRow(m);
            if(row == null) {
                row = sheet.createRow(m);
            }
            csCell = row.createCell(212);  //在HE列生成数据
            csCell.setCellValue("");// 考勤班次号
            m++;
            
            for(int i=0;i<list.size();i++){
            	String value = list.get(i);
            	x ++;
                row = sheet.getRow(m);
                if(row == null) {
                    row = sheet.createRow(m);
                }
                csCell = row.createCell(212);  //在HE列生成数据
                csCell.setCellValue(value);// 考勤班次号
                m++;
            }
            sheet.setColumnHidden(212, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String strFormula = "$HE$" + s + ":$HE$" + (1050) + ""; // 表示HE列1001-1050行作为下拉列表来源数据
        CellRangeAddressList addressList = new CellRangeAddressList(1, n, cells, cells);
        DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(strFormula);
        HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
        dataValidation.setSuppressDropDownArrow(false);
        sheet.addValidationData(dataValidation);
    }
}


