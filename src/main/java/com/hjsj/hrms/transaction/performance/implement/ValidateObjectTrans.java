package com.hjsj.hrms.transaction.performance.implement;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/** 
 *<p>Title:ValidateObjectTrans.java</p> 
 *<p>Description:校验考核对象是否设置了考核主体</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jul 10, 2008</p> 
 *@author JinChunhai
 *@version 4.0
 */

public class ValidateObjectTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
        {
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			String plan_id=(String)this.getFormHM().get("plan_id");
			String objectIDs=(String)this.getFormHM().get("objectIDs");
			String template_id=(String)this.getFormHM().get("template_id");
			StringBuffer obj=new StringBuffer("");
			String[] objs=objectIDs.split("@");
			for(int i=0;i<objs.length;i++)
			{
				if(objs[i].trim().length()>0)
				{
					obj.append(",'"+objs[i]+"'");
				}
			}
			
			this.frowset=dao.search("select count(id) from per_mainbody where plan_id=1 and object_id in ("+obj.substring(1)+")");
			String flag="0";
			if(this.frowset.next())
			{
				if(this.frowset.getInt(1)==0)
					flag="1";
			}
			this.getFormHM().put("flag",flag);
			this.getFormHM().put("objectIDs", objectIDs);
			this.getFormHM().put("template_id", template_id);
			
        }
		catch(Exception sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
		
	}

}
