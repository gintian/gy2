/*
 * Created on 2005-8-16
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
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SubmitStaffReqTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		RecordVo vo=(RecordVo)this.getFormHM().get("zpgathervo");
        ContentDAO dao=new ContentDAO(this.getFrameconn()); 
        try{
        String sql="update zp_gather set status = '03' where gather_id=?";
        ArrayList paralist=new ArrayList();
        paralist.add(vo.getString("gather_id"));
        dao.update(sql,paralist);    	
    	this.getFormHM().put("gather_id_value",vo.getString("gather_id"));  	
    	
    }
    catch(SQLException sqle)
    {
   	     sqle.printStackTrace();
	     throw GeneralExceptionHandler.Handle(sqle);            
    }

	}

}
