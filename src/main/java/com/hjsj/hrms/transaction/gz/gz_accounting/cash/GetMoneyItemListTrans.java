package com.hjsj.hrms.transaction.gz.gz_accounting.cash;

import com.hjsj.hrms.businessobject.gz.CashListBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class GetMoneyItemListTrans extends IBusiness{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
			String moneyid=(String)map.get("moneyid");
			CashListBo bo = new CashListBo(this.getFrameconn());
			ArrayList moneyItemList =bo.getAllMoneyItemList(moneyid);
			this.getFormHM().put("moneyItemList",moneyItemList);
			this.getFormHM().put("nmoneyid",moneyid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
