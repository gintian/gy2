package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hjsj.hrms.businessobject.sys.busimaintence.BusiSelStr;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class CheckedItemdescTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String itemid = (String)this.getFormHM().get("itemid");
			String itemdesc=(String)this.getFormHM().get("itemdesc");
			String setid=(String)this.getFormHM().get("setid");
			String msg = "0";
			BusiSelStr  bss = new BusiSelStr();
			boolean flag = bss.isExist(itemid, setid, itemdesc, this.getFrameconn());
			if(!flag)
			{
				msg="1";
			}
			this.getFormHM().put("msg", msg);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
