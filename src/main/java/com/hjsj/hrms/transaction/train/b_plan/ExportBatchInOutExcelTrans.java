package com.hjsj.hrms.transaction.train.b_plan;

import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
/**
 * 下载培训计划模板
 * @author xujian
 *Apr 21, 2012
 */
public class ExportBatchInOutExcelTrans extends IBusiness {
	private Connection conn=null;
	public ExportBatchInOutExcelTrans() {
		this.conn = this.frameconn;
	}

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		//long startTime=System.currentTimeMillis(); 
		String outName="";
		String model = (String)this.getFormHM().get("model");
		//PlanTransBo transbo = new PlanTransBo(this.getFrameconn(),model); 
		try{
			
			ArrayList list = DataDictionary.getFieldList("r25",Constant.USED_FIELD_SET);
			ArrayList ls = new ArrayList();
			for(int i=0;i<list.size();i++){
				FieldItem item=(FieldItem)list.get(i);
				if(!("R2501".equalsIgnoreCase(item.getItemid())||"R2513".equalsIgnoreCase(item.getItemid())||"R2509".equalsIgnoreCase(item.getItemid())||"R2512".equalsIgnoreCase(item.getItemid()))){
					ls.add(item);
				}
			}
			outName=this.creatExcel(ls);
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			this.getFormHM().put("outName", PubFunc.encrypt(outName));
		}
		//long endTime=System.currentTimeMillis(); 
		//System.out.println("程序运行时间： "+(endTime-startTime)+"ms");   
        //System.out.println("1时间： "+startTime+"ms");   
        //System.out.println("2时间： "+endTime+"ms");   

	}
	
	private String creatExcel(ArrayList list) throws Exception{
		
		HSSFWorkbook wb = new HSSFWorkbook(); // 创建新的Excel 工作簿
		HSSFSheet sheet = wb.createSheet();
		// sheet.setProtect(true);
		HSSFFont font1 = wb.createFont();
		font1.setFontHeightInPoints((short) 11);
		font1.setBold(true);
		HSSFCellStyle style2 = wb.createCellStyle();
		style2.setFont(font1);
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

		HSSFFont font2 = wb.createFont();
		font2.setFontHeightInPoints((short) 10);
		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setFont(font2);
		style1.setAlignment(HorizontalAlignment.CENTER);
		style1.setVerticalAlignment(VerticalAlignment.CENTER);
		style1.setWrapText(true);
		style1.setBorderBottom(BorderStyle.THIN);
		style1.setBorderLeft(BorderStyle.THIN);
		style1.setBorderRight(BorderStyle.THIN);
		style1.setBorderTop(BorderStyle.THIN);
		style1.setBottomBorderColor((short) 8);
		style1.setLeftBorderColor((short) 8);
		style1.setRightBorderColor((short) 8);
		style1.setTopBorderColor((short) 8);
		style1.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式

		HSSFCellStyle styleN = dataStyle(wb);
		styleN.setAlignment(HorizontalAlignment.RIGHT);
		styleN.setWrapText(true);
		HSSFDataFormat df = wb.createDataFormat();
		styleN.setDataFormat(df.getFormat(decimalwidth(0)));

		HSSFCellStyle styleCol0 = dataStyle(wb);
		HSSFFont font0 = wb.createFont();
		font0.setFontHeightInPoints((short) 5);
		styleCol0.setFont(font0);
		styleCol0.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式
		styleCol0.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleCol0.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

		HSSFCellStyle styleCol0_title = dataStyle(wb);
		styleCol0_title.setFont(font2);
		styleCol0_title.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式
		styleCol0_title.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleCol0_title.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

		HSSFCellStyle styleF1 = dataStyle(wb);
		styleF1.setAlignment(HorizontalAlignment.RIGHT);
		styleF1.setWrapText(true);
		HSSFDataFormat df1 = wb.createDataFormat();
		styleF1.setDataFormat(df1.getFormat(decimalwidth(1)));

		HSSFCellStyle styleF2 = dataStyle(wb);
		styleF2.setAlignment(HorizontalAlignment.RIGHT);
		styleF2.setWrapText(true);
		HSSFDataFormat df2 = wb.createDataFormat();
		styleF2.setDataFormat(df2.getFormat(decimalwidth(2)));

		HSSFCellStyle styleF3 = dataStyle(wb);
		styleF3.setAlignment(HorizontalAlignment.RIGHT);
		styleF3.setWrapText(true);
		HSSFDataFormat df3 = wb.createDataFormat();
		styleF3.setDataFormat(df3.getFormat(decimalwidth(3)));

		HSSFCellStyle styleF4 = dataStyle(wb);
		styleF4.setAlignment(HorizontalAlignment.RIGHT);
		styleF4.setWrapText(true);
		HSSFDataFormat df4 = wb.createDataFormat();
		styleF4.setDataFormat(df4.getFormat(decimalwidth(4)));

		HSSFCellStyle styleF5 = dataStyle(wb);
		styleF5.setAlignment(HorizontalAlignment.RIGHT);
		styleF5.setWrapText(true);
		HSSFDataFormat df5 = wb.createDataFormat();
		styleF5.setDataFormat(df5.getFormat(decimalwidth(5)));

		//sheet.setColumnWidth((short) 0, (short) 1000);// 标识列不隐藏了，因为客户复制整行数据时候不能复制第一列的内容
		HSSFPatriarch patr = sheet.createDrawingPatriarch();
		
		HSSFRow row =sheet.getRow(0);
		if(row==null){
			row=sheet.createRow(0);
		}
		row.setHeight((short)600);
		HSSFCell cell = null;
		HSSFComment comm = null;

		

		ArrayList codeCols = new ArrayList();
		for (int i = 0; i < list.size(); i++)
		{
			FieldItem field = (FieldItem) list.get(i);
			String fieldName = field.getItemid().toLowerCase();
			String fieldLabel = field.getItemdesc();
			int w=field.getDisplaywidth();
			if(w==0){
				w=8;
			}
			if(w > 30)
				w=30;
			sheet.setColumnWidth((i), w*350);
			cell=row.getCell(i);
			if(cell==null)
				cell=row.createCell(i);

			cell.setCellValue(cellStr(fieldLabel));
			cell.setCellStyle(style2);
			comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) (i + 1), 0, (short) (i + 2), 1));
			comm.setString(new HSSFRichTextString(fieldName));
			cell.setCellComment(comm);
			if ("A".equalsIgnoreCase(field.getItemtype())&&(field.getCodesetid()!=null&&!"".equals(field.getCodesetid())&&!"0".equals(field.getCodesetid())))
				codeCols.add(field.getCodesetid() + ":" + new Integer(i).toString());
		}

		try
		{
			ContentDAO dao = new ContentDAO(this.frameconn);
			int rowCount = 1;
			while(rowCount<1001)
			{
				row =sheet.getRow(rowCount);
				if(row==null){
					row=sheet.createRow(rowCount);
				}
				for (int i = 0; i < list.size(); i++)
				{
					FieldItem field = (FieldItem) list.get(i);
					String itemtype = field.getItemtype();
					int decwidth = field.getDecimalwidth();

					cell = row.getCell(i);
					if(cell==null)
						cell=row.createCell(i);
					if ("N".equals(itemtype))
					{
						if (decwidth == 0)
							cell.setCellStyle(styleN);
						else if (decwidth == 1)
							cell.setCellStyle(styleF1);
						else if (decwidth == 2)
							cell.setCellStyle(styleF2);
						else if (decwidth == 3)
							cell.setCellStyle(styleF3);
						else if (decwidth == 4)
							cell.setCellStyle(styleF4);
						// else if(decwidth==5)
						// cell.setCellStyle(styleF5);
						cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						cell.setCellValue("");
					} else
					{
						cell.setCellStyle(style1);
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					}

				}
				rowCount++;
			}
			rowCount--;
			
			int index = 0;

			String[] lettersUpper =
			{ "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
			for (int n = 0; n < codeCols.size(); n++)
			{
				int m = 0;
				String codeCol = (String) codeCols.get(n);
				String[] temp = codeCol.split(":");
				String codesetid = temp[0];
				int codeCol1 = Integer.valueOf(temp[1]).intValue();
				StringBuffer codeBuf = new StringBuffer();
				if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@K".equalsIgnoreCase(codesetid))
				{
					codeBuf.append("select count(*) from codeitem where codesetid='" + codesetid + "'");// and codeitemid=childid and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date");
					this.frowset = dao.search(codeBuf.toString());
					if(this.frowset.next()){
						if(this.frowset.getInt(1)<200){
							codeBuf.setLength(0);
							codeBuf.append("select codesetid,codeitemid,codeitemdesc from codeitem where codesetid='" + codesetid + "'");// and codeitemid=childid and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date");
						}else{
							continue;
						}
					}
				} else
				{
					if (!"UN".equals(codesetid)){
						m=loadorg(sheet,row,cell,index,m,dao,codesetid);
					}else if ("UN".equals(codesetid))
					{
						codeBuf.setLength(0);
						codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid='" + codesetid
								+ "' and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid");
						
					}
				}
				if (!"UM".equals(codesetid) && !"@K".equalsIgnoreCase(codesetid))
				{
					this.frowset = dao.search(codeBuf.toString());
					while (this.frowset.next())
					{
						row = sheet.getRow(m + 0);
						if (row == null)
							row = sheet.createRow(m + 0);
						cell = row.createCell((208 + index));
						if("UN".equals(codesetid)){
							int grade=this.frowset.getInt("grade");
							StringBuffer sb=new StringBuffer();
							sb.setLength(0);
							for(int i=1;i<grade;i++){
								sb.append("  ");
							}
							cell.setCellValue(new HSSFRichTextString(sb.toString()+this.frowset.getString("codeitemdesc")+"("+this.frowset.getString("codeitemid")+")"));
						}else{
							cell.setCellValue(new HSSFRichTextString(this.frowset.getString("codeitemdesc")));
						}
						m++;
					}
				}
				if(m==0){
					continue;
				}
				sheet.setColumnWidth((208 + index),0);
				String strFormula ="";
				if(index<=25){
					strFormula = "$H" + lettersUpper[index] + "$1:$H" + lettersUpper[index] + "$" + Integer.toString(m); // 表示BA列1到m行作为下拉列表来源数据
				}else if(index>25){
					strFormula = "$I" + lettersUpper[index-26] + "$1:$I" + lettersUpper[index-26] + "$" + Integer.toString(m); // 表示BA列1到m行作为下拉列表来源数据
				}
				CellRangeAddressList addressList = new CellRangeAddressList(1, rowCount, codeCol1, codeCol1);//rowCount
				DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(strFormula);
				HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
				dataValidation.setSuppressDropDownArrow(false);
				sheet.addValidationData(dataValidation);
				index++;
			}

		} catch (Exception e1)
		{
			e1.printStackTrace();
		}
		
		String outName = this.userView.getUserName() + "_train.xls";

		try
		{
			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outName);
			wb.write(fileOut);
			fileOut.close();
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		sheet = null;
		wb = null;
		return outName;
	}

	public HSSFRichTextString cellStr(String context)
	{

		HSSFRichTextString textstr = new HSSFRichTextString(context);
		return textstr;
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
	
	private int loadorg(HSSFSheet sheet,HSSFRow row,HSSFCell cell,int index,int m,ContentDAO dao,String type) throws Exception {
		Statement st=null;
		ResultSet rs = null;
		DbSecurityImpl dbS = new DbSecurityImpl();
		try{
			
			st=this.frameconn.createStatement();
			String sql="select codesetid,codeitemid,childid,codeitemdesc,grade from organization where codeitemid=parentid and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid";
			//rs = dao.search(sql);
			dbS.open(conn, sql);
			rs = st.executeQuery(sql);
			String codesetid="";
			String codeitemid="";
			String childid="";
			String codeitemdesc="";
			int grade=0;
			while(rs.next()){
				codesetid=rs.getString("codesetid");
				codeitemid=rs.getString("codeitemid");
				childid=rs.getString("childid");
				codeitemdesc=rs.getString("codeitemdesc");
				grade=rs.getInt("grade");
				row = sheet.getRow(m + 0);
				if (row == null)
					row = sheet.createRow(m + 0);
				cell = row.createCell((208 + index));
				StringBuffer sb=new StringBuffer();
				sb.setLength(0);
				for(int i=1;i<grade;i++){
					sb.append("  ");
				}
				cell.setCellValue(new HSSFRichTextString(sb.toString()+codeitemdesc+"("+codeitemid+")"));
				m++;
				if(!codeitemid.equals(childid))
					m=loadchild(sheet,row,cell,index,m,dao,codeitemid,type);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				// 关闭Wallet
				dbS.close(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			PubFunc.closeResource(rs);
			PubFunc.closeResource(st);
		}
		return m;
	}
	
	private int loadchild(HSSFSheet sheet,HSSFRow row,HSSFCell cell,int index,int m,ContentDAO dao,String parentid,String type) throws Exception {
		ResultSet rs = null;
		Statement st=null;
		DbSecurityImpl dbS = new DbSecurityImpl();
		try{
			String sql = null;
			st=this.frameconn.createStatement();
			if("@K".equalsIgnoreCase(type)){
				sql="select codesetid,codeitemid,childid,codeitemdesc,grade from organization where parentid='"+parentid+"' and parentid<>codeitemid and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid";
			}else{
				sql="select codesetid,codeitemid,childid,codeitemdesc,grade from organization where parentid='"+parentid+"' and codesetid<>'@K' and parentid<>codeitemid and "+Sql_switcher.dateValue(PubFunc.FormatDate(new Date(),"yyyy-MM-dd"))+" between start_date and end_date order by a0000,codeitemid";
			}
			//rs = dao.search(sql);
			dbS.open(conn, sql);
			rs = st.executeQuery(sql);
			String codesetid="";
			String codeitemid="";
			String childid="";
			String codeitemdesc="";
			int grade=0;
			while(rs.next()){
				codesetid=rs.getString("codesetid");
				codeitemid=rs.getString("codeitemid");
				childid=rs.getString("childid");
				codeitemdesc=rs.getString("codeitemdesc");
				grade=rs.getInt("grade");
				row = sheet.getRow(m + 0);
				if (row == null)
					row = sheet.createRow(m + 0);
				cell = row.createCell((208 + index));
				StringBuffer sb=new StringBuffer();
				sb.setLength(0);
				for(int i=1;i<grade;i++){
					sb.append("  ");
				}
				cell.setCellValue(new HSSFRichTextString(sb.toString()+codeitemdesc+"("+codeitemid+")"));
				m++;
				if(!codeitemid.equals(childid))
					m=loadchild(sheet,row,cell,index,m,dao,codeitemid,type);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
			try {
				// 关闭Wallet
				dbS.close(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			PubFunc.closeResource(rs);
			PubFunc.closeResource(st);
		}
		return m;
	}
}
