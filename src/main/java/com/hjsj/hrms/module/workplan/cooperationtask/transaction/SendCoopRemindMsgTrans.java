package com.hjsj.hrms.module.workplan.cooperationtask.transaction;

import com.hjsj.hrms.module.workplan.cooperationtask.businessobject.CooperationTaskBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SendCoopRemindMsgTrans extends IBusiness{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void execute() throws GeneralException {
		ArrayList<String> al = (ArrayList)this.getFormHM().get("selectedArr");
		CooperationTaskBo bo = new CooperationTaskBo(this.frameconn,this.userView);
		bo.sendPending_cooperationTask(al);
	}
	
}
