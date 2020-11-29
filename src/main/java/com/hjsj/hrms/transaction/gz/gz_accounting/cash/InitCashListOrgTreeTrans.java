package com.hjsj.hrms.transaction.gz.gz_accounting.cash;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class InitCashListOrgTreeTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
			String priv=(String)this.getFormHM().get("priv");
			this.getFormHM().put("priv",priv);
			String salaryid=(String)this.getFormHM().get("salaryid");
			String showUnitCodeTree="0";  //是否按操作单位来显示树
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			showUnitCodeTree=gzbo.getControlByUnitcode();
			this.getFormHM().put("showUnitCodeTree", showUnitCodeTree);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
