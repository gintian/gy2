package com.hjsj.hrms.actionform.report.retport_status;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;
import java.util.HashMap;

public class ReportStatusForm extends FrameForm {
	private String unitCode="";
	private String unitName="";
	private String selfUnitcode="";
	/** 报表类别 */
	private ArrayList reportSetList=new ArrayList();
	private ArrayList subUnitList=new ArrayList();
	private ArrayList tabDataList=new ArrayList();
	private HashMap   setTabCountMap=new HashMap();
	
	private String    tableHtml="";
	
	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub

	}

	@Override
    public void outPutFormHM() {
		this.setSelfUnitcode((String)this.getFormHM().get("selfUnitcode"));
		this.setTableHtml((String)this.getFormHM().get("tableHtml"));
		this.setSetTabCountMap((HashMap)this.getFormHM().get("setTabCountMap"));
		this.setUnitName((String)this.getFormHM().get("unitName"));
		this.setUnitCode((String)this.getFormHM().get("unitCode"));
		this.setReportSetList((ArrayList)this.getFormHM().get("reportSetList"));
		this.setSubUnitList((ArrayList)this.getFormHM().get("subUnitList"));
		this.setTabDataList((ArrayList)this.getFormHM().get("tabDataList"));
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
	}

	public ArrayList getReportSetList() {
		return reportSetList;
	}

	public void setReportSetList(ArrayList reportSetList) {
		this.reportSetList = reportSetList;
	}

	public ArrayList getSubUnitList() {
		return subUnitList;
	}

	public void setSubUnitList(ArrayList subUnitList) {
		this.subUnitList = subUnitList;
	}

	public ArrayList getTabDataList() {
		return tabDataList;
	}

	public void setTabDataList(ArrayList tabDataList) {
		this.tabDataList = tabDataList;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public HashMap getSetTabCountMap() {
		return setTabCountMap;
	}

	public void setSetTabCountMap(HashMap setTabCountMap) {
		this.setTabCountMap = setTabCountMap;
	}

	public String getTableHtml() {
		return tableHtml;
	}

	public void setTableHtml(String tableHtml) {
		this.tableHtml = tableHtml;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getSelfUnitcode() {
		return selfUnitcode;
	}

	public void setSelfUnitcode(String selfUnitcode) {
		this.selfUnitcode = selfUnitcode;
	}

}
