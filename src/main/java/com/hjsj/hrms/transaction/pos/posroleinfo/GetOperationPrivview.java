package com.hjsj.hrms.transaction.pos.posroleinfo;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 *<p>Title:GetOperationPrivview.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:May 6, 2009:6:34:47 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class GetOperationPrivview extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		if("UN".equalsIgnoreCase(this.userView.getManagePrivCode()))
			this.getFormHM().put("loadtype","2");
		else if("UM".equalsIgnoreCase(this.userView.getManagePrivCode()))
			this.getFormHM().put("loadtype","1");
		else
			this.getFormHM().put("loadtype","0");
		
	}

}
