package com.hjsj.hrms.actionform.welcome.per;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;

public class PerIndexForm extends FrameForm {
	 private ArrayList dblist=new ArrayList();
	 private HttpSession session;
	 private ArrayList marklist=new ArrayList();
	 public ArrayList getMarklist() {
		return marklist;
	}
	public void setMarklist(ArrayList marklist) {
		this.marklist = marklist;
	}
	@Override
    public void outPutFormHM()
	 {
		 this.setDblist((ArrayList)this.getFormHM().get("dblist"));
		 this.setMarklist((ArrayList)this.getFormHM().get("marklist"));
	 }
	 public ArrayList getDblist() {
		return dblist;
	}
	public void setDblist(ArrayList dblist) {
		this.dblist = dblist;		
	}
	@Override
    public void inPutTransHM()
	{
		this.getFormHM().put("session",session);
	}
	 @Override
     public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	 {
		 session=arg1.getSession();
		 return super.validate(arg0, arg1);
	 }
	}

