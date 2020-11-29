package com.hjsj.hrms.actionform.hire.zp_options.itemstat;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShowDailyForm extends FrameForm {
	/*
	 * 分页显示属性
	 */
	 private String sql;
	 private String where;
	 private String column;
	 private String orderby;
	 private PaginationForm pageListForm = new PaginationForm();
	//	开始时间
	private String startime ;
	//	　结束时间
	private String endtime;

	//部门id
	private String depid;
	
	//	职位id 
	private String jobid;
	
	//部门list
	private List  deplist;

	//职位list
	private List  joblist;
	
	private HashMap  joblistview;
	
	private String notes;
	private String schoolPosition;
	private PaginationForm recordListForm=new PaginationForm();
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		this.setSchoolPosition((String)hm.get("schoolPosition"));
		this.setReturnflag((String)hm.get("returnflag"));
		this.setSql((String) hm.get("sql"));
		this.setWhere((String) hm.get("where"));
		this.setColumn((String) hm.get("column"));
		this.setOrderby((String)hm.get("orderby"));
		this.setStartime((String) hm.get("startime"));
		this.setEndtime((String) hm.get("endtime"));
		this.setDepid((String) hm.get("depid"));
		this.setJobid((String) hm.get("jobid"));
		this.setDeplist((List) hm.get("deplist"));
		this.setJoblist((List) hm.get("joblist"));
		this.setJoblistview((HashMap) hm.get("joblistview"));
		this.setNotes((String) hm.get("notes"));
		this.getRecordListForm().setList((ArrayList)this.getFormHM().get("dataList"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		this.getFormHM().put("selectedlist",(ArrayList)this.getRecordListForm().getSelectedList());
		if(this.getPagination()!=null)
			hm.put("selitem",(ArrayList)this.getPagination().getSelectedList());
		hm.put("startime",this.getStartime());
		hm.put("endtime",this.getEndtime());
		hm.put("jobid",this.getJobid());
		hm.put("depid",this.getDepid());
		hm.put("joblistview",this.getJoblistview());
		hm.put("returnflag", this.getReturnflag());
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/hire/zp_options/stat/itemstat/showjobdaily".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null){
            /**定位到首页,*/
            if(this.getPagination()!=null){
            	this.getPagination().firstPage();    
            	
            }
            if(this.getRecordListForm()!=null)
				this.getRecordListForm().getPagination().firstPage();
        }
		return super.validate(arg0, arg1);
	}
	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

	public PaginationForm getPageListForm() {
		return pageListForm;
	}

	public void setPageListForm(PaginationForm pageListForm) {
		this.pageListForm = pageListForm;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}
	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}
	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getStartime() {
		return startime;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getNotes() {
		return notes;
	}

	public void setStartime(String startime) {
		this.startime = startime;
	}
	
	public void setDepid(String depid) {
		this.depid = depid;
	}
	public String getDepid() {
		return depid;
	}

	public void setJobid(String jobid) {
		this.jobid = jobid;
	}
	public String getJobid() {
		return jobid;
	}

	public void setDeplist(List deplist) {
		this.deplist = deplist;
	}
	public List getDeplist() {
		return deplist;
	}

	public void setJoblist(List joblist) {
		this.joblist = joblist;
	}
	public List getJoblist() {
		return joblist;
	}
	public void setJoblistview(HashMap joblistview) {
		this.joblistview = joblistview;
	}
	public HashMap getJoblistview() {
		return joblistview;
	}

	public String getSchoolPosition() {
		return schoolPosition;
	}

	public void setSchoolPosition(String schoolPosition) {
		this.schoolPosition = schoolPosition;
	}

	public PaginationForm getRecordListForm() {
		return recordListForm;
	}

	public void setRecordListForm(PaginationForm recordListForm) {
		this.recordListForm = recordListForm;
	}

}
