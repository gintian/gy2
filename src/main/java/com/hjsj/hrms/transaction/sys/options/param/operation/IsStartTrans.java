package com.hjsj.hrms.transaction.sys.options.param.operation;

import com.hjsj.hrms.businessobject.sys.options.param.SubsysOperation;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 *<p>Title:IsStartTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Sep 15, 2008:7:45:32 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class IsStartTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String operationid = (String)this.getFormHM().get("operationid");
		String checked = (String)this.getFormHM().get("checked");
		SubsysOperation so = new SubsysOperation(this.frameconn,this.userView);
		so.checkFlag(operationid,checked);
	}

}
