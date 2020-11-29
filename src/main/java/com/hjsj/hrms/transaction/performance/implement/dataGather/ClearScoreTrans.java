package com.hjsj.hrms.transaction.performance.implement.dataGather;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 *<p>Title:ClearScoreTrans.java</p> 
 *<p>Description:清空分数</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jun 30, 2009</p> 
 *@author JinChunhai
 *@version 4.0
 */

public class ClearScoreTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String object_id=PubFunc.decrypt((String)this.getFormHM().get("object_id"));
			String planid=(String)this.getFormHM().get("planid");
			String body_id=PubFunc.decrypt((String)this.getFormHM().get("body_id"));
			dao.delete("delete from per_table_"+planid+" where object_Id='"+object_id+"' and  mainbody_id='"+body_id+"'",new ArrayList());
			dao.update("update per_mainbody set status=0, know_id=null,whole_grade_id=null where plan_id="+planid+" and mainbody_id='"+body_id+"' and object_id='"+object_id+"'");
			this.getFormHM().put("planid",planid);
			//这里应该放加密后的object_id和body_id
			this.getFormHM().put("objectID",this.getFormHM().get("object_id"));
			this.getFormHM().put("mainbodyID",this.getFormHM().get("body_id"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
