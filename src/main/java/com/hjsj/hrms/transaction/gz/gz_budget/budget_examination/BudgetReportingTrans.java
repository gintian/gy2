package com.hjsj.hrms.transaction.gz.gz_budget.budget_examination;


import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class BudgetReportingTrans extends IBusiness {


	public void execute() throws GeneralException {
		// TODO Auto-generated method stub

		try
		{
			//既然在BudgetExamTrans类处理了，就不要再用这个类了，只是一个判断，屏蔽 wangrd
//			String budget_id=(String)this.getFormHM().get("budget_id");  
//			String tab_id="";  
//			String b0110="";
//			b0110=(String) this.getFormHM().get("b0110");
//			BudgetExamBo examBo=new BudgetExamBo(this.getFrameconn(),tab_id,this.userView);
//			boolean isTopUn=examBo.isTopUn(b0110); //是不是顶层节点
//			if(isTopUn)
//			{
//				throw GeneralExceptionHandler.Handle(new Exception("顶级单位，无需上报"));
//			}
//			else{
//				examBo.budgetReporting(budget_id, b0110);
//				this.getFormHM().put("flag", "4");
//			}
		}		
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
}


