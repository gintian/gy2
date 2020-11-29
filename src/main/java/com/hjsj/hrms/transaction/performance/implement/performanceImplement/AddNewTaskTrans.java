package com.hjsj.hrms.transaction.performance.implement.performanceImplement;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:AddNewTaskTrans.java</p>
 * <p>Description:考核实施/目标卡制定/新建任务</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-12-07 14:21:56</p> 
 * @author JinChunhai
 * @version 5.0
 */

public class AddNewTaskTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String beforeItemid=(String)hm.get("beforeitemid");
			
			
			this.getFormHM().put("beforeItemid",beforeItemid);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}