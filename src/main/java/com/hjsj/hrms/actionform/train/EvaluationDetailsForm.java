package com.hjsj.hrms.actionform.train;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

public class EvaluationDetailsForm extends FrameForm{
	
	private ArrayList list = new ArrayList();
	
	private PaginationForm msgPageForm=new PaginationForm();
	
	private String flag = "";

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("list", this.getList());
		this.getFormHM().put("flag", this.getFlag());
	}

	@Override
    public void outPutFormHM() {
		this.setList((ArrayList)this.getFormHM().get("list"));
		this.getMsgPageForm().setList((ArrayList)this.getFormHM().get("list"));
		this.setFlag((String)this.getFormHM().get("flag"));
	}
	
	public ArrayList getList() {
		return list;
	}
	
	public void setList(ArrayList list) {
		this.list = list;
	}
	
	public PaginationForm getMsgPageForm() {
		return msgPageForm;
	}

	public void setMsgPageForm(PaginationForm msgPageForm) {
		this.msgPageForm = msgPageForm;
	}
	
	public String getFlag(){
	    return flag;
	}
	
	public void setFlag(String flag){
	    this.flag = flag;
	}
}
