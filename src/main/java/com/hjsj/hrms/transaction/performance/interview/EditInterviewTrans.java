package com.hjsj.hrms.transaction.performance.interview;

import com.hjsj.hrms.businessobject.performance.interview.PerformanceInterviewBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class EditInterviewTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String objectid=PubFunc.decrypt((String)hm.get("objectid"));
			String plan_id=PubFunc.decrypt((String)hm.get("planid"));
			if(plan_id.length()==0){
				plan_id = (String) this.getFormHM().get("plan_id");
			}
			String id=(String)hm.get("id");
			String body=(String)hm.get("body");
			String oper=(String)hm.get("oper");
			String interview="";
			String status="0";
			PerformanceInterviewBo bo = new PerformanceInterviewBo(this.getFrameconn());
			if("1".equals(oper))
			{
		     	if(!"-1".equals(id))
		    	{
		    		//PerformanceInterviewBo bo = new PerformanceInterviewBo(this.getFrameconn());
		    		HashMap mp=bo.getInterviewContentById(id);
		     		interview=(String)mp.get("str");
		     		status=(String)mp.get("status");
		    		//interview=interview.replaceAll("#@#", " ");
		    	}
			}
			else
			{
				interview=bo.getInterviewContent(plan_id, objectid);
			}
			this.getFormHM().put("id",id);
			this.getFormHM().put("objectid", objectid);
			this.getFormHM().put("interview",interview);
			this.getFormHM().put("plan_id", plan_id);
			this.getFormHM().put("body", body);
			this.getFormHM().put("status", status);		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	

}
