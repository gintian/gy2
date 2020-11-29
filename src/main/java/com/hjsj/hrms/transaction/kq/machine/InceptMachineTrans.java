package com.hjsj.hrms.transaction.kq.machine;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class InceptMachineTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		String machine_num=(String)this.getFormHM().get("machine_num");
		String machine_data=(String)this.getFormHM().get("machine_data");
		String cardno_len=(String)this.getFormHM().get("cardno_len");
	    this.getFormHM().put("machine_num", machine_num);
	    this.getFormHM().put("machine_data", machine_data);
	    this.getFormHM().put("cardno_len", cardno_len);
	}

}
