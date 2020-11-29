/**
 * 
 */
package com.hjsj.hrms.transaction.sys.logonuser;

import com.hjsj.hrms.businessobject.sys.logonuser.UserObjectBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:AddUserTrans</p>
 * <p>Description:验证密码复杂度</p>
 * <p>Company:hjsj</p>
 * <p>create time:2013-6-7:17:05:35</p>
 * @author xuj
 * @version 1.0
 */
public class CheckPwdOkTrans extends IBusiness {


	public void execute() throws GeneralException {
		String pwd_ok=(String)this.getFormHM().get("pwd_ok");
		String mess = "ok";
		try
		{

			UserObjectBo userbo=new UserObjectBo(this.getFrameconn());
			//现对密码复杂度进行0低|1中|2强三种模式划分  xuj update 2013-5-29
			userbo.validatePasswordNew(pwd_ok);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			mess="errer";
			throw GeneralExceptionHandler.Handle(ex);
		}finally{
			this.getFormHM().put("mess", mess);
		}

	}

}
