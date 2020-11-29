package com.hjsj.hrms.transaction.gz.premium.param;

import com.hjsj.hrms.businessobject.gz.premium.FormulaPremiumBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:计算公式</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
**/
public class SortingTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		String setid = (String)reqhm.get("setid");
		setid=setid!=null&&setid.trim().length()>0?setid:"";
		reqhm.remove("setid");
		String fmode = (String)hm.get("fmode");
		FormulaPremiumBo formulaPremiumBo = new FormulaPremiumBo();
		hm.put("sortlist",formulaPremiumBo.sortList(this.frameconn,setid,fmode));
		
		hm.put("setid",setid);
	}

}
