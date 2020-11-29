package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard;

import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:</p> 
 *<p>Description:删除目标任务</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jun 2, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class DeleteTasknodeTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String p0400 = (String)hm.get("p0400");
			
			String plan_id = (String)this.getFormHM().get("planid");
			String object_id = (String)this.getFormHM().get("object_id");		    
		    String objectSpFlag = (String)this.getFormHM().get("objectSpFlag");		    
		    ObjectCardBo bo = new ObjectCardBo(this.getFrameconn(),plan_id,object_id,this.userView);
		    // 修改员工已批的目标卡时程序将自动发送邮件给员工  JinChunhai 2013.03.19
		    if(objectSpFlag!=null && objectSpFlag.trim().length()>0 && "03".equalsIgnoreCase(objectSpFlag) && !object_id.equalsIgnoreCase(this.userView.getA0100()))
		    	bo.sendEmailObj(p0400,"del",new ArrayList());
		    
		    ContentDAO dao = new ContentDAO(this.getFrameconn());
		    dao.delete("delete from  p04 where p0400="+p0400,new ArrayList());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
