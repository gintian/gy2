package com.hjsj.hrms.transaction.performance.markStatus;

import com.hjsj.hrms.businessobject.performance.markStatus.MarkStatusBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 *<p>Title:SetReScoreTrans.java</p> 
 *<p>Description:重新打分</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jun 10, 2010</p> 
 *@author JinChunhai
 *@version 4.0
 */

public class SetReScoreTrans extends IBusiness
{
	public void execute() throws GeneralException 
	{
		try
		{
			String id=(String)this.getFormHM().get("id");
			MarkStatusBo bo = new MarkStatusBo(this.getFrameconn(), this.userView); // bo中需要userView add by刘蒙
			bo.setStatus(id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
