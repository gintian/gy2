/*
 * Created on 2006-4-10
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.actionform.report.org_maintenance;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

public class ReportTypeForm extends FrameForm {

	private int current=1;
	private PaginationForm reportTypeList = new PaginationForm();
	
	//在保存报表类别时使用，填报单位编码
	private String rtUnitCode;
	
	//批量表类授权中的填报单位编码集合
	private String rtUnitCodes;
	
	@Override
    public void outPutFormHM() {
		//显示报表表信息
		this.getReportTypeList().setList((ArrayList)this.getFormHM().get("reporttypelist"));
		this.getReportTypeList().getPagination().gotoPage(current);	
		this.setRtUnitCode((String)this.getFormHM().get("rtunitcode"));
		this.setRtUnitCodes((String)this.getFormHM().get("rtunitcodes"));
	}

	
	@Override
    public void inPutTransHM() {
		//选中的填报单位对象
		this.getFormHM().put("selectedlist",this.getReportTypeList().getSelectedList());
	}
	
	
	/**
	 * @return Returns the reportTypeList.
	 */
	public PaginationForm getReportTypeList() {
		return reportTypeList;
	}
	/**
	 * @param reportTypeList The reportTypeList to set.
	 */
	public void setReportTypeList(PaginationForm reportTypeList) {
		this.reportTypeList = reportTypeList;
	}
	

	public String getRtUnitCode() {
		return rtUnitCode;
	}


	public void setRtUnitCode(String rtUnitCode) {
		this.rtUnitCode = rtUnitCode;
	}


	public String getRtUnitCodes() {
		return rtUnitCodes;
	}


	public void setRtUnitCodes(String rtUnitCodes) {
		this.rtUnitCodes = rtUnitCodes;
	}
	
	
}
