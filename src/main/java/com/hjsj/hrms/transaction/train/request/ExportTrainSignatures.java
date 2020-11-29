package com.hjsj.hrms.transaction.train.request;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.businessobject.train.TrainClassBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;

public class ExportTrainSignatures extends IBusiness {
	private Connection conn = null;

	public ExportTrainSignatures() {
		this.conn = this.frameconn;
	}

	public void execute() throws GeneralException {
		String outName = "";
		try {
			ArrayList list = DataDictionary.getFieldList("r40",
					Constant.USED_FIELD_SET);
			ArrayList ls = new ArrayList();
			for(int i = 0 ; i < list.size() ; i++){
				FieldItem fieldItem = (FieldItem)list.get(i);
				//导出时只导出表中部分列
				if("b0110".equals(fieldItem.getItemid()) ||	
						"e0122".equals(fieldItem.getItemid())||
						"r4002".equals(fieldItem.getItemid())){
					ls.add(fieldItem);
				}
			}
			
			outName = this.creatExcel(ls);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			this.getFormHM().put("outName", PubFunc.encrypt(outName));
		}
	}

	private String creatExcel(ArrayList list) throws Exception {
		ArrayList ls = new ArrayList();
		String r3101 = (String)this.getFormHM().get("r3101");
		
		TrainClassBo bo = new TrainClassBo(this.frameconn);
		if(!bo.checkClassPiv(r3101, this.userView))
		    throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("train.info.chang.nopiv")));
		
		/**得到系统的唯一性指标**/
        Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.frameconn);
        String onlyFlag = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
        FieldItem fieldItem = DataDictionary.getFieldItem(onlyFlag);
        //存在并且构库
        boolean onlyItemExist = fieldItem!=null && "1".equals(fieldItem.getUseflag());
        if(onlyItemExist){
        	list.add(fieldItem);
        }
		
		HSSFWorkbook wb = new HSSFWorkbook(); // 创建新的Excel 工作簿
		HSSFSheet sheet = wb.createSheet();
		// sheet.setProtect(true);

		HSSFFont font1 = wb.createFont(); //设置样式
		font1.setFontHeightInPoints((short) 20);
		font1.setBold(true);
		font1.setColor(HSSFFont.COLOR_NORMAL);
		HSSFCellStyle style2 = wb.createCellStyle();
		style2.setFont(font1);
		style2.setAlignment(HorizontalAlignment.CENTER);
		style2.setVerticalAlignment(VerticalAlignment.CENTER);
		style2.setWrapText(true);
		style2.setBorderLeft(BorderStyle.valueOf((short)1));   //设置左边框   
        style2.setBorderRight(BorderStyle.valueOf((short)1));   //设置有边框   
        style2.setBorderTop(BorderStyle.valueOf((short)1));   //设置下边框 
        style2.setBorderBottom(BorderStyle.valueOf((short)1));
		// style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		// style2.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

		HSSFFont font2 = wb.createFont();
		font2.setFontHeightInPoints((short) 10);
		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setFont(font2);
		style1.setAlignment(HorizontalAlignment.CENTER);
		style1.setVerticalAlignment(VerticalAlignment.CENTER);
		style1.setWrapText(true);
		style1.setBorderLeft(BorderStyle.valueOf((short)1));   //设置左边框   
        style1.setBorderRight(BorderStyle.valueOf((short)1));   //设置有边框   
        style1.setBorderTop(BorderStyle.valueOf((short)1));   //设置下边框 
        style1.setBorderBottom(BorderStyle.valueOf((short)1));
		// style1.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式

		HSSFCellStyle styleN = dataStyle(wb);
		styleN.setAlignment(HorizontalAlignment.RIGHT);
		styleN.setWrapText(true);
		HSSFDataFormat df = wb.createDataFormat();
		styleN.setDataFormat(df.getFormat(decimalwidth(0)));

		HSSFCellStyle styleCol0 = dataStyle(wb);
		HSSFFont font0 = wb.createFont();
		font0.setFontHeightInPoints((short) 5);
		styleCol0.setFont(font0);
		// styleCol0.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));//
		// 文本格式
		// styleCol0.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		// styleCol0.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

		HSSFCellStyle styleCol0_title = dataStyle(wb);
		styleCol0_title.setFont(font2);
		// styleCol0_title.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));//
		// 文本格式
		// styleCol0_title.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		// styleCol0_title.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

		HSSFCellStyle styleF1 = dataStyle(wb);
		styleF1.setAlignment(HorizontalAlignment.RIGHT);
		HSSFFont font3 = wb.createFont(); //设置样式
		font3.setFontHeightInPoints((short) 3);
		styleF1.setFont(font3);
		styleF1.setWrapText(true);
		HSSFDataFormat df1 = wb.createDataFormat();
		styleF1.setDataFormat(df1.getFormat(decimalwidth(1)));

		HSSFCellStyle styleF2 = dataStyle(wb);
		styleF2.setAlignment(HorizontalAlignment.RIGHT);
		styleF2.setFont(font3);
		styleF2.setWrapText(true);
		HSSFDataFormat df2 = wb.createDataFormat();
		styleF2.setDataFormat(df2.getFormat(decimalwidth(2)));

		HSSFCellStyle styleF3 = dataStyle(wb);
		styleF3.setAlignment(HorizontalAlignment.RIGHT);
		styleF3.setFont(font3);
		styleF3.setWrapText(true);
		HSSFDataFormat df3 = wb.createDataFormat();
		styleF3.setDataFormat(df3.getFormat(decimalwidth(3)));

		HSSFCellStyle styleF4 = dataStyle(wb);
		styleF4.setAlignment(HorizontalAlignment.RIGHT);
		styleF4.setFont(font3);
		styleF4.setWrapText(true);
		HSSFDataFormat df4 = wb.createDataFormat();
		styleF4.setDataFormat(df4.getFormat(decimalwidth(4)));

		HSSFCellStyle styleF5 = dataStyle(wb);
		styleF5.setAlignment(HorizontalAlignment.RIGHT);
		styleF5.setFont(font3);
		styleF5.setWrapText(true);
		HSSFDataFormat df5 = wb.createDataFormat();
		styleF5.setDataFormat(df5.getFormat(decimalwidth(5)));

		HSSFRow row = sheet.getRow(0);
		if (row == null) {
			row = sheet.createRow(0);
		}
		row.setHeight((short) 1000);
		HSSFCell cell = null;

		sheet.setColumnWidth((1), 15 * 500);	//设置列宽
		sheet.setColumnWidth((2), 15 * 500);    //设置列宽
		if(onlyItemExist){
			sheet.setColumnWidth((3), 15 * 400);    //设置列宽
			// 设置第一行的数据
			ExportExcelUtil.mergeCell(sheet, 0, (short) 0, 0, (short) 6);// 合并第一行的单元格
		}else
			// 设置第一行的数据
			ExportExcelUtil.mergeCell(sheet, 0, (short) 0, 0, (short) 5);// 合并第一行的单元格

			String trainName = this.getNameById(r3101); //培训班名称
			//获取当前系统时间
			int year = 0;
			int month = 0; 
			int day = 0;
			Calendar c=Calendar.getInstance();
			year=c.get(Calendar.YEAR);
			month=c.get(Calendar.MONTH)+1;
			day=c.get(Calendar.DAY_OF_MONTH);
			String data = year+"年"+month+"月"+day+"日";
			StringBuffer sb = new StringBuffer(trainName+"签到表   "+data);
			if (row.getRowNum() == 0) {
				cell = row.getCell(0);
				if (cell == null) {
					cell = row.createCell(0);
				}
				cell.setCellValue(cellStr(sb.toString()));
				cell.setCellStyle(style2);

				row = sheet.createRow(row.getRowNum() + 1); // 第二行开始
			}
			
			cell = row.getCell(0); //第二行第一列
			if(cell == null){
				cell = row.createCell(0);
			}
			cell.setCellValue("序号");
			cell.setCellStyle(style1);
			
			
			cell = row.getCell(1);
			if(cell == null){
				cell = row.createCell(1);
			}
			cell.setCellValue("单位");
			cell.setCellStyle(style1);

			cell = row.getCell(2);
			if(cell == null){
				cell = row.createCell(2);
			}
			cell.setCellValue("部门");
			cell.setCellStyle(style1);
			if(onlyItemExist){
				cell = row.getCell(3);
				if(cell == null){
					cell = row.createCell(3);
				}
				cell.setCellValue(fieldItem.getItemdesc());
				cell.setCellStyle(style1);
			}
			
			cell = row.getCell(list.size());
			if(cell == null){
				cell = row.createCell(list.size());
			}
			cell.setCellValue("姓名");
			cell.setCellStyle(style1);
			
			cell = row.getCell(list.size()+1);
			if(cell == null){
				cell = row.createCell(list.size()+1);
			}
			cell.setCellValue("签到处");
			cell.setCellStyle(style1);
			
			cell = row.getCell(list.size()+2);
			if(cell == null){
				cell = row.createCell(list.size()+2);
			}
			cell.setCellValue("备注");
			cell.setCellStyle(style1);
			RowSet r = null;
		try {
			
			ContentDAO dao = new ContentDAO(this.frameconn);
			String sql = "select r4002,b0110,e0122,NBase,R4001 from r40 where r4005='"+r3101+"' and R4013='03' order by b0110,e0122";
			r = dao.search(sql);
			String onlyFlag2 = "";
			while(r.next()){
				String name = r.getString("r4002");
				String b = r.getString("b0110");
				String e = r.getString("e0122");
				String unitName = this.getDescById(b);
				String departmentName = this.getDetailById(e, b);
				if(onlyItemExist){
					onlyFlag2 = this.getOnlyFlag(r.getString("R4001"), r.getString("NBase"), fieldItem);
					String [] s = {unitName,departmentName,onlyFlag2,name,""};
					ls.add(s);
				}else{
					String [] s = {unitName,departmentName,name,""};
					ls.add(s);
				}
				
			}
			int rowCount = 2;
			while (rowCount < ls.size()+2) {
				row = sheet.getRow(rowCount);
				if (row == null) {
					row = sheet.createRow(rowCount);
				}
				cell = row.getCell(0);
				if (cell == null)
					cell = row.createCell(0);
					cell.setCellValue(rowCount-1);
					cell.setCellStyle(style1);
				for (int i = 1; i <= list.size(); i++) {
					String [] s = (String [] )ls.get(rowCount-2);
					cell = row.getCell(i);
					if (cell == null)
						cell = row.createCell(i);
					//if(i<s.length){
					for(int j = 0 ; j < s.length ; j ++){						
						cell.setCellValue(s[i-1]);
						cell.setCellStyle(style1);
					}
					//}
					//cell.setCellStyle(style1);
					//cell.setCellType(HSSFCell.CELL_TYPE_STRING);

				}
				cell = row.getCell(list.size()+1);
				if(cell == null){
					cell = row.createCell(list.size()+1);
					cell.setCellStyle(style1);
				}
				
				cell = row.getCell(list.size()+2);
				if(cell == null){
					cell = row.createCell(list.size()+2);
					cell.setCellStyle(style1);
				}
				rowCount++;
			}
			rowCount--;
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally{
			try{
				if(r!=null)
					r.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}

		String outName = this.userView.getUserName() + "_train.xls";

		try {
            FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outName);
			wb.write(fileOut);
			fileOut.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		sheet = null;
		wb = null;
		return outName;
	}

	public HSSFRichTextString cellStr(String context) {
		HSSFRichTextString textstr = new HSSFRichTextString(context);
		return textstr;
	}

	public String decimalwidth(int len) {

		StringBuffer decimal = new StringBuffer("0");
		if (len > 0)
			decimal.append(".");
		for (int i = 0; i < len; i++) {
			decimal.append("0");
		}
		decimal.append("_ ");
		return decimal.toString();
	}

	public HSSFCellStyle dataStyle(HSSFWorkbook workbook) {
		HSSFCellStyle style = workbook.createCellStyle();
		// style.setVerticalAlignment(VerticalAlignment.CENTER);
		return style;
	}
	
	//通过培训班ID找到培训班名称
	public String getNameById(String id ){
		String name = "";
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			String sql = "select r3130 from r31 where r3101 = '"+id+"'";
			rs = dao.search(sql);
			if(rs.next()){
				name = rs.getString("r3130");
				name=name.replaceAll("%26lt;","<").replaceAll("%26gt;",">").replaceAll("%26quot;", "\"").replaceAll("%26amp;", "&");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try{
				if(rs != null)
					rs.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return name;
	}
	
	//通过单位ID查找到单位
	public String getDescById(String id){
		String desc = "";
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			String sql = "select codeitemdesc from organization where codeitemid = '"+id+"' and codesetid = 'UN'";
			rs = dao.search(sql);
			if(rs.next()){
				desc = rs.getString("codeitemdesc");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return desc;
	}
	
	//通过单位下的部门ID 找到部门名称
	public String getDetailById(String id , String parentId){
		String desc = "";
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			String sql = " select codeitemdesc from organization where codeitemid = '"+id+"' and parentid like '"+parentId+"%' and codesetid = 'UM'";
			rs = dao.search(sql);
			if(rs.next()){
				desc = rs.getString("codeitemdesc");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return desc;
	}
	
	//获取唯一性指标值
	public String getOnlyFlag(String a0100 , String nbase, FieldItem item){
		String value = "";
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			StringBuffer sql = new StringBuffer();
			if("A01".equals(item.getFieldsetid())){
				sql.append("select "+item.getItemid());
				sql.append(" from "+nbase+"A01");
				sql.append(" where a0100 = '"+a0100+"'");
			}else{
				sql.append("select "+item.getItemid());
				sql.append(" from "+item.getFieldsetid());
				sql.append(" where a0100 = '"+a0100+"' and nbase = "+nbase);
			}
			rs = dao.search(sql.toString());
			if(rs.next()){
				value = rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null)
					rs.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return value;
	}
}
