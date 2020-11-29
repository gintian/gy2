package com.hjsj.hrms.transaction.performance.scoreAjust;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.Hashtable;

/**
 * 评分调整临时计算
 * @author JinChunhai
 *
 */

public class TempCalculateTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		String plan_id = (String) this.getFormHM().get("plan_id");
		plan_id = PubFunc.decrypt(SafeCode.decode(plan_id));
		String object_id = (String) this.getFormHM().get("object_id");	
		object_id = PubFunc.decrypt(SafeCode.decode(object_id));
		try
		{	
			LoadXml loadXml = new LoadXml(this.getFrameconn(),plan_id);
			Hashtable param = loadXml.getDegreeWhole();

			HashMap map = new HashMap();
			map.put("ThrowHighCount", (String) param.get("ThrowHighCount"));
			map.put("ThrowLowCount", (String) param.get("ThrowLowCount"));
			map.put("KeepDecimal", (String) param.get("KeepDecimal"));
			map.put("UseWeight", (String) param.get("UseWeight"));
			map.put("UseKnow", (String) param.get("UseKnow"));
			map.put("KnowText", (String) param.get("KnowText"));
			map.put("AppUseWeight", (String) param.get("AppUseWeight"));
			map.put("EstBodyText", (String) param.get("EstBodyText"));
			map.put("ThrowBaseNum", (String) param.get("ThrowBaseNum"));
			if (param.get("formulaSql") != null)
				map.put("formulaSql", (String) param.get("formulaSql"));
			
			String EvalClass = (String)param.get("EvalClass");            //在计划参数中的等级分类ID
			if(EvalClass==null || EvalClass.trim().length()<=0 || "0".equals(EvalClass.trim()))
				EvalClass = (String)param.get("GradeClass");					//等级分类ID											
			if(EvalClass!=null && EvalClass.trim().length()>0)
				map.put("EvalClass",EvalClass);
			if (param.get("GradeClass") != null)
				map.put("GradeClass", (String) param.get("GradeClass"));
			if (param.get("NodeKnowDegree") != null)
				map.put("NodeKnowDegree", (String) param.get("NodeKnowDegree"));
			if (param.get("WholeEval") != null)
				map.put("WholeEval", (String) param.get("WholeEval"));
			if (param.get("UnLeadSingleAvg") != null)
				map.put("UnLeadSingleAvg", (String) param.get("UnLeadSingleAvg"));
			PerEvaluationBo bo=new PerEvaluationBo(this.getFrameconn(),plan_id,"",this.userView);
			bo.setPriv_where(" and object_id='"+object_id+"' ");
			bo.calculatePlan(this.getUserView(),map,4);
			
			
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
