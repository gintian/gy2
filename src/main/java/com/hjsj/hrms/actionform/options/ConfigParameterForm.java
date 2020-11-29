package com.hjsj.hrms.actionform.options;

import com.hrms.struts.action.FrameForm;

public class ConfigParameterForm extends FrameForm {
	
	
	private String redio;
	
	public String getRedio() {
		return redio;
	}

	public void setRedio(String redio) {
		this.redio = redio;
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("redio",this.getRedio());
		
	}

	@Override
    public void outPutFormHM() {
		
		this.setRedio((String)this.getFormHM().get("redio"));
		
	}







}
