package com.hjsj.hrms.transaction.performance.batchGrade;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * <p>Title:GhostScoreTrans.java</p>
 * <p>Description:一键评分</p>
 * <p>Company:hjsj</p>
 * <p>create time:2012-06-14 11:28:36</p>
 * @author JinChunhai
 * @version 6.0
 */

public class GhostScoreTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		
		try
		{
			HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
			String plan_id = (String)hm.get("plan_id");
			String grade_id = (String)this.getFormHM().get("grade_id");
			String current = (String)this.getFormHM().get("current");   // 页数
			CheckPrivSafeBo _bo = new CheckPrivSafeBo(this.frameconn,this.userView);
			boolean flag = _bo.isPlanIdPriv(plan_id);
			if(!flag){
				return;
			}
			ArrayList object_idList = new ArrayList();
			BatchGradeBo batchGradeBo = new BatchGradeBo(this.getFrameconn(),plan_id);
			//按总分排序 wangrd 2014-12-20 bug6026
			ExamPlanBo PlanBo= new ExamPlanBo(this.frameconn,this.userView,plan_id);
            String template_id=PlanBo.getPlanVo().getString("template_id");			
            batchGradeBo.getDynaRankInfoMap(plan_id);
            batchGradeBo.getObjectInfoMap(plan_id);
            HashMap objectTotalScoreMap=batchGradeBo.getObjectTotalScore(Integer.parseInt(plan_id),this.userView.getA0100(),template_id,this.userView);
            batchGradeBo.setObjectTotalScoreMap(objectTotalScoreMap);
            
			// 登录用户需打分的考核对象集合
			ArrayList objectList = batchGradeBo.getPerPlanObjects(Integer.parseInt(plan_id), this.userView.getA0100(), Integer.parseInt(current), batchGradeBo.getScoreNumPerPage());
			/* 得到某计划考核主体给对象的评分结果hashMap */
		 // HashMap perTableMap = batchGradeBo.getPerTableXXX(Integer.parseInt(plan_id), this.userView.getA0100(), objectList);
						
		    for (Iterator t = objectList.iterator(); t.hasNext();)
		    {
				String[] temp = (String[]) t.next();
				if(temp[2]!=null && temp[2].trim().length()>0 && ("2".equalsIgnoreCase(temp[2]) || "3".equalsIgnoreCase(temp[2]))) // 已评价和不评价
					continue;
				
				LazyDynaBean abean = new LazyDynaBean();
				abean.set("object_id", temp[0]);
			//	if(temp[2]!=null && temp[2].trim().length()>0 && temp[2].equalsIgnoreCase("1")) // 正评价
			//		abean.set("a0101", temp[1]+"(已评)");
			//	else
					abean.set("a0101", temp[1]);
				abean.set("status", temp[2]);								
			//	abean.set("fillctrl", temp[3]); // 是否必打分
				
			    object_idList.add(abean);																		
		    }		    
		    
		    // 获得绩效或能力素质标准标度
			ArrayList gradeList = batchGradeBo.getGradeOrCompeDesc();
			if(SystemConfig.getPropertyValue("clientName")!=null && "gjkhxt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName").trim())) // 干警考核系统
			{
				gradeList = batchGradeBo.getGradeForgjkhxt();
			}
			
		    this.getFormHM().put("object_idList",object_idList);
		    this.getFormHM().put("gradeList",gradeList);
		    this.getFormHM().put("grade_id",grade_id);
			
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	
}