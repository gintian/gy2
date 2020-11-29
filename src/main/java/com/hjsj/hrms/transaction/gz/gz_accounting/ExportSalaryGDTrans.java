package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.DateStyle;
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
import java.util.HashMap;

/**
 * <p>
 * Title:ExportExcelTrans.java
 * </p>
 * <p>
 * Description:薪资归档导出Excel
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-06-08 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class ExportSalaryGDTrans extends IBusiness {
	public void execute() throws GeneralException {

		String sqlStr = (String) this.getFormHM().get("sqlStr");
		String fieldStr = (String) this.getFormHM().get("fieldStr");
		sqlStr = SafeCode.decode(sqlStr);
		sqlStr = PubFunc.decrypt(sqlStr);
		fieldStr = SafeCode.decode(fieldStr);
		String[] fields = fieldStr.split(",");

		HSSFWorkbook wb = new HSSFWorkbook(); // 创建新的Excel 工作簿
		try {
			HSSFSheet sheet = wb.createSheet();

			HSSFFont font2 = wb.createFont();
			font2.setFontHeightInPoints((short) 10);
			HSSFCellStyle style2 = wb.createCellStyle();
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

			HSSFCellStyle style = wb.createCellStyle();
			style.setFont(font2);
			style.setAlignment(HorizontalAlignment.RIGHT);
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

			HSSFCellStyle style1 = wb.createCellStyle();
			style1.setFont(font2);
			style1.setAlignment(HorizontalAlignment.LEFT);
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

			HSSFRow row = sheet.createRow(0);
			for (int i = 0; i < fields.length; i++) {
				String[] field = fields[i].split(":");
				HSSFCell cell = row.createCell((short) i);
//	    cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				FieldItem item = DataDictionary.getFieldItem(field[0]);
//	    if (item == null)
//		System.out.println("取不到的字段：" + field[0] + ":" + field[1]);
//	    else
//		System.out.println(field[0] + ":" + field[1] + ":" + item.getCodesetid() + ":" + item.getItemtype());
				cell.setCellValue(new HSSFRichTextString(field[1]));
				cell.setCellStyle(style2);
			}

			ContentDAO dao = new ContentDAO(this.frameconn);

			HashMap dbs = new HashMap();
			RowSet rs = dao.search("SELECT Pre, DBName FROM DBName");
			while (rs.next()) {
				dbs.put(rs.getString(1).toUpperCase(), rs.getString(2));
			}

			rs = dao.search(sqlStr);
			int i = 1;
			while (rs.next()) {
				row = sheet.createRow(i++);
				row.setHeight(Short.parseShort("700"));
				for (int j = 0; j < fields.length; j++) {
					String[] field = fields[j].split(":");
					HSSFCell cell = row.createCell((short) j);

					//		    cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					FieldItem item = DataDictionary.getFieldItem(field[0]);
					String value = "";

					if (item == null || "nbase".equalsIgnoreCase(field[0]))// 取不到的字段
					{
						if ("nbase".equalsIgnoreCase(field[0]))
							value = (String) (dbs.get(rs.getString("nbase").toUpperCase()));
						else if ("sp_flag".equalsIgnoreCase(field[0]))
							value = AdminCode.getCode("23", rs.getString("sp_flag")) != null ? AdminCode.getCode("23", rs.getString("sp_flag")).getCodename() : "";
						else if ("a00z2".equalsIgnoreCase(field[0]) || "a00z0".equalsIgnoreCase(field[0]))
							value = DateStyle.dateformat(rs.getDate(field[0]), "yyyy-MM-dd");
						else
							value = rs.getString(field[0]);
						if ("a00z1".equalsIgnoreCase(field[0]) || "a00z3".equalsIgnoreCase(field[0]) || "add_flag".equalsIgnoreCase(field[0]))
							cell.setCellStyle(style);
						else
							cell.setCellStyle(style1);

					} else {
						int decwidth = item.getDecimalwidth();
						String dataType = item.getItemtype();
						String codesetid = item.getCodesetid();
						if ("N".equals(dataType) || "D".equals(dataType))
							cell.setCellStyle(style);
						else
							cell.setCellStyle(style1);

						if ("A".equals(dataType) && !"0".equals(codesetid))
							value = AdminCode.getCode(codesetid, rs.getString(field[0])) != null ? AdminCode.getCode(codesetid, rs.getString(field[0])).getCodename() : "";
						else if ("D".equals(dataType)) {
							if (rs.getDate(field[0]) == null)
								value = "";
							else
								value = DateStyle.dateformat(rs.getDate(field[0]), "yyyy-MM-dd");
						} else if ("N".equals(dataType)) {
							if (rs.getString(field[0]) == null)
								value = "";
							else
								value = PubFunc.round(rs.getString(field[0]), decwidth);
						} else {
							if (rs.getString(field[0]) == null)
								value = "";
							else
								value = rs.getString(field[0]);
						}

					}
					if ((item != null && "N".equals(item.getItemtype())) || "a00z3".equalsIgnoreCase(field[0])) {
						value = (value == null || "".equals(value.trim())) ? "" : value;
						//cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						//if(Double.parseDouble(value)>0) zxj 20140923 不导出负数不合理 jazz:4422
						if (!"".equals(value))
							cell.setCellValue(Double.parseDouble(value));
					} else
						cell.setCellValue(new HSSFRichTextString(value));
				}
			}

			for (int j = 0; j < fields.length; j++) {
				String[] field = fields[j].split(":");
				if ("appprocess".equalsIgnoreCase(field[0]))
					sheet.setColumnWidth(j, 8000);
				else
					sheet.setColumnWidth(j, 3000);
			}


			String outName = "薪资历史数据归档_" + this.userView.getUserName() + ".xls";

			FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outName);
			wb.write(fileOut);
			fileOut.close();
			this.getFormHM().put("outName", SafeCode.decode(PubFunc.encrypt(outName)));

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(wb);
		}

	}


}
