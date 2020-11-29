package com.hjsj.hrms.transaction.hire.employActualize.personnelFilter;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SetPersonnelIDTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String id=(String)hm.get("id");
		this.getFormHM().put("a0100",id.split("/")[0]);
	}

}
