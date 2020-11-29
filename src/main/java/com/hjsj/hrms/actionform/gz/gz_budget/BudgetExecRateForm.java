package com.hjsj.hrms.actionform.gz.gz_budget;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.upload.FormFile;

import java.util.ArrayList;

public class BudgetExecRateForm extends FrameForm {
	String budget_id="";   //预算索引号
	String tab_id="";      //预算计划表
	String b0110="";       //预算单位
	String b0110_desc="";
	String tab_name="";//表名 sc03 sc02;
	String rootUnitCode="";//顶级组织机构
	String rootUnitDesc="";//顶级组织机构描述
	
	String budgetYear="";
	ArrayList budgetYearList=new ArrayList();
	
	String budgetMonth="";
	ArrayList budgetMonthList=new ArrayList();
	
	ArrayList fieldList=new ArrayList();
	String sql="";
	String treeJs="";
	private FormFile templateFile;

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("tab_id",this.getTab_id());
		this.getFormHM().put("b0110",this.getB0110());
		this.getFormHM().put("templateFile", this.getTemplateFile());
		this.getFormHM().put("budgetYear",this.getBudgetYear());
		this.getFormHM().put("budgetMonth",this.getBudgetMonth());

	}

	@Override
    public void outPutFormHM() {
		this.setBudgetYearList((ArrayList) this.getFormHM().get("budgetYearList"));
		this.setBudgetYear((String)this.getFormHM().get("budgetYear"));
		this.setBudgetMonthList((ArrayList) this.getFormHM().get("budgetMonthList"));
		this.setBudgetMonth((String)this.getFormHM().get("budgetMonth"));
		
		this.setBudget_id((String)this.getFormHM().get("budget_id"));
		this.setTab_id((String)this.getFormHM().get("tab_id"));
		this.setB0110((String)this.getFormHM().get("b0110"));
		this.setB0110_desc((String)this.getFormHM().get("b0110_desc"));
		this.setFieldList((ArrayList)this.getFormHM().get("fieldList"));
		this.setSql((String)this.getFormHM().get("sql"));
		this.setTab_name((String)this.getFormHM().get("tab_name"));
		this.setRootUnitCode((String) this.getFormHM().get("rootUnitCode"));
		this.setRootUnitDesc((String) this.getFormHM().get("rootUnitDesc"));
		
		this.setTreeJs((String)this.getFormHM().get("treeJs"));
	}

	public String getBudgetYear() {
		return budgetYear;
	}

	public void setBudgetYear(String budgetYear) {
		this.budgetYear = budgetYear;
	}

	public ArrayList getBudgetYearList() {
		return budgetYearList;
	}

	public void setBudgetYearList(ArrayList budgetYearList) {
		this.budgetYearList = budgetYearList;
	}
	public String getBudget_id() {
		return budget_id;
	}

	public void setBudget_id(String budget_id) {
		this.budget_id = budget_id;
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

	public String getB0110_desc() {
		return b0110_desc;
	}

	public void setB0110_desc(String b0110_desc) {
		this.b0110_desc = b0110_desc;
	}

	public String getTab_name() {
		return tab_name;
	}

	public void setTab_name(String tab_name) {
		this.tab_name = tab_name;
	}

	public String getRootUnitCode() {
		return rootUnitCode;
	}

	public void setRootUnitCode(String rootUnitCode) {
		this.rootUnitCode = rootUnitCode;
	}

	public String getRootUnitDesc() {
		return rootUnitDesc;
	}

	public void setRootUnitDesc(String rootUnitDesc) {
		this.rootUnitDesc = rootUnitDesc;
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

	public String getBudgetMonth() {
		return budgetMonth;
	}

	public void setBudgetMonth(String budgetMonth) {
		this.budgetMonth = budgetMonth;
	}

	public ArrayList getBudgetMonthList() {
		return budgetMonthList;
	}

	public void setBudgetMonthList(ArrayList budgetMonthList) {
		this.budgetMonthList = budgetMonthList;
	}

	public FormFile getTemplateFile() {
		return templateFile;
	}

	public void setTemplateFile(FormFile templateFile) {
		this.templateFile = templateFile;
	}

}
