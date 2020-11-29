package com.hjsj.hrms.transaction.performance.batchGrade;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
import java.util.Hashtable;

/** 
 *<p>Title:ComputObjectsScoreTrans.java</p> 
 *<p>Description:多人考评临时计算</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 15, 2013</p> 
 *@author JinChunhai
 *@version 6.0
 */

public class ComputObjectsScoreTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		String plan_id = (String) this.getFormHM().get("plan_id");
		CheckPrivSafeBo _bo = new CheckPrivSafeBo(this.frameconn,this.userView);
		boolean _flag = _bo.isPlanIdPriv(plan_id);
		if(!_flag){
			return;
		}
	//	String object_id = (String) this.getFormHM().get("object_id");	
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
			map.put("WholeEvalMode", (String) param.get("WholeEvalMode"));
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
			PerEvaluationBo bo = new PerEvaluationBo(this.getFrameconn(),plan_id,"",this.userView);
			bo.setBatchComput("True");
			bo.setPresentMainbody_id(this.userView.getA0100());
			bo.setPriv_where(" and object_id in (select object_id from per_mainbody where plan_id = '"+plan_id+"' and mainbody_id = '"+this.userView.getA0100()+"') ");
			String batchScoreImportFormula = (String)param.get("BatchScoreImportFormula");
			if("false".equalsIgnoreCase(batchScoreImportFormula)){//多人评分计算时是否引用总分计算公式  pjf 2014.01.03
				bo.setBatchScoreImportFormulaFlag("notImport");
			}
			bo.calculatePlan(this.getUserView(),map,4);
			
			
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
