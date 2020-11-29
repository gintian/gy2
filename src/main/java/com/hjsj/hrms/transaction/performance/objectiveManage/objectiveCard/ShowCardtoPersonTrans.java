package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard;

import com.hjsj.hrms.businessobject.performance.objectiveManage.DesignateTaskBo;
import com.hjsj.hrms.businessobject.performance.options.ConfigParamBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.Hashtable;

/**
 *<p>Title:ShowCardtoPersonTrans.java</p> 
 *<p>Description:目标卡任务分解</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Apr 24, 2012</p> 
 *@author JinChunhai
 *@version 6.0
 */

public class ShowCardtoPersonTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{			
		try
		{			 
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");			
			String plan_id = (String)hm.get("plan_id");			
			String p0400 = (String)hm.get("p0400");					
			
			DesignateTaskBo bo = new DesignateTaskBo(this.getFrameconn(),this.userView); 
			LoadXml loadxml = new LoadXml(this.frameconn, plan_id);
			Hashtable params = loadxml.getDegreeWhole();
			String cardTaskNameDesc = (String)params.get("TaskNameDesc"); // 取得任务内容名称 
			String targetTraceEnabled = (String) params.get("TargetTraceEnabled");
			String targetTraceItem = ""; // 目标跟踪指标			
			if(targetTraceEnabled!=null && "true".equals(targetTraceEnabled))
			    targetTraceItem = (String) params.get("TargetTraceItem");
			else
			{	// 2.从绩效模块参数配置中取目标跟踪指标			
			    ConfigParamBo configParamBo = new ConfigParamBo(this.getFrameconn());
			    targetTraceItem = configParamBo.getTargetTraceItem();
			}
			
			// 取得需要展现的json格式的数据的表头
			String columns = bo.getColumnsHead(cardTaskNameDesc,targetTraceItem);
			// 取得需要展现的json格式的数据的表体
			String datajson = bo.getDataJson(plan_id,p0400,targetTraceItem);
			
/*			
			System.out.println(columns);
			System.out.println("===============================================================================================================");
			System.out.println(datajson);
*/									
			this.getFormHM().put("columns",columns);
			this.getFormHM().put("datajson",datajson);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}	
	
}