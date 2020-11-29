package com.hjsj.hrms.transaction.performance.options.per_relation;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 删除考核对象
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:HJHJ
 * </p>
 * <p>
 * Create time:Apr 18, 2009
 * </p>
 * 
 * @author fanzhiguo
 * @version 1.0
 */
public class DelObjTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
	try
	{
	    String[] objectIDs = (String[]) this.getFormHM().get("objectID");
	    ContentDAO dao = new ContentDAO(this.getFrameconn());
	    StringBuffer whl = new StringBuffer("");
	    for (int i = 0; i < objectIDs.length; i++)
	    {
		if (objectIDs[i].trim().length() > 0)
		    whl.append(",'" + objectIDs[i] + "'");
	    }
	    if (whl.length() > 0)
	    {
		dao.delete("delete from per_object_std where  object_id in (" + whl.substring(1) + ")", new ArrayList());
		dao.delete("delete from per_mainbody_std where object_id in (" + whl.substring(1) + ")", new ArrayList());
	    }
	} catch (Exception e)
	{
	    e.printStackTrace();
	    throw GeneralExceptionHandler.Handle(e);
	}
    }

}
