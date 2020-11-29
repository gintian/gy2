/*
 * Created on 2005-8-10
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_resource;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>Title:DeleteZpresourceTrans</p>
 * <p>Description:删除招聘资源,zp_resource</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class DeleteZpresourceTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		ArrayList zpresourcelist=(ArrayList)this.getFormHM().get("selectedlist");
		if(zpresourcelist==null||zpresourcelist.size()==0)
            return;
		ExecuteSQL executeSQL = new ExecuteSQL();
        String sql = "";
        try
        {
        	for(int i=0;i<zpresourcelist.size();i++)
        	{
        		RecordVo rv=(RecordVo)zpresourcelist.get(i);
        		String type_id = rv.getString("type_id");
        		String resource_id = rv.getString("resource_id");
        		sql="delete from zp_resource where resource_id = "+resource_id;
        		executeSQL.execUpdate(sql);
        	}
        }
         
        
        catch(Exception sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }

	}

}
