package com.hjsj.hrms.actionform.gz.gz_accounting.piecerate;

import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

public class PieceRateFormulaForm extends FrameForm {
	/*
	 * 分页显示属性
	 */
	 private String sql;
	 private String where;
	 private String column;
	 private String orderby;
	 
	private String formula;//计算公式
	
	private String[] codesetid_arr;
	
	private String formulaid;
	
	private ArrayList itemlist = new ArrayList();
	private String fielditemid; 

	private String busiid;
	private String s0100;
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
	private String itemid; 
	
	/**计算公式列表*/
	private ArrayList formulalist=new ArrayList();
	/**分页管理器*/
    private PaginationForm formulalistform=new PaginationForm();

	@Override
    public void outPutFormHM() {
		this.getFormulalistform().setList((ArrayList)this.getFormHM().get("formulalist"));
		this.setSql((String) this.getFormHM().get("sql"));
		this.setWhere((String) this.getFormHM().get("where"));
		this.setColumn((String) this.getFormHM().get("column"));
		this.setOrderby((String)this.getFormHM().get("orderby"));
		this.setFormula((String)this.getFormHM().get("formula"));
		this.setItemid((String)this.getFormHM().get("itemid"));
		this.setFielditemid((String)this.getFormHM().get("fielditemid"));
		this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
		this.setBusiid((String)this.getFormHM().get("busiid"));
		this.setS0100((String)this.getFormHM().get("s0100"));
		this.setBusiid((String)this.getFormHM().get("busiid"));
		this.setFormulaid((String)this.getFormHM().get("formulaid"));

		this.setCodesetid_arr((String[])this.getFormHM().get("codesetid_arr"));
		this.setFormulaitemid((String)this.getFormHM().get("formulaitemid"));
		this.setFormulaitemlist((ArrayList)this.getFormHM().get("formulaitemlist"));
		this.setSort_fields((String[])this.getFormHM().get("sort_fields"));
		this.setSortlist((ArrayList)this.getFormHM().get("sortlist"));
		this.setConditions((String)this.getFormHM().get("conditions"));
		
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

	public String[] getCodesetid_arr() {
		return codesetid_arr;
	}

	public void setCodesetid_arr(String[] codesetid_arr) {
		this.codesetid_arr = codesetid_arr;
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


	public String getBusiid() {
		return busiid;
	}

	public void setBusiid(String busiid) {
		this.busiid = busiid;
	}

	public String getFormulaid() {
		return formulaid;
	}

	public void setFormulaid(String formulaid) {
		this.formulaid = formulaid;
	}

	public String getFielditemid() {
		return fielditemid;
	}

	public void setFielditemid(String fielditemid) {
		this.fielditemid = fielditemid;
	}

	public ArrayList getFormulalist() {
		return formulalist;
	}

	public void setFormulalist(ArrayList formulalist) {
		this.formulalist = formulalist;
	}

	public PaginationForm getFormulalistform() {
		return formulalistform;
	}

	public void setFormulalistform(PaginationForm formulalistform) {
		this.formulalistform = formulalistform;
	}

	public String getS0100() {
		return s0100;
	}

	public void setS0100(String s0100) {
		this.s0100 = s0100;
	}


}