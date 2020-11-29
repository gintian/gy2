package com.hjsj.hrms.transaction.general.deci.definition.statCutline;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class InitItemtypeListTrans extends IBusiness {

	public void execute() throws GeneralException {
		    this.getFormHM().put("typeid","");
	        this.getFormHM().put("name","");
	        this.getFormHM().put("status","");

	}

}
