/*
 * Created on 2010-07-06
 */
package com.hjsj.hrms.transaction.addressbook;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.FileOutputStream;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>
 * Title:AddressBookExportExcelTrans
 * </p>
 * <p>
 * Description:通讯录导出Excel
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2010-07-06
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class AddressBookExportExcelTrans extends IBusiness {

	public void execute() throws GeneralException {

     	// 选中的所有字段
		ArrayList fieldList = new ArrayList();
		// 所有列名
		StringBuffer sql = new StringBuffer("select ");
		// 显示部门层数
		Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.getFrameconn());
		String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
		if (uplevel == null || uplevel.length() == 0)
			uplevel = "0";

		ContentDAO dao = new ContentDAO(this.frameconn);

		try {
			// 查询常量表，取得设置的字段
			String str_value = "";
			StringBuffer strsql = new StringBuffer();
			strsql.append("select str_value from constant ");
			strsql.append("where constant='SS_ADDRESSBOOK'");
			this.frowset = dao.search(strsql.toString());
			if (this.frowset.next()) {
				str_value = this.frowset.getString("str_value");

			}
			if (str_value == null || str_value.length() < 1) {
				// 没有设置显示字段时，只显示单位、部门、岗位、姓名
				str_value = "b0110,e0122,e01a1,a0101";
			}

			String str[] = str_value.split(",");
			for (int i = 0; i < str.length; i++) {
				String st = str[i];
				// 获得数据字典中的字段对象
				FieldItem item = DataDictionary.getFieldItem(st);
				fieldList.add(item);
				sql.append(item.getItemid() + ",");
			}

			// 添加人员序列字段名称
			sql.append("a0100 ");
			sql.append((String)this.userView.getHm().get("selfservice_sql"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 创建excel
		Workbook workbook = new HSSFWorkbook();
		// 文件名称
		String fileName = "addr_" + this.userView.getUserName() + ".xls";
		// 文件路径
		String filePath = System.getProperty("java.io.tmpdir");
		filePath += System.getProperty("file.separator") + fileName;
		FileOutputStream fileOut = null;
		ResultSet rs = null;
		try {
			// 获得第一个工作表
			Sheet sheet = workbook.createSheet("通讯录");
			// 行数标志
			int rowNum = 0;
			// 标题头“通讯录”
			writeHeader(sheet, rowNum, fieldList.size() - 1, workbook);
			rowNum++;
			// 列标题
			writeTitle(sheet, rowNum, workbook, fieldList);
			rowNum++;

			// 查询数据
			rs = dao.search(sql.toString());

			// 将所有查询到的数据写到excel中
			while (rs.next()) {
				// 封装数据
				ArrayList dataList = packageData(rs, fieldList, uplevel);
				// 将数据写到excel中
				writeData(sheet, rowNum, dataList);
				rowNum++;
			}

			// 自动调整列宽
			for (int i = 0; i < fieldList.size(); i++) {
				// sheet.autoSizeColumn(i);
				sheet.autoSizeColumn(i, true);
			}
			// 将内容写到文件中
			fileOut = new FileOutputStream(filePath);
			workbook.write(fileOut);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(rs);
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(workbook);
		}

		// 保存文件名称
		//20/3/5 xus vfs 改造
		this.getFormHM().put("fileName", PubFunc.encrypt(fileName));

	}

	/**
	 * 获得查询通讯录的sql语句
	 * 
	 * @param cloumns
	 *            列名 ,分割
	 * @param kind
	 *            类型，
	 * @param nbase
	 *            人员库
	 * @param code
	 *            组织结构代码
	 * @param select_name
	 *            查询的姓名
	 * @return String sql语句
	 */
	private String getSQl(String cloumns, String kind, String nbase,
			String code, String select_name) {

		StringBuffer sql = new StringBuffer();
		// 条件语句
		StringBuffer where = new StringBuffer();
		try {
			sql.append("select ");
			sql.append(cloumns);

			where.append(" from ");
			where.append(nbase);
			where.append("A01 where 1=1");
			if (kind == null || kind.length() <= 0)
				kind = "2";
			if (code == null || code.length() <= 0)
				code = "";
			if ("1".equals(kind)) {
				where.append(" and e0122 like '");
				where.append(code);
				where.append("%'");
			} else if ("0".equals(kind)) {
				where.append(" and e01a1 like '");
				where.append(code);
				where.append("%'");
			} else {
				where.append(" and b0110 like '");
				where.append(code);
				where.append("%'");
			}

			// 根据姓名查询
			if (select_name != null && select_name.trim().length() > 0) {

				select_name = PubFunc.getStr(select_name);
				InfoUtils infoUtils = new InfoUtils();
				String whereA0101 = infoUtils.whereA0101NoPriv(this.userView,
						this.getFrameconn(), nbase, select_name, "0");
				if (whereA0101 != null && whereA0101.length() > 0) {
					where.append(" and ");
					where.append(whereA0101);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// 添加条件
		sql.append(where.toString());
		// 排序
		sql.append(" order by a0000");

		return sql.toString();

	}

	/**
	 * 填写标题头
	 * 
	 * @param sheet
	 * @param rownum
	 *            行数
	 * @param cols
	 *            总列数
	 * @param workbook
	 */
	private void writeHeader(Sheet sheet, int rownum, int cols,
			Workbook workbook) {
		// 获得行，该行null时创建行
		Row row = sheet.getRow(rownum);
		if (row == null) {
			row = sheet.createRow(rownum);
		}
		// 创建单元格，并填写内容
		Cell cell = row.createCell(0);
		cell.setCellValue("通讯录");

		// 创建合并单元格
		if(cols > 0) {
			CellRangeAddress address = new CellRangeAddress(rownum, rownum, 0, cols);
			sheet.addMergedRegion(address);
		}

		// 设置合并的单元格样式
		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBold(true);
		font.setFontHeightInPoints((short) 20);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setFont(font);
		cell.setCellStyle(style);

		// 设置标题高度
		row.setHeightInPoints(25);
	}

	/**
	 * 填写标题
	 * 
	 * @param rs
	 *            结果集
	 * @param sheet
	 * @param rownum
	 *            行数
	 * @param fieldList
	 *            总列数
	 */
	private void writeTitle(Sheet sheet, int rownum, Workbook workbook,
			ArrayList fieldList) {
		// 获得行，该行null时创建行
		Row row = sheet.getRow(rownum);
		if (row == null) {
			row = sheet.createRow(rownum);
		}
		// 设置合并的单元格样式
		CellStyle style = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBold(true);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setFont(font);

		// 创建单元格，并填写内容
		for (int i = 0; i < fieldList.size(); i++) {
			FieldItem item = (FieldItem) fieldList.get(i);
			Cell cell = row.createCell(i);
			cell.setCellValue(item.getItemdesc());
			cell.setCellStyle(style);
		}

	}

	/**
	 * 将一条数据写到excel中
	 * 
	 * @param rs
	 * @param sheet
	 * @param rownum
	 * @param fieldList
	 * @throws SQLException
	 */
	private void writeData(Sheet sheet, int rownum, ArrayList DataList)
			throws SQLException {
		// 获得行，该行null时创建行
		Row row = sheet.getRow(rownum);
		if (row == null) {
			row = sheet.createRow(rownum);
		}

		// 创建单元格，并填写内容
		for (int i = 0; i < DataList.size(); i++) {
			Cell cell = row.createCell(i);
			cell.setCellValue((String) DataList.get(i));
		}

	}

	/**
	 * 封装数据
	 * 
	 * @param rs
	 * @param fieldList
	 * @throws SQLException
	 */
	private ArrayList packageData(ResultSet rs, ArrayList fieldList,
			String uplevel) throws SQLException {
		ArrayList list = new ArrayList();
		for (int i = 0; i < fieldList.size(); i++) {
			FieldItem item = (FieldItem) fieldList.get(i);
			if (!item.isCode()) {
				String value = "";
				if("D".equalsIgnoreCase(item.getItemtype())) {
					Date date = rs.getDate(item.getItemid());
					if(date != null) {
						java.util.Date d = new java.util.Date (date.getTime());
						String pattern = "yyyy-MM-dd";
						if(4 == item.getItemlength())
							pattern = "yyyy";
						if(7 == item.getItemlength())
							pattern = "yyyy-MM";
						if(10 == item.getItemlength())
							pattern = "yyyy-MM-dd";
						if(16 == item.getItemlength())
							pattern = "yyyy-MM-dd hh:mm";
						if(18 <= item.getItemlength())
							pattern = "yyyy-MM-dd hh:mm:ss";
						
						value = DateUtils.format(d, pattern);
					} else
						value = "";
					
				} else
					value = rs.getString(item.getItemid());
				
				list.add(value);
			} else {
				// 代码值转换
				String value = "";
				if (!"e0122".equalsIgnoreCase(item.getItemid())) {
					String code = rs.getString(item.getItemid());
					value = AdminCode.getCodeName(item.getCodesetid(), code);
				} else {
					String codeValue = rs.getString(item.getItemid());
					String codeSetId = item.getCodesetid();
					int nlevel = Integer.parseInt(uplevel);
					CodeItem codeItem = AdminCode.getCode(codeSetId, codeValue,
							nlevel);
					value = codeItem == null ? "" : codeItem.getCodename();
				}
				list.add(value);
			}
		}

		return list;

	}

}
