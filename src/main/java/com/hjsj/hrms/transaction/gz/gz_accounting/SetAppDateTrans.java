/**
 * 
 */
package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 *<p>Title:设置业务日期</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-9-21:下午02:29:59</p> 
 *@author cmq
 *@version 4.0
 */
public class SetAppDateTrans extends IBusiness {

	public void execute() throws GeneralException {
		String appdate=(String)this.getFormHM().get("appdate");
		try
		{
			ConstantParamter.putAppdate(this.userView.getUserName(), appdate);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
}
