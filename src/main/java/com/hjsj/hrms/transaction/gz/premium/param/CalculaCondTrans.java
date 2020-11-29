package com.hjsj.hrms.transaction.gz.premium.param;

import com.hjsj.hrms.businessobject.gz.premium.FormulaPremiumBo;
import com.hrms.frame.codec.SafeCode;
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
public class CalculaCondTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		
		String id = (String)reqhm.get("id");
		id=id!=null&&id.length()>0?id:"";
		reqhm.remove("id");
		
		String conditions = (String)reqhm.get("conditions");
		conditions=conditions!=null&&conditions.length()>0?conditions:"";
		conditions = "undefined".equalsIgnoreCase(conditions)?"":conditions;
		reqhm.remove("conditions");
		
		
		hm.put("setid",id);
		hm.put("conditions",SafeCode.decode(conditions));
		
		FormulaPremiumBo formulaPremiumBo = new FormulaPremiumBo();
		
		hm.put("itemid","");
		hm.put("itemlist",formulaPremiumBo.conditionsList(this.frameconn,id));
	}

}
