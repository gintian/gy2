package com.hjsj.hrms.actionform.lawbase;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

public class GlobalSearchForm extends FrameForm {
	private PaginationForm paginationForm = new PaginationForm();
	
	private String basetype;

	private String term;
	
	private String base_id;

	public PaginationForm getPaginationForm() {
		return paginationForm;
	}

	public void setPaginationForm(PaginationForm paginationForm) {
		this.paginationForm = paginationForm;
	}

	public GlobalSearchForm() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	@Override
    public void outPutFormHM() {
		getPaginationForm().setList((ArrayList) this.getFormHM().get("myList"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("term", term);
		this.getFormHM().put("base_id", base_id);
		this.getFormHM().put("basetype", basetype);
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getBase_id() {
		return base_id;
	}

	public void setBase_id(String base_id) {
		this.base_id = base_id;
	}

	public String getBasetype() {
		return basetype;
	}

	public void setBasetype(String basetype) {
		this.basetype = basetype;
	}

}
