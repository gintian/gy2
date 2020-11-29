package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCardMonitor;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:BeginObjectiveCardTrans.java</p>
 * <p>Description:目标卡状态入口类</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-04-25 14:15:22</p>
 * @author JinChunhai
 * @version 1.0
 */

public class BeginObjectiveCardTrans extends IBusiness
{
	
	public void execute() throws GeneralException 
	{
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			
			String convertPageEntry=(String)hm.get("convertPageEntry");						
			hm.remove("convertPageEntry");
						
			this.getFormHM().put("convertPageEntry", convertPageEntry);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}		
	}	
}
