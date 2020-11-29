package com.hjsj.hrms.transaction.workplan.plan_task;

import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <p>Title:查询候选人</p>
 * <p>Description:根据关键字，从姓名(汉字)、姓名(拼音)和邮箱中查找匹配的项</p>
 * <p>Company:HJSJ</p>
 * <p>Create time:2014-7-28:上午10:11:28</p>
 * @author 刘蒙
 * @version 1.0
 */
public class SearchCandidateByKeywordTrans extends IBusiness {

	private static final long serialVersionUID = 6502325967118820485L;

	public void execute() throws GeneralException {
		Map params = PlanTaskBo.setOutParams(formHM);
		
		String p0800 = (String) params.get("p0800"); // 任务id
		String keyword = (String) formHM.get("keyword"); // 关键字
		String staff = (String) formHM.get("staff"); // director(负责人), member(参与人), follower(关注人), subtask(子任务)
		
		int type = 0; // 人员在任务中的身份，与P09.p0905对应
		if ("director".equals(staff)) {
			type = 1;
		} else if ("member".equals(staff)) {
			type = 2;
		} else if ("follower".equals(staff)) {
			type = 3;
		} else if ("subtask".equals(staff)) {
			type = 100; // 子任务，不在p09表中
		} else {
			return;
		}
		
		// 查询需要排除的人员id
		String sql = null;
		switch (type) {
			case 1: {
				sql = "SELECT * FROM P09 WHERE p0905 IN (1,2) AND p0903=? AND p0901=2";
				break;
			}
			case 2: {
				sql = "SELECT * FROM P09 WHERE p0905 IN (1,2) AND p0903=? AND p0901=2";
				break;
			}
			case 3: {
				sql = "SELECT * FROM P09 WHERE p0903=? AND p0901=2";
				break;
			}
		}
		//父任务和子任务的负责人参与人是没有任何关系的，在此处无需排除。之后可能会添加当前任务的创建人不能指派任务给其上级的逻辑
		//sql = "SELECT * FROM P09 WHERE p0903=? AND p0901=2 "; // 临时修改: 任务成员不能重复

		List candidates = null; // 符合条件的候选名单
		StringBuffer excludeIds = new StringBuffer(); // 排除的id
        try {
        	if (sql != null) {
	        	ContentDAO dao = new ContentDAO(frameconn);
	        	Integer iP0800 = new Integer(p0800);
	        	frowset = dao.search(sql, Arrays.asList(new Object[] {iP0800}));
	        	while (frowset.next()) {
	        		excludeIds.append(frowset.getString("nbase")).append(frowset.getString("a0100")).append(",");
	        	}
        	}
        	
        	candidates = new WorkPlanUtil(frameconn, userView).getCandidateByKeyword(keyword, excludeIds.toString());
        	formHM.put("candidates", candidates == null ? Collections.EMPTY_LIST : candidates);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
