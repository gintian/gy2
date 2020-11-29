/*
 * Created on 2005-9-15
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.hire;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * <p>Title:ZpFilterForm</p>
 * <p>Description:人员面试表单</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 21, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class ZpInterviewForm extends FrameForm {
	/**
	 * 面试环节对象
	 */
	private RecordVo zptachevo = new RecordVo("ZP_TACHE");

	/**
	 * 面试环节对象列表
	 */
	private PaginationForm zptacheForm = new PaginationForm();
	/**
	 * 候选人对象
	 */
	private RecordVo zpPosTachevo = new RecordVo("ZP_POS_TACHE");
	/**
	 * 候选人对象列表
	 */
	private PaginationForm zpPosTacheForm = new PaginationForm();
	/**
	 * 招聘岗位对象
	 */
	private RecordVo zpDeptPosvo = new RecordVo("ZP_POSITION");

	/**
	 * 招聘岗位对象列表
	 */
	private PaginationForm zpDeptPosForm = new PaginationForm();
	/**
	 * 面试纪录对象
	 */
	private RecordVo zpProcessLogvo = new RecordVo("ZP_PROCESS_LOG");

	/**
	 * 面试纪录对象列表
	 */
	private PaginationForm zpProcessLogForm = new PaginationForm();
	
	private String description = "";
	/*人员id*/
	private String a0100;
	/*人员库*/
	private String dbpre;
	
	@Override
    public void outPutFormHM() {
		this.setZpDeptPosvo((RecordVo) this.getFormHM().get("zpDeptPosvo"));
		this.getZpDeptPosForm().setList(
				(ArrayList) this.getFormHM().get("zpDeptPoslist"));
		this.setZpPosTachevo((RecordVo) this.getFormHM().get("zpPosTachevo"));
		this.getZpPosTacheForm().setList(
				(ArrayList) this.getFormHM().get("zpPosTachelist"));
		this.setZpProcessLogvo((RecordVo) this.getFormHM().get("zpProcessLogvo"));
		this.getZpProcessLogForm().setList(
				(ArrayList) this.getFormHM().get("zpProcessLoglist"));
		this.setDescription((String)this.getFormHM().get("description"));
		this.setDbpre((String)this.getFormHM().get("dbpre"));

	}

	/* (non-Javadoc)
	 * @see com.hrms.struts.action.FrameForm#inPutTransHM()
	 */
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("zpDeptPosvo", this.getZpDeptPosvo());
		this.getFormHM().put("zptachevo", this.getZptachevo());
		this.getFormHM().put("zpPosTachevo", this.getZpPosTachevo());
		this.getFormHM().put("selectedlist",
				(ArrayList) this.getZpPosTacheForm().getSelectedList());
		this.getFormHM().put("zpProcessLogvo", this.getZpProcessLogvo());
		this.getFormHM().put("description", this.getDescription());
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		return super.validate(arg0, arg1);
	}
	/**
	 * @return Returns the zpDeptPosForm.
	 */
	public PaginationForm getZpDeptPosForm() {
		return zpDeptPosForm;
	}
	/**
	 * @param zpDeptPosForm The zpDeptPosForm to set.
	 */
	public void setZpDeptPosForm(PaginationForm zpDeptPosForm) {
		this.zpDeptPosForm = zpDeptPosForm;
	}
	/**
	 * @return Returns the zpDeptPosvo.
	 */
	public RecordVo getZpDeptPosvo() {
		return zpDeptPosvo;
	}
	/**
	 * @param zpDeptPosvo The zpDeptPosvo to set.
	 */
	public void setZpDeptPosvo(RecordVo zpDeptPosvo) {
		this.zpDeptPosvo = zpDeptPosvo;
	}
	/**
	 * @return Returns the zptachevo.
	 */
	public RecordVo getZptachevo() {
		return zptachevo;
	}
	/**
	 * @param zptachevo The zptachevo to set.
	 */
	public void setZptachevo(RecordVo zptachevo) {
		this.zptachevo = zptachevo;
	}
	/**
	 * @return Returns the zptachevoForm.
	 */
	public PaginationForm getZptacheForm() {
		return zptacheForm;
	}
	/**
	 * @param zptachevoForm The zptachevoForm to set.
	 */
	public void setZptacheForm(PaginationForm zptacheForm) {
		this.zptacheForm = zptacheForm;
	}
	/**
	 * @return Returns the zpPosTacheForm.
	 */
	public PaginationForm getZpPosTacheForm() {
		return zpPosTacheForm;
	}
	/**
	 * @param zpPosTacheForm The zpPosTacheForm to set.
	 */
	public void setZpPosTacheForm(PaginationForm zpPosTacheForm) {
		this.zpPosTacheForm = zpPosTacheForm;
	}
	/**
	 * @return Returns the zpProcessLogForm.
	 */
	public PaginationForm getZpProcessLogForm() {
		return zpProcessLogForm;
	}
	/**
	 * @param zpProcessLogForm The zpProcessLogForm to set.
	 */
	public void setZpProcessLogForm(PaginationForm zpProcessLogForm) {
		this.zpProcessLogForm = zpProcessLogForm;
	}
	/**
	 * @return Returns the zpProcessLogvo.
	 */
	public RecordVo getZpProcessLogvo() {
		return zpProcessLogvo;
	}
	/**
	 * @param zpProcessLogvo The zpProcessLogvo to set.
	 */
	public void setZpProcessLogvo(RecordVo zpProcessLogvo) {
		this.zpProcessLogvo = zpProcessLogvo;
	}
	/**
	 * @return Returns the zpPosTachevo.
	 */
	public RecordVo getZpPosTachevo() {
		return zpPosTachevo;
	}
	/**
	 * @param zpPosTachevo The zpPosTachevo to set.
	 */
	public void setZpPosTachevo(RecordVo zpPosTachevo) {
		this.zpPosTachevo = zpPosTachevo;
	}
	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return Returns the a0100.
	 */
	public String getA0100() {
		return a0100;
	}
	/**
	 * @param a0100 The a0100 to set.
	 */
	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}
	/**
	 * @return Returns the dbpre.
	 */
	public String getDbpre() {
		return dbpre;
	}
	/**
	 * @param dbpre The dbpre to set.
	 */
	public void setDbpre(String dbpre) {
		this.dbpre = dbpre;
	}
}
