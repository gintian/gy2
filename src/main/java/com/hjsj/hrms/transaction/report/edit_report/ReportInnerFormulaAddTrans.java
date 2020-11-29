/**
 * 
 */
package com.hjsj.hrms.transaction.report.edit_report;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Aug 15, 2006:4:56:06 PM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ReportInnerFormulaAddTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm =(HashMap)(this.getFormHM().get("requestPamaHM"));
		String tabid = (String)hm.get("tabid");
		this.getFormHM().put("tabid",tabid);
		this.getFormHM().put("expid", "");		
		this.getFormHM().put("cname" ,"");
		this.getFormHM().put("lexpr","");
		this.getFormHM().put("rexpr","");
		this.getFormHM().put("colrow","");
		this.getFormHM().put("excludeexpr","");
		//this.getFormHM().put("tabid","");
		
		this.getFormHM().put("returnflag",(String)hm.get("returnflag"));
		this.getFormHM().put("status",(String)hm.get("status"));
		
	}

}
