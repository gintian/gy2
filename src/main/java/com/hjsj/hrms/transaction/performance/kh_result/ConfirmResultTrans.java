package com.hjsj.hrms.transaction.performance.kh_result;

import com.hjsj.hrms.businessobject.performance.kh_result.ResultBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class ConfirmResultTrans extends IBusiness{


	public void execute() throws GeneralException {
		try
		{
			String plan_id=(String)this.getFormHM().get("plan_id");
			plan_id = PubFunc.decrypt(plan_id);
			String object_id=(String)this.getFormHM().get("object_id");
			object_id = PubFunc.decrypt(object_id);
			String selectTabId=(String)this.getFormHM().get("selectTabId");
			selectTabId = PubFunc.decrypt(selectTabId);
			String personOrTeamType=(String)this.getFormHM().get("personOrTeamType");
			personOrTeamType = PubFunc.decrypt(personOrTeamType);
			ResultBo bo = new ResultBo(this.getFrameconn());
			bo.ConfirmFlag(plan_id, object_id,personOrTeamType);
			this.getFormHM().put("selectTabId", selectTabId);
			this.getFormHM().put("personOrTeamType", personOrTeamType);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
