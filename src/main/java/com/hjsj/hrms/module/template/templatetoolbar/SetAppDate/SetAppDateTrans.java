package com.hjsj.hrms.module.template.templatetoolbar.SetAppDate;

import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 
 * 
 * @Description: 人事异动-设置业务日期
 * @author gaohy 
 * @date Mar 4, 2016 4:47:40 PM 
 * @version V7x
 */
public class SetAppDateTrans extends IBusiness {

	@Override
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
