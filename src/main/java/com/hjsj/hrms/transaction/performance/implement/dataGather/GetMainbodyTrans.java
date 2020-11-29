package com.hjsj.hrms.transaction.performance.implement.dataGather;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/** 
 *<p>Title:GetMainbodyTrans.java</p> 
 *<p>Description:数据采集：展现考核主体</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jun 30, 2010</p> 
 *@author JinChunhai
 *@version 4.0
 */

public class GetMainbodyTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String object_id=(String)hm.get("objectId");
			object_id = PubFunc.decrypt(object_id);
			String planid=(String)this.getFormHM().get("planId");
			PerformanceImplementBo pb=new PerformanceImplementBo(this.getFrameconn());
			ArrayList list=pb.getMainbodyListByObject(object_id,planid);
			this.getFormHM().put("mainbodyList",list);
			this.getFormHM().put("object_id",PubFunc.encrypt(object_id));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
