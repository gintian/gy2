package com.hjsj.hrms.businessobject.kq.register;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.sql.RowSet;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExecuteKqDailyExcel {
	private Connection conn;
	private ReportParseVo parsevo;
	private UserView userView;
	private final float rate = 0.24f;
	private String self_flag;
	private String sjelement; // 制作时间 用户可以修改
	private String timeqd; // 时间 用户可以修改
	// private final int r_add_height=16; 原来
	private final int r_add_height = 54;
	private String whereIN;
	private String cardno = "";
	private boolean kqtablejudge; // 首钢增加 true展现本月出缺勤情况统计小计，否则原始
	private short paletteIndex = 9;
	private HSSFCellStyle borderStyle;
	private HSSFFont fontIndex1 = null;
	private HSSFFont fontIndex2 = null;
	private HSSFFont fontIndex3 = null;
	private HSSFFont fontIndex4 = null;
	private HSSFFont fontIndex5 = null;

	private HSSFFont fontIndex6 = null;
	private HSSFFont fontIndex7 = null;
	private HSSFFont fontIndex8 = null;
	private HSSFFont fontIndex9 = null;
	
	private HSSFCellStyle borderCellStyle = null;
	private HSSFCellStyle numberCellStyle = null;
	private HSSFCellStyle dateCellStyle = null;
	private HSSFWorkbook workbook = null;
	
	private ResultSet rs2 = null;
	private Map colormap = new HashMap();
	private HashMap a0100map = new HashMap();
	private Statement st = null;
	private String uplevel = "";
	private String sortSql = "";
	private String sortSqlDesc = "";
	private boolean noSelected = false;
	// excel中标志第几行
	private int rownum;
	private String curTab;

	public int getRownum() {
		return rownum;
	}

	public void setRownum(int rownum) {
		this.rownum = rownum;
	}

	public String getWhereIN() {
		return whereIN;
	}

	public void setWhereIN(String whereIN) {
		this.whereIN = whereIN;
	}

	public String getSelf_flag() {
		if (this.self_flag == null || this.self_flag.length() <= 0) {
            this.self_flag = "";
        }
		return this.self_flag;
	}

	public void setSelf_flag(String self_flag) {
		this.self_flag = self_flag;
	}

	public String getSjelement() {
		return sjelement;
	}

	public void setSjelement(String sjelement) {
		this.sjelement = sjelement;
	}

	public String getTimeqd() {
		return timeqd;
	}

	public void setTimeqd(String timeqd) {
		this.timeqd = timeqd;
	}

	public ExecuteKqDailyExcel() {
	}

	public ExecuteKqDailyExcel(Connection conn) {
		this.conn = conn;
	}

	/**
	 * @param 数据库前缀
	 *            userbase
	 * @param 级别
	 *            code
	 * @param kind
	 *            1，部门，2单位 kind
	 * @param 考勤期间
	 *            coursedate
	 * @param 该考勤打印参数集
	 *            parsevo
	 * @param 用户参数集
	 *            userView
	 */
	public String executeExcel(String code, String kind, String coursedate, ReportParseVo parsevo, UserView userView, HashMap formHM, String dbty) throws GeneralException {

		Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
		uplevel = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
		if (uplevel == null || uplevel.length() == 0) {
            uplevel = "0";
        }

		this.parsevo = parsevo;
		this.userView = userView;

		if ((!"#dept[1]".equalsIgnoreCase(parsevo.getBody_dept())) && (!"#pos[1]".equalsIgnoreCase(parsevo.getBody_pos())) && (!"#gh[1]".equalsIgnoreCase(parsevo.getBody_gh()))
				&& (!"#kqfu[1]".equalsIgnoreCase(parsevo.getBody_kqfu())) && (!"#tjxm[1]".equalsIgnoreCase(parsevo.getBody_tjxm()))) {
			this.noSelected = true;

		}

		// 默认第一行
		this.rownum = 0;
		KqViewDailyBo kqView = new KqViewDailyBo();
		this.kqtablejudge = kqView.getkqtablejudge(this.conn); // 考勤表：true=展现本月出缺勤情况统计小计
		// false=不展现
		ArrayList kqq03list = kqView.getKqBookItemList(); // 本月出缺勤情况统计小计

		KqReportInit kqReprotInit = new KqReportInit(this.conn);
		ArrayList item_list = kqReprotInit.getKq_Item_listPdf();// 考勤项目参数
		//判断数据是否位归档数据
		kqReprotInit.checkArcData(parsevo.getValue().trim(), coursedate);

		if ("self".equals(this.getSelf_flag())) {
			if (this.userView.getUserDeptId() != null && this.userView.getUserDeptId().length() > 0) {
                this.whereIN = " from q03 WHERE e0122 like '" + this.userView.getUserDeptId() + "%'";
            } else if (this.userView.getUserOrgId() != null && this.userView.getUserOrgId().length() > 0) {
                this.whereIN = " from q03 WHERE b0110 like '" + this.userView.getUserOrgId() + "%'";
            } else {
                this.whereIN = " from q03  WHERE a0100='" + this.userView.getA0100() + "' and nbase='" + this.userView.getDbname() + "'";
            }
		}
		KqParameter para = new KqParameter(new HashMap(), this.userView, code, this.conn);
		this.cardno = para.getG_no();
		
        
		KqViewDailyBo kqViewDailyBo = new KqViewDailyBo(this.conn, this.parsevo, this.userView);
		kqViewDailyBo.setCardno(this.cardno);
		kqViewDailyBo.setSelf_flag(this.getSelf_flag());
		kqViewDailyBo.setCurTab(kqReprotInit.getCurTab());
	    this.curTab = kqViewDailyBo.getCurTab();
		
		ArrayList datelist = kqViewDailyBo.getDateList(this.conn, coursedate);
		// ArrayList kq_dbase_list = userView.getPrivDbList(); //这取的是全部人员库
		// 这里数据考勤表有个错误，这里得到的是全部人员库，但是应该是考勤员权限下的人员库才对
		/** 得到考勤权限下的人员库 **/
		KqUtilsClass kqUtilsClass = new KqUtilsClass(this.conn, this.userView);
		ArrayList kq_dbase_list = kqUtilsClass.getKqPreList();
		/** 结束 **/
		int recordNum = kqViewDailyBo.getAllRecordNum(code, kind, datelist, kq_dbase_list);// 总纪录数
		ArrayList keylist = new ArrayList();
		keylist.add("a0100");
		keylist.add("nbase");
		kqViewDailyBo.setSelf_flag(this.getSelf_flag());
		kqViewDailyBo.setWhereIN(this.getWhereIN());
		kqViewDailyBo.setDbtype(dbty);
		ArrayList a0100list = null;

		kqViewDailyBo.setSortItem(sortSql);

		if (!"all".equals(dbty) && dbty.length() > 0) {
			a0100list = kqViewDailyBo.getA0100Listusr(code, kind, datelist, 1, recordNum, kq_dbase_list, keylist, dbty, recordNum);// 所有人员信息
		}
		else {
			a0100list = kqViewDailyBo.getA0100List(code, kind, datelist, 1, recordNum, kq_dbase_list, keylist, recordNum);// 所有人员信息
		}
		String a0100sql = kqViewDailyBo.getA0100sql();

		a0100list = kqViewDailyBo.getList(a0100sql);
		a0100sql = a0100sql.substring(0, a0100sql.lastIndexOf("order"));
		String[] codeitem = kqViewDailyBo.getCodeItemDesc(code);// 部门信息

		String context = "";
		if (parsevo.getTitle_fw() != null && parsevo.getTitle_fw().length() > 0) {
			context = parsevo.getTitle_fw();
		} else {
			context = parsevo.getName();
		}
		String url = userView.getUserName()+ "_" + context.trim() + ".xls";

		HSSFWorkbook workbook = new HSSFWorkbook();
		OutputStream writer = null;
		try {
			writer = new FileOutputStream(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + url);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// 总共的列数
		int maxColumn = 2;
		// 保存为每一列的宽度
		// 考勤表：true=展现本月出缺勤情况统计小计 false=不展现
		if (this.kqtablejudge && ("#tjxm[1]".equalsIgnoreCase(parsevo.getBody_tjxm()) || this.noSelected)) {
			maxColumn += kqq03list.size();
		}

		if ("#dept[1]".equalsIgnoreCase(parsevo.getBody_dept()) || this.noSelected) {
			maxColumn++;
		}

		if ("#pos[1]".equalsIgnoreCase(parsevo.getBody_pos()) || this.noSelected) {
			if (!"1".equals(para.getKq_orgView_post())) {
                maxColumn++;
            }
		}

		if ("#gh[1]".equalsIgnoreCase(parsevo.getBody_gh()) || this.noSelected) {
			maxColumn++;
		}

		if ("#kqfu[1]".equalsIgnoreCase(parsevo.getBody_kqfu()) || this.noSelected) {
			maxColumn += datelist.size();
		}
		// 创建一个新的工作表
		HSSFSheet sheet = workbook.createSheet();
		try {
			int start_record = 0;
			int end_record = 0;
			// 设置标题
			getTableTitle(workbook, sheet, maxColumn);
			getTableHead(workbook, sheet, maxColumn, codeitem, coursedate, kqq03list);
			getTableBodyHead(workbook, sheet, datelist, kqq03list, maxColumn);
			start_record = getTableBodyNew(code, kind, workbook, sheet, datelist, a0100list, start_record, end_record, item_list, coursedate, kqq03list, a0100sql, maxColumn);
			getTileHtml(workbook, sheet, code, kind, item_list, maxColumn);
			// 设置每列的宽度
			try {
				for (int i = 0; i < maxColumn; i++) {
					sheet.autoSizeColumn(i);
				}
			} catch (Exception e) {
				//TODO 自动设置列宽有时报数组越界错误 -32695 原因不明，待查
			}
			workbook.write(writer);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
		    PubFunc.closeIoResource(writer);
		}
		return url;
	}

	/**
	 * 创建excle表头信息
	 * 
	 * @param workbook
	 * @param sheet
	 * @param maxColumn
	 */
	public void getTableTitle(HSSFWorkbook workbook, HSSFSheet sheet, int maxColumn) {

		if (maxColumn < 6) {
			maxColumn = 6;
		}

		// 获得标题样式
		HSSFFont font = getFont(parsevo.getTitle_fn(), parsevo.getTitle_fb(), parsevo.getTitle_fi(), parsevo.getTitle_fu(), parsevo.getTitle_fz(), "", workbook);
		// 标题内容
		String context = "";
		if (parsevo.getTitle_fw() != null && parsevo.getTitle_fw().length() > 0) {
			context = parsevo.getTitle_fw();
		}
		else {
			context = parsevo.getName();
		}

		try {

			// 获得行，该行null时创建行
			HSSFRow row = sheet.getRow(this.rownum);
			if (row == null) {
				row = sheet.createRow(this.rownum);
			}
			// 创建单元格，并填写内容
			HSSFCell cell = row.createCell(0);
			cell.setCellValue(context);

			// 创建合并单元格
			sheet.addMergedRegion(new CellRangeAddress(this.rownum, this.rownum, 0, maxColumn - 1));

			// 设置合并的单元格样式
			HSSFCellStyle style = workbook.createCellStyle();
			style = this.setHSSFCellStyle(style);
			style.setFont(font);
			style.setWrapText(true);
			style.setAlignment(HorizontalAlignment.CENTER);
			cell.setCellStyle(style);
			for (int i = 1; i < maxColumn; i++) {
				cell = row.createCell(i);
				cell.setCellValue("");
				cell.setCellStyle(style);
			}
			float title_h = Float.parseFloat(parsevo.getTitle_h());
			// 设置标题高度
			row.setHeightInPoints(title_h);

			// 将行数加1
			this.rownum++;

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 建立表头信息
	 */
	public void getTableHead(HSSFWorkbook workbook, HSSFSheet sheet, int maxColumn, String[] codeitem, String coursedate, ArrayList kqq03list) {
		if (maxColumn < 6) {
			maxColumn = 6;
		}
		// 生成字体样式
		HSSFFont font = getFont(parsevo.getHead_fn(), parsevo.getHead_fb(), parsevo.getHead_fi(), parsevo.getHead_fu(), parsevo.getHead_fz(), "", workbook);

		// 表头行共几列
		int other = 0;

		if (this.kqtablejudge && ("#tjxm[1]".equalsIgnoreCase(parsevo.getBody_tjxm()) || this.noSelected)) {
			other = maxColumn - kqq03list.size();
		}
		else {
			other = maxColumn;
		}
		if (other < 3) {
			other = 3;
		}

		// 获得行
		HSSFRow row = sheet.getRow(this.getRownum());
		if (row == null) {
			row = sheet.createRow(this.getRownum());
		}

		try {
			/** 时间显示，格式如：2010-02 (2010.02.01~2010.02.28) wangy **/
			KqViewDailyBo kqView = new KqViewDailyBo(this.conn);
			ArrayList datelist = kqView.getDateList(this.conn, coursedate);
			CommonData vo = (CommonData) datelist.get(0);
			String start_date = vo.getDataName();
			vo = (CommonData) datelist.get(datelist.size() - 1);
			String end_date = vo.getDataName();
			/** 结束 **/
			HSSFCell cell = row.createCell(0);
			// 高度
			String dv_content = "";
			if (codeitem[1] == null || codeitem[1].length() <= 0) {
				dv_content = "   单位：";
			}
			else {
				dv_content = "   单位：" + codeitem[1];
			}
			// 合并单元格
			sheet.addMergedRegion(new CellRangeAddress(this.rownum, this.rownum, 0, other / 3 - 1));
			cell.setCellValue(dv_content);
			HSSFCellStyle style = workbook.createCellStyle();
			style = this.setHSSFCellStyle(style);
			style.setWrapText(true);
			style.setFont(font);
			cell.setCellStyle(style);
			for (int i = 1; i < other / 3; i++) {
				cell = row.createCell(i);
				cell.setCellValue("");
				cell.setCellStyle(style);
			}
			String bm_content = "";
			if (codeitem[0] == null || codeitem[0].length() <= 0) {
				bm_content = "   部门：";
			}
			else {
				bm_content = "   部门：" + codeitem[0];
			}
			// 创建"部门："一单元格
			cell = row.createCell(1 * other / 3);
			cell.setCellValue(bm_content);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, other / 3, (2 * other) / 3 - 1));
			cell.setCellStyle(style);
			for (int i = other / 3 + 1; i < (2 * other) / 3; i++) {
				cell = row.createCell(i);
				cell.setCellValue("");
				cell.setCellStyle(style);
			}

			if (this.kqtablejudge && ("#tjxm[1]".equalsIgnoreCase(parsevo.getBody_tjxm()) || this.noSelected)) {

				HSSFCellStyle style2 = workbook.createCellStyle();
				style2.setFont(font);
				// String content = "      " + kq_year + " 年 第" + kq_duration
				// + " 期间";
				String content = "      " + coursedate + " (" + start_date + "~" + end_date + ")";
				cell = row.createCell(2 * other / 3);
				cell.setCellValue(content);
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, (2 * other) / 3, other - 1));
				cell.setCellStyle(style);
				for (int i = (2 * other) / 3 + 1; i < other; i++) {
					cell = row.createCell(i);
					cell.setCellValue("");
					cell.setCellStyle(style);
				}
				String content2 = " 本月出缺勤情况统计小计 ";
				cell = row.createCell(other);
				cell.setCellValue(content2);
				// 合并单元格
				sheet.addMergedRegion(new CellRangeAddress(this.rownum, this.rownum, other, maxColumn - 1));
				cell.setCellStyle(style2);
				for (int i = other + 1; i < maxColumn; i++) {
					cell = row.createCell(i);
					cell.setCellValue("");
					cell.setCellStyle(style);
				}
			}
			else {

				String content = "      " + coursedate + " (" + start_date + "~" + end_date + ")";
				cell = row.createCell(2 * other / 3);
				cell.setCellValue(content);

				// 合并单元格
				sheet.addMergedRegion(new CellRangeAddress(this.rownum, this.rownum, 2 * other / 3, other - 1));
				cell.setCellStyle(style);
			}

			// 换行
			int par_column = getNumColumn_3(parsevo.getHead_c(), parsevo.getHead_p(), parsevo.getHead_e(), parsevo.getHead_u(), parsevo.getHead_d(), parsevo.getHead_t());

			// 下一行
			this.rownum++;

			if (par_column > 0) {
				// 生成字体样式
				HSSFFont font_head = getFont(parsevo.getHead_fn(), parsevo.getHead_fb(), parsevo.getHead_fi(), parsevo.getHead_fu(), parsevo.getHead_fz(), "", workbook);
				HSSFCellStyle cellStyle = workbook.createCellStyle();
				cellStyle = this.setHSSFCellStyle(cellStyle);
				cellStyle.setFont(font_head);
				cellStyle.setWrapText(true);
				int ave = maxColumn / par_column;

				int i = 1;
				// 制作人
				if ("#e".equals(parsevo.getHead_e().trim())) {

					String e_str = "    制作人: " + this.userView.getUserFullName();
					row = this.getRow(sheet, this.getRownum());
					cell = row.createCell((i - 1) * ave);
					cell.setCellValue(e_str);
					cell.setCellStyle(cellStyle);
					i++;

				}
				// 制作人单位
				if ("#u".equals(parsevo.getHead_u().trim())) {
					String u_code = "";
					if (!userView.isSuper_admin()) {
						if (userView.getUserOrgId() != null && userView.getUserOrgId().trim().length() > 0) {
							u_code = userView.getUserOrgId();
						}
						else {
							u_code = RegisterInitInfoData.getKqPrivCodeValue(userView);
						}
					}
					KqViewDailyBo kqViewDailyBo = new KqViewDailyBo(this.conn, this.parsevo, this.userView);
					String[] u_codeitem = kqViewDailyBo.getCodeItemDesc(u_code);
					String u_str = " 制作人单位: " + u_codeitem[0];
					row = this.getRow(sheet, this.getRownum());
					cell = row.createCell((i - 1) * ave);
					cell.setCellValue(u_str);
					cell.setCellStyle(cellStyle);
					i++;
				}
				/** 制作日期* */
				if ("#d".equals(parsevo.getHead_d().trim())) {
					String d_str = "  制作日期: " + PubFunc.getStringDate("yyyy.MM.dd");
					row = this.getRow(sheet, this.getRownum());
					cell = row.createCell((i - 1) * ave);
					cell.setCellValue(d_str);
					cell.setCellStyle(cellStyle);
					i++;
				}

				/** 制作时间* */
				if ("#t".equals(parsevo.getHead_t().trim())) {
					String t_str = "  时间: " + PubFunc.getStringDate("HH:mm:ss");
					row = this.getRow(sheet, this.getRownum());
					cell = row.createCell((i - 1) * ave);
					cell.setCellValue(t_str);
					cell.setCellStyle(cellStyle);
					i++;

				}

				/** 合并单元格 */
				for (int j = 1; j <= i - 1; j++) {
					CellRangeAddress addresses = null;
					if (j == i - 1) {
						sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, (j - 1) * (maxColumn / (i - 1)), maxColumn - 1));
						for (int c = (j - 1) * (maxColumn / (i - 1)) + 1; c < maxColumn; c++) {
							cell = row.createCell(c);
							cell.setCellValue("");
							cell.setCellStyle(style);
						}

					}
					else {
						sheet.addMergedRegion(new CellRangeAddress( rownum, rownum, (j - 1) * (maxColumn / (i - 1)), j * (maxColumn / (i - 1)) - 1));
						for (int c = (j - 1) * (maxColumn / (i - 1)) + 1; c < j * (maxColumn / (i - 1)); c++) {
							cell = row.createCell(c);
							cell.setCellValue("");
							cell.setCellStyle(style);
						}

					}
				}
				// 下一行
				this.rownum++;
				// 设置行高
				// row.setHeightInPoints(head_h);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建行
	 * 
	 * @param sheet
	 *            工作薄对象
	 * @param rowNum
	 *            行索引
	 * @return HSSFRow
	 */
	private HSSFRow getRow(HSSFSheet sheet, int rowNum) {
		HSSFRow row = sheet.getRow(rowNum);
		if (row == null) {
			row = sheet.createRow(rownum);
		}
		return row;
	}

	/**
	 * Body内行信息
	 * 
	 */
	private int getTableBodyNew(String code, String kind, HSSFWorkbook workbook, HSSFSheet sheet, ArrayList datelist, ArrayList a0100list, int start_record, int end_record, ArrayList item_list,
			String coursedate, ArrayList kqq03list, String a0100sql, int maxColnum) throws GeneralException {
		float body_h = Float.parseFloat(parsevo.getBody_fz()) + r_add_height;
		// 生成字体样式
		HSSFFont font = getFont(parsevo.getBody_fn(), parsevo.getBody_fb(), parsevo.getBody_fi(), parsevo.getBody_fu(), parsevo.getBody_fz(), "", workbook);
		HSSFCellStyle style = workbook.createCellStyle();
		style = this.setHSSFCellStyle(style);
		style.setFont(font);
		int row_h = 0;
		// 获得查询的sql语句
		CommonData start_vo = (CommonData) datelist.get(0);
		String start_date = start_vo.getDataName();
		CommonData end_vo = (CommonData) datelist.get(datelist.size() - 1);
		String end_date = end_vo.getDataName();

		// 获得列字符窜
		StringBuffer column = new StringBuffer();
		ArrayList columnlist = new ArrayList();
		ArrayList fielditemlist = DataDictionary.getFieldList("q03", Constant.USED_FIELD_SET);
		for (int i = 0; i < fielditemlist.size(); i++) {
			FieldItem fielditem = (FieldItem) fielditemlist.get(i);
			if (!"i9999".equals(fielditem.getItemid())) {
				column.append(fielditem.getItemid().toString());
				column.append(",");
				columnlist.add(fielditem.getItemid());
			}

		}

		for (int i = 0; i < kqq03list.size(); i++) {
			FieldItem fielditem = (FieldItem) kqq03list.get(i);
			if (!"i9999".equals(fielditem.getItemid())) {
				column.append("" + fielditem.getItemid() + ",");
				columnlist.add(fielditem.getItemid());
			}
		}

		// 获得列名称，
		StringBuffer columStr = new StringBuffer();
		for (int t = 0; t < columnlist.size(); t++) {
			if (t == 0) {
				columStr.append("a.");
				columStr.append((String) columnlist.get(t));
			}
			else {

				columStr.append(",a.");
				columStr.append((String) columnlist.get(t));
			}

		}

		ResultSet rs = null;

		StringBuffer sql_one = new StringBuffer();
		sql_one.append("select ");
		sql_one.append(columStr.toString());
		sql_one.append(" from (");
		sql_one.append(a0100sql);
		sql_one.append(") b left join  q05");
		if (this.curTab.toLowerCase().contains("_arc")) {
            sql_one.append("_arc");
        }

		sql_one.append(" a on b.a0100=a.a0100 and b.nbase=a.nbase");
		sql_one.append(" and a.Q03Z0='");
		sql_one.append(coursedate);
		sql_one.append("' ");

		StringBuffer sql_o = new StringBuffer();
		for (int i = 0; i < a0100list.size(); i++) {
			String[] a0100 = (String[]) a0100list.get(i);
			String a0 = a0100[0];
			if (i % 800 == 0) {
				if (i == 0) {
					sql_o.append(" a.a0100 in ('");
					sql_o.append(a0);
					sql_o.append("'");
					if (i == a0100list.size() - 1) {
						sql_o.append(" )");
					}
				}
				else {
					if (i == a0100list.size() - 1) {
						sql_o.append(" )");
					}
					else {
						sql_o.append(" )or a.a0100 in ('");
						sql_o.append(a0);
						sql_o.append("'");
					}
				}
			}
			else {
				sql_o.append(",'");
				sql_o.append(a0);
				sql_o.append("'");
				if (i == a0100list.size() - 1) {
					sql_o.append(" )");
				}
			}
		}
		if (sql_o.length() > 0) {
			sql_one.append(" and ( ");
			sql_one.append(sql_o.toString());
			sql_one.append(")");
		}
		// sql_one.append(") order by b.b0110,b.e0122,b.e01a1,b.a0100,b.nbase");

		String sort = "";
		String desc = "";
		//SortBo bo = new SortBo(this.conn, this.userView);
		//if (sortSql != null && sortSql.length() > 0 && !"not".equalsIgnoreCase(sortSql)) {
		//	sort = bo.getSortSql(sortSql, "b");
			//desc = bo.getSortSql(sortSql, "b", false);
		//} else if (bo.isExistSort()) {
		//	sort = bo.getSortSqlTable("b");
			//desc = bo.getSortSqlTable("b", false);
		//} else {
			sort = "order by b.dbid,b.a0000,q03z0";
			//desc = "order by b.dbid desc,b.a0000 desc";
		//}

		sql_one.append(sort);
		String sql = this.selcet_kq_one_emp(start_date, end_date, code, kind, column.toString(), a0100sql, sql_o.toString());
		Statement stt = null;
		DbSecurityImpl dbs = new DbSecurityImpl();
		try {
			if (this.kqtablejudge && ("#tjxm[1]".equalsIgnoreCase(parsevo.getBody_tjxm()) || this.noSelected)) {
				String onsql = sql_one.toString();

				ContentDAO dao = new ContentDAO(conn);
				rs2 = dao.search(onsql);
			}

			stt = this.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			dbs.open(conn, sql);
			rs = stt.executeQuery(sql);
			
			if (!rs.next()) {
                return end_record;
            }

			boolean find = false;
			boolean isCurrentEmp = false;
			for (int i = 0; i < a0100list.size(); i++) {
				rs.beforeFirst();
				
				String a0100[] = (String[]) a0100list.get(i);
				HashMap kq_item_map = new HashMap();// 考勤期间每天要打印的图标集合
				HashMap kq_item_all = querryKq_item();
				
				find = false;
				isCurrentEmp = false;
				while (rs.next()) {
					//结果集当前记录是否为当前人员的数据
					isCurrentEmp = rs.getString("a0100").equals(a0100[0]) && rs.getString("nbase").equalsIgnoreCase(a0100[2]);
					if (isCurrentEmp) {
                        find = true;
                    }
					
					//不是当前人员，但是之前找到过，由于结果集中每个人的数据都紧挨在一起，那么说明当前人员数据已找完
					if (!isCurrentEmp && find) {
                        break;
                    }
					
					//不是当前人员数据，继续找下一条记录
				    if (!isCurrentEmp) {
                        continue;
                    }
				
					// 保存单元格内容和字体（当单元格内有多个内容时）
					ArrayList list = new ArrayList();// 有效考勤规则的集合
					String q03z0 = rs.getString("q03z0");
					int num_r = 0;// 考勤规则有效的个数
					int cellnum = datelist.size() + 6;
					// paragraph=new Paragraph();
					String values = "";
					double q03z2 = 0;// 实出勤
					String[] q03z2_item = null;
					for (int j = 0; j <= fielditemlist.size(); j++) {
						if (j < fielditemlist.size()) {
							FieldItem fielditem = (FieldItem) fielditemlist.get(j);
							if ("N".equals(fielditem.getItemtype()) && !"i9999".equals(fielditem.getItemid())) {
								if ("q03z1".equalsIgnoreCase(fielditem.getItemid())) {
									continue;
								} 
								
								String value = rs.getString(fielditem.getItemid());
								if (value == null || value.length() <= 0) {
									value = "0";
								}
								
								if ("实出勤".equals(fielditem.getItemdesc()) || fielditem.getItemdesc().indexOf("出勤率") != -1) {
									q03z2 = Double.parseDouble(value);
									if (q03z2 != 0) {
										q03z2_item = KqReportInit.getKq_Item(fielditem.getItemid().toString(), item_list);

										continue;
									}
								} else {
									double dv = Double.parseDouble(value);
									if (dv != 0) {
										String[] kq_item = KqReportInit.getKq_Item(fielditem.getItemid().toString(), item_list);
                                        //没有定义图标的不处理
                                        if (kq_item[0] == null) {
                                            continue;
                                        }
                                        
										HSSFFont font_1 = getColorFont(parsevo.getBody_fn(), parsevo.getBody_fb(), parsevo.getBody_fi(),
												parsevo.getBody_fu(), parsevo.getBody_fz(), kq_item[1], workbook, kq_item[0]);
										// paragraph.add(new
										// Paragraph(kq_item[0],font_1));//多个
										ArrayList li = new ArrayList();
										li.add(kq_item[0]);
										li.add(font_1);
										list.add(li);
										if (kq_item_all.containsKey(fielditem.getItemid().toLowerCase())) {
											num_r++;
										}
										continue;
									}
								}
							}
							
                            if (!"N".equals(fielditem.getItemtype()) && !"q03z0".equalsIgnoreCase(fielditem.getItemid())
                                    && !"nbase".equalsIgnoreCase(fielditem.getItemid()) && !"a0100".equalsIgnoreCase(fielditem.getItemid())
                                    && !"b0110".equalsIgnoreCase(fielditem.getItemid()) && !"e0122".equalsIgnoreCase(fielditem.getItemid())
                                    && !"e01a1".equalsIgnoreCase(fielditem.getItemid()) && !"q03z3".equalsIgnoreCase(fielditem.getItemid())
                                    && !"q03z5".equalsIgnoreCase(fielditem.getItemid()) && !"state".equalsIgnoreCase(fielditem.getItemid())
                                    && !"a0101".equalsIgnoreCase(fielditem.getItemid()) 
                                    && !"modtime".equalsIgnoreCase(fielditem.getItemid()) && !"modusername".equalsIgnoreCase(fielditem.getItemid())) {

								String sr = rs.getString(fielditem.getItemid());
								if (sr != null && sr.length() > 0 && !"0".equals(sr)) {
									String[] kq_item = KqReportInit.getKq_Item(fielditem.getItemid(), item_list);
                                    if (kq_item[0] == null) {
                                        continue;
                                    }
                                    
									HSSFFont font_1 = getColorFont(parsevo.getBody_fn(), parsevo.getBody_fb(), parsevo.getBody_fi(), parsevo
											.getBody_fu(), parsevo.getBody_fz(), kq_item[1], workbook, kq_item[0]);
									ArrayList one_list = new ArrayList();
									// paragraph.add(new
									// Paragraph(kq_item[0],font_1));//多个
									ArrayList li = new ArrayList();
									li.add(kq_item[0]);
									li.add(font_1);
									list.add(li);
									// num_r++;
								}

							}
						} else if (j == fielditemlist.size() && num_r > 0) {// 指标循环结束，且有效指标数大于0
							ArrayList list2 = new ArrayList();
							list2.add(list);
							list2.add(new Double(1));
							kq_item_map.put(q03z0, list2);
							break;
						} else if (j == fielditemlist.size() && q03z2 > 0)// 指标循环结束，且实出勤大于0
						{
							// 添加实出勤符号 --2010.4.10
							HSSFFont font_1 = getColorFont(parsevo.getBody_fn(), parsevo.getBody_fb(), parsevo.getBody_fi(),
									parsevo.getBody_fu(), parsevo.getBody_fz(), q03z2_item[1], workbook, q03z2_item[0]);
							ArrayList li = new ArrayList();
							li.add(q03z2_item[0]);
							li.add(font_1);
							list.add(li);
							// HSSFCellStyle style2 = workbook.createCellStyle();
							// style2 = this.setHSSFCellStyle(style2);
							// style2.setWrapText(true);
							// style2.setFont(font_1);
							ArrayList list2 = new ArrayList();
							list2.add(list);
							list2.add(new Double(1));
							kq_item_map.put(q03z0, list2);

						}

						if (num_r > 0) {
							if (row_h < num_r) {
                                row_h = num_r;
                            }
						}
					}

					if (num_r == 0) {
						if (list != null && list.size() > 0) {
							ArrayList list2 = new ArrayList();
							list2.add(list);
							list2.add(new Double(1));
							kq_item_map.put(q03z0, list2);
						}
					}
				}

				row_h = getA0100DataNew(workbook, sheet, code, kind, a0100, datelist, body_h, item_list, style, i, coursedate, kqq03list, rs,
						kq_item_map, maxColnum);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		    dbs.close(conn);
			KqUtilsClass.closeDBResource(rs);
			KqUtilsClass.closeDBResource(rs2);
			KqUtilsClass.closeDBResource(st);
			KqUtilsClass.closeDBResource(stt);
		}

		return end_record;
	}

	/**
	 * 通过一个员工编号得到该员工考勤期间的数据 coursedate 考勤月 如：2009-1
	 */
	public int getA0100DataNew(HSSFWorkbook workbook, HSSFSheet sheet, String code, String kind, String a0100[], ArrayList datelist, float body_h, ArrayList item_list, HSSFCellStyle style, int num,
			String coursedate, ArrayList kqq03list, ResultSet rs, HashMap kq_item_map, int maxColnum) throws GeneralException {

		// 获得行
		HSSFRow row = this.getRow(sheet, this.getRownum());
		// 单元格
		HSSFCell cell = null;
		int col = 0;
		// 序号
		if (maxColnum < 6) {
			cell = row.createCell(col * (6 / maxColnum));
		}
		else {
			cell = row.createCell(col);
		}
		cell.setCellValue(num + 1 + "");
		cell.setCellStyle(style);
		style.setWrapText(true);
		col++;

		// 人员库
		// String
		// dbname=AdminCode.getCode("@@",a0100[2])!=null?AdminCode.getCode("@@",a0100[2]).getCodename():"";
		// cell = row.createCell(1);
		// cell.setCellValue(dbname);
		// cell.setCellStyle(style);
		// style.setWrapText(true);

		// 岗位
		if ("#dept[1]".equalsIgnoreCase(parsevo.getBody_dept()) || this.noSelected) {
			String dd = AdminCode.getCode("UM", a0100[4]) != null ? AdminCode.getCode("UM", a0100[4], Integer.parseInt(uplevel)).getCodename() : "";
			if (maxColnum < 6) {
				cell = row.createCell(col * (6 / maxColnum));
			}
			else {
				cell = row.createCell(col);
			}
			cell.setCellValue(dd);
			cell.setCellStyle(style);
			col++;
		}
		// 职位
		if ("#pos[1]".equalsIgnoreCase(parsevo.getBody_pos()) || this.noSelected) {
			KqParameter para = new KqParameter();
			if (!"1".equals(para.getKq_orgView_post())) {
				String e01 = AdminCode.getCode("@K", a0100[5]) != null ? AdminCode.getCode("@K", a0100[5].trim()).getCodename() : "";
				if (maxColnum < 6) {
					cell = row.createCell(col * (6 / maxColnum));
				}
				else {
					cell = row.createCell(col);
				}
				cell.setCellValue(e01);
				cell.setCellStyle(style);
				col++;
			}
		}

		// 姓名
		if (maxColnum < 6) {
			cell = row.createCell(col * (6 / maxColnum));
		}
		else {
			cell = row.createCell(col);
		}
		cell.setCellValue(a0100[1]);
		cell.setCellStyle(style);
		col++;
		// 工号
		if ("#gh[1]".equalsIgnoreCase(parsevo.getBody_gh()) || this.noSelected) {
			if (maxColnum < 6) {
				cell = row.createCell(col * (6 / maxColnum));
			}
			else {
				cell = row.createCell(col);
			}
			if (this.cardno != null && this.cardno.length() > 0) {
				cell.setCellValue(a0100[3]);
			}
			else {
				cell.setCellValue(a0100[0]);
			}
			cell.setCellStyle(style);
			col++;
		}

		int row_h = 1;
		try {
			if ("#kqfu[1]".equalsIgnoreCase(parsevo.getBody_kqfu()) || this.noSelected) {
				for (int s = 0; s < datelist.size(); s++) {
					CommonData cur_vo = (CommonData) datelist.get(s);
					String cur_date = cur_vo.getDataName().trim();
					ArrayList kq_item_list = (ArrayList) kq_item_map.get(cur_date);
					if (maxColnum < 6) {
						cell = row.createCell(col * (6 / maxColnum));
					}
					else {
						cell = row.createCell(col);
					}
					if (kq_item_list != null && kq_item_list.size() > 0) {
						Double dv = (Double) kq_item_list.get(1);
						double value = dv.doubleValue();
						if (value != 0) {
							ArrayList list = (ArrayList) kq_item_list.get(0);
							Map map = new HashMap();
							StringBuffer buf = new StringBuffer();
							int start = 0;
							for (int j = 0; j < list.size(); j++) {
								ArrayList lis = (ArrayList) list.get(j);
								String str = (String) lis.get(0);
								if (str == null) {
									str = "";
								}
								buf.append(str);
								map.put("start" + j, new Integer(start));
								start += str.length();
								map.put("end" + j, new Integer(start));
								map.put("font" + j, lis.get(1));

							}

							HSSFRichTextString ts = new HSSFRichTextString(buf.toString());
							for (int j = 0; j < list.size(); j++) {
								if (map.get("font" + j) != null) {
									ts.applyFont(((Integer) map.get("start" + j)).intValue(), ((Integer) map.get("end" + j)).intValue(), (HSSFFont) map.get("font" + j));
								}
							}
							cell.setCellValue(ts);
						}
					}
					cell.setCellStyle(style);
					col++;
				}
			}

			if (this.kqtablejudge && ("#tjxm[1]".equalsIgnoreCase(parsevo.getBody_tjxm()) || this.noSelected)) {
				rs2.next();
				col = getOneA0100ValueNew(code, kind, a0100, coursedate, kqq03list, body_h, workbook, sheet, datelist, col, maxColnum);
			}

			if (maxColnum < 6) {
				/** 合并单元格 */
				for (int j = 1; j <= col; j++) {
					CellRangeAddress addresses = null;
					if (j == col) {
						sheet.addMergedRegion(new CellRangeAddress(this.rownum, this.rownum, (j - 1) * (6 / maxColnum), 6 - 1));
					} else {
						sheet.addMergedRegion(new CellRangeAddress(this.rownum, this.rownum, (j - 1) * (6 / maxColnum), j * (6 / maxColnum) - 1));
					}
				}
			}

			this.rownum++;

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return row_h;
	}

	/**
	 * 通过一个员工编号得到该员工考勤期间的数据 coursedate 考勤月 如：2009-1
	 */
	public int getOneA0100Data(HSSFWorkbook workbook, HSSFSheet sheet, String code, String kind, String a0100[], ArrayList datelist, float body_h, ArrayList item_list, HSSFCellStyle style, int num,
			String coursedate, ArrayList kqq03list) throws GeneralException {
		ArrayList fielditemlist = DataDictionary.getFieldList("q03", Constant.USED_FIELD_SET);

		// 获得行
		HSSFRow row = this.getRow(sheet, this.getRownum());
		// 单元格
		HSSFCell cell = null;

		StringBuffer column = new StringBuffer();
		ArrayList columnlist = new ArrayList();
		for (int i = 0; i < fielditemlist.size(); i++) {
			FieldItem fielditem = (FieldItem) fielditemlist.get(i);

			if (!"i9999".equals(fielditem.getItemid())) {
				column.append("" + fielditem.getItemid() + ",");
				columnlist.add(fielditem.getItemid());
			}

		}
		CommonData start_vo = (CommonData) datelist.get(0);
		String start_date = start_vo.getDataName();
		CommonData end_vo = (CommonData) datelist.get(datelist.size() - 1);
		String end_date = end_vo.getDataName();
		String sql_one_a0100 = KqReportInit.select_kq_one_emp(this.curTab, a0100[2], a0100[0], start_date, end_date, code, kind, column.toString());
		ContentDAO dao = new ContentDAO(this.conn);
		// 序号
		cell = row.createCell(0);
		cell.setCellValue(num + 1 + "");
		cell.setCellStyle(style);
		style.setWrapText(true);
		// 姓名
		cell = row.createCell(1);
		cell.setCellValue(a0100[1]);
		cell.setCellStyle(style);
		// 工号
		cell = row.createCell(2);
		if (this.cardno != null && this.cardno.length() > 0) {
			cell.setCellValue(a0100[3]);
		}
		else {
			cell.setCellValue(a0100[0]);
		}
		cell.setCellStyle(style);

		int row_h = 1;

		RowSet rowSet = null;
		try {

			rowSet = dao.search(sql_one_a0100);
			HashMap kq_item_map = new HashMap();
			HashMap kq_item_all = querryKq_item();
			while (rowSet.next()) {
				// 保存单元格内容和字体（当单元格内有多个内容时）
				ArrayList list = new ArrayList();
				String q03z0 = rowSet.getString("q03z0").trim();
				int num_r = 0;
				// paragraph=new Paragraph();
				double q03z2 = 0;// 实出勤
				String[] q03z2_item = null;
				for (int i = 0; i <= fielditemlist.size(); i++) {
					if (i < fielditemlist.size()) {
						FieldItem fielditem = (FieldItem) fielditemlist.get(i);
						if ("N".equals(fielditem.getItemtype()) && !"i9999".equals(fielditem.getItemid())) {
							if ("q03z1".equalsIgnoreCase(fielditem.getItemid())) {
								continue;
							}
							else {
								String value = rowSet.getString(fielditem.getItemid());
								if (value == null || value.length() <= 0) {
									value = "0";
								}
								if ("实出勤".equals(fielditem.getItemdesc()) || fielditem.getItemdesc().indexOf("出勤率") != -1) {
									q03z2 = Double.parseDouble(value);
									if (q03z2 != 0) {
										q03z2_item = KqReportInit.getKq_Item(fielditem.getItemid().toString(), item_list);

										continue;
									}
								}
								else {
									double dv = Double.parseDouble(value);
									if (dv != 0) {
										String[] kq_item = KqReportInit.getKq_Item(fielditem.getItemid().toString(), item_list);
										HSSFFont font_1 = getColorFont(parsevo.getBody_fn(), parsevo.getBody_fb(), parsevo.getBody_fi(), parsevo.getBody_fu(), parsevo.getBody_fz(), kq_item[1],
												workbook, kq_item[0]);
										// paragraph.add(new
										// Paragraph(kq_item[0],font_1));//多个
										ArrayList li = new ArrayList();
										li.add(kq_item[0]);
										li.add(font_1);
										list.add(li);
										if (kq_item_all.containsKey(fielditem.getItemid().toLowerCase())) {
											num_r++;
										}
										continue;
									}
								}
							}
						}
						if (!"N".equals(fielditem.getItemtype()) && !"q03z0".equals(fielditem.getItemid()) && !"nbase".equals(fielditem.getItemid()) && !"a0100".equals(fielditem.getItemid())
								&& !"b0110".equals(fielditem.getItemid()) && !"e0122".equals(fielditem.getItemid()) && !"e01a1".equals(fielditem.getItemid()) && !"q03z3".equals(fielditem.getItemid())
								&& !"q03z5".equals(fielditem.getItemid()) && !"state".equals(fielditem.getItemid()) && !"a0101".equals(fielditem.getItemid())) {
							String sr = rowSet.getString(fielditem.getItemid());
							if (sr != null && sr.length() > 0 && !"0".equals(sr)) {
								String[] kq_item = KqReportInit.getKq_Item(fielditem.getItemid(), item_list);
								HSSFFont font_1 = getColorFont(parsevo.getBody_fn(), parsevo.getBody_fb(), parsevo.getBody_fi(), parsevo.getBody_fu(), parsevo.getBody_fz(), kq_item[1], workbook,
										kq_item[0]);
								ArrayList li = new ArrayList();
								li.add(kq_item[0]);
								li.add(font_1);
								list.add(li);
							}
						}
					}
					else if (i == fielditemlist.size() && num_r > 0) {
						ArrayList list2 = new ArrayList();
						list2.add(list);
						list2.add(new Double(1));
						kq_item_map.put(q03z0, list2);
						break;
					}
					else if (i == fielditemlist.size() && q03z2 > 0)// 实出勤
					{
						// 添加实出勤符号 --2010.4.10
						HSSFFont font_1 = getColorFont(parsevo.getBody_fn(), parsevo.getBody_fb(), parsevo.getBody_fi(), parsevo.getBody_fu(), parsevo.getBody_fz(), q03z2_item[1], workbook,
								q03z2_item[0]);
						ArrayList li = new ArrayList();
						li.add(q03z2_item[0]);
						li.add(font_1);
						list.add(li);
//						HSSFCellStyle style2 = workbook.createCellStyle();
//						style2 = this.setHSSFCellStyle(style2);
//						style2.setWrapText(true);
//						style2.setFont(font_1);
						ArrayList list2 = new ArrayList();
						list2.add(list);
						list2.add(new Double(1));
						kq_item_map.put(q03z0, list2);

					}

					if (num_r > 0) {
						if (row_h < num_r) {
                            row_h = num_r;
                        }
					}
				}
			}
			for (int s = 0; s < datelist.size(); s++) {
				int cellnum = s + 3;
				CommonData cur_vo = (CommonData) datelist.get(s);
				String cur_date = cur_vo.getDataName().trim();
				ArrayList kq_item_list = (ArrayList) kq_item_map.get(cur_date);

				if (kq_item_list != null && kq_item_list.size() > 0) {
					Double dv = (Double) kq_item_list.get(1);
					double value = dv.doubleValue();
					if (value != 0) {
						ArrayList list = (ArrayList) kq_item_list.get(0);
						Map map = new HashMap();
						StringBuffer buf = new StringBuffer();
						int start = 0;
						for (int j = 0; j < list.size(); j++) {
							ArrayList lis = (ArrayList) list.get(j);
							String str = (String) lis.get(0);
							if (str == null) {
								str = " ";
							}
							buf.append(str);
							map.put("start" + j, new Integer(start));
							start += str.length();
							map.put("end" + j, new Integer(start));
							map.put("font" + j, lis.get(1));

						}

						HSSFRichTextString ts = new HSSFRichTextString(buf.toString());
						for (int j = 0; j < list.size(); j++) {
							if (map.get("font" + j) != null) {
								ts.applyFont(((Integer) map.get("start" + j)).intValue(), ((Integer) map.get("end" + j)).intValue(), (HSSFFont) map.get("font" + j));
							}
						}
						cell = row.createCell(cellnum);
						cell.setCellValue(ts);
						cell.setCellStyle(style);
					}
					else {
						HSSFFont font_1 = getFont(parsevo.getBody_fn(), parsevo.getBody_fb(), parsevo.getBody_fi(), parsevo.getBody_fu(), parsevo.getBody_fz(), "", workbook);
						HSSFCellStyle style2 = workbook.createCellStyle();
						style2 = this.setHSSFCellStyle(style2);
						// style2.setWrapText(true);
						style2.setFont(font_1);
						cell = row.createCell(cellnum);
						cell.setCellValue("   ");
						cell.setCellStyle(style2);
					}
				}
				else {
					HSSFFont font_1 = getFont(parsevo.getBody_fn(), parsevo.getBody_fb(), parsevo.getBody_fi(), parsevo.getBody_fu(), parsevo.getBody_fz(), "", workbook);
					HSSFCellStyle style2 = workbook.createCellStyle();
					style2 = this.setHSSFCellStyle(style2);
					// style2.setWrapText(true);
					style2.setFont(font_1);
					cell = row.createCell(cellnum);
					cell.setCellValue("   ");
					cell.setCellStyle(style2);
				}
			}
			if (this.kqtablejudge) {
				getOneA0100Value(code, kind, a0100, coursedate, kqq03list, body_h, workbook, sheet, datelist);
			}
			this.rownum++;

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			PubFunc.closeDbObj(rowSet);
		}
		return row_h;
	}

	/**
	 * 查询某月的所有记录的sql语句
	 * 
	 * @param nbase
	 * @param a0100
	 * @param start_date
	 * @param end_date
	 * @param code
	 * @param kind
	 * @param column
	 * @return
	 */
	private String selcet_kq_one_emp(String start_date, String end_date, String code, String kind, String column, String a0100str, String sql_o) {

		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select ");
		sqlstr.append("*");
		sqlstr.append(" from (");
		sqlstr.append(a0100str);
		sqlstr.append(")b left join ").append(this.curTab).append(" a ");
		sqlstr.append(" on Q03Z0 >= '");
		sqlstr.append(start_date);
		sqlstr.append("' and a.a0100=b.a0100 and a.nbase=b.nbase");
		sqlstr.append(" and Q03Z0 <= '");
		sqlstr.append(end_date);
		sqlstr.append("%'");

		String sort = "";
		// zxj 20150518 自定义排序如果不是把个人数据排到一起，那么后续取数是会导致混乱（jazz 9612）
		//String desc = "";
		//SortBo bo = new SortBo(this.conn, this.userView);
		//if (sortSql != null && sortSql.length() > 0 && !"not".equalsIgnoreCase(sortSql)) {
		//	sort = bo.getSortSql(sortSql, "b");
//			desc = bo.getSortSql(sortSql, "b", false);
//		} else if (bo.isExistSort()) {
//			sort = bo.getSortSqlTable("b");
//			desc = bo.getSortSqlTable("b", false);
//		} else {
			sort = "order by b.dbid,b.a0000";
//			desc = "order by b.dbid desc,b.a0000 desc";
	//	}
		sqlstr.append(sort);

		return sqlstr.toString();
	}

	public int getOneA0100ValueNew(String code, String kind, String a0100[], String coursedate, ArrayList kqq03list, float body_h, HSSFWorkbook workbook, HSSFSheet sheet, ArrayList datelist, int col,
			int maxColnum) {
	    this.workbook = workbook;
		HSSFCell cell = null;
		HSSFRow row = this.getRow(sheet, this.getRownum());
		String itemid = "";
		try {
		    Date itemDate;
		    HSSFCellStyle cellStyle = this.getBorderCellStyle();
		    
			for (int t = 0; t < kqq03list.size(); t++) {
				FieldItem fielditem = (FieldItem) kqq03list.get(t);
                if(maxColnum < 6) {
                    cell = row.createCell(col * (6 / maxColnum));
                } else {
                    cell = row.createCell(col);
                }
				if (!"i9999".equals(fielditem.getItemid())) {
					String itd = fielditem.getItemid();
                    if("D".equalsIgnoreCase(fielditem.getItemtype())){
                        int length =fielditem.getItemlength();
					    itemDate = rs2.getDate(itd);
                        
					    String dateString = "";
					    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					    if(length == 4) {
                            formatter = new SimpleDateFormat("yyyy");
                        } else if(length == 7) {
                            formatter = new SimpleDateFormat("yyyy-MM");
                        } else if(length == 16) {
                            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        } else if(length >= 18) {
                            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        }
					    
					    
					    if(itemDate != null) {
                            dateString = formatter.format(itemDate);
                        }
					    
					    itemid =dateString;
                    }else {
                        itemid = rs2.getString(itd);
                    }
                        
					if (itemid != null) {
						if (!"".equals(itemid) && !"0E-8".equalsIgnoreCase(itemid) && !"0".equalsIgnoreCase(itemid)) {
							if ("N".equalsIgnoreCase(fielditem.getItemtype())) {
								int num = fielditem.getDecimalwidth();
								if (itemid.indexOf(".") != -1) {
									for (int k = 0; k < num; k++) {
										itemid += "0";
									}
									itemid = PubFunc.round(itemid, num);
								}
								else {
									for (int k = 0; k < num; k++) {
										if (k == 0) {
											itemid += "." + "0";
										}
										else {
											itemid += "0";
										}
									}
									itemid = PubFunc.round(itemid, num);

								}
							}
							else if ("A".equalsIgnoreCase(fielditem.getItemtype())) {
								String setid = fielditem.getCodesetid();
								if (!"0".equalsIgnoreCase(setid)) {
									itemid = AdminCode.getCodeName(setid, itemid);
								}
							}
							
							
							if ("N".equalsIgnoreCase(fielditem.getItemtype())) {
								// 2014.10.14 xiexd设置导出数据类型为两位小数数值型
								cell.setCellValue(1.2);
								cellStyle = this.getNumberCellStyle();
								cell.setCellValue(Double.parseDouble(itemid));
							}
							else if ("A".equalsIgnoreCase(fielditem.getItemtype())) {
								cell.setCellValue(itemid);
							}
							else if ("D".equalsIgnoreCase(fielditem.getItemtype())) {
								cellStyle = this.getDateCellStyle(); // 设置cell样式为定制的日期格式
								cell.setCellValue(itemid);
							}
						}
					}
				}
				cell.setCellStyle(cellStyle);
				col++;
			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return col;
	}

	public void getOneA0100Value(String code, String kind, String a0100[], String coursedate, ArrayList kqq03list, float body_h, HSSFWorkbook workbook, HSSFSheet sheet, ArrayList datelist) {
		StringBuffer column = new StringBuffer();
		ArrayList columnlist = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		HSSFCell cell = null;
		HSSFRow row = this.getRow(sheet, this.getRownum());
		for (int i = 0; i < kqq03list.size(); i++) {
			FieldItem fielditem = (FieldItem) kqq03list.get(i);
			if (!"i9999".equals(fielditem.getItemid())) {
				column.append("" + fielditem.getItemid() + ",");
				columnlist.add(fielditem.getItemid());
			}
		}
		RowSet rowSet = null;
		String itemid = "";
		try {
			// 获得列名称，
			StringBuffer columStr = new StringBuffer();
			for (int t = 0; t < columnlist.size(); t++) {
				if (t == 0) {
					columStr.append((String) columnlist.get(t));
				}
				else {
					columStr.append(",");
					columStr.append((String) columnlist.get(t));
				}
			}
			StringBuffer sql_one = new StringBuffer();
			sql_one.append("select ");
			sql_one.append(columStr.toString());
			sql_one.append(" from Q05");
			if(this.curTab.toLowerCase().contains("_arc")) {
                sql_one.append("_arc");
            }
			sql_one.append(" where Q03Z0='");
			sql_one.append(coursedate);
			sql_one.append("' and ");
			sql_one.append("nbase='");
			sql_one.append(a0100[2]);
			sql_one.append("' and A0100='");
			sql_one.append(a0100[0]);
			sql_one.append("'");
			rowSet = dao.search(sql_one.toString());
			if (rowSet.next()) {
				for (int t = 0; t < columnlist.size(); t++) {
					int cellnum = t + 3 + datelist.size();
					String itd = (String) columnlist.get(t);
					itemid = rowSet.getString(itd);
					if (itemid != null) {
						if ("".equals(itemid) || "0E-8".equalsIgnoreCase(itemid) || "0".equalsIgnoreCase(itemid)) {
							itemid = "";
							HSSFFont font_1 = getFont(parsevo.getBody_fn(), parsevo.getBody_fb(), parsevo.getBody_fi(), parsevo.getBody_fu(), parsevo.getBody_fz(), "", workbook);
							HSSFCellStyle style2 = workbook.createCellStyle();
							style2 = this.setHSSFCellStyle(style2);
							// style2.setWrapText(true);
							style2.setFont(font_1);
							cell = row.createCell(cellnum);
							cell.setCellValue("   ");
							cell.setCellStyle(style2);
						}
						else {
							// int id = (int) (Float.parseFloat(itemid));
							// itemid = Integer.toString(id);
							HSSFCellStyle style2 = workbook.createCellStyle();
							style2 = this.setHSSFCellStyle(style2);
							// style2.setWrapText(true);
							cell = row.createCell(cellnum);
							cell.setCellValue(itemid);
							cell.setCellStyle(style2);

						}
					}
					else {
						itemid = "";
						HSSFFont font_1 = getFont(parsevo.getBody_fn(), parsevo.getBody_fb(), parsevo.getBody_fi(), parsevo.getBody_fu(), parsevo.getBody_fz(), "", workbook);
						HSSFCellStyle style2 = workbook.createCellStyle();
						style2 = this.setHSSFCellStyle(style2);
						// style2.setWrapText(true);
						style2.setFont(font_1);
						cell = row.createCell(cellnum);
						cell.setCellValue("   ");
						cell.setCellStyle(style2);

					}
				}
			}
			else {
				for (int t = 0; t < columnlist.size(); t++) {
					int cellnum = t + 3 + datelist.size();
					HSSFFont font_1 = getFont(parsevo.getBody_fn(), parsevo.getBody_fb(), parsevo.getBody_fi(), parsevo.getBody_fu(), parsevo.getBody_fz(), "", workbook);
					HSSFCellStyle style2 = workbook.createCellStyle();
					style2 = this.setHSSFCellStyle(style2);
					// style2.setWrapText(true);
					style2.setFont(font_1);
					cell = row.createCell(cellnum);
					cell.setCellValue("   ");
					cell.setCellStyle(style2);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
		    PubFunc.closeDbObj(rowSet);
		}
	}

	public void getTableBodyHead(HSSFWorkbook workbook, HSSFSheet sheet, ArrayList datelist, ArrayList kqq03list, int maxColnum) {
		try {
			// 生成字体样式
			HSSFFont font = getFont(parsevo.getBody_fn(), parsevo.getBody_fb(), parsevo.getBody_fi(), parsevo.getBody_fu(), parsevo.getBody_fz(), "", workbook);
			HSSFCellStyle style = workbook.createCellStyle();
			style = this.setHSSFCellStyle(style);
			style.setWrapText(true);
			style.setFont(font);
			style.setAlignment(HorizontalAlignment.CENTER);
			HSSFRow row = null;
			HSSFCell cell = null;
			row = this.getRow(sheet, this.getRownum());
			int col = 0;
			if (maxColnum < 6) {
				cell = row.createCell(col * (6 / maxColnum));
			}
			else {
				cell = row.createCell(col);
			}
			cell.setCellValue(" 序号 ");
			cell.setCellStyle(style);
			col++;
			
			// cell = row.createCell(1);
			// cell.setCellValue(" 人员库 ");
			// cell.setCellStyle(style);
			if ("#dept[1]".equalsIgnoreCase(parsevo.getBody_dept()) || this.noSelected) {
				if (maxColnum < 6) {
					cell = row.createCell(col * (6 / maxColnum));
				}
				else {
					cell = row.createCell(col);
				}
				cell.setCellValue(" 部门 ");
				cell.setCellStyle(style);
				col++;
			}
			
			if ("#pos[1]".equalsIgnoreCase(parsevo.getBody_pos()) || this.noSelected) {
				KqParameter para = new KqParameter();
				if (!"1".equals(para.getKq_orgView_post())) {
					if (maxColnum < 6) {
						cell = row.createCell(col * (6 / maxColnum));
					}
					else {
						cell = row.createCell(col);
					}
					cell.setCellValue(" 岗位 ");
					cell.setCellStyle(style);
					col++;
				}
			}
			
			if (maxColnum < 6) {
				cell = row.createCell(col * (6 / maxColnum));
			}
			else {
				cell = row.createCell(col);
			}
			cell.setCellValue(" 姓名 ");
			cell.setCellStyle(style);
			col++;
			
			if ("#gh[1]".equalsIgnoreCase(parsevo.getBody_gh()) || this.noSelected) {
				if (maxColnum < 6) {
					cell = row.createCell(col * (6 / maxColnum));
				}
				else {
					cell = row.createCell(col);
				}
				cell.setCellValue(" 工号 ");
				cell.setCellStyle(style);
				col++;
			}
			
			if ("#kqfu[1]".equalsIgnoreCase(parsevo.getBody_kqfu()) || this.noSelected) {
				for (int i = 0; i < datelist.size(); i++) {
					CommonData vo = (CommonData) datelist.get(i);
					String value = vo.getDataValue();
					if (maxColnum < 6) {
						cell = row.createCell(col * (6 / maxColnum));
					}
					else {
						cell = row.createCell(col);
					}
					cell.setCellValue(value);
					cell.setCellStyle(style);
					col++;
				}
			}
			
			if (this.kqtablejudge && ("#tjxm[1]".equalsIgnoreCase(parsevo.getBody_tjxm())) || this.noSelected) {
				for (int p = 0; p < kqq03list.size(); p++) {
					FieldItem fielditem = (FieldItem) kqq03list.get(p);
					String value = fielditem.getItemdesc();
					if (maxColnum < 6) {
						cell = row.createCell(col * (6 / maxColnum));
					}
					else {
						cell = row.createCell(col);
					}
					cell.setCellValue(value);
					cell.setCellStyle(style);
					col++;
				}
			}
			
			if (maxColnum < 6) {
				/** 合并单元格 */
				for (int j = 1; j <= col; j++) {
					CellRangeAddress addresses = null;
					if (j == col) {
						sheet.addMergedRegion(new CellRangeAddress(this.rownum, this.rownum, (j - 1) * (6 / maxColnum), 6 - 1));
					}
					else {
						sheet.addMergedRegion(new CellRangeAddress(this.rownum, this.rownum, (j - 1) * (6 / maxColnum), j * (6 / maxColnum) - 1));
					}
				}
			}
			// 下一行
			this.rownum++;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***************************************************************************
	 * 建立表尾信息
	 **************************************************************************/
	/***************************************************************************
	 * 得到表尾数据
	 **************************************************************************/
	public void getTileHtml(HSSFWorkbook workbook, HSSFSheet sheet, String code, String kind, ArrayList item_list, int maxColumn) throws GeneralException {
		if (maxColumn < 6) {
			maxColumn = 6;
		}

		HSSFCell cell = null;
		HSSFRow row = this.getRow(sheet, this.getRownum());
		StringBuffer note_len_str = new StringBuffer();
		int start = 0;
		// 保存备注的所有信息（字符窜，字体，某字体的开始结束位置）
		ArrayList list = new ArrayList();
		int note_h_1 = Integer.parseInt(parsevo.getBody_fz()) + 6;
		if ("#kqfu[1]".equalsIgnoreCase(parsevo.getBody_kqfu()) || this.noSelected) {
			// PdfPCell cell = null;

			ArrayList fielditemlist = DataDictionary.getFieldList("q03", Constant.USED_FIELD_SET);

			HSSFFont font_b = getFont(parsevo.getBody_fn(), parsevo.getBody_fb(), parsevo.getBody_fi(), parsevo.getBody_fu(), parsevo.getBody_fz(), "", workbook); // 生成字体样式

			String note_str = "备注：1.";

			Map map1 = new HashMap();

			note_len_str.append(note_str);
			map1.put("start", new Integer(start));
			start += note_str.length();
			map1.put("end", new Integer(start));
			map1.put("font", font_b);
			list.add(map1);

			for (int i = 0; i < fielditemlist.size(); i++) {
				FieldItem fielditem = (FieldItem) fielditemlist.get(i);
				/*
				 * if("N".equals(fielditem.getItemtype())) {
				 */
				if (!"i9999".equals(fielditem.getItemid())) {
					String kq_item[] = KqReportInit.getKq_Item(fielditem.getItemid(), item_list);
					if (kq_item[0] != null && kq_item[0].length() > 0) {
						HSSFFont font_tag = getColorFont(parsevo.getBody_fn(), parsevo.getBody_fb(), parsevo.getBody_fi(), parsevo.getBody_fu(), parsevo.getBody_fz(), kq_item[1], workbook, kq_item[0]); // 生成字体样式
						Map map2 = new HashMap();
						String note = fielditem.getItemdesc() + "(";
						note_len_str.append(note);
						map2.put("start", new Integer(start));
						start += note.length();
						map2.put("end", new Integer(start));
						map2.put("font", null);
						list.add(map2);

						Map map3 = new HashMap();
						note = kq_item[0];
						note_len_str.append(note);
						map3.put("start", new Integer(start));
						start += note.length();
						map3.put("end", new Integer(start));
						map3.put("font", font_tag);
						list.add(map3);

						Map map4 = new HashMap();
						note = ")";
						note_len_str.append(note);
						map4.put("start", new Integer(start));
						start += note.length();
						map4.put("end", new Integer(start));
						map4.put("font", null);
						list.add(map4);

					}
				}
				// }
			}
			int strlen = note_len_str.toString().length() * Integer.parseInt(parsevo.getBody_fz());

			int numrow_1 = getNumRow(strlen);
			if (numrow_1 != 0) {
				note_h_1 = note_h_1 * numrow_1;
			}
		}
		if (parsevo.getTile_fw() != null && parsevo.getTile_fw().length() > 0) {
			String tile_fw = "     2." + parsevo.getTile_fw();
			int str_tile_2 = tile_fw.length() * Integer.parseInt(parsevo.getBody_fz());
			int numrow_2 = getNumRow(str_tile_2);
			int note_h_2 = Integer.parseInt(parsevo.getBody_fz()) + 6;
			if (numrow_2 != 0) {
				note_h_2 = note_h_2 * numrow_2;
			}
			Map map5 = new HashMap();
			note_len_str.append("\r\n");
			note_len_str.append(tile_fw);
			map5.put("start", new Integer(start));
			start += tile_fw.length();
			map5.put("end", new Integer(start));
			map5.put("font", null);
			list.add(map5);
		}

		HSSFRichTextString ts = new HSSFRichTextString(note_len_str.toString());
		for (int j = 0; j < list.size(); j++) {
			Map map = (Map) list.get(j);
			if (map.get("font") != null) {
				int start4 = ((Integer) map.get("start")).intValue();
				int end4 = ((Integer) map.get("end")).intValue();
				HSSFFont fon = (HSSFFont) map.get("font");
				ts.applyFont(start4, end4, fon);
			}
		}
		cell = row.createCell(0);

		HSSFCellStyle style2 = workbook.createCellStyle();
		style2 = this.setHSSFCellStyle(style2);
		style2.setWrapText(true);
		style2.setAlignment(HorizontalAlignment.JUSTIFY);
		row.setHeightInPoints(note_h_1);
		cell.setCellValue(ts);
		cell.setCellStyle(style2);

		// 合并单元格
		sheet.addMergedRegion(new CellRangeAddress(this.rownum, this.rownum, 0, maxColumn - 1));
		for(int i = 1; i < maxColumn; i++)
        {
            cell = row.createCell(i);
            cell.setCellValue("");
            cell.setCellStyle(style2);
        }
		this.rownum++;

		/** *表尾基本参数* */
		int par_column = getNumColumn_2("", "", parsevo.getTile_e(), parsevo.getTile_u(), parsevo.getTile_d(), parsevo.getTile_t());
		float tile_h = Float.parseFloat(parsevo.getTile_h());
		row = this.getRow(sheet, this.getRownum());

		if (par_column > 0) {
			int ave = maxColumn / par_column;
			// 生成字体样式
			HSSFFont font_tile = getFont(parsevo.getTile_fn(), parsevo.getTile_fb(), parsevo.getTile_fi(), parsevo.getTile_fu(), parsevo.getTile_fz(), "", workbook);
			HSSFCellStyle style = workbook.createCellStyle();
			style = this.setHSSFCellStyle(style);
			style.setWrapText(true);
			style.setFont(font_tile);

			/** 制作人* */
			int i = 1;
			/** 制作人单位* */
			if ("#u".equals(parsevo.getTile_u().trim())) {
				String u_code = "";
				if (!userView.isSuper_admin()) {
					if (userView.getUserOrgId() != null && userView.getUserOrgId().trim().length() > 0) {
						u_code = userView.getUserOrgId();
					}
					else {
						u_code = RegisterInitInfoData.getKqPrivCodeValue(userView);
					}
				}
				KqViewDailyBo kqViewDailyBo = new KqViewDailyBo(this.conn, this.parsevo, this.userView);
				String[] u_codeitem = kqViewDailyBo.getCodeItemDesc(u_code);
				String u_str = "  制作人单位: " + u_codeitem[0];

				cell = row.getCell((i - 1) * ave);
				if (cell == null) {
					cell = row.createCell((i - 1) * ave);
				}
				cell.setCellValue(u_str);
				cell.setCellStyle(style);
				i++;
			}
			/** 制作日期* */
			if ("#d".equals(parsevo.getTile_d().trim())) {
				String d_str = " 制作日期: " + this.sjelement;
				cell = row.createCell((i - 1) * ave);
				cell.setCellValue(d_str);
				cell.setCellStyle(style);
				i++;
			}
			if ("#e".equals(parsevo.getTile_e().trim())) {

				String e_str = "    制作人: " + this.userView.getUserFullName();
				cell = row.createCell((i - 1) * ave);
				cell.setCellValue(e_str);
				cell.setCellStyle(style);
				i++;

			}

			/** 制作时间* */
			if ("#t".equals(parsevo.getTile_t().trim())) {
				String t_str = " 时间: " + this.timeqd;
				cell = row.createCell((i - 1) * ave);
				cell.setCellValue(t_str);
				cell.setCellStyle(style);
				i++;

			}
			for (int k = 1; k <= par_column; k++) {CellRangeAddress addresses = null;
            int start2 = (k - 1) * ave;
            int end2 = k * ave - 1;
            if(k == par_column)
            {
                sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, start2, maxColumn - 1));
                for(int c = start2 + 1; c < maxColumn; c++)
                {
                    cell = row.createCell(c);
                    cell.setCellValue("");
                    cell.setCellStyle(style);
                }

            } else
            {
                sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, start2, end2));
                for(int c = start2 + 1; c < end2 + 1; c++)
                {
                    cell = row.createCell(c);
                    cell.setCellValue("");
                    cell.setCellStyle(style);
                }

            }}
		}

	}

	/**
	 * 生成字体样式,解决中文问题
	 * 
	 */
	public HSSFFont getFont(String fn, String bold, String italic, String underline, String fontSizeStr, String color, HSSFWorkbook workbook) {
		// Font font=null;
		// BaseFont bfComic=null;
		HSSFFont font = null;
		int fontSize = 12;

		if (fontSizeStr != null && fontSizeStr.length() > 0) {
			fontSize = Integer.parseInt(fontSizeStr);
		}
		try {

			if ("#fb[1]".equals(bold.trim()) && !"#fu[1]".equals(underline.trim()) && !"#fi[1]".equals(underline.trim())) { // 粗体
				if (fontIndex1 == null) {
					fontIndex1 = workbook.createFont();
				}
				font = fontIndex1;
				font.setFontHeightInPoints((short) (fontSize - 2));
				font.setBold(true);
			}
			else if (!"#fb[1]".equals(bold.trim()) && !"#fu[1]".equals(underline.trim()) && "#fi[1]".equals(underline.trim())) { // 斜体
				if (fontIndex2 == null) {
					fontIndex2 = workbook.createFont();
				}
				font = fontIndex2;
				font.setFontHeightInPoints((short) (fontSize - 2));
				font.setItalic(true);
			}
			else if (!"#fb[1]".equals(bold.trim()) && "#fu[1]".equals(underline.trim()) && !"#fi[1]".equals(underline.trim())) { // 下划线
				if (fontIndex3 == null) {
					fontIndex3 = workbook.createFont();
				}
				font = fontIndex3;
				font.setFontHeightInPoints((short) (fontSize - 2));
				font.setUnderline(HSSFFont.U_SINGLE);
			}
			else if ("#fb[1]".equals(bold.trim()) && "#fu[1]".equals(underline.trim()) && !"#fi[1]".equals(underline.trim())) { // 粗体||下划线
				if (fontIndex4 == null) {
					fontIndex4 = workbook.createFont();
				}
				font = fontIndex4;
				font.setFontHeightInPoints((short) (fontSize - 2));
				font.setBold(true);
				font.setUnderline(HSSFFont.U_SINGLE);
			}
			else if ("#fb[1]".equals(bold.trim()) && !"#fu[1]".equals(underline.trim()) && "#fi[1]".equals(underline.trim())) { // 粗体||斜体
				if (fontIndex5 == null) {
					fontIndex5 = workbook.createFont();
				}
				font = fontIndex5;
				font.setFontHeightInPoints((short) (fontSize - 2));
				font.setBold(true);
				font.setItalic(true);
			}
			else if (!"#fb[1]".equals(bold.trim()) && "#fu[1]".equals(underline.trim()) && "#fi[1]".equals(underline.trim())) { // 斜体||下划线
				if (fontIndex6 == null) {
					fontIndex6 = workbook.createFont();
				}
				font = fontIndex6;
				font.setFontHeightInPoints((short) (fontSize - 2));
				font.setItalic(true);
				font.setUnderline(HSSFFont.U_SINGLE);
			}
			else if ("#fb[1]".equals(bold.trim()) && "#fu[1]".equals(underline.trim()) && "#fi[1]".equals(underline.trim())) { // 斜体||下划线||下划线
				if (fontIndex7 == null) {
					fontIndex7 = workbook.createFont();
				}
				font = fontIndex7;
				font.setFontHeightInPoints((short) (fontSize - 2));
				font.setItalic(true);
				font.setUnderline(HSSFFont.U_SINGLE);
				font.setBold(true);
			}
			else {
				if (fontIndex8 == null) {
					fontIndex8 = workbook.createFont();
				}
				font = fontIndex8;
				font.setFontHeightInPoints((short) (fontSize - 2));
			}
			font.setFontName(ResourceFactory.getProperty("gz.gz_acounting.m.font"));
			if (color != null && color.length() > 0) {
				if (color.length() > 9) {
					int cos = Integer.parseInt(color.substring(1, 4));
					int cos1 = Integer.parseInt(color.substring(4, 7));
					int cos2 = Integer.parseInt(color.substring(7, 10));
					Color color2 = new Color(cos, cos1, cos2);
					HSSFPalette palette = workbook.getCustomPalette();

					palette.setColorAtIndex(this.paletteIndex, (byte) cos, (byte) cos1, (byte) cos2);
					font.setColor(this.paletteIndex);
					this.paletteIndex++;
				}

			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return font;

	}

	public HSSFFont getColorFont(String fn, String bold, String italic, String underline, String fontSizeStr, String color, HSSFWorkbook workbook, String content) {
		HSSFFont font = null;
		int fontSize = 12;

		if (fontSizeStr != null && fontSizeStr.length() > 0) {
			fontSize = Integer.parseInt(fontSizeStr);
		}
		try {
			font = this.createColor(workbook, font, color, content);
			if (font != null) {
				if ("#fb[1]".equals(bold.trim()) && !"#fu[1]".equals(underline.trim()) && !"#fi[1]".equals(underline.trim())) { // 粗体
					font.setFontHeightInPoints((short) (fontSize - 2));
					font.setBold(true);
				}
				else if (!"#fb[1]".equals(bold.trim()) && !"#fu[1]".equals(underline.trim()) && "#fi[1]".equals(underline.trim())) { // 斜体
					font.setFontHeightInPoints((short) (fontSize - 2));
					font.setItalic(true);
				}
				else if (!"#fb[1]".equals(bold.trim()) && "#fu[1]".equals(underline.trim()) && !"#fi[1]".equals(underline.trim())) { // 下划线
					font.setFontHeightInPoints((short) (fontSize - 2));
					font.setUnderline(HSSFFont.U_SINGLE);
				}
				else if ("#fb[1]".equals(bold.trim()) && "#fu[1]".equals(underline.trim()) && !"#fi[1]".equals(underline.trim())) { // 粗体||下划线
					font.setFontHeightInPoints((short) (fontSize - 2));
					font.setBold(true);
					font.setUnderline(HSSFFont.U_SINGLE);
				}
				else if ("#fb[1]".equals(bold.trim()) && !"#fu[1]".equals(underline.trim()) && "#fi[1]".equals(underline.trim())) { // 粗体||斜体
					font.setFontHeightInPoints((short) (fontSize - 2));
					font.setBold(true);
					font.setItalic(true);
				}
				else if (!"#fb[1]".equals(bold.trim()) && "#fu[1]".equals(underline.trim()) && "#fi[1]".equals(underline.trim())) { // 斜体||下划线
					font.setFontHeightInPoints((short) (fontSize - 2));
					font.setItalic(true);
					font.setUnderline(HSSFFont.U_SINGLE);
				}
				else if ("#fb[1]".equals(bold.trim()) && "#fu[1]".equals(underline.trim()) && "#fi[1]".equals(underline.trim())) { // 斜体||下划线
					font.setFontHeightInPoints((short) (fontSize - 2));
					font.setItalic(true);
					font.setUnderline(HSSFFont.U_SINGLE);
					font.setBold(true);
				}
				else {
					font.setFontHeightInPoints((short) (fontSize - 2));
				}
				font.setFontName(ResourceFactory.getProperty("gz.gz_acounting.m.font"));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return font;

	}

	private HSSFFont createColor(HSSFWorkbook workbook, HSSFFont font, String color, String content) {
		if (color != null && color.length() > 0) {
			if (this.colormap.get(content + color) != null) {
				font = (HSSFFont) colormap.get(content + color);
				return font;
			}
			if (color.length() > 9) {
				font = workbook.createFont();
				int cos = Integer.parseInt(color.substring(1, 4));
				int cos1 = Integer.parseInt(color.substring(4, 7));
				int cos2 = Integer.parseInt(color.substring(7, 10));
				HSSFPalette palette = workbook.getCustomPalette();

				palette.setColorAtIndex(this.paletteIndex, (byte) cos, (byte) cos1, (byte) cos2);
				font.setColor(this.paletteIndex);
				this.paletteIndex++;
				this.colormap.put(content + color, font);
			}
		}
		return font;
	}

	/***************************************************************************
	 * 表格样式边线
	 * 
	 * @return String[] ltrb ltrb[0]=左 ltrb[1]=上 ltrb[2]=右 ltrb[3]=下
	 **************************************************************************/
	public String[] getLtrb(String v) {
		String[] ltrb = new String[4];
		if ("1".equals(v)) {
			ltrb[0] = "1";
			ltrb[1] = "1";
			ltrb[2] = "1";
			ltrb[3] = "1";
		}
		else if ("2".equals(v))// "RecordRow_self_l";
		{
			ltrb[0] = "0";
			ltrb[1] = "1";
			ltrb[2] = "1";
			ltrb[3] = "1";
		}
		else if ("3".equals(v))// "RecordRow_self_t";
		{
			ltrb[0] = "1";
			ltrb[1] = "0";
			ltrb[2] = "1";
			ltrb[3] = "1";
		}
		else if ("4".equals(v))// "RecordRow_self_r";
		{
			ltrb[0] = "1";
			ltrb[1] = "1";
			ltrb[2] = "0";
			ltrb[3] = "1";
		}
		else if ("5".equals(v))// "RecordRow_self_b";
		{
			ltrb[0] = "1";
			ltrb[1] = "1";
			ltrb[2] = "1";
			ltrb[3] = "0";
		}
		else if ("6".equals(v))// "RecordRow_self_l_t";
		{
			ltrb[0] = "1";
			ltrb[1] = "0";
			ltrb[2] = "0";
			ltrb[3] = "1";
		}
		else if ("7".equals(v))// "RecordRow_self_r_t";
		{
			ltrb[0] = "0";
			ltrb[1] = "1";
			ltrb[2] = "0";
			ltrb[3] = "1";
		}
		return ltrb;
	}

	/** **************参数的运算&定义******************* */
	/***************************************************************************
	 * 以用高度
	 **************************************************************************/
	public float getIsUseHieght(ArrayList item_list) throws GeneralException {

		float height = Float.parseFloat(getPxFormMm_f(parsevo.getTop())) + Float.parseFloat(getPxFormMm_f(parsevo.getBottom())) + Float.parseFloat(getPxFormMm_f(parsevo.getTitle_h()));
		height = height + Float.parseFloat(getPxFormMm_f(parsevo.getHead_h()));
		height = height + Float.parseFloat(parsevo.getBody_fz()) + 13;

		ArrayList fielditemlist = DataDictionary.getFieldList("q03", Constant.USED_FIELD_SET);
		// 计算备注1
		StringBuffer note_len_str = new StringBuffer();
		note_len_str.append("备注：1.");
		for (int i = 0; i < fielditemlist.size(); i++) {
			FieldItem fielditem = (FieldItem) fielditemlist.get(i);
			if ("N".equals(fielditem.getItemtype())) {
				if (!"i9999".equals(fielditem.getItemid())) {
					String kq_item[] = KqReportInit.getKq_Item(fielditem.getItemid(), item_list);
					if (kq_item[0] != null && kq_item[0].length() > 0) {
						note_len_str.append(fielditem.getItemdesc() + "(" + kq_item[0] + ")");
					}
				}
			}
		}
		int strlen = note_len_str.toString().length() * Integer.parseInt(parsevo.getBody_fz());
		int numrow_tile_1 = getNumRow(strlen);

		if (numrow_tile_1 != 0) {
			height = height + (Float.parseFloat(parsevo.getBody_fz()) + 6) * numrow_tile_1;
		}
		else {
			height = height + (Float.parseFloat(parsevo.getBody_fz()) + 6);
		}

		// 计算表尾客户添加的文本内容
		if (parsevo.getTile_fw() != null && parsevo.getTile_fw().length() > 0) {
			/** ***#代表一个空格*** */
			String tile_fw = "#####2." + parsevo.getTile_fw();
			int str_tile_2 = tile_fw.length() * Integer.parseInt(parsevo.getBody_fz());

			int note_tile_2 = getNumRow(str_tile_2);

			if (note_tile_2 != 0) {
				height = height + (Float.parseFloat(parsevo.getBody_fz()) + 6) * note_tile_2;
			}
		}

		if ("#c".equals(parsevo.getHead_c()) || "#p".equals(parsevo.getHead_p()) || "#e".equals(parsevo.getHead_e()) || "#u".equals(parsevo.getHead_u()) || "#d".equals(parsevo.getHead_d())
				|| "#t".equals(parsevo.getHead_t())) {
			height = height + Float.parseFloat(getPxFormMm_f(parsevo.getHead_h()));
		}

		if ("#c".equals(parsevo.getTile_c()) || "#p".equals(parsevo.getTile_p()) || "#e".equals(parsevo.getTile_e()) || "#u".equals(parsevo.getTile_u()) || "#d".equals(parsevo.getTile_d())
				|| "#t".equals(parsevo.getTile_t())) {
			height = height + Float.parseFloat(getPxFormMm_f(parsevo.getTile_h()));
		}

		return height;
	}

	/***************************************************************************
	 * 剩余高度
	 */

	public float getSpareHieght(ArrayList item_list) throws GeneralException {
		float spare_hieght = 0;
		float height = getIsUseHieght(item_list);
		String unit = parsevo.getUnit().trim();
		if ("px".equals(unit)) {
			spare_hieght = Float.parseFloat(getFactHeight()) - height;
		}
		else {
			spare_hieght = Float.parseFloat(getFactHeight()) - height / this.rate;
		}
		return spare_hieght;
	}

	/**
	 * 转换，毫米转换为像素
	 */
	public String getPxFormMm_f(String value) {
		String unit = parsevo.getUnit().trim();// 长度单位,毫米还是像素
		if ("mm".equals(unit)) {
			float dv = Float.parseFloat(value) / this.rate;
			return KqReportInit.round(dv + "", 0);
		}
		else {
			return KqReportInit.round(value, 0);
		}
	}

	/**
	 * 计算表格实际总宽度
	 */
	public String getFactWidth() {
		String unit = parsevo.getUnit().trim();// 长度单位,毫米还是像素
		if ("1".equals(parsevo.getOrientation().trim())) {
			if ("px".equals(unit)) {
				float width = Float.parseFloat(parsevo.getHeight()) / this.rate - Float.parseFloat(getPxFormMm_f(parsevo.getLeft())) - Float.parseFloat(getPxFormMm_f(parsevo.getRight()));
				return KqReportInit.round(width + "", 0);
			}
			else {
				float width = Float.parseFloat(parsevo.getHeight()) / this.rate - Float.parseFloat(getPxFormMm_f(parsevo.getLeft())) - Float.parseFloat(getPxFormMm_f(parsevo.getRight()));
				return KqReportInit.round(width + "", 0);
			}
		}
		else {
			if ("px".equals(unit)) {
				float width = Float.parseFloat(parsevo.getWidth()) / this.rate - Float.parseFloat(getPxFormMm_f(parsevo.getLeft())) - Float.parseFloat(getPxFormMm_f(parsevo.getRight()));
				return KqReportInit.round(width + "", 0);
			}
			else {
				float width = Float.parseFloat(parsevo.getWidth()) / this.rate - Float.parseFloat(getPxFormMm_f(parsevo.getLeft())) - Float.parseFloat(getPxFormMm_f(parsevo.getRight()));
				return KqReportInit.round(width + "", 0);
			}
		}
	}

	/**
	 * 计算表格实际总高度
	 */
	public String getFactHeight() {
		String unit = parsevo.getUnit().trim();// 长度单位,毫米还是像素
		if ("1".equals(parsevo.getOrientation().trim())) {
			if ("px".equals(unit)) {
				double height = Double.parseDouble(parsevo.getWidth()) / this.rate - Double.parseDouble(getPxFormMm_f(parsevo.getTop())) - Double.parseDouble(getPxFormMm_f(parsevo.getBottom()));
				return KqReportInit.round(height + "", 0);
			}
			else {
				double height = Double.parseDouble(parsevo.getWidth()) / this.rate - Double.parseDouble(getPxFormMm_f(parsevo.getTop())) - Double.parseDouble(getPxFormMm_f(parsevo.getBottom()));
				return KqReportInit.round(height + "", 0);
			}
		}
		else {
			if ("px".equals(unit)) {
				double height = Double.parseDouble(parsevo.getHeight()) / this.rate - Double.parseDouble(getPxFormMm_f(parsevo.getTop())) - Double.parseDouble(getPxFormMm_f(parsevo.getBottom()));
				return KqReportInit.round(height + "", 0);
			}
			else {
				double height = Double.parseDouble(parsevo.getHeight()) / this.rate - Double.parseDouble(getPxFormMm_f(parsevo.getTop())) - Double.parseDouble(getPxFormMm_f(parsevo.getBottom()));
				return KqReportInit.round(height + "", 0);
			}
		}
	}

	/**
	 * 计算在规定的字数中，一串字符，有多少行
	 */
	public int getNumRow(int strlen) {
		int factwidth = Integer.parseInt(getFactWidth());

		int ss = strlen / factwidth;
		int dd = strlen % factwidth;
		if (dd != 0) {
			ss = ss + 1;
		}
		return ss;
	}

	/**
	 * 返回表头||表尾的烈属
	 * 
	 * @param c
	 *            总页数
	 * @param p
	 *            页码
	 * @param e
	 *            制作人
	 * @param u
	 *            制作人所在的单位
	 * @param d
	 *            日期
	 * @param t
	 *            时间
	 * @return 列数
	 */
	public int getNumColumn_2(String c, String p, String e, String u, String d, String t) {
		int i = 0;
		if ("#c".equals(c.trim())) {
            i = i + 1;
        }
		if ("#p".equals(p.trim())) {
            i = i + 1;
        }
		if ("#e".equals(e.trim())) {
            i = i + 1;
        }
		if ("#u".equals(u.trim())) {
            i = i + 1;
        }
		if ("#d".equals(d.trim())) {
            i = i + 1;
        }
		if ("#t".equals(t.trim())) {
            i = i + 1;
        }
		return i;
	}

	public int getNumColumn_3(String c, String p, String e, String u, String d, String t) {
		int i = 0;
		if ("#e".equals(e.trim())) {
            i = i + 1;
        }
		if ("#u".equals(u.trim())) {
            i = i + 1;
        }
		if ("#d".equals(d.trim())) {
            i = i + 1;
        }
		if ("#t".equals(t.trim())) {
            i = i + 1;
        }
		return i;
	}

	public String getCardno() {
		return cardno;
	}

	public void setCardno(String cardno) {
		this.cardno = cardno;
	}

	public short getPaletteIndex() {
		return paletteIndex;
	}

	public void setPaletteIndex(short paletteIndex) {
		this.paletteIndex = paletteIndex;
	}

	private HSSFCellStyle setHSSFCellStyle(HSSFCellStyle style) {
		 style.setBorderBottom(BorderStyle.THIN);
		 style.setBorderLeft(BorderStyle.THIN);
		 style.setBorderRight(BorderStyle.THIN);
		 style.setBorderTop(BorderStyle.THIN);
		return style;
	}

	public HSSFCellStyle getBorderStyle() {
		return borderStyle;
	}

	public void setBorderStyle(HSSFCellStyle borderStyle) {
		this.borderStyle = borderStyle;
	}

	/**
	 * 查询考勤期间的所有指标
	 * 
	 * @return
	 */
	private HashMap querryKq_item() {
		HashMap map = new HashMap();
		String sql = "select item_symbol,fielditemid from kq_item";
		ContentDAO dao = new ContentDAO(this.conn);
		ResultSet rs = null;
		try {
			rs = dao.search(sql);
			while (rs.next()) {
				String key = rs.getString("fielditemid");
				String value = rs.getString("item_symbol");
				if (key != null) {
					map.put(key.toLowerCase(), value);
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
		    PubFunc.closeDbObj(rs);
		}
		return map;
	}

	public String getSortSql() {
		return sortSql;
	}

	public void setSortSql(String sortSql) {
		this.sortSql = sortSql;
	}

	public String getSortSqlDesc() {
		return sortSqlDesc;
	}

	public void setSortSqlDesc(String sortSqlDesc) {
		this.sortSqlDesc = sortSqlDesc;
	}

    private HSSFCellStyle getNumberCellStyle() {
        if (this.numberCellStyle == null) {
            numberCellStyle = workbook.createCellStyle();
            numberCellStyle.setBorderBottom(BorderStyle.THIN);
            numberCellStyle.setBorderLeft(BorderStyle.THIN);
            numberCellStyle.setBorderRight(BorderStyle.THIN);
            numberCellStyle.setBorderTop(BorderStyle.THIN);
            numberCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
        }
        return numberCellStyle;
    }

    private HSSFCellStyle getDateCellStyle() {
        if (this.dateCellStyle == null) {
            dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setBorderBottom(BorderStyle.THIN);
            dateCellStyle.setBorderLeft(BorderStyle.THIN);
            dateCellStyle.setBorderRight(BorderStyle.THIN);
            dateCellStyle.setBorderTop(BorderStyle.THIN);
            dateCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m-d-yy"));
        }
       
        return dateCellStyle;
    }

    private HSSFCellStyle getBorderCellStyle() {
        if (this.borderCellStyle == null) {
            borderCellStyle = workbook.createCellStyle();
            borderCellStyle.setBorderBottom(BorderStyle.THIN);
            borderCellStyle.setBorderLeft(BorderStyle.THIN);
            borderCellStyle.setBorderRight(BorderStyle.THIN);
            borderCellStyle.setBorderTop(BorderStyle.THIN);
        }
        return borderCellStyle;
    }
}
