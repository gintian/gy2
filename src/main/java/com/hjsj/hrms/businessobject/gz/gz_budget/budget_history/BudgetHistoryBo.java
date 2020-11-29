package com.hjsj.hrms.businessobject.gz.gz_budget.budget_history;

import com.hjsj.hrms.utils.ResourceFactory;

public class BudgetHistoryBo {
	public String getInfoStr(String budgetType){
		String currentYs="";
		try{

				if("1".equals(budgetType))
					currentYs+=ResourceFactory.getProperty("gz.budget.budgeting.yearc");
				else if("2".equals(budgetType))
					currentYs+=ResourceFactory.getProperty("gz.budget.budgeting.yearz");
				else if("3".equals(budgetType))
					currentYs+=ResourceFactory.getProperty("gz.budget.budgeting.tbtz");
				else if("4".equals(budgetType))
					currentYs+=ResourceFactory.getProperty("gz.budget.budgeting.sz");
				else if("5".equals(budgetType))
					currentYs+=ResourceFactory.getProperty("gz.budget.budgeting.wbsf");
		}catch(Exception e){
			e.printStackTrace();
		}
		return currentYs;
	}
}
