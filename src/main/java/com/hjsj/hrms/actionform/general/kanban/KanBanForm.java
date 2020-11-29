package com.hjsj.hrms.actionform.general.kanban;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class KanBanForm extends FrameForm {
	private ArrayList fieldlist=new ArrayList();/**字段列表*/
	private String sqlstr="";
	private String cloums="";
	private String orderby="";
	private ArrayList orderlist=new ArrayList();/**排序字段列表*/
	private String orderid="";
	private String p0500="";
	private ArrayList desclist=new ArrayList();/**升序降序字段列表*/
	private String descid="";
	private String checkflag="";//操作方式：增删改查
	private String checkperson=""; /**任务审核人*/
	private String person="";/**接单人*/
	private String billperson="";/**发单人*/
	private String kbtitle="";/**标题*/
	private String itemid="";/**查询指标*/
	private String fromnum=""; 
    private String tonum="";
    private String fromdate="";
    private String todate="";
    private String searchtext="";
    private String codeid="";
    private String hsearch="";
    private ArrayList itemlist=new ArrayList();/**查询字段列表*/
    private String filltable="";
    private String sortitem;//组合排序

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("orderid",this.getOrderid());
		this.getFormHM().put("descid",this.getDescid());
		this.getFormHM().put("fieldlist",this.getFieldlist());
		this.getFormHM().put("checkperson",this.getCheckperson());
		this.getFormHM().put("person",this.getPerson());
		this.getFormHM().put("fromnum", this.getFromnum());
    	this.getFormHM().put("tonum", this.getTonum());
    	this.getFormHM().put("fromdate", this.getFromdate());
    	this.getFormHM().put("todate", this.getTodate());
    	this.getFormHM().put("searchtext", this.getSearchtext());
    	this.getFormHM().put("itemid", this.getItemid());
    	this.getFormHM().put("codeid", this.getCodeid());
    	this.getFormHM().put("hsearch", this.getHsearch());
    	this.getFormHM().put("sortitem", this.getSortitem());
	}

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.setSqlstr((String)this.getFormHM().get("sqlstr"));
		this.setCloums((String)this.getFormHM().get("cloums"));
		this.setOrderby((String)this.getFormHM().get("orderby"));
		this.setOrderlist((ArrayList)this.getFormHM().get("orderlist"));
		this.setOrderid((String)this.getFormHM().get("orderid"));
		this.setP0500((String)this.getFormHM().get("p0500"));
		this.setDescid((String)this.getFormHM().get("descid"));
		this.setDesclist((ArrayList)this.getFormHM().get("desclist"));
		this.setCheckflag((String)this.getFormHM().get("checkflag"));
		this.setCheckperson((String)this.getFormHM().get("checkperson"));
		this.setPerson((String)this.getFormHM().get("person"));
		this.setBillperson((String)this.getFormHM().get("billperson"));
		this.setKbtitle((String)this.getFormHM().get("kbtitle"));
		this.setFromnum((String) this.getFormHM().get("fromnum"));
    	this.setTonum((String) this.getFormHM().get("tonum"));
    	this.setFromdate((String) this.getFormHM().get("fromdate"));
    	this.setTodate((String) this.getFormHM().get("todate"));
    	this.setSearchtext((String) this.getFormHM().get("searchtext"));
    	this.setItemid((String) this.getFormHM().get("itemid"));
    	this.setCodeid((String) this.getFormHM().get("codeid"));
    	this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
    	this.setHsearch((String) this.getFormHM().get("hsearch"));
    	this.setFilltable((String) this.getFormHM().get("filltable"));
    	this.setSortitem((String)this.getFormHM().get("sortitem"));
	}

	public String getCloums() {
		return cloums;
	}

	public void setCloums(String cloums) {
		this.cloums = cloums;
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public String getSqlstr() {
		return sqlstr;
	}

	public void setSqlstr(String sqlstr) {
		this.sqlstr = sqlstr;
	}

	public ArrayList getOrderlist() {
		return orderlist;
	}

	public void setOrderlist(ArrayList orderlist) {
		this.orderlist = orderlist;
	}

	public String getDescid() {
		return descid;
	}

	public void setDescid(String descid) {
		this.descid = descid;
	}

	public ArrayList getDesclist() {
		return desclist;
	}

	public void setDesclist(ArrayList desclist) {
		this.desclist = desclist;
	}

	public String getCheckflag() {
		return checkflag;
	}

	public void setCheckflag(String checkflag) {
		this.checkflag = checkflag;
	}

	public String getP0500() {
		return p0500;
	}

	public void setP0500(String p0500) {
		this.p0500 = p0500;
	}

	public String getCheckperson() {
		return checkperson;
	}

	public void setCheckperson(String checkperson) {
		this.checkperson = checkperson;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getBillperson() {
		return billperson;
	}

	public void setBillperson(String billperson) {
		this.billperson = billperson;
	}

	public String getKbtitle() {
		return kbtitle;
	}

	public void setKbtitle(String kbtitle) {
		this.kbtitle = kbtitle;
	}

	public String getFromdate() {
		return fromdate;
	}

	public void setFromdate(String fromdate) {
		this.fromdate = fromdate;
	}

	public String getFromnum() {
		return fromnum;
	}

	public void setFromnum(String fromnum) {
		this.fromnum = fromnum;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public String getSearchtext() {
		return searchtext;
	}

	public void setSearchtext(String searchtext) {
		this.searchtext = searchtext;
	}

	public String getTodate() {
		return todate;
	}

	public void setTodate(String todate) {
		this.todate = todate;
	}

	public String getTonum() {
		return tonum;
	}

	public void setTonum(String tonum) {
		this.tonum = tonum;
	}

	public String getCodeid() {
		return codeid;
	}

	public void setCodeid(String codeid) {
		this.codeid = codeid;
	}

	public ArrayList getItemlist() {
		return itemlist;
	}

	public void setItemlist(ArrayList itemlist) {
		this.itemlist = itemlist;
	}
	public String getHsearch() {
		return hsearch;
	}

	public void setHsearch(String hsearch) {
		this.hsearch = hsearch;
	}

	public String getFilltable() {
		return filltable;
	}

	public void setFilltable(String filltable) {
		this.filltable = filltable;
	}

	public String getSortitem() {
		return sortitem;
	}

	public void setSortitem(String sortitem) {
		this.sortitem = sortitem;
	}

}
