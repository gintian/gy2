package com.hjsj.hrms.transaction.performance;

import com.hjsj.hrms.interfaces.performance.PerMainBody;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveSubmitScoreTrans extends IBusiness {

	public void execute() throws GeneralException {
		String planId=null;
		if (this.getFormHM().get("planNum") != null) 
			planId = this.getFormHM().get("planNum").toString();
		else 
			return;
		/**保存打分提交状态*/		
		PerMainBody permainbody=new PerMainBody(this.getFrameconn());
		permainbody.updateEditStatus(userView.getA0100(),userView.getA0100(),planId,"2");
		this.getFormHM().put("status","2");
	}

}
