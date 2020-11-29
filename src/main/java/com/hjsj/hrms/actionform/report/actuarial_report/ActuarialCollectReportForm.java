package com.hjsj.hrms.actionform.report.actuarial_report;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class ActuarialCollectReportForm extends FrameForm {
	private ArrayList cycleList=new ArrayList();  //填报周期列表
	private String    cycle_id="";                //填报周期id
	private String cycleStatus="";  //填报周期状态
	private ArrayList actuarialReportStatusList=new ArrayList();
	private String    selfUnitcode="";
	private String    unitCode="";      //填报单位
	private String    unitName="";
	
	private String    isAllSub="0";           //判断是否都处于上报状态
	private String    isAllEdit="0";           //判断是否都处于编辑状态
	private String    isAllSub_child="0";      //判断是否全部提交
	private String    isUnderUnit="0";   //是否是直属单位
	private String    isCollectUnit="1"; //是否是汇总单位  1: 是  0: 不是
	private String    isTopUnit="0";     //是否是顶层单位  1: 是  0: 不是
	private String    tableHtml="";
	private String htmlbody="";
	private String htmlbody2="";
	private String paracopy="";
	private String paracopy2="";
	private String isfillpara="";
	private String isfillpara2 ="";
	private String rootUnit="";
	private String kmethod="0";
	private String u02_3flag="0";
	public String getRootUnit() {
		return rootUnit;
	}


	public void setRootUnit(String rootUnit) {
		this.rootUnit = rootUnit;
	}


	public String getIsfillpara() {
		return isfillpara;
	}


	public void setIsfillpara(String isfillpara) {
		this.isfillpara = isfillpara;
	}


	public String getIsfillpara2() {
		return isfillpara2;
	}


	public void setIsfillpara2(String isfillpara2) {
		this.isfillpara2 = isfillpara2;
	}


	@Override
    public void inPutTransHM() {
		this.getFormHM().put("cycle_id",this.getCycle_id());

	}


	@Override
    public void outPutFormHM() {
		this.setIsCollectUnit((String)this.getFormHM().get("isCollectUnit"));
		this.setIsTopUnit((String)this.getFormHM().get("isTopUnit"));
		this.setSelfUnitcode((String)this.getFormHM().get("selfUnitcode"));
		this.setIsUnderUnit((String)this.getFormHM().get("isUnderUnit"));
		this.setIsAllSub_child((String)this.getFormHM().get("isAllSub_child"));
		this.setIsAllEdit((String)this.getFormHM().get("isAllEdit"));
		this.setIsAllSub((String)this.getFormHM().get("isAllSub"));
		
		this.setCycleStatus((String)this.getFormHM().get("cycleStatus"));
		this.setCycle_id((String)this.getFormHM().get("cycle_id"));
		this.setCycleList((ArrayList)this.getFormHM().get("cycleList"));
		this.setActuarialReportStatusList((ArrayList)this.getFormHM().get("actuarialReportStatusList"));
		this.setUnitCode((String)this.getFormHM().get("unitCode"));
		this.setUnitName((String)this.getFormHM().get("unitName"));
		this.setTableHtml((String)this.getFormHM().get("tableHtml"));
		this.setHtmlbody((String)this.getFormHM().get("htmlbody"));
		this.setHtmlbody2((String)this.getFormHM().get("htmlbody2"));
		this.setParacopy((String)this.getFormHM().get("paracopy"));
		this.setParacopy2((String)this.getFormHM().get("paracopy2"));
		this.setIsfillpara((String)this.getFormHM().get("isfillpara"));
		this.setIsfillpara2((String)this.getFormHM().get("isfillpara2"));
		this.setRootUnit((String)this.getFormHM().get("rootUnit"));
		this.setKmethod((String)this.getFormHM().get("kmethod"));
		this.setU02_3flag((String)this.getFormHM().get("u02_3flag"));
	}

	public ArrayList getActuarialReportStatusList() {
		return actuarialReportStatusList;
	}

	public void setActuarialReportStatusList(ArrayList actuarialReportStatusList) {
		this.actuarialReportStatusList = actuarialReportStatusList;
	}

	public String getCycle_id() {
		return cycle_id;
	}

	public String getU02_3flag() {
		return u02_3flag;
	}


	public void setU02_3flag(String u02_3flag) {
		this.u02_3flag = u02_3flag;
	}


	public void setCycle_id(String cycle_id) {
		this.cycle_id = cycle_id;
	}

	public ArrayList getCycleList() {
		return cycleList;
	}

	public void setCycleList(ArrayList cycleList) {
		this.cycleList = cycleList;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}


	public String getIsCollectUnit() {
		return isCollectUnit;
	}


	public void setIsCollectUnit(String isCollectUnit) {
		this.isCollectUnit = isCollectUnit;
	}


	public String getIsTopUnit() {
		return isTopUnit;
	}


	public void setIsTopUnit(String isTopUnit) {
		this.isTopUnit = isTopUnit;
	}


	public String getSelfUnitcode() {
		return selfUnitcode;
	}


	public void setSelfUnitcode(String selfUnitcode) {
		this.selfUnitcode = selfUnitcode;
	}


	public String getIsUnderUnit() {
		return isUnderUnit;
	}


	public void setIsUnderUnit(String isUnderUnit) {
		this.isUnderUnit = isUnderUnit;
	}


	


	public String getIsAllSub_child() {
		return isAllSub_child;
	}


	public void setIsAllSub_child(String isAllSub_child) {
		this.isAllSub_child = isAllSub_child;
	}


	public String getIsAllEdit() {
		return isAllEdit;
	}


	public void setIsAllEdit(String isAllEdit) {
		this.isAllEdit = isAllEdit;
	}


	public String getIsAllSub() {
		return isAllSub;
	}


	public void setIsAllSub(String isAllSub) {
		this.isAllSub = isAllSub;
	}


	public String getCycleStatus() {
		return cycleStatus;
	}


	public void setCycleStatus(String cycleStatus) {
		this.cycleStatus = cycleStatus;
	}


	public String getTableHtml() {
		return tableHtml;
	}


	public void setTableHtml(String tableHtml) {
		this.tableHtml = tableHtml;
	}


	public String getHtmlbody() {
		return htmlbody;
	}


	public void setHtmlbody(String htmlbody) {
		this.htmlbody = htmlbody;
	}


	public String getHtmlbody2() {
		return htmlbody2;
	}


	public void setHtmlbody2(String htmlbody2) {
		this.htmlbody2 = htmlbody2;
	}


	public String getParacopy() {
		return paracopy;
	}


	public void setParacopy(String paracopy) {
		this.paracopy = paracopy;
	}


	public String getParacopy2() {
		return paracopy2;
	}


	public void setParacopy2(String paracopy2) {
		this.paracopy2 = paracopy2;
	}


	public String getKmethod() {
		return kmethod;
	}


	public void setKmethod(String kmethod) {
		this.kmethod = kmethod;
	}


	
}
