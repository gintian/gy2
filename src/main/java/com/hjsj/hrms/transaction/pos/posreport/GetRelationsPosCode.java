package com.hjsj.hrms.transaction.pos.posreport;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class GetRelationsPosCode extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
         HashMap hs = (HashMap)this.formHM.get("requestPamaHM");
         String code = (String)hs.get("code");
         hs.remove("code");
         this.getFormHM().put("code", code);
	}

}
