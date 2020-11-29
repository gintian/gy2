package com.hjsj.hrms.transaction.workplan.plan_task;

import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.Map;

/**
 * <p>Title:任务状态迁移</p>
 * <p>Description:任务状态迁移</p>
 * <p>Company:HJSJ</p>
 * <p>Create time:2014-9-23:上午9:11:28</p>
 * @author 刘蒙
 * @version 1.0
 */
public class TaskStatusTransitTrans extends IBusiness {

	private static final long serialVersionUID = 3157510426367064522L;

	public void execute() throws GeneralException {
		Map params = PlanTaskBo.setOutParams(formHM);
		try {
			PlanTaskBo bo = new PlanTaskBo(frameconn, userView);
			bo.transit(PlanTaskBo.setOutParams(formHM));
			
			LazyDynaBean bean = bo.privilege(params);
			formHM.put("privilege", bean);
			bo.setWorkPlanChangeFlg("true");//工作计划页面变更flg设定为true
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
