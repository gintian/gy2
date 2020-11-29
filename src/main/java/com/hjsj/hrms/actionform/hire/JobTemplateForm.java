package com.hjsj.hrms.actionform.hire;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;

public class JobTemplateForm extends FrameForm {

	private RecordVo jobTemplatevo = new RecordVo("CONSTANT");
	private String edition;
	private String ps_c_card_attach = "";

	public RecordVo getJobTemplatevo() {
		return jobTemplatevo;
	}

	public void setJobTemplatevo(RecordVo jobTemplatevo) {
		this.jobTemplatevo = jobTemplatevo;
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public String getPs_c_card_attach() {
		return ps_c_card_attach;
	}

	public void setPs_c_card_attach(String ps_c_card_attach) {
		this.ps_c_card_attach = ps_c_card_attach;
	}

	@Override
    public void outPutFormHM() {
		this.setJobTemplatevo((RecordVo) this.getFormHM().get("jobTemplatevo"));
		this.setEdition((String) this.getFormHM().get("edition"));
		this.setPs_c_card_attach((String) this.getFormHM().get(
				"ps_c_card_attach"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("jobTemplatevo", this.getJobTemplatevo());
		this.getFormHM().put("ps_c_card_attach", this.getPs_c_card_attach());

	}
}
