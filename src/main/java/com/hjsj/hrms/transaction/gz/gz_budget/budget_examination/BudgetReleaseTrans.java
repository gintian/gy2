package com.hjsj.hrms.transaction.gz.gz_budget.budget_examination;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_examination.BudgetExamBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;

public class BudgetReleaseTrans extends IBusiness {


	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try
		{
			String tab_id = "";
			ContentDAO dao=new ContentDAO(this.frameconn);
			String budget_id=(String)this.getFormHM().get("budget_id");  
			String flag=(String)this.getFormHM().get("flag"); 
			BudgetExamBo examBo=new BudgetExamBo(this.getFrameconn(),tab_id,this.userView);
			RowSet rowSet=dao.search("select extAttr from gz_budget_index where budget_id="+budget_id+"");
			while(rowSet.next())
			{
				String exAttr=Sql_switcher.readMemo(rowSet,"extAttr");
				String[] temps=exAttr.split(",");
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].length()>0)
					{					
						examBo.isApproval(budget_id, temps[i]);
					}
				}
			} 
			this.getFormHM().put("flag", flag);

		}		
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
