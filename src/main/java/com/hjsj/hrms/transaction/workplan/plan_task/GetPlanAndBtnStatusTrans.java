package com.hjsj.hrms.transaction.workplan.plan_task;

import com.hjsj.hrms.businessobject.workplan.WorkPlanBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskBo;
import com.hjsj.hrms.businessobject.workplan.plan_task.PlanTaskTreeTableBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.List;

/**
 * 
 * <p>Title:GetPlanTaskDataTrans.java</p>
 * <p>Description:获得计划任务表数据</p> 
 * <p>Company:hjsj</p> 
 * create time at:2014-7-11 上午09:58:03 
 * @author dengcan
 * @version 6.x
 */
public class GetPlanAndBtnStatusTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try {
			String p0700=WorkPlanUtil.decryption(this.getFormHM().get("p0700")!=null?(String)this.getFormHM().get("p0700"):"");  //计划id
			String p0723=WorkPlanUtil.decryption(this.getFormHM().get("p0723")!=null?(String)this.getFormHM().get("p0723"):"");  //计划类型 1：人员计划  2：团队计划  3：项目
			String p0725=WorkPlanUtil.decryption(this.getFormHM().get("p0725")!=null?(String)this.getFormHM().get("p0725"):"");  //计划类型 1、年度 2:半年  3：季度  4：月份  5：周
			String p0727=this.getFormHM().get("p0727")!=null?(String)this.getFormHM().get("p0727"):"0";  //计划年
			String p0729=this.getFormHM().get("p0729")!=null?(String)this.getFormHM().get("p0729"):"0";  //计划月份、季度
			String p0731=this.getFormHM().get("p0731")!=null?(String)this.getFormHM().get("p0731"):"0";  //计划月份、季度
			String object_id=WorkPlanUtil.decryption(this.getFormHM().get("object_id")!=null?(String)this.getFormHM().get("object_id"):""); //对象id
			String showType=this.getFormHM().get("showType")!=null?(String)this.getFormHM().get("showType"):"1";  //任务列表视图类型  1：计划制定   2：计划跟踪
			
			if(p0700.length()==0)
				p0700="0";
			if(p0725.length()==0)
				p0725="1";

			PlanTaskTreeTableBo planBo=new PlanTaskTreeTableBo(this.getFrameconn(),Integer.parseInt(p0700),this.getUserView());
			if ("0".equals(p0700)){
			    try {
			        planBo.setP0727(Integer.parseInt(p0727));
			        planBo.setP0729(Integer.parseInt(p0729));
			        planBo.setP0731(Integer.parseInt(p0731));
                } catch (Exception e) {
                }
			}
			// 计划内如果包含已变更的任务，则出现“重新发布”按钮 lium
			List beans = planBo.getTableData(""); //获得计划下的任务数据
			PlanTaskBo ptbo = new PlanTaskBo(frameconn, userView);
			WorkPlanBo wpbo = new WorkPlanBo(frameconn, userView);
			wpbo.initPlan(Integer.parseInt(p0700));
			formHM.put("needToRepublish", "false");//开始将其定义为false(修正重新发布和发布按钮同时出现wusy)
			formHM.put("planChangeStatus", ""); 
			if (wpbo.isMyPlan()) { // 查看本人的计划时，寻找需要报批的任务 lium
				for (int i = 0, len = beans.size(); i < len; i++) {
					LazyDynaBean bean = (LazyDynaBean) beans.get(i);
					
					int planId = Integer.parseInt(p0700);
					if (planId==0) break;//如果planId为0,跳出(修正重新发布和发布按钮同时出现wusy)
					int taskId = Integer.parseInt((String) bean.get("p0800"));
					if (ptbo.ifNeedToPublish(planId, taskId)) {
						formHM.put("needToRepublish", "true"); // 是否需要重新发布
						break;
					}
				}
				
				
				for (int i = 0, len = beans.size(); i < len; i++) {
					LazyDynaBean bean = (LazyDynaBean) beans.get(i);
					
					int planId = Integer.parseInt(p0700);
					if (planId==0) break;//如果planId为0,跳出(修正重新发布和发布按钮同时出现wusy)
					int taskId = Integer.parseInt((String) bean.get("p0800"));
					if (ptbo.isTaskChanged(planId, taskId)) {
						formHM.put("planChangeStatus", "已变更"); 
						break;
					}
				}
			}
			formHM.put("needToApprove", "false");
			//if (wpbo.isMySubTeamPeople()) { // 查看下级的计划时，寻找需要批准的任务
			if (wpbo.isSameToMyTeamPlan()) { // 查看下级的计划时，寻找需要批准的任务
				for (int i = 0; i < beans.size(); i++) {
					LazyDynaBean bean = (LazyDynaBean) beans.get(i);
					
					int planId = Integer.parseInt(p0700);
					int taskId = Integer.parseInt((String) bean.get("p0800"));
					//if (ptbo.ifNeedToApprove(planId, taskId)) {
					if (ptbo.ifNeedToApproveForPlan(planId, taskId)) {
						formHM.put("needToApprove", "true"); // 是否需要批准
						break;
					}
				}
			}
		}  catch(Exception ee)
        {
        	ee.printStackTrace();
        	throw GeneralExceptionHandler.Handle(ee);
        }
	}

	
	
}
