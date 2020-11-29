/*
 * Created on 2006-2-8
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.SelectAllOperate;
import com.hjsj.hrms.businessobject.kq.app_check_in.SearchAllApp;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.query.CodingAnalytical;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class SearchAllAppTrans extends IBusiness {

	/**
	 * 校验日期是否正确
	 * 
	 * @return
	 */
	private boolean validateDate(String datestr) {
		boolean bflag = true;
		if (datestr == null || "".equals(datestr))
			return false;
		try {
			java.util.Date date = DateStyle.parseDate(datestr);
			if (date == null)
				bflag = false;
		}
		catch (Exception ex) {
			bflag = false;
		}
		return bflag;
	}

	/**
	 * 获取领导建议字段长度
	 * 
	 * @param table
	 * @throws SQLException
	 */

	private void getOpinionlength(String table) throws SQLException {
		String Opinionlength = "";
		FieldItem item = DataDictionary.getFieldItem(table + "15", table);
		Opinionlength = item.getItemlength() + "";
		this.getFormHM().put("opinionlength", Opinionlength);
	}

	public void execute() throws GeneralException {
		try {
			/** 判断考勤期间 */
			ArrayList kqlist = RegisterDate.getKqDayList(this.getFrameconn());
			if (kqlist == null || kqlist.size() <= 0) {
				throw new GeneralException(ResourceFactory.getProperty("error.kq.please"));
			}

			/** chenmengqing added */
			String dotflag = (String) this.getFormHM().get("dotflag");
			if (!"0".equalsIgnoreCase(dotflag)) {
				this.getFormHM().put("dotflag", "0");
			}

			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String query_type = (String) this.getFormHM().get("query_type");
			ArrayList kq_dbase_list = (ArrayList) this.getFormHM().get("kq_dbase_list");
			if (query_type == null || "".equals(query_type))
				query_type = "1";

			String sp_flag = (String) this.getFormHM().get("sp_flag");
			// 如果form中没有sp_flag，检查是否url参数中带有sp_flag(主页待办链接会有此参数）
			if (sp_flag == null || "".equals(sp_flag)) {
				sp_flag = (String) hm.get("sp_flag");

				if (sp_flag == null || "".equals(sp_flag))
					sp_flag = "all";
			}

			String full = (String) this.getFormHM().get("full");

			/** 考勤项目（年假、病假、节日加班等类型） */
			String kq_item = (String) this.getFormHM().get("showtype");
			if (kq_item == null || "".equals(kq_item))
				kq_item = "all";

			this.getFormHM().put("sp_flag", sp_flag);
			this.getFormHM().put("showtype", kq_item);

			String start_date = (String) this.getFormHM().get("start_date");
			String end_date = (String) this.getFormHM().get("end_date");

			// ** -------------------------郑文龙---------------------- 加 工号、考勤卡号
			KqParameter para = new KqParameter(this.userView, "", this.getFrameconn());
			HashMap hashmap = para.getKqParamterMap();
			String g_no = (String) hashmap.get("g_no");
			String cardno = (String) hashmap.get("cardno");
			// ** -------------------------郑文龙---------------------- 加 工号、考勤卡号

			if (start_date != null && start_date.length() > 0)
				start_date = start_date.replaceAll("\\.", "-");

			if (end_date != null && end_date.length() > 0)
				end_date = end_date.replaceAll("\\.", "-");

			if (!(validateDate(start_date) && validateDate(end_date))) {
				ArrayList datelist = RegisterDate.getKqDayList(this.getFrameconn());
				if (datelist != null && datelist.size() > 0) {
					start_date = datelist.get(0).toString();
					end_date = datelist.get(datelist.size() - 1).toString();

					if (start_date != null && start_date.length() > 0)
						start_date = start_date.replaceAll("\\.", "-");

					if (end_date != null && end_date.length() > 0)
						end_date = end_date.replaceAll("\\.", "-");
				}
				else {
					start_date = DateStyle.dateformat(new java.util.Date(), "yyyy-MM-dd");
					end_date = start_date;
				}
				// 当天假单
			}

			String select_flag = (String) hm.get("select_flag");
			hm.remove("select_flag");
			if (select_flag == null || "".equals(select_flag)) {
				select_flag = (String) this.getFormHM().get("select_flag");
				this.getFormHM().put("select_flag", "0");
			}

			if (select_flag != null && "0".equals(select_flag)) {
				query_type = "9";
				this.getFormHM().put("showtype", "");
				this.getFormHM().put("sp_flag", "");
				ArrayList datelist = RegisterDate.getKqDayList(this.getFrameconn());
				if (datelist != null && datelist.size() > 0) {
					start_date = datelist.get(0).toString();
					end_date = datelist.get(datelist.size() - 1).toString();
					if (start_date != null && start_date.length() > 0)
						start_date = start_date.replaceAll("\\.", "-");
					if (end_date != null && end_date.length() > 0)
						end_date = end_date.replaceAll("\\.", "-");
				}
			
			}

			String select_name = (String) this.getFormHM().get("select_name");
			if ("1".equalsIgnoreCase(full)) {
				select_name = "";
				full = "0";
			}

			String select_pre = (String) this.getFormHM().get("select_pre");
			String select_time_type = (String) this.getFormHM().get("select_time_type");
			if ("0".equalsIgnoreCase(dotflag)) {
				select_time_type = "0";
				// szk 20131106获取最后一天
				Date date = new Date();
				end_date = DateUtils.format(date, "yyyy") + "-12-31";
				//end_date = end_date.substring(0, 4) + "-12-31";
			}

			this.getFormHM().put("select_time_type", select_time_type);
			// this.getFormHM().put("select_flag",select_flag);
			this.getFormHM().put("select_name", select_name);
			this.getFormHM().put("full", full);

			/** 左边树节点对应的代码值及类型 */
			/*
			 * // String code1=(String)this.getFormHM().get("code"); String
			 * code=(String)hm.get("code"); hm.remove("code");
			 * this.getFormHM().put("code", code);
			 */
			String code = (String) this.getFormHM().get("code");

			/**
			 *=0 职位 =1 部门 =2 单位
			 */
			String kind = (String) this.getFormHM().get("kind");
			KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn(), this.userView);
			kq_dbase_list = kqUtilsClass.setKqPerList(code, kind);
			if (kq_dbase_list.size() == 0 || kq_dbase_list == null) {
				throw new GeneralException(ResourceFactory.getProperty("kq.register.dbase.nosave"));
			}

			/** 申请登记表名 */
			String table = (String) hm.get("table");
			String ta = table.toLowerCase();
			SearchAllApp searchAllApp = new SearchAllApp(this.getFrameconn(), this.userView);
			searchAllApp.reconstructionApp(table);

			this.getOpinionlength(table);
			if ("q15".equalsIgnoreCase(table)) {
				this.getFormHM().put("dert_itemid", "");// 是否有扣除休息时间
			}

			if ("q11".equalsIgnoreCase(table)) {
				this.getFormHM().put("dert_itemid", searchAllApp.isDeductResttime(table));// 是否有扣除休息时间
			}

			if ("q13".equalsIgnoreCase(table)) {
				this.getFormHM().put("dert_itemid", "");// 是否有扣除休息时间
			}

			/* 添加字段 */
			SelectAllOperate selectAllOperate = new SelectAllOperate(this.getFrameconn(), this.userView);
			selectAllOperate.allOperate(table);

			String frist = (String) hm.get("wo");
			String relatTableid = ta.substring(1);
			ArrayList fieldlist = DataDictionary.getFieldList(table, Constant.USED_FIELD_SET);// 字段名
			// 如果申请表中没有工号，固定加上工号指标
			if (!fieldlist.toString().toLowerCase().contains("=" + g_no.toLowerCase())) {
				ArrayList list = DataDictionary.getFieldList("A01", Constant.USED_FIELD_SET);
				for (int i = 0; i < list.size(); i++) {
					FieldItem item = new FieldItem();
					item = (FieldItem) list.get(i);
					if (g_no.equalsIgnoreCase(item.getItemid()))
						fieldlist.add(item);
				}
			}

			ArrayList searchfieldlist = new ArrayList();
			fieldlist = isExistsG_noAndCardno("A0101", table, g_no, cardno, fieldlist);
			int index_a0101 = 0;
			int index_g_no = 0;
			FieldItem field_g_no = new FieldItem();
			for (int i = 0; i < fieldlist.size(); i++) {
				FieldItem field_new = new FieldItem();
				FieldItem field = (FieldItem) fieldlist.get(i);
				field.setValue("");
				field.setViewvalue("");
				if ("1".equals(field.getState()))
					field.setVisible(true);
				else
					field.setVisible(false);
				/*
				 * if(field.getItemid().equals(ta+"01")||field.getItemid().equals
				 * (
				 * "nbase")||field.getItemid().equals("a0100")||field.getItemid(
				 * ).equals(ta+"09")||field.getItemid().equals(ta+"11")||field.
				 * getItemid
				 * ().equals(ta+"13")||field.getItemid().equals(ta+"15"))
				 * field.setVisible(false); else
				 * if(field.getItemid().equals("q1517"
				 * )||field.getItemid().equals("q1519"))
				 * field.setVisible(false); else field.setVisible(true);
				 */
				if (field.getItemid().equals(ta + "07"))
					this.getFormHM().put("visi", ta + "07");
				// 用来判断批量签批是M型还是A代码型
				if (field.getItemid().equals(ta + "11")) {
					String bflag = codesetidQ(ta, field.getItemid());
					this.getFormHM().put("bflag", bflag);
				}
				field_new = (FieldItem) field.cloneItem();
				if ("a0101".equalsIgnoreCase(field.getItemid()))
					index_a0101 = i;
				if (field.getItemid().equalsIgnoreCase(g_no)) {
					index_g_no = i;
					field_g_no = field_new;
				}
				searchfieldlist.add(field_new);
			}
			searchfieldlist.remove(index_g_no);
			searchfieldlist.add(index_a0101 + 1, field_g_no);

			this.getFormHM().put("table", table);
			this.getFormHM().put("searchfieldlist", searchfieldlist);
			// this.getFormHM().put("fieldlist", fieldlist);

			// StringBuffer sql_str = new StringBuffer();
			StringBuffer cond_str = new StringBuffer();
			String columns = "", columns2 = "", sel_columns = "";
			// sql_str.append("select ");
			for (int i = 0; i < fieldlist.size(); i++) {
				FieldItem field = (FieldItem) fieldlist.get(i);
				String itemid = field.getItemid();
				if (columns.length() < 1) {
					if ("A0100".equalsIgnoreCase(itemid) || "nbase".equalsIgnoreCase(itemid)) {
						columns = itemid;
						sel_columns = "Q." + itemid;
					}
					else if (itemid.equalsIgnoreCase(g_no) || itemid.equalsIgnoreCase(cardno)) {
						columns = itemid;
						sel_columns = "A." + itemid;
					}
					else {
						columns = itemid;
						sel_columns = itemid;
					}
				}
				else {
					if ("A0100".equalsIgnoreCase(itemid) || "nbase".equalsIgnoreCase(itemid)) {
						columns += "," + itemid;
						sel_columns += ",Q." + itemid;
					}
					else if (itemid.equalsIgnoreCase(g_no) || itemid.equalsIgnoreCase(cardno)) {
						columns += "," + itemid;
						sel_columns += ",A." + itemid;
					}
					else {
						columns += "," + itemid;
						sel_columns += "," + itemid;
					}
				}
			}

			columns = columns + ",state";
			sel_columns = sel_columns + ",Q.state";

			switch (Sql_switcher.searchDbServer()) {
			case Constant.ORACEL: {
				for (int i = 0; i < fieldlist.size(); i++) {
					FieldItem field = (FieldItem) fieldlist.get(i);
					if (columns2.length() < 1) {
						if ("D".equalsIgnoreCase(field.getItemtype())) {
							String format = "YYYY-MM-DD HH24:MI:SS";
							if (field.getItemlength() == 16) {
								format = "YYYY-MM-DD HH24:MI";
							}
							else if (field.getItemlength() == 10) {
								format = "YYYY-MM-DD";
							}
							else if (field.getItemlength() == 7) {
								format = "YYYY-MM";
							}
							else if (field.getItemlength() == 4) {
								format = "YYYY";
							}
							columns2 = "TO_CHAR(" + field.getItemid().toString() + ",'" + format + "') " + field.getItemid().toString();
						}
						else {
							columns2 = field.getItemid().toString();
						}
					}
					else {
						if ("D".equalsIgnoreCase(field.getItemtype())) {
							//szk bug7418
							String format = "YYYY-MM-DD HH24:MI:SS";
							if (field.getItemlength() == 16) {
								format = "YYYY-MM-DD HH24:MI";
							}
							else if (field.getItemlength() == 10) {
								format = "YYYY-MM-DD";
							}
							else if (field.getItemlength() == 7) {
								format = "YYYY-MM";
							}
							else if (field.getItemlength() == 4) {
								format = "YYYY";
							}
							columns2 += ",TO_CHAR(" + field.getItemid().toString() + ",'" + format + "') " + field.getItemid().toString();
						}
						else {
							columns2 += "," + field.getItemid().toString();
						}
					}
				}
				columns2 = columns2 + ",state";
				break;
			}
			}

			/** 考勤项目 */
			this.getFormHM().put("showtypelist", searchAllApp.getShowType(ta, this.getFrameconn()));
			/** 审批状态列表 */
			this.getFormHM().put("splist", searchAllApp.getSplist());

			/** 条件过滤 */
			StringBuffer whereINStr = new StringBuffer();
			cond_str.append(" from ");
			cond_str.append(table);
			cond_str.append(" where ");
			whereINStr.append(" 1=1 ");// 过滤条件
			/** 左边树节点代码 */
			if (!(code == null || "".equalsIgnoreCase(code))) {
				if ("1".equals(kind)) {
					whereINStr.append(" and e0122 like '" + code + "%'");
				}
				else if ("0".equals(kind)) {
					whereINStr.append(" and e01a1 like '" + code + "%'");
				}
				else {
					whereINStr.append(" and b0110 like '" + code + "%'");
				}
			}
			else {
				String privcode = RegisterInitInfoData.getKqPrivCode(userView);
				String codevalue = RegisterInitInfoData.getKqPrivCodeValue(userView);
				if ("UM".equalsIgnoreCase(privcode))
					whereINStr.append(" and e0122 like '" + codevalue + "%'");
				else if ("@K".equalsIgnoreCase(privcode))
					whereINStr.append(" and e01a1 like '" + codevalue + "%'");
				else if ("UN".equalsIgnoreCase(privcode))
					whereINStr.append(" and b0110 like '" + codevalue + "%'");
			}

			start_date = kqUtilsClass.getSafeCode(start_date);
			end_date = kqUtilsClass.getSafeCode(end_date);

			String select_type = (String) this.getFormHM().get("select_type");
			String where_c = "";
			if ("2".equals(select_flag)) {
				String selectResult = (String) hm.get("selectResult");
				where_c = " and " + new CodingAnalytical().analytical(selectResult);
				this.formHM.put("select_flag", "2");
			}
			else if ("1".equals(select_flag)) {
				this.formHM.put("select_flag", "1");
				// 姓名、工号、卡号模糊查询条件
				if ("0".equals(select_type)) {
					where_c = kqUtilsClass.getWhere_C(select_flag, "a0101", select_name);
				}
				else if ("1".equals(select_type)) {
					where_c = kqUtilsClass.getWhere_C(select_flag, g_no, select_name);
				}
				else if ("2".equals(select_type)) {
					where_c = kqUtilsClass.getWhere_C(select_flag, cardno, select_name);
				}
				else {
					this.getFormHM().put("select_name", "");
				}
			}

			// this.formHM.put("select_flag", "1"); 始终判断条件查询
			String cond0 = searchAllApp.getWhere2(table, start_date, end_date, kq_item, sp_flag, query_type, select_time_type);
			if (cond0.length() > 0) {
				whereINStr.append(" and ");
				whereINStr.append(cond0);
			}

			String where_is_excle = whereINStr.toString();
			whereINStr.append(" and " + Sql_switcher.isnull(ta + "17", "0") + "=0");

			String where_is = whereINStr.toString();
			ArrayList sql_db_list = new ArrayList();
			if (select_pre != null && select_pre.length() > 0 && !"all".equals(select_pre)) {
				sql_db_list.add(select_pre);
			}
			else {
				sql_db_list = kq_dbase_list;
			}

			cond0 = searchAllApp.getPrivWhere(kind, code, sql_db_list, table);
			if (cond0.length() > 0) {
				whereINStr.append(" and a0100 in (");
				whereINStr.append(cond0);
				whereINStr.append(")");
			}

			if (sql_db_list != null && sql_db_list.size() > 0) {
				whereINStr.append(" and (");
				for (int i = 0; i < sql_db_list.size(); i++) {
					whereINStr.append("nbase='" + sql_db_list.get(i).toString() + "'");
					if (i != sql_db_list.size() - 1)
						whereINStr.append(" or ");
				}
				whereINStr.append(")");
			}

			String sql = "";
			String sql_excle = "";
			String tablejoin = "";
			StringBuffer join = new StringBuffer();
			for (Iterator it = sql_db_list.iterator(); it.hasNext();) {
				String nbase = (String) it.next();
				if (join.length() < 1) {
					join.append("SELECT A0100,'" + nbase + "' nbase," + g_no + "," + cardno + " FROM " + nbase + "A01");
				}
				else {
					join.append(" UNION ALL SELECT A0100,'" + nbase + "' nbase," + g_no + "," + cardno + " FROM " + nbase + "A01");
				}
			}

			tablejoin = "SELECT " + sel_columns + " FROM " + table + " Q INNER JOIN (" + join + ") A ON Q.A0100 = A.A0100 AND Q.nbase = A.nbase";
			// if(this.userView.isSuper_admin())
			tablejoin = tablejoin + " and " + ta + "z5 <> '01'";
			if (where_c != null && where_c.length() > 0)
				tablejoin = tablejoin + where_c;

			if ("Q15".equalsIgnoreCase(table))
				tablejoin = tablejoin + " and Q.q1501 not in (select q1519 from Q15 where Q1519 IS NOT NULL and Q15Z5 = '01')";

			StringBuffer tablejoin_excle = new StringBuffer();
			if ("Q15".equalsIgnoreCase(table))
				tablejoin_excle = tablejoin_excle.append(" and Q.q1501 not in (select q1519 from Q15 where Q15Z5 = '01')");

			switch (Sql_switcher.searchDbServer()) {
			case Constant.ORACEL: {
				sql = searchAllApp.getSQLUnionWhere(kind, code, "(" + tablejoin + ") B", columns2, where_is, sql_db_list);// 海关修改:select_time_type=2时全部查询
				sql_excle = searchAllApp.getSQLUnionWhere(kind, code, "(" + tablejoin + ") B", columns2, where_is_excle, sql_db_list);
				break;
			}
			default: {
				sql = searchAllApp.getSQLUnionWhere(kind, code, "(" + tablejoin + ") B", columns, where_is, sql_db_list);
				sql_excle = searchAllApp.getSQLUnionWhere(kind, code, "(" + tablejoin + ") B", columns, where_is_excle, sql_db_list);
				break;
			}
			}

			selectAllOperate.allSelectApp(table, sql_db_list);
			String seal_date = (String) this.getFormHM().get("seal_date");// 封存的最后一天
			// 查询时不能查出封存期间以前的
			/*
			 * if(seal_date==null||seal_date.length()<=0) { ArrayList
			 * list=RegisterDate.getKqSealMaxDayList(this.getFrameconn());
			 * if(list!=null&&list.size()>0)
			 * seal_date=(String)list.get(list.size()-1); }
			 * if(seal_date!=null&&seal_date.length()>0) {
			 * cond_str.append(" and ("); cond_str.append(table);
			 * cond_str.append("05 "); cond_str.append(" >=");
			 * cond_str.append(Sql_switcher.dateValue(seal_date));
			 * cond_str.append(" or "); cond_str.append(table);
			 * cond_str.append("z1 "); cond_str.append(" >=");
			 * cond_str.append(Sql_switcher.dateValue(seal_date));
			 * cond_str.append(")"); }
			 */

			cond_str.append(whereINStr.toString());
			// fugleUnderwrite(table,where_is);//清空考勤申请中没有报批驳回的领导签名
			// System.out.println(sql);
			// xiexd 2014.09.11 将导出模板的sql语句保存至服务器
			String kq_sql = sql_excle + "" + "order by " + table + "Z1";
			this.userView.getHm().put("kq_sql_1", kq_sql);
			this.getFormHM().put("kq_dbase_list", kq_dbase_list);
			this.getFormHM().put("kq_list", kqUtilsClass.getKqNbaseList(kq_dbase_list));
			this.getFormHM().put("sql_str", sql);
			this.getFormHM().put("sql_excle", sql_excle);
			this.getFormHM().put("columns", columns);
			this.getFormHM().put("cond_str", "");
			this.getFormHM().put("orderby", "order by " + table + "Z1"); // 按起始时间排序
			this.getFormHM().put("seal_date", seal_date);
			this.getFormHM().put("returnURL", "/kq/app_check_in/all_app_data.do?b_search=link&encryptParam=" + PubFunc.encrypt("wo=" + frist + "&table=" + table));
			String mustercond = relatTableid + "`" + whereINStr; // 高级花名册参数
			if (where_c != null && where_c.length() > 0)
				mustercond += "`" + where_c;
			// 涉及SQL注入直接放进userView里
			this.userView.getHm().put("kq_condition", mustercond);
			this.getFormHM().put("relatTableid", relatTableid);
			this.getFormHM().put("start_date", start_date);
			this.getFormHM().put("end_date", end_date);
			// szk判断是否有q11z4
			String applytime = KqUtilsClass.getFieldByDesc("q11", ResourceFactory.getProperty("kq.class.applytime"));
			this.getFormHM().put("applytime", applytime);
			/** end. */
			// 显示部门层数
			Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.getFrameconn());
			String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if (uplevel == null || uplevel.length() == 0)
				uplevel = "0";
			this.getFormHM().put("uplevel", uplevel);
			String approved_delete = KqParam.getInstance().getApprovedDelete();// 已批申请登记数据是否可以删除;0:不删除；1：删除
			approved_delete = approved_delete != null && approved_delete.length() > 0 ? approved_delete : "1";
			this.getFormHM().put("approved_delete", approved_delete);

			String isExistIftoRest = KqUtilsClass.getFieldByDesc(table, ResourceFactory.getProperty("kq.self.app.workingdaysoff.yesorno"));
			if (isExistIftoRest != null && isExistIftoRest.length() > 0)
				this.getFormHM().put("isExistIftoRest", "1");
		}
		catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	private String codesetidQ(String teble, String itemid) {
		String codesetid = "";
		teble = teble.toUpperCase();
		itemid = itemid.toUpperCase();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		StringBuffer sql = new StringBuffer();
		RowSet rowSet = null;
		String itemtype = "";
		String codeid = "";
		try {
			sql.append("select itemtype,codesetid from t_hr_busifield  where fieldsetid='" + teble + "' and itemid='" + itemid + "'");
			rowSet = dao.search(sql.toString());
			while (rowSet.next()) {
				itemtype = rowSet.getString("itemtype");
				codeid = rowSet.getString("codesetid");
			}
			if ("A".equals(itemtype) && "0".equals(codeid)) {
				codesetid = "M";
			}
			else {
				codesetid = "A";
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			KqUtilsClass.closeDBResource(rowSet);
		}
		return codesetid;
	}

	private ArrayList isExistsG_noAndCardno(String endfield, String table, String g_no, String cardno, ArrayList list) {
		g_no = g_no.toLowerCase();
		cardno = cardno.toLowerCase();
		FieldItem addfield = new FieldItem();
		DbWizard db = new DbWizard(this.getFrameconn());
		for (int i = 0; i < list.size(); i++) {
			FieldItem field = (FieldItem) list.get(i);
			String itemid = field.getItemid();
			if (endfield != null && endfield.equalsIgnoreCase(itemid)) {
				if (list.toString().indexOf(cardno) == -1 && db.isExistField(table, cardno, false)) {
					addfield = new FieldItem();
					addfield.setFieldsetid(table);
					addfield.setItemid(cardno);
					addfield.setItemtype("A");
					addfield.setCodesetid("0");
					addfield.setState("1");
					addfield.setItemdesc("考勤卡号");
					list.add(i + 1, addfield);
				}
				if (list.toString().indexOf(g_no) == -1 && db.isExistField(table, g_no, false)) {
					addfield = new FieldItem();
					addfield.setFieldsetid(table);
					addfield.setItemid(g_no);
					addfield.setItemtype("A");
					addfield.setCodesetid("0");
					addfield.setState("1");
					addfield.setItemdesc("工号");
					list.add(i + 1, addfield);
				}
			}
		}
		return list;
	}
}
