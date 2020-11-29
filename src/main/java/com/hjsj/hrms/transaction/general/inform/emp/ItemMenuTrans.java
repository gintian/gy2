package com.hjsj.hrms.transaction.general.inform.emp;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class ItemMenuTrans extends IBusiness {


	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String a0100 = (String)hm.get("a0100");
		a0100=a0100!=null?a0100:"";
		hm.remove("a0100");
		
		String dbname = (String)hm.get("dbname");
		dbname=dbname!=null?dbname:"Usr";
		hm.remove("dbname");
		
		String defitem = (String)this.getFormHM().get("defitem");
		defitem=defitem!=null?defitem:"";
		
		this.getFormHM().put("defitem", defitem);
		this.getFormHM().put("a0100", a0100);
		this.getFormHM().put("dbname", dbname);
	}

}
