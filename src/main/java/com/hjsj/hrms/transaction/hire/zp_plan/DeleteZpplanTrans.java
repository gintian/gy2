/*
 * Created on 2005-8-10
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_plan;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>Title:DeleteZpplanTrans</p>
 * <p>Description:删除招聘计划,zp_plan</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class DeleteZpplanTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		ArrayList zpplanlist=(ArrayList)this.getFormHM().get("selectedzpplanlist");
		if(zpplanlist==null||zpplanlist.size()==0)
            return;
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        String sql = "";
        String sql_pos = "";
        String status="";
        try
        {
        	for(int i=0;i<zpplanlist.size();i++)
        	{
        		RecordVo rv=(RecordVo)zpplanlist.get(i);
        		String plan_id = rv.getString("plan_id");
        		sql="delete from zp_plan where plan_id = '" + plan_id + "'";
        		this.frowset=dao.search("select status from zp_plan where plan_id='" + plan_id + "'");
        		if(this.frowset.next())
        			status=this.frowset.getString("status");
        		ArrayList templst=new ArrayList();
        		dao.delete(sql,templst);
        		this.frowset=dao.search("select distinct gather_id from zp_plan_details where plan_id='" + plan_id + "'");
        		while(this.frowset.next() && !"06".equals(status))
        		{
        			String sql_gather="update zp_gather set usedflag=0 where gather_id='" + this.frowset.getString("gather_id") + "'";
        			dao.update(sql_gather);
        		}
        		sql_pos = "delete from zp_plan_details where plan_id = '" + plan_id + "'";
        		dao.delete(sql_pos,templst);
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
