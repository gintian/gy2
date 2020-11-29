package com.hjsj.hrms.transaction.gz.gz_budget.budget_allocation;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_allocation.BudgetAllocBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 删除预算单位交易类
 * Create Time: 2012.10.29
 * @author genglz
 *
 */
public class DelBudgetUnitTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		try{
			String budgetid = (String)this.getFormHM().get("budgetid");
			String units = (String)this.getFormHM().get("units");
			String flag = (String)this.getFormHM().get("flag");
			BudgetAllocBo bo=new BudgetAllocBo(this.getFrameconn(),this.userView);
			if("checkonly".equalsIgnoreCase(flag))
			{
				boolean can=bo.checkCanDeleteBudgetUnit(units, Integer.valueOf(budgetid));
				this.getFormHM().clear();
				this.getFormHM().put("flag", can?"1":"0");
				this.getFormHM().put("msg", bo.getErrorMessage());
			}
			else 
			{
				bo.deleteBudgetUnit(units, Integer.valueOf(budgetid));
				this.getFormHM().clear();
				this.getFormHM().put("flag", "1");
			}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}