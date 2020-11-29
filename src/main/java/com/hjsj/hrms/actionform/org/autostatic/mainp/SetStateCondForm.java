package com.hjsj.hrms.actionform.org.autostatic.mainp;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class SetStateCondForm extends FrameForm {
	private ArrayList setlist = new ArrayList();
	private String tablestr="";
	private String item_field[]; //指标列表
	private String fieldid="";
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setSetlist((ArrayList)this.getFormHM().get("setlist"));
		this.setTablestr((String)this.getFormHM().get("tablestr"));
		this.setItem_field((String[])this.getFormHM().get("item_field"));
		this.setFieldid((String)this.getFormHM().get("fieldid"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub

	}

	public ArrayList getSetlist() {
		return setlist;
	}

	public void setSetlist(ArrayList setlist) {
		this.setlist = setlist;
	}

	public String getTablestr() {
		return tablestr;
	}

	public void setTablestr(String tablestr) {
		this.tablestr = tablestr;
	}

	public String[] getItem_field() {
		return item_field;
	}

	public void setItem_field(String[] item_field) {
		this.item_field = item_field;
	}

	public String getFieldid() {
		return fieldid;
	}

	public void setFieldid(String fieldid) {
		this.fieldid = fieldid;
	}

}
