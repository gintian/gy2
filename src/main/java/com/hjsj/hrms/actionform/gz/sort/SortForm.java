package com.hjsj.hrms.actionform.gz.sort;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class SortForm extends FrameForm {
	private String fieldid;
	private ArrayList fieldlist = new ArrayList();
	private String itemid;
	private ArrayList itemlist = new ArrayList();
	private String flag;
	private String sortitem;
	private String checkflag="";
	private String salaryid;
	private String xuj;
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setFieldid((String)this.getFormHM().get("fieldid"));
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.setItemid((String)this.getFormHM().get("itemid"));
		this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
		this.setFlag((String)this.getFormHM().get("flag"));
		this.setSortitem((String)this.getFormHM().get("sortitem"));
		this.setCheckflag((String)this.getFormHM().get("checkflag"));
		this.setSalaryid((String)this.getFormHM().get("salaryid"));
		this.getFormHM().remove("salaryid");
		this.setXuj((String)this.getFormHM().get("xuj"));
		this.getFormHM().remove("xuj");
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public ArrayList getItemlist() {
		return itemlist;
	}

	public void setItemlist(ArrayList itemlist) {
		this.itemlist = itemlist;
	}

	public String getFieldid() {
		return fieldid;
	}

	public void setFieldid(String fieldid) {
		this.fieldid = fieldid;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public String getSortitem() {
		return sortitem;
	}

	public void setSortitem(String sortitem) {
		this.sortitem = sortitem;
	}

	public String getCheckflag() {
		return checkflag;
	}

	public void setCheckflag(String checkflag) {
		this.checkflag = checkflag;
	}

	public String getSalaryid() {
		return salaryid;
	}

	public void setSalaryid(String salaryid) {
		this.salaryid = salaryid;
	}

	public String getXuj() {
		return xuj;
	}

	public void setXuj(String xuj) {
		this.xuj = xuj;
	}

}
