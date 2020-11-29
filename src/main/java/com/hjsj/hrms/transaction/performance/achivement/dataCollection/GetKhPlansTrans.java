package com.hjsj.hrms.transaction.performance.achivement.dataCollection;

import com.hjsj.hrms.businessobject.performance.achivement.dataCollection.DataCollectBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:GetKhPlansTrans.java</p>
 * <p>Description:获得考核计划</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-09-08 09:03:30</p>
 * @author JinChunhai
 * @version 5.0
 */  

public class GetKhPlansTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
		DataCollectBo bo = new DataCollectBo(this.getFrameconn(),this.userView);
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		String planContext="";
		if(hm.get("planContext")!=null)
		{
			planContext=(String)hm.get("planContext");
			hm.remove("planContext");
		}
		ArrayList list = bo.getPlanList(planContext);
		if(list.size()==0)
		    throw new GeneralException("没有有效的考核计划！");
		this.getFormHM().put("khPlans",list);
    }

}
