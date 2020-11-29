package com.hjsj.hrms.actionform.lawbase;

import com.hrms.struts.action.FrameForm;
public class RebuildIndexForm extends FrameForm{
    private String a_base_id;
    private String basetype;
	public RebuildIndexForm() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
    public void outPutFormHM() {
		
	}

	/*
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("a_base_id", a_base_id);
		this.getFormHM().put("basetype", basetype);
	}
	
	public String getA_base_id() {
		return a_base_id;
	}
	public void setA_base_id(String a_base_id) {
		this.a_base_id = a_base_id;
	}
	public String getBasetype() {
		return basetype;
	}
	public void setBasetype(String basetype) {
		this.basetype = basetype;
	}

}
