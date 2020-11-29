package com.hjsj.hrms.actionform.ht.ctstatic;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class StAnalysisForm extends FrameForm {
	/**子集列表*/
	private ArrayList setlist=new ArrayList();
	/**指标列表*/
	private ArrayList itemlist=new ArrayList();
	/**子集名称*/
	private String setid="";
	/**指标名称*/
	private String itemid="";
	/**值列表*/
	private ArrayList valuelist=new ArrayList();
	/**表*/
	private String tablestr="";
	/**应用库前缀*/
	private ArrayList dblist=new ArrayList();
	/**应用库前缀*/
	private String dbname="";
	/**图表名称*/
	private String charname="";
	private String orgcode="";
	
	/** 可选的字段名数组 */
	private String left_fields[];

	/** 选中的字段名数组 */
	 private String right_fields[];
	 
	/**待选的指标集合*/
	 private ArrayList fieldsSet = new ArrayList();
	 
	 /**已选的指标集合*/
	 private ArrayList fieldsSel = new ArrayList();
	
	 private String subSet ="";
	 
	 private ArrayList datalist=new ArrayList();
	 
	// list页面用
	private PaginationForm setlistform = new PaginationForm();
	//变动字段的中文名字
	private ArrayList items=new ArrayList();
	
	private String a_code = "";
	
	private String itemvalue = "";	
	
	private String sqlStr = "";
	
	// 是否通过左边菜单进入统计分析,1为是，0为否
	private String isFromLeft;
	// 组织机构代码（为避免冲突，不适a_code，重新定义变量）
	private String code;
	
	private String returnvalue = "1";
	
	public String getIsFromLeft() {
		return isFromLeft;
	}

	public void setIsFromLeft(String isFromLeft) {
		this.isFromLeft = isFromLeft;
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("setid",this.getSetid());
		this.getFormHM().put("itemid",this.getItemid());
		this.getFormHM().put("dbname",this.getDbname());
		this.getFormHM().put("left_fields", this.getLeft_fields());
		this.getFormHM().put("right_fields", this.getRight_fields());
		this.getFormHM().put("fieldsSet", this.getFieldsSet());
		this.getFormHM().put("fieldsSel", this.getFieldsSel());
		this.getFormHM().put("subSet", this.getSubSet());
		this.getFormHM().put("items", this.getItems());
		this.getFormHM().put("a_code", this.getA_code());
		this.getFormHM().put("itemvalue", this.getItemvalue());	
		this.getFormHM().put("sqlStr", this.getSqlStr());	
		this.getFormHM().put("isFromLeft", this.getIsFromLeft());
	}

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setSetlist((ArrayList)this.getFormHM().get("setlist"));
		this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
		this.setValuelist((ArrayList)this.getFormHM().get("valuelist"));
		this.setSetid((String)this.getFormHM().get("setid"));
		this.setTablestr((String)this.getFormHM().get("tablestr"));
		this.setItemid((String)this.getFormHM().get("itemid"));
		this.setDbname((String)this.getFormHM().get("dbname"));
		this.setDblist((ArrayList)this.getFormHM().get("dblist"));
		this.setCharname((String)this.getFormHM().get("charname"));
		this.setOrgcode((String)this.getFormHM().get("orgcode"));
		this.setLeft_fields((String[]) this.getFormHM().get("left_fields"));
		this.setRight_fields((String[]) this.getFormHM().get("right_fields"));
		this.setFieldsSet((ArrayList)this.getFormHM().get("fieldsSet"));
		this.setFieldsSel((ArrayList)this.getFormHM().get("fieldsSel"));
		this.setSubSet((String)this.getFormHM().get("subSet"));
		this.getSetlistform().setList((ArrayList) this.getFormHM().get("datalist"));
		this.setDatalist((ArrayList) this.getFormHM().get("datalist"));
		this.setItems((ArrayList) this.getFormHM().get("items"));
		this.setA_code((String)this.getFormHM().get("a_code"));
		this.setItemvalue((String)this.getFormHM().get("itemvalue"));
		this.setSqlStr((String)this.getFormHM().get("sqlStr"));
		this.setIsFromLeft((String)this.getFormHM().get("isFromLeft"));
		this.setCode((String) this.getFormHM().get("code"));
	}
	
	
	    @Override
        public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	    {
		try
		{
		    if ("/ht/ctstatic/ht_static_detail".equals(arg0.getPath()) && arg1.getParameter("b_query") != null)
		    {		
			if (this.setlistform.getPagination() != null){
			    this.setlistform.getPagination().firstPage();
			}
		    }
		    
		    if ("/ht/ctstatic/ctanalysis".equals(arg0.getPath()) && arg1.getParameter("b_tree") != null) {
		    	this.isFromLeft = "1";
		    } 
		} catch (Exception e)
		{
		    e.printStackTrace();
		}
		return super.validate(arg0, arg1);
	    }

	public ArrayList getItemlist() {
		return itemlist;
	}

	public void setItemlist(ArrayList itemlist) {
		this.itemlist = itemlist;
	}

	public String getSetid() {
		return setid;
	}

	public void setSetid(String setid) {
		this.setid = setid;
	}

	public ArrayList getSetlist() {
		return setlist;
	}

	public void setSetlist(ArrayList setlist) {
		this.setlist = setlist;
	}

	public ArrayList getValuelist() {
		return valuelist;
	}

	public void setValuelist(ArrayList valuelist) {
		this.valuelist = valuelist;
	}

	public String getTablestr() {
		return tablestr;
	}

	public void setTablestr(String tablestr) {
		this.tablestr = tablestr;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public ArrayList getDblist() {
		return dblist;
	}

	public void setDblist(ArrayList dblist) {
		this.dblist = dblist;
	}

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
	}

	public String getCharname() {
		return charname;
	}

	public void setCharname(String charname) {
		this.charname = charname;
	}

	public String getOrgcode() {
		return orgcode;
	}

	public void setOrgcode(String orgcode) {
		this.orgcode = orgcode;
	}

	public String[] getLeft_fields()
	{
	
	    return left_fields;
	}

	public void setLeft_fields(String[] left_fields)
	{
	
	    this.left_fields = left_fields;
	}

	public String[] getRight_fields()
	{
	
	    return right_fields;
	}

	public void setRight_fields(String[] right_fields)
	{
	
	    this.right_fields = right_fields;
	}

	public ArrayList getFieldsSet()
	{
	
	    return fieldsSet;
	}

	public void setFieldsSet(ArrayList fieldsSet)
	{
	
	    this.fieldsSet = fieldsSet;
	}

	public ArrayList getFieldsSel()
	{
	
	    return fieldsSel;
	}

	public void setFieldsSel(ArrayList fieldsSel)
	{
	
	    this.fieldsSel = fieldsSel;
	}

	public String getSubSet()
	{
	
	    return subSet;
	}

	public void setSubSet(String subSet)
	{
	
	    this.subSet = subSet;
	}

	public ArrayList getDatalist()
	{
	
	    return datalist;
	}

	public void setDatalist(ArrayList datalist)
	{
	
	    this.datalist = datalist;
	}

	public PaginationForm getSetlistform()
	{
	
	    return setlistform;
	}

	public void setSetlistform(PaginationForm setlistform)
	{
	
	    this.setlistform = setlistform;
	}

	public ArrayList getItems()
	{
	
	    return items;
	}

	public void setItems(ArrayList items)
	{
	
	    this.items = items;
	}

	public String getA_code()
	{
	
	    return a_code;
	}

	public void setA_code(String a_code)
	{
	
	    this.a_code = a_code;
	}

	public String getItemvalue()
	{
	
	    return itemvalue;
	}

	public void setItemvalue(String itemvalue)
	{
	
	    this.itemvalue = itemvalue;
	}

	public String getSqlStr()
	{
	
	    return sqlStr;
	}

	public void setSqlStr(String sqlStr)
	{
	
	    this.sqlStr = sqlStr;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

    public void setReturnvalue(String returnvalue) {
        this.returnvalue = returnvalue;
    }

    public String getReturnvalue() {
        return returnvalue;
    }
	
}
