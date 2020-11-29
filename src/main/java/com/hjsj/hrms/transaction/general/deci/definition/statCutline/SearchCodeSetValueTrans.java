package com.hjsj.hrms.transaction.general.deci.definition.statCutline;

import com.hjsj.hrms.businessobject.general.deci.definition.StatCutlineBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SearchCodeSetValueTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		String fieldItemID=(String)this.getFormHM().get("fielditemid");
		StatCutlineBo statCutlineBo=new StatCutlineBo(this.getFrameconn());
		String    CodeSetID=statCutlineBo.getCodeSetIDmByID(fieldItemID);
		this.getFormHM().put("codeSetid",CodeSetID);
	}

}
