package com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.transaction;

import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * @ClassName: SetAppDateTrans 
 * @Description: TODO(设置业务日期) 
 * @author lis 
 * @date 2015-7-23 下午05:18:51
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
