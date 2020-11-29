package com.hjsj.hrms.utils.components.tablefactory.transaction;

import com.hjsj.hrms.utils.components.tablefactory.businessobject.TableFactoryBO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.util.HashMap;

public class UpdateAnalysePlanTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		
		DynaBean analyseParams = (DynaBean)this.getFormHM().get("updateParam");
		String planId = this.getFormHM().get("planId").toString();
		String planName = this.getFormHM().get("planName").toString();
		String subModuleId = this.getFormHM().get("subModuleId").toString();
        
        TableFactoryBO tfb = new TableFactoryBO(subModuleId, userView, this.frameconn);
        tfb.updatePlanConfig(analyseParams, planId,planName);
        HashMap planConfig = tfb.getAnalysePlanConfig(planId);
        this.formHM.clear();
        this.formHM.put("settingParam", planConfig);
	}

}
