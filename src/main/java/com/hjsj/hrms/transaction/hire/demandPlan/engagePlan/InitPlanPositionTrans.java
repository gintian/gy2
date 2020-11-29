package com.hjsj.hrms.transaction.hire.demandPlan.engagePlan;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class InitPlanPositionTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String z0101=(String)hm.get("z0101");
		hm.remove("z0101");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			dao.update("update z01 set z0115=null where z0101='"+z0101+"'");
			dao.update("update z03 set z0101='',z0317='0' where z0301 in (select z0301 from z03 where z0101='"+z0101+"')");
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
