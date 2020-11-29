package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
/**
 * 
* 类名称：ExportDemandExcelTrans   
* 类描述： excel招聘导出查询到的需求
* 创建人：akuan   
* 创建时间：Aug 5, 2013 9:16:16 AM   
* 修改人：akuan   
* 修改时间：Aug 5, 2013 9:16:16 AM   
* 修改备注：   
* @version  v6x 1.0  
*
 */
public class ExportDemandExcelTrans extends IBusiness {

	public void execute() throws GeneralException {
		ResultSet rs=null;
		String infor="ok";
		String name="";
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("招聘需求表HJSJ");
		FileOutputStream fileOut =null;
		try{
			ContentDAO dao = new ContentDAO(this.frameconn);
			rs=dao.search("select * from z03");
			ResultSetMetaData md=rs.getMetaData();
			int nColumn=md.getColumnCount();
			ArrayList fieldlist=new ArrayList(); 
			String fieldName="";
			FieldItem item;
			for(int i=1;i<=nColumn;i++)
			{ 
				fieldName=md.getColumnName(i);
				item=DataDictionary.getFieldItem(fieldName);
				if(item==null){//非指标字段
					item=new FieldItem();
					item.setItemid(fieldName);
					item.setItemdesc(fieldName);
					item.setFieldsetid("");
					item.setItemtype("M");
				}
				fieldlist.add(item);

			}
			//var sql="${positionDemandForm.sql}";
			//hashvo.setValue("sql",sql);
			//安全平台改造,将sql语句存放在后台userview中（招聘管理-需求报批-文件-导出）原来是通过ajax传递的
			String sql=(String) this.userView.getHm().get("hire_sql");//(String) this.getFormHM().get("sql");//获得查询的sql
			sql=PubFunc.keyWord_reback(sql);
			sql="select z03.* " +sql.substring(sql.indexOf("from"));
			rs=dao.search(sql);
			if(rs.next()){
				getExcelTittle(workbook, sheet, fieldlist);
				getExcelBody(workbook,sheet,fieldlist,sql);
				// outName ="Hire";//用中文名字 导出后下载页面不自动关掉 ? zxj没发现这种情况。
				 String outName = "hire_" + this.userView.getUserName() +".xls";
				
				name=outName;
			    fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outName);
			    workbook.write(fileOut);
			    fileOut.flush();
			    fileOut.close();
			}else{
				infor="none";
			}

			}catch(Exception e){	
				infor="error";
			e.printStackTrace();	
			throw GeneralExceptionHandler.Handle(e);
			}		
			finally		{
				PubFunc.closeResource(workbook);
				PubFunc.closeResource(fileOut);
				PubFunc.closeResource(rs);
			}

