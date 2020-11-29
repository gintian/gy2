package com.hjsj.hrms.transaction.performance.showkhresult;

import com.hjsj.hrms.businessobject.performance.showkhresult.TotalEvaluate;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:展现 总体评价 分析图</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jun 27, 2007:9:47:13 AM</p> 
 *@author dengcan
 *@version 4.0
 */
public class ShowTotalEvaluateTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String  type=(String)hm.get("type");      // 5:饼图   11:直方图 
			String  objectid=(String)hm.get("objectid");
			String  planid=(String)hm.get("planid");
			
			TotalEvaluate totalEvaluate=new TotalEvaluate(this.getFrameconn());
			ArrayList totalEvaluateInfoList=totalEvaluate.getTotalEvaluateLineList(planid,objectid);
			ArrayList remarkList=totalEvaluate.getRemarkList(planid,objectid);
			String title=totalEvaluate.getTitle(objectid);
			this.getFormHM().put("totalEvaluateInfoList",totalEvaluateInfoList);
			this.getFormHM().put("remarkList",remarkList);
			this.getFormHM().put("title",title);
			this.getFormHM().put("type",type);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
