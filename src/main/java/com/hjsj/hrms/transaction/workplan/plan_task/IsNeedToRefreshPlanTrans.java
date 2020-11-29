package com.hjsj.hrms.transaction.workplan.plan_task;

import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskBo;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskTreeTableBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * <p>Title:GetPlanTaskDataTrans.java</p>
 * <p>Description:刷新工作计划</p> 
 * <p>Company:hjsj</p> 
 * create time at:2015-05-11 上午09:58:03 
 * @author ct
 * @version 7.x
 */
public class IsNeedToRefreshPlanTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = this.getFormHM();
		String p0700 = (String) hm.get("p0700");// 工作计划id
		ArrayList rowData = new ArrayList();
		String showType=this.getFormHM().get("showType")!=null?(String)this.getFormHM().get("showType"):"1";  //任务列表视图类型  1：计划制定   2：计划跟踪
		String shwoSubTask = (String)this.getFormHM().get("exportSubTask");
		// lis 20160317 start
		if(StringUtils.isNotBlank(p0700)){
			p0700 = WorkPlanUtil.decryption(p0700);
			String p0800 = (String) hm.get("p0800");// 任务id
			p0800 = WorkPlanUtil.decryption(p0800);
			String p0723 = (String) hm.get("p0723");// 工作计划id
			p0723 = WorkPlanUtil.decryption(p0723);
			
			PlanTaskTreeTableBo taskTreeBo=new PlanTaskTreeTableBo(this.getFrameconn(),Integer.parseInt(p0700),this.userView);
			ArrayList headList = taskTreeBo.getHeadList(Integer.valueOf(showType),Integer.valueOf(p0723)); //获得计划任务列表表头指标 （1阶段固定表头  后期可配置）
			ArrayList columnList = taskTreeBo.getColumnList(headList);
			rowData =taskTreeBo.getRowData(columnList,p0800, Integer.valueOf(p0723),shwoSubTask);
		}
		// lis 20160317  end
		/*formHM.put("isRefreshWorkPlan", ""); // 是否需要重新刷新工作计划页面
		String workPlanChangeFlg = (String) userView.getHm().get("workPlanChangeFlg");
		
		if("true".equals(workPlanChangeFlg)){
		}*/
		String recordId = (String) hm.get("recordId");// 工作计划id
		formHM.put("recordId", recordId); // 是否需要重新刷新
		formHM.put("isRefreshWorkPlan", "true"); // 是否需要重新刷新
		formHM.put("rowData", rowData); // 是否需要重新刷新
		
		PlanTaskBo ptbo = new PlanTaskBo(frameconn, userView);
		ptbo.setWorkPlanChangeFlg("");//重置工作计划页面变更flg
	}
}
