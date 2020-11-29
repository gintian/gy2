package com.hjsj.hrms.actionform.general.query;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
/**
 * 复杂查询
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jul 5, 2008</p> 
 *@author sunxin
 *@version 4.0
 */
public class ComplexInterfaceForm extends FrameForm {
    private ArrayList complexList=new ArrayList();
    private ArrayList setList=new ArrayList();
	private ArrayList itemList=new ArrayList();
	private String fieldItems="";
	private String complex_expr="";
	private String complex_id="";
	private String strsql="";
	private String columns="";
	private String dbpre="";
	private String tabid="";
	private ArrayList dblist=new ArrayList();
	private ArrayList compledblist=new ArrayList();
	private String comple_db="";
	public ArrayList getCompledblist() {
		return compledblist;
	}
	public void setCompledblist(ArrayList compledblist) {
		this.compledblist = compledblist;
	}

	public String getComple_db() {
		return comple_db;
	}
	public void setComple_db(String comple_db) {
		this.comple_db = comple_db;
	}
	@Override
    public void outPutFormHM() {
		this.setComplexList((ArrayList)this.getFormHM().get("complexList"));
		this.setSetList((ArrayList)this.getFormHM().get("setList"));
		this.setItemList((ArrayList)this.getFormHM().get("itemList"));
		this.setFieldItems((String)this.getFormHM().get("fieldItems"));
		this.setComplex_expr((String)this.getFormHM().get("complex_expr"));
		this.setDblist((ArrayList)this.getFormHM().get("dblist"));
		this.setDbpre((String)this.getFormHM().get("dbpre"));
		this.setStrsql((String)this.getFormHM().get("strsql"));
		this.setColumns((String)this.getFormHM().get("columns"));
		this.setTabid((String)this.getFormHM().get("tabid"));
		this.setCompledblist((ArrayList)this.getFormHM().get("compledblist"));
		this.setComple_db((String)this.getFormHM().get("comple_db"));		
	}
	@Override
    public void inPutTransHM() {
		this.getFormHM().put("complex_expr", this.getComplex_expr());
		this.getFormHM().put("complex_id", this.getComplex_id());
		this.getFormHM().put("dbpre", this.getDbpre());
		this.getFormHM().put("comple_db", this.comple_db);
	}
	 @Override
     public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	 {
	    if("/workbench/query/complex_interface".equals(arg0.getPath())&&arg1.getParameter("b_gquery")!=null)
	    {
	            if(this.getPagination()!=null)
	              this.getPagination().firstPage();
	            this.setComplex_id("");
	            this.getFormHM().put("complex_id", "");
	    }
	    if("/workbench/query/complex_interface".equals(arg0.getPath())&&arg1.getParameter("b_qsearch")!=null)
	    {
	            if(this.getPagination()!=null)
	              this.getPagination().firstPage();	            
	            this.getFormHM().put("dbpre", "");
	    }
		return super.validate(arg0, arg1);
	 }
    public String getColumns() {
		return columns;
	}
	public void setColumns(String columns) {
		this.columns = columns;
	}
	public ArrayList getDblist() {
		return dblist;
	}
	public void setDblist(ArrayList dblist) {
		this.dblist = dblist;
	}
	public String getDbpre() {
		return dbpre;
	}
	public void setDbpre(String dbpre) {
		this.dbpre = dbpre;
	}
	public String getStrsql() {
		return strsql;
	}
	public void setStrsql(String strsql) {
		this.strsql = strsql;
	}
	public String getComplex_expr() {
		return complex_expr;
	}
	public void setComplex_expr(String complex_expr) {
		this.complex_expr = complex_expr;
	}

	public ArrayList getComplexList() {
		return complexList;
	}
	public void setComplexList(ArrayList complexList) {
		this.complexList = complexList;
	}
	public String getFieldItems() {
		return fieldItems;
	}
	public void setFieldItems(String fieldItems) {
		this.fieldItems = fieldItems;
	}
	public ArrayList getItemList() {
		return itemList;
	}
	public void setItemList(ArrayList itemList) {
		this.itemList = itemList;
	}
	public ArrayList getSetList() {
		return setList;
	}
	public void setSetList(ArrayList setList) {
		this.setList = setList;
	}
	public String getComplex_id() {
		return complex_id;
	}
	public void setComplex_id(String complex_id) {
		this.complex_id = complex_id;
	}
	public String getTabid() {
		return tabid;
	}
	public void setTabid(String tabid) {
		this.tabid = tabid;
	}
	
}
