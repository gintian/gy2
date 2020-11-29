package com.hjsj.hrms.transaction.performance.achivement.standarditem;

import com.hjsj.hrms.businessobject.performance.achivement.StandardItemBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchBaseRuleTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
			String type=(String)map.get("type");
			String pointid=(String)map.get("point_id");
			StandardItemBo bo = new StandardItemBo(this.getFrameconn());
			ArrayList baseRuleList=null;
			
			baseRuleList=bo.getStandardItemByPointid(pointid);

			this.getFormHM().put("baseRuleList",baseRuleList);
			this.getFormHM().put("point_id",pointid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
