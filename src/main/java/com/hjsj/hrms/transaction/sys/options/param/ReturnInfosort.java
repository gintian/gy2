package com.hjsj.hrms.transaction.sys.options.param;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class ReturnInfosort extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		this.getFormHM().put("tag","set_b");
	}

}
