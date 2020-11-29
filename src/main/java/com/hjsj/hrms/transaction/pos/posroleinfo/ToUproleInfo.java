package com.hjsj.hrms.transaction.pos.posroleinfo;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * 
 *<p>Title:ToUproleInfo.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:May 6, 2009:2:39:34 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class ToUproleInfo extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String codesetid = (String)hm.get("codesetid");
		this.getFormHM().put("codesetid",codesetid);
	}

}
