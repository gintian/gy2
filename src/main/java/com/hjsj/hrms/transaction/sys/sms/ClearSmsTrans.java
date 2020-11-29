/**
 * 
 */
package com.hjsj.hrms.transaction.sys.sms;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Aug 3, 20062:56:00 PM
 * @author chenmengqing
 * @version 4.0
 */
public class ClearSmsTrans extends IBusiness {


	public void execute() throws GeneralException {
		String stat=(String)this.getFormHM().get("state");
		StringBuffer strsql=new StringBuffer();
		try
		{
			strsql.append("delete from t_sys_smsbox where status='");
			strsql.append(stat);
			strsql.append("'");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			dao.update(strsql.toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