			this.getFormHM().put("infor", infor);
			/**安全平台改造,防止任意文件下载漏洞**/
			this.getFormHM().put("name", PubFunc.encrypt(name));

	}
	/**
	 * 获得excel表头
	 * @param workbook
	 * @param sheet
	 * @param fieldlist
	 */
	public void getExcelTittle(HSSFWorkbook workbook,HSSFSheet sheet,ArrayList fieldlist){
		HSSFRow row = sheet.createRow(0);
		HSSFCell cell = null;
		HSSFComment comm =null;
		HSSFPatriarch patr = sheet.createDrawingPatriarch();
		HSSFCellStyle style = workbook.createCellStyle();
		HSSFFont font = workbook.createFont();
		font.setFontHeightInPoints((short) 10);
		style.setFont(font);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setWrapText(true);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBottomBorderColor((short) 8);
		style.setLeftBorderColor((short) 8);
		style.setRightBorderColor((short) 8);
		style.setTopBorderColor((short) 8);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
		for (int i = 0; i < fieldlist.size(); i++)
		{
			FieldItem field = (FieldItem) fieldlist.get(i);
		    String fieldId = field.getItemid().toLowerCase();
		    String fieldDesc = field.getItemdesc();
		    sheet.setColumnWidth((short) (i), (short) 5000);
		    cell = row.createCell((short) (i));
		    cell.setCellValue(cellStr(fieldDesc));
		    cell.setCellStyle(style);
		    comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) (i + 1), 0, (short) (i + 2), 1));
		    comm.setString(new HSSFRichTextString(fieldId));
		    cell.setCellComment(comm);
		}
		
	}
	/**
	 * 获得excel表体
	 * @param workbook
	 * @param sheet
	 * @param fieldlist
	 * @throws SQLException 
	 */
	public void getExcelBody(HSSFWorkbook workbook,HSSFSheet sheet,ArrayList fieldlist,String sql)  throws GeneralException{
		ContentDAO dao = new ContentDAO(this.frameconn);
		HSSFRow row = null;
		HSSFCell cell = null;
		FieldItem field;
		String fieldId;//指标id
		String itemtype = "";//指标类型
		int itemlength=0;//指标长度
		int decwidth=0;//小数位数
		String codesetid="";//代码类
		HSSFCellStyle style = dataStyle(workbook);
		try {
			int rowCount = 1;
			this.frowset=dao.search(sql);
			while(this.frowset.next()){
				row = sheet.createRow(rowCount++);
				row.setHeight((short) 1000);
				for (int i = 0; i < fieldlist.size(); i++)
				{
					 field = (FieldItem) fieldlist.get(i);
				     fieldId = field.getItemid().toLowerCase();
				     itemtype = field.getItemtype();
				     decwidth = field.getDecimalwidth();
				     codesetid = field.getCodesetid();
				     itemlength =  field.getItemlength();
				     style = dataStyle(workbook);
				     style.setWrapText(true);
				     cell = row.createCell((short) i);
				     cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					 if("N".equals(itemtype)){
						style.setAlignment(HorizontalAlignment.LEFT);
						cell.setCellStyle(style);
						cell.setCellValue(this.frowset.getString(fieldId));
						
					 }else if("D".equals(itemtype)){
					  style.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式	
					   cell.setCellStyle(style);
				    	Date date = this.frowset.getDate(fieldId);
				    	if(date==null)
				    	{
				    		 cell.setCellValue("");
				    	}else
				    	{
				    		
				    		String value =DateUtils.format(date,"yyyy-MM-dd");
				    		cell.setCellValue(cellStr(value));
				    	}
					 }else if("M".equals(itemtype)){
					    style.setAlignment(HorizontalAlignment.LEFT);
						cell.setCellStyle(style);
						cell.setCellValue(this.frowset.getString(fieldId));
						
					 }else if("A".equals(itemtype)){
						cell.setCellStyle(style);
						if("0".equals(codesetid)){//非代码型
							cell.setCellValue(this.frowset.getString(fieldId));
						}else{//代码型
							String code=this.frowset.getString(fieldId);
							String name=AdminCode.getCode(codesetid.toUpperCase(), code) != null ? AdminCode.getCode(codesetid.toUpperCase(), code).getCodename() : ""; 
							if(code!=null&&!"".equals(code)){
								cell.setCellValue(code+":"+name);
							}else{
								cell.setCellValue(code);	
							}						

						}
					 }
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
  public HSSFRichTextString cellStr(String context)
	{

			HSSFRichTextString textstr = new HSSFRichTextString(context);
			return textstr;
	}
  public HSSFCellStyle dataStyle(HSSFWorkbook workbook)
  {

	HSSFCellStyle style = workbook.createCellStyle();
	style.setBorderBottom(BorderStyle.THIN);
	style.setBorderLeft(BorderStyle.THIN);
	style.setBorderRight(BorderStyle.THIN);
	style.setBorderTop(BorderStyle.THIN);
	style.setVerticalAlignment(VerticalAlignment.CENTER);
	style.setBottomBorderColor((short) 8);
	style.setLeftBorderColor((short) 8);
	style.setRightBorderColor((short) 8);
	style.setTopBorderColor((short) 8);
	return style;
  }
  public String decimalwidth(int len)
  {

	StringBuffer decimal = new StringBuffer("0");
	if (len > 0)
	    decimal.append(".");
	for (int i = 0; i < len; i++)
	{
	    decimal.append("0");
	}
	decimal.append("_ ");
	return decimal.toString();
  }
}
