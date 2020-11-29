package com.hjsj.hrms.transaction.gz.gz_budget.budget_allocation;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_allocation.BudgetAllocBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/** 预算退回
 * Create Time: 2012.10.27
 * @author genglz
 *
 */
public class RejectBudgetTrans extends IBusiness {

	public void execute() throws GeneralException {
		try{
			String budgetid = (String)this.getFormHM().get("budgetid");
			String unitid = (String)this.getFormHM().get("unitid");
			String flag = (String)this.getFormHM().get("flag");
			BudgetAllocBo bo=new BudgetAllocBo(this.getFrameconn(),this.userView);
			if("checkonly".equalsIgnoreCase(flag))
			{
				boolean can=bo.checkCanReject(unitid, Integer.valueOf(budgetid));
				this.getFormHM().put("flag", can?"1":"0");
				this.getFormHM().put("msg", bo.getErrorMessage());
			}
			else 
			{
				bo.rejectBudget(unitid, Integer.valueOf(budgetid));
				this.getFormHM().put("flag", "1");
			}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}	
	}

}
