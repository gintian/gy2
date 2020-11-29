package com.hjsj.hrms.transaction.sys.bos.func;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class AddFuncMainTrans extends IBusiness {

	public void execute() throws GeneralException {
			HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String parentid=(String)hm.get("functionid");
		if("root".equals(parentid)){
			parentid="-1";
		}
			this.getFormHM().put("parentid", parentid);
			
	}


}
