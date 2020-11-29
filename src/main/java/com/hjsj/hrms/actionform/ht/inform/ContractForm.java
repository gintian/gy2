package com.hjsj.hrms.actionform.ht.inform;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class ContractForm extends FrameForm {
	/**应用库前缀*/
	private String dbname;
	/***数据表名称*/
	private String tablename;
	/**查询语句*/
	private String sql;
	/**查询语句*/
	private String itemsql;
	/**字段列表*/
	private ArrayList fieldlist=new ArrayList();
	/**子集列表*/
	private ArrayList setlist=new ArrayList();
	/**应用库前缀*/
	private ArrayList dblist=new ArrayList();
	 /**组织机构代码*/
    private String a_code;
    /**常用查询*/
    private ArrayList searchlist = new ArrayList();
    /**是否显示查询结果*/
    private String viewsearch;
    /***子集数据表名称*/
	private String itemtable;
	private String defitem=""; /**选项卡默认子集按钮*/
	private String a0100=""; 
	/**子集指标列表*/
	private ArrayList itemlist=new ArrayList();
	 /**合同状态*/
    private ArrayList ctflaglist = new ArrayList();
    private String ctflag=""; 
    /**是否显示签订、续签、终止、解除、变更按钮*/
    private String checkflag=""; 

    /**主集字段*/
    HashMap mainFlds = new HashMap();
    /**子集字段*/
    ArrayList subFlds = new ArrayList();
    /**子集记录总数，大于1为1，小于1为0**/
    private String count = "1";
    
    private String i9999 = "";
    
    private String returnvalue="1";
    
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("a_code",this.getA_code());
		this.getFormHM().put("viewsearch",this.getViewsearch());
		this.getFormHM().put("defitem",this.getDefitem());
		this.getFormHM().put("dbname", this.getDbname());
		this.getFormHM().put("ctflag", this.getCtflag());
		this.getFormHM().put("mainFlds", this.getMainFlds());
		this.getFormHM().put("subFlds", this.getSubFlds());
		this.getFormHM().put("i9999", this.getI9999());
	}


	@Override
    public void outPutFormHM() {
		this.setDbname((String)this.getFormHM().get("dbname"));
		this.setTablename((String)this.getFormHM().get("tablename"));
		this.setSql((String)this.getFormHM().get("sql"));
		this.setItemsql((String)this.getFormHM().get("itemsql"));
		this.setFieldlist((ArrayList)this.getFormHM().get("fieldlist"));
		this.setSetlist((ArrayList)this.getFormHM().get("setlist"));
		this.setDblist((ArrayList)this.getFormHM().get("dblist"));
		this.setA_code((String)this.getFormHM().get("a_code"));
		this.setSearchlist((ArrayList)this.getFormHM().get("searchlist"));
		this.setViewsearch((String)this.getFormHM().get("viewsearch"));
		this.setItemtable((String)this.getFormHM().get("itemtable"));
		this.setDefitem((String)this.getFormHM().get("defitem"));
		this.setA0100((String)this.getFormHM().get("a0100"));
		this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
		this.setCtflag((String)this.getFormHM().get("ctflag"));
		this.setCtflaglist((ArrayList)this.getFormHM().get("ctflaglist"));
		this.setCheckflag((String)this.getFormHM().get("checkflag"));
		this.setMainFlds((HashMap)this.getFormHM().get("mainFlds"));
		this.setSubFlds((ArrayList)this.getFormHM().get("subFlds"));
		this.setI9999((String)this.getFormHM().get("i9999"));
		this.setCount((String) this.getFormHM().get("count"));
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		try{
			if("/ht/inform/data_table".equals(arg0.getPath())
					&&arg1.getParameter("b_query")!=null){
				if(this.getPagination()!=null)
					this.getPagination().firstPage();
			}else if("/ht/inform/data_table".equals(arg0.getPath())
					&&arg1.getParameter("b_menu")!=null){
				if(this.getPagination()!=null)
					this.getPagination().firstPage();
			}else if("/ht/inform/data_table".equals(arg0.getPath())
					&&arg1.getParameter("b_item")!=null){
				if(this.getPagination()!=null)
					this.getPagination().firstPage();
			}			

		}catch(Exception e){
			e.printStackTrace();
		}
		return super.validate(arg0, arg1);
	}
	public String getA_code() {
		return a_code;
	}

	public void setA_code(String a_code) {
		this.a_code = a_code;
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

	public ArrayList getFieldlist() {
		return fieldlist;
	}

	public void setFieldlist(ArrayList fieldlist) {
		this.fieldlist = fieldlist;
	}

	public String getItemsql() {
		return itemsql;
	}

	public void setItemsql(String itemsql) {
		this.itemsql = itemsql;
	}

	public ArrayList getSearchlist() {
		return searchlist;
	}

	public void setSearchlist(ArrayList searchlist) {
		this.searchlist = searchlist;
	}

	public ArrayList getSetlist() {
		return setlist;
	}

	public void setSetlist(ArrayList setlist) {
		this.setlist = setlist;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public String getViewsearch() {
		return viewsearch;
	}

	public void setViewsearch(String viewsearch) {
		this.viewsearch = viewsearch;
	}

	public String getItemtable() {
		return itemtable;
	}

	public void setItemtable(String itemtable) {
		this.itemtable = itemtable;
	}

	public String getDefitem() {
		return defitem;
	}

	public void setDefitem(String defitem) {
		this.defitem = defitem;
	}

	public String getA0100() {
		return a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

	public ArrayList getItemlist() {
		return itemlist;
	}

	public void setItemlist(ArrayList itemlist) {
		this.itemlist = itemlist;
	}

	public String getCtflag() {
		return ctflag;
	}

	public void setCtflag(String ctflag) {
		this.ctflag = ctflag;
	}

	public ArrayList getCtflaglist() {
		return ctflaglist;
	}

	public void setCtflaglist(ArrayList ctflaglist) {
		this.ctflaglist = ctflaglist;
	}

	public String getCheckflag() {
		return checkflag;
	}

	public void setCheckflag(String checkflag) {
		this.checkflag = checkflag;
	}


	public HashMap getMainFlds()
	{
	
	    return mainFlds;
	}


	public void setMainFlds(HashMap mainFlds)
	{
	
	    this.mainFlds = mainFlds;
	}


	public ArrayList getSubFlds()
	{
	
	    return subFlds;
	}


	public void setSubFlds(ArrayList subFlds)
	{
	
	    this.subFlds = subFlds;
	}


	public String getI9999()
	{
	
	    return i9999;
	}


	public void setI9999(String i9999)
	{
	
	    this.i9999 = i9999;
	}


	public String getCount() {
		return count;
	}


	public void setCount(String count) {
		this.count = count;
	}


    public void setReturnvalue(String returnvalue) {
        this.returnvalue = returnvalue;
    }


    public String getReturnvalue() {
        return returnvalue;
    }
	
}
