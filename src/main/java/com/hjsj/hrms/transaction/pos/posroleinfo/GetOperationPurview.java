package com.hjsj.hrms.transaction.pos.posroleinfo;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Map;
/**
 * 
 *<p>Title:GetOperationPurview.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:May 6, 2009:2:38:32 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class GetOperationPurview extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		if("UN".equalsIgnoreCase(this.userView.getUnit_id()))
			this.getFormHM().put("loadtype","2");
		else if("UM".equalsIgnoreCase(this.userView.getUnit_id()))
			this.getFormHM().put("loadtype","1");
		else
			this.getFormHM().put("loadtype","0");
		
		Map map = (Map) this.getFormHM().get("requestPamaHM");
		String modular = (String) map.get("modular");
		this.getFormHM().put("modular", modular);
		
	}

}
