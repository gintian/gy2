package com.hjsj.hrms.actionform.browse;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class SynthesisBrowseForm extends FrameForm {
	private String dbpre;
	private String a_code;
	private String a0100;
	private String inforkind;
	private String tabid;
	private String queryname;
	private String browse_dbpre;
	private ArrayList dblist=new ArrayList();
    private String syn_flag;
	public String getSyn_flag() {
		return syn_flag;
	}

	public void setSyn_flag(String syn_flag) {
		this.syn_flag = syn_flag;
	}

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setA0100((String)this.getFormHM().get("a0100"));
		if(this.getFormHM().get("dbpre")!=null)
		   this.setDbpre((String)this.getFormHM().get("dbpre"));
		this.setInforkind((String)this.getFormHM().get("inforkind"));
		this.setTabid((String)this.getFormHM().get("tabid"));
	    if(this.getFormHM().get("a_code")!=null)
	    	this.setA_code((String)this.getFormHM().get("a_code"));
	    this.setDblist((ArrayList)this.getFormHM().get("dblist"));
	    this.setBrowse_dbpre((String)this.getFormHM().get("browse_dbpre"));
        this.setSyn_flag((String)this.getFormHM().get("syn_flag"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("a_code",this.a_code);
		this.getFormHM().put("a0100",this.a0100);
		this.getFormHM().put("inforkind",this.inforkind);
		//this.getFormHM().put("tabid",this.tabid);
		this.getFormHM().put("dbpre",this.dbpre);

	}

	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		this.getFormHM().put("session",arg1.getSession());
		return super.validate(arg0, arg1);
	}


	public String getDbpre() {
		return dbpre;
	}

	public void setDbpre(String dbpre) {
		this.dbpre = dbpre;
	}

	public String getA_code() {
		return a_code;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
	}

	public String getInforkind() {
		return inforkind;
	}

	public void setInforkind(String inforkind) {
		this.inforkind = inforkind;
	}

	public String getA0100() {
		return a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

	public String getTabid() {
		return tabid;
	}

	public void setTabid(String tabid) {
		this.tabid = tabid;
	}

	public ArrayList getDblist() {
		return dblist;
	}

	public void setDblist(ArrayList dblist) {
		this.dblist = dblist;
	}

	public String getQueryname() {
		return queryname;
	}

	public void setQueryname(String queryname) {
		this.queryname = queryname;
	}

	public String getBrowse_dbpre() {
		return this.browse_dbpre;
	}

	public void setBrowse_dbpre(String browse_dbpre) {
		this.browse_dbpre = browse_dbpre;
	}

}
