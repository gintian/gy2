package com.hjsj.hrms.transaction.performance.nworkplan.season;

import com.hjsj.hrms.businessobject.performance.nworkplan.season.NewWorkPlanBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class CommitInfoTrans extends IBusiness{

	public void execute() throws GeneralException {
		String p0100 = (String)this.getFormHM().get("p0100");
		NewWorkPlanBo bo = new NewWorkPlanBo(this.frameconn , this.userView);
		String isok = "提交失败!";
		if(bo.commitInfo(p0100)){
			isok = "提交成功!";
		}
		
		this.getFormHM().put("isok", isok);
	}
	
}
