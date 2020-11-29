package com.hjsj.hrms.actionform.gz.gz_budget.budget_allocation;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class BudgetAllocationParamsForm extends FrameForm {
	ArrayList fieldList=new ArrayList();
	String sql="";
	String tab_name="";
	String b0110="";
	String zhuangtai="";

	public String getB0110() {
		return b0110;
	}

	public void setB0110(String b0110) {
		this.b0110 = b0110;
	}

	public String getTab_name() {
		return tab_name;
	}

	public void setTab_name(String tab_name) {
		this.tab_name = tab_name;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("fieldList", (ArrayList)this.getFieldList());
		this.getFormHM().put("zhuangtai", (String)this.getZhuangtai());
	}

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setFieldList((ArrayList)this.getFormHM().get("fieldList"));
		this.setSql((String)this.getFormHM().get("sql"));
		this.setTab_name((String)this.getFormHM().get("tab_name"));
		this.setZhuangtai((String)this.getFormHM().get("zhuangtai"));
	}

	public ArrayList getFieldList() {
		return fieldList;
	}

	public void setFieldList(ArrayList fieldList) {
		this.fieldList = fieldList;
	}

	public String getZhuangtai() {
		return zhuangtai;
	}

	public void setZhuangtai(String zhuangtai) {
		this.zhuangtai = zhuangtai;
	}

}
