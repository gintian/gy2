
package com.hjsj.hrms.actionform.report.org_maintenance;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

//报表负责人
public class ReportUserForm extends FrameForm {
	
	//分页处理
	private int current=1;
	private PaginationForm reportUserListForm = new PaginationForm();
	
	//填报单位编码与JSP中的隐藏域对应
	private String usUnitCode;
	
	@Override
    public void outPutFormHM() {
		
		this.getReportUserListForm().setList((ArrayList)this.getFormHM().get("reportuserlist"));
		this.getReportUserListForm().getPagination().gotoPage(current);
		this.setUsUnitCode((String)this.getFormHM().get("usunitcode"));
	}

	
	@Override
    public void inPutTransHM() {
		//选中的用户对象
		this.getFormHM().put("selectedlist",this.getReportUserListForm().getSelectedList());
	}

	
	/**
	 * @return Returns the reportUserListForm.
	 */
	public PaginationForm getReportUserListForm() {
		return reportUserListForm;
	}
	/**
	 * @param reportUserListForm The reportUserListForm to set.
	 */
	public void setReportUserListForm(PaginationForm reportUserListForm) {
		this.reportUserListForm = reportUserListForm;
	}


	public String getUsUnitCode() {
		return usUnitCode;
	}


	public void setUsUnitCode(String usUnitCode) {
		this.usUnitCode = usUnitCode;
	}


}
