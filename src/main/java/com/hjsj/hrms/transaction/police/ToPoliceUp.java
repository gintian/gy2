package com.hjsj.hrms.transaction.police;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * 
 *<p>Title:ToTaskUproleInfo.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:May 7, 2009:3:40:58 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class ToPoliceUp extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String b0110 = (String)hm.get("b0110");
		String a0100 = (String) hm.get("a0100");
		this.getFormHM().put("b0110",b0110);
		this.getFormHM().put("a0100",a0100);
	}

}
