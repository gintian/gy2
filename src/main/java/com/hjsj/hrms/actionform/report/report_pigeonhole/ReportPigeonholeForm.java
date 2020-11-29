package com.hjsj.hrms.actionform.report.report_pigeonhole;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class ReportPigeonholeForm extends FrameForm {
	private String operate="1";   // 1:表类  2：单表
	private String selectUnitType="1";  //1:全部  2：部分
	private ArrayList reportSortList=new ArrayList();
	private String    sortid="";
	private ArrayList infoList=new ArrayList();
	private String   selectedIDs="";
	private String   unitIDs="";
	
	
	@Override
    public void outPutFormHM() {
		this.setReportSortList((ArrayList)this.getFormHM().get("reportSortList"));
		this.setInfoList((ArrayList)this.getFormHM().get("infoList"));
		this.setOperate((String)this.getFormHM().get("operate"));
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("selectUnitType",this.getSelectUnitType());
		this.getFormHM().put("sortid",this.getSortid());
		this.getFormHM().put("operate",this.getOperate());		
		this.getFormHM().put("unitIDs",this.getUnitIDs());

	}

	public ArrayList getInfoList() {
		return infoList;
	}

	public void setInfoList(ArrayList infoList) {
		this.infoList = infoList;
	}

	public String getOperate() {
		return operate;
	}

	public void setOperate(String operate) {
		this.operate = operate;
	}

	public ArrayList getReportSortList() {
		return reportSortList;
	}

	public void setReportSortList(ArrayList reportSortList) {
		this.reportSortList = reportSortList;
	}




	public String getUnitIDs() {
		return unitIDs;
	}

	public void setUnitIDs(String unitIDs) {
		this.unitIDs = unitIDs;
	}

	public String getSortid() {
		return sortid;
	}

	public void setSortid(String sortid) {
		this.sortid = sortid;
	}

	public String getSelectUnitType() {
		return selectUnitType;
	}

	public void setSelectUnitType(String selectUnitType) {
		this.selectUnitType = selectUnitType;
	}

	public String getSelectedIDs() {
		return selectedIDs;
	}

	public void setSelectedIDs(String selectedIDs) {
		this.selectedIDs = selectedIDs;
	}

}
