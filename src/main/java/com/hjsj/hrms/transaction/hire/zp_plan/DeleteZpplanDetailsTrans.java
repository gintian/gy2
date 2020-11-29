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
 * <p>Title:DeleteZpplanDetailsTrans</p>
 * <p>Description:删除招聘计划明细,zp_plan_details</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class DeleteZpplanDetailsTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		ArrayList zpplanDetailslist=(ArrayList)this.getFormHM().get("selectedlist");
		if(zpplanDetailslist==null||zpplanDetailslist.size()==0)
            return;
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        String sql = "";
        try
        {
        	String gather_id=null;
        	for(int i=0;i<zpplanDetailslist.size();i++)
        	{
        		RecordVo rv=(RecordVo)zpplanDetailslist.get(i);
        		String plan_id = rv.getString("plan_id");
        		String details_id = rv.getString("details_id");
        		sql="delete from zp_plan_details where plan_id = '" + plan_id + "' and details_id = '"+details_id+"'";
        		gather_id=rv.getString("gather_id");
        		ArrayList templst=new ArrayList();
        		dao.delete(sql,templst);
        		if(gather_id!=null)
            	{
            		sql="update zp_gather set usedflag=0 where gather_id='" + gather_id + "' and gather_id not in (select gather_id from zp_plan_details where gather_id='" + gather_id + "')";
            		dao.update(sql);
            	}
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
