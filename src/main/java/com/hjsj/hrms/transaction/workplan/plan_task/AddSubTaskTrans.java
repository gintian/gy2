package com.hjsj.hrms.transaction.workplan.plan_task;

import com.hjsj.hrms.businessobject.workplan.WorkPlanConstant;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskBo;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskTreeTableBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.Map;

/**
 * 
 * <p>Title:AddSubTaskTrans.java</p>
 * <p>Description:添加子任务</p> 
 * <p>Company:hjsj</p> 
 * create time at:2014-7-24 下午16:33:03 
 * @author 刘蒙
 * @version 6.x
 */
public class AddSubTaskTrans extends IBusiness {

	private static final long serialVersionUID = -9185778914423701532L;

	public void execute() throws GeneralException {
		Map params = PlanTaskBo.setOutParams(formHM);
		String p0700 = (String) params.get("p0700"); // 计划id
		String taskName = formHM.get("taskName")!= null ? (String) formHM.get("taskName") : "";  // 子任务名称
		String p0813 = SafeCode.decode(formHM.get("p0813") != null  ? (String) formHM.get("p0813") : ""); // 开始时间
		String p0815 = SafeCode.decode(formHM.get("p0815") != null  ? (String) formHM.get("p0815") : ""); // 结束时间
		String director = WorkPlanUtil.decryption(formHM.get("director")!= null ? (String) formHM.get("director") : "");  // 负责人id
		String task_seq = (String) formHM.get("task_seq"); 
		if(director.length() == 0) {
			director = userView.getDbname() + userView.getA0100();
		}
		
		params.put("taskName", SafeCode.decode(taskName));
		params.put("p0813", p0813);
		params.put("p0815", p0815);
		params.put("director", director);

		try {
			PlanTaskBo ptbo = new PlanTaskBo(frameconn, userView);
			RecordVo subtask = ptbo.addSubtask(params);
			formHM.put("clearIDs", (String)params.get("clearIDs"));//清除上下级权重的相关任务id lis  20160321
			/* ################################ 给任务成员发送邮件 ################################ */
			if (WorkPlanConstant.TaskStatus.APPROVE.equals(subtask.getString("p0811"))) { // 子任务处于发布状态
				params.put("p0800", subtask.getString("p0800"));
				ptbo.sendEmailsToAll(params, "set"); // 发送邮件
			}
			
			LazyDynaBean bean = new LazyDynaBean();
			bean.set("subtaskName", taskName);
			
			// 用户名
			bean.set("director", ptbo.getA0101(director));
			bean.set("p0813", p0813.replaceAll("\\.", "-"));
			bean.set("p0815", p0815.replaceAll("\\.", "-"));
			PlanTaskTreeTableBo bo = new PlanTaskTreeTableBo(frameconn, Integer.parseInt(p0700));
			bean.set("timeDesc", bo.getTimeArrangeText(bean));
			bean.set("url", ptbo.getTaskUrl(formHM, subtask.getString("p0800")));
			
			
			//**计划页面实时显示添加后的子任务**start**
			PlanTaskTreeTableBo taskTreeBo=new PlanTaskTreeTableBo(this.frameconn,Integer.parseInt(p0700),this.userView);
			String sumRank = taskTreeBo.getSumRank();
			String info = taskTreeBo.getRowJson(subtask.getString("p0800"),task_seq,taskTreeBo.getP07_vo().getInt("p0723"));
			/*info = info = "{"
				+ quotedValue("rowinfo")
				+ ":"
				+ info
				+ ","
				+ quotedValue("p0700")
				+ ":"
				+ quotedValue(WorkPlanUtil.encryption(p0700)) + ","
				+ quotedValue("sum_rank") + ":"
				+ quotedValue(sumRank) + "}";*/
			//**计划页面实时显示添加后的子任务**end**
			formHM.put("subtask", bean);
			ptbo.setWorkPlanChangeFlg("true");//工作计划页面变更flg设定为true
			//formHM.put("info", SafeCode.encode(info));
			info = SafeCode.encode(info);
			formHM.put("rowinfo", info);
			formHM.put("p0700", WorkPlanUtil.encryption(p0700));
			formHM.put("sum_rank", sumRank);
			formHM.put("clearIDs", (String)params.get("clearIDs"));
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	private String quotedValue(String value) {
		String str = WorkPlanUtil.quotedDoubleValue(value);

		return str;
	}
	
}
