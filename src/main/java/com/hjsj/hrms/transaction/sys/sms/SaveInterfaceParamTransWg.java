package com.hjsj.hrms.transaction.sys.sms;

import com.hjsj.hrms.businessobject.sys.Sms_Parameter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveInterfaceParamTransWg extends IBusiness {

	public void execute() throws GeneralException {
		
		String userName = (String) this.getFormHM().get("userName");
		String password = (String) this.getFormHM().get("password");
		String service = (String) this.getFormHM().get("service");
		String upUrl = (String) this.getFormHM().get("upUrl");
		String downUrl = (String) this.getFormHM().get("downUrl");
		String channelId = (String) this.getFormHM().get("channelId");
		String qy = (String) this.getFormHM().get("qy");
		String spname = (String) this.getFormHM().get("spname");

		try {
			Sms_Parameter sparam = new Sms_Parameter(this.getFrameconn());
			sparam.saveGateway(service, userName, password, upUrl, downUrl,
					channelId, qy, spname);
		} catch (Exception ex) {
	
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
