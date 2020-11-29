package com.hjsj.hrms.utils.components.tablefactory.transaction;

import com.hjsj.hrms.utils.components.tablefactory.businessobject.TableFactoryBO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.util.HashMap;

public class SaveAnalysePlanTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		
		String subModuleId = this.getFormHM().get("subModuleId").toString();
		DynaBean analyseParams = (DynaBean)this.getFormHM().get("settingParam");
	    
	    String planName = this.getFormHM().get("planName").toString();
	    int is_share = Integer.parseInt(this.getFormHM().get("isShare").toString());
	    
	    this.formHM.clear();
	    TableFactoryBO tfb = new TableFactoryBO(subModuleId, userView, this.frameconn);
	    String planId = tfb.saveAnalysePlan(planName,is_share,analyseParams);
	    planId = Integer.parseInt(planId)+"";
	    //if(planId==null){
	    //	this.formHM.put("result",Boolean.FALSE);
	    //}
	    HashMap planConfig = tfb.getAnalysePlanConfig(planId);
	    this.formHM.put("result",Boolean.TRUE);
	    this.formHM.put("planConfig",planConfig);
	   
	    
	}
    
}
