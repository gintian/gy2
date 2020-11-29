package com.hjsj.hrms.transaction.gz.formula;

import com.hjsj.hrms.businessobject.gz.FormulaBo;
import com.hjsj.hrms.utils.PubFunc;
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
		
		
		hm.put("salaryid",id);
		hm.put("conditions",PubFunc.keyWord_reback(SafeCode.decode(conditions)));
		
		FormulaBo formulsbo = new FormulaBo();
		
		hm.put("itemid","");
		if("-2".equals(id)){
			String fieldsetid = (String) this.getFormHM().get("fieldsetid");
			fieldsetid = "undefined".equalsIgnoreCase(fieldsetid)?"":fieldsetid;
			hm.put("itemlist",formulsbo.conditionsList(this.frameconn,id,fieldsetid));
		}else{
			hm.put("itemlist",formulsbo.conditionsList(this.frameconn,id));
		}
		
	}

}
