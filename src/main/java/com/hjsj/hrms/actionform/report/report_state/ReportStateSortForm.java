package com.hjsj.hrms.actionform.report.report_state;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

public class ReportStateSortForm extends FrameForm {

	private int current=1;
	private PaginationForm reportTypeList = new PaginationForm();
	
	@Override
    public void outPutFormHM() {
		//显示报表表信息
		this.getReportTypeList().setList((ArrayList)this.getFormHM().get("reporttypelist"));
		this.getReportTypeList().getPagination().gotoPage(current);	
		this.setReturnflag((String)this.getFormHM().get("returnflag"));
	}

	@Override
    public void inPutTransHM() {
		
	}

	
	public PaginationForm getReportTypeList() {
		return reportTypeList;
	}

	public void setReportTypeList(PaginationForm reportTypeList) {
		this.reportTypeList = reportTypeList;
	}

	
	
}
