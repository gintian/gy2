package com.hjsj.hrms.transaction.sys.options.param.operation;

import com.hjsj.hrms.businessobject.sys.options.param.SubsysOperation;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * 
 *<p>Title:ReworkSortTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Sep 15, 2008:6:46:30 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class ReworkSortTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		SubsysOperation so = new SubsysOperation(this.frameconn,this.userView);
		String operationid = (String)this.getFormHM().get("operationid");
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String reworkname = (String)hm.get("reworkname");
		String reworkoldname = (String)hm.get("reworkoldname");
		hm.remove("reworkname");
		hm.remove("reworkoldname");
		String errmes = so.updateTag(operationid,SafeCode.decode(reworkoldname),SafeCode.decode(reworkname));
		this.getFormHM().put("errmes",errmes);
	}

}
