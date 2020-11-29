package com.hjsj.hrms.utils.components.tablefactory.transaction;

import com.hjsj.hrms.utils.components.tablefactory.businessobject.TableFactoryBO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class DeleteAnalysePlanTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
        String planId = this.getFormHM().get("planId").toString();
        TableFactoryBO tfb = new TableFactoryBO(null,null,this.frameconn);
        tfb.deleteAnalysePlan(planId);
        
	}

}
