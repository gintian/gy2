package com.hjsj.hrms.transaction.gz.templateset.standard.standardPackage;

import com.hjsj.hrms.businessobject.gz.templateset.SalaryStandardPackBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:启动工资标准包</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jul 6, 2007:3:35:34 PM</p> 
 *@author dengcan
 *@version 4.0
 */
public class StartGzStandardPackageTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String startDate=(String)this.getFormHM().get("startDate");
			ArrayList list=(ArrayList)this.getFormHM().get("selectedList");
			LazyDynaBean abean=(LazyDynaBean)list.get(0);
			
			SalaryStandardPackBo bo=new SalaryStandardPackBo(this.getFrameconn());
			bo.startSalaryStandardPack((String)abean.get("pkg_id"),startDate.trim());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		
		

	}

}
