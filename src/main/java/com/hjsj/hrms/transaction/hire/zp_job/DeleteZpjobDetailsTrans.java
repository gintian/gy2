/*
 * Created on 2005-8-12
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_job;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>Title:DeleteZpjobDetailsTrans</p>
 * <p>Description:删除招聘活动明细,zp_job_details</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class DeleteZpjobDetailsTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		ArrayList zpjobDetailslist=(ArrayList)this.getFormHM().get("selectedlist");
		if(zpjobDetailslist==null||zpjobDetailslist.size()==0)
            return;
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        String sql = "";
        try
        {
        	for(int i=0;i<zpjobDetailslist.size();i++)
        	{
        		RecordVo rv=(RecordVo)zpjobDetailslist.get(i);
        		String zp_job_id = rv.getString("zp_job_id");
        		String detail_id = rv.getString("detail_id");
        		sql="delete from zp_job_details where zp_job_id = '" + zp_job_id + "' and detail_id = "+detail_id;
        		ArrayList templst=new ArrayList();
        		dao.delete(sql,templst);
        	}
        }
         
        
        catch(Exception sqle)
	   // catch(SQLException sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }

	}

}
