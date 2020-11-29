package com.hjsj.hrms.transaction.sys.options.param;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SysParamTrans extends IBusiness {

	public void execute() throws GeneralException {	
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String edition = (String)hm.get("edition");
		if(edition!=null&& "4".equalsIgnoreCase(edition)){
			this.getFormHM().put("edition","4");
		}else{
			this.getFormHM().put("edition","5");
		}
	}

}
