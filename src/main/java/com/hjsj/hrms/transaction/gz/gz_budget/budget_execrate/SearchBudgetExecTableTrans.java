package com.hjsj.hrms.transaction.gz.gz_budget.budget_execrate;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_execrate.BudgetExecrateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchBudgetExecTableTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			hm.remove("tab_id");			
			
			String tab_id="";  
			String b0110="";
			String budget_id=(String)this.getFormHM().get("budget_id"); 
			String budgetYear=(String)this.getFormHM().get("budgetYear"); 
			String budgetMonth=(String)this.getFormHM().get("budgetMonth"); 


			if(hm.get("a_code")!=null) {
				b0110=((String)hm.get("a_code")).substring(2);
				this.getFormHM().put("b0110", "b0110");
				
			}
			tab_id=(String)this.getFormHM().get("tab_id");
			if (tab_id ==null) tab_id="";	
			if ("".equals(tab_id)||("init".equals(tab_id))){
				
				throw GeneralExceptionHandler.Handle(new Exception("请选择预算表！"));	
			}
			
			this.userView.getHm().put("execrate_budgetyear", budgetYear);
			this.userView.getHm().put("execrate_budgetmonth", budgetMonth);
			this.userView.getHm().put("execrate_b0110", b0110);
			this.userView.getHm().put("execrate_tab_id", tab_id);
				
			BudgetExecrateBo ExecBO= new BudgetExecrateBo(this.frameconn,this.userView);
			ArrayList fieldList=ExecBO.getExecrateFieldList(b0110, Integer.parseInt(budgetYear), Integer.parseInt(tab_id));
			String sql=ExecBO.getExecrateSQL(b0110, Integer.parseInt(budgetYear), Integer.parseInt(budgetMonth), Integer.parseInt(tab_id));		
			this.getFormHM().put("tab_name", ExecBO.getRateTablename());			
			
			this.getFormHM().put("fieldList",fieldList);
			this.getFormHM().put("sql",sql);

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}
