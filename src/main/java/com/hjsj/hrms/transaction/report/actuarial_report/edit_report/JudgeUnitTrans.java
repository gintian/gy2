/*
 * Created on 2006-3-27
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.report.actuarial_report.edit_report;


import com.hjsj.hrms.businessobject.report.actuarial_report.ActuarialReportBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class JudgeUnitTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		String unitcode=(String)this.getFormHM().get("unitcode");
		ActuarialReportBo ab=new ActuarialReportBo(this.getFrameconn(),this.getUserView());
		this.getFormHM().put("isCollectUnit", ab.isCollectUnit(unitcode));
		
	}

}
