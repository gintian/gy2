package com.hjsj.hrms.transaction.workplan.plan_task;

import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:查询指定候选人</p>
 * <p>Description:根据objectId</p>
 * <p>Company:HJSJ</p>
 * <p>Create time:2014-7-28:上午10:11:28</p>
 * @author 刘蒙
 * @version 1.0
 */
public class SearchPeopleByObjectIdTrans extends IBusiness {

	private static final long serialVersionUID = -3586072572710160779L;

	public void execute() throws GeneralException {
		try {
			String objectId = (String) formHM.get("object_id"); // nbase + a0100
			if (objectId == null || "".equals(objectId.trim())) {
				return;
			}
			objectId = PubFunc.decryption(objectId);
			
			formHM.put("people", new PlanTaskBo(frameconn, userView).getPersonBean(objectId));
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
