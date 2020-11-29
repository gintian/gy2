package com.hjsj.hrms.actionform.gz.gz_budget;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class BudgetExaminationForm extends FrameForm {
	String budget_id="";   //预算索引号
	String currentBudgetDesc="";  //当前预算描述
	String tab_id="";      //预算计划表
	String b0110="";       //预算单位
	String b0110_desc="";
	String appeal_status="";  //填报状态；
	String appealStatusDesc=""; //填报状态描述
	String tab_name="";
	String tabName="";
	String flag="";//1审批 2 历史
	String rootUnitcode="";//顶级组织机构
	String rootunitstatus="";//顶级组织机构描述
	
	ArrayList fieldList=new ArrayList();
	String sql="";
	String treeJs="";
	
	
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("tab_id",this.getTab_id());
		this.getFormHM().put("b0110",this.getB0110());
		this.getFormHM().put("flag", this.getFlag());
	}

	@Override
    public void outPutFormHM() {
		this.setBudget_id((String)this.getFormHM().get("budget_id"));
		this.setCurrentBudgetDesc((String)this.getFormHM().get("currentBudgetDesc"));
		this.setTab_id((String)this.getFormHM().get("tab_id"));
		this.setB0110((String)this.getFormHM().get("b0110"));
		this.setB0110_desc((String)this.getFormHM().get("b0110_desc"));
		this.setAppealStatusDesc((String)this.getFormHM().get("appealStatusDesc"));
		this.setAppeal_status((String)this.getFormHM().get("appeal_status"));
		this.setFieldList((ArrayList)this.getFormHM().get("fieldList"));
		this.setSql((String)this.getFormHM().get("sql"));
		this.setTab_name((String)this.getFormHM().get("tab_name"));
		this.setTabName((String)this.getFormHM().get("tabName"));
		this.setFlag((String) this.getFormHM().get("flag"));
		this.setRootUnitcode((String) this.getFormHM().get("rootunitcode"));
		this.setRootunitstatus((String) this.getFormHM().get("rootunitstatus"));
		
		this.setTreeJs((String)this.getFormHM().get("treeJs"));
	}

	public String getBudget_id() {
		return budget_id;
	}

	public void setBudget_id(String budget_id) {
		this.budget_id = budget_id;
	}

	public String getCurrentBudgetDesc() {
		return currentBudgetDesc;
	}

	public void setCurrentBudgetDesc(String currentBudgetDesc) {
		this.currentBudgetDesc = currentBudgetDesc;
	}

	public String getTab_id() {
		return tab_id;
	}

	public void setTab_id(String tab_id) {
		this.tab_id = tab_id;
	}

	public String getB0110() {
		return b0110;
	}

	public void setB0110(String b0110) {
		this.b0110 = b0110;
	}

	public String getAppeal_status() {
		return appeal_status;
	}

	public void setAppeal_status(String appeal_status) {
		this.appeal_status = appeal_status;
	}

	public String getAppealStatusDesc() {
		return appealStatusDesc;
	}

	public void setAppealStatusDesc(String appealStatusDesc) {
		this.appealStatusDesc = appealStatusDesc;
	}

	public ArrayList getFieldList() {
		return fieldList;
	}

	public void setFieldList(ArrayList fieldList) {
		this.fieldList = fieldList;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getTreeJs() {
		return treeJs;
	}

	public void setTreeJs(String treeJs) {
		this.treeJs = treeJs;
	}

	public String getTab_name() {
		return tab_name;
	}

	public void setTab_name(String tab_name) {
		this.tab_name = tab_name;
	}

	public String getB0110_desc() {
		return b0110_desc;
	}

	public void setB0110_desc(String b0110_desc) {
		this.b0110_desc = b0110_desc;
	}

	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getRootUnitcode() {
		return rootUnitcode;
	}

	public void setRootUnitcode(String rootUnitcode) {
		this.rootUnitcode = rootUnitcode;
	}

	public String getRootunitstatus() {
		return rootunitstatus;
	}

	public void setRootunitstatus(String rootunitdesc) {
		this.rootunitstatus = rootunitdesc;
	}


}
