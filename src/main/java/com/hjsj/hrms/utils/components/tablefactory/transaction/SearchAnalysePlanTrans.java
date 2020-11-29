package com.hjsj.hrms.utils.components.tablefactory.transaction;

import com.hjsj.hrms.utils.components.tablefactory.businessobject.TableFactoryBO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchAnalysePlanTrans extends IBusiness{

	public void execute() throws GeneralException {
		String subModuleId = this.formHM.get("subModuleId").toString();
		String searchType = this.formHM.get("searchType").toString();
		
		TableFactoryBO tfb = new TableFactoryBO(subModuleId, userView, frameconn);
		if("plan".equals(searchType)){
			ArrayList plan = tfb.searchAnalysePlan();
			this.formHM.clear();
			this.formHM.put("plans", plan);
		}else{
			String planId = this.formHM.get("planId").toString();
			HashMap settingParam = tfb.getAnalysePlanConfig(planId);
			this.formHM.clear();
			this.formHM.put("settingParam", settingParam);
		}
		
		
		
	}

	
}
