package com.hjsj.hrms.businessobject.gz.gz_budget.budget_execrate;

/**
 * 每年最近有效的预算，数据类
 * @author wangjh
 * 2013-3-14
 */

public class BudgetYearItem {
	private int year;
	private int budgetIdx;
	private int actualIdx;
	private String name;
	
	public boolean haveActualVo(){
		return actualIdx > -1;
	}

	public int getYear() {
		return year;
	}

	public String getYearText() {
		return Integer.toString(year);
	}
	public void setYear(int year) {
		this.year = year;
		this.name = year + "年";
	}

	public int getBudgetIdx() {
		return budgetIdx;
	}

	public void setBudgetIdx(int budgetIdx) {
		this.budgetIdx = budgetIdx;
	}

	public int getActualIdx() {
		return actualIdx;
	}

	public void setActualIdx(int actualIdx) {
		this.actualIdx = actualIdx;
	}

	public String getName() {
		return name;
	}

}
