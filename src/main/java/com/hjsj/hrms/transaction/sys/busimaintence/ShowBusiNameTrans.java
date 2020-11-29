package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hjsj.hrms.businessobject.sys.busimaintence.BusiSQLStr;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class ShowBusiNameTrans extends IBusiness {
	
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
//		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		BusiSQLStr bss=new BusiSQLStr();
		String[] sql=bss.getBusiNameStr();
		hm.put("sql",sql[0]+" "+sql[1]);
		hm.put("where","");
		hm.put("column",sql[2]);
		hm.put("orderby",sql[3]);
	}

}
