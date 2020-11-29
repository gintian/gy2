package com.hjsj.hrms.actionform.report.edit_report;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

public class StaticStatementForm extends FrameForm {
	String tabid="0";
	String use_scope_cond="";						//是否使用统计口径，0不使用(默认值), 1使用
	String scopename ="";							//口径名称
	String scopeownerunit ="";						//所属机构
	String scopeunits ="";							//统计单位名称
	String scopeunitsids="";						//统计单位ID
	String scopeownerunitid="";						//所属机构id
	String scopeid="";								//口径id
	ArrayList sortList = new ArrayList();			//口径排序
	String sort_fields[];							//口径选择节点
	ArrayList unitslist = new ArrayList();			//部门列表
	ArrayList scopelist = new ArrayList();			//口径列表
	ArrayList unitcodelist=new ArrayList();			//
	PaginationForm staticStatementForm=new PaginationForm();
	String odscopeunitsids="";
	String position="";
	String method="";
	String count="";
	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
	
	
	@Override
    public void outPutFormHM() {
		if(this.getFormHM().get("tabid")!=null)
			this.setTabid((String)this.getFormHM().get("tabid"));
		this.setUse_scope_cond((String)this.getFormHM().get("use_scope_cond"));
		this.setScopename((String)this.getFormHM().get("scopename"));
		this.setScopeownerunit((String)this.getFormHM().get("scopeownerunit"));
		this.setScopeunits((String)this.getFormHM().get("scopeunits"));
		this.setScopeownerunitid((String)this.getFormHM().get("scopeownerunitid"));
		this.setScopeunitsids((String)this.getFormHM().get("scopeunitsids"));
		this.setScopeid((String)this.getFormHM().get("scopeid"));
		this.setSortList((ArrayList)this.getFormHM().get("sortList"));
		this.setUnitslist((ArrayList)this.getFormHM().get("unitslist"));
		this.setScopelist((ArrayList)this.getFormHM().get("scopelist"));
		this.setUnitcodelist((ArrayList)this.getFormHM().get("unitcodelist"));
		this.getStaticStatementForm().setList((ArrayList)this.getFormHM().get("unitcodelist"));
		this.setOdscopeunitsids((String)this.getFormHM().get("odscopeunitsids"));
		this.setCount(String.valueOf(this.staticStatementForm.getPagination().getCount()));
	}

	@Override
    public void inPutTransHM() {
	//	this.getFormHM().put("tabid",this.getTabid());
		this.getFormHM().put("selectedList", this.staticStatementForm.getSelectedList());
		this.getFormHM().put("position", this.getPosition());
		this.getFormHM().put("method", this.getMethod());
	}


	public String getTabid() {
		return tabid;
	}

	public void setTabid(String tabid) {
		this.tabid = tabid;
	}


	public String getUse_scope_cond() {
		return use_scope_cond;
	}

	public void setUse_scope_cond(String use_scope_cond) {
		this.use_scope_cond = use_scope_cond;
	}

	public String getScopeownerunitid() {
		return scopeownerunitid;
	}

	public void setScopeownerunitid(String scopeownerunitid) {
		this.scopeownerunitid = scopeownerunitid;
	}

	public ArrayList getSortList() {
		return sortList;
	}

	public void setSortList(ArrayList sortList) {
		this.sortList = sortList;
	}

	public String getScopeid() {
		return scopeid;
	}

	public void setScopeid(String scopeid) {
		this.scopeid = scopeid;
	}

	public String getScopename() {
		return scopename;
	}

	public String getScopeunitsids() {
		return scopeunitsids;
	}

	public void setScopeunitsids(String scopeunitsids) {
		this.scopeunitsids = scopeunitsids;
	}

	public String[] getSort_fields() {
		return sort_fields;
	}

	public void setSort_fields(String[] sort_fields) {
		this.sort_fields = sort_fields;
	}

	public void setScopename(String scopename) {
		this.scopename = scopename;
	}

	public String getScopeownerunit() {
		return scopeownerunit;
	}

	public void setScopeownerunit(String scopeownerunit) {
		this.scopeownerunit = scopeownerunit;
	}

	public String getScopeunits() {
		return scopeunits;
	}

	public void setScopeunits(String scopeunits) {
		this.scopeunits = scopeunits;
	}

	public ArrayList getUnitslist() {
		return unitslist;
	}

	public void setUnitslist(ArrayList unitslist) {
		this.unitslist = unitslist;
	}

	public ArrayList getScopelist() {
		return scopelist;
	}

	public void setScopelist(ArrayList scopelist) {
		this.scopelist = scopelist;
	}

	public ArrayList getUnitcodelist() {
		return unitcodelist;
	}

	public void setUnitcodelist(ArrayList unitcodelist) {
		this.unitcodelist = unitcodelist;
	}

	public PaginationForm getStaticStatementForm() {
		return staticStatementForm;
	}

	public void setStaticStatementForm(PaginationForm staticStatementForm) {
		this.staticStatementForm = staticStatementForm;
	}

	public String getOdscopeunitsids() {
		return odscopeunitsids;
	}

	public void setOdscopeunitsids(String odscopeunitsids) {
		this.odscopeunitsids = odscopeunitsids;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	
	
}
