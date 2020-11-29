package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCardMonitor;

import com.hjsj.hrms.businessobject.performance.objectiveManage.ParseXmlBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class ObjectSpDetailTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String plan_id=(String)hm.get("plan_id");
			String object_id=(String)hm.get("object_id");
			ParseXmlBo bo = new ParseXmlBo(this.getFrameconn());
			HashMap map=bo.getObjectSpDetailInfo(plan_id, object_id);
			this.getFormHM().put("objectSpDetailInfo", (String)map.get("detail"));
			this.getFormHM().put("a0101", (String)map.get("a0101"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
