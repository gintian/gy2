package com.hjsj.hrms.transaction.general.approve.personinfo;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SumTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		Sys_Oth_Parameter othparam = new Sys_Oth_Parameter(this.getFrameconn());
		String inputchinfor=othparam.getValue(Sys_Oth_Parameter.INPUTCHINFOR);
		inputchinfor=inputchinfor!=null&&inputchinfor.trim().length()>0?inputchinfor:"1";//如果1：不直接入库；0为直接入库
		String ff=(String)this.getFormHM().get("ff");
		String checked_may_reject=SystemConfig.getPropertyValue("checked_may_reject");//审核后可驳回 2010.01.08 s.xin加
		checked_may_reject=checked_may_reject!=null&&checked_may_reject.trim().length()>0?checked_may_reject:"false";
		this.getFormHM().put("ff", ff);
		this.getFormHM().put("inputchinfor",inputchinfor);
		this.getFormHM().put("checked_may_reject", checked_may_reject);
		
	}

}
