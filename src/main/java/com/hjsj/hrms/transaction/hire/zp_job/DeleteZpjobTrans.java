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
 * <p>Title:DeleteZpjobTrans</p>
 * <p>Description:删除招聘活动,zp_job</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 02, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class DeleteZpjobTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		ArrayList zpjoblist=(ArrayList)this.getFormHM().get("selectedzpjoblist");
		if(zpjoblist==null||zpjoblist.size()==0)
            return;
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        String sql = "";
        String sql_pos = "";
        try
        {
        	for(int i=0;i<zpjoblist.size();i++)
        	{
        		RecordVo rv=(RecordVo)zpjoblist.get(i);
        		String zp_job_id = rv.getString("zp_job_id");
        		sql="delete from zp_job where zp_job_id = '" + zp_job_id + "'";
        		ArrayList templst=new ArrayList();
        		dao.delete(sql,templst);
        		sql_pos = "delete from zp_job_details where zp_job_id = '" + zp_job_id + "'";
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
