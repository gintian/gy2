package com.hjsj.hrms.transaction.report.edit_report.send_receive_report;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SendReceiveTrans  extends IBusiness {

	public SendReceiveTrans() {
		super();
	}
	
	public void execute() throws GeneralException {
		String[] tabid = (String[])this.getFormHM().get("tabids");
		for (int i = 0; i < tabid.length; i++) {
			System.out.println(tabid[i]);	
		}
		
	}

}
