package com.hjsj.hrms.actionform.general.sys.validate;

import com.hrms.struts.action.FrameForm;

public class SecondValidateForm extends FrameForm{

	private String phoneNumber;
	private String passPhone="";
	
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("passPhone", this.getPassPhone());
		this.getFormHM().put("phoneNumber", this.getPhoneNumber());
	}
	@Override
    public void outPutFormHM() {
		this.setPassPhone((String)this.getFormHM().get("passPhone"));
		this.setPhoneNumber((String)this.getFormHM().get("phoneNumber"));
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getPassPhone() {
		return passPhone;
	}
	public void setPassPhone(String passPhone) {
		this.passPhone = passPhone;
	}
	
}
