package com.hjsj.hrms.actionform.report.report_isApprove;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

public class Report_isApproveForm extends FrameForm {

	private ArrayList  list=new ArrayList();
	private PaginationForm report_isApproveForm = new PaginationForm();
	private String tabid = "";
	private String content = "";
	private String content2 = "";
	private String flag = "";
	private RecordVo treport_ctrl=new RecordVo("treport_ctrl");
	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("selectedlist", this.getReport_isApproveForm().getSelectedList());
		this.getFormHM().put("treport_ctrl", this.getTreport_ctrl());
	}

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setList((ArrayList) this.getFormHM().get("list"));
		this.getReport_isApproveForm().setList((ArrayList) this.getFormHM().get("list"));
		this.setTabid((String) this.getFormHM().get("tabid"));
		this.setContent((String) this.getFormHM().get("content"));
		this.setFlag((String) this.getFormHM().get("flag"));
		this.setContent2((String) this.getFormHM().get("content2"));
		this.setTreport_ctrl((RecordVo) this.getFormHM().get("treport_ctrl"));
	}

	public ArrayList getList() {
		return list;
	}

	public void setList(ArrayList list) {
		this.list = list;
	}

	public PaginationForm getReport_isApproveForm() {
		return report_isApproveForm;
	}

	public void setReport_isApproveForm(PaginationForm report_isApproveForm) {
		this.report_isApproveForm = report_isApproveForm;
	}

	public String getTabid() {
		return tabid;
	}

	public void setTabid(String tabid) {
		this.tabid = tabid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getContent2() {
		return content2;
	}

	public void setContent2(String content2) {
		this.content2 = content2;
	}

	public RecordVo getTreport_ctrl() {
		return treport_ctrl;
	}

	public void setTreport_ctrl(RecordVo treport_ctrl) {
		this.treport_ctrl = treport_ctrl;
	}
	
	

}
