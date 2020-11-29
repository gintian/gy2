/*
 * Created on 2005-8-3
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.staffreq;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:DeleteStaffReqTrans</p>
 * <p>Description:删除临时用工申请，zp_gather</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class DeleteStaffReqTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		ArrayList zpgatherlist=(ArrayList)this.getFormHM().get("selectedzpgatherlist");
		if(zpgatherlist==null||zpgatherlist.size()==0)
            return;
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        String sql = "";
        String sql_pos = "";
        try
        {
        	for(int i=0;i<zpgatherlist.size();i++)
        	{
        		RecordVo rv=(RecordVo)zpgatherlist.get(i);
        		String gather_id = rv.getString("gather_id");
        		sql="delete from zp_gather where gather_id = '" + gather_id + "'";
        		ArrayList templst=new ArrayList();
        		dao.delete(sql,templst);
        		sql_pos = "delete from zp_gather_pos where gather_id = '" + gather_id + "'";
        		dao.delete(sql_pos,templst);
        	}
        }
	   catch(SQLException sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }

	}

}
