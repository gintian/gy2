package com.hjsj.hrms.transaction.kq.app_check_in.exchange_class;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class ChooseAppExchangeTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		ArrayList selectedinfolist=(ArrayList)this.getFormHM().get("infolist");
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String audit_flag=(String)hm.get("audit_flag");
		this.getFormHM().put("audit_flag",audit_flag);		
		this.getFormHM().put("selectedinfolist",selectedinfolist);
	}

}
