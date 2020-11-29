package com.hjsj.hrms.transaction.performance.achivement.achivementTask;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:撤销任务</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 20, 2010</p> 
 *@author JinChunhai
 *@version 4.0
 */

public class DelBigTaskTrans extends IBusiness {
	
	public void execute() throws GeneralException {
		
		String orgCode=(String)this.getFormHM().get("orgCode");
		String target_id=(String)this.getFormHM().get("target_id");
		try
		{						
			ContentDAO dao=new ContentDAO(this.getFrameconn());		
			StringBuffer sqlStr=new StringBuffer();
			sqlStr.append("delete from per_target_mx where target_id="+target_id+" and object_id='"+orgCode+"'");			
			dao.delete(sqlStr.toString(), null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
