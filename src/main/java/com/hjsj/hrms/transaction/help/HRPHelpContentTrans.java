/**
 * 
 */
package com.hjsj.hrms.transaction.help;

import com.hjsj.hrms.interfaces.help.HRPHelp;
import com.hjsj.hrms.interfaces.help.HRPHelpXmlAnalyse;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Aug 13, 2006:5:17:42 PM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class HRPHelpContentTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm =(HashMap)(this.getFormHM().get("requestPamaHM"));
		String help_id = (String)hm.get("helpid");	
		HRPHelpXmlAnalyse hhxa = new HRPHelpXmlAnalyse();
		hhxa.init();
		HRPHelp hh = hhxa.getHRPHelp(help_id);
		String url = hh.getHelp_url();
		if(url == null){
			this.getFormHM().put("url","");
		}else{
			this.getFormHM().put("url",url);
		}
	}

}
