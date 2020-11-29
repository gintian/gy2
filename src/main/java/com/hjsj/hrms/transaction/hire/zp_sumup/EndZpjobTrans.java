/*
 * Created on 2005-9-9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_sumup;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:EndZpjobTrans</p>
 * <p>Description:结束招聘活动</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 18, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class EndZpjobTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		RecordVo vo=(RecordVo)this.getFormHM().get("zpSumupvo");
        if(vo==null)
            return;
        ContentDAO dao=new ContentDAO(this.getFrameconn()); 
	        try
	        {
	        	ArrayList list = new ArrayList();
	        	String sql="update zp_job set status = '06' where zp_job_id='"+vo.getString("zp_job_id")+"'";
	        	dao.update(sql,list);
	        }
	        catch(SQLException sqle)
	        {
	       	     sqle.printStackTrace();
	    	     throw GeneralExceptionHandler.Handle(sqle);            
	        }

	}

}
