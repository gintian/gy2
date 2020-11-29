package com.hjsj.hrms.businessobject.train.attendance;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.OperateDate;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * <p>
 * Title:TrainAddBo.java
 * </p>
 * <p>
 * Description:培训考勤
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2011-03-09 14:40:00
 * </p>
 * 
 * @author LiWeichao
 * @version 5.0
 */
public class ExportExcelBo {
	private String classplan;
	//判断是否是出勤汇总页面进，flag="1"时表示是出勤汇总页面。
	private String flag = "0";
	public ExportExcelBo(){}
	public ExportExcelBo(String classplan){
		this.classplan=classplan;
	}
	
	/**
	 * 导出Excel
	 * @param title 标题
	 * @param columns 显示列
	 * @param colStr 列别名
	 * @param sql 
	 * @return filename
	 * @throws Exception 
	 */
	public String ExportExcel(Connection conn,String title,String columns,String colStr,String sql, UserView userView) throws Exception{
		
	    ArrayList fieldItems = DataDictionary.getFieldList("R47", Constant.USED_FIELD_SET);
	    
	    String filename = userView.getUserName() + "_train.xls";
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(title);
		int num=0;
		HSSFRow row = sheet.createRow((short) num);
		String[] cols=colStr.split(",");
		headerAdd(row, sheet, wb, title, cols.length);// 设置头
		num++;
		HSSFCell cell = null;
		HSSFRichTextString rts=null;
		HSSFCellStyle style=createStyle(wb,1);
		row = sheet.createRow(num);
		for (int i = 0; i < cols.length; i++) {
			cell = row.createCell(i);
			rts=new HSSFRichTextString("　"+cols[i]+"　");
			cell.setCellValue(rts);
			cell.setCellStyle(style);
			if(i==cols.length-1){
				cell = row.createCell(cols.length);
				cell.setCellValue("");
				style = wb.createCellStyle();
				style.setBorderLeft(BorderStyle.THIN);
				cell.setCellStyle(style);
			}
		}
		ConstantXml constantbo = new ConstantXml(conn, "TR_PARAM");
        String card_no = constantbo.getTextValue("/param/attendance/card_no");// 获得设置的卡号字段名称
        FieldItem cardItem = DataDictionary.getFieldItem(card_no, "A01");
        if(cardItem == null || !"1".equals(cardItem.getUseflag()) )
        {
            card_no = null;
        }
		SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
		ContentDAO dao=new ContentDAO(conn);
		RowSet rs = dao.search(sql);
		while(rs.next()){
		    RowSet frowset;
			num++;
			row = sheet.createRow(num);
			style = createStyle(wb,0);
			String[] c=columns.split(",");
			for (int i = 0; i < c.length; i++) {
			    cell = row.createCell(i);
                Object obj = null;
                //判断不是出勤汇总页面或者当前列的字段不等于培训卡号的字段
                if(!"1".equalsIgnoreCase(flag) || (StringUtils.isNotEmpty(card_no) && !card_no.equalsIgnoreCase(c[i])) )
                {
                    obj = rs.getObject(c[i]);
                }
                
                obj = obj == null ? "" : obj;
                //判断是否有设置培训卡号，同时是出勤汇总页面
                if(StringUtils.isNotEmpty(card_no) && card_no.equalsIgnoreCase(c[i]) && "1".equalsIgnoreCase(flag) )
                {
                    String nbase = rs.getString("nbase");
                    String a0100 = rs.getString("a0100");
                    StringBuffer cardBuf = new StringBuffer();
                    cardBuf.append("select "); 
                    cardBuf.append(card_no);
                    cardBuf.append(" from ");
                    cardBuf.append(nbase);
                    cardBuf.append("A01 ");
                    cardBuf.append("where A0100 =");
                    cardBuf.append(a0100);
                    frowset = dao.search(cardBuf.toString());
                    
                    if (frowset.next()) 
                    {
                      String cardId = frowset.getString(card_no);
                      rts = new HSSFRichTextString("　"+cardId+"　");
                      cell.setCellValue(rts);
                      cell.setCellStyle(style);
                      continue;
                    }
                }
				if("r4101".equalsIgnoreCase(c[i])){
					rts = new HSSFRichTextString("　"+setR4101(conn,obj)+"　");
					cell.setCellValue(rts);
					cell.setCellStyle(style);
					continue;
				} else if("r4103".equalsIgnoreCase(c[i])){
					rts=new HSSFRichTextString("　"+setR3130(conn,obj)+"　");
					cell.setCellValue(rts);
					cell.setCellStyle(style);
					continue;
				}else if("b0110".equalsIgnoreCase(c[i])){
					rts = new HSSFRichTextString("　"+AdminCode.getCodeName("UN",obj.toString())+"　");
					cell.setCellValue(rts);
					cell.setCellStyle(style);
					continue;
				}else if("e0122".equalsIgnoreCase(c[i])){
					rts = new HSSFRichTextString("　"+AdminCode.getCodeName("UM",obj.toString())+"　");
					cell.setCellValue(rts);
					cell.setCellStyle(style);
					continue;
				}else if("e01a1".equalsIgnoreCase(c[i])){
					rts = new HSSFRichTextString("　"+AdminCode.getCodeName("@K",obj.toString())+"　");
					cell.setCellValue(rts);
					cell.setCellStyle(style);
					continue;
				}else if("card_time".equalsIgnoreCase(c[i])){
					String card_date = OperateDate.dateToStr(DateUtils.getDate(obj.toString(), "yyyy-MM-dd HH:mm:ss"), "yyyy-MM-dd");
					rts = new HSSFRichTextString("　"+card_date+"　");
					cell.setCellValue(rts);
					cell.setCellStyle(style);
					i++;
					cell = row.createCell(i);
					String card_time = OperateDate.dateToStr(DateUtils.getDate(obj.toString(), "yyyy-MM-dd HH:mm:ss"), "HH:mm");
					rts = new HSSFRichTextString("　"+card_time+"　");
					cell.setCellValue(rts);
					cell.setCellStyle(style);
					continue;
				}else if("train_date".equalsIgnoreCase(c[i])){
					rts = new HSSFRichTextString("　"+sf.format(obj)+"　");
					cell.setCellValue(rts);
					cell.setCellStyle(style);
					continue;
				}else if("r3130".equalsIgnoreCase(c[i])||"r3126".equalsIgnoreCase(c[i])||"r3133".equalsIgnoreCase(c[i])){
					rts = new HSSFRichTextString("　"+getStr(conn,"r31",c[i],this.classplan)+"　");
					cell.setCellValue(rts);
					cell.setCellStyle(style);
					continue;
				}
				
				FieldItem fieldItem = findFieldItem(fieldItems, c[i]);
				if((fieldItem!=null)&&("N".equalsIgnoreCase(fieldItem.getItemtype()))){
				    String aValue= PubFunc.round(rs.getString(fieldItem.getItemid()), fieldItem.getDecimalwidth());
				    if(aValue==null) {
                        aValue = "";
                    } else if(0 == Double.parseDouble(aValue)) {
                        aValue = "";
                    }
				    rts=new HSSFRichTextString("　"+aValue+"　");
				}
				else {
	                //obj=obj.toString().equals("0")||obj.toString().equals("0.00")?"":obj;
	                rts=new HSSFRichTextString("　"+obj.toString()+"　");
	            }
				if("class_len".equalsIgnoreCase(c[i])){
					DecimalFormat df = new DecimalFormat("#.##");
					rts=new HSSFRichTextString("　"+df.format(Double.parseDouble(obj.toString()))+"　");
					
				}

				cell.setCellValue(rts);
				//System.out.println(temp+"+++++++++");
				cell.setCellStyle(style);
				if(i==c.length-1){
					cell = row.createCell(c.length);
					cell.setCellValue("");
					style = wb.createCellStyle();
					style.setBorderLeft(BorderStyle.THIN);
					cell.setCellStyle(style);
				}
			}
		}
		
		
		for (int i = 0; i < cols.length; i++) {
			sheet.autoSizeColumn((short)i);
		}
		FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")
				+ System.getProperty("file.separator") + filename);
		wb.write(fileOut);
		fileOut.close();
		return filename;
	}
	
	private FieldItem findFieldItem(ArrayList fieldItems, String itemid){
	    
	    FieldItem fieldItem = null;
	    
	    for(int i=0; i < fieldItems.size(); i++){
	        FieldItem item = (FieldItem)fieldItems.get(i);
	        if(item.getItemid().equalsIgnoreCase(itemid)){
	            fieldItem = item;
	            break;
	        }	            
	    }
	    
	    return fieldItem;	    
	}
	
	// 设置标题(tableName:标题名、size:生成Excel的列数)
	private void headerAdd(HSSFRow row, HSSFSheet sheet, HSSFWorkbook wb,
			String tableName, int size) {
		try {
			ExportExcelUtil.mergeCell(sheet, 0, (short) 0, 0, (short) (size - 1));// 跨一行duo列
			HSSFCell cell1 = row.createCell(0);
			cell1.setCellValue(tableName);
			HSSFCellStyle style = wb.createCellStyle();
			HSSFFont font = wb.createFont();
			font.setBold(true);
			font.setFontHeightInPoints(Short.parseShort(10 + ""));
			style.setFont(font);
			font.setFontHeightInPoints((short) 20);
			font.setBold(true);
			font.setFontHeightInPoints((short) 24);
			style.setFont(font);
			style.setAlignment(HorizontalAlignment.CENTER);
			cell1.setCellStyle(style);
			for (int i = 1; i < size; i++) {
				cell1 = row.createCell(i);
				cell1.setCellStyle(style);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 设置单元格和字体样式 (type=1：字体加粗)
	private HSSFCellStyle createStyle(HSSFWorkbook wb, int type) {
		HSSFCellStyle style = wb.createCellStyle();
		HSSFFont font = wb.createFont();
		if (type == 1) {
            font.setBold(true);
        }
		font.setFontHeightInPoints(Short.parseShort(10 + ""));
		style.setFont(font);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setWrapText(false);// 自动换行
		return style;
	}

	private String setR4101(Connection conn,Object obj){
		String temp="";
		String sql="select r1302 from r13,r41 where r13.r1301=r41.r4105 and r4101='"+obj+"'";
		ContentDAO dao=new ContentDAO(conn);
		RowSet rs=null;
		try {
			rs=dao.search(sql);
			if(rs.next()) {
                temp=rs.getString("r1302");
            }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
		    PubFunc.closeDbObj(rs);
		}
		return temp;
	}
	/**
	 * 得到培训班名称
	 * @param conn
	 * @param obj
	 * @return
	 */
	private String setR3130(Connection conn,Object obj){
		
		String temp="";
		String  sql="select r3130 from r31 where r3101='"+obj+"'";
		ContentDAO dao=new ContentDAO(conn);
		RowSet rs=null;
		try {
			rs=dao.search(sql);
			if(rs.next()) {
                temp=rs.getString("r3130");
            }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
		    PubFunc.closeDbObj(rs);
		}
		return temp;
	}
	
	private String getStr(Connection conn,String table,String cln,String value){
		ContentDAO dao=new ContentDAO(conn);
		String temp="";
		String sql="select "+cln+" from "+table+" where "+table+"01='"+value+"'";
		RowSet rs=null;
		try {
			rs=dao.search(sql);
			if(rs.next()) {
                temp=rs.getString(cln);
            }
			temp=temp==null?"":temp;
			if("r3133".equalsIgnoreCase(cln)){
				temp=AdminCode.getCodeName("45",temp);
			}else if("r3126".equalsIgnoreCase(cln)){
				temp=getStr(conn,"r10","r1011",temp);
			}
			temp=temp==null?"":temp;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
		    PubFunc.closeDbObj(rs);
		}
		return temp;
	}
	
	public String setFlag(String flag) {
	   return this.flag = flag;
    }
}