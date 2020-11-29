package com.hjsj.hrms.transaction.report.retport_status;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 28, 2006:2:06:13 PM</p>
 * @author xieguiquan
 * @version 1.0
 * 
 */
public class GetReportUnittreeTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String dxt = (String)hm.get("returnvalue");
		if(dxt!=null&&!"dxt".equals(dxt))
			hm.remove("returnvalue");
		if(dxt==null)
			dxt="";
		this.getFormHM().put("returnflag", dxt);
	}


}
