package com.hjsj.hrms.transaction.performance.achivement.achivementTask;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:删除绩效任务书</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 16, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class DelAchievementTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String target_id=(String)this.getFormHM().get("target_id");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			dao.delete("delete from per_target_list where target_id="+target_id,new ArrayList());
			dao.delete("delete from per_target_point where target_id="+target_id,new ArrayList());
			dao.delete("delete from per_target_mx where target_id="+target_id,new ArrayList());
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
