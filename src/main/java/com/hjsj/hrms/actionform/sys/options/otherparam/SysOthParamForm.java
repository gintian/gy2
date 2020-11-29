package com.hjsj.hrms.actionform.sys.options.otherparam;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

public class SysOthParamForm extends FrameForm {
	private String sql;
	private String where;
	private String column;
	private String orderby;
	private PaginationForm pageListForm = new PaginationForm();
	private String treecode;
	private String selStr;
	private HashMap dbMap;
	private HashMap itemMap;
	private String cid;
	private String dbvalid;
	private String itemvalid;
	private String view_check;
	public String getView_check() {
		return view_check;
	}

	public void setView_check(String view_check) {
		this.view_check = view_check;
	}

	public String getSelStr() {
		return selStr;
	}

	public void setSelStr(String selStr) {
		this.selStr = selStr;
	}

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		this.setTreecode((String) hm.get("treecode"));
		this.setSql((String) hm.get("sql"));
		this.setColumn((String) hm.get("column"));
		this.setWhere((String) hm.get("where"));
		this.setOrderby((String) hm.get("orderby"));
		this.setSelStr((String) hm.get("selStr"));
		this.setDbMap((HashMap)hm.get("dbMap"));
		this.setItemMap((HashMap)hm.get("itemMap"));
		this.setDbvalid((String) hm.get("dbvalid"));
		this.setItemvalid((String) hm.get("itemvalid"));
		this.setView_check((String)hm.get("view_check"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		hm.put("cid",this.getCid());
		hm.put("dbvlaid",this.getDbvalid());
		hm.put("itemvalid",this.getItemvalid());
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/system/options/otherparam/showsetitem".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
        }
		if("/system/options/otherparam/showdbitem".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null)
        {
            /**定位到首页,*/
            if(this.getPagination()!=null)
            	this.getPagination().firstPage();              
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

	public String getTreecode() {
		return treecode;
	}

	public void setTreecode(String treecode) {
		this.treecode = treecode;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public HashMap getDbMap() {
		return dbMap;
	}

	public void setDbMap(HashMap dbMap) {
		this.dbMap = dbMap;
	}

	public HashMap getItemMap() {
		return itemMap;
	}

	public void setItemMap(HashMap itemMap) {
		this.itemMap = itemMap;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getDbvalid() {
		return dbvalid;
	}

	public void setDbvalid(String dbvalid) {
		this.dbvalid = dbvalid;
	}

	public String getItemvalid() {
		return itemvalid;
	}

	public void setItemvalid(String itemvalid) {
		this.itemvalid = itemvalid;
	}

}
