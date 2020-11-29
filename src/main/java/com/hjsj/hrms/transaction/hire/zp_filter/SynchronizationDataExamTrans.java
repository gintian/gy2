/*
 * Created on 2006-5-25
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_filter;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SynchronizationDataExamTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try{
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		 StringBuffer sql=new StringBuffer();
		 sql.append("INSERT INTO zp_exam_report(a0100) select distinct a0100 from zp_pos_tache where a0100 not in (select a0100 from zp_exam_report)");
	     dao.insert(sql.toString(),new ArrayList());	
		}catch(Exception e)
		{
			e.printStackTrace();
	  	    throw GeneralExceptionHandler.Handle(e);
		}

	}

}
