package com.hjsj.hrms.actionform.general.salarychange;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

public class SetFormulaForm extends FrameForm {
	private String tableid; //模版id
	private ArrayList itemlist = new ArrayList(); //子标集合
	private String itemid;  //指标id
	private String formulatemp; //生成模版表
	private String formula; //公式
	private String cfactor; //计算条件
	private String id; //公式id
	private String item_arr; //指标集以","隔开,
	private String codesetid_arr; //代码集合
	private String itemtable; //计算项目表
	private String item; //计算项目
	private String itemids;  //指标id
	private String[] sort_fields;  //指标数组
	private ArrayList sortlist = new ArrayList();
	private String flag;  //判断公式是否是新增或修改
	private String chz;  //公式名称
	private String affteritem_arr; //变化后子集
	private String chz_arr; //项目中文名
	
	@Override
    public void outPutFormHM() {
		// TODO Auto-generated method stub
		this.setItemlist((ArrayList)this.getFormHM().get("itemlist"));
		this.setTableid((String)this.getFormHM().get("tableid"));
		this.setItemid((String)this.getFormHM().get("itemid"));
		this.setFormulatemp((String)this.getFormHM().get("formulatemp"));
		this.setFormula((String)this.getFormHM().get("formula"));
		this.setCfactor((String)this.getFormHM().get("cfactor"));
		this.setId((String)this.getFormHM().get("id"));
		this.setItem_arr((String)this.getFormHM().get("item_arr"));
		this.setItemtable((String)this.getFormHM().get("itemtable"));
		this.setItem((String)this.getFormHM().get("item"));
		this.setItemids((String)this.getFormHM().get("itemids"));
		this.setSort_fields((String[])this.getFormHM().get("sort_fields"));
		this.setSortlist((ArrayList)this.getFormHM().get("sortlist"));
		this.setFlag((String)this.getFormHM().get("flag"));
		this.setChz((String)this.getFormHM().get("chz"));
		this.setAffteritem_arr((String)this.getFormHM().get("affteritem_arr"));
		this.setChz_arr((String)this.getFormHM().get("chz_arr"));
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("item",this.getItem());
	}

	public String getCfactor() {
		return cfactor;
	}

	public void setCfactor(String cfactor) {
		this.cfactor = cfactor;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getFormulatemp() {
		return formulatemp;
	}

	public void setFormulatemp(String formulatemp) {
		this.formulatemp = formulatemp;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getTableid() {
		return tableid;
	}

	public void setTableid(String tableid) {
		this.tableid = tableid;
	}

	public String getItem_arr() {
		return item_arr;
	}

	public void setItem_arr(String item_arr) {
		this.item_arr = item_arr;
	}

	public String getCodesetid_arr() {
		return codesetid_arr;
	}

	public void setCodesetid_arr(String codesetid_arr) {
		this.codesetid_arr = codesetid_arr;
	}

	public String getItemtable() {
		return itemtable;
	}

	public void setItemtable(String itemtable) {
		this.itemtable = itemtable;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getItemids() {
		return itemids;
	}

	public void setItemids(String itemids) {
		this.itemids = itemids;
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

	public String getChz() {
		return chz;
	}

	public void setChz(String chz) {
		this.chz = chz;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getAffteritem_arr() {
		return affteritem_arr;
	}

	public void setAffteritem_arr(String affteritem_arr) {
		this.affteritem_arr = affteritem_arr;
	}

	public String getChz_arr() {
		return chz_arr;
	}

	public void setChz_arr(String chz_arr) {
		this.chz_arr = chz_arr;
	}
}
