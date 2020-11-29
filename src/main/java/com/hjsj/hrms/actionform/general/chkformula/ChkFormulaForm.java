package com.hjsj.hrms.actionform.general.chkformula;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class ChkFormulaForm extends FrameForm {
	/*
	 * 分页显示属性
	 */
	private String sql;
	private String where;
	private String column;
	private String orderby;
	private String tabid="";
	private String flag=""; //0.业务模板 1.工资类别
	private String formula; //公式内容
	 
	private String[] codesetid_arr;
		
	private String itemid;
	private ArrayList itemlist = new ArrayList();
	private String conditions; //计算条件
	private String tipinfor; //提示信息
	private String chkid="";
	private String name="";
	private String information="";
	private String[] sort_fields; 
	 private ArrayList sortlist = new ArrayList(); 
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setSql((String) this.getFormHM().get("sql"));
		this.setWhere((String) this.getFormHM().get("where"));
		this.setColumn((String) this.getFormHM().get("column"));
		this.setOrderby((String)this.getFormHM().get("orderby"));
		this.setFormula((String)this.getFormHM().get("formula"));
		this.setTabid((String)this.getFormHM().get("tabid"));
		this.setFlag((String)this.getFormHM().get("flag"));
		this.setCodesetid_arr((String[])this.getFormHM().get("codesetid_arr"));
		this.setItemid((String)this.getFormHM().get("itemid"));
		this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
		this.setConditions((String)this.getFormHM().get("conditions"));
		this.setTipinfor((String)this.getFormHM().get("tipinfor"));
		this.setChkid((String)this.getFormHM().get("chkid"));
		this.setName((String)this.getFormHM().get("name"));
		this.setInformation((String)this.getFormHM().get("information"));
		this.setSort_fields((String[])this.getFormHM().get("sort_fields"));
		this.setSortlist((ArrayList)this.getFormHM().get("sortlist"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub

	}

	public String[] getCodesetid_arr() {
		return codesetid_arr;
	}

	public void setCodesetid_arr(String[] codesetid_arr) {
		this.codesetid_arr = codesetid_arr;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getConditions() {
		return conditions;
	}

	public void setConditions(String conditions) {
		this.conditions = conditions;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
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

	public ArrayList getItemlist() {
		return itemlist;
	}

	public void setItemlist(ArrayList itemlist) {
		this.itemlist = itemlist;
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

	public String getTabid() {
		return tabid;
	}

	public void setTabid(String tabid) {
		this.tabid = tabid;
	}

	public String getTipinfor() {
		return tipinfor;
	}

	public void setTipinfor(String tipinfor) {
		this.tipinfor = tipinfor;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public String getChkid() {
		return chkid;
	}

	public void setChkid(String chkid) {
		this.chkid = chkid;
	}

	public String getInformation() {
		return information;
	}

	public void setInformation(String information) {
		this.information = information;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

}
