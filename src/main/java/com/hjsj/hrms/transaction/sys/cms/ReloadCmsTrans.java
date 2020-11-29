/**
 * 
 */
package com.hjsj.hrms.transaction.sys.cms;

import com.hjsj.hrms.businessobject.sys.cms.Cms_ChannelBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 *<p>Title:ReloadCmsTrans</p> 
 *<p>Description:重新加载内容平台</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-4-17:上午09:12:14</p> 
 *@author cmq
 *@version 4.0
 */
public class ReloadCmsTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			Cms_ChannelBo cms_bo=new Cms_ChannelBo(this.getFrameconn());
			cms_bo.refreshChildlist();
			this.getFormHM().clear();
			this.getFormHM().put("message", "内容加载成功!");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		
	}

}
