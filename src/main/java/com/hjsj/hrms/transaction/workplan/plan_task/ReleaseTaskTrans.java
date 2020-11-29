package com.hjsj.hrms.transaction.workplan.plan_task;

import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Map;

/**
 * <p>Title:发布任务</p>
 * <p>Description:发布除任务</p>
 * <p>Company:HJSJ</p>
 * <p>Create time:2014-8-4:下午15:11:28</p>
 * @author 刘蒙
 * @version 1.0
 */
public class ReleaseTaskTrans extends IBusiness {

	private static final long serialVersionUID = 3157510426367064522L;

	public void execute() throws GeneralException {
		Map params = PlanTaskBo.setOutParams(formHM);
		
		try {
			PlanTaskBo bo = new PlanTaskBo(frameconn, userView);
			bo.transit(params); // 发布任务(任务状态迁移)
			bo.sendEmailsToAll(params, "set"); // 发送邮件
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
