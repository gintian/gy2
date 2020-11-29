package com.hjsj.hrms.actionform.report.report_state;

import com.hrms.struts.action.FrameForm;

public class ReportStatePanelForm extends FrameForm {

	private String code ; 
	
	@Override
    public void outPutFormHM() {
		this.setCode((String)this.getFormHM().get("ucode"));
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
	}

	@Override
    public void inPutTransHM() {
		
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	

}
