/*
 * Created on 2005-8-12
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
 * <p>Title:DeleteZpreleasePosTrans</p>
 * <p>Description:删除招聘发布岗位,zp_position</p>
 * <p>Company:hjsj</p>
 * <p>create time:August 15, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */

public class DeleteZpreleasePosTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		ArrayList zppositionlist=(ArrayList)this.getFormHM().get("selectedzppositionlist");
		if(zppositionlist==null||zppositionlist.size()==0)
            return;
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        String sql = "";
        try
        {
        	for(int i=0;i<zppositionlist.size();i++)
        	{
        		RecordVo rv=(RecordVo)zppositionlist.get(i);
        		String zp_pos_id = rv.getString("zp_pos_id");
        		sql="delete from zp_position where zp_pos_id = '" + zp_pos_id + "'";
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