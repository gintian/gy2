package com.hjsj.hrms.actionform.gz.gz_amount.tax;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class SortingForm extends FrameForm {
    
    private ArrayList sortlist = new ArrayList(); //指标list
    private String[] sort_fields; 
    private String salaryid;

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setSort_fields((String[])this.getFormHM().get("sort_fields"));
		this.setSortlist((ArrayList)this.getFormHM().get("sortlist"));
		this.setSalaryid((String)this.getFormHM().get("salaryid"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("sort_fields",this.getSort_fields());
	}

	public ArrayList getSortlist() {
		return sortlist;
	}

	public void setSortlist(ArrayList sortlist) {
		this.sortlist = sortlist;
	}

	public String[] getSort_fields() {
		return sort_fields;
	}

	public void setSort_fields(String[] sort_fields) {
		this.sort_fields = sort_fields;
	}

	public String getSalaryid() {
		return salaryid;
	}

	public void setSalaryid(String salaryid) {
		this.salaryid = salaryid;
	}
}
