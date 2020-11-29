package com.hjsj.hrms.transaction.workplan.plan_task;

import com.hjsj.hrms.businessobject.workplan.WorkPlanBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanOperationLogBo;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Map;

/**
 * <p>Title:取消或删除任务</p>
 * <p>Description:根据计划id取消或删除任务</p>
 * <p>Company:HJSJ</p>
 * <p>Create time:2014-7-29:下午15:11:28</p>
 * @author 刘蒙
 * @version 1.0
 */
public class CancelOrDeleteTaskTrans extends IBusiness {
	
	private static final long serialVersionUID = 6326419392174948327L;

	public void execute() throws GeneralException {
		Map params = PlanTaskBo.setOutParams(formHM);
		String p0800 = (String) params.get("p0800"); // 任务id
		String p0700 = (String) params.get("p0700"); // 计划id
		String othertask = (String) params.get("othertask"); // 是否是穿透任务
		String objectid = (String) params.get("objectid");
		try {
			PlanTaskBo bo = new PlanTaskBo(frameconn, userView);
			int superiorEdit = bo.isSuperiorEdit(params);
			//移除部门计划wusy
			if("delDeptTask".equals(formHM.get("action"))){
				new PlanTaskBo(frameconn, userView).delDeptTask(p0800, objectid);
			}
			
			if((superiorEdit >= 1 && superiorEdit <= 4) && bo.isSubCanEdit(params)){
				//是上级,获得下级在此任务中的权限,如果下级能动,上级就能动
			}else{
				if (!bo.isCreater(params) && !"delDeptTask".equals(formHM.get("action"))) { // 创建人才可以删除或取消
					throw new Exception("您没有取消或删除的权限");
				}
			}
			
			if ("cancel".equals(formHM.get("action"))) {
				bo.cancel(params);
				//记录日志  取消任务
				RecordVo p08Vo = new RecordVo("p08");
				ContentDAO dao = new ContentDAO(frameconn);
		    	p08Vo.setInt("p0800", Integer.parseInt(p0800));
		    	try {
					p08Vo = dao.findByPrimaryKey(p08Vo);
				} catch (Exception e) {
					e.printStackTrace();
				} 
				if("02".equals(p08Vo.getString("p0811")) || "03".equals(p08Vo.getString("p0811"))){
					String logcontent = "取消了任务";
					new WorkPlanOperationLogBo(frameconn, userView).addLog(Integer.parseInt(p0800), logcontent);
				}
				params.put("superiorEdit", superiorEdit);
				bo.sendEmailsToAll(params, "cancel");
			} else if ("delete".equals(formHM.get("action"))) {
//				bo.delete(params);
				p0800 = p0800+"_"+othertask;
				new WorkPlanBo(frameconn, userView).delTask(p0700, p0800,"withChild");
			}
			bo.setWorkPlanChangeFlg("true");//工作计划页面变更flg设定为true
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
