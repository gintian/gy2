package com.hjsj.hrms.transaction.performance.implement.performanceImplement;

import com.hjsj.hrms.businessobject.performance.performanceImplement.ExamActualizeBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:ImplementWeightTran.java</p>
 * <p>Description:设置主体权重</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 4, 2008:9:05:04 AM</p>
 * @author JinChunhai
 * @version 1.0
 */

public class ImplementWeightTran extends IBusiness 
{
	
	public void execute() throws GeneralException 
	{
		// TODO Auto-generated method stub
		try
		{
			
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String planid=(String)hm.get("planid");
			CheckPrivSafeBo _bo = new CheckPrivSafeBo(this.frameconn,this.userView);
	        if(!_bo.isHavePriv(this.userView, planid)){	
	        	return;
	        } 
			ExamActualizeBo pe=new ExamActualizeBo(this.getFrameconn(),planid);
			ArrayList purviewList=pe.getPurviewList(planid);
			this.getFormHM().put("purviewList",purviewList);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}

