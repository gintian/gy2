package com.hjsj.hrms.actionform.train.trainCosts;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class TrainCostsForm extends FrameForm {
	private String viewunit="";
	private String r2501="";
	private String sql="";
	private String wherestr="";
	private String columns="";
	private ArrayList setlist=new ArrayList();
	private String sort_fields="";
	private ArrayList sortlist = new ArrayList();
	private String returnvalue;
	
	private ArrayList assessList = new ArrayList();
	private String recTab = ""; //培训资源表
	
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setViewunit((String)this.getFormHM().get("viewunit"));
		this.setR2501((String)this.getFormHM().get("r2501"));
		this.setSql((String)this.getFormHM().get("sql"));
		this.setWherestr((String)this.getFormHM().get("wherestr"));
		this.setColumns((String)this.getFormHM().get("columns"));
		this.setSetlist((ArrayList)this.getFormHM().get("setlist"));
		this.setSort_fields((String)this.getFormHM().get("sort_fields"));
		this.setSortlist((ArrayList)this.getFormHM().get("sortlist"));
		this.setAssessList((ArrayList)this.getFormHM().get("assessList"));
		this.setRecTab((String)this.getFormHM().get("recTab"));
	}

	@Override
    public void inPutTransHM() {
		this.getFormHM().put("sort_fields", this.getSort_fields());
		this.getFormHM().put("sortlist", this.getSortlist());
	}
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		   try{
			   if("/train/trainCosts/trainCosts".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null){
				   if(this.getPagination()!=null)
					   this.getPagination().firstPage();
			   }
		   }catch(Exception e){
		   	  e.printStackTrace();
		   }
	       return super.validate(arg0, arg1); 
	}
	public String getViewunit() {
		return viewunit;
	}

	public void setViewunit(String viewunit) {
		this.viewunit = viewunit;
	}

	public String getR2501() {
		return r2501;
	}

	public void setR2501(String r2501) {
		this.r2501 = r2501;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
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

	public String getWherestr() {
		return wherestr;
	}

	public void setWherestr(String wherestr) {
		this.wherestr = wherestr;
	}

	public String getSort_fields()
	{
	
	    return sort_fields;
	}

	public void setSort_fields(String sort_fields)
	{
	
	    this.sort_fields = sort_fields;
	}

	public ArrayList getSortlist()
	{
	
	    return sortlist;
	}

	public void setSortlist(ArrayList sortlist)
	{
	
	    this.sortlist = sortlist;
	}

	public String getReturnvalue() {
		return returnvalue;
	}

	public void setReturnvalue(String returnvalue) {
		this.returnvalue = returnvalue;
	}

	public ArrayList getAssessList() {
		return assessList;
	}

	public void setAssessList(ArrayList assessList) {
		this.assessList = assessList;
	}

	public String getRecTab(){
	    return this.recTab;
	}
	
	public void setRecTab(String recTab){
	    this.recTab = recTab;
	}
}
