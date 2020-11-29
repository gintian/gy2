package com.hjsj.hrms.transaction.police;

import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 * <p>
 * Title:PoliceYqdtTrans
 * </p>
 * <p>
 * Description:狱情动态
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * Create time:2010-2-7
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 */
public class PoliceYqdtTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		//模块名称
		String cyclename = (String) this.getFormHM().get("cyclename");
		// 获得管理权限范围
		String manaagePriv = this.userView.getManagePrivCode();
		String manamgePrivCode = this.userView.getManagePrivCodeValue();

		// 机构代码
		String uid = (String) hm.get("a_code");
		String codesetid = uid.substring(0, 2);
		String id = uid.substring(2);

		// sql语句
		StringBuffer sql = new StringBuffer();
		sql.append("select org.codeitemid,org.parentid,b.i9999,");
		sql.append("b.ext,b.createtime,b.title from organization");
		//sql.append(" as org left join b00 as b on org.codeitemid=b.b0110 ");//sunx：oracle不支持as
		sql.append("  org left join b00  b on org.codeitemid=b.b0110 ");
		// 获得周期参数,0为按年，1为按月，2为按季度
		String cycle = (String) this.getFormHM().get("cycle");
		if ("0".equals(cycle)) {
			String taskyear = (String) this.getFormHM().get("taskyear");

			// 默认当前时间的年份
			if (taskyear == null || "".equalsIgnoreCase(taskyear)) {
				SimpleDateFormat fo = new SimpleDateFormat("yyyy");
				taskyear = fo.format(new Date());
			}
			this.getFormHM().put("taskyear", taskyear);
			this.getFormHM().put("yearlist", this.getYearList(id));

			// 该年份的范围
			String end = String.valueOf(Integer.parseInt(taskyear) + 1);
			sql.append(" and b.createtime between ");
			sql.append(Sql_switcher.dateValue(taskyear));
			sql.append(" and " + Sql_switcher.dateValue(end));

		} else if ("1".equals(cycle)) {
			
			// 获得年份和月份
			String taskyear = (String) this.getFormHM().get("taskyear");
			String taskmonth = (String) this.getFormHM().get("taskmonth");
			
			// 初始值为当前时间的年份和月份
			if (taskyear == null || "".equalsIgnoreCase(taskyear)) {
				SimpleDateFormat fo = new SimpleDateFormat("yyyy");
				taskyear = fo.format(new Date());
			}
			if (taskmonth == null || "".equalsIgnoreCase(taskmonth)) {
				taskmonth = String.valueOf(new Date().getMonth() + 1);
			}
			
			// 获得时间范围
			String start = taskyear + "-" + taskmonth + "-" + "01";
			WeekUtils util = new WeekUtils();
			String end = util.lastMonthStr(Integer.parseInt(taskyear), Integer
					.parseInt(taskmonth));
			if (Integer.parseInt(taskmonth) == 12) {
				end = String.valueOf(Integer.parseInt(taskyear) + 1) + "-"
						+ "01-01";
			} else {
				end = taskyear + "-"
						+ String.valueOf(Integer.parseInt(taskmonth) + 1)
						+ "-01";
			}
			sql.append(" and b.createtime >= ");
			sql.append(Sql_switcher.dateValue(start));
			sql.append(" and b.createtime <" + Sql_switcher.dateValue(end));
			
			this.getFormHM().put("taskyear", taskyear);
			this.getFormHM().put("taskmonth", taskmonth);
			this.getFormHM().put("yearlist", this.getYearList(id));
			this.getFormHM().put("monthlist", this.getMonthList());

		} else if ("2".equals(cycle)) {
			// 获得年份和季度
			String taskyear = (String) this.getFormHM().get("taskyear");
			String taskweek = (String) this.getFormHM().get("taskweek");
			
			// 默认为当前时间的年份和季度
			if (taskyear == null || "".equalsIgnoreCase(taskyear)) {
				SimpleDateFormat fo = new SimpleDateFormat("yyyy");
				taskyear = fo.format(new Date());
			}
			if (taskweek == null || "".equalsIgnoreCase(taskweek)) {
				Calendar ca = Calendar.getInstance();
				ca.setTime(new Date());
				int month = ca.get(Calendar.MONTH) + 1;
				if (month % 3 != 0) {
					taskweek = String.valueOf(month / 3 + 1);
				} else {
					taskweek = String.valueOf(month / 3);
				}
			}
			
			// 时间范围
			String start = taskyear + "-"
					+ (Integer.parseInt(taskweek) * 3 - 2) + "-01";
			String end = "";
			if (Integer.parseInt(taskweek) == 4) {
				end = (Integer.parseInt(taskyear) + 1) + "-01-01";
			} else {
				end = taskyear + "-"
						+ ((Integer.parseInt(taskweek) + 1) * 3 - 2) + "-01";
			}
			sql.append(" and b.createtime >= ");
			sql.append(Sql_switcher.dateValue(start));
			sql.append(" and b.createtime < " + Sql_switcher.dateValue(end));
			
			this.getFormHM().put("taskyear", taskyear);
			this.getFormHM().put("taskweek", taskweek);
			this.getFormHM().put("yearlist", this.getYearList(id));
			this.getFormHM().put("weeklist",
					this.getWeekList());

		}
		// 文件flag
		if ("yqdt".equals(cyclename)) {
			sql.append(" and Upper(b.flag)='Y' ");
		} else if ("dept".equals(cyclename)) {
			sql.append("and Upper(b.flag)='T'");
		} 

		// 主表的条件
		sql.append(" where org.codesetid='UM' ");
		sql.append(" and org.codeitemid like '");
		sql.append(id);
		sql.append("%'");
		
		//根据设置显示组织机构
    	if (selectPoliceConstant().length() > 0 && "yqdt".equalsIgnoreCase(cyclename)) {
    		sql.append(" and codeitemid in (");
    		sql.append(selectPoliceConstant());
    		sql.append(") ");
    	}
    	
		// 机构名称查询时的条件
		String orgname = (String) this.getFormHM().get("orgname");
		this.getFormHM().remove("orgname");
		if (orgname != null && orgname.trim().length() > 0) {
			sql.append(" and org.codeitemdesc='");
			sql.append(orgname);
			sql.append("'");
		}

		// 登陆用户权限
		if (manamgePrivCode != null && manamgePrivCode.trim().length() > 0) {
			sql.append(" and org.codeitemid like '");
			sql.append(manamgePrivCode);
			sql.append("%' ");
		} else {
			sql.append(" and org.codeitemid like '");
			sql.append(this.userView.getUserDeptId());
			sql.append("%' ");
		}
		// 显示部门层数
		Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.getFrameconn());
		String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
		if (uplevel == null || uplevel.length() == 0)
			uplevel = "0";
        
		this.getFormHM().put("column",
				"codeitemid,parentid,i9999,ext,createtime,title");
		this.getFormHM().put("sqlstr", sql.toString());
		this.getFormHM().put("order_by",
				" order by org.codeitemid ,b.createtime");
		this.getFormHM().put("uplevel", uplevel);
		this.getFormHM().put("a_code", uid);

	}

	/**
	 * 获得年列表
	 * 
	 * @param id
	 * @return
	 */
	private List getYearList(String id) {
		List list = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("select createtime from B00 where b0110 like '");
		sql.append(id);
		sql.append("%'");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(sql.toString());
			int minyear = Integer.parseInt(PubFunc.FormatDate(new Date(),
					"yyyy"));
			if (this.frowset.next()) {
				int temp = Integer.parseInt(PubFunc.FormatDate(this.frowset
						.getDate("createtime"), "yyyy"));
				minyear = minyear > temp ? temp : minyear;
				while (this.frowset.next()) {
					temp = Integer.parseInt(PubFunc.FormatDate(this.frowset
							.getDate("createtime"), "yyyy"));
					minyear = minyear > temp ? temp : minyear;
				}
				int nowYear = Integer.parseInt(PubFunc.FormatDate(new Date(),
						"yyyy"));
				for (int i = nowYear; i >= minyear; i--) {
					CommonData data = new CommonData(String.valueOf(i), String
							.valueOf(i));
					list.add(data);
				}

			} else {
				CommonData data = new CommonData(PubFunc.FormatDate(new Date(),
						"yyyy"), PubFunc.FormatDate(new Date(), "yyyy"));
				list.add(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 获得月列表
	 * 
	 * @param id
	 * @return
	 */
	private List getMonthList() {
		List list = new ArrayList();
		for (int i = 1; i < 13; i++) {
			CommonData data = new CommonData(String.valueOf(i), String
					.valueOf(i));
			list.add(data);
		}
		return list;
	}

	/**
	 * 获得季度列表
	 * 
	 * @return
	 */
	private List getWeekList() {
		List list = new ArrayList();
		String[] week = { "一", "二", "三", "四" };
		for (int i = 1; i <= 4; i++) {
			CommonData data = new CommonData(String.valueOf(i), "第"
					+ week[i - 1] + "季度");
			list.add(data);
		}

		return list;
	}
	
	private String selectPoliceConstant() {
		String checkvalues = "";
		String sql = "select * from constant where constant='POLICE_SETYQDT'";
		Connection conn = null;
		ResultSet rs = null;
		try {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		
			frowset = dao.search(sql);
			if (frowset.next()) {
				checkvalues = frowset.getString("str_value");
				if (checkvalues == null) {
					checkvalues = "";
				}
			}
		if (checkvalues != null && checkvalues.trim().length() > 0) {
			String []check = checkvalues.split(",");
			checkvalues = "";
			for (int i = 0; i < check.length; i++) {
				if (i == 0) {
					checkvalues += ("'" + check[i].substring(2) + "'");
				} else {
					checkvalues += ("," + "'" +check[i].substring(2) + "'");
				}
			}
		}
		} catch (Exception e) {
			e.printStackTrace();
		} 
			
		return checkvalues;
	}

}
