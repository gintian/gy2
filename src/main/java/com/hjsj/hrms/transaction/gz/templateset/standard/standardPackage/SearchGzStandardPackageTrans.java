package com.hjsj.hrms.transaction.gz.templateset.standard.standardPackage;

import com.hjsj.hrms.businessobject.gz.templateset.SalaryStandardPackBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchGzStandardPackageTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			SalaryStandardPackBo bo=new SalaryStandardPackBo(this.getFrameconn());
			
			HashMap pamaHm=(HashMap)this.getFormHM().get("requestPamaHM");
			String returnflag=(String)pamaHm.get("returnflag"); 
			this.getFormHM().put("returnflag",returnflag);
			
			ArrayList standardPackagelist=bo.getSalaryStandardPackList();
			if(standardPackagelist.size()>0)
			{
				LazyDynaBean abean=(LazyDynaBean)standardPackagelist.get(0);
				String status=(String)abean.get("status");
				if("1".equals(status))
					this.getFormHM().put("startUpIndex","0");
				else
					this.getFormHM().put("startUpIndex","-1");
					
			}
			else
				this.getFormHM().put("startUpIndex","-1");
			this.getFormHM().put("standardPackagelist",standardPackagelist);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
