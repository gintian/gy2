package com.hjsj.hrms.transaction.gz.gz_budget.budget_examination;
import com.hjsj.hrms.businessobject.gz.gz_budget.BudgetServiceBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class BudgetDataSyncTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
		
			String _budgetIdx=(String)this.getFormHM().get("budgetIdx");
	 		BudgetServiceBo bo=new BudgetServiceBo(this.getFrameconn(),Integer.parseInt(_budgetIdx));
	 		String info=bo.SendMessage();
	 		this.getFormHM().put("info",SafeCode.encode(info)); 
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
