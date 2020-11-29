package com.hjsj.hrms.transaction.performance.objectiveManage.setUnderlingObjective;

import com.hjsj.hrms.businessobject.performance.objectiveManage.SetUnderlingObjectiveBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchReverseMainbodyTrans.java</p>
 * <p>Description>:目标执行情况回顾反查</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Oct 18, 2011 11:11:11 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class SearchReverseMainbodyTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");	
			String opt = (String)map.get("opt");  // 已回顾:already  未回顾：noAlready
			String plan_id = (String)map.get("plan_id");
			String object_id = (String)map.get("object_id"); 
			String alreadyCaseMainbody = (String)map.get("alreadyCaseMainbody");  // 已回顾的主体编号串
			map.remove("opt");		
			map.remove("plan_id");		
			map.remove("object_id");							
			map.remove("alreadyCaseMainbody");
			
			String[] mainbodyIds = null;
			if(alreadyCaseMainbody!=null && alreadyCaseMainbody.trim().length()>0)
			{								
				alreadyCaseMainbody = alreadyCaseMainbody.substring(1);
				mainbodyIds = alreadyCaseMainbody.replaceAll("／", "/").split("/");				
			}
			String mainbody_ids = "";
			if(mainbodyIds!=null && mainbodyIds.length>0)
			{
				for (int k = 0; k < mainbodyIds.length; k++)
				{
					mainbody_ids += ",'" + mainbodyIds[k] + "'";
				}
			}
			
			SetUnderlingObjectiveBo bo = new SetUnderlingObjectiveBo(this.getFrameconn());
			ArrayList reverseList = bo.getPerMainBodyList(plan_id, object_id, mainbody_ids, opt);
			this.getFormHM().put("reverseList",reverseList);
			
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
