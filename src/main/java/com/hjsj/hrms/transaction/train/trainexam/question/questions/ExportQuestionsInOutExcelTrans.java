package com.hjsj.hrms.transaction.train.trainexam.question.questions;

import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
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
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

public class ExportQuestionsInOutExcelTrans extends IBusiness {
	private Connection conn = null;

	public ExportQuestionsInOutExcelTrans() {
		this.conn = this.frameconn;
	}

	public void execute() throws GeneralException {
		String outName = "";

		try {
			ArrayList list = DataDictionary.getFieldList("r52", Constant.USED_FIELD_SET);
			ArrayList ls = new ArrayList();
			for (int i = 0; i < list.size(); i++) {
				FieldItem item = (FieldItem) list.get(i);
				if (!("R5200".equalsIgnoreCase(item.getItemid()) || "R5201".equalsIgnoreCase(item.getItemid())//只显示表中部分列 其余的则不显示
						|| "R5208".equalsIgnoreCase(item.getItemid()) || "R5209".equalsIgnoreCase(item.getItemid()) || "R5214".equalsIgnoreCase(item.getItemid()) || "R5215".equalsIgnoreCase(item.getItemid()) || "R5217".equalsIgnoreCase(item.getItemid()) || "R5207".equalsIgnoreCase(item.getItemid()) || "create_time".equalsIgnoreCase(item.getItemid()) || "b0110".equalsIgnoreCase(item.getItemid()))) {
					ls.add(item);
				}
			}

			outName = this.creatExcel(ls);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			this.getFormHM().put("outName", PubFunc.encrypt(outName));
		}
		// long endTime=System.currentTimeMillis();
		// System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
		// System.out.println("1时间： "+startTime+"ms");
		// System.out.println("2时间： "+endTime+"ms");

	}

