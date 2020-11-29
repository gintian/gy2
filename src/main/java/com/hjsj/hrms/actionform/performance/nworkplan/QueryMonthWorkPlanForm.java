package com.hjsj.hrms.actionform.performance.nworkplan;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

public class QueryMonthWorkPlanForm extends FrameForm{
	// 年，季，月，周 标识 4,3,2,1
	private String state = "";
	private String init="";
	//0 从人员tab进入   1 从部门tab进入
	String personPage = "";
	//当前人是否是处长  0 职员 1 处长
	String isChuZhang="";
	String backurl = "";
	String queryContent = "";
	String belong_type = "";
	ArrayList queryDataList = new ArrayList();
	private PaginationForm setlistform = new PaginationForm();
	@Override
    public void inPutTransHM()
    {
		this.getFormHM().put("belong_type", this.getBelong_type());
		this.getFormHM().put("state", this.getState());
		this.getFormHM().put("personPage", this.getPersonPage());
		this.getFormHM().put("isChuZhang", this.getIsChuZhang());
		this.getFormHM().put("backurl", this.getBackurl());
		this.getFormHM().put("queryContent", this.getQueryContent());
		this.getFormHM().put("pagerows", this.getPagerows()==0?"20":(this.getPagerows()+""));
    }
	@Override
    public void outPutFormHM()
    {
		this.setPersonPage((String)this.getFormHM().get("personPage"));
    	this.setIsChuZhang((String)this.getFormHM().get("isChuZhang"));
    	this.setState((String)this.getFormHM().get("state"));
    	this.setInit((String)this.getFormHM().get("init"));
    	this.setQueryContent((String)this.getFormHM().get("queryContent"));
    	this.setBelong_type((String)this.getFormHM().get("belong_type"));
    	this.setQueryDataList((ArrayList)this.getFormHM().get("queryDataList"));
    	this.setPagerows(Integer.parseInt((String)this.getFormHM().get("pagerows")));
    	this.getSetlistform().setList((ArrayList) this.getFormHM().get("queryDataList"));
    }
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getInit() {
		return init;
	}
	public void setInit(String init) {
		this.init = init;
	}
	public String getPersonPage() {
		return personPage;
	}
	public void setPersonPage(String personPage) {
		this.personPage = personPage;
	}
	public String getIsChuZhang() {
		return isChuZhang;
	}
	public void setIsChuZhang(String isChuZhang) {
		this.isChuZhang = isChuZhang;
	}
	public String getBackurl() {
		return backurl;
	}
	public void setBackurl(String backurl) {
		this.backurl = backurl;
	}
	public String getQueryContent() {
		return queryContent;
	}
	public void setQueryContent(String queryContent) {
		this.queryContent = queryContent;
	}
	public ArrayList getQueryDataList() {
		return queryDataList;
	}
	public void setQueryDataList(ArrayList queryDataList) {
		this.queryDataList = queryDataList;
	}
	public PaginationForm getSetlistform() {
		return setlistform;
	}
	public void setSetlistform(PaginationForm setlistform) {
		this.setlistform = setlistform;
	}
	public String getBelong_type() {
		return belong_type;
	}
	public void setBelong_type(String belong_type) {
		this.belong_type = belong_type;
	}
	
}
