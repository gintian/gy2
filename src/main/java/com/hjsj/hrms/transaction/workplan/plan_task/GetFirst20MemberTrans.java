package com.hjsj.hrms.transaction.workplan.plan_task;

import com.hjsj.hrms.businessobject.workplan.WorkPlanConstant;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.*;

/**
 * <p>Title:选择最多20位参与人</p>
 * <p>Description:从参与人中选择最多20位作为负责人的候选人</p>
 * <p>Company:HJSJ</p>
 * <p>Create time:2014-9-18:下午16:11:28</p>
 * @author 刘蒙
 * @version 1.0
 */
public class GetFirst20MemberTrans extends IBusiness {

	private static final long serialVersionUID = 6502325967118820485L;

	public void execute() throws GeneralException {
		Map params = PlanTaskBo.setOutParams(formHM);
		
		String p0800 = (String) params.get("p0800"); // 任务id
		Integer iP0800 = new Integer(p0800);
		
		List members = new ArrayList(); // 都是参与人
		
		String sql = "SELECT * FROM P09 WHERE p0905 IN (2,3) AND p0903=? AND p0901=2";
        try {
        	ContentDAO dao = new ContentDAO(frameconn);
        	frowset = dao.search(sql, Arrays.asList(new Object[] {iP0800}));
        	PlanTaskBo bo = new PlanTaskBo(frameconn, userView);
        	while (frowset.next()) {
        		members.add(bo.getPersonBean(frowset.getString("nbase") + frowset.getString("a0100")));
				
				if (members.size() == WorkPlanConstant.TaskInfo.MAX_CANDIDATE_NUMBER) { // 已经拿到了足够的候选人
					break;
				}
        	}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
        
		formHM.put("members", members == null ? Collections.EMPTY_LIST : members);
	}

}
