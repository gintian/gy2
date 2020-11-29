package com.hjsj.hrms.transaction.gz.gz_budget.budget_examination;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_examination.BudgetExamBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SummaryTrans extends IBusiness {


	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String budget_id=(String)this.getFormHM().get("budget_id");  
			String tab_id="";  
			String b0110="";
			b0110=(String) this.getFormHM().get("b0110");	
	
			if(this.getFormHM().get("tab_id")!=null)
			{
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				this.frowset=dao.search("select min(tab_id) from gz_budget_tab where (tab_type=4 or tab_type=3) and validFlag=1");
				if(this.frowset.next())
					tab_id=this.frowset.getString(1);
				else
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz.budget.budget_examination.definePlan")+"!"));
			}
			else if(hm.get("tab_id")!=null)
			{
				tab_id=(String)hm.get("tab_id");
			}
			else
				tab_id=(String)this.getFormHM().get("tab_id");							 
			
			BudgetExamBo examBo=new BudgetExamBo(this.getFrameconn(),tab_id,this.userView);
			
			ArrayList fieldList=examBo.getFieldList(b0110,budget_id,tab_id);
			String sql=examBo.getSummarySql(b0110,budget_id,tab_id,fieldList);
		 			
			
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
