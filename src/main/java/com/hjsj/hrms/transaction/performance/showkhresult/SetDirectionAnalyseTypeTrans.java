package com.hjsj.hrms.transaction.performance.showkhresult;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SetDirectionAnalyseTypeTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		this.getFormHM().put("showType",(String)this.getFormHM().get("showType"));
		this.getFormHM().put("template_id",(String)this.getFormHM().get("template_id"));
	}

}
