/*
 * Created on 2005-9-1
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.hire;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;

/**
 * <p>Title:PosTemplateForm</p>
 * <p>Description:岗位职责说明书表单</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 09, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class PosTemplateForm extends FrameForm {

	/**
	 * 常量对象
	 */
	private RecordVo posTemplatevo = new RecordVo("CONSTANT");
	private String edition;
	private String ps_card_attach="";
	@Override
    public void outPutFormHM() {
		this.setPosTemplatevo((RecordVo)this.getFormHM().get("posTemplatevo"));
		this.setEdition((String)this.getFormHM().get("edition"));
		this.setPs_card_attach((String)this.getFormHM().get("ps_card_attach"));
	}
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("posTemplatevo", this.getPosTemplatevo());
		this.getFormHM().put("ps_card_attach", this.getPs_card_attach());
	}
	/**
	 * @return Returns the posTemplatevo.
	 */
	public RecordVo getPosTemplatevo() {
		return posTemplatevo;
	}
	/**
	 * @param posTemplatevo The posTemplatevo to set.
	 */
	public void setPosTemplatevo(RecordVo posTemplatevo) {
		this.posTemplatevo = posTemplatevo;
	}
	public String getEdition() {
		return edition;
	}
	public void setEdition(String edition) {
		this.edition = edition;
	}
	public String getPs_card_attach() {
		return ps_card_attach;
	}
	public void setPs_card_attach(String ps_card_attach) {
		this.ps_card_attach = ps_card_attach;
	}
}
