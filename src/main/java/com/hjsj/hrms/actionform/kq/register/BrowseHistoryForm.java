package com.hjsj.hrms.actionform.kq.register;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class BrowseHistoryForm extends FrameForm {
	private String treeCode;
	private String coursedate;// 考勤期间
	private String code;// 连接级别
	private String columns;
	private String year;// 考勤期间
	private String duration;
	private ArrayList yearlist = new ArrayList();// 考勤期间年份list
	private ArrayList durationlist = new ArrayList();// 考勤期间月份list
	private ArrayList courselist = new ArrayList();// 考勤期间list
	private ArrayList fielditemlist = new ArrayList();
	private String sqlstr;
	private String strwhere;
	private String orderby;
	private String A0100;
	private String dbase;
	private String orgvali;
	private String kind;
	private String codesetid;
	private String orgsumvali;
	private String kq_period;
	private String start_date;
	private String end_date;
	private String kq_duration;
	private ArrayList datelist = new ArrayList();// 日期list
	private String registerdate;// 登记日期
	private ArrayList kq_dbase_list = new ArrayList();
	private String workcalendar;
	private String sessiondate;
	private ArrayList sessionlist = new ArrayList();
	private String condition;// 高级花名册打印的条件
	private String returnURL;// 返回的连接
	private String relatTableid;// 高级花名册对应的单表名称
	private String error_message;
	private String error_flag;
	private String error_return;
	private String error_stuts;
	private ArrayList kq_list = new ArrayList();// 人员库
	private String select_name;// 筛选名字
	private String select_flag;// 筛选表示
	private String select_type = "0";
	private String select_pre;
	private String selectys;// 转换小时 1=默认；2=HH:MM
	/** 考勤规则* */
	private HashMap kqItem_hash = new HashMap();
	/** 历史查询统计分析 * */
	private String workstat; // 开始时间 下拉
	private String workjs; // 结束时间 下拉
	private String jsdatetime;// 结束时间
	private ArrayList kqq03list = new ArrayList();// list
	private String sqlstrs;
	private String strwheres;
	private String orderbys;
	private String columnss;
	private String sanshu;
	private String uplevel;
	private String backy;
	private String codetj;
	private String start_datetj;
	private String end_datetj;
	private String registertime;// 登记日期 统计分析
	private String tableValue;
	private ArrayList searchfieldlist = new ArrayList();// list

	private ArrayList kqnamelsit = new ArrayList();// list
	private String itemid;
	private String b01101;
	private String flag;
	private ArrayList slist = new ArrayList();
	private String dbpre;

	public String getSelect_name() {
		return select_name;
	}

	public void setSelect_name(String select_name) {
		this.select_name = select_name;
	}

	public ArrayList getKq_list() {
		return kq_list;
	}

	public void setKq_list(ArrayList kq_list) {
		this.kq_list = kq_list;
	}

	public String getSelect_flag() {
		return select_flag;
	}

	public void setSelect_flag(String select_flag) {
		this.select_flag = select_flag;
	}

	public String getSelect_pre() {
		return select_pre;
	}

	public void setSelect_pre(String select_pre) {
		this.select_pre = select_pre;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getRelatTableid() {
		return relatTableid;
	}

	public void setRelatTableid(String relatTableid) {
		this.relatTableid = relatTableid;
	}

	public String getReturnURL() {
		return returnURL;
	}

	public void setReturnURL(String returnURL) {
		this.returnURL = returnURL;
	}

	public ArrayList getDatelist() {
		return datelist;
	}

	public void setDatelist(ArrayList datelist) {
		this.datelist = datelist;
	}

	public String getRegisterdate() {
		return registerdate;
	}

	public void setRegisterdate(String registerdate) {
		this.registerdate = registerdate;
	}

	@Override
    public void outPutFormHM() {
		this.setTreeCode((String) this.getFormHM().get("treeCode"));
		this.setCode((String) this.getFormHM().get("code"));
		this.setCourselist((ArrayList) this.getFormHM().get("courselist"));
		this.setYear((String) this.getFormHM().get("year"));
		this.setDuration((String) this.getFormHM().get("duration"));
		this.setYearlist((ArrayList) this.getFormHM().get("yearlist"));
		this.setDurationlist((ArrayList) this.getFormHM().get("durationlist"));
		this.setSqlstr((String) this.getFormHM().get("sqlstr"));
		this.setColumns((String) this.getFormHM().get("columns"));
		this.setStrwhere((String) this.getFormHM().get("strwhere"));
		this.setOrderby((String) this.getFormHM().get("orderby"));
		this.setKind((String) this.getFormHM().get("kind"));
		this
				.setFielditemlist((ArrayList) this.getFormHM().get(
						"fielditemlist"));
		this.setCoursedate((String) this.getFormHM().get("coursedate"));
		this.setRegisterdate((String) this.getFormHM().get("registerdate"));
		this.setDatelist((ArrayList) this.getFormHM().get("datelist"));
		this
				.setKq_dbase_list((ArrayList) this.getFormHM().get(
						"kq_dbase_list"));
		this.setWorkcalendar((String) this.getFormHM().get("workcalendar"));
		this.setStart_date((String) this.getFormHM().get("start_date"));
		this.setEnd_date((String) this.getFormHM().get("end_date"));
		this.setSessiondate((String) this.getFormHM().get("sessiondate"));
		this.setSessionlist((ArrayList) this.getFormHM().get("sessionlist"));
		this.setRelatTableid((String) this.getFormHM().get("relatTableid"));
		this.setCondition((String) this.getFormHM().get("condition"));
		this.setReturnURL((String) this.getFormHM().get("returnURL"));
		this.setError_flag((String) this.getFormHM().get("error_flag"));
		this.setError_message((String) this.getFormHM().get("error_message"));
		this.setError_return((String) this.getFormHM().get("error_return"));
		this.setError_stuts((String) this.getFormHM().get("error_stuts"));
		this.setKq_list((ArrayList) this.getFormHM().get("kq_list"));
		this.setSelect_name((String) this.getFormHM().get("select_name"));
		this.setSelect_flag((String) this.getFormHM().get("select_flag"));
		this.setSelect_pre((String) this.getFormHM().get("select_pre"));
		this.setKqItem_hash((HashMap) this.getFormHM().get("kqItem_hash"));
		this.setSelectys((String) this.getFormHM().get("selectys"));
		this.setWorkstat((String) this.getFormHM().get("workstat"));
		this.setWorkjs((String) this.getFormHM().get("workjs"));
		this.setJsdatetime((String) this.getFormHM().get("jsdatetime"));
		this.setKqq03list((ArrayList) this.getFormHM().get("kqq03list"));
		this.setSqlstrs((String) this.getFormHM().get("sqlstrs"));
		this.setStrwheres((String) this.getFormHM().get("strwheres"));
		this.setOrderbys((String) this.getFormHM().get("orderbys"));
		this.setColumnss((String) this.getFormHM().get("columnss"));
		this.setSanshu((String) this.getFormHM().get("sanshu"));
		this.setKqnamelsit((ArrayList) this.getFormHM().get("kqnamelsit"));
		this.setUplevel((String) this.getFormHM().get("uplevel"));
		this.setBacky((String) this.getFormHM().get("backy"));
		this.setCodetj((String) this.getFormHM().get("codetj"));
		this.setStart_datetj((String) this.getFormHM().get("start_datetj"));
		this.setEnd_datetj((String) this.getFormHM().get("end_datetj"));
		this.setRegistertime((String) this.getFormHM().get("registertime"));
		this.setTableValue((String) this.getFormHM().get("tableValue"));
		this.setSearchfieldlist((ArrayList) this.getFormHM().get(
				"searchfieldlist"));
		this.setFlag((String) this.getFormHM().get("flag"));
		this.setSlist((ArrayList) this.getFormHM().get("slist"));
		this.setDbpre((String) this.getFormHM().get("dbpre"));
		this.setSelect_type((String) this.getFormHM().get("select_type"));
		this.setKq_duration((String)this.getFormHM().get("kq_duration"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("code", code);
		this.getFormHM().put("coursedate", coursedate);
		this.getFormHM().put("kind", kind);
		this.getFormHM().put("year", year);
		this.getFormHM().put("duration", duration);
		this.getFormHM().put("courselist", courselist);
		this.getFormHM().put("yearlist", yearlist);
		this.getFormHM().put("datelist", datelist);
		this.getFormHM().put("start_date", start_date);
		this.getFormHM().put("end_date", end_date);
		this.getFormHM().put("kq_dbase_list", kq_dbase_list);
		this.getFormHM().put("registerdate", registerdate);
		this.getFormHM().put("courselist", courselist);
		this.getFormHM().put("sessionlist", sessionlist);
		this.getFormHM().put("sessiondate", sessiondate);
		this.getFormHM().put("error_stuts", this.getError_stuts());
		this.getFormHM().put("error_flag", this.getError_flag());
		this.getFormHM().put("error_return", this.getError_return());
		this.getFormHM().put("select_name", this.getSelect_name());
		this.getFormHM().put("select_flag", this.getSelect_flag());
		this.getFormHM().put("select_pre", this.getSelect_pre());
		this.getFormHM().put("selectys", this.getSelectys());
		this.getFormHM().put("jsdatetime", jsdatetime);
		this.getFormHM().put("kqq03list", kqq03list);
		this.getFormHM().put("kqnamelsit", kqnamelsit);
		this.getFormHM().put("codetj", codetj);
		this.getFormHM().put("start_datetj", start_datetj);
		this.getFormHM().put("end_datetj", end_datetj);
		this.getFormHM().put("registertime", registertime);
		this.getFormHM().put("searchfieldlist", searchfieldlist);
		this.getFormHM().put("slist", slist);
		this.getFormHM().put("dbpre", dbpre);
		this.getFormHM().put("select_type", this.select_type);
	}

	public String getA0100() {
		return A0100;
	}

	public void setA0100(String a0100) {
		A0100 = a0100;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCodesetid() {
		return codesetid;
	}

	public void setCodesetid(String codesetid) {
		this.codesetid = codesetid;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getCoursedate() {
		return coursedate;
	}

	public void setCoursedate(String coursedate) {
		this.coursedate = coursedate;
	}

	public ArrayList getCourselist() {
		return courselist;
	}

	public void setCourselist(ArrayList courselist) {
		this.courselist = courselist;
	}

	public String getDbase() {
		return dbase;
	}

	public void setDbase(String dbase) {
		this.dbase = dbase;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	public ArrayList getFielditemlist() {
		return fielditemlist;
	}

	public void setFielditemlist(ArrayList fielditemlist) {
		this.fielditemlist = fielditemlist;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getKq_duration() {
		return kq_duration;
	}

	public void setKq_duration(String kq_duration) {
		this.kq_duration = kq_duration;
	}

	public String getKq_period() {
		return kq_period;
	}

	public void setKq_period(String kq_period) {
		this.kq_period = kq_period;
	}

	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

	public String getOrgsumvali() {
		return orgsumvali;
	}

	public void setOrgsumvali(String orgsumvali) {
		this.orgsumvali = orgsumvali;
	}

	public String getOrgvali() {
		return orgvali;
	}

	public void setOrgvali(String orgvali) {
		this.orgvali = orgvali;
	}

	public String getSqlstr() {
		return sqlstr;
	}

	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}

	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	public String getStrwhere() {
		return strwhere;
	}

	public void setStrwhere(String strwhere) {
		this.strwhere = strwhere;
	}

	public String getTreeCode() {
		return treeCode;
	}

	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}

	public ArrayList getKq_dbase_list() {
		return kq_dbase_list;
	}

	public void setKq_dbase_list(ArrayList kq_dbase_list) {
		this.kq_dbase_list = kq_dbase_list;
	}

	public String getWorkcalendar() {
		return workcalendar;
	}

	public void setWorkcalendar(String workcalendar) {
		this.workcalendar = workcalendar;
	}

	public String getSessiondate() {
		return sessiondate;
	}

	public void setSessiondate(String sessiondate) {
		this.sessiondate = sessiondate;
	}

	public ArrayList getSessionlist() {
		return sessionlist;
	}

	public void setSessionlist(ArrayList sessionlist) {
		this.sessionlist = sessionlist;
	}

	public String getError_flag() {
		return error_flag;
	}

	public void setError_flag(String error_flag) {
		if (error_flag == null || error_flag.length() <= 0)
			error_flag = "0";
		this.error_flag = error_flag;
	}

	public String getError_message() {
		return error_message;
	}

	public void setError_message(String error_message) {
		this.error_message = error_message;
	}

	public String getError_return() {
		return error_return;
	}

	public void setError_return(String error_return) {
		this.error_return = error_return;
	}

	public String getError_stuts() {
		return error_stuts;
	}

	public void setError_stuts(String error_stuts) {
		this.error_stuts = error_stuts;
	}

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if ("/kq/register/history/dailybrowse".equals(arg0.getPath())
				&& arg1.getParameter("b_search") != null) {
			if (this.getPagination() != null)
				this.getPagination().firstPage();// ?
			this.setCode("");
			this.setSessiondate("");
			this.setSessionlist(null);
			this.setKind("");
			this.setCoursedate("");
			this.setYear("");
			this.setDuration("");
			this.getFormHM().clear();
			
			arg1.setAttribute("targetWindow", "0");
		}
		if ("/kq/register/history/dailyorgbrowse".equals(arg0.getPath())
				&& arg1.getParameter("b_search") != null) {
			if (this.getPagination() != null)
				this.getPagination().firstPage();// ?
			this.setCode("");
			this.setSessiondate("");
			this.setCoursedate("");
			this.setYear("");
			this.setDuration("");
			this.setSessionlist(null);
			this.setKind("");
			this.getFormHM().clear();
		}
		if ("/kq/register/history/dailybrowsedata".equals(arg0.getPath())
				&& arg1.getParameter("b_search") != null) {
			if (this.getPagination() != null)
				this.getPagination().firstPage();// ?
		}
		if ("/kq/register/history/dailyorgbrowsedata".equals(arg0.getPath())
				&& arg1.getParameter("b_search") != null) {
			if (this.getPagination() != null)
				this.getPagination().firstPage();// ?
		}
		if ("/kq/register/history/sumbrowse".equals(arg0.getPath())
				&& arg1.getParameter("b_search") != null) {
			if (this.getPagination() != null)
				this.getPagination().firstPage();// ?
			this.setCode("");
			this.setSessiondate("");
			this.setCoursedate("");
			this.setYear("");
			this.setDuration("");
			this.setSessionlist(null);
			this.setKind("");
			this.getFormHM().clear();
		}
		if ("/kq/register/history/sumorgbrowse".equals(arg0.getPath())
				&& arg1.getParameter("b_search") != null) {
			if (this.getPagination() != null)
				this.getPagination().firstPage();// ?
			this.setCode("");
			this.setSessiondate("");
			this.setCoursedate("");
			this.setYear("");
			this.setDuration("");
			this.setSessionlist(null);
			this.setKind("");
			this.getFormHM().clear();
		}
		if ("/kq/register/history/sumbrowsedata".equals(arg0.getPath())
				&& arg1.getParameter("b_search") != null) {
			if (this.getPagination() != null)
				this.getPagination().firstPage();// ?
		}
		if ("/kq/register/history/sumorgbrowsedata".equals(arg0.getPath())
				&& arg1.getParameter("b_search") != null) {
			if (this.getPagination() != null)
				this.getPagination().firstPage();// ?
		}
		if ("/kq/register/history/statfx/statfxdata".equals(arg0.getPath())
				&& arg1.getParameter("b_search") != null) {
			if (this.getPagination() != null)
				this.getPagination().firstPage();// 点击树出现的右边页面
		}
		if ("/kq/register/history/statfx/statfxname".equals(arg0.getPath())
				&& arg1.getParameter("b_seename") != null) {
			if (this.getPagination() != null)
				this.getPagination().firstPage();// 点击人数
		}
		if ("/kq/register/history/statfx/statfxuserinfo".equals(arg0.getPath())
				&& arg1.getParameter("b_userinfo") != null) {
			if (this.getPagination() != null)
				this.getPagination().firstPage();// 点击人数详细信息
		}
		return super.validate(arg0, arg1);
	}

	public String getSelectys() {
		return selectys;
	}

	public void setSelectys(String selectys) {
		this.selectys = selectys;
	}

	public HashMap getKqItem_hash() {
		return kqItem_hash;
	}

	public void setKqItem_hash(HashMap kqItem_hash) {
		this.kqItem_hash = kqItem_hash;
	}

	public String getWorkstat() {
		return workstat;
	}

	public void setWorkstat(String workstat) {
		this.workstat = workstat;
	}

	public String getWorkjs() {
		return workjs;
	}

	public void setWorkjs(String workjs) {
		this.workjs = workjs;
	}

	public String getJsdatetime() {
		return jsdatetime;
	}

	public void setJsdatetime(String jsdatetime) {
		this.jsdatetime = jsdatetime;
	}

	public ArrayList getKqq03list() {
		return kqq03list;
	}

	public void setKqq03list(ArrayList kqq03list) {
		this.kqq03list = kqq03list;
	}

	public String getSqlstrs() {
		return sqlstrs;
	}

	public void setSqlstrs(String sqlstrs) {
		this.sqlstrs = sqlstrs;
	}

	public String getStrwheres() {
		return strwheres;
	}

	public void setStrwheres(String strwheres) {
		this.strwheres = strwheres;
	}

	public String getOrderbys() {
		return orderbys;
	}

	public void setOrderbys(String orderbys) {
		this.orderbys = orderbys;
	}

	public String getColumnss() {
		return columnss;
	}

	public void setColumnss(String columnss) {
		this.columnss = columnss;
	}

	public String getSanshu() {
		return sanshu;
	}

	public void setSanshu(String sanshu) {
		this.sanshu = sanshu;
	}

	public ArrayList getKqnamelsit() {
		return kqnamelsit;
	}

	public void setKqnamelsit(ArrayList kqnamelsit) {
		this.kqnamelsit = kqnamelsit;
	}

	public String getUplevel() {
		return uplevel;
	}

	public void setUplevel(String uplevel) {
		this.uplevel = uplevel;
	}

	public String getBacky() {
		return backy;
	}

	public void setBacky(String backy) {
		this.backy = backy;
	}

	public String getCodetj() {
		return codetj;
	}

	public void setCodetj(String codetj) {
		this.codetj = codetj;
	}

	public String getStart_datetj() {
		return start_datetj;
	}

	public void setStart_datetj(String start_datetj) {
		this.start_datetj = start_datetj;
	}

	public String getEnd_datetj() {
		return end_datetj;
	}

	public void setEnd_datetj(String end_datetj) {
		this.end_datetj = end_datetj;
	}

	public String getRegistertime() {
		return registertime;
	}

	public void setRegistertime(String registertime) {
		this.registertime = registertime;
	}

	public String getTableValue() {
		return tableValue;
	}

	public void setTableValue(String tableValue) {
		this.tableValue = tableValue;
	}

	public ArrayList getSearchfieldlist() {
		return searchfieldlist;
	}

	public void setSearchfieldlist(ArrayList searchfieldlist) {
		this.searchfieldlist = searchfieldlist;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public String getB01101() {
		return b01101;
	}

	public void setB01101(String b01101) {
		this.b01101 = b01101;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public ArrayList getSlist() {
		return slist;
	}

	public void setSlist(ArrayList slist) {
		this.slist = slist;
	}

	public String getDbpre() {
		return dbpre;
	}

	public void setDbpre(String dbpre) {
		this.dbpre = dbpre;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public ArrayList getYearlist() {
		return yearlist;
	}

	public void setYearlist(ArrayList yearlist) {
		this.yearlist = yearlist;
	}

	public ArrayList getDurationlist() {
		return durationlist;
	}

	public void setDurationlist(ArrayList durationlist) {
		this.durationlist = durationlist;
	}

	public String getSelect_type() {
		return select_type;
	}

	public void setSelect_type(String select_type) {
		this.select_type = select_type;
	}

}
