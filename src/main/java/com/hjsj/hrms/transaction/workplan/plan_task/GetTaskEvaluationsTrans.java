package com.hjsj.hrms.transaction.workplan.plan_task;

import com.hjsj.hrms.businessobject.workplan.WorkPlanBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskBo;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.Map;

/**
 * <p>Title:任务评价列表</p>
 * <p>Description:查询任务评价</p>
 * <p>Company:HJSJ</p>
 * <p>Create time:2014-10-8:下午16:11:28</p>
 * @author 刘蒙
 * @version 1.0
 */
public class GetTaskEvaluationsTrans extends IBusiness {
	
	private static final long serialVersionUID = 6502325967118820485L;

	public void execute() throws GeneralException {
		Map params = PlanTaskBo.setOutParams(formHM);
		WorkPlanUtil util = new WorkPlanUtil(frameconn, userView);
		try {
			
			// 先检查是否符合评价的条件
			PlanTaskBo bo = new PlanTaskBo(frameconn, userView);
			WorkPlanBo wpbo = new WorkPlanBo(frameconn, userView);
			
			String p0800 = (String) params.get("p0800");
			String othertask = (String) params.get("othertask");
			boolean isMySelf = false;//true是我的
			if("1".equals(othertask)){//是穿透任务
				String myP0700 = (String) params.get("myP0700");
				int IMyP0700 = "".equals(myP0700) ? 0 : Integer.parseInt(myP0700);//当前
				RecordVo p07 = wpbo.getP07Vo(IMyP0700);
				if(p07 != null && p07.getString("nbase").equals(this.userView.getDbname()) && p07.getString("a0100").equals(this.userView.getA0100())){
					isMySelf = true;
				}
				if(isMySelf){
					Map parentParams = bo.findParentNodeP(p0800);//向上查找不是穿透任务的第一个任务
					p0800 = (String)parentParams.get("p0800");
					boolean isEvaluable = bo.isEvaluable(parentParams);
					if (!isEvaluable) {
						return;
					}
				}
			}else{
				if (!bo.isEvaluable(params)) {
					return;
				}
			}
			
			String object = null; // 被查看人姓名
			String p0723 = (String) params.get("p0723");
			String objectid = (String) params.get("objectid");
			int iP0800 = "".equals(p0800) ? 0 : Integer.parseInt(p0800);
			
			String usrId = "";
			if("1".equals(p0723)){
				usrId = objectid;
			}
			if ("2".equals(p0723)) {
				usrId = util.getFirstDeptLeaders(objectid);
			}
			String p0700 = (String) params.get("p0700");//当前登录人员的工作计划id
			int iP0700 = "".equals(p0700) ? 0 : Integer.parseInt(p0700);
			wpbo.initPlan(iP0700);
			if ("1".equals(p0723)) { // 个人任务
				object = objectid;
			} else if ("2".equals(p0723)) {
				object = new WorkPlanUtil(frameconn, userView).getFirstDeptLeaders(objectid);
			} else {
				object = "";
			}
			formHM.put("object", bo.getA0101(object));
			//who指当前登录用户和被查看人的关系 self:查看的自己,  super:上级, director:任务负责人,  member:任务成员/关注人
			String who = "";
			//自己查看自己
			if (usrId.equals(userView.getDbname() + userView.getA0100())) {
				who = "self";
			} else if (wpbo.isMySubTeamPeople()) { // 上级
				who = "super";
			} else if (bo.isDirector(iP0800)) {//负责人
				who = "director";
			} else if (bo.isFollower(iP0800)|| bo.isMember(iP0800)) { // 参与人,关注人
				who = "member";
			}
			
			if("1".equals(othertask) && "director".equals(who) && isMySelf){//穿透任务，父节点为第一个非穿透任务，当前用户是是负责人或是创建者
				who = "super";//otherMember
			}
			
			formHM.put("who", who);
			//查找当前用户可见的任务评价
			params.put("who", who);
			formHM.put("visible", bo.getVisibleEvaluations(params));
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			if (frowset != null) {
				try {
					frowset.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
