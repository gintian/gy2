package com.hjsj.hrms.actionform.sys.options;

import com.hrms.struts.action.FrameForm;

public class CardSalaryShowForm extends FrameForm {
	
	private String a0100;
	private String flag;
	private String pre;
	private String view_photo;//是否是照片墙跳转过去的
	private String b0110;
	private String payment;
	private String showFlag;
	private String recardconstant;//薪酬纪录方式
	public String getRecardconstant() {
		return recardconstant;
	}

	public void setRecardconstant(String recardconstant) {
		this.recardconstant = recardconstant;
	}

	@Override
    public void outPutFormHM() {
		this.setPayment((String)this.getFormHM().get("payment"));
		this.setA0100((String)this.getFormHM().get("a0100"));
		this.setFlag((String)this.getFormHM().get("flag"));
		this.setPre((String)this.getFormHM().get("pre"));
		this.setView_photo((String)this.getFormHM().get("view_photo"));
		this.setShowFlag((String)this.getFormHM().get("showflag"));
		this.setB0110((String)this.getFormHM().get("b0110"));
		this.setRecardconstant((String)this.getFormHM().get("recardconstant"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("flag", this.getFlag());
		this.getFormHM().put("view_photo", this.getView_photo());
		this.getFormHM().put("a0100", this.getA0100());
		this.getFormHM().put("b0110", this.getB0110());
		this.getFormHM().put("payment", this.getPayment());
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
	
	public String getView_photo() {
		return view_photo;
	}

	public void setView_photo(String view_photo) {
		this.view_photo = view_photo;
	}

	public String getShowFlag() {
		return showFlag;
	}

	public void setShowFlag(String showFlag) {
		this.showFlag = showFlag;
	}

	public String getB0110() {
		return b0110;
	}

	public void setB0110(String b0110) {
		this.b0110 = b0110;
	}

	public String getPayment() {
		return payment;
	}

	public void setPayment(String payment) {
		this.payment = payment;
	}

	
}
