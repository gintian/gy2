package com.hjsj.hrms.transaction.performance.options.per_relation;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * <p>
 * Title:BatchSetObjTypeTrans.java
 * </p>
 * <p>
 * Description:批量设置考核对象类型
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2010-11-30 09:56:36
 * </p>
 * 
 * @author JinChunhai
 * @version 1.0
 * 
 */
public class BatchSetObjTypeTrans extends IBusiness
{
	public void execute() throws GeneralException
	{
		try
		{
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String body_id = (String) hm.get("objTypeId");
			hm.remove("objTypeId");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String objectIDs=(String) hm.get("objectIDs");
			String[] object_id = (String[])objectIDs.split("`");
			if(body_id!=null && body_id.length()>0)
			{
				for (int i = 1; i < object_id.length; i++)
				{
					dao.update("update per_object_std set obj_body_id=" + body_id + " where  object_id='" + object_id[i] + "'");
				
				}
			}						
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
