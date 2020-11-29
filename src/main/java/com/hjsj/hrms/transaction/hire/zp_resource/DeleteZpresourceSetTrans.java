/*
 * Created on 2005-8-9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_resource;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>Title:DeleteZpresourceSetTrans</p>
 * <p>Description:删除招聘资源分类,zp_resource_set</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class DeleteZpresourceSetTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {

		ArrayList zpresourceSetlist=(ArrayList)this.getFormHM().get("selectedzpresourceSetlist");
		if(zpresourceSetlist==null||zpresourceSetlist.size()==0)
            return;
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        String sql = "";
        String sql_pos = "";
        try
        {
        	for(int i=0;i<zpresourceSetlist.size();i++)
        	{
        		RecordVo rv=(RecordVo)zpresourceSetlist.get(i);
        		String type_id = rv.getString("type_id");
        		sql="delete from zp_resource_set where type_id = '" + type_id + "'";
        		ArrayList templst=new ArrayList();
        		dao.delete(sql,templst);
        	}
         // dao.deleteValueObject(liuyanlist);
        }
         
        
        catch(Exception sqle)
	   // catch(SQLException sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }

	}

}
