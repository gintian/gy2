package com.hjsj.hrms.transaction.org.orgdata;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class ItemMenuTrans extends IBusiness {


	public void execute() throws GeneralException {
	
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String itemid = (String)hm.get("itemid");
		itemid=itemid!=null?itemid:"";
		hm.remove("itemid");
		
		String infor = (String)hm.get("infor");
		infor=infor!=null?infor:"";
		hm.remove("infor");
		
		String defitem = (String)this.getFormHM().get("defitem");
		defitem=defitem!=null?defitem:"";
		
		this.getFormHM().put("defitem", defitem);
		this.getFormHM().put("itemid", itemid);
		this.getFormHM().put("infor", infor);
	}

}
