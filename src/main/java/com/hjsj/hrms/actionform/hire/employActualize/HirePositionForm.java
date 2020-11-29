package com.hjsj.hrms.actionform.hire.employActualize;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;
public class HirePositionForm extends FrameForm {
	private PaginationForm positionListform=new PaginationForm();
	private String a0100="";
	
	
	@Override
    public void outPutFormHM() {
		this.getPositionListform().setList((ArrayList)this.getFormHM().get("positionList"));
		this.setA0100((String)this.getFormHM().get("a0100"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub

	}

	public String getA0100() {
		return a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

	public PaginationForm getPositionListform() {
		return positionListform;
	}

	public void setPositionListform(PaginationForm positionListform) {
		this.positionListform = positionListform;
	}
}
