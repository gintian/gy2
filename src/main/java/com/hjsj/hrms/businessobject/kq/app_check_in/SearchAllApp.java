package com.hjsj.hrms.businessobject.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.machine.ReconstructionKqField;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class SearchAllApp {

	private Connection conn;
	private UserView userView;

	public SearchAllApp() {

	}

	public SearchAllApp(Connection conn, UserView userView) {
		this.conn = conn;
		this.userView = userView;
	}

	public ArrayList getShowType(String ta_flag, Connection conn)
			throws GeneralException {
		ArrayList list = new ArrayList();
		if ("q11".equals(ta_flag)) {
			list = getList("1", conn);
		} else if ("q13".equals(ta_flag)) {
			list = getList("3", conn);
		} else if ("q15".equals(ta_flag)) {
			list = getList("0", conn);
		}
		return list;
	}

	/**
	 * 取得审批状态列表
	 */
	public ArrayList getSplist() {
		ArrayList list = new ArrayList();
		ArrayList codelist = AdminCode.getCodeItemList("23");
		CommonData datavo = new CommonData("all", ResourceFactory
				.getProperty("label.all"));
		list.add(datavo);
		for (int i = 0; i < codelist.size(); i++) {
			CodeItem codeitem = (CodeItem) codelist.get(i);
			String codevalue = codeitem.getCodeitem();
			// if(!(codevalue.equals("01")||codevalue.equals("02")||codevalue.equals("03")||codevalue.equals("07")||codevalue.equals("08")))
			// 不展现起草状态
			if (!("02".equals(codevalue) || "03".equals(codevalue)
					|| "07".equals(codevalue) || "08".equals(codevalue))) {
                continue;
            }
			
			if ("已报批".equalsIgnoreCase(codeitem.getCodename())) {
				codeitem.setCodename("待批");
			}
			
			CommonData data = new CommonData(codeitem.getCodeitem(), codeitem
					.getCodename());
			list.add(data);
		}
		return list;
	}

	/**
	 * 得到对应子集
	 * 
	 * @param ins
	 * @param conn
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getList(String ins, Connection conn)
			throws GeneralException {
		ArrayList list = new ArrayList();
		StringBuffer sql = new StringBuffer();
		ContentDAO dao = new ContentDAO(conn);
		CommonData datavo = new CommonData("all", ResourceFactory.getProperty("label.all"));
		list.add(datavo);
		RowSet rs = null;
		try {
			sql.append("SELECT codeitemid, codeitemdesc,parentid  FROM codeitem  where codesetid ='27'");
			sql.append(" and parentid like '" + ins + "%'");
			sql.append(" and codeitemid<>parentid");
			rs = dao.search(sql.toString());
			while (rs.next()) {
				if (rs.getString("codeitemid").length() > 1) {
					datavo = new CommonData(rs.getString("codeitemid"), rs.getString("codeitemdesc"));
					list.add(datavo);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		} finally {
		    KqUtilsClass.closeDBResource(rs);
		}
		return list;
	}

	/**
	 * chenmengqing added at 20070112 根据前台定义参数，生成过滤条件
	 * 
	 * @param table
	 * @param kq_start
	 * @param kq_end
	 * @param kqitem
	 * @param sp_flag
	 * @param query_type
	 * @return
	 */
	public String getWhere(String table, String kq_start, String kq_end,
			String kqitem, String sp_flag, String query_type)
			throws GeneralException {
		StringBuffer buf = new StringBuffer();
		String fieldname = null;
		if ("2".equalsIgnoreCase(query_type))// 按审批状态
		{
			fieldname = table + "z5";
			buf.append(" 1=1 ");
			if (!"all".equalsIgnoreCase(sp_flag)) {
				buf.append(" and " + fieldname);
				buf.append("='");
				buf.append(sp_flag);
				buf.append("'");
			}
			
			String column_z1 = table + "z1";
			String column_z3 = table + "z3";
			ArrayList datelist = RegisterDate.getKqDayList(this.conn);
			if (datelist != null && datelist.size() > 0) {
				kq_start = datelist.get(0).toString();
				kq_end = datelist.get(datelist.size() - 1).toString();
				if (kq_start != null && kq_start.length() > 0) {
                    kq_start = kq_start.replaceAll("\\.", "-");
                }
				if (kq_end != null && kq_end.length() > 0) {
                    kq_end = kq_end.replaceAll("\\.", "-");
                }
			}
			
			String z1 = kq_start + " 00:00:00";
			String z3 = kq_end + " 23:59:59";
			buf.append(" and ((" + column_z1 + ">=" + Sql_switcher.dateValue(z1));
			buf.append(" and " + column_z1 + "<=" + Sql_switcher.dateValue(z3) + ")");
			buf.append(" or (" + column_z3 + ">=" + Sql_switcher.dateValue(z1));
			buf.append(" and " + column_z3 + "<=" + Sql_switcher.dateValue(z3) + ")");
			buf.append(" or (" + column_z1 + "<=" + Sql_switcher.dateValue(z1));
			buf.append(" and " + column_z3 + ">=" + Sql_switcher.dateValue(z3) + ")");
			buf.append(")");
		} else if ("3".equalsIgnoreCase(query_type))// 按时间范围
		{
			String column_z1 = table + "z1";
			String column_z3 = table + "z3";
			String z1 = kq_start + " 00:00:00";
			String z3 = kq_end + " 23:59:59";
			buf.append(" ((" + column_z1 + ">=" + Sql_switcher.dateValue(z1));
			buf.append(" and " + column_z1 + "<=" + Sql_switcher.dateValue(z3) + ")");
			buf.append(" or (" + column_z3 + ">=" + Sql_switcher.dateValue(z1));
			buf.append(" and " + column_z3 + "<=" + Sql_switcher.dateValue(z3) + ")");
			buf.append(" or (" + column_z1 + "<=" + Sql_switcher.dateValue(z1));
			buf.append(" and " + column_z3 + ">=" + Sql_switcher.dateValue(z3) + ")");
			buf.append(")");

		} else// 按考勤项目
		{
			fieldname = table + "03";
			buf.append(" 1=1 ");
			if (!"all".equalsIgnoreCase(kqitem)) {
				buf.append(" and " + fieldname);
				buf.append("='");
				buf.append(kqitem);
				buf.append("'");
			}
			
			ArrayList datelist = RegisterDate.getKqDayList(this.conn);
			if (datelist != null && datelist.size() > 0) {
				kq_start = datelist.get(0).toString();
				kq_end = datelist.get(datelist.size() - 1).toString();
				
				if (kq_start != null && kq_start.length() > 0) {
                    kq_start = kq_start.replaceAll("\\.", "-");
                }
				
				if (kq_end != null && kq_end.length() > 0) {
                    kq_end = kq_end.replaceAll("\\.", "-");
                }
			}
			
			String column_z1 = table + "z1";
			String column_z3 = table + "z3";
			String z1 = kq_start + " 00:00:00";
			String z3 = kq_end + " 23:59:59";
			buf.append(" and ((" + column_z1 + ">=" + Sql_switcher.dateValue(z1));
			buf.append(" and " + column_z1 + "<=" + Sql_switcher.dateValue(z3) + ")");
			buf.append(" or (" + column_z3 + ">=" + Sql_switcher.dateValue(z1));
			buf.append(" and " + column_z3 + "<=" + Sql_switcher.dateValue(z3) + ")");
			buf.append(" or (" + column_z1 + "<=" + Sql_switcher.dateValue(z1));
			buf.append(" and " + column_z3 + ">=" + Sql_switcher.dateValue(z3) + ")");
			buf.append(")");
		}
		return buf.toString();
	}

	/**
	 * chenmengqing added at 20070112 根据前台定义参数，生成过滤条件
	 * 
	 * @param table
	 * @param kq_start
	 * @param kq_end
	 * @param kqitem
	 * @param sp_flag
	 * @param query_type
	 * @return
	 */
	public String getWhere2(String table, String kq_start, String kq_end,
			String kqitem, String sp_flag, String query_type,
			String select_time_type) throws GeneralException {
		StringBuffer buf = new StringBuffer();
		String fieldname = null;
		buf.append(" 1=1 ");
		String column_z1 = table + "z1";
		String column_z3 = table + "z3";
		String column_05 = table + "05";
		if ("9".equals(query_type)) {
			ArrayList datelist = RegisterDate.getKqDayList(this.conn);
			if (datelist != null && datelist.size() > 0) {
				kq_start = datelist.get(0).toString();
				kq_end = datelist.get(datelist.size() - 1).toString();
				if (kq_start != null && kq_start.length() > 0) {
                    kq_start = kq_start.replaceAll("\\.", "-");
                }
				/*
				 * if(kq_end!=null&&kq_end.length()>0)
				 * kq_end=kq_end.replaceAll("\\.","-");
				 */
				Date date = new Date();
				kq_end = DateUtils.format(date, "yyyy") + "-12-31";
			}
			
			String z1 = kq_start + " 00:00:00";
			String z3 = kq_end + " 23:59:59";
			if (select_time_type != null && "1".equals(select_time_type))// 按申请日期
			{
				buf.append(" and (" + column_05 + ">=" + Sql_switcher.dateValue(z1));
				buf.append(" and " + column_05 + "<=" + Sql_switcher.dateValue(z3) + ")");
			} else if (select_time_type != null && "0".equals(select_time_type))// 按起止时间
			{
				buf.append(" and ((" + column_z1 + ">="	+ Sql_switcher.dateValue(z1));
				buf.append(" and " + column_z1 + "<=" + Sql_switcher.dateValue(z3) + ")");
				buf.append(" or (" + column_z3 + ">=" + Sql_switcher.dateValue(z1));
				buf.append(" and " + column_z3 + "<=" + Sql_switcher.dateValue(z3) + ")");
				buf.append(" or (" + column_z1 + "<=" + Sql_switcher.dateValue(z1));
				buf.append(" and " + column_z3 + ">=" + Sql_switcher.dateValue(z3) + ")");
				buf.append(")");
			}
			
			fieldname = table + "z5";
			buf.append(" and " + fieldname);
            buf.append("<>'01'");
			return buf.toString();
		}
		if (kq_start != null && kq_start.length() > 0 && kq_end != null	&& kq_end.length() > 0) {
			String z1 = kq_start + " 00:00:00";
			String z3 = kq_end + " 23:59:59";
			if (select_time_type != null && "1".equals(select_time_type))// 按申请日期
			{
				buf.append(" and (" + column_05 + ">=" + Sql_switcher.dateValue(z1));
				buf.append(" and " + column_05 + "<=" + Sql_switcher.dateValue(z3) + ")");
			} else if (select_time_type != null && "0".equals(select_time_type))// 按起止时间
			{
				buf.append(" and ((" + column_z1 + ">="	+ Sql_switcher.dateValue(z1));
				buf.append(" and " + column_z1 + "<=" + Sql_switcher.dateValue(z3) + ")");
				buf.append(" or (" + column_z3 + ">=" + Sql_switcher.dateValue(z1));
				buf.append(" and " + column_z3 + "<=" + Sql_switcher.dateValue(z3) + ")");
				buf.append(" or (" + column_z1 + "<=" + Sql_switcher.dateValue(z1));
				buf.append(" and " + column_z3 + ">=" + Sql_switcher.dateValue(z3) + ")");
				buf.append(")");
			}
		} else {
			ArrayList datelist = RegisterDate.getKqDayList(this.conn);
			if (datelist != null && datelist.size() > 0) {
				kq_start = datelist.get(0).toString();				
				kq_end = datelist.get(datelist.size() - 1).toString();
				
				if (kq_start != null && kq_start.length() > 0) {
                    kq_start = kq_start.replaceAll("\\.", "-");
                }
				
				if (kq_end != null && kq_end.length() > 0) {
                    kq_end = kq_end.replaceAll("\\.", "-");
                }
			}
			
			String z1 = kq_start + " 00:00:00";
			String z3 = kq_end + " 23:59:59";
			if (select_time_type != null && "1".equals(select_time_type))// 按申请日期
			{
				buf.append(" and (" + column_05 + ">=" + Sql_switcher.dateValue(z1));
				buf.append(" and " + column_05 + "<=" + Sql_switcher.dateValue(z3) + ")");
			} else if (select_time_type != null && "0".equals(select_time_type))// 按起止时间
			{
				buf.append(" and ((" + column_z1 + ">="	+ Sql_switcher.dateValue(z1));
				buf.append(" and " + column_z1 + "<=" + Sql_switcher.dateValue(z3) + ")");
				buf.append(" or (" + column_z3 + ">=" + Sql_switcher.dateValue(z1));
				buf.append(" and " + column_z3 + "<=" + Sql_switcher.dateValue(z3) + ")");
				buf.append(" or (" + column_z1 + "<=" + Sql_switcher.dateValue(z1));
				buf.append(" and " + column_z3 + ">=" + Sql_switcher.dateValue(z3) + ")");
				buf.append(")");
			}
		}
		if (!"all".equalsIgnoreCase(sp_flag)) {
			fieldname = table + "z5";
			buf.append(" and " + fieldname);
			buf.append("='");
			buf.append(sp_flag);
			buf.append("'");
		} else {
			// 部门考勤申请页面里不展现起草状态
			fieldname = table + "z5";
			buf.append(" and " + fieldname);
			buf.append("<>'01'");
		}

		if (!"all".equalsIgnoreCase(kqitem)) {
			fieldname = table + "03";
			buf.append(" and " + fieldname);
			buf.append("='");
			buf.append(kqitem);
			buf.append("'");
		}
		return buf.toString();
	}

	/**
	 * 权限过滤条件 这种处理有可能会出现问题，不同的库的人员编号相同
	 * 
	 * @return
	 */
	public String getPrivWhere(String kind, String code,
			ArrayList kq_dbase_list, String table) {
		StringBuffer buf = new StringBuffer();
		if (this.userView.isSuper_admin()) {
            return buf.toString();
        }
		
		StringBuffer sql = new StringBuffer();
		String strWhere = null;
		
		/** 指定的考勤人员库，从参数取得 */
		if (code == null || code.length() <= 0) {
			LazyDynaBean bean = RegisterInitInfoData.getKqPrivCodeAndKind(userView);
			code = (String) bean.get("code");
			kind = (String) bean.get("kind");
		}
		
		ArrayList fieldlist = new ArrayList();
		try {
			for (int i = 0; i < kq_dbase_list.size(); i++)// for i loop end.
			{
				String expr = "1";
				String factor = "";
				if ("2".equals(kind)) {
					factor = "B0110=";
					if (code != null && code.length() > 0) {
						factor += code;
						factor += "%`";
					} else {
						expr = "1+2";
						factor += code;
						factor += "%`B0110=`";
					}
				} else if ("1".equals(kind)) {
					factor = "E0122=";
					if (code != null && code.length() > 0) {
						factor += code;
						factor += "%`";
					} else {
						expr = "1+2";
						factor += code;
						factor += "%`E0122=`";
					}
				} else if ("0".equals(kind)) {
					factor = "E01A1=";
					if (code != null && code.length() > 0) {
						factor += code;
						factor += "%`";
					} else {
						expr = "1+2";
						factor += code;
						factor += "%`E01A1=`";
					}
				} else {
					expr = "1+2";
					factor = "B0110=";
					kind = "2";
					if (userView.getManagePrivCodeValue() != null
							&& userView.getManagePrivCodeValue().length() > 0) {
                        factor += userView.getManagePrivCodeValue();
                    }
					factor += "%`B0110=`";
				}
				buf.append("select a0100 ");
				// buf.append(nbase);
				// buf.append("a01 from ");
				// strWhere=userView.getPrivSQLExpression("|",(String)dbpre.get(i),false,true,fieldlist);
				if (userView.getKqManageValue() != null	&& !"".equals(userView.getKqManageValue())) {
                    strWhere = userView.getKqPrivSQLExpression("", (String) kq_dbase_list.get(i), fieldlist);
                } else {
                    strWhere = userView.getPrivSQLExpression(expr + "|"	+ factor,
                            (String) kq_dbase_list.get(i),
                            false,
                            fieldlist);
                }
				buf.append(strWhere);
				sql.append("select a0100 from " + table + " where nbase='" + (String) kq_dbase_list.get(i) + "'");
				sql.append(" and a0100 in(select a0100 " + strWhere + ")");
				sql.append(" union ");
			}
			if (sql.length() > 0) {
                sql.setLength(sql.length() - 7);
            }

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return sql.toString();
	}

	/**
	 * 返回where条件
	 * 
	 * @param table
	 * @param kq_start
	 * @param kq_end
	 * @param ta
	 * @param showtype
	 * @param kq_dbase_list
	 * @param userView
	 * @return
	 */
	public String getWhereStr(String table, Date kq_start, Date kq_end,
			String ta, String showtype, ArrayList kq_dbase_list,
			UserView userView, String code, String kind) {
		StringBuffer cond_str = new StringBuffer();
		cond_str.append(table + "z1");
		cond_str.append(">=");
		cond_str.append(Sql_switcher.dateValue(kq_start + " 00:00:00"));
		cond_str.append(" and ");
		cond_str.append(table + "z1");
		cond_str.append("<=");
		cond_str.append(Sql_switcher.dateValue(kq_end + " 23:59:59"));
		if (code != null | code.length() <= 0) {
			code = RegisterInitInfoData.getKqPrivCodeValue(this.userView);
		}
		
		if ("1".equals(kind)) {
			cond_str.append(" and e0122 like '" + code + "%'");
		} else if ("0".equals(kind)) {
			cond_str.append(" and e01a1 like '" + code + "%'");
		} else {
			cond_str.append(" and b0110 like '" + code + "%'");
		}
		
		if (!"all".equals(showtype)) {
			cond_str.append(" and " + ta + "03='" + showtype + "'");
		}
		
		for (int i = 0; i < kq_dbase_list.size(); i++) {
			if (i > 0) {
				cond_str.append(" or ");
			} else {
				cond_str.append(" and ( ");
			}
			cond_str.append(" UPPER(nbase)='"
					+ kq_dbase_list.get(i).toString().toUpperCase() + "'");
			if (i == kq_dbase_list.size() - 1) {
                cond_str.append(")");
            }
		}
		
		for (int i = 0; i < kq_dbase_list.size(); i++) {
			String dbase = kq_dbase_list.get(i).toString();
			String whereIN = RegisterInitInfoData.getWhereINSql(userView, dbase);
			if (i > 0) {
				cond_str.append(" or ");
			} else {
				cond_str.append(" and ( ");
			}
			cond_str.append(" a0100 in(select a0100 " + whereIN + ") ");
			if (i == kq_dbase_list.size() - 1) {
                cond_str.append(")");
            }
		}
		return cond_str.toString();
	}

	public ArrayList getTableList(String table, Connection conn)
			throws GeneralException {
		ArrayList list = new ArrayList();
		StringBuffer sql = new StringBuffer();
		ContentDAO dao = new ContentDAO(conn);
		RowSet rs = null;
		try {
		    sql.append("select A.item_id, A.item_name");
		    sql.append(" from kq_item A LEFT JOIN codeitem B");
		    sql.append(" ON A.item_id=B.codeitemid");
		    sql.append(" where B.codesetid='27'");
			sql.append(" AND A.sdata_src='"	+ table.toUpperCase() + "'");
			sql.append(" ORDER BY B.a0000,B.codeitemid");
			rs = dao.search(sql.toString());
			list.add(new CommonData("", ""));
			while (rs.next()) {
				if (rs.getString("item_id").length() > 1) {
					CommonData datavo = new CommonData(rs.getString("item_id"),	rs.getString("item_name"));
					list.add(datavo);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		} finally {
		    KqUtilsClass.closeDBResource(rs);
		}
		return list;
	}

	// Q1515
	public ArrayList getOneList15(String ins, Connection conn)
			throws GeneralException {
		ArrayList list = new ArrayList();
		StringBuffer sql = new StringBuffer();
		ContentDAO dao = new ContentDAO(conn);
		RowSet rs = null;
		String codesetid = "";
		ins = ins.toUpperCase();
		try {
			// sql.append("SELECT codeitemid, codeitemdesc FROM codeitem where
			// codesetid ='72' and codeitemid like '");
			// sql.append(ins);
			// sql.append("%'");
			String sql2 = "select codesetid from t_hr_busifield where  itemid='"
					+ ins + "'";
			rs = dao.search(sql2);
			while (rs.next()) {
				codesetid = rs.getString("codesetid");
			}
			codesetid = codesetid.toUpperCase();
			sql.append("SELECT codeitemid, codeitemdesc  FROM codeitem  where codesetid ='"	+ codesetid + "'");
			rs = dao.search(sql.toString());
			list.add(new CommonData("", ""));
			while (rs.next()) {
				// if(rs.getString("codeitemid").length()>1)
				// {
				CommonData datavo = new CommonData(rs.getString("codeitemid"), rs.getString("codeitemdesc"));
				list.add(datavo);
				// }
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		} finally {
		    KqUtilsClass.closeDBResource(rs);
		}
		return list;
	}

	// Q1511
	public ArrayList getOneList11(String ins, Connection conn)
			throws GeneralException {
		ArrayList list = new ArrayList();
		StringBuffer sql = new StringBuffer();
		ContentDAO dao = new ContentDAO(conn);
		RowSet rs = null;
		String codesetid = "";
		ins = ins.toUpperCase();
		try {
			// sql.append("SELECT codeitemid, codeitemdesc FROM codeitem where
			// codesetid ='72' and codeitemid like '");
			// sql.append(ins);
			// sql.append("%'");
			String sql2 = "select codesetid from t_hr_busifield where  itemid='" + ins + "'";
			rs = dao.search(sql2);
			while (rs.next()) {
				codesetid = rs.getString("codesetid");
			}
			
			codesetid = codesetid.toUpperCase();
			sql.append("SELECT codeitemid, codeitemdesc  FROM codeitem  where codesetid ='"	+ codesetid + "'");
			rs = dao.search(sql.toString());
			list.add(new CommonData("", ""));
			while (rs.next()) {
				// if(rs.getString("codeitemid").length()>1)
				// {
				CommonData datavo = new CommonData(rs.getString("codeitemid"), rs.getString("codeitemdesc"));
				list.add(datavo);
				// }
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		} finally {
		    KqUtilsClass.closeDBResource(rs);
		}
		return list;
	}

	public ArrayList getNewFiledList(ArrayList filedlist, String ta) {

		if (filedlist != null && filedlist.size() > 0) {
			for (int i = 0; i < filedlist.size(); i++) {

				FieldItem field = (FieldItem) filedlist.get(i);
				field.setValue("");
				field.setViewvalue("");
				if (field.getItemid().equals(ta + "01")
						|| "nbase".equals(field.getItemid())
						|| "a0100".equals(field.getItemid())
						|| field.getItemid().equals(ta + "09")
						|| field.getItemid().equals(ta + "11")
						|| field.getItemid().equals(ta + "13")
						|| field.getItemid().equals(ta + "15")
						|| field.getItemid().equals(ta + "07")) {
                    field.setVisible(false);
                } else {
                    field.setVisible(true);
                }
			}
		}
		return filedlist;
	}

	/**
	 * 驳回
	 * 
	 * @param table
	 * @param idea
	 * @param infolist
	 * @throws GeneralException
	 */
	public void upData_overrule(String table, String idea, ArrayList infolist)
			throws GeneralException {
		String ta = table.toLowerCase();
		ContentDAO dao = new ContentDAO(this.conn);
		String idea_coulmn = getIdeaCoulmn(ta);
		String lean_column = getLeanCoulmn(ta);
		StringBuffer upl = null;
		try {

			for (int i = 0; i < infolist.size(); i++) {
				LazyDynaBean rec = (LazyDynaBean) infolist.get(i);
				upl = new StringBuffer();
				upl.append(" update ");
				upl.append(table);
				upl.append(" set ");
				if (userView.isOrgLeader()) {
					upl.append(ta + "15='");
					upl.append(idea);
					upl.append("',");
					upl.append(ta + "13='");
					upl.append(this.userView.getUserFullName());
					upl.append("',");
				} else {

					upl.append(idea_coulmn + "='");
					upl.append(idea);
					upl.append("',");
					upl.append(lean_column + "='");
					upl.append(this.userView.getUserFullName());
					upl.append("',");
				}
				upl.append(ta + "z0='02',");
				upl.append(ta + "z5='");
				upl.append("07");
				upl.append("' where ");
				upl.append(ta + "01='");
				upl.append(rec.get(ta + "01").toString());
				upl.append("'");

				dao.update(upl.toString());
			}
		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}
	}

	/**
	 * 审批
	 * 
	 * @param table
	 * @param radio
	 * @param idea
	 * @param infolist
	 * @throws GeneralException
	 */
	public void upData_audit(String table, String radio, String idea,
			ArrayList infolist) throws GeneralException {
		String ta = table.toLowerCase();
		ContentDAO dao = new ContentDAO(this.conn);
		String idea_coulmn = getIdeaCoulmn(ta);
		String lean_column = getLeanCoulmn(ta);
		StringBuffer upl = null;
		try {

			for (int i = 0; i < infolist.size(); i++) {
				LazyDynaBean rec = (LazyDynaBean) infolist.get(i);
				upl = new StringBuffer();
				upl.append(" update ");
				upl.append(table);
				upl.append(" set ");
				if (userView.isOrgLeader()) {
					upl.append(ta + "15='");
					upl.append(idea);
					upl.append("',");
					upl.append(ta + "13='");
					upl.append(this.userView.getUserFullName());
					upl.append("',");
				} else {

					upl.append(idea_coulmn + "='");
					upl.append(idea);
					upl.append("',");
					upl.append(lean_column + "='");
					upl.append(this.userView.getUserFullName());
					upl.append("',");
				}
				upl.append(ta + "z0='");
				upl.append(radio);
				upl.append("',");
				upl.append(ta + "z5='");
				upl.append("03");
				upl.append("' where ");
				upl.append(ta + "01='");
				upl.append(rec.get(ta + "01").toString());
				upl.append("'");
				upl.append(" and " + ta + "z5<>'03'");

				dao.update(upl.toString());
			}
		} catch (Exception sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		}
	}

	/**
	 * 得到级别意见字段
	 * 
	 * @param ta
	 * @return
	 */
	public String getIdeaCoulmn(String ta) {
		String privCode = RegisterInitInfoData.getKqPrivCode(userView);
		String idea_coulmn = ta + "11";
		if (privCode == null || privCode.length() <= 0) {
			idea_coulmn = ta + "11";
		} else if ("UM".equals(privCode.toUpperCase())) {
			idea_coulmn = ta + "11";
		} else if ("UN".equals(privCode.toUpperCase())) {
			idea_coulmn = ta + "15";
		}
		return idea_coulmn;
	}

	/**
	 * 得到领导级别
	 * 
	 * @param ta
	 * @return
	 */
	public String getLeanCoulmn(String ta) {
		String privCode = RegisterInitInfoData.getKqPrivCode(userView);
		String idea_coulmn = ta + "09";
		if (privCode == null || privCode.length() <= 0) {
			idea_coulmn = ta + "09";
		} else if ("UM".equals(privCode.toUpperCase())) {
			idea_coulmn = ta + "09";
		} else if ("UN".equals(privCode.toUpperCase())) {
			idea_coulmn = ta + "13";
		}
		return idea_coulmn;
	}

	public ArrayList opin_Yearlist(ArrayList yearlist) throws GeneralException {
		if (yearlist == null || yearlist.size() <= 0) {
			RegisterDate registerDate = new RegisterDate();
			yearlist = registerDate.getAllYearListVo(this.conn);
		}
		return yearlist;
	}

	public String getFirstOfList(ArrayList list) {
		CommonData vo = (CommonData) list.get(0);

		return vo.getDataValue();
	}

	/**
	 * 权限过滤条件 这种处理有可能会出现问题，不同的库的人员编号相同
	 * 
	 * @return
	 */
	public String getSQLUnionWhere(String kind, String code, String table,
			String column, String where_is, ArrayList dbpre) {
		StringBuffer buf = new StringBuffer();
		if (this.userView.isSuper_admin()) {
			buf.append("select " + column + " from " + table);
			buf.append(" where " + where_is);
			if (dbpre != null && dbpre.size() > 0) {
				buf.append(" and (");
				for (int i = 0; i < dbpre.size(); i++) {
					buf.append("nbase='" + dbpre.get(i).toString() + "'");
					if (i != dbpre.size() - 1) {
                        buf.append(" or ");
                    }
				}
				buf.append(")");
			}

			return buf.toString();
		}
		StringBuffer sql = new StringBuffer();
		String strWhere = null;
		/** 指定的考勤人员库，从参数取得 */
		if (code == null || code.length() <= 0) {
			LazyDynaBean bean = RegisterInitInfoData
					.getKqPrivCodeAndKind(userView);
			code = (String) bean.get("code");
			kind = (String) bean.get("kind");
		}
		ArrayList fieldlist = new ArrayList();
		try {
			for (int i = 0; i < dbpre.size(); i++)// for i loop end.
			{
				String expr = "1";
				String factor = "";
				if ("2".equals(kind)) {
					factor = "B0110=";
					if (code != null && code.length() > 0) {
						factor += code;
						factor += "%`";
					} else {
						expr = "1+2";
						factor += code;
						factor += "%`B0110=`";
					}
				} else if ("1".equals(kind)) {
					factor = "E0122=";
					if (code != null && code.length() > 0) {
						factor += code;
						factor += "%`";
					} else {
						expr = "1+2";
						factor += code;
						factor += "%`E0122=`";
					}
				} else if ("0".equals(kind)) {
					factor = "E01A1=";
					if (code != null && code.length() > 0) {
						factor += code;
						factor += "%`";
					} else {
						expr = "1+2";
						factor += code;
						factor += "%`E01A1=`";
					}
				} else {
					expr = "1+2";
					factor = "B0110=";
					kind = "2";
					if (userView.getManagePrivCodeValue() != null
							&& userView.getManagePrivCodeValue().length() > 0) {
                        factor += userView.getManagePrivCodeValue();
                    }
					factor += "%`B0110=`";
				}
				buf.append("select a0100 ");
				// buf.append(nbase);
				// buf.append("a01 from ");
				// strWhere=userView.getPrivSQLExpression("|",(String)dbpre.get(i),false,true,fieldlist);
				if (userView.getKqManageValue() != null
						&& !"".equals(userView.getKqManageValue())) {
                    strWhere = userView.getKqPrivSQLExpression("",
                            (String) dbpre.get(i), fieldlist);
                } else {
                    strWhere = userView.getPrivSQLExpression(expr + "|"
                            + factor, (String) dbpre.get(i), false, fieldlist);
                }

				buf.append(strWhere);
				sql.append("select " + column + " from " + table
						+ " where nbase='" + (String) dbpre.get(i) + "'");
				sql.append(" and a0100 in(select a0100 " + strWhere + ")");
				sql.append(" and " + where_is);

				sql.append(" union all ");
			}
			if (sql.length() > 0) {
                sql.setLength(sql.length() - 11);
            }

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return sql.toString();
	}

	/**
	 * 数据重构
	 * 
	 * @param table
	 * @throws GeneralException
	 */
	public void reconstructionApp(String table) throws GeneralException {
		ReconstructionKqField reconstructionKqField = new ReconstructionKqField(this.conn);
		String ta = table.toLowerCase();
		ArrayList list = new ArrayList();
		Field temp = new Field(ta + "04", "参考班次");
		temp.setDatatype(DataType.STRING);
		temp.setLength(2);
		temp.setKeyable(false);
		temp.setVisible(false);
		list.add(temp);
		if (!reconstructionKqField.checkFieldSave(ta, ta + "04")) {
			if (!reconstructionKqField.ceaterField_originality(list, table)) {
                throw GeneralExceptionHandler.Handle(new GeneralException("",
                        "重构数据表错误", "", ""));
            }
			try {
				StringBuffer sql = new StringBuffer();
				sql
						.append("insert into  t_hr_busiField (fieldsetid, itemid, displayid, itemtype, itemdesc, itemlength,");
				sql
						.append("decimalwidth, codesetid, expression, displaywidth, reserveitem, auditingformula,");
				sql
						.append("auditinginformation, state, useflag, keyflag, itemmemo, codeflag,ownflag)");
				sql.append(" values ");
				sql
						.append("('"
								+ table
								+ "', '"
								+ table
								+ "04', 20, 'N', '参考班次', 8, 0, 0, Null, 10, Null, Null, Null, '1',");
				sql.append("'1', '0', Null, '0','1')");
				ContentDAO dao = new ContentDAO(this.conn);
				dao.insert(sql.toString(), new ArrayList());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		list = new ArrayList();
		temp = new Field(ta + "Z7", "审批时间");
		temp.setDatatype(DataType.DATETIME);
		temp.setKeyable(false);
		temp.setVisible(false);
		list.add(temp);
		if (!reconstructionKqField.checkFieldSave(ta, ta + "z7")) {
			if (!reconstructionKqField.ceaterField_originality(list, table)) {
                throw GeneralExceptionHandler.Handle(new GeneralException("",
                        "重构数据表错误", "", ""));
            }
			try {
				StringBuffer sql = new StringBuffer();
				sql
						.append("insert into  t_hr_busiField (fieldsetid, itemid, displayid, itemtype, itemdesc, itemlength,");
				sql
						.append("decimalwidth, codesetid, expression, displaywidth, reserveitem, auditingformula,");
				sql
						.append("auditinginformation, state, useflag, keyflag, itemmemo, codeflag,ownflag)");
				sql.append(" values ");
				sql
						.append("('"
								+ table
								+ "', '"
								+ table
								+ "Z7', 21, 'D', '审批时间', 16, 0, Null, Null, 13, Null, Null, Null, '1',");
				sql.append("'1', '0', Null, '0','1')");
				ContentDAO dao = new ContentDAO(this.conn);
				dao.insert(sql.toString(), new ArrayList());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 加班时间添加一个加班休息扣除数
	 * 
	 * @param table
	 * @return
	 */
	public String isDeductResttime(String table) {
		if (table == null || table.length() <= 0
				|| !"q11".equalsIgnoreCase(table)) {
            return "";
        }
		ArrayList fieldList = DataDictionary.getFieldList(table,
				Constant.USED_FIELD_SET);// 字段名
		String desc = "";
		for (int i = 0; i < fieldList.size(); i++)// 休息扣除数
		{
			FieldItem field = (FieldItem) fieldList.get(i);
			desc = field.getItemdesc();
			if (desc != null && "休息扣除数".equalsIgnoreCase(desc)) {
                return field.getItemid();
            }
		}
		return "";
	}
	
	/**
	 * added at 2012.08.10 根据前台定义参数，查询某人假期剩余天数
	 * @param conn
	 * @param nbase
	 * @param A0100
	 * @param year
	 * @return
	 */
	public static float getLeftDaysOfYearVacation(Connection conn,String app_type,String peopleName,String year){
		float leftdays0fvacation = 0;
		StringBuffer sb = new StringBuffer();
		StringBuffer sb1 = new StringBuffer();
		String nbase = "";
		String A0100 = "";
		sb1.append("select nbase,A0100 from Q17 where Q1709 = '" + app_type + "' " + "and Q1701 = '" + year + "'");
		ContentDAO cd = new ContentDAO(conn);
		RowSet rs = null;
		RowSet rs1 = null;
		String balance = "";
        ArrayList fieldList = DataDictionary
                    .getFieldList("q17", Constant.USED_FIELD_SET);
        for (int i = 0; i < fieldList.size(); i++) {
            FieldItem item = (FieldItem) fieldList.get(i);
            if ("结余剩余".equalsIgnoreCase(item.getItemdesc())) {
                balance = item.getItemid();
            }
        }
		try {
			rs1 = cd.search(sb1.toString());
			while(rs1.next()){
				nbase = rs1.getString(1);
				A0100 = rs1.getString(2);
				if((nbase+A0100).equals(peopleName)){
					sb.append("select Q1707 ");
					if (null != balance && !"".equals(balance)) {
                        sb.append("," + balance );
                    }
					sb.append(" from Q17 where nbase = '" + nbase + "' and Q1709 = '" + app_type + "' and A0100 = '" + A0100 + "' and Q1701 = '" + year + "'");
					rs = cd.search(sb.toString());
					while(rs.next()){
						leftdays0fvacation = rs.getFloat(1);
						if (null != balance && !"".equals(balance)) {
                            leftdays0fvacation = leftdays0fvacation + rs.getFloat(2);
                        }
						return leftdays0fvacation;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			KqUtilsClass.closeDBResource(rs);
			KqUtilsClass.closeDBResource(rs1);
		}	
		return -1;
	}
}
