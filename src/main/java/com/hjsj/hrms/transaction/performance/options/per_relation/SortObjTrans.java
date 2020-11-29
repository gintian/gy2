package com.hjsj.hrms.transaction.performance.options.per_relation;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:SortObjTrans.java</p>
 * <p> Description:人员排序</p>
 * <p>Company:hjsj</p>
 * <p> create time:2009-04-16 11:11:11</p> 
 * @author JinChunhai
 * @version 1.0 
 */

public class SortObjTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		StringBuffer strSql = new StringBuffer();
		//以下写法ora里通不过
	//	strSql.append("update per_object_std set per_object_std.a0000 = usra01.a0000");
	//	strSql.append(" from usra01 where per_object_std.object_id=usra01.a0100");
		
		strSql.append("update per_object_std set a0000 = ");
		strSql.append("(select usra01.a0000 from usra01 where per_object_std.object_id=usra01.a0100)");
		
		
		try
		{
		    dao.update(strSql.toString());
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
    }
}
