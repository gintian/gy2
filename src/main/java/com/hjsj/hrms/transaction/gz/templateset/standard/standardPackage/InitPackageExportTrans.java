package com.hjsj.hrms.transaction.gz.templateset.standard.standardPackage;

import com.hjsj.hrms.businessobject.gz.templateset.SalaryStandardPackBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class InitPackageExportTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			ArrayList list=(ArrayList)this.getFormHM().get("selectedList");
			SalaryStandardPackBo bo=new SalaryStandardPackBo(this.getFrameconn(),this.userView);
			ArrayList gzStandardPackageInfo=bo.getGzStandardPackageInfo(list);
			this.getFormHM().put("gzStandardPackageInfo",gzStandardPackageInfo);
			
		
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
