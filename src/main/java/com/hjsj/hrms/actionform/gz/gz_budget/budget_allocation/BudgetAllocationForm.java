package com.hjsj.hrms.actionform.gz.gz_budget.budget_allocation;

import com.hrms.struts.action.FrameForm;

import java.util.ArrayList;

/**
 * 工资预算分配Form类
 * @author genglz
 *
 */
public class BudgetAllocationForm extends FrameForm {
	String budget_id="";   //预算索引号
	String currentBudgetDesc="";  //当前预算描述
	String b0110="";       //预算单位
	String b0110_desc="";
	String budgetStatus="";  //预算状态；
	String budgetStatusDesc=""; //预算状态描述
	String tab_name="";  // 预算总额子集
	String topUnitId="";  // 当前用户可管理的顶级机构
	String selectedUnit="";  // 当前选中单位编码
	
	ArrayList fieldList=new ArrayList();
	String sql="";  // 预算总额子集查询sql语句
	
	//************************************************
	private ArrayList yearNumlist;
	private ArrayList budgetTypelist;
	private ArrayList firstMonthlist;
	private String yearnum;
	private String budgettype;
	private String firstmonth;
	private String bb203;
	private String tishi1;
	private String tishi2;
	private String checkBudgetType;
	private String amountDesc;

	public String getAmountDesc() {
		return amountDesc;
	}

	public void setAmountDesc(String amountDesc) {
		this.amountDesc = amountDesc;
	}

	public ArrayList getYearNumlist() {
		return yearNumlist;
	}

	public void setYearNumlist(ArrayList yearNumlist) {
		this.yearNumlist = yearNumlist;
	}

	public ArrayList getBudgetTypelist() {
		return budgetTypelist;
	}

	public void setBudgetTypelist(ArrayList budgetTypelist) {
		this.budgetTypelist = budgetTypelist;
	}

	public ArrayList getFirstMonthlist() {
		return firstMonthlist;
	}

	public void setFirstMonthlist(ArrayList firstMonthlist) {
		this.firstMonthlist = firstMonthlist;
	}

	public String getYearnum() {
		return yearnum;
	}

	public void setYearnum(String yearnum) {
		this.yearnum = yearnum;
	}

	public String getBudgettype() {
		return budgettype;
	}

	public void setBudgettype(String budgettype) {
		this.budgettype = budgettype;
	}

	public String getFirstmonth() {
		return firstmonth;
	}

	public void setFirstmonth(String firstmonth) {
		this.firstmonth = firstmonth;
	}

	public String getBb203() {
		return bb203;
	}

	public void setBb203(String bb203) {
		this.bb203 = bb203;
	}

	@Override
    public void inPutTransHM() {
		// TODO Auto-generated method stub
		this.getFormHM().put("yearNumlist", (ArrayList)this.getYearNumlist());
		this.getFormHM().put("budgetTypelist", (ArrayList)this.getBudgetTypelist());
		this.getFormHM().put("firstMonthlist", (ArrayList)this.getFirstMonthlist());
		this.getFormHM().put("yearnum", (String)this.getYearnum());
		this.getFormHM().put("budgettype", (String)this.getBudgettype());
		this.getFormHM().put("firstmonth", (String)this.getFirstmonth());
		this.getFormHM().put("bb203", (String)this.getBb203());
		this.getFormHM().put("budget_id", (String)this.getBudget_id());
		this.getFormHM().put("checkBudgetType", (String)this.getCheckBudgetType());
		
		
		
		
	}

	@Override
    public void outPutFormHM() {
		this.setBudget_id((String)this.getFormHM().get("budget_id"));
		this.setCurrentBudgetDesc((String)this.getFormHM().get("currentBudgetDesc"));
		this.setB0110((String)this.getFormHM().get("b0110"));
		this.setB0110_desc((String)this.getFormHM().get("b0110_desc"));
		this.setBudgetStatusDesc((String)this.getFormHM().get("budgetStatusDesc"));
		this.setBudgetStatus((String)this.getFormHM().get("budgetStatus"));
		this.setFieldList((ArrayList)this.getFormHM().get("fieldList"));
		this.setSql((String)this.getFormHM().get("sql"));
		this.setTab_name((String)this.getFormHM().get("tab_name"));
		this.setTopUnitId((String)this.getFormHM().get("topUnitId"));
		//******************
		this.setYearNumlist((ArrayList)this.getFormHM().get("yearNumlist"));
		this.setBudgetTypelist((ArrayList)this.getFormHM().get("budgetTypelist"));
		this.setFirstMonthlist((ArrayList)this.getFormHM().get("firstMonthlist"));
		
		this.setYearnum((String)this.getFormHM().get("yearnum"));
		this.setBudgettype((String)this.getFormHM().get("budgettype"));
		this.setFirstmonth((String)this.getFormHM().get("firstmonth"));
		
		this.setTishi1((String)this.getFormHM().get("tishi1"));
		this.setTishi2((String)this.getFormHM().get("tishi2"));
		this.setBb203((String)this.getFormHM().get("bb203"));
		this.setSelectedUnit((String)this.getFormHM().get("selectedUnit"));
		this.setCheckBudgetType((String)this.getFormHM().get("checkBudgetType"));
		
		this.setAmountDesc((String)this.getFormHM().get("amountDesc"));
		
	}
	
	public String getBudget_id() {
		return budget_id;
	}

	public void setBudget_id(String budget_id) {
		this.budget_id = budget_id;
	}

	public String getCurrentBudgetDesc() {
		return currentBudgetDesc;
	}

	public void setCurrentBudgetDesc(String currentBudgetDesc) {
		this.currentBudgetDesc = currentBudgetDesc;
	}

	public String getB0110() {
		return b0110;
	}

	public void setB0110(String b0110) {
		this.b0110 = b0110;
	}

	public String getBudgetStatus() {
		return budgetStatus;
	}

	public void setBudgetStatus(String budgetStatus) {
		this.budgetStatus = budgetStatus;
	}

	public String getBudgetStatusDesc() {
		return budgetStatusDesc;
	}

	public void setBudgetStatusDesc(String budgetStatusDesc) {
		this.budgetStatusDesc = budgetStatusDesc;
	}

	public ArrayList getFieldList() {
		return fieldList;
	}

	public void setFieldList(ArrayList fieldList) {
		this.fieldList = fieldList;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public String getTab_name() {
		return tab_name;
	}

	public void setTab_name(String tab_name) {
		this.tab_name = tab_name;
	}

	public String getTopUnitId() {
		return topUnitId;
	}

	public void setTopUnitId(String topUnitId) {
		this.topUnitId = topUnitId;
	}

	public String getB0110_desc() {
		return b0110_desc;
	}

	public void setB0110_desc(String b0110_desc) {
		this.b0110_desc = b0110_desc;
	}

	public String getTishi1() {
		return tishi1;
	}

	public void setTishi1(String tishi1) {
		this.tishi1 = tishi1;
	}

	public String getTishi2() {
		return tishi2;
	}

	public void setTishi2(String tishi2) {
		this.tishi2 = tishi2;
	}
	public String getSelectedUnit() {
		return selectedUnit;
	}

	public void setSelectedUnit(String selectedUnit) {
		this.selectedUnit = selectedUnit;
	}

	public String getCheckBudgetType() {
		return checkBudgetType;
	}

	public void setCheckBudgetType(String checkBudgetType) {
		this.checkBudgetType = checkBudgetType;
	}
	
}
