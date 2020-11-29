/*
 * Created on 2005-8-17
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

import java.sql.SQLException;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SubmitZpjobTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		RecordVo vo=(RecordVo)this.getFormHM().get("zpjobvo");
        ContentDAO dao=new ContentDAO(this.getFrameconn()); 
        try{
    	String sql="update zp_job set status = '05' where zp_job_id='"+vo.getString("zp_job_id")+"'";
    	dao.update(sql);
    	this.getFormHM().put("zp_job_id_value",vo.getString("zp_job_id"));
    }
    catch(SQLException sqle)
    {
   	     sqle.printStackTrace();
	     throw GeneralExceptionHandler.Handle(sqle);            
    }

	}

}
