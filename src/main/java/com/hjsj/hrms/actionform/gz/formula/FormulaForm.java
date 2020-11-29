package com.hjsj.hrms.actionform.gz.formula;

import com.hrms.struts.action.FrameForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
/**
 *<p>Title:</p> 
 *<p>Description:计算公式</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
**/
public class FormulaForm extends FrameForm {
	/*
	 * 分页显示属性
	 */
	 private String sql;
	 private String where;
	 private String column;
	 private String orderby;
	 
	private String formula;//计算公式
	
	private String[] codesetid_arr;
	
	private String fieldsetid;
	

	private String itemid;
	private ArrayList itemlist = new ArrayList();
	
	private String salaryid;
	
	private String runflag;
	private String item;
	
	/**
	 * 提供增加公式的项目选项
	 * */
	private String formulaitemid;
	private ArrayList formulaitemlist = new ArrayList();
	
	 /***
	  * 临时中的项目调整顺序
	  * */
	private String[] sort_fields; 
	private ArrayList sortlist = new ArrayList();
	
	private String conditions; //计算条件
	private String itemname; 
	
	/**
	 * 选择薪资标准表
	 * */
	private String standardid;
	private ArrayList standardlist = new ArrayList();
	
	/**
	 * 选择税率表
	 * */
	private String taxid;
	private ArrayList taxlist = new ArrayList();

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setSql((String) this.getFormHM().get("sql"));
		this.setWhere((String) this.getFormHM().get("where"));
		this.setColumn((String) this.getFormHM().get("column"));
		this.setOrderby((String)this.getFormHM().get("orderby"));
		this.setFormula((String)this.getFormHM().get("formula"));
		this.setItemid((String)this.getFormHM().get("itemid"));
		this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
		this.setSalaryid((String)this.getFormHM().get("salaryid"));
		this.setRunflag((String)this.getFormHM().get("runflag"));
		this.setItem((String)this.getFormHM().get("item"));
		this.setCodesetid_arr((String[])this.getFormHM().get("codesetid_arr"));
		this.setFormulaitemid((String)this.getFormHM().get("formulaitemid"));
		this.setFormulaitemlist((ArrayList)this.getFormHM().get("formulaitemlist"));
		this.setSort_fields((String[])this.getFormHM().get("sort_fields"));
		this.setSortlist((ArrayList)this.getFormHM().get("sortlist"));
		this.setConditions((String)this.getFormHM().get("conditions"));
		this.setItemname((String)this.getFormHM().get("itemname"));
		
		this.setStandardid((String)this.getFormHM().get("standardid"));
		this.setStandardlist((ArrayList)this.getFormHM().get("standardlist"));
		
		this.setTaxid((String)this.getFormHM().get("taxid"));
		this.setTaxlist((ArrayList)this.getFormHM().get("taxlist"));
		this.setFieldsetid((String) this.getFormHM().get("fieldsetid"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		if(this.getPagination()!=null)
			this.getFormHM().put("selitem",(ArrayList)this.getPagination().getSelectedList());
		this.getFormHM().put("formula",this.getFormula());
	}

	@Override
    public void reset(ActionMapping arg0, HttpServletRequest arg1) {
        super.reset(arg0, arg1);  
    }
	
	@Override
    public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1) {
		if("/gz/formula/viewformula".equals(arg0.getPath())&&arg1.getParameter("b_query")!=null){
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

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}


	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
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

	public ArrayList getItemlist() {
		return itemlist;
	}

	public void setItemlist(ArrayList itemlist) {
		this.itemlist = itemlist;
	}

	public String getSalaryid() {
		return salaryid;
	}

	public void setSalaryid(String salaryid) {
		this.salaryid = salaryid;
	}

	public String[] getCodesetid_arr() {
		return codesetid_arr;
	}

	public void setCodesetid_arr(String[] codesetid_arr) {
		this.codesetid_arr = codesetid_arr;
	}

	public String getRunflag() {
		return runflag;
	}

	public void setRunflag(String runflag) {
		this.runflag = runflag;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getFormulaitemid() {
		return formulaitemid;
	}

	public void setFormulaitemid(String formulaitemid) {
		this.formulaitemid = formulaitemid;
	}

	public ArrayList getFormulaitemlist() {
		return formulaitemlist;
	}

	public void setFormulaitemlist(ArrayList formulaitemlist) {
		this.formulaitemlist = formulaitemlist;
	}

	public String[] getSort_fields() {
		return sort_fields;
	}

	public void setSort_fields(String[] sort_fields) {
		this.sort_fields = sort_fields;
	}

	public ArrayList getSortlist() {
		return sortlist;
	}

	public void setSortlist(ArrayList sortlist) {
		this.sortlist = sortlist;
	}

	public String getConditions() {
		return conditions;
	}

	public void setConditions(String conditions) {
		this.conditions = conditions;
	}

	public String getItemname() {
		return itemname;
	}

	public void setItemname(String itemname) {
		this.itemname = itemname;
	}

	public String getStandardid() {
		return standardid;
	}

	public void setStandardid(String standardid) {
		this.standardid = standardid;
	}

	public ArrayList getStandardlist() {
		return standardlist;
	}

	public void setStandardlist(ArrayList standardlist) {
		this.standardlist = standardlist;
	}

	public String getTaxid() {
		return taxid;
	}

	public void setTaxid(String taxid) {
		this.taxid = taxid;
	}

	public ArrayList getTaxlist() {
		return taxlist;
	}

	public void setTaxlist(ArrayList taxlist) {
		this.taxlist = taxlist;
	}

	public String getFieldsetid() {
		return fieldsetid;
	}

	public void setFieldsetid(String fieldsetid) {
		this.fieldsetid = fieldsetid;
	}
}
