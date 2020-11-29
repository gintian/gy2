package com.hjsj.hrms.transaction.browse.history;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title:ExportExcelTrans.java</p>
 * <p>Description>:ExportExcelTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Nov 23, 2010 11:56:36 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: LiWeichao</p>
 */
public class ExportExcelTrans extends IBusiness{

	public void execute() throws GeneralException {
		String fileName="";
		String outfilefields=(String) this.getFormHM().get("outfilefields");
		if(outfilefields==null||outfilefields.length()<1)
			outfilefields="";
		outfilefields=SafeCode.decode(outfilefields);
		try {
			fileName=exportExcel(outfilefields);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.getFormHM().put("outName",PubFunc.encrypt(fileName));
	}

	private String exportExcel(String outfilefields) throws Exception{
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		RowSet rs=dao.search("select dbname,pre from dbname");
		Map map=new HashMap();
		while(rs.next()){
			map.put(rs.getString("pre"), rs.getString("dbname"));
		}
		String[] offvalue=outfilefields.split("#")[1].split("`");
		String fileName = userView.getUserFullName() + "_" + PubFunc.getStrg() + ".xls";
		HSSFWorkbook workbook = new HSSFWorkbook();
		try {
			HSSFSheet sheet = workbook.createSheet();
			HSSFRow row = null;
			HSSFCell csCell = null;
			HSSFFont font = workbook.createFont();
			font.setColor(HSSFFont.COLOR_NORMAL);
			font.setBold(true);
			HSSFCellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setAlignment(HorizontalAlignment.CENTER);
			cellStyle.setBorderBottom(BorderStyle.THIN);
			cellStyle.setBottomBorderColor(IndexedColors.BLACK.index);
			cellStyle.setBorderLeft(BorderStyle.THIN);
			cellStyle.setLeftBorderColor(IndexedColors.BLACK.index);
			cellStyle.setBorderRight(BorderStyle.THIN);
			cellStyle.setRightBorderColor(IndexedColors.BLACK.index);
			cellStyle.setBorderTop(BorderStyle.THIN);
			cellStyle.setTopBorderColor(IndexedColors.BLACK.index);
			cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			cellStyle.setWrapText(false);
			HSSFFont afont = workbook.createFont();
			afont.setColor(HSSFFont.COLOR_NORMAL);
			afont.setBold(false);
			HSSFCellStyle abStyle = workbook.createCellStyle();
			abStyle.setFont(afont);
			abStyle.setAlignment(HorizontalAlignment.CENTER);
			abStyle.setBorderBottom(BorderStyle.THIN);
			abStyle.setBottomBorderColor(IndexedColors.BLACK.index);
			abStyle.setBorderLeft(BorderStyle.THIN);
			abStyle.setLeftBorderColor(IndexedColors.BLACK.index);
			abStyle.setBorderRight(BorderStyle.THIN);
			abStyle.setRightBorderColor(IndexedColors.BLACK.index);
			abStyle.setBorderTop(BorderStyle.THIN);
			abStyle.setTopBorderColor(IndexedColors.BLACK.index);
			abStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			abStyle.setWrapText(false);
			int rowIndex = 0;
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			row = sheet.createRow(rowIndex);
			for (int i = 0; i < offvalue.length; i++) {
				csCell = row.createCell(i);
				csCell.setCellStyle(cellStyle);
				HSSFRichTextString textstr = new HSSFRichTextString(offvalue[i]);
				csCell.setCellValue(textstr);
			}
			rowIndex++;

			Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.frameconn);
			String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);//显示部门层数
			uplevel = uplevel != null && uplevel.trim().length() > 0 ? uplevel : "0";
			int nlevel = Integer.parseInt(uplevel);

			String sfield = outfilefields.split("#")[0];
			String[] sfields = sfield.split(",");
			StringBuffer sqlstr = new StringBuffer();
			//4636 普通业务用户导出人员历时数据，界面空白    jingq upd 2014.10.23
			sqlstr.append("select " + sfield.substring(0, sfield.length() - 1) + " ");
			sqlstr.append(PubFunc.keyWord_reback((String) this.getFormHM().get("cond_str")));
			rs = dao.search(sqlstr.toString());
			while (rs.next()) {
				row = sheet.createRow(rowIndex);
				for (int i = 0; i < sfields.length; i++) {
					csCell = row.createCell(i);
					csCell.setCellStyle(abStyle);
					if ("create_date".equalsIgnoreCase(sfields[i])) {
						Date date = rs.getDate("create_date");
						HSSFRichTextString textstr = new HSSFRichTextString(date == null || "".equals(date) ? "" : format.format(date));
						csCell.setCellValue(textstr);
						continue;
					}
					if ("Nbase".equalsIgnoreCase(sfields[i])) {
						String nbase = (String) map.get(rs.getString(sfields[i]));
						HSSFRichTextString textstr = new HSSFRichTextString(nbase == null ? "" : nbase);
						csCell.setCellValue(textstr);
						continue;
					}
					FieldItem item = DataDictionary.getFieldItem(sfields[i]);
					if ("A".equalsIgnoreCase(item.getItemtype())) {
						String value = rs.getString(item.getItemid()) == null ? "" : rs.getString(item.getItemid());
						if (item.isCode()) {
							if ("e0122".equalsIgnoreCase(item.getItemid()) && nlevel > 0) {
								CodeItem codeItem = AdminCode.getCode("UM", value, nlevel);
								if (codeItem != null)
									value = codeItem.getCodename();

							} else
								value = AdminCode.getCodeName(item.getCodesetid(), value);

							//tianye update start
							//关联部门的指标支持指定单位（部门中查不出信息就去单位中查找）
							if ((value == null || "".equals(value)) && !"E0122".equalsIgnoreCase(item.getItemid()) && "UM".equalsIgnoreCase(item.getCodesetid())) {
								value = rs.getString(item.getItemid()) == null ? "" : rs.getString(item.getItemid());
								value = AdminCode.getCodeName("UN", value);
							}
							//end
						}
						HSSFRichTextString textstr = new HSSFRichTextString(value);
						csCell.setCellValue(textstr);
					} else if ("N".equalsIgnoreCase(item.getItemtype())) {
						double value = rs.getFloat(item.getItemid());
						csCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
						csCell.setCellValue(Double.parseDouble(PubFunc.round(value + "", item.getDecimalwidth())));
					} else if ("D".equalsIgnoreCase(item.getItemtype())) {
						if (rs.getDate(item.getItemid()) != null) {
							HSSFRichTextString textstr = new HSSFRichTextString(format.format(rs.getDate(item.getItemid())));
							csCell.setCellValue(textstr);
						} else {
							HSSFRichTextString textstr = new HSSFRichTextString("");
							csCell.setCellValue(textstr);
						}
					} else {
						String value = rs.getString(item.getItemid()) == null ? "" : rs.getString(item.getItemid());
						HSSFRichTextString textstr = new HSSFRichTextString(value);
						csCell.setCellValue(textstr);
					}
				}
				rowIndex++;
			}
			for (int i = 0; i <= sfields.length; i++) {
				sheet.setColumnWidth(Short.parseShort(String.valueOf(i)), (short) 6000);
			}
			for (int i = 0; i <= rowIndex; i++) {
				row = sheet.getRow(i);
				if (row == null)
					row = sheet.createRow(i);
				row.setHeight((short) 400);
			}
			try (FileOutputStream fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + fileName)) {
				workbook.write(fileOut);
			}
		}finally {
			PubFunc.closeResource(workbook);
		}
		return fileName;
	}
}
