package com.hjsj.hrms.transaction.performance.achivement.achivementTask;

import com.hjsj.hrms.businessobject.performance.achivement.AchivementTaskBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:AllocateTaskTrans.java</p> 
 *<p>Description:分配任务</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 10, 2008</p> 
 *@author JinChunhai
 *@version 4.0
 */

public class AllocateTaskTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String target_id=(String)hm.get("target_id");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String tree_loadtype="0";  //0（单位|部门|职位）  =1 (单位|部门)
			String tree_flag="1";  //1:加载人员信息  0:不加载
			this.frowset=dao.search("select object_type,cycle from per_target_list where target_id="+target_id);
			ArrayList cycleList=new ArrayList();
			AchivementTaskBo bo=new AchivementTaskBo(this.getFrameconn(),this.userView);
			if(this.frowset.next())
			{
				String object_type=this.frowset.getString("object_type");
				if("1".equals(object_type))
				{
					tree_loadtype="1";
					tree_flag="0";
				}
				
				cycleList=bo.getCycleList(this.frowset.getInt("cycle"));
			}
			this.getFormHM().put("cycleList",cycleList);
			this.getFormHM().put("tree_loadtype",tree_loadtype);
			this.getFormHM().put("tree_flag", tree_flag);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}		
	
}
