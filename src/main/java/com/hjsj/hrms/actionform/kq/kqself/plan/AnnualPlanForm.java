package com.hjsj.hrms.actionform.kq.kqself.plan;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

public class AnnualPlanForm extends FrameForm {
	
	private String com;
	
	private String sql;
	
	private String where;
	private String order;
	private String plan_id;
	private ArrayList selectfieldlist= new ArrayList();
	private String year;
	private String table;
	// 年休假计划 新增的list参数
	private ArrayList flist=new ArrayList();
	private ArrayList slist =new ArrayList();
	private ArrayList tlist =new ArrayList();
	// 年休假计划 修改的list参数
	private ArrayList onelist =new ArrayList();
	private ArrayList approvelist= new ArrayList();	
	private String status;
	private String approve_result;
	private String approve_date;
	private PaginationForm recordListForm=new PaginationForm();
	private ArrayList q29z0list=new ArrayList();
	private String q29z0;
	private String param;
	private String sp_result;
	private String warn;
	public String getSp_result() {
		return sp_result;
	}

	public void setSp_result(String sp_result) {
		this.sp_result = sp_result;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getQ29z0() {
		return q29z0;
	}

	public void setQ29z0(String q29z0) {
		this.q29z0 = q29z0;
	}

	@Override
    public void outPutFormHM() {
		 this.setFlist((ArrayList)this.getFormHM().get("flist"));
		 this.setCom((String)this.getFormHM().get("com"));
		 this.setSql((String)this.getFormHM().get("sql"));
		 this.setOrder((String)this.getFormHM().get("order"));
		 this.setWhere((String)this.getFormHM().get("where"));
		 this.setPlan_id((String)this.getFormHM().get("plan_id"));
		 this.setYear((String)this.getFormHM().get("year"));
		 this.setSlist((ArrayList)this.getFormHM().get("slist"));
		 this.setTlist((ArrayList)this.getFormHM().get("tlist")); 
		 if(this.getFormHM().get("selectfieldlist")!=null)
	           this.setSelectfieldlist((ArrayList)this.getFormHM().get("selectfieldlist"));		
		 this.setPlan_id((String)this.getFormHM().get("plan_id"));
		 this.setOnelist((ArrayList)this.getFormHM().get("onelist"));
		 this.setApprovelist((ArrayList)this.getFormHM().get("approvelist"));
		 this.setStatus((String)this.getFormHM().get("status"));
		 this.setApprove_result((String)this.getFormHM().get("approve_result"));
		 this.setApprove_date((String)this.getFormHM().get("approve_date"));
		 this.setQ29z0list((ArrayList)this.getFormHM().get("q29z0list"));
		 this.setQ29z0((String)this.getFormHM().get("q29z0"));
		 this.setSp_result((String)this.getFormHM().get("sp_result"));
		 this.setWarn((String)this.getFormHM().get("warn"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("flist",this.getFlist()); 
		this.getFormHM().put("year",this.getYear());
		this.getFormHM().put("plan_id",this.getPlan_id());
		if(this.getPagination()!=null)			
			   this.getFormHM().put("selectedinfolist",(ArrayList)this.getPagination().getSelectedList());
		this.getFormHM().put("onelist",this.getOnelist());
		this.getFormHM().put("approvelist",this.getApprovelist());
		this.getFormHM().put("status",status);
		this.getFormHM().put("approve_result",approve_result);
		this.getFormHM().put("approve_date",approve_date);
		this.getFormHM().put("q29z0",this.getQ29z0());
		this.getFormHM().put("param",this.getParam());
		this.getFormHM().put("sp_result",this.getSp_result());
		this.getFormHM().put("warn",this.getWarn());
	}

	public ArrayList getFlist() {
		return flist;
	}

	public void setFlist(ArrayList flist) {
		this.flist = flist;
	}

	public String getCom() {
		return com;
	}

	public void setCom(String com) {
		this.com = com;
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

	public String getPlan_id() {
		return plan_id;
	}

	public void setPlan_id(String plan_id) {
		this.plan_id = plan_id;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public ArrayList getSlist() {
		return slist;
	}

	public void setSlist(ArrayList slist) {
		this.slist = slist;
	}

	public ArrayList getTlist() {
		return tlist;
	}

	public void setTlist(ArrayList tlist) {
		this.tlist = tlist;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public PaginationForm getRecordListForm() {
		return recordListForm;
	}

	public void setRecordListForm(PaginationForm recordListForm) {
		this.recordListForm = recordListForm;
	}

	public ArrayList getSelectfieldlist() {
		return selectfieldlist;
	}

	public void setSelectfieldlist(ArrayList selectfieldlist) {
		this.selectfieldlist = selectfieldlist;
	}

	public ArrayList getOnelist() {
		return onelist;
	}

	public void setOnelist(ArrayList onelist) {
		this.onelist = onelist;
	}

	public ArrayList getApprovelist() {
		return approvelist;
	}

	public void setApprovelist(ArrayList approvelist) {
		this.approvelist = approvelist;
	}

	public String getApprove_date() {
		return approve_date;
	}

	public void setApprove_date(String approve_date) {
		this.approve_date = approve_date;
	}

	public String getApprove_result() {
		return approve_result;
	}

	public void setApprove_result(String approve_result) {
		this.approve_result = approve_result;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public ArrayList getQ29z0list() {
		return q29z0list;
	}

	public void setQ29z0list(ArrayList q29z0list) {
		this.q29z0list = q29z0list;
	}

	public String getWarn()
	{
		return warn;
	}

	public void setWarn(String warn)
	{
		this.warn = warn;
	}


}
