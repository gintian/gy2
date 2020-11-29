package com.hjsj.hrms.transaction.gz.gz_budget.budget_allocation;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_allocation.BudgetAllocBo;
import com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.options.BudgetSysBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class SetBudgetParamsTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String b0110="";
			if(hm.get("a_code")!=null)
			{
				b0110=((String)hm.get("a_code"));
			}
			
			BudgetAllocBo allocBo=new BudgetAllocBo(this.getFrameconn(),this.userView);
			BudgetSysBo sysbo=new BudgetSysBo(this.getFrameconn(),this.userView);
			String tabName = ((String)sysbo.getSysValueMap().get("ysparam_set")).toLowerCase();
			Integer budget_id = allocBo.getCurrentBudgetId(); 
			
			ArrayList fieldList=allocBo.getFieldList1(b0110);
			this.getFormHM().put("tab_name", tabName);
			this.getFormHM().put("b0110", b0110);
			this.getFormHM().put("fieldList",fieldList);
			this.getFormHM().put("budget_id", budget_id.toString());
			
			LazyDynaBean bean = allocBo.getAppealStatus(b0110,budget_id.toString());
			LazyDynaBean bean1 = allocBo.getAppealStatus_1(b0110,budget_id.toString());
			String status =(String) bean.get("status");
			if("04".equals(status)){
				status=(String) bean1.get("status1");
			}
			if(status!=null&&("01".equals(status)|| "07".equals(status))){
				this.getFormHM().put("zhuangtai", "write");
			}else{
				this.getFormHM().put("zhuangtai", "readonly");
			}	
		
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		
	}
	


}
