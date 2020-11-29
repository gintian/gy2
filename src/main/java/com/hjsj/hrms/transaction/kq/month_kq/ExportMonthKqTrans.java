package com.hjsj.hrms.transaction.kq.month_kq;

import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ExportMonthKqTrans extends IBusiness{

	public void execute() throws GeneralException {
		String outName = "";
		try {
			ArrayList list = DataDictionary.getFieldList("q35",
					Constant.USED_FIELD_SET);
			ArrayList ls = new ArrayList();
			String year = (String)this.getFormHM().get("year");
			String month = (String)this.getFormHM().get("month");
			WeekUtils wk = new WeekUtils();
			Date date = null;
			date = wk.lastMonth(Integer.parseInt(year), Integer.parseInt(month)); //按年月得到当前需要导出的月份的最后一天
			for(int i = 0 ; i < list.size() ; i++){
				FieldItem fieldItem = (FieldItem)list.get(i);
				String tempitemid = fieldItem.getItemid();
				if("appuser".equalsIgnoreCase(tempitemid)){
					continue;
				}
				if("1".equalsIgnoreCase(fieldItem.getState())){
					if(date.getDate() == 28){//如果是二十八天一个月 则二十九以后的日都不要 导出时按月份动态导出列
						if(!("q3531".equalsIgnoreCase(fieldItem.getItemid()))&&
								!("q3530".equalsIgnoreCase(fieldItem.getItemid()))&&
								!("q3529".equalsIgnoreCase(fieldItem.getItemid()))){
							ls.add(fieldItem);
						}
					}else if(date.getDate() == 29){
						if(!"q3531".equalsIgnoreCase(fieldItem.getItemid())&&
								!"q3530".equalsIgnoreCase(fieldItem.getItemid())){
							ls.add(fieldItem);
						}
					}else if(date.getDate() == 30){
						if(!"q3531".equalsIgnoreCase(fieldItem.getItemid())){
							ls.add(fieldItem);
						}
					}else if(date.getDate() == 31){
						ls.add(fieldItem);
					}
				}
			}
				outName = this.creatExcel(ls , year , month);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			this.getFormHM().put("outName", PubFunc.encrypt(outName));
		}
	}

	private String creatExcel(ArrayList ls , String year , String month) throws Exception{
		StringBuffer sb = new StringBuffer();
		String where = (String)this.getFormHM().get("where");
		StringBuffer sql = new StringBuffer();
		
		HSSFWorkbook wb = new HSSFWorkbook(); // 创建新的Excel 工作簿
		HSSFSheet sheet = wb.createSheet();
		// sheet.setProtect(true);

		HSSFFont font1 = wb.createFont(); //设置样式
		font1.setFontHeightInPoints((short) 10);
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
		// style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		// style2.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

		HSSFFont font2 = wb.createFont();
		font2.setFontHeightInPoints((short) 10);
		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setFont(font2);
		style1.setAlignment(HorizontalAlignment.CENTER);
		style1.setVerticalAlignment(VerticalAlignment.CENTER);
		//style1.setWrapText(true);
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
		// styleCol0.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		// styleCol0.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

		HSSFCellStyle styleCol0_title = dataStyle(wb);
		styleCol0_title.setFont(font2);
		// styleCol0_title.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));//
		// 文本格式
		// styleCol0_title.setFillPattern(FillPatternType.SOLID_FOREGROUND);
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
		
		MonthKqBo bo = new MonthKqBo(this.frameconn);
		//System.out.println(ls.size());
		ExportExcelUtil.mergeCell(sheet, 0, (short) 0, 0, (short) (ls.size()-1)); //第一行 合并所有的列
		HSSFRow row = sheet.getRow(0);
		if (row == null) {
			row = sheet.createRow(0);
		}
		HSSFCell cell = null;
		if(row.getRowNum() == 0){//第一行
			StringBuffer ss = new StringBuffer(year + "年" + month +"月考勤信息");
			cell = row.getCell(0);
			if (cell == null) {
				cell = row.createCell(0);
			}
			cell.setCellValue(cellStr(ss.toString()));
			cell.setCellStyle(style2);
			row = sheet.createRow(1); 
		}
		for(int i = 0 ; i < ls.size() ; i++){
			FieldItem item = (FieldItem)ls.get(i);
			cell = row.getCell(i);
			if(cell == null){
				cell = row.createCell(i);
			}
			sb.append(item.getItemid() + ","); //需要导出的列
			cell.setCellValue(item.getItemdesc());
			cell.setCellStyle(style1);
		}
		String s = sb.toString();
		s = s.substring(0,s.length() - 1 ); // 去掉最后一个逗号
		
		where = PubFunc.keyWord_reback(where); //大小转换特殊符号 比如 ）--> )
		
		//String nbase = this.userView.getDbpriv().toString();
		//nbase.substring(0,nbase.length()-1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
		String nbases = "";
		for(int i = 0 ; i < this.userView.getPrivDbList().size() ; i ++){
			nbases += "'"+this.userView.getPrivDbList().get(i) + "',";
		}
		nbases = nbases.substring(0,nbases.length() - 1);
		sql.append(" select ");
		sql.append(s + where);
	//	sql.append(" and nbase in (");
	//	sql.append(nbases + ")");
		int count = 1;
		
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			FieldItem item = null;
			this.frowset = dao.search(sql.toString());
			while(this.frowset.next()){
				count ++;
				//第N行
				row = sheet.createRow(count);
				for(int i = 1 ; i <= ls.size() ; i ++){
					item = (FieldItem)ls.get(i-1);
					cell = row.getCell(i);
					if(cell == null){
						cell = row.createCell(i-1);
						if("0".equals(item.getCodesetid())){
							if("userflag".equalsIgnoreCase(item.getItemid())
								|| "curr_user".equalsIgnoreCase(item.getItemid())){
								if(null == this.frowset.getString(i) ||
										"null".equalsIgnoreCase(this.frowset.getString(i))){
									cell.setCellValue("");
								}else{
									cell.setCellValue(bo.getUserNameById(this.frowset.getString(i)));
								}
							}else if("approcess".equalsIgnoreCase(item.getItemid())){
								cell.setCellValue(Sql_switcher.readMemo(this.frowset, "approcess"));// clob类型 直接取报错
							}else if("q35z0".equalsIgnoreCase(item.getItemid())){
								
								cell.setCellValue(sdf.format(this.frowset.getDate("q35z0")));
							}else{
								if(null ==this.frowset.getString(i)){
									cell.setCellValue("");
								}else{
								    cell.setCellValue(this.frowset.getString(i));
								}
							}
						}else{
							if("27".equals(item.getCodesetid())){
								cell.setCellValue(bo.getImgByCodeId(this.frowset.getString(i)));
							}else{
								cell.setCellValue(AdminCode.getCodeName(item.getCodesetid(), this.frowset.getString(i)));
							}
						}
						cell.setCellStyle(style1);
					}
				}
			
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String outName =  "month_kq_"+ PubFunc.getStrg() + ".xls";

		try {
			FileOutputStream fileOut = new FileOutputStream(System
					.getProperty("java.io.tmpdir")
					+ System.getProperty("file.separator") + outName);
			wb.write(fileOut);
			fileOut.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		outName = outName.replace(".xls", "#");
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
}
