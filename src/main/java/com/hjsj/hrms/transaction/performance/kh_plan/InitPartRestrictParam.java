package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:InitPartRestrictParam.java</p>
 * <p>Description:最高标度对象数--部分指标分别设置</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-09-08 11:11:11</p> 
 * @author JinChunhai
 * @version 1.0
 */

public class InitPartRestrictParam extends IBusiness
{

	public void execute() throws GeneralException
	{
		
		// list页面操作的 会根据选中的模板有个初始化 detail页面初始化为一个空的ArrayList然后在此处根据变动的考核模板编号来改变初始值
		ArrayList badly_partRestrict = (ArrayList) this.getFormHM().get("Badly_partRestrict");
		ArrayList fine_partRestrict = (ArrayList) this.getFormHM().get("Fine_partRestrict");
		
		String accordPVFlag="1";//打分控制参数 最高标度对象数设置不超过 按比例还是按数值标志 1：按数值 2：按比例
		String paramOper = (String) this.getFormHM().get("paramOper");
		
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");	
		String type = (String) hm.get("type");	
		hm.remove("type");
		
		ExamPlanBo bo = new ExamPlanBo(this.frameconn);
//		if (paramOper.equalsIgnoreCase("detail"))// 详细页面 部分指标分别设置 在这个时候才做初始化
//		{			
			String planId = (String) hm.get("plan_id");
			String templateId = (String) hm.get("templId");
			hm.remove("plan_id");
			hm.remove("templId");
			
			String parameter_content = bo.getParameter_content(planId);
			String tempTemplateId = (String) this.getFormHM().get("tempTemplateId");
			tempTemplateId = tempTemplateId == null ? "" : tempTemplateId;
			try
			{
				if (!tempTemplateId.equals(templateId))
				{
					
					if ("badly".equalsIgnoreCase(type))
					{
					//	if (bo.isExist(planId) && parameter_content.length()>0 && bo.getPerPlanVo(planId).getString("template_id").equalsIgnoreCase(templateId) && bo.isExists(parameter_content, "BadlyMax"))
					//		badly_partRestrict = bo.getRestrictList(parameter_content, "BadlyMax",templateId);
					//	else
							badly_partRestrict = bo.notExists(templateId);
						fine_partRestrict = new ArrayList();
						/*
						float sumvalue = bo.getSumPointValue(badly_partRestrict);
						if(sumvalue<1 && sumvalue>0)
							accordPVFlag="2";
						*/
						if (!bo.isPointValueOverOne(badly_partRestrict)){
                            accordPVFlag="2";
                        }
							
					} else if ("fine".equalsIgnoreCase(type))
					{
					//	if (bo.isExist(planId) && parameter_content.length()>0 && bo.getPerPlanVo(planId).getString("template_id").equalsIgnoreCase(templateId) && bo.isExists(parameter_content, "FineMax"))
					//		fine_partRestrict = bo.getRestrictList(parameter_content, "FineMax",templateId);
					//	else
						if(fine_partRestrict.size()==0)//zhaoxg add list有值就不要更新为空的了  2014-10-16
						{
							fine_partRestrict = bo.notExists(templateId);
						}
						badly_partRestrict = new ArrayList();
						/*
						float sumvalue = bo.getSumPointValue(fine_partRestrict);
						if(sumvalue<1 && sumvalue>0)
							accordPVFlag="2";
						*/
					
						if (!bo.isPointValueOverOne(fine_partRestrict)){
						    accordPVFlag="2";
						}
					}

				} else
				{
					if ("badly".equalsIgnoreCase(type))
					{
						if(badly_partRestrict.size()==0)
						{
							if (bo.isExist(planId) && parameter_content.length()>0  && bo.getPerPlanVo(planId).getString("template_id").equalsIgnoreCase(templateId) &&  bo.isExists(parameter_content, "BadlyMax"))
								badly_partRestrict = bo.getRestrictList(parameter_content, "BadlyMax",templateId);
							else
								badly_partRestrict = bo.notExists(templateId);							
						}
						/*
						float sumvalue = bo.getSumPointValue(badly_partRestrict);
						if(sumvalue<1 && sumvalue>0)
							accordPVFlag="2";
						*/	
						if (!bo.isPointValueOverOne(badly_partRestrict)){
                            accordPVFlag="2";
                        }
					} else if ("fine".equalsIgnoreCase(type))
					{
						if(fine_partRestrict.size()==0)
						{
							if (bo.isExist(planId) && parameter_content.length()>0 && bo.getPerPlanVo(planId).getString("template_id").equalsIgnoreCase(templateId) && bo.isExists(parameter_content, "FineMax"))
								fine_partRestrict = bo.getRestrictList(parameter_content, "FineMax",templateId);
							else
								fine_partRestrict = bo.notExists(templateId);
						}
						/*
						float sumvalue = bo.getSumPointValue(fine_partRestrict);
						if(sumvalue<1 && sumvalue>0)
							accordPVFlag="2";
						*/
						if (!bo.isPointValueOverOne(fine_partRestrict)){
                            accordPVFlag="2";
                        }
					}
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			    throw GeneralExceptionHandler.Handle(e);
			}
			this.getFormHM().put("tempTemplateId", templateId);	
/*			
		}else if(paramOper.equalsIgnoreCase("list"))
		{
			if (type.equalsIgnoreCase("badly"))
			{	
				float sumvalue = bo.getSumPointValue(badly_partRestrict);
				if(sumvalue<1 && sumvalue>0)
					accordPVFlag="2";
				
			}else if (type.equalsIgnoreCase("fine"))
			{
				float sumvalue = bo.getSumPointValue(fine_partRestrict);
				if(sumvalue<1 && sumvalue>0)
					accordPVFlag="2";
			}
		}
*/		this.getFormHM().put("accordPVFlag", accordPVFlag);
		this.getFormHM().put("Badly_partRestrict", badly_partRestrict);
		this.getFormHM().put("Fine_partRestrict", fine_partRestrict);
	}
	
}
