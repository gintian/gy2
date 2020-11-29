package com.hjsj.hrms.actionform.gz.gz_budget.budget_rule.formula;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.action.FrameForm;
import com.hrms.struts.valueobject.PaginationForm;

import java.util.ArrayList;

public class BudgetFormulaForm extends FrameForm {

	private ArrayList list = new ArrayList();
	private ArrayList itemlist = new ArrayList();
	private ArrayList list1 = new ArrayList();
	private ArrayList list2 = new ArrayList();

	private ArrayList tablist = new ArrayList();
	private RecordVo vo = new RecordVo("gz_budget_formula");
	private PaginationForm budgetformulaForm = new PaginationForm();
	private ArrayList fieldsetlist = new ArrayList();
	private ArrayList fieldrowlist = new ArrayList();
	private ArrayList fieldcollist = new ArrayList();
	private ArrayList fieldcodelist = new ArrayList();
	private String itemid;
	private String itemid1;
	private String sql;
	private String where;
	private String column;
	private String orderby;
	private String formuladcrp;
	private String tab_id;
	private String tab_name;
	private String formula_id;
	private String itemdesc;
	private String itemdesc1;
	String count = "0";
	private String tj_type;
	private String btnreturnvisible;
	

	private String cond_value;
	private String cond_setid;
	private ArrayList cond_setlist = new ArrayList();
	private ArrayList cond_itemlist = new ArrayList();
	private ArrayList cond_codelist = new ArrayList();
	private String l1 = "";
	private String l2 = "";
	
	private String addmode = "";
	private String addcurformulaid = "";
	

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("selectedlist",
				(ArrayList) this.getBudgetformulaForm().getSelectedList());
		this.getFormHM().put("count", this.getCount());
		this.getFormHM().put("formula_id", this.getFormula_id());
		this.getFormHM().put("itemdesc", this.getItemdesc());
		this.getFormHM().put("itemdesc1", this.getItemdesc1());

