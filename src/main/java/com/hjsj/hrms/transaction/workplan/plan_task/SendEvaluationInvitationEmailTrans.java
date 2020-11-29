package com.hjsj.hrms.transaction.workplan.plan_task;

import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskBo;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title:发送任务评价邀请</p>
 * <p>Description:发送任务评价邀请</p>
 * <p>Company:HJSJ</p>
 * <p>Create time:2014-10-13:下午15:11:28</p>
 * @author 刘蒙
 * @version 1.0
 */
public class SendEvaluationInvitationEmailTrans extends IBusiness {

	private static final long serialVersionUID = 3157510426367064522L;

	public void execute() throws GeneralException {
		Map params = PlanTaskBo.setOutParams(formHM);
		
		String p0800 = (String) params.get("p0800"); // 任务id
		String objectid = (String) params.get("objectid"); // 对象id: usr00000019
		String p0723 = (String) params.get("p0723"); // 计划类型 1：人员计划  2：团队计划  3：项目
		
		String evaluatorId = (String) formHM.get("evaluatorid");
		evaluatorId = WorkPlanUtil.decryption(evaluatorId);
		try {
			PlanTaskBo bo = new PlanTaskBo(frameconn, userView);
			
			RecordVo task = bo.getTask(Integer.parseInt(p0800));
			
			LazyDynaBean body = new LazyDynaBean();
			body.set("target", bo.getA0101(evaluatorId));
			
			StringBuffer object = new StringBuffer(); // 被评价人或部门
			if ("1".equals(p0723)) {
				object.append(bo.getA0101(objectid));
			} else if ("2".equals(p0723)) {
				String _fzr = new WorkPlanUtil(frameconn, userView).getFirstDeptLeaders(objectid);
				object.append(bo.getA0101(_fzr)).append("负责的");
				object.append(AdminCode.getCodeName("UM", objectid));
			}
			body.set("object", object.toString());
			
			body.set("taskName", task.getString("p0801"));
			body.set("operator", userView.getUserFullName());
			String bodyText = PlanTaskBo.getBodyText(body, PlanTaskBo.getTplOfTaskEvaluation());
			String subject =userView.getUserFullName()+"邀请你对"+ object.toString()
			+"的\""+task.getString("p0801")+"\"任务完成情况进行评价";
			
			LazyDynaBean email = new LazyDynaBean();
			email.set("objectId", evaluatorId);
			email.set("subject", subject);
			email.set("bodyText", bodyText);
			email.set("hrefDesc", "去评价");
			email.set("bodySubject", "任务评价邀请通知");

			Map p = new HashMap();
			p.putAll(params);
			p.put("from", "evaluate"); // 发送评价邀请的邮件，区别于指定负责人、参与人等的邮件
			p.put("logonUser", evaluatorId); // 接收者id
			email.set("href", bo.getHref(p) + "&needEvaluate=true");
			
			bo.send(email, task.getString("p0811"));
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
