package com.hjsj.hrms.businessobject.hire;

import com.hjsj.hrms.businessobject.hire.jp_contest.param.EngageParamXML;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
/**
 * 
 *<p>Title:JingPingPosBo.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 19, 2007</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class JingPingPosBo {

	private Connection conn=null;
//	private ArrayList fieldlist=new ArrayList();
	
	public JingPingPosBo(Connection con) {
		this.conn=con;
	}
	
	/**
	 * 取得竞聘职位表结构中所有指标列表
	 * @return
	 */
	public ArrayList getFieldlist() 
	{
		ArrayList list = DataDictionary.getFieldList("z07",Constant.USED_FIELD_SET);
		ArrayList fieldlist = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			FieldItem item = (FieldItem) list.get(i);
			if(!"1".equalsIgnoreCase(item.getState())) {
                continue;
            }
			Field field = (Field) item.cloneField();
			if("Z0700".equalsIgnoreCase(field.getName()))
			{
				field.setDatatype(DataType.INT);
				field.setLength(4);
				field.setFormat("####");
				field.setVisible(false);
				fieldlist.add(0,field);
			}else
			{
				if("Z0701".equalsIgnoreCase(field.getName()))
				{
					field.setLabel(ResourceFactory.getProperty("hire.jp.pos.position"));				
				}
//				if(field.getName().equalsIgnoreCase("Z0706"))
//				{
//					field.setCodesetid("UM");			
//				}
				/**状态指标为只读*/
				if("Z0713".equalsIgnoreCase(field.getName()))
				{
					field.setReadonly(true);
					field.setCodesetid("23");
				}
				field.setSortable(true);			
				if(!item.isVisible()) {
                    field.setVisible(false);
                }
				fieldlist.add(field);
			}	

		}
		Field field = new Field("b","岗位说明书");
		field.setAlign("center");
		field.setReadonly(true);
		fieldlist.add(field);
		return fieldlist;
	}
	
	public ArrayList getApplylist() 
	{
		ArrayList fieldlist = new ArrayList();
		EngageParamXML epXML = new EngageParamXML(this.conn);
		String app_view = epXML.getTextValue(EngageParamXML.APP_VIEW);
		if(app_view.indexOf("z0701")==-1 && app_view.indexOf("Z0701")==-1)
		{
			if(app_view==null || "".equals(app_view))
			{
				app_view = "z0701";
			}else
			{
				app_view = app_view+",z0701";
			}
		}else
		{
			app_view=app_view;
		}
		String[] apply = app_view.split(",");
		for (int i = 0; i < apply.length; i++) 
		{
			String applyfield = apply[i];
			FieldItem fi = DataDictionary.getFieldItem(applyfield);
			fi.setItemid(fi.getItemid().toLowerCase());
//			Field field = fi.cloneField();
			fieldlist.add(fi);
		}
//		ArrayList list = DataDictionary.getFieldList("z07",Constant.USED_FIELD_SET);
//		ArrayList fieldlist = new ArrayList();
//		for (int i = 0; i < list.size(); i++) {
//			FieldItem item = (FieldItem) list.get(i);
//			Field field = (Field) item.cloneField();
//			if(field.getName().equalsIgnoreCase("Z0700"))
//			{
//				field.setVisible(false);
//				fieldlist.add(0,field);
//			}else
//			{
//				if(field.getName().equalsIgnoreCase("Z0701"))
//				{
//					field.setLabel(ResourceFactory.getProperty("hire.jp.pos.position"));
////					field.setCodesetid("@k");
//				}
////				if(field.getName().equalsIgnoreCase("Z0706"))
////				{
////					field.setCodesetid("UM");
////				}	
//				/**状态指标为只读*/
//				if(field.getName().equalsIgnoreCase("Z0713"))
//				{
//					field.setReadonly(true);
//					field.setCodesetid("23");
//				}
//				field.setSortable(true);			
//				if(!item.isVisible())
//					field.setVisible(false);
//				fieldlist.add(field);
//			}	
//
//		}
		return fieldlist;
	}
	
	public String getSelectStr()
	{
		String retstr = "";
		EngageParamXML epXML = new EngageParamXML(this.conn);
		String app_view = epXML.getTextValue(EngageParamXML.APP_VIEW);
		if(app_view.indexOf("z0701")==-1 && app_view.indexOf("Z0701")==-1)
		{
			if(app_view==null || "".equals(app_view))
			{
				retstr = "z0701";
			}else
			{
				retstr = app_view+",z0701";
			}
		}else
		{
			retstr=app_view;
		}
		return retstr;
	}
	/**
	 * 数据导出到Excel
	 * @param tablename
	 * @param where
	 * @return
	 */
	public  String exportJPExcel(String tablename,String where)
	{
		ContentDAO dao = new ContentDAO(this.conn);
		String outname="ExportJingPing.xls";
		String filename=outname;
		Calendar cal=Calendar.getInstance(); 
		try{
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet();
			HSSFRow row=null;
			HSSFCell csCell=null;
			HSSFCellStyle style = workbook.createCellStyle();
			style.setAlignment(HorizontalAlignment.RIGHT); 
		
		    ArrayList itemlist = this.getFieldlist();
			StringBuffer selectsb = new StringBuffer();
//			 第一行 标题
//			row = sheet.createRow((short)0);
			row = sheet.getRow((short)0);
			if(row==null) {
                row = sheet.createRow((short)0);
            }

			row.setHeightInPoints(22); 
//			sheet.addMergedRegion(new Region(0,(short)0,0,(short)4)); 
//			csCell =row.createCell((short)(0));
//			csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
//			csCell.setCellValue("拟竞岗位一览表");
//			csCell.setCellStyle(this.setTitleStyle(workbook));
//			 第二行 时间
//			row = sheet.createRow((short)1);
			row = sheet.getRow((short)1);
			if(row==null) {
                row = sheet.createRow((short)1);
            }

			row.setHeightInPoints(15); 
//			sheet.addMergedRegion(new Region(1,(short)0,1,(short)4));
//			csCell =row.createCell((short)(0));
//			csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
//			int month = cal.get(Calendar.MONTH)+1;
//			csCell.setCellValue(cal.get(Calendar.YEAR)+"年"+month+"月"+cal.get(Calendar.DATE)+"日");
//			csCell.setCellStyle(this.setDateStyle(workbook));
//			 第三行 表头			
//			row = sheet.createRow((short)2);
			row = sheet.getRow((short)2);
			if(row==null) {
                row = sheet.createRow((short)2);
            }

			row.setHeightInPoints(17); 
			csCell =row.createCell((short)(0));
//			csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
			csCell.setCellValue("序号");
//			csCell.setCellStyle(this.setFirstRowBeginStyle(workbook));
			csCell.setCellStyle(this.setFirstCommonStyle(workbook));
			int t=1;
			//  形成SQL语句
			for(int i=0;i<itemlist.size();i++){
				Field fi = (Field)itemlist.get(i);
				String itemid =  (String)fi.getName();
				if("b".equalsIgnoreCase(itemid)|| "Z0700".equalsIgnoreCase(itemid))
				{
					continue;
				}
				String itemdesc = "";
				if("Z0700".equalsIgnoreCase(itemid))
				{
					itemdesc = "岗位名称";
				}else if("Z0709".equalsIgnoreCase(itemid))
				{
					itemdesc = (String)fi.getLabel();
//					itemdesc = "岗位职责及任职条件";
				}else
				{
					itemdesc = (String)fi.getLabel();
				}				
				int typenum = fi.getDatatype();
				// 形成表头
//				row = sheet.createRow((short)2);
				row.setHeightInPoints(17); 
				csCell =row.createCell((short)(t));
//				csCell.setEncoding(HSSFCell.ENCODING_UTF_16);					
				csCell.setCellValue(itemdesc);
//				if(t>0)
//					csCell.setCellStyle(this.setFirstRowStyle(workbook));
				csCell.setCellStyle(this.setFirstCommonStyle(workbook));
				String itemstr = "";
				//itemstr = getitemstr(itemid);
				selectsb.append(","+itemid);
				t++;
			}
			row = sheet.getRow(0);
			csCell =row.createCell((short)((t-1)/2));
//			csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
			csCell.setCellValue("拟竞岗位一览表");
			csCell.setCellStyle(this.setTitleStyle(workbook));
			
			row = sheet.getRow(1);
			csCell =row.createCell((short)(0));
//			csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
			csCell.setCellValue("编号:");
			
			csCell =row.createCell((short)((t-1)/2));
//			csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
			csCell.setCellValue("管理处名称:");
			
			csCell =row.createCell((short)(t-1));
//			csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
			int month = cal.get(Calendar.MONTH)+1;
			csCell.setCellValue(cal.get(Calendar.YEAR)+"年"+month+"月"+cal.get(Calendar.DATE)+"日");
//			if(t>1)
//			{
//				row = sheet.getRow(2);
//				csCell = row.getCell( row.getLastCellNum());
//				csCell.setCellStyle(this.setFirstRowEndStyle(workbook));
//			}
			
		
			
			String sql = this.getExportSQL(tablename,selectsb.substring(1).toString(),where);
			//System.out.println(sql);
			RowSet rset=dao.search(sql);
			int n=3;
			int x = 0;
			
			while(rset.next()){
				int m = 1;
//				row = sheet.createRow((short)n);
				row = sheet.getRow((short)n);
				if(row==null) {
                    row = sheet.createRow((short)n);
                }

				csCell =row.createCell((short)(0));
//				csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
				csCell.setCellValue(n-2);
//				csCell.setCellStyle(this.setDataBeginStyle(workbook));
				csCell.setCellStyle(this.setCommonDataStyle(workbook));
				for(int i=0;i<itemlist.size();i++){
					Field fielditem = (Field)itemlist.get(i);
					if("b".equalsIgnoreCase(fielditem.getName())|| "Z0700".equalsIgnoreCase(fielditem.getName()))
					{
						continue;
					}	
					
					ResultSetMetaData rsetmd=rset.getMetaData();
					String fieldesc = getColumStr(rset,rsetmd,fielditem.getName());
					fieldesc = fieldesc!=null?fieldesc:"";
					String desc = "";
					if(fielditem.isCode()){
						desc = AdminCode.getCodeName("UN", fieldesc);
						if(desc.length()<1){
							desc = AdminCode.getCodeName("UM", fieldesc);
							if(desc.length()<1){
								desc = AdminCode.getCodeName("@K", fieldesc);
								if(desc.length()<1){
									desc = AdminCode.getCodeName(fielditem.getCodesetid(),fieldesc);
								}
							}
						}
					}else{
						int type = fielditem.getDatatype();						
						if(type==6){
							if(fieldesc!=null&&fieldesc.trim().length()>0){
								desc = getFloat(fieldesc,fielditem.getDecimalDigits());
							}else{
								desc = "";
							}
						}else if(type==4){						
							if(fieldesc!=null&&fieldesc.trim().length()>0){
								desc = Math.round(Float.parseFloat(fieldesc))+"";
								if("0".equals(desc)){
									desc = "";
								}
							}else{
								desc = "";
							}
						}else{
							desc = fieldesc;
						}
					}
					csCell =row.createCell((short)(m));
//					csCell.setEncoding(HSSFCell.ENCODING_UTF_16);
					csCell.setCellValue(desc);
//					csCell.setCellStyle(this.setDataStyle(workbook));
					csCell.setCellStyle(this.setCommonDataStyle(workbook));
					x=m;
					m++;
				}
//				row = sheet.getRow(n);
//				csCell = row.getCell( row.getLastCellNum());
//				csCell.setCellStyle(this.setDataEndStyle(workbook));
				n++;
			}
//			if(x>0)
//			{
//				row = sheet.getRow(n-1);
//				for(int i=0;i<=x;i++)
//				{
//					csCell = row.getCell((short)(i));
//					if(i!=x)
//					{
//						if(i==0)
//						{
//							csCell.setCellStyle(this.setLastDataBeginStyle(workbook));	
//						}else
//							csCell.setCellStyle(this.setLastDataStyle(workbook));	
//					}								
//					else
//						csCell.setCellStyle(this.setLastDataEndStyle(workbook));
//					
//				}
//			}
			
			
			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+outname);
			workbook.write(fileOut);
			fileOut.close();	
			sheet=null;
			workbook=null;	
		}catch(Exception e){
			e.printStackTrace();
		}
		return filename;
	
	}
	
	
	public String getColumStr(RowSet rset,ResultSetMetaData rsetmd,String str) throws SQLException{
		int j=rset.findColumn(str);
		String temp=null;
		switch(rsetmd.getColumnType(j)){
		
		case Types.DATE:
		        temp=PubFunc.FormatDate(rset.getDate(j));
		        break;			
		case Types.TIMESTAMP:
			    temp=PubFunc.FormatDate(rset.getDate(j),"yyyy-MM-dd hh:mm:ss");
			    if(temp.indexOf("12:00:00")!=-1) {
                    temp=PubFunc.FormatDate(rset.getDate(j));
                }
				break;
		case Types.CLOB:
			    temp=Sql_switcher.readMemo(rset,rsetmd.getColumnName(j));	                    	
				break;
		case Types.BLOB:
				temp="二进制文件";	                    	
				break;		
		case Types.NUMERIC:
			  int preci=rsetmd.getScale(j);
			  temp=String.valueOf(rset.getDouble(j));			  
			  temp=PubFunc.DoFormatDecimal(temp, preci);
			  break;
		default:		
				temp=rset.getString(j);
				break;
		}
		return temp;
	}
	/**
	 * 过滤查询的字段
	 * @param itemid
	 * @return
	 */
	public String getitemstr(String itemid)
	{
		String itemstr= "";
		if(!"z0700".equalsIgnoreCase(itemid) )
		{
			itemstr=itemid;
		}			
		
		return itemstr;
	}
	
	public String getFloat(String desc,int decimalwidth){
		String fielddesc = "";
		StringBuffer temp= new StringBuffer("#0.");
		for(int i=0;i<decimalwidth;i++){
			temp.append("0");
		}
		
		DecimalFormat format = new DecimalFormat(temp.toString());
		double a=0;
		if(desc!=null&&desc.trim().length()>0){
			a = Double.parseDouble(desc);
			fielddesc = format.format(a);
		}
		
		return fielddesc;
	}
	
	public String getExportSQL(String tablename,String select,String where)
	{
		select = this.getExportField(select);
		StringBuffer sql = new StringBuffer();
		sql.append(" select "+select);
		sql.append(" from "+tablename);
		sql.append(where);
//		System.out.println(sql.toString());
		return sql.toString();
	}
	
	public String getExportField(String select)
	{
		StringBuffer retstr = new StringBuffer();
		if(!(select==null || "".equals(select)))
		{
			String[] arr = select.split(",");
			for(int i=0;i<arr.length;i++)
			{
				retstr.append(","+arr[i]);
			}
		}
		return retstr.substring(1).toString().toUpperCase();
	}
	
	/**
	 * 设置单元格格式
	 * @param workbook
	 * @return
	 */
	public HSSFCellStyle setTitleStyle(HSSFWorkbook workbook) 
	{
		 // 先定义一个字体对象
        HSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        // font.setColor(HSSFFont.COLOR_RED);
        font.setFontHeightInPoints((short) 13); // 字体大小
        font.setBold(true); // 加粗

        // 定义表头单元格格式
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font); // 单元格字体
        style.setAlignment(HorizontalAlignment.CENTER); // 水平对齐方式
        style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直对齐方式
        return style;

	}
	
	public HSSFCellStyle setDateStyle(HSSFWorkbook workbook) 
	{
		 // 先定义一个字体对象
        HSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        font.setFontHeightInPoints((short) 10); // 字体大小
        // 定义表头单元格格式
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font); // 单元格字体
        style.setAlignment(HorizontalAlignment.CENTER); // 水平对齐方式
        style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直对齐方式
        return style;

	}
	
	public HSSFCellStyle setFirstRowStyle(HSSFWorkbook workbook) 
	{
		 // 先定义一个字体对象
        HSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        // font.setColor(HSSFFont.COLOR_RED);
        font.setFontHeightInPoints((short) 10); // 字体大小
        font.setBold(true); // 加粗

        // 定义表头单元格格式
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font); // 单元格字体
        style.setAlignment(HorizontalAlignment.CENTER); // 水平对齐方式
        style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直对齐方式
        style.setBorderTop(BorderStyle.THICK);
        style.setBorderBottom(BorderStyle.THIN); // 表格细边框
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);

        return style;

	}
	
	public HSSFCellStyle setFirstRowBeginStyle(HSSFWorkbook workbook) 
	{
		 // 先定义一个字体对象
        HSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        // font.setColor(HSSFFont.COLOR_RED);
        font.setFontHeightInPoints((short) 10); // 字体大小
        font.setBold(true); // 加粗

        // 定义表头单元格格式
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font); // 单元格字体
        style.setAlignment(HorizontalAlignment.CENTER); // 水平对齐方式
        style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直对齐方式
        style.setBorderTop(BorderStyle.THICK);
        style.setBorderBottom(BorderStyle.THIN); // 表格细边框
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THICK);

        return style;

	}
	
	public HSSFCellStyle setFirstRowEndStyle(HSSFWorkbook workbook) 
	{
		 // 先定义一个字体对象
        HSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        // font.setColor(HSSFFont.COLOR_RED);
        font.setFontHeightInPoints((short) 10); // 字体大小
        font.setBold(true); // 加粗

        // 定义表头单元格格式
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font); // 单元格字体
        style.setAlignment(HorizontalAlignment.CENTER); // 水平对齐方式
        style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直对齐方式
        style.setBorderTop(BorderStyle.THICK);
        style.setBorderBottom(BorderStyle.THIN); // 表格细边框
        style.setBorderRight(BorderStyle.THICK);
        style.setBorderLeft(BorderStyle.THIN);

        return style;

	}
	
	public HSSFCellStyle setDataStyle(HSSFWorkbook workbook) 
	{
		 // 先定义一个字体对象
        HSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        // 定义表头单元格格式
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font); // 单元格字体
        style.setAlignment(HorizontalAlignment.RIGHT); // 水平对齐方式
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN); // 表格细边框
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;

	}
	
	public HSSFCellStyle setDataBeginStyle(HSSFWorkbook workbook) 
	{
		 // 先定义一个字体对象
        HSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        // 定义表头单元格格式
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font); // 单元格字体
        style.setAlignment(HorizontalAlignment.RIGHT); // 水平对齐方式
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN); // 表格细边框
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THICK);
        return style;

	}
	
	public HSSFCellStyle setDataEndStyle(HSSFWorkbook workbook) 
	{
		 // 先定义一个字体对象
        HSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        // 定义表头单元格格式
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font); // 单元格字体
        style.setAlignment(HorizontalAlignment.RIGHT); // 水平对齐方式
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN); // 表格细边框
        style.setBorderRight(BorderStyle.THICK);
        style.setBorderLeft(BorderStyle.THIN);
        return style;

	}
	
	public HSSFCellStyle setLastDataBeginStyle(HSSFWorkbook workbook) 
	{
		 // 先定义一个字体对象
        HSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        // 定义表头单元格格式
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font); // 单元格字体
        style.setAlignment(HorizontalAlignment.RIGHT); // 水平对齐方式
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THICK); // 表格细边框
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THICK);
        return style;

	}		
	
	public HSSFCellStyle setLastDataEndStyle(HSSFWorkbook workbook) 
	{
		 // 先定义一个字体对象
        HSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        // 定义表头单元格格式
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font); // 单元格字体
        style.setAlignment(HorizontalAlignment.RIGHT); // 水平对齐方式
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THICK); // 表格细边框
        style.setBorderRight(BorderStyle.THICK);
        style.setBorderLeft(BorderStyle.THIN);
        return style;

	}
	
	public HSSFCellStyle setLastDataStyle(HSSFWorkbook workbook) 
	{
		 // 先定义一个字体对象
        HSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        // 定义表头单元格格式
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font); // 单元格字体
        style.setAlignment(HorizontalAlignment.RIGHT); // 水平对齐方式
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THICK); // 表格细边框
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;

	}
	
	public HSSFCellStyle setCommonDataStyle(HSSFWorkbook workbook) 
	{
		 // 先定义一个字体对象
        HSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        // 定义表头单元格格式
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font); // 单元格字体
        style.setAlignment(HorizontalAlignment.RIGHT); // 水平对齐方式
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN); // 表格细边框
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;

	}
	
	public HSSFCellStyle setFirstCommonStyle(HSSFWorkbook workbook) 
	{
		 // 先定义一个字体对象
        HSSFFont font = workbook.createFont();
        font.setFontName("黑体");
        font.setFontHeightInPoints((short) 10); // 字体大小
        font.setBold(true); // 加粗
        // 定义表头单元格格式
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font); // 单元格字体
        style.setAlignment(HorizontalAlignment.CENTER); // 水平对齐方式
        style.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直对齐方式
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN); // 表格细边框
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;

	}
	
	

}
