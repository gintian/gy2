package com.hjsj.hrms.transaction.gz.gz_budget.budget_allocation;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_allocation.BudgetAllocBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/** 预算分发
 * Create Time: 2012.10.26
 * @author genglz
 *
 */
public class DistributeBudgetTrans extends IBusiness {

	public void execute() throws GeneralException {
		try{
			String budgetid = (String)this.getFormHM().get("budgetid");
			String unitid = (String)this.getFormHM().get("unitid");
			String flag = (String)this.getFormHM().get("flag");
			BudgetAllocBo bo=new BudgetAllocBo(this.getFrameconn(),this.userView);
			if("checkonly".equalsIgnoreCase(flag))
			{
				boolean candist=bo.checkCanDistribute(unitid, Integer.valueOf(budgetid));
				this.getFormHM().clear();
				this.getFormHM().put("flag", candist?"1":"0");
				this.getFormHM().put("msg", bo.getErrorMessage());
			}
			else 
			{
				bo.distributeBudget(unitid, Integer.valueOf(budgetid));
				this.getFormHM().clear();
				this.getFormHM().put("flag", "1");
			}

		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
