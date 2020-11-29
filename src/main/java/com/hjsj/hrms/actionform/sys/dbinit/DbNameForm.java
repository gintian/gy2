package com.hjsj.hrms.actionform.sys.dbinit;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;
/**
 * 
 *<p>Title:DbNameForm.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Sep 4, 2008:9:35:22 AM</p> 
 *@author huaitao
 *@version 1.0
 */
public class DbNameForm extends FrameForm {

	private ArrayList dbnamelist = new ArrayList();
	private String[] left_fields;
	private String vflag;
	private RecordVo dbvo=new RecordVo("dbname");
	public ArrayList getDbnamelist() {
		return dbnamelist;
	}

	public void setDbnamelist(ArrayList dbnamelist) {
		this.dbnamelist = dbnamelist;
	}

	public String[] getLeft_fields() {
		return left_fields;
	}

	public void setLeft_fields(String[] left_fields) {
		this.left_fields = left_fields;
	}

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setDbnamelist((ArrayList)this.getFormHM().get("dbnamelist"));
		this.setVflag((String)this.getFormHM().get("vflag"));
		this.setDbvo((RecordVo)this.getFormHM().get("dbvo"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
	}

	public String getVflag() {
		return vflag;
	}

	public void setVflag(String vflag) {
		this.vflag = vflag;
	}

	public RecordVo getDbvo() {
		return dbvo;
	}

	public void setDbvo(RecordVo dbvo) {
		this.dbvo = dbvo;
	}

}
