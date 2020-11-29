package com.hjsj.hrms.transaction.performance.achivement.standarditem;

import com.hjsj.hrms.businessobject.performance.achivement.StandardItemBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class DeleteStandardItemTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String itemid=(String)this.getFormHM().get("itemid");
			String point_id = (String)this.getFormHM().get("point_id");
			StandardItemBo bo = new StandardItemBo(this.getFrameconn());
			bo.deleteStandardItem(itemid);
			bo.configChild(itemid);
			this.getFormHM().put("point_id",point_id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
