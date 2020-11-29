package com.hjsj.hrms.transaction.performance.workplan.workplanview;

import com.hjsj.hrms.businessobject.performance.WorkPlanViewBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SearchAttachTrans extends IBusiness
{
	
	public void execute() throws GeneralException 
	{
		
		try
		{
			
			String p0100=(String)this.getFormHM().get("p0100");
			WorkPlanViewBo bo = new WorkPlanViewBo(this.userView,this.getFrameconn());
			ArrayList attachList = bo.getAttachFileList(p0100);
			this.getFormHM().put("attachList",attachList);
			this.getFormHM().put("p0100",p0100);
			
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	
}
