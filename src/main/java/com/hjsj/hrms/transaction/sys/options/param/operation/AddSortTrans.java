package com.hjsj.hrms.transaction.sys.options.param.operation;

import com.hjsj.hrms.businessobject.sys.options.param.SubsysOperation;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 *<p>Title:AddSortTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Sep 15, 2008:3:55:12 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class AddSortTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String sortname = (String)this.getFormHM().get("sortname");
		String operationid = (String)this.getFormHM().get("operationid");
		SubsysOperation so = new SubsysOperation(this.frameconn,this.userView);
		String errmes = so.saveView_param(operationid,sortname,this.frameconn);
		this.getFormHM().put("sortname","");
		this.getFormHM().put("errmes",errmes);
	}

}
