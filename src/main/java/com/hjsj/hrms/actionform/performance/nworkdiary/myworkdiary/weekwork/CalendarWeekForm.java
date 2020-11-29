package com.hjsj.hrms.actionform.performance.nworkdiary.myworkdiary.weekwork;

import com.hrms.struts.action.FrameForm;

public class CalendarWeekForm extends FrameForm{

	private String tableHtml = "";//生成周报的表头
	private String weekHtml = "";//生成周一至周六
	private String jsonstr = "";//全天事件
	private String periodjsonstr = "";//时间段的事件
	
	//员工日志的参数
	private String a0100 = "";//人员编号
	private String nbase = "";//人员库
	private String staff_url = "";//返回到员工日志的url
	private String frompage = "";//标识是从菜单进入还是从员工日志中进入
	private String isowner = "";//js中showDetail的一个参数
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("tableHtml", this.getTableHtml());
		this.getFormHM().put("weekHtml", this.getWeekHtml());
		this.getFormHM().put(jsonstr, this.getJsonstr());
		this.getFormHM().put(periodjsonstr, this.getPeriodjsonstr());
		this.getFormHM().put("a0100", this.getA0100());
		this.getFormHM().put("nbase", this.getNbase());
		this.getFormHM().put("staff_url", this.getStaff_url());
		this.getFormHM().put("frompage", this.getFrompage());
		this.getFormHM().put("isowner", this.getIsowner());
	}

	@Override
    public void outPutFormHM() {
		this.setTableHtml((String)this.getFormHM().get("tableHtml"));
		this.setWeekHtml((String)this.getFormHM().get("weekHtml"));
		this.setJsonstr((String)this.getFormHM().get("jsonstr"));
		this.setPeriodjsonstr((String)this.getFormHM().get("periodjsonstr"));
		this.setA0100((String)this.getFormHM().get("a0100"));
		this.setNbase((String)this.getFormHM().get("nbase"));
		this.setStaff_url((String)this.getFormHM().get("staff_url"));
		this.setFrompage((String)this.getFormHM().get("frompage"));
		this.setIsowner((String)this.getFormHM().get("isowner"));
	}

	public String getTableHtml() {
		return tableHtml;
	}

	public void setTableHtml(String tableHtml) {
		this.tableHtml = tableHtml;
	}

	public String getA0100() {
		return a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

	public String getStaff_url() {
		return staff_url;
	}

	public void setStaff_url(String staff_url) {
		this.staff_url = staff_url;
	}

	public String getFrompage() {
		return frompage;
	}

	public void setFrompage(String frompage) {
		this.frompage = frompage;
	}

	public String getNbase() {
		return nbase;
	}

	public void setNbase(String nbase) {
		this.nbase = nbase;
	}

	public String getJsonstr() {
		return jsonstr;
	}

	public void setJsonstr(String jsonstr) {
		this.jsonstr = jsonstr;
	}

	public String getPeriodjsonstr() {
		return periodjsonstr;
	}

	public void setPeriodjsonstr(String periodjsonstr) {
		this.periodjsonstr = periodjsonstr;
	}

	public String getIsowner() {
		return isowner;
	}

	public void setIsowner(String isowner) {
		this.isowner = isowner;
	}

	public String getWeekHtml() {
		return weekHtml;
	}

	public void setWeekHtml(String weekHtml) {
		this.weekHtml = weekHtml;
	}

}
