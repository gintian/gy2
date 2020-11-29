package com.hjsj.hrms.module.workplan.yearplan.transaction;

import com.hjsj.hrms.module.workplan.yearplan.businessobject.YearPlanTraceBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 年计划审批
 * @author haosl
 *
 */
@SuppressWarnings("serial")
public class YearPlanApprovalTrans extends IBusiness{
	@Override
	public void execute() throws GeneralException {
		try {
			YearPlanTraceBo traceBo = new YearPlanTraceBo(this.getFrameconn(),this.getUserView());
			HashMap hm = this.getFormHM();
			String opt = (String)hm.get("opt");
			String msg = "";
			if("0".equals(opt)){//保存季度总结
			   Integer planId = (Integer)hm.get("planId");
			   String itemId = (String)hm.get("itemId");
			   String content = (String)hm.get("content");
		       msg = traceBo.saveAchievementInfo(planId, itemId, content);
		       if(StringUtils.isNotBlank(msg))
		    	   this.getFormHM().put("msg", msg);
		       
			}else if("1".equals(opt)){//查询各个季度总结的审批状态
				Integer planId = (Integer)hm.get("planId");
				List<String> roles = traceBo.getCurrentRoleByPlan(planId);
				Map mapState = traceBo.getAchievementState(planId);
				//查询当前登录人的当前计划下的角色
				if(roles!=null)
					mapState.put("role", roles);
				this.getFormHM().put("mapState", mapState);
			}else if("2".equals(opt)){//报批
				/**
				 * 报批分两种情况
				 * 		1.负责人报审到审核人
				 * 		2.没有审核人的情况下直接报批到审批人
				 * 		3.审核人报批到审批人
				 */
				Integer planId = (Integer)hm.get("planId");
				List<String> roles = traceBo.getCurrentRoleByPlan(planId);
				
				Integer quarter = (Integer)hm.get("quarter");
				String itemId = (String)hm.get("itemId");
			    String content = (String)hm.get("content");
			    //责任人报批
				if(roles.contains("7")){
					msg = traceBo.toApprove(planId,quarter,itemId,content,false);
				}else{//审核人报批
					msg = traceBo.toApprove(planId,quarter,itemId,content,true);
				}
			  this.getFormHM().put("msg", msg);
			}else if("3".equals(opt)){//负责人撤回
				Integer planId = (Integer)hm.get("planId");
				Integer quarter = (Integer)hm.get("quarter");
				traceBo.revocation(planId, quarter);
				this.getFormHM().put("flag", "true");
			}else if("4".equals(opt)){//审批人或审核人退回
				Integer planId = (Integer)hm.get("planId");
				Integer quarter = (Integer)hm.get("quarter");
				String advice = (String)hm.get("advice");
				List<String> roles = traceBo.getCurrentRoleByPlan(planId);
				if(roles.contains("8")){
					msg = traceBo.reject(planId, quarter, advice,true);
				}else{
					//审核人报批
					msg = traceBo.reject(planId, quarter, advice,false);
				}
				this.getFormHM().put("msg", msg);
			}else if("5".equals(opt)){//审批人批准或者审核人发布
				Integer planId = (Integer)hm.get("planId");
				Integer quarter = (Integer)hm.get("quarter");
				msg = traceBo.approve(planId, quarter);
				this.getFormHM().put("msg", msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} 
		
		
	}

}
