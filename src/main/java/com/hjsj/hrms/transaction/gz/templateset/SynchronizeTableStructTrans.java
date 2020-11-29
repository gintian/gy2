package com.hjsj.hrms.transaction.gz.templateset;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SynchronizeTableStructTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			SalaryTemplateBo bo=new SalaryTemplateBo(this.getFrameconn());
			bo.setUserview(this.userView);
			String salaryids=(String)this.getFormHM().get("salaryids");
			bo.synchronizeSalaryStruct(salaryids);
			
			/* 薪资类别-结构同步 没有同步归档表修正 xiaoyun 2014-9-30 start */
//			HistoryDataBo hisBo = new HistoryDataBo(this.getFrameconn(),"");
//			hisBo.syncSalaryarchiveStrut();
			/* 薪资类别-结构同步 没有同步归档表修正 xiaoyun 2014-9-30 end */
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
