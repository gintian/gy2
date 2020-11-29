package com.hjsj.hrms.actionform.kq.register;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

public class KqIndicatorForm extends FrameForm{
	private ArrayList fieldlist=new ArrayList();
	private ArrayList v_h_list=new ArrayList();
	private String re_flag;
	private PaginationForm recordListForm=new PaginationForm();   
	private String[] state;
	public String[] getState() {
		return state;
	}
	public void setState(String[] state) {
		this.state = state;
	}
	public PaginationForm getRecordListForm() {
		return recordListForm;
	}
	public void setRecordListForm(PaginationForm recordListForm) {
		this.recordListForm = recordListForm;
	}
	public ArrayList getFieldlist() {
		return fieldlist;
	}
	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}
	public ArrayList getV_h_list() {
		return v_h_list;
	}
	public void setV_h_list(ArrayList v_h_list) {
		this.v_h_list = v_h_list;
	}
	@Override
    public void outPutFormHM()
	{
		this.getRecordListForm().setList((ArrayList)this.getFormHM().get("fieldlist"));
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.setV_h_list((ArrayList)this.getFormHM().get("v_h_list"));
		this.setRe_flag((String)this.getFormHM().get("re_flag"));
		this.getRecordListForm().getPagination().gotoPage(1);
	}
	@Override
    public void inPutTransHM()
	{ 
		this.getFormHM().put("fieldlist",this.getFieldlist());
		this.getFormHM().put("state",this.getState());
		this.getFormHM().put("re_flag",this.getRe_flag());
	}
	public String getRe_flag() {
		return re_flag;
	}
	public void setRe_flag(String re_flag) {
		this.re_flag = re_flag;
	}
}
