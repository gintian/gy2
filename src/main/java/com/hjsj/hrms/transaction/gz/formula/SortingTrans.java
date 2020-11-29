package com.hjsj.hrms.transaction.gz.formula;

import com.hjsj.hrms.businessobject.gz.FormulaBo;
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
		String salaryid = (String)reqhm.get("salaryid");
		salaryid=salaryid!=null&&salaryid.trim().length()>0?salaryid:"";
		reqhm.remove("salaryid");

		FormulaBo tempvarbo = new FormulaBo();
		hm.put("sortlist",tempvarbo.sortList(this.frameconn,salaryid));
		
		hm.put("salaryid",salaryid);
	}

}
