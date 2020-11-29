package com.hjsj.hrms.transaction.performance.achivement.achivementTask;

import com.hjsj.hrms.businessobject.performance.achivement.AchivementTaskBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 *<p>Title:</p> 
 *<p>Description:提交任务分配</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 11, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class SubAllocateTaskTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String cycle=(String)this.getFormHM().get("cycle");
			String target_id=(String)this.getFormHM().get("target_id");
			String[] right_fields=(String[])this.getFormHM().get("right_fields");
			AchivementTaskBo bo=new AchivementTaskBo(this.getFrameconn(),this.userView);
			bo.subAchivementTask(target_id,right_fields,cycle);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
