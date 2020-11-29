package com.hjsj.hrms.transaction.kq.app_check_in.exchange_class;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SelectAppExchangeTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		ArrayList selectedinfolist=(ArrayList)this.getFormHM().get("selectedinfolist");
		this.getFormHM().put("infolist",selectedinfolist);
	}


}