	private String creatExcel(ArrayList list) throws Exception {
		HSSFWorkbook wb = new HSSFWorkbook(); // 创建新的Excel 工作簿
		HSSFSheet sheet = wb.createSheet();
		sheet.setColumnWidth(0, 32 * 1500);
		sheet.autoSizeColumn((short) 4);
		// sheet.setProtect(true);

		HSSFFont font1 = wb.createFont(); //设置样式
		font1.setFontHeightInPoints((short) 11);
//		font1.setBoldweight((short) 500);
		font1.setBold(true);
		font1.setColor(HSSFFont.COLOR_RED);
		HSSFCellStyle style2 = wb.createCellStyle();
		style2.setFont(font1);
		style2.setAlignment(HorizontalAlignment.LEFT);
		style2.setVerticalAlignment(VerticalAlignment.TOP);
		style2.setWrapText(true);
		// style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		// style2.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

		HSSFFont font2 = wb.createFont();
		font2.setFontHeightInPoints((short) 10);
		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setFont(font2);
		style1.setAlignment(HorizontalAlignment.CENTER);
		style1.setVerticalAlignment(VerticalAlignment.CENTER);
		style1.setWrapText(true);
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

		// sheet.setColumnWidth((short) 0, (short) 1000);//
		// 标识列不隐藏了，因为客户复制整行数据时候不能复制第一列的内容
		HSSFPatriarch patr = sheet.createDrawingPatriarch();

		HSSFRow row = sheet.getRow(0);
		if (row == null) {
			row = sheet.createRow(0);
		}
		row.setHeight((short) 2000);
		HSSFCell cell = null;
		HSSFComment comm = null;

		ArrayList codeCols = new ArrayList();

		for (int i = 0; i < list.size(); i++) // 遍历列
		{
			FieldItem field = (FieldItem) list.get(i);
			String fieldName = field.getItemid().toLowerCase();
			String fieldLabel = field.getItemdesc();
			int w = field.getDisplaywidth();
			if (w == 0) {
				w = 8;
			}

			if (row.getRowNum() == 0) {
				// 设置第一行的数据
				ExportExcelUtil.mergeCell(sheet, 0, (short) 0, 0, (short) ((short) list.size()+8));// 合并第一行的单元格
				StringBuffer ss = new StringBuffer("填写说明:\n" + "1、单选题答案为单个英文字母;\n" + "2、多选题答案为多个英文字母;\n" + "3、选择题默认提供8个选项，需要更多的选项时请顺序增加;\n4、若必填项(有'*'的为必填项)为空则默认此行不导入,知识点不能为空;\n5、若一个题对应多个知识点,则需要用逗号隔开;");
				cell = row.getCell(i);
				if (cell == null) {
					cell = row.createCell(i);
				}
				cell.setCellValue(cellStr(ss.toString()));
				cell.setCellStyle(style2);
				row = sheet.createRow(row.getRowNum() + 1); // 第二行开始
			}

			sheet.setColumnWidth((i), w * 500); //设置列宽
			cell = row.getCell(i);
			if (cell == null)
				cell = row.createCell(i);
			if ("create_user".equals(field.getItemid())) {
				cell.setCellValue("试题答案");
			} else if ("r5211".equals(field.getItemid())) {
				cell.setCellValue("考试时间(s)");
			} else if(field.isFillable()){
				cell.setCellValue(cellStr(fieldLabel+"*"));
			}else if("type_id".equalsIgnoreCase(field.getItemid())){
				cell.setCellValue(fieldLabel+"*");
			}else if("r5201".equalsIgnoreCase(field.getItemid())){
				cell.setCellValue(fieldLabel+"*"); //试题分类考试得分默认为必填项自动加* 
			}else if("r5213".equalsIgnoreCase(field.getItemid())){
				cell.setCellValue(fieldLabel+"*"); //考试得分如果是必填项的话生成excel会自动加* 所以导入时候理所当然的需要对这项做必填项的判断处理
			}else {
				cell.setCellValue(cellStr(fieldLabel));// 绑定列数据
			}
			comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) (i + 1), 0, (short) (i + 2), 1));
			comm.setString(new HSSFRichTextString(fieldName));// 制定ID
			cell.setCellComment(comm);

			if ("A".equalsIgnoreCase(field.getItemtype()) && (field.getCodesetid() != null && !"".equals(field.getCodesetid())) || ("N".equalsIgnoreCase(field.getItemtype()) && ("试题难度".equalsIgnoreCase(field.getItemdesc())) || ("题型编号".equals(field.getItemdesc())))) {
				codeCols.add(field.getCodesetid() + ":" + new Integer(i).toString());
			}
		}
		
		sheet.setColumnWidth((10), 4000); //设置列宽
		cell = row.getCell(list.size()); // 动态添加后面的列
		if (cell == null) {
			cell = row.createCell(list.size());
		}
		cell.setCellValue("知识点*");
		comm = patr.createComment(new HSSFClientAnchor(0, 0, 0, 1, (short) 10, 0, (short) 11, 1));
		comm.setString(new HSSFRichTextString("知识点"));// 制定ID
		cell.setCellComment(comm);
		codeCols.add("68:"+list.size());
		
        cell = row.getCell(list.size()+1); // 动态添加后面的列
        if (cell == null) {
            cell = row.createCell(list.size()+1);
        }
        cell.setCellValue("选项A");

        cell = row.getCell(list.size()+2); // 动态添加后面的列
        if (cell == null) {
            cell = row.createCell(list.size()+2);
        }
        cell.setCellValue("选项B");

        cell = row.getCell(list.size()+3); // 动态添加后面的列
        if (cell == null) {
            cell = row.createCell(list.size()+3);
        }
        cell.setCellValue("选项C");

        cell = row.getCell(list.size()+4); // 动态添加后面的列
        if (cell == null) {
            cell = row.createCell(list.size()+4);
        }
        cell.setCellValue("选项D");

        cell = row.getCell(list.size()+5); // 动态添加后面的列
        if (cell == null) {
            cell = row.createCell(list.size()+5);
        }
        cell.setCellValue("选项E");

        cell = row.getCell(list.size()+6); // 动态添加后面的列
        if (cell == null) {
            cell = row.createCell(list.size()+6);
        }
        cell.setCellValue("选项F");

        cell = row.getCell(list.size()+7); // 动态添加后面的列
        if (cell == null) {
            cell = row.createCell(list.size()+7);
        }
        cell.setCellValue("选项G");

        cell = row.getCell(list.size()+8); // 动态添加后面的列
        if (cell == null) {
            cell = row.createCell(list.size()+8);
        }
        cell.setCellValue("选项H");

		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			int rowCount = 1;
			while (rowCount < 1001) {
				row = sheet.getRow(rowCount);
				if (row == null) {
					row = sheet.createRow(rowCount);
				}
				for (int i = 0; i < list.size(); i++) {
					cell = row.getCell(i);
					if (cell == null)
						cell = row.createCell(i);
					cell.setCellStyle(style1);
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);

				}
				rowCount++;
			}
			rowCount--;

			int index = 0;

			String[] lettersUpper = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
			for (int n = 0; n < codeCols.size(); n++) {
				int m = 0;
				String codeCol = (String) codeCols.get(n);
				String[] temp = codeCol.split(":");
				String codesetid = temp[0];
				int codeCol1 = Integer.valueOf(temp[1]).intValue();
				StringBuffer codeBuf = new StringBuffer();

				if (!"UM".equals(codesetid) && !"UN".equals(codesetid) && !"@K".equalsIgnoreCase(codesetid)) {
					codeBuf.append("select count(*) from codeitem where codesetid='" + codesetid + "'");

					this.frowset = dao.search(codeBuf.toString());
					if (this.frowset.next()) {
						if (this.frowset.getInt(1) < 200) {
							codeBuf.setLength(0);
							codeBuf.append("select codesetid,codeitemid,codeitemdesc from codeitem where codesetid='" + codesetid + "'");

						} else {
							continue;
						}
					}
				} else {
					if (!"UN".equals(codesetid)) {
						m = loadorg(sheet, row, cell, index, m, dao, codesetid);
					} else if ("UN".equals(codesetid)) {
						codeBuf.setLength(0);
						codeBuf.append("select codesetid,codeitemid,codeitemdesc,grade from organization where codesetid='" + codesetid + "' and " + Sql_switcher.dateValue(PubFunc.FormatDate(new Date(), "yyyy-MM-dd")) + " between start_date and end_date order by a0000,codeitemid");

					}
				}

				if ("0".equals(codesetid)) {
					if (codeCol1 == 0) {
						row = sheet.getRow(2);
						if (row == null)
							row = sheet.createRow(2);
						cell = row.createCell(codeCol1);
						cell.setCellStyle(style1); //此处添加列样式 文本居中
						String[] s = { "容易", "较易", "中度", "难(*)", "难(**)", "难(***)", "难(****)", "难(*****)" };
						CellRangeAddressList addressList1 = new CellRangeAddressList(2, rowCount, codeCol1, codeCol1);
						DVConstraint dvConstraint1 = DVConstraint.createExplicitListConstraint(s);
						HSSFDataValidation dataValidation1 = new HSSFDataValidation(addressList1, dvConstraint1);
						sheet.addValidationData(dataValidation1);
					} else if (codeCol1 == 7) { //第八列
						codeBuf.setLength(0);
						codeBuf.append("select type_name,type_id from tr_question_type");
					}
				}

				if (!"UM".equals(codesetid) && !"@K".equalsIgnoreCase(codesetid)) {

					this.frowset = dao.search(codeBuf.toString());
					while (this.frowset.next()) {
						row = sheet.getRow(m + 0);
						if (row == null)
							row = sheet.createRow(m + 0);
						cell = row.createCell((208 + index));
						if ("UN".equals(codesetid)) {
							int grade = this.frowset.getInt("grade");
							StringBuffer sb = new StringBuffer();
							sb.setLength(0);
							for (int i = 1; i < grade; i++) {
								sb.append("  ");
							}

							cell.setCellValue(new HSSFRichTextString(sb.toString() + this.frowset.getString("codeitemdesc") + "(" + this.frowset.getString("codeitemid") + ")"));
						} else if (!"0".equals(codesetid)) {
							cell.setCellValue(new HSSFRichTextString(this.frowset.getString("codeitemdesc")));
						} else {
							cell.setCellValue(new HSSFRichTextString(this.frowset.getString("type_name")));
						}

						m++;
					}

					if (m == 0) {
						continue;
					}
					sheet.setColumnWidth((208 + index), 0);
					String strFormula = "";

					if (index <= 25) {
						strFormula = "$H" + lettersUpper[index] + "$1:$H" + lettersUpper[index] + "$" + Integer.toString(m); // 表示BA列1到m行作为下拉列表来源数据
					} else if (index > 25) {
						strFormula = "$I" + lettersUpper[index - 26] + "$1:$I" + lettersUpper[index - 26] + "$" + Integer.toString(m); // 表示BA列1到m行作为下拉列表来源数据
					}

					CellRangeAddressList addressList = new CellRangeAddressList(2, rowCount, codeCol1, codeCol1);// rowCount//只对单元格有效
					// 四个参数分别是：起始行、终止行、起始列、终止列
					DVConstraint dvConstraint = DVConstraint.createFormulaListConstraint(strFormula);// 生成下拉框内容
					HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);// 绑定下拉框和作用区域
					dataValidation.setSuppressDropDownArrow(false);
					sheet.addValidationData(dataValidation);// 对sheet页生效

					index++;
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
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

	private int loadorg(HSSFSheet sheet, HSSFRow row, HSSFCell cell, int index, int m, ContentDAO dao, String type) throws Exception {
		Statement st = null;
		ResultSet rs = null;
		DbSecurityImpl dbS = new DbSecurityImpl();
		try {
			st = conn.createStatement();
			String sql = "select codesetid,codeitemid,childid,codeitemdesc,grade from organization where codeitemid=parentid and " + Sql_switcher.dateValue(PubFunc.FormatDate(new Date(), "yyyy-MM-dd")) + " between start_date and end_date order by a0000,codeitemid";
			dbS.open(conn, sql);
			rs = st.executeQuery(sql);
			String codesetid = "";
			String codeitemid = "";
			String childid = "";
			String codeitemdesc = "";
			int grade = 0;
			while (rs.next()) {
				codesetid = rs.getString("codesetid");
				codeitemid = rs.getString("codeitemid");
				childid = rs.getString("childid");
				codeitemdesc = rs.getString("codeitemdesc");
				grade = rs.getInt("grade");
				row = sheet.getRow(m + 0);
				if (row == null)
					row = sheet.createRow(m + 0);
				cell = row.createCell((208 + index));
				StringBuffer sb = new StringBuffer();
				sb.setLength(0);
				for (int i = 1; i < grade; i++) {
					sb.append("  ");
				}
				cell.setCellValue(new HSSFRichTextString(sb.toString() + codeitemdesc + "(" + codeitemid + ")"));
				m++;
				if (!codeitemid.equals(childid))
					m = loadchild(sheet, row, cell, index, m, dao, codeitemid, type);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
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

	private int loadchild(HSSFSheet sheet, HSSFRow row, HSSFCell cell, int index, int m, ContentDAO dao, String parentid, String type) throws Exception {
		ResultSet rs = null;
		Statement st = null;
		DbSecurityImpl dbS = new DbSecurityImpl();
		try {
			String sql = null;
			st = conn.createStatement();
			if ("@K".equalsIgnoreCase(type)) {
				sql = "select codesetid,codeitemid,childid,codeitemdesc,grade from organization where parentid='" + parentid + "' and parentid<>codeitemid and " + Sql_switcher.dateValue(PubFunc.FormatDate(new Date(), "yyyy-MM-dd")) + " between start_date and end_date order by a0000,codeitemid";
			} else {
				sql = "select codesetid,codeitemid,childid,codeitemdesc,grade from organization where parentid='" + parentid + "' and codesetid<>'@K' and parentid<>codeitemid and " + Sql_switcher.dateValue(PubFunc.FormatDate(new Date(), "yyyy-MM-dd")) + " between start_date and end_date order by a0000,codeitemid";
			}
			// rs = dao.search(sql);
			dbS.open(conn, sql);
			rs = st.executeQuery(sql);
			String codesetid = "";
			String codeitemid = "";
			String childid = "";
			String codeitemdesc = "";
			int grade = 0;
			while (rs.next()) {
				codesetid = rs.getString("codesetid");
				codeitemid = rs.getString("codeitemid");
				childid = rs.getString("childid");
				codeitemdesc = rs.getString("codeitemdesc");
				grade = rs.getInt("grade");
				row = sheet.getRow(m + 0);
				if (row == null)
					row = sheet.createRow(m + 0);
				cell = row.createCell((208 + index));
				StringBuffer sb = new StringBuffer();
				sb.setLength(0);
				for (int i = 1; i < grade; i++) {
					sb.append("  ");
				}
				cell.setCellValue(new HSSFRichTextString(sb.toString() + codeitemdesc + "(" + codeitemid + ")"));
				m++;
				if (!codeitemid.equals(childid))
					m = loadchild(sheet, row, cell, index, m, dao, codeitemid, type);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
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
