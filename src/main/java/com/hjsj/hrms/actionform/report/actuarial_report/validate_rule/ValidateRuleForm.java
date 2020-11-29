/**
 * 
 */
package com.hjsj.hrms.actionform.report.actuarial_report.validate_rule;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

/**
 * <p>Title:报表周期</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:oct 6, 2009:10:46:29 AM</p>
 * @author xieguiquan
 * @version 1.0
 * 
 */
public class ValidateRuleForm extends FrameForm {
	private String paramcopy="";
	private String paramcopy2="";
	
	/**
	 * 建议对象列表
	 */

	private PaginationForm validateRuleForm = new PaginationForm();
	
	private String    tableHtml="";
	private String    tableHtml2="";
	
	@Override
    public void inPutTransHM() {
	//	this.getFormHM().put("reportcyclevo", this.getReportcyclevo());

	}

	@Override
    public void outPutFormHM() {
		this.setTableHtml((String)this.getFormHM().get("tableHtml"));
		this.setTableHtml2((String)this.getFormHM().get("tableHtml2"));
		// this.getValidateRuleForm().setList((ArrayList)this.getFormHM().get("rulelist"));
		 this.setParamcopy((String)this.getFormHM().get("paramcopy"));
		 this.setParamcopy2((String)this.getFormHM().get("paramcopy2"));
	}

	

	public String getTableHtml() {
		return tableHtml;
	}

	public void setTableHtml(String tableHtml) {
		this.tableHtml = tableHtml;
	}

	
	public PaginationForm getValidateRuleForm() {
		return validateRuleForm;
	}

	public void setValidateRuleForm(PaginationForm validateRuleForm) {
		this.validateRuleForm = validateRuleForm;
	}

	public String getParamcopy() {
		return paramcopy;
	}

	public void setParamcopy(String paramcopy) {
		this.paramcopy = paramcopy;
	}

	public String getParamcopy2() {
		return paramcopy2;
	}

	public void setParamcopy2(String paramcopy2) {
		this.paramcopy2 = paramcopy2;
	}

	public String getTableHtml2() {
		return tableHtml2;
	}

	public void setTableHtml2(String tableHtml2) {
		this.tableHtml2 = tableHtml2;
	}

}
