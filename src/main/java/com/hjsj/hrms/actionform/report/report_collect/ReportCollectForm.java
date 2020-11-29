package com.hjsj.hrms.actionform.report.report_collect;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class ReportCollectForm extends FrameForm {
	private String unitcode="";								//填报单位编码
	private String isLeafUnit="0";                          //是否是基层单位  1：是  0：不是
	private ArrayList underUnitList=new ArrayList();		//直属单位集合
	private ArrayList tableList=new ArrayList();            //报表集合
	private ArrayList commonsParamList=new ArrayList();     //全局\表类 代码类型的参数集合
	private ArrayList rightFieldsList=new ArrayList();
	private ArrayList codeItemList=new ArrayList();		
	private ArrayList sortIdList=new ArrayList();
	private String  sortid="";

	/**选中的字段名数组*/
    private String right_fields[];  
	private ArrayList selectedParamList=new ArrayList();

	
	 //表内校验结果
    private String reportInnerCheckResult;
    //表间校验结果
    private String reportSpaceCheckResult;
    
    private ArrayList c_unitList=new ArrayList();   //填报单位
    private ArrayList c_sortIdList=new ArrayList();  //表类列表
    private ArrayList c_tableList=new ArrayList();  //报表集合
    private ArrayList formulaList=new ArrayList();
    
	@Override
    public void outPutFormHM() {
		this.setFormulaList((ArrayList)this.getFormHM().get("formulaList"));
		this.setC_sortIdList((ArrayList)this.getFormHM().get("c_sortIdList"));
		this.setC_tableList((ArrayList)this.getFormHM().get("c_tableList"));
		this.setC_unitList((ArrayList)this.getFormHM().get("c_unitList"));
		
		this.setIsLeafUnit((String)this.getFormHM().get("isLeafUnit"));
		this.setUnitcode((String)this.getFormHM().get("unitcode"));
		this.setUnderUnitList((ArrayList)this.getFormHM().get("underUnitList"));
		this.setTableList((ArrayList)this.getFormHM().get("tableList"));
		this.setCommonsParamList((ArrayList)this.getFormHM().get("commonsParam"));
		this.setCodeItemList((ArrayList)this.getFormHM().get("codeItemList"));
		this.setSelectedParamList((ArrayList)this.getFormHM().get("selectedParamList"));
		this.setRightFieldsList((ArrayList)this.getFormHM().get("rightFieldsList"));
		this.setSortIdList((ArrayList)this.getFormHM().get("sortIdList"));
		this.setSortid((String)this.getFormHM().get("sortid"));
		
		this.setReportInnerCheckResult((String)this.getFormHM().get("reportInnerCheckResult"));
		this.setReportSpaceCheckResult((String)this.getFormHM().get("reportSpaceCheckResult"));
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("right_fields",this.getRight_fields());
	}

	public String getUnitcode() {
		return unitcode;
	}

	public void setUnitcode(String unitcode) {
		this.unitcode = unitcode;
	}

	public ArrayList getUnderUnitList() {
		return underUnitList;
	}

	public void setUnderUnitList(ArrayList underUnitList) {
		this.underUnitList = underUnitList;
	}

	public ArrayList getTableList() {
		return tableList;
	}

	public void setTableList(ArrayList tableList) {
		this.tableList = tableList;
	}

	public ArrayList getCommonsParamList() {
		return commonsParamList;
	}

	public void setCommonsParamList(ArrayList commonsParamList) {
		this.commonsParamList = commonsParamList;
	}

	public ArrayList getCodeItemList() {
		return codeItemList;
	}

	public void setCodeItemList(ArrayList codeItemList) {
		this.codeItemList = codeItemList;
	}

	public String[] getRight_fields() {
		return right_fields;
	}

	public void setRight_fields(String[] right_fields) {
		this.right_fields = right_fields;
	}

	public ArrayList getSelectedParamList() {
		return selectedParamList;
	}

	public void setSelectedParamList(ArrayList selectedParamList) {
		this.selectedParamList = selectedParamList;
	}

	public String getIsLeafUnit() {
		return isLeafUnit;
	}

	public void setIsLeafUnit(String isLeafUnit) {
		this.isLeafUnit = isLeafUnit;
	}

	public ArrayList getRightFieldsList() {
		return rightFieldsList;
	}

	public void setRightFieldsList(ArrayList rightFieldsList) {
		this.rightFieldsList = rightFieldsList;
	}

	public ArrayList getSortIdList() {
		return sortIdList;
	}

	public void setSortIdList(ArrayList sortIdList) {
		this.sortIdList = sortIdList;
	}

	public String getSortid() {
		return sortid;
	}

	public void setSortid(String sortid) {
		this.sortid = sortid;
	}

	public String getReportInnerCheckResult() {
		return reportInnerCheckResult;
	}

	public void setReportInnerCheckResult(String reportInnerCheckResult) {
		this.reportInnerCheckResult = reportInnerCheckResult;
	}

	public String getReportSpaceCheckResult() {
		return reportSpaceCheckResult;
	}

	public void setReportSpaceCheckResult(String reportSpaceCheckResult) {
		this.reportSpaceCheckResult = reportSpaceCheckResult;
	}

	public ArrayList getC_unitList() {
		return c_unitList;
	}

	public void setC_unitList(ArrayList list) {
		c_unitList = list;
	}

	 

	public ArrayList getC_tableList() {
		return c_tableList;
	}

	public void setC_tableList(ArrayList list) {
		c_tableList = list;
	}

	public ArrayList getC_sortIdList() {
		return c_sortIdList;
	}

	public void setC_sortIdList(ArrayList idList) {
		c_sortIdList = idList;
	}

	public ArrayList getFormulaList() {
		return formulaList;
	}

	public void setFormulaList(ArrayList formulaList) {
		this.formulaList = formulaList;
	}



}
