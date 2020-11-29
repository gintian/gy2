package com.hjsj.hrms.actionform.general.query;

import com.hrms.struts.action.FrameForm;

public class HandworkSelectForm extends FrameForm {
	private String managerstr=" ";
	private String infor="";
	private String dbpre_arr="";
	private String rootdesc="";
	public HandworkSelectForm() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
    public void outPutFormHM() {
		this.setManagerstr((String)this.getFormHM().get("managerstr"));
		this.setInfor((String)this.getFormHM().get("infor"));
		this.setDbpre_arr((String)this.getFormHM().get("dbpre_arr"));
		this.setRootdesc((String)this.getFormHM().get("rootdesc"));
	}

	@Override
    public void inPutTransHM() {
		

	}

	public String getManagerstr() {
		return managerstr;
	}

	public void setManagerstr(String managerstr) {
		this.managerstr = managerstr;
	}

	public String getDbpre_arr() {
		return dbpre_arr;
	}

	public void setDbpre_arr(String dbpre_arr) {
		this.dbpre_arr = dbpre_arr;
	}

	public String getInfor() {
		return infor;
	}

	public void setInfor(String infor) {
		this.infor = infor;
	}

	public String getRootdesc() {
		return rootdesc;
	}

	public void setRootdesc(String rootdesc) {
		this.rootdesc = rootdesc;
	}

}
