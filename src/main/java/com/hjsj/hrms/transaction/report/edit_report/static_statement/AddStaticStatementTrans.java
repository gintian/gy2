/**
 * 
 */
package com.hjsj.hrms.transaction.report.edit_report.static_statement;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:查询功能列表</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Oct 29, 2008:3:15:01 PM</p>
 * @author xgq
 * @version 1.0
 * 
 */
public class AddStaticStatementTrans extends IBusiness {
    /**
	 */
	
	public void execute() throws GeneralException {
	
	this.getFormHM().put("scopeid", "");
	this.getFormHM().put("scopename", "");
	
	this.getFormHM().put("scopeownerunitid", "");
	
	this.getFormHM().put("scopeownerunit","");
	this.getFormHM().put("scopeunitsids", "");
	this.getFormHM().put("scopeunits", "");

	}
	
}
