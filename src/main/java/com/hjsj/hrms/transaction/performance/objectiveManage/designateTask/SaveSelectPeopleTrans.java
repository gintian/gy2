package com.hjsj.hrms.transaction.performance.objectiveManage.designateTask;

import com.hjsj.hrms.businessobject.performance.objectiveManage.DesignateTaskBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:SaveSelectPeopleTrans.java</p>
 * <p>Description>:目标卡任务下达操作类</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jul 27, 2011  3:14:07 PM </p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class SaveSelectPeopleTrans extends IBusiness
{
	public void execute() throws GeneralException 
	{
		try
		{
			DesignateTaskBo bo = new DesignateTaskBo(this.getFrameconn(),this.userView);
			String plan_id=(String)this.getFormHM().get("plan_id");
			String to_plan_id=(String)this.getFormHM().get("to_plan_id");
			String to_itemid=(String)this.getFormHM().get("to_itemid");
			String p0400=(String)this.getFormHM().get("p0400");
			String qzfp=(String)this.getFormHM().get("qzfp");
			String fromflag=(String)this.getFormHM().get("fromflag");
			String p0401=(String)this.getFormHM().get("p0401");
			String a0100s=(String)this.getFormHM().get("hiddenStr");
			String p0407=(String)this.getFormHM().get("p0407");
			String type=(String)this.getFormHM().get("type");
			String taskid=(String)this.getFormHM().get("taskid");
			String group_id=(String)this.getFormHM().get("group_id");
			String task_type=(String)this.getFormHM().get("task_type");
			bo.addDesinateTask(p0400, plan_id, to_plan_id, to_itemid, a0100s.replaceAll("／", "/"), p0407, fromflag, p0401, qzfp,type,taskid,group_id,task_type);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
