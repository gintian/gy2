package com.hjsj.hrms.transaction.gz.templateset.moneystyle;

import com.hjsj.hrms.businessobject.gz.templateset.moneystyle.MoneyStyleSetBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class InitMoneyStyleTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			ArrayList moneyStyleList= new ArrayList();
			MoneyStyleSetBo bo= new MoneyStyleSetBo(this.getFrameconn());
			moneyStyleList=bo.getMoneyStyleList();
			this.getFormHM().put("moneyList",moneyStyleList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
