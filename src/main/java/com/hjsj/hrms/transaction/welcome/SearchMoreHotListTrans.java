package com.hjsj.hrms.transaction.welcome;

import com.hjsj.hrms.businessobject.gz.GzSpFlowBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchMoreHotListTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String home=(String)map.get("home");
			String discriminateFlag=(String)map.get("discriminateFlag");
			GzSpFlowBo bo = new GzSpFlowBo(this.getFrameconn(),this.getUserView());
			bo.setEnteryType("0");
			ArrayList list = bo.getHotInvestigateList(discriminateFlag);
			this.getFormHM().put("moreList",list);
			this.getFormHM().put("home", home);
			this.getFormHM().put("discriminateFlag", discriminateFlag);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
