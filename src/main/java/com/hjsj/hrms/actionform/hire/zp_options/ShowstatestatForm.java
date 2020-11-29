package com.hjsj.hrms.actionform.hire.zp_options;

import com.hrms.struts.action.FrameForm;

import java.util.HashMap;

public class ShowstatestatForm extends FrameForm {
	private String height;
    private String schoolPosition;
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		this.setSchoolPosition((String)hm.get("schoolPosition"));
		this.setHeight((String) hm.get("height"));
		this.setReturnflag((String)hm.get("returnflag"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		hm.put("height",this.getHeight());
		hm.put("returnflag", this.getReturnflag());
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getSchoolPosition() {
		return schoolPosition;
	}

	public void setSchoolPosition(String schoolPosition) {
		this.schoolPosition = schoolPosition;
	}

}
