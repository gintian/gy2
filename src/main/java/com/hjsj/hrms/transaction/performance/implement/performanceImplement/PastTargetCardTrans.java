package com.hjsj.hrms.transaction.performance.implement.performanceImplement;

import com.hjsj.hrms.businessobject.performance.kh_plan.KhTemplateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:SearchTargetCardSetTrans.java</p>
 * <p>Description:考核实施/目标卡制定/粘帖目标卡</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-11-01 11:11:11</p> 
 * @author JinChunhai
 * @version 1.0
 */

public class PastTargetCardTrans extends IBusiness
{
	
	public void execute() throws GeneralException
	{
		
		String planid = (String) this.getFormHM().get("planid");		
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String objCode = (String) hm.get("codeid");
		String pastObjId = (String) hm.get("pastObjId");
		String copyObjId = (String) hm.get("copyObjId");
		String delOldTarget = (String) hm.get("delOldTarget");
		hm.remove("pastObjId");
		hm.remove("copyObjId");
		hm.remove("delOldTarget");
		try
		{
			KhTemplateBo bo = new KhTemplateBo(this.getFrameconn(),"1",objCode,planid,"targetCard");
			if("1".equals(delOldTarget))
				bo.delOldTarget(planid, pastObjId);
			bo.pastObjTarget(planid,planid,pastObjId, copyObjId);
			
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
