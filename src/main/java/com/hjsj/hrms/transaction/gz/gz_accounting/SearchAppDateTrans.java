/**
 * 
 */
package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;


/**
 *<p>Title:</p> 
 *<p>Description:查询当前用户设置的业务日期</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-10-27:上午09:14:30</p> 
 *@author cmq
 *@version 4.0
 */
public class SearchAppDateTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String appdate=ConstantParamter.getAppdate(this.userView.getUserName()).trim();
			this.getFormHM().put("appdate", appdate);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

}