		this.getFormHM().put("tj_type", this.getTj_type());
		this.getFormHM().put("btnreturnvisible", this.getBtnreturnvisible());
	}

	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.getBudgetformulaForm().setList(
				(ArrayList) this.getFormHM().get("list"));
		this.setList((ArrayList) this.getFormHM().get("list"));
		this.setTablist((ArrayList) this.getFormHM().get("tablist"));
		this.setVo((RecordVo) this.getFormHM().get("vo"));
		this.setSql((String) this.getFormHM().get("sql"));
		this.setWhere((String) this.getFormHM().get("where"));
		this.setColumn((String) this.getFormHM().get("column"));
		this.setOrderby((String) this.getFormHM().get("orderby"));
		this.setCount((String) this.getFormHM().get("count"));
		this.setFormuladcrp((String) this.getFormHM().get("formuladcrp"));
		this.setTab_id((String) this.getFormHM().get("tab_id"));
		this.setTab_name((String) this.getFormHM().get("tab_name"));
		this.setFormula_id((String) this.getFormHM().get("formula_id"));

		this.setCond_value((String) this.getFormHM().get("cond_value"));

		this.setCond_setid((String) this.getFormHM().get("cond_setid"));
		this.setCond_setlist((ArrayList) this.getFormHM().get("cond_setlist"));
		this.setList1((ArrayList) this.getFormHM().get("list1"));
		this.setList2((ArrayList) this.getFormHM().get("list2"));
		this.setItemid1((String) this.getFormHM().get("itemid1"));
		this.setItemid((String) this.getFormHM().get("itemid"));
		this.setItemdesc((String) this.getFormHM().get("itemdesc"));
		this.setTj_type((String) this.getFormHM().get("tj_type"));
		this.setBtnreturnvisible((String) this.getFormHM().get("btnreturnvisible"));
		this.setL1((String) this.getFormHM().get("l1"));
		this.setL2((String) this.getFormHM().get("l2"));
		
		this.setAddcurformulaid((String) this.getFormHM().get("addcurformulaid"));
		this.setAddmode((String) this.getFormHM().get("addmode"));
	}

	public String getBtnreturnvisible() {
		return btnreturnvisible;
	}

	public void setBtnreturnvisible(String btnreturnvisible) {
		this.btnreturnvisible = btnreturnvisible;
	}

	public ArrayList getList() {
		return list;
	}

	public void setList(ArrayList list) {
		this.list = list;
	}

	public RecordVo getVo() {
		return vo;
	}

	public void setVo(RecordVo vo) {
		this.vo = vo;
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

	public PaginationForm getBudgetformulaForm() {
		return budgetformulaForm;
	}

	public void setBudgetformulaForm(PaginationForm budgetformulaForm) {
		this.budgetformulaForm = budgetformulaForm;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getFormuladcrp() {
		return formuladcrp;
	}

	public void setFormuladcrp(String formuladcrp) {
		this.formuladcrp = formuladcrp;
	}

	public ArrayList getTablist() {
		return tablist;
	}

	public void setTablist(ArrayList tablist) {
		this.tablist = tablist;
	}

	public String getTab_id() {
		return tab_id;
	}

	public void setTab_id(String tab_id) {
		this.tab_id = tab_id;
	}

	public String getTab_name() {
		return tab_name;
	}

	public void setTab_name(String tab_name) {
		this.tab_name = tab_name;
	}

	public String getFormula_id() {
		return formula_id;
	}

	public void setFormula_id(String formula_id) {
		this.formula_id = formula_id;
	}

	public ArrayList getItemlist() {
		return itemlist;
	}

	public void setItemlist(ArrayList itemlist) {
		this.itemlist = itemlist;
	}

	public String getItemid() {
		return itemid;
	}

	public void setItemid(String itemid) {
		this.itemid = itemid;
	}

	public ArrayList getFieldsetlist() {
		return fieldsetlist;
	}

	public ArrayList getFieldrowlist() {
		return fieldrowlist;
	}

	public ArrayList getFieldcollist() {
		return fieldcollist;
	}

	public ArrayList getFieldcodelist() {
		return fieldcodelist;
	}

	public String getItemdesc() {
		return itemdesc;
	}

	public void setItemdesc(String itemdesc) {
		this.itemdesc = itemdesc;
	}

	public String getCond_value() {
		return cond_value;
	}

	public void setCond_value(String cond_value) {
		this.cond_value = cond_value;
	}

	public String getCond_setid() {
		return cond_setid;
	}

	public void setCond_setid(String cond_setid) {
		this.cond_setid = cond_setid;
	}

	public ArrayList getCond_setlist() {
		return cond_setlist;
	}

	public void setCond_setlist(ArrayList cond_setlist) {
		this.cond_setlist = cond_setlist;
	}

	public ArrayList getList1() {
		return list1;
	}

	public void setList1(ArrayList list1) {
		this.list1 = list1;
	}

	public ArrayList getList2() {
		return list2;
	}

	public void setList2(ArrayList list2) {
		this.list2 = list2;
	}

	public String getItemid1() {
		return itemid1;
	}

	public void setItemid1(String itemid1) {
		this.itemid1 = itemid1;
	}

	public String getItemdesc1() {
		return itemdesc1;
	}

	public void setItemdesc1(String itemdesc1) {
		this.itemdesc1 = itemdesc1;
	}

	public ArrayList getCond_itemlist() {
		return cond_itemlist;
	}

	public ArrayList getCond_codelist() {
		return cond_codelist;
	}

	public String getTj_type() {
		return tj_type;
	}

	public void setTj_type(String tj_type) {
		this.tj_type = tj_type;
	}

	public String getL1() {
		return l1;
	}

	public void setL1(String l1) {
		this.l1 = l1;
	}

	public String getL2() {
		return l2;
	}

	public void setL2(String l2) {
		this.l2 = l2;
	}

	public String getAddmode() {
		return addmode;
	}

	public void setAddmode(String addmode) {
		this.addmode = addmode;
	}

	public String getAddcurformulaid() {
		return addcurformulaid;
	}

	public void setAddcurformulaid(String addcurformulaid) {
		this.addcurformulaid = addcurformulaid;
	}

}
