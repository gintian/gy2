package com.hjsj.hrms.transaction.gz.bonus.inform;

import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:ExportSummaryDataTrans.java
 * </p>
 * <p>
 * Description:奖金汇总信息导出Excel
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-07-18 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class ExportSummaryDataTrans extends IBusiness {
	public void execute() throws GeneralException {

		String bonusSet = (String) this.getFormHM().get("bonusSet");
		String sql = (String) this.getFormHM().get("sql");
		sql = SafeCode.decode(sql);
		String businessDate = (String) this.getFormHM().get("businessDate");

		HashMap fields = new HashMap();
		ArrayList list = DataDictionary.getFieldList(bonusSet, Constant.USED_FIELD_SET);
		for (int i = 0; i < list.size(); i++) {
			FieldItem fielditem = (FieldItem) list.get(i);
			fields.put(fielditem.getItemdesc(), fielditem.getItemid());
		}

		HSSFWorkbook wb = new HSSFWorkbook(); // 创建新的Excel 工作簿
		// //////////////////////////////////封皮//////////////////////////////////////
		HSSFSheet sheet = wb.createSheet("封皮");
		HSSFRow row = sheet.createRow(0);
		HSSFCell cell = row.createCell((short) 0);
		row.setHeight((short) (15.625 * 450));
		sheet.setColumnWidth((short) 0, (short) 90000);

		HSSFCellStyle style2 = wb.createCellStyle();
		style2.setFont(getContentFont(wb));
		style2.setAlignment(HorizontalAlignment.CENTER);
		style2.setVerticalAlignment(VerticalAlignment.CENTER);
		style2.setWrapText(true);

		String dateStr = this.getDateStr(businessDate);
		String conent = dateStr + "奖金\r\n发放明细表";
		cell.setCellValue(cellStr(conent));
		cell.setCellStyle(style2);
		// //////////////////////////////////汇总//////////////////////////////////////
		sheet = wb.createSheet("汇总");
		sheet.setColumnWidth((short) 0, (short) 2000);
		sheet.setColumnWidth((short) 1, (short) 6000);
		sheet.setColumnWidth((short) 2, (short) 2000);
		sheet.setColumnWidth((short) 3, (short) 4000);
		sheet.setColumnWidth((short) 4, (short) 4000);

		HSSFCellStyle styleTitle = dataStyle(wb);
		styleTitle.setAlignment(HorizontalAlignment.CENTER);
		styleTitle.setFont(getContentFont2(wb));

		HSSFCellStyle styleText = dataStyle(wb);
		styleText.setAlignment(HorizontalAlignment.LEFT);
		HSSFFont font2 = wb.createFont();
		font2.setFontHeightInPoints((short) 10);
		styleText.setFont(font2);
		styleText.setWrapText(true);

		HSSFCellStyle styleText2 = wb.createCellStyle();
		styleText2.setAlignment(HorizontalAlignment.LEFT);
		styleText2.setFont(font2);

		HSSFCellStyle styleN = dataStyle(wb);
		styleN.setAlignment(HorizontalAlignment.RIGHT);
		styleN.setWrapText(true);
		HSSFDataFormat df = wb.createDataFormat();
		styleN.setDataFormat(df.getFormat(decimalwidth(0)));

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

		row = sheet.createRow(0);
		row.setHeight((short) (15.625 * 60));
		ExportExcelUtil.mergeCell(sheet, 0, (short) 0, 0, (short) 4);
		cell = row.createCell((short) 0);
		cell.setCellValue(cellStr(dateStr + "奖金发放汇总表"));
		cell.setCellStyle(style2);

		row = sheet.createRow(1);
		row.setHeight((short) (15.625 * 30));
		ExportExcelUtil.mergeCell(sheet, 0, (short) 0, 0, (short) 1);
		cell = row.createCell((short) 0);
		cell.setCellValue(cellStr("类别：进工资部分"));
		cell.setCellStyle(styleText2);

		cell = row.createCell((short) 4);
		cell.setCellValue(cellStr("单位：人、元"));
		cell.setCellStyle(styleText2);

		row = sheet.createRow(2);
		cell = row.createCell((short) 0);
		cell.setCellValue(cellStr("序号"));
		cell.setCellStyle(styleTitle);

		cell = row.createCell((short) 1);
		cell.setCellValue(cellStr("奖金名称"));
		cell.setCellStyle(styleTitle);

		cell = row.createCell((short) 2);
		cell.setCellValue(cellStr("人数"));
		cell.setCellStyle(styleTitle);

		cell = row.createCell((short) 3);
		cell.setCellValue(cellStr("奖金金额(元)"));
		cell.setCellStyle(styleTitle);

		cell = row.createCell((short) 4);
		cell.setCellValue(cellStr("审批"));
		cell.setCellStyle(styleTitle);

		sql = sql.substring(0, sql.indexOf("order"));
		String sql1 = "select " + (String) fields.get("奖金项目") + ",count(a0100),sum(" + (String) fields.get("金额")
				+ "),max(" + (String) fields.get("奖金审批单位") + ") from (" + sql + ") b ";
		sql1 += " group by " + (String) fields.get("奖金项目") + " order by " + (String) fields.get("奖金项目");

		try {
			FieldItem fieldItem = DataDictionary.getFieldItem((String) fields.get("金额"));
			int decwidth = fieldItem.getDecimalwidth();
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RowSet rs = dao.search(sql1);
			int rowCount = 1;
			int sumPCount = 0;
			double sumMoney = 0.0;
			while (rs.next()) {
				// 奖金项目
				String f1 = rs.getString(1) == null ? "" : rs.getString(1);
				f1 = AdminCode.getCode("49", f1) != null ? AdminCode.getCode("49", f1).getCodename() : "";
				// 人数
				int f2 = rs.getInt(2);
				sumPCount += f2;
				// 金额合计
				String f3 = rs.getString(3) == null ? "0" : rs.getString(3);
				f3 = PubFunc.round(f3, decwidth);
				sumMoney += Double.parseDouble(f3);
				// 奖金审批单位
				String f4 = rs.getString(4) == null ? "" : rs.getString(4);
				f4 = AdminCode.getCode("50", f4) != null ? AdminCode.getCode("50", f4).getCodename() : "";

				row = sheet.createRow(rowCount + 2);
				cell = row.createCell((short) 0);
				cell.setCellValue(cellStr(Integer.valueOf(rowCount++).toString()));
				cell.setCellStyle(styleText);

				cell = row.createCell((short) 1);
				cell.setCellValue(cellStr(f1));
				cell.setCellStyle(styleText);

				cell = row.createCell((short) 2);
				// cell.setCellValue(cellStr(Integer.valueOf(f2).toString()));
				cell.setCellValue(f2);
				cell.setCellStyle(styleN);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);

				cell = row.createCell((short) 3);
				cell.setCellValue(Double.parseDouble(f3));
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
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);

				cell = row.createCell((short) 4);
				cell.setCellValue(cellStr(f4));
				cell.setCellStyle(styleText);
			}
			row = sheet.createRow(rowCount + 2);
			ExportExcelUtil.mergeCell(sheet, rowCount + 2, (short) 0, rowCount + 2, (short) 1);
			cell = row.createCell((short) 0);
			cell.setCellValue(cellStr("合计"));
			cell.setCellStyle(styleN);

			cell = row.createCell((short) 1);
			cell.setCellStyle(styleText);

			cell = row.createCell((short) 2);
			cell.setCellValue(sumPCount);
			cell.setCellStyle(styleN);
			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);

			cell = row.createCell((short) 3);
			// cell.setCellValue(cellStr(PubFunc.round(Double.toString(sumMoney),
			// decwidth)));
			cell.setCellValue(sumMoney);
			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
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

			cell = row.createCell((short) 4);
			cell.setCellStyle(styleText);

		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		// //////////////////////////////////明细//////////////////////////////////////
		sheet = wb.createSheet("明细");

		sheet.setColumnWidth((short) 0, (short) 2000);
		sheet.setColumnWidth((short) 1, (short) 6000);
		sheet.setColumnWidth((short) 2, (short) 6000);
		sheet.setColumnWidth((short) 3, (short) 2000);
		sheet.setColumnWidth((short) 4, (short) 4000);
		sheet.setColumnWidth((short) 5, (short) 4000);

		row = sheet.createRow(0);
		row.setHeight((short) (15.625 * 60));
		ExportExcelUtil.mergeCell(sheet, 0, (short) 0, 0, (short) 5);
		cell = row.createCell((short) 0);
		cell.setCellValue(cellStr(dateStr + "奖金发放明细表"));
		cell.setCellStyle(style2);

		row = sheet.createRow(1);
		row.setHeight((short) (15.625 * 30));
		ExportExcelUtil.mergeCell(sheet, 0, (short) 0, 0, (short) 1);
		cell = row.createCell((short) 0);
		cell.setCellValue(cellStr("类别：进工资部分"));
		cell.setCellStyle(styleText2);

		row = sheet.createRow(2);
		cell = row.createCell((short) 0);
		cell.setCellValue(cellStr("序号"));
		cell.setCellStyle(styleTitle);

		cell = row.createCell((short) 1);
		cell.setCellValue(cellStr("单位名称"));
		cell.setCellStyle(styleTitle);

		cell = row.createCell((short) 2);
		cell.setCellValue(cellStr("奖金名称"));
		cell.setCellStyle(styleTitle);

		cell = row.createCell((short) 3);
		cell.setCellValue(cellStr("人数"));
		cell.setCellStyle(styleTitle);

		cell = row.createCell((short) 4);
		cell.setCellValue(cellStr("奖金金额(元)"));
		cell.setCellStyle(styleTitle);

		cell = row.createCell((short) 5);
		cell.setCellValue(cellStr("审批"));
		cell.setCellStyle(styleTitle);

		sql1 = "select E0122  部门," + (String) fields.get("奖金项目") + ",count(a0100),sum(" + (String) fields.get("金额")
				+ "),max(" + (String) fields.get("奖金审批单位") + ") from (" + sql + ") b ";
		sql1 += " group by E0122," + (String) fields.get("奖金项目") + " order by " + (String) fields.get("奖金项目")
				+ ",E0122";

		try {
			FieldItem fieldItem = DataDictionary.getFieldItem((String) fields.get("金额"));
			int decwidth = fieldItem.getDecimalwidth();
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RowSet rs = dao.search(sql1);
			int rowCount = 2;
			int sumPCount = 0;
			double sumMoney = 0.0;
			int sumPCount2 = 0;
			double sumMoney2 = 0.0;
			String bonusItem = "00奖金项目名称00";
			int index = 1;
			while (rs.next()) {
				// 部门名称
				String f0 = rs.getString(1) == null ? "" : rs.getString(1);
				f0 = AdminCode.getCode("UM", f0) != null ? AdminCode.getCode("UM", f0).getCodename() : "";

				// 奖金项目
				String f1 = rs.getString(2) == null ? "" : rs.getString(2);
				f1 = AdminCode.getCode("49", f1) != null ? AdminCode.getCode("49", f1).getCodename() : "";
				// 人数
				int f2 = rs.getInt(3);

				// 金额合计
				String f3 = rs.getString(4) == null ? "0" : rs.getString(4);
				f3 = PubFunc.round(f3, decwidth);
				// 奖金审批单位
				String f4 = rs.getString(5) == null ? "" : rs.getString(5);
				f4 = AdminCode.getCode("50", f4) != null ? AdminCode.getCode("50", f4).getCodename() : "";

				if ("00奖金项目名称00".equals(bonusItem))
					bonusItem = f1;

				if (!bonusItem.equals(f1))// 进行上一类奖金项目的小计
				{
					row = sheet.createRow(rowCount + 2);
					ExportExcelUtil.mergeCell(sheet, rowCount + 2, (short) 0, rowCount + 2, (short) 1);
					rowCount++;
					cell = row.createCell((short) 0);
					cell.setCellValue(cellStr("小计"));
					cell.setCellStyle(styleN);

					cell = row.createCell((short) 1);
					cell.setCellStyle(styleText);

					cell = row.createCell((short) 2);
					cell.setCellStyle(styleN);

					cell = row.createCell((short) 3);
					// cell.setCellValue(cellStr(Integer.valueOf(sumPCount).toString()));
					cell.setCellValue(sumPCount);
					cell.setCellStyle(styleN);
					cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);

					cell = row.createCell((short) 4);
					// cell.setCellValue(cellStr(PubFunc.round(Double.toString(sumMoney),
					// decwidth)));
					cell.setCellValue(sumMoney);
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
					cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);

					cell = row.createCell((short) 5);
					cell.setCellStyle(styleText);
					sumPCount2 += sumPCount;
					sumMoney2 += sumMoney;
					sumPCount = 0;
					sumMoney = 0.0;
					index = 1;
				}

				sumPCount += f2;
				sumMoney += Double.parseDouble(f3);

				row = sheet.createRow(rowCount++ + 2);
				cell = row.createCell((short) 0);
				cell.setCellValue(cellStr(Integer.valueOf(index++).toString()));
				cell.setCellStyle(styleText);

				cell = row.createCell((short) 1);
				cell.setCellValue(cellStr(f0));
				cell.setCellStyle(styleText);

				cell = row.createCell((short) 2);
				cell.setCellValue(cellStr(f1));
				cell.setCellStyle(styleText);

				cell = row.createCell((short) 3);
				cell.setCellValue(f2);
				cell.setCellStyle(styleN);
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);

				cell = row.createCell((short) 4);
				cell.setCellValue(Double.parseDouble(f3));
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
				cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);

				cell = row.createCell((short) 5);
				cell.setCellValue(cellStr(f4));
				cell.setCellStyle(styleText);
			}
			// 最后一个小计
			row = sheet.createRow(rowCount + 2);
			ExportExcelUtil.mergeCell(sheet, rowCount + 2, (short) 0, rowCount + 2, (short) 1);
			cell = row.createCell((short) 0);
			cell.setCellValue(cellStr("小计"));
			cell.setCellStyle(styleN);

			cell = row.createCell((short) 1);
			cell.setCellStyle(styleText);

			cell = row.createCell((short) 2);
			cell.setCellStyle(styleN);

			cell = row.createCell((short) 3);
			cell.setCellValue(sumPCount);
			cell.setCellStyle(styleN);
			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);

			cell = row.createCell((short) 4);
			// cell.setCellValue(cellStr(PubFunc.round(Double.toString(sumMoney),
			// decwidth)));
			cell.setCellValue(sumMoney);
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
			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);

			cell = row.createCell((short) 5);
			cell.setCellStyle(styleText);
			sumPCount2 += sumPCount;
			sumMoney2 += sumMoney;
			// 合计
			row = sheet.createRow(3);
			ExportExcelUtil.mergeCell(sheet, 3, (short) 0, 3, (short) 1);
			cell = row.createCell((short) 0);
			cell.setCellValue(cellStr("合计"));
			cell.setCellStyle(styleN);

			cell = row.createCell((short) 1);
			cell.setCellStyle(styleText);

			cell = row.createCell((short) 2);
			cell.setCellStyle(styleN);

			cell = row.createCell((short) 3);
			// cell.setCellValue(cellStr(Integer.valueOf(sumPCount2).toString()));
			cell.setCellValue(sumPCount2);
			cell.setCellStyle(styleN);
			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);

			cell = row.createCell((short) 4);
			// cell.setCellValue(cellStr(PubFunc.round(Double.toString(sumMoney2),
			// decwidth)));
			cell.setCellValue(sumMoney2);
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
			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);

			cell = row.createCell((short) 5);
			cell.setCellStyle(styleText);

		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		String outName = "奖金汇总表.xls";
		try {
			FileOutputStream fileOut = new FileOutputStream(
					System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + outName);
			wb.write(fileOut);
			fileOut.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		outName = outName.replace(".xls", "#");
		this.getFormHM().put("outName", outName);
	}

	public HSSFRichTextString cellStr(String context) {

		HSSFRichTextString textstr = new HSSFRichTextString(context);
		return textstr;
	}

	public static HSSFFont getContentFont(HSSFWorkbook wb) {

		HSSFFont fontStyle = wb.createFont();
		fontStyle.setFontName("宋体");
		fontStyle.setFontHeightInPoints((short) 20);
		fontStyle.setBold(true);
		return fontStyle;
	}

	public static HSSFFont getContentFont2(HSSFWorkbook wb) {

		HSSFFont fontStyle = wb.createFont();
		fontStyle.setFontName("宋体");
		fontStyle.setFontHeightInPoints((short) 12);
		fontStyle.setBold(true);
		return fontStyle;
	}

	public String getDateStr(String date) {

		String str = "";
		int x = date.indexOf(".");
		String year = date.substring(0, x);
		String month = date.substring(x + 1, date.length());
		String[] months = { "01:一", "02:二", "03:三", "04:四", "05:五", "06:六", "07:七", "08:八", "09:九", "10:十", "11:十一",
				"12:十二" };
		String[] years = { "O", "一", "二", "三", "四", "五", "六", "七", "八", "九" };
		String yearStr = "";

		for (int i = 0; i < year.length(); i++) {
			String a = year.substring(i, i + 1);
			yearStr += years[Integer.valueOf(a).intValue()];
		}
		yearStr += "年";
		String monthStr = "";
		for (int j = 0; j < months.length; j++) {
			String m = months[j];
			String[] temp = m.split(":");
			if (temp[0].equals(month)) {
				monthStr = temp[1];
				break;
			}
		}
		monthStr += "月份";
		str = yearStr + monthStr;
		return str;
	}

	public HSSFCellStyle dataStyle(HSSFWorkbook workbook) {

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
		style.setWrapText(true);
		return style;
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
}
