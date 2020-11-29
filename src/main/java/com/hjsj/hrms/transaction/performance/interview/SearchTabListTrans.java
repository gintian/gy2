package com.hjsj.hrms.transaction.performance.interview;

import com.hjsj.hrms.businessobject.performance.interview.PerformanceInterviewBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 9028000411
 * <p>Title:SearchTabListTrans.java</p>
 * <p>Description>:SearchTabListTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jun 27, 2009 1:13:36 PM</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class SearchTabListTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String plan_id = (String)map.get("plan_id");
			PerformanceInterviewBo bo = new PerformanceInterviewBo(this.getFrameconn());
			ArrayList tabList = bo.getTabids(plan_id);
			this.getFormHM().put("tabList",tabList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
