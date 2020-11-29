/*
 * Created on 2005-8-31
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>Title:DeleteTestProcessTrans</p>
 * <p>Description:删除面试环节</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 20, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class DeleteTestProcessTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		ArrayList testProcesslist=(ArrayList)this.getFormHM().get("selectedlist");
		if(testProcesslist==null||testProcesslist.size()==0)
            return;
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        String sql = "";
        try
        {
        	for(int i=0;i<testProcesslist.size();i++)
        	{
        		RecordVo rv=(RecordVo)testProcesslist.get(i);
        		String tache_id = rv.getString("tache_id");
        		sql="delete from zp_tache where tache_id = " + tache_id;
        		ArrayList templst=new ArrayList();
        		dao.delete(sql,templst);
        	}
        }
         
        
        catch(Exception sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }

	}

}
