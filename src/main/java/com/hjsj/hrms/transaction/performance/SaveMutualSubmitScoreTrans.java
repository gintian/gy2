package com.hjsj.hrms.transaction.performance;

import com.hjsj.hrms.interfaces.performance.PerMainBody;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveMutualSubmitScoreTrans extends IBusiness {

	public void execute() throws GeneralException {
		String objectId=null;
		String planId=null;
		if (this.getFormHM().get("planNum") != null) 
			planId = this.getFormHM().get("planNum").toString();
		else 
			return;
		if(this.getFormHM().get("objectId")!=null)
			 objectId=this.getFormHM().get("objectId").toString();
		else
			return;
		/**保存打分提交状态*/		
		PerMainBody permainbody=new PerMainBody(this.getFrameconn());
		permainbody.updateEditStatus(objectId,userView.getA0100(),planId,"2");
		this.getFormHM().put("status","2");		
	}

}
