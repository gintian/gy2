package com.hjsj.hrms.transaction.gz.gz_accounting.piecerate;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SearchHandSelObjTrans extends IBusiness {

	public void execute() throws GeneralException {

		
		
		
		this.getFormHM().put("planName", "");
		this.getFormHM().put("object_type", "");
	}

}
