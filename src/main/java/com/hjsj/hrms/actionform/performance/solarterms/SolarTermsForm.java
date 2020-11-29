package com.hjsj.hrms.actionform.performance.solarterms;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class SolarTermsForm extends FrameForm{

	private String year = "";//年
	private String depart = "";//部门编码
	private String departdesc = "";//部门描述
	private ArrayList yearlist = new ArrayList();//年列表
	private ArrayList departoptionslist = new ArrayList();//部门列表
	private String indexHtml = "";//首页html
	private String taskHtml = "";//显示具体的任务
	private String showType = "";//按时间维度还是任务维度。0：任务维度。1：时间维度
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("year", this.getYear());
		this.getFormHM().put("depart", this.getDepart());
		this.getFormHM().put("departdesc", this.getDepartdesc());
		this.getFormHM().put("yearlist", this.getYearlist());
		this.getFormHM().put("departoptionslist", this.getDepartoptionslist());
		this.getFormHM().put("indexHtml", this.getIndexHtml());
		this.getFormHM().put("taskHtml", this.getTaskHtml());
		this.getFormHM().put("showType", this.getShowType());
	}

	@Override
    public void outPutFormHM() {
		this.setYear((String)this.getFormHM().get("year"));
		this.setDepart((String)this.getFormHM().get("depart"));
		this.setDepartdesc((String)this.getFormHM().get("departdesc"));
		this.setYearlist((ArrayList)this.getFormHM().get("yearlist"));
		this.setDepartoptionslist((ArrayList)this.getFormHM().get("departoptionslist"));
		this.setIndexHtml((String)this.getFormHM().get("indexHtml"));
		this.setTaskHtml((String)this.getFormHM().get("taskHtml"));
		this.setShowType((String)this.getFormHM().get("showType"));
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getDepart() {
		return depart;
	}

	public void setDepart(String depart) {
		this.depart = depart;
	}

	public ArrayList getYearlist() {
		return yearlist;
	}

	public void setYearlist(ArrayList yearlist) {
		this.yearlist = yearlist;
	}

	public ArrayList getDepartoptionslist() {
		return departoptionslist;
	}

	public void setDepartoptionslist(ArrayList departoptionslist) {
		this.departoptionslist = departoptionslist;
	}

	public String getIndexHtml() {
		return indexHtml;
	}

	public void setIndexHtml(String indexHtml) {
		this.indexHtml = indexHtml;
	}

	public String getTaskHtml() {
		return taskHtml;
	}

	public void setTaskHtml(String taskHtml) {
		this.taskHtml = taskHtml;
	}

	public String getShowType() {
		return showType;
	}

	public void setShowType(String showType) {
		this.showType = showType;
	}

	public String getDepartdesc() {
		return departdesc;
	}

	public void setDepartdesc(String departdesc) {
		this.departdesc = departdesc;
	}

}
