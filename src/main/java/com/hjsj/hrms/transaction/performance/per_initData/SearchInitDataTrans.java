package com.hjsj.hrms.transaction.performance.per_initData;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/** 
 *<p>Title:SearchInitDataTrans.java</p> 
 *<p>Description:初始化绩效/能力素质数据</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jan 12, 2009</p> 
 *@author JinChunhai
 *@version 4.2
 */

public class SearchInitDataTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String busitype = (String) hm.get("busitype");			
			ArrayList tableList = new ArrayList();		
			
			if(busitype!=null && busitype.trim().length()>0 && "1".equalsIgnoreCase(busitype))
			{
				tableList.add(getBean("per_grade_competence","能力素质标准标度表"));
				tableList.add(getBean("per_pointset","素质指标分类表"));
				tableList.add(getBean("per_point","素质指标要素表"));
				tableList.add(getBean("per_grade","素质指标标度表"));
				tableList.add(getBean("per_template_set","素质模板分类表"));
				tableList.add(getBean("per_template","素质模板表"));
				tableList.add(getBean("per_template_item","素质模板项目表"));
				tableList.add(getBean("per_template_point","素质模板要素表"));
				tableList.add(getBean("per_competency_modal","岗位素质模型表"));
				
				tableList.add(getBean("per_plan","评估计划信息表"));
				tableList.add(getBean("per_object","评估对象信息表"));
				tableList.add(getBean("per_mainbody","评估主体信息表"));
				tableList.add(getBean("per_table_xxx","评估信息表"));
				tableList.add(getBean("per_result_xxx","评估结果表"));
				tableList.add(getBean("per_ScoreDetail","素质评估得分明细表"));
				tableList.add(getBean("per_object_know","了解程度统计表"));								
				
				tableList.add(getBean("per_param","评语模板表"));
                tableList.add(getBean("per_archive_schema","考核结果归档方案表"));
                
				tableList.add(getBean("per_bodyscore_xxx","主体分数,了解程度分数表"));
				tableList.add(getBean("per_bodyVote_xxx","主体选票统计表"));
				tableList.add(getBean("per_result_correct","评估结果修正表"));
				tableList.add(getBean("per_history_result","评估结果历史记录表"));
			//	tableList.add(getBean("per_pointpriv_xxx","考核主体要素权限表"));
			//	tableList.add(getBean("per_interview","面谈结果记录表"));
				tableList.add(getBean("per_objectbody_score","评估对象类别得分表"));
				tableList.add(getBean("per_object_vote","总体评价票数"));
			//	tableList.add(getBean("per_dyna_rank","考核对象要素动态权重表"));
			//	tableList.add(getBean("per_dyna_bodyrank","考核主体动态权重表"));
				tableList.add(getBean("per_plan_body","评估主体分类权重表"));
				//////////
			//	tableList.add(getBean("per_gather","业绩数据信息明细表"));
			//	tableList.add(getBean("per_gather_xxx","业绩数据信息表"));				
			//	tableList.add(getBean("per_target_list","业绩目标任务书"));
				
			//	tableList.add(getBean("per_target_mx","目标任务明细表"));
			//	tableList.add(getBean("per_target_point","目标任务书对应的要素信息表"));
			//	tableList.add(getBean("per_target_evaluation","目标评估信息表"));
			//	tableList.add(getBean("P04","工作任务信息表"));
												
			}
			else
			{
				tableList.add(getBean("per_point","考核指标要素表"));
				tableList.add(getBean("per_grade","考核指标标度表"));
				tableList.add(getBean("per_pointset","考核指标分类表"));
				tableList.add(getBean("per_template_point","模板要素表"));
				tableList.add(getBean("per_template","考核模板表"));
				tableList.add(getBean("per_template_item","模板项目表"));
				tableList.add(getBean("per_grade_template","绩效标准标度表"));
				tableList.add(getBean("per_template_set","考核模板分类表"));
				
				tableList.add(getBean("per_plan","考核计划信息表"));
				tableList.add(getBean("per_object","考核对象信息表"));
				tableList.add(getBean("per_mainbody","考核主体信息表"));
				tableList.add(getBean("per_table_xxx","考核信息表"));
				tableList.add(getBean("per_result_xxx","考核结果表"));
				tableList.add(getBean("per_ScoreDetail","绩效评估得分明细表"));
				tableList.add(getBean("per_object_know","了解程度统计表"));
				
				tableList.add(getBean("per_article","绩效报告信息表"));
                tableList.add(getBean("per_param","评语模板表"));
                tableList.add(getBean("per_key_event","关键事件信息表"));
                tableList.add(getBean("per_appraise","考核综合评价表"));
				
				tableList.add(getBean("per_bodyscore_xxx","主体分数,了解程度分数表"));
				tableList.add(getBean("per_bodyVote_xxx","主体选票统计表"));
				tableList.add(getBean("per_result_correct","考核结果修正表"));
				tableList.add(getBean("per_history_result","考核结果历史记录表"));
				tableList.add(getBean("per_pointpriv_xxx","考核主体要素权限表"));
				tableList.add(getBean("per_interview","面谈结果记录表"));
				tableList.add(getBean("per_objectbody_score","考核对象类别得分表"));
				tableList.add(getBean("per_object_vote","总体评价票数"));
				tableList.add(getBean("per_dyna_rank","考核对象要素动态权重表"));
				tableList.add(getBean("per_dyna_bodyrank","考核主体动态权重表"));
				tableList.add(getBean("per_plan_body","考核主体分类权重表"));
				//////////
				tableList.add(getBean("per_gather","业绩数据信息明细表"));
				tableList.add(getBean("per_gather_xxx","业绩数据信息表"));				
				tableList.add(getBean("per_target_list","业绩目标任务书"));
				
				tableList.add(getBean("per_target_mx","目标任务明细表"));
				tableList.add(getBean("per_target_point","目标任务书对应的要素信息表"));
				tableList.add(getBean("per_target_evaluation","目标评估信息表"));
				tableList.add(getBean("P04","工作任务信息表"));
				tableList.add(getBean("OrgPointTable","组织机构考核指标表"));
			}						
			
			this.getFormHM().put("tableList",tableList);
			this.getFormHM().put("busitype",busitype);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
		
	public LazyDynaBean getBean(String id,String desc)
	{
		LazyDynaBean abean=new LazyDynaBean();
		abean.set("id",id);
		abean.set("desc",desc);
		return abean;
	}

}
