
package com.hjsj.hrms.transaction.report.org_maintenance;


import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SerachBackDateTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String backdate = (String)hm.get("backdate");
		this.getFormHM().put("backdate", backdate);
	}

}
