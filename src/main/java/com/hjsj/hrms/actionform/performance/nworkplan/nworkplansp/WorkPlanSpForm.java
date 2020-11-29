package com.hjsj.hrms.actionform.performance.nworkplan.nworkplansp;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

public class WorkPlanSpForm extends FrameForm {

	private String a0100="";
	private String dbname="";
	private String sp_relation="";
	private PaginationForm paginationForm = new PaginationForm();
	ArrayList list =new ArrayList();
	private String year="";
	private String month="";
	private String season="";
	private String week="";
	private String sp_type="";
	private String state="";// 0/1/2/3/4 日周月季年
	private String content="";
	private String name="";
	private String flag="";//用于判断登入用户是报批还是批准
	private String reason="";
	ArrayList yearlist =new ArrayList();
	ArrayList monthlist =new ArrayList();
	ArrayList sptypelist =new ArrayList();
	ArrayList seasonlist =new ArrayList();
	ArrayList weeklist =new ArrayList();
    private String belong_type;//=0查个人=1查处室=2查部门

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("a0100", this.getA0100());
		this.getFormHM().put("dbname", this.getDbname());
		this.getFormHM().put("sp_relation", this.getSp_relation());
		this.getFormHM().put("yearlist", this.getYearlist());
		this.getFormHM().put("monthlist", this.getMonthlist());
		this.getFormHM().put("sptypelist", this.getSptypelist());
		this.getFormHM().put("seasonlist", this.getSeasonlist());
		this.getFormHM().put("weeklist", this.getWeeklist());
		this.getFormHM().put("year", this.getYear());
		this.getFormHM().put("month", this.getMonth());
		this.getFormHM().put("season", this.getSeason());
		this.getFormHM().put("week", this.getWeek());
		this.getFormHM().put("sp_type", this.getSp_type());
		this.getFormHM().put("state", this.getState());
		this.getFormHM().put("content", this.getContent());
		this.getFormHM().put("name", this.getName());
		this.getFormHM().put("flag", this.getFlag());
		this.getFormHM().put("reason", this.getReason());
		this.getFormHM().put("belong_type",this.getBelong_type());
	}

	@Override
    public void outPutFormHM() {
		this.setBelong_type((String)this.getFormHM().get("belong_type"));
		this.setA0100((String)this.getFormHM().get("a0100"));
		this.setDbname((String)this.getFormHM().get("dbname"));
		this.setSp_relation((String)this.getFormHM().get("sp_relation"));
		this.setList((ArrayList)this.getFormHM().get("list"));
		this.getPaginationForm().setList((ArrayList) this.getFormHM().get("list"));
		this.setYearlist((ArrayList)this.getFormHM().get("yearlist"));
		this.setMonthlist((ArrayList)this.getFormHM().get("monthlist"));
		this.setSptypelist((ArrayList)this.getFormHM().get("sptypelist"));
		this.setSeasonlist((ArrayList)this.getFormHM().get("seasonlist"));
		this.setWeeklist((ArrayList)this.getFormHM().get("weeklist"));
		this.setYear((String)this.getFormHM().get("year"));
		this.setMonth((String)this.getFormHM().get("month"));
		this.setSeason((String)this.getFormHM().get("season"));
		this.setWeek((String)this.getFormHM().get("week"));
		this.setSp_type((String)this.getFormHM().get("sp_type"));
		this.setState((String)this.getFormHM().get("state"));
		this.setName((String)this.getFormHM().get("name"));
		this.setContent((String)this.getFormHM().get("content"));
		this.setFlag((String)this.getFormHM().get("flag"));
		this.setReason((String)this.getFormHM().get("reason"));
	}

	public String getBelong_type() {
		return belong_type;
	}

	public void setBelong_type(String belong_type) {
		this.belong_type = belong_type;
	}

	public String getA0100() {
		return a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public String getSp_relation() {
		return sp_relation;
	}

	public void setSp_relation(String sp_relation) {
		this.sp_relation = sp_relation;
	}

	public PaginationForm getPaginationForm() {
		return paginationForm;
	}

	public void setPaginationForm(PaginationForm paginationForm) {
		this.paginationForm = paginationForm;
	}

	public ArrayList getList() {
		return list;
	}

	public void setList(ArrayList list) {
		this.list = list;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public ArrayList getYearlist() {
		return yearlist;
	}

	public void setYearlist(ArrayList yearlist) {
		this.yearlist = yearlist;
	}

	public ArrayList getMonthlist() {
		return monthlist;
	}

	public void setMonthlist(ArrayList monthlist) {
		this.monthlist = monthlist;
	}

	public ArrayList getSptypelist() {
		return sptypelist;
	}

	public void setSptypelist(ArrayList sptypelist) {
		this.sptypelist = sptypelist;
	}

	public String getSp_type() {
		return sp_type;
	}

	public void setSp_type(String sp_type) {
		this.sp_type = sp_type;
	}
	

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getContent() {
		return content;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSeason() {
		return season;
	}

	public void setSeason(String season) {
		this.season = season;
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public ArrayList getSeasonlist() {
		return seasonlist;
	}

	public void setSeasonlist(ArrayList seasonlist) {
		this.seasonlist = seasonlist;
	}

	public ArrayList getWeeklist() {
		return weeklist;
	}

	public void setWeeklist(ArrayList weeklist) {
		this.weeklist = weeklist;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}


}
