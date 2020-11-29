package com.hjsj.hrms.actionform.general.salarychange;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class SetCondForm extends FrameForm {
	private String tableid;
	private String conditions;
	private String itemid;
	private ArrayList itemlist = new ArrayList();
	private String[] codesetid_arr;

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
		this.setTableid((String)this.getFormHM().get("tableid"));
		this.setConditions((String)this.getFormHM().get("conditions"));
		this.setItemid((String)this.getFormHM().get("itemid"));
		this.setCodesetid_arr((String[])this.getFormHM().get("codesetid_arr"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub

	}

	public String getConditions() {
		return conditions;
	}

	public void setConditions(String conditions) {
		this.conditions = conditions;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public ArrayList getItemlist() {
		return itemlist;
	}

	public void setItemlist(ArrayList itemlist) {
		this.itemlist = itemlist;
	}

	public String[] getCodesetid_arr() {
		return codesetid_arr;
	}

	public void setCodesetid_arr(String[] codesetid_arr) {
		this.codesetid_arr = codesetid_arr;
	}

	public String getTableid() {
		return tableid;
	}

	public void setTableid(String tableid) {
		this.tableid = tableid;
	}

}
