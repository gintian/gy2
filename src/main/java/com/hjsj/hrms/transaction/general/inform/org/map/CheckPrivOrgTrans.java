package com.hjsj.hrms.transaction.general.inform.org.map;

import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class CheckPrivOrgTrans extends IBusiness{

	public void execute() throws GeneralException {
		
		String org_id = (String)this.getFormHM().get("org_id");
		if(!org_id.equals(new CheckPrivSafeBo(frameconn, userView).checkOrg(org_id, "4")))
			throw GeneralExceptionHandler.Handle(new Exception("您没有此机构访问权限！"));
	}
}
