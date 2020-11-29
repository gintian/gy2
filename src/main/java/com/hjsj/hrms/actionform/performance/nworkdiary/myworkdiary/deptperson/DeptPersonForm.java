package com.hjsj.hrms.actionform.performance.nworkdiary.myworkdiary.deptperson;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class DeptPersonForm extends FrameForm{
	private ArrayList list = new ArrayList();
	private String a0100 = "";
	private String nbase = "";
	
	private String fromFlag="";//=1从部门人员进入,=2从部门人员进入处室人员,=3从处室人员进入
	private String htmlStr="";
	private String e0122="";
	
	@Override
    public void inPutTransHM()
	{
		this.getFormHM().put("fromFlag", this.getFromFlag());
		this.getFormHM().put("e0122", this.getE0122());
		this.getFormHM().put("a0100", this.getA0100());
		this.getFormHM().put("nbase", this.getNbase());
	}
	@Override
    public void outPutFormHM()
	{	
		this.setList((ArrayList)this.getFormHM().get("list"));
		this.setA0100((String)this.getFormHM().get("a0100"));
		this.setNbase((String)this.getFormHM().get("nbase"));
		this.setFromFlag((String)this.getFormHM().get("fromFlag"));
		this.setHtmlStr((String)this.getFormHM().get("htmlStr"));
		this.setE0122((String)this.getFormHM().get("e0122"));
	}
	public ArrayList getList() {
		return list;
	}
	public void setList(ArrayList list) {
		this.list = list;
	}
	public String getA0100() {
		return a0100;
	}
	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}
	public String getNbase() {
		return nbase;
	}
	public void setNbase(String nbase) {
		this.nbase = nbase;
	}
	public String getFromFlag() {
		return fromFlag;
	}
	public void setFromFlag(String fromFlag) {
		this.fromFlag = fromFlag;
	}
	public String getHtmlStr() {
		return htmlStr;
	}
	public void setHtmlStr(String htmlStr) {
		this.htmlStr = htmlStr;
	}
	public String getE0122() {
		return e0122;
	}
	public void setE0122(String e0122) {
		this.e0122 = e0122;
	}
	
}

