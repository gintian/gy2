package com.hjsj.hrms.actionform.general.inform.emp;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class ShiftLibraryForm extends FrameForm {
	private ArrayList dblist = new ArrayList();
	private String dbname;
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setDblist((ArrayList)this.getFormHM().get("dblist"));
		this.setDbname((String)this.getFormHM().get("dbname"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub

	}

	public ArrayList getDblist() {
		return dblist;
	}

	public void setDblist(ArrayList dblist) {
		this.dblist = dblist;
	}

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

}
