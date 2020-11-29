package com.hjsj.hrms.module.gz.salarytype.transaction;


import com.hjsj.hrms.module.gz.salarytype.businessobject.SalaryTypeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 项目名称 ：ehr
 * 类名称：SynchronizeTableStructTrans
 * 类描述：同步结构
 * 创建人： lis
 * 创建时间：2015-12-4
 */
public class SynchronizeTableStructTrans extends IBusiness {


	@Override
    public void execute() throws GeneralException {
		try
		{
			SalaryTypeBo bo=new SalaryTypeBo(this.getFrameconn(),this.userView);
			String salaryids=(String)this.getFormHM().get("salaryids");
			String errorMessage = bo.synchronizeSalaryStruct(salaryids.substring(1));
			this.getFormHM().put("errorMessage", errorMessage);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
