package com.hjsj.hrms.transaction.gz.gz_budget.budget_allocation;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_allocation.BudgetAllocBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class CheckBudgetParamsTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try{
			String budgetid = (String)this.getFormHM().get("budgetid");
			String unitid = (String)this.getFormHM().get("unitid");
			String flag = (String)this.getFormHM().get("flag");
			BudgetAllocBo bo=new BudgetAllocBo(this.getFrameconn(),this.userView);
			if("checkonly".equalsIgnoreCase(flag))
			{
				boolean candist=bo.checkBudgetParams(unitid, Integer.valueOf(budgetid));
				this.getFormHM().clear();
				this.getFormHM().put("flag", candist?"1":"0");
				this.getFormHM().put("msg", bo.getErrorMessage());
			}


		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	
	}

}
