package com.hjsj.hrms.actionform.org.orgdata;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class OrgDataForm extends FrameForm {
	private String selectsql="";
	private String itemsql="";
	private String fieldsetid = "";
	private ArrayList itemlist = new ArrayList();
	private ArrayList fieldlist = new ArrayList();
	private ArrayList searchlist = new ArrayList();
	private ArrayList setlist = new ArrayList();
	private String tablename = "";
	private String itemtable = "";
	private String setname = "";
	private String a_code = "";
	private String infor = "";
	private String viewsearch = "";
	private String sort_str;
	private String viewdata;
	private String checkadd;
	private String itemid="";
	private String defitem="";
	private String loadtype="";
	private ArrayList fieldslist=new ArrayList();
	private String itemVal="";
	private String orgType="";
	private String priItem="";
	private ArrayList subFlds=new ArrayList();
	private String i9999="";
	private ArrayList codeitemlist=new ArrayList();
	private String codeitem="";
	private String checkorg="";
	private ArrayList readOnlyFlds = new ArrayList();
	private ArrayList readOnlyFlds2 = new ArrayList();
	private String isInsert="1";
	private String reserveitem="";
	private String resitemid="";
	private String curri9999="";//插入子集时候用
	private FormFile picturefile; //上传文件
	@Override
    public void outPutFormHM() {
	    	this.setIsInsert((String)this.getFormHM().get("isInsert"));
		this.setA_code((String)this.getFormHM().get("a_code"));
		this.setSelectsql((String)this.getFormHM().get("selectsql"));
		this.setTablename((String)this.getFormHM().get("tablename"));
		this.setInfor((String)this.getFormHM().get("infor"));
		this.setFieldsetid((String)this.getFormHM().get("fieldsetid"));
		this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.setSetlist((ArrayList)this.getFormHM().get("setlist"));
		this.setSetname((String)this.getFormHM().get("setname"));
		this.setViewsearch((String)this.getFormHM().get("viewsearch"));
		this.setSort_str((String)this.getFormHM().get("sort_str"));
		this.setViewdata((String)this.getFormHM().get("viewdata"));
		this.setSearchlist((ArrayList)this.getFormHM().get("searchlist"));
		this.setCheckadd((String)this.getFormHM().get("checkadd"));
		this.setItemid((String)this.getFormHM().get("itemid"));
		this.setDefitem((String)this.getFormHM().get("defitem"));
		this.setItemsql((String)this.getFormHM().get("itemsql"));
		this.setItemtable((String)this.getFormHM().get("itemtable"));
		this.setLoadtype((String)this.getFormHM().get("loadtype"));
		this.setFieldslist((ArrayList)this.getFormHM().get("fieldslist"));
		this.setItemVal((String)this.getFormHM().get("itemVal"));
		this.setOrgType((String)this.getFormHM().get("orgType"));
		this.setPriItem((String)this.getFormHM().get("priItem"));
		this.setCodeitem((String)this.getFormHM().get("codeitem"));
		this.setCodeitemlist((ArrayList)this.getFormHM().get("codeitemlist"));
		this.setSubFlds((ArrayList)this.getFormHM().get("subFlds"));
		this.setI9999((String)this.getFormHM().get("i9999"));
		this.setCheckorg((String)this.getFormHM().get("checkorg"));
		this.setReadOnlyFlds((ArrayList)this.getFormHM().get("readOnlyFlds"));
		this.setReadOnlyFlds2((ArrayList)this.getFormHM().get("readOnlyFlds2"));
		this.setReserveitem((String)this.getFormHM().get("reserveitem"));
	    this.setResitemid((String)this.getFormHM().get("resitemid"));
	    this.setCurri9999((String)this.getFormHM().get("curri9999"));
	}

	@Override
    public void inPutTransHM() {
	    	 this.getFormHM().put("curri9999",this.getCurri9999());
	    	 this.getFormHM().put("isInsert",this.getIsInsert());
		 this.getFormHM().put("setname",this.getSetname());
		 this.getFormHM().put("viewsearch",this.getViewsearch());
		 this.getFormHM().put("sort_str",this.getSort_str());
		 this.getFormHM().put("viewdata",this.getViewdata());
		 this.getFormHM().put("defitem",this.getDefitem());
		 this.getFormHM().put("fieldslist",this.getFieldslist());
		 this.getFormHM().put("itemVal",this.getItemVal());
		 this.getFormHM().put("orgType",this.getOrgType());
		 this.getFormHM().put("subFlds", this.getSubFlds());
		 this.getFormHM().put("i9999", this.getI9999());
		 this.getFormHM().put("readOnlyFlds",this.getReadOnlyFlds());
		 this.getFormHM().put("readOnlyFlds2",this.getReadOnlyFlds2());
		 this.getFormHM().put("picturefile", this.getPicturefile());
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
        /**定位到首页,*/
        if(this.getPagination()!=null)
        	this.getPagination().firstPage();
        if("/org/orgdata/orgdata".equals(arg0.getPath()) && arg1.getParameter("b_ritem")!=null)
        {
        	this.setSetname("");
        }
        return super.validate(arg0, arg1);
	}

	public String getA_code() {
		return a_code;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
	}

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String getFieldsetid() {
		return fieldsetid;
	}

	public void setFieldsetid(String fieldsetid) {
		this.fieldsetid = fieldsetid;
	}

	public String getInfor() {
		return infor;
	}

	public void setInfor(String infor) {
		this.infor = infor;
	}

	public ArrayList getItemlist() {
		return itemlist;
	}

	public void setItemlist(ArrayList itemlist) {
		this.itemlist = itemlist;
	}

	public String getSelectsql() {
		return selectsql;
	}

	public void setSelectsql(String selectsql) {
		this.selectsql = selectsql;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public ArrayList getSetlist() {
		return setlist;
	}

	public void setSetlist(ArrayList setlist) {
		this.setlist = setlist;
	}

	public String getSetname() {
		return setname;
	}

	public void setSetname(String setname) {
		this.setname = setname;
	}

	public String getViewsearch() {
		return viewsearch;
	}

	public void setViewsearch(String viewsearch) {
		this.viewsearch = viewsearch;
	}

	public String getSort_str() {
		return sort_str;
	}

	public void setSort_str(String sort_str) {
		this.sort_str = sort_str;
	}

	public String getViewdata() {
		return viewdata;
	}

	public void setViewdata(String viewdata) {
		this.viewdata = viewdata;
	}

	public ArrayList getSearchlist() {
		return searchlist;
	}

	public void setSearchlist(ArrayList searchlist) {
		this.searchlist = searchlist;
	}

	public String getCheckadd() {
		return checkadd;
	}

	public void setCheckadd(String checkadd) {
		this.checkadd = checkadd;
	}

	public String getDefitem() {
		return defitem;
	}

	public void setDefitem(String defitem) {
		this.defitem = defitem;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public String getItemsql() {
		return itemsql;
	}

	public void setItemsql(String itemsql) {
		this.itemsql = itemsql;
	}

	public String getItemtable() {
		return itemtable;
	}

	public void setItemtable(String itemtable) {
		this.itemtable = itemtable;
	}

	public String getLoadtype() {
		return loadtype;
	}

	public void setLoadtype(String loadtype) {
		this.loadtype = loadtype;
	}

	public ArrayList getFieldslist()
	{
	
	    return fieldslist;
	}

	public void setFieldslist(ArrayList fieldslist)
	{
	
	    this.fieldslist = fieldslist;
	}



	public String getItemVal()
	{
	
	    return itemVal;
	}

	public void setItemVal(String itemVal)
	{
	
	    this.itemVal = itemVal;
	}

	public String getOrgType()
	{
	
	    return orgType;
	}

	public void setOrgType(String orgType)
	{
	
	    this.orgType = orgType;
	}

	public String getPriItem() {
		return priItem;
	}

	public void setPriItem(String priItem) {
		this.priItem = priItem;
	}

	public String getCodeitem() {
		return codeitem;
	}

	public void setCodeitem(String codeitem) {
		this.codeitem = codeitem;
	}

	public ArrayList getCodeitemlist() {
		return codeitemlist;
	}

	public void setCodeitemlist(ArrayList codeitemlist) {
		this.codeitemlist = codeitemlist;
	}

	public String getI9999() {
		return i9999;
	}

	public void setI9999(String i9999) {
		this.i9999 = i9999;
	}

	public ArrayList getSubFlds() {
		return subFlds;
	}

	public void setSubFlds(ArrayList subFlds) {
		this.subFlds = subFlds;
	}

	public String getCheckorg() {
		return checkorg;
	}

	public void setCheckorg(String checkorg) {
		this.checkorg = checkorg;
	}

	public ArrayList getReadOnlyFlds()
	{
	
	    return readOnlyFlds;
	}

	public void setReadOnlyFlds(ArrayList readOnlyFlds)
	{
	
	    this.readOnlyFlds = readOnlyFlds;
	}

	public String getIsInsert()
	{
	
	    return isInsert;
	}

	public void setIsInsert(String isInsert)
	{
	
	    this.isInsert = isInsert;
	}

	public ArrayList getReadOnlyFlds2()
	{
	
	    return readOnlyFlds2;
	}

	public void setReadOnlyFlds2(ArrayList readOnlyFlds2)
	{
	
	    this.readOnlyFlds2 = readOnlyFlds2;
	}

	public String getReserveitem() {
		return reserveitem;
	}

	public void setReserveitem(String reserveitem) {
		this.reserveitem = reserveitem;
	}

	public String getResitemid() {
		return resitemid;
	}

	public void setResitemid(String resitemid) {
		this.resitemid = resitemid;
	}

	public String getCurri9999()
	{
	
	    return curri9999;
	}

	public void setCurri9999(String curri9999)
	{
	
	    this.curri9999 = curri9999;
	}

	public FormFile getPicturefile() {
		return picturefile;
	}

	public void setPicturefile(FormFile picturefile) {
		this.picturefile = picturefile;
	}
	
}
