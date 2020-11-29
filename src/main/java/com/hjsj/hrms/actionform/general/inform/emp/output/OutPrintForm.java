package com.hjsj.hrms.actionform.general.inform.emp.output;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class OutPrintForm extends FrameForm {
	private String styleid;
	private ArrayList stylelist = new ArrayList();
	private String tabid;
	private ArrayList tablist = new ArrayList();
	private String dbname;
	private String inforkind;
	private String result;
	private String a_code;
	private String hmusterid;
	private ArrayList hmusterlist = new ArrayList();
	
	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setStyleid((String)this.getFormHM().get("styleid"));
		this.setStylelist((ArrayList)this.getFormHM().get("stylelist"));
		this.setTabid((String)this.getFormHM().get("tabid"));
		this.setTablist((ArrayList)this.getFormHM().get("tablist"));
		this.setDbname((String)this.getFormHM().get("dbname"));
		this.setInforkind((String)this.getFormHM().get("inforkind"));
		this.setResult((String)this.getFormHM().get("result"));
		this.setA_code((String)this.getFormHM().get("a_code"));
		this.setHmusterlist((ArrayList)this.getFormHM().get("hmusterlist"));
		this.setHmusterid((String)this.getFormHM().get("hmusterid"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub

	}

	public String getStyleid() {
		return styleid;
	}

	public void setStyleid(String styleid) {
		this.styleid = styleid;
	}

	public ArrayList getStylelist() {
		return stylelist;
	}

	public void setStylelist(ArrayList stylelist) {
		this.stylelist = stylelist;
	}

	public String getTabid() {
		return tabid;
	}

	public void setTabid(String tabid) {
		this.tabid = tabid;
	}

	public ArrayList getTablist() {
		return tablist;
	}

	public void setTablist(ArrayList tablist) {
		this.tablist = tablist;
	}

	public String getInforkind() {
		return inforkind;
	}

	public void setInforkind(String a_inforkind) {
		this.inforkind = a_inforkind;
	}
	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getA_code() {
		return a_code;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
	}

	public ArrayList getHmusterlist() {
		return hmusterlist;
	}

	public void setHmusterlist(ArrayList hmusterlist) {
		this.hmusterlist = hmusterlist;
	}

	public String getHmusterid() {
		return hmusterid;
	}

	public void setHmusterid(String hmusterid) {
		this.hmusterid = hmusterid;
	}
}
