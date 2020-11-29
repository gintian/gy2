package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hjsj.hrms.businessobject.sys.busimaintence.BusiSQLStr;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class ShowSubsysTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		String id;
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		id=(String) reqhm.get("id");
		if(id==null||id==""){
			id=(String)this.getFormHM().get("id");
		}
		reqhm.remove("id");
		BusiSQLStr bss=new BusiSQLStr();
		String[] sql=bss.getSubsysStr(id);
		hm.put("sql",sql[0]);
		hm.put("where",sql[1]);
		hm.put("column",sql[2]);
		hm.put("orderby",sql[3]);
		hm.put("mid", id);
		hm.put("isrefresh","false");
	}

}
