package com.hjsj.hrms.transaction.hire.demandPlan;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class GoBackTrans extends IBusiness {

	public void execute() throws GeneralException {
		 
         	   
	            String flik=(String)this.getFormHM().get("flag");        //dengcan  用于返回
	            if(flik!=null)
	            {
	            	
	            	this.getUserView().getHm().put("flik",flik);
	            	this.getFormHM().remove("flik");
	            }
	            
        
	}

}
