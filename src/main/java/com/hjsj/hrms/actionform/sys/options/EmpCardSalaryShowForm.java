package com.hjsj.hrms.actionform.sys.options;

import com.hrms.struts.action.FrameForm;

public class EmpCardSalaryShowForm extends FrameForm {
	
	private String a0100;
	private String flag;
	private String pre;
	private String b0110;
	private String tabid="0";
	private String recardconstant;//薪酬纪录方式
	public String getRecardconstant() {
		return recardconstant;
	}

	public void setRecardconstant(String recardconstant) {
		this.recardconstant = recardconstant;
	}

	public String getB0110() {
		return b0110;
	}

	public void setB0110(String b0110) {
		this.b0110 = b0110;
	}

	public String getTabid() {
		return tabid;
	}

	public void setTabid(String tabid) {
		this.tabid = tabid;
	}

	@Override
    public void outPutFormHM() {
		this.setA0100((String)this.getFormHM().get("a0100"));
		this.setFlag((String)this.getFormHM().get("flag"));
		this.setPre((String)this.getFormHM().get("pre"));
		this.setB0110((String)this.getFormHM().get("b0110"));
		this.setTabid((String)this.getFormHM().get("tabid"));
		this.setRecardconstant((String)this.getFormHM().get("recardconstant"));
	}

	@Override
    public void inPutTransHM() {
	}

	public String getA0100() {
		return a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getPre() {
		return pre;
	}

	public void setPre(String pre) {
		this.pre = pre;
	}

}
