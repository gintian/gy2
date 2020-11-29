package com.hjsj.hrms.transaction.gz.templateset.standard.standardPackage;

import com.hjsj.hrms.businessobject.gz.templateset.SalaryStandardPackBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:取得当前历史沿革标准</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jul 10, 2007:9:15:21 AM</p> 
 *@author dengcan
 *@version 4.0
 */
public class GeCurrentStandardTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			SalaryStandardPackBo bo=new SalaryStandardPackBo(this.getFrameconn());
			ArrayList standardList=bo.GetCurrentStandardList();
			this.getFormHM().put("currentStandardList",standardList);
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		

	}

}
