package com.hjsj.hrms.transaction.performance.objectiveManage.designateTask;

import com.hjsj.hrms.businessobject.performance.objectiveManage.DesignateTaskBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:AddDesignateTasktrans.java</p>
 * <p>Description>:AddDesignateTasktrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jul 28, 2011  1:56:32 PM </p>
 * <p>@version: 5.0</p>
 * <p>@author: LiZhenWei
 */
public class AddDesignateTaskTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			DesignateTaskBo dtb = new DesignateTaskBo(this.getFrameconn(),this.userView);
			String p0407 = (String)this.getFormHM().get("p0407");
			String plan_id = (String)this.getFormHM().get("plan_id");
			String objectid=(String)this.getFormHM().get("objectid");
			String p0400=(String)this.getFormHM().get("p0400");
			dtb.addRecord(p0400, p0407);
			this.getFormHM().put("plan_id", plan_id);
			this.getFormHM().put("objectid", objectid);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
