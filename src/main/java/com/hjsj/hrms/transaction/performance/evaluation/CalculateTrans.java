package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.singleGrade.SingleGradeBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
/**
 * 
 *<p>Title:</p> 
 *<p>Description:计算考评结果</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 15, 2008</p> 
 *@author dengcan
 *@version 4.0
 */
public class CalculateTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{/*
			String ThrowHighCount=(String)this.getFormHM().get("throwHighCount");   //去掉最大值数
			String ThrowLowCount=(String)this.getFormHM().get("throwLowCount");    //去掉最小值数
			String KeepDecimal=(String)this.getFormHM().get("keepDecimal");     //计算结果保留小数位
			String UseWeight=(String)this.getFormHM().get("useWeight");        //是否使用权重
			String UseKnow=(String)this.getFormHM().get("useKnow");          //是否过滤了解程度
			String[] KnowText=(String[])this.getFormHM().get("knowText");         //保留的了解程度ID
			String AppUseWeight=(String)this.getFormHM().get("appUseWeight");     //评估中总体评价票数有权重
//			String UnLeadSingleAvg=(String)this.getFormHM().get("unLeadSingleAvg");  //对空票作废的主体类中单项未评分的，按该项总平均分值和赋分权重计分
			String[] EstBodyText=(String[])this.getFormHM().get("estBodyText");      //去最值的ID
			String  planid=(String)this.getFormHM().get("planid");
			
			String nodeKnowDegree=(String)this.getFormHM().get("nodeKnowDegree"); //是否有了解程度
			
			String KnowText_value="";
			if(UseKnow.equals("-1")&&KnowText!=null)
			{
				for(int i=0;i<KnowText.length;i++)
				{
					if(!KnowText[i].equals("-1"))
						KnowText_value+=","+KnowText[i];
				}
				
			}
			String EstBodyText_value="";
			if(Integer.parseInt(ThrowHighCount)>0||Integer.parseInt(ThrowLowCount)>0)
			{
				if(EstBodyText!=null)
				{
					for(int i=0;i<EstBodyText.length;i++)
					{
						if(!EstBodyText[i].equals("-1"))
							EstBodyText_value+=","+EstBodyText[i];
					}
				}
			}
			*/
			String code=(String)this.getFormHM().get("code");   // 点击机构树传过来的编码
			String ThrowHighCount=(String)this.getFormHM().get("throwHighCount");   //去掉最大值数
			String ThrowLowCount=(String)this.getFormHM().get("throwLowCount");    //去掉最小值数
			String throwBaseNum=(String)this.getFormHM().get("throwBaseNum");    //主体类别人数大于
			String KeepDecimal=(String)this.getFormHM().get("keepDecimal");     //计算结果保留小数位
			String UseWeight=(String)this.getFormHM().get("useWeight");        //是否使用权重
			String UseKnow=(String)this.getFormHM().get("useKnow");          //是否过滤了解程度
			String KnowText_value=(String)this.getFormHM().get("KnowText_value");         //保留的了解程度ID
			String AppUseWeight=(String)this.getFormHM().get("appUseWeight");     //评估中总体评价票数有权重
			String EstBodyText_value=(String)this.getFormHM().get("EstBodyText_value");      //去最值的ID
			String  planid=(String)this.getFormHM().get("planid");			
			String nodeKnowDegree=(String)this.getFormHM().get("nodeKnowDegree"); //是否有了解程度
			String UnLeadSingleAvg=(String)this.getFormHM().get("unLeadSingleAvg");  //对空票作废的主体类中单项未评分的，按该项总平均分值和赋分权重计分
			String isShowValPrecision = (String)this.getFormHM().get("isShowValPrecision");
			String PointScoreFromKeyEvent=(String)this.getFormHM().get("pointScoreFromKeyEvent");
			
			String isvalidate=(String)this.getFormHM().get("isvalidate");// 定义分值指标范围 是否有效 true有效 false无效
			if(isvalidate==null){
				isvalidate="false";
			}
			String zeroByNull = (String)this.getFormHM().get("zeroByNull"); //空票按零分计算
			if(PointScoreFromKeyEvent==null||PointScoreFromKeyEvent.trim().length()==0)
				PointScoreFromKeyEvent="False";
			String method=(String)this.getFormHM().get("method");
			
			String CheckInvalidGrade="False";		
		    String InvalidGrade="";
		    String clientName="";
			if(SystemConfig.getPropertyValue("clientName")!=null)
		  		clientName=SystemConfig.getPropertyValue("clientName").trim();
		    if("1".equalsIgnoreCase(method)&& "bjga".equalsIgnoreCase(clientName)&&this.getFormHM().get("checkInvalidGrade")!=null)
		    {
		    	CheckInvalidGrade= (String)this.getFormHM().get("checkInvalidGrade");
		    	if("True".equalsIgnoreCase(CheckInvalidGrade))
		    		InvalidGrade=(String)this.getFormHM().get("invalidGrade");
		    }
			//保存计算规则
			LoadXml loadxml=new LoadXml(this.getFrameconn(),planid);
			HashMap map=new HashMap();
			map.put("zeroByNull", zeroByNull);
			map.put("CheckInvalidGrade",CheckInvalidGrade);
			map.put("InvalidGrade",InvalidGrade);
			if("false".equalsIgnoreCase(isShowValPrecision))
			{
				map.put("ThrowHighCount",ThrowHighCount);
				map.put("ThrowLowCount",ThrowLowCount);
				map.put("KeepDecimal",KeepDecimal);
				map.put("UseWeight", "1".equals(UseWeight)?"True":"False");
				if("false".equalsIgnoreCase(nodeKnowDegree))
					map.put("UseKnow","False");
				else
					map.put("UseKnow",UseKnow);
				map.put("KnowText",KnowText_value.length()>0?KnowText_value.substring(1):"");
				map.put("AppUseWeight", "1".equals(AppUseWeight)?"True":"False");
				map.put("EstBodyText",EstBodyText_value.length()>0?EstBodyText_value.substring(1):"");
				map.put("ThrowBaseNum",throwBaseNum);
			}else if("true".equalsIgnoreCase(isShowValPrecision))
			{
				map.put("KeepDecimal",KeepDecimal);
				
				
				map.put("ThrowHighCount","0");
				map.put("ThrowLowCount","0");
				map.put("UseWeight", "False");
				map.put("UseKnow","False");
				
				map.put("KnowText","");
				map.put("AppUseWeight","False");
				map.put("EstBodyText","");
				map.put("ThrowBaseNum","0");
			} 
			map.put("PointScoreFromKeyEvent",PointScoreFromKeyEvent);  //指标评分优先取自关键事件
			loadxml.saveComputeRule(map);
			loadxml.refreshNodeAttribute("/PerPlan_Parameter/PointScoreScopes", "PointScoreScopes", "IsValid", isvalidate);
			
			
			//dengcan 2010-5-17 
			Hashtable htxml = new Hashtable();
			LoadXml _loadxml=null;
			if(BatchGradeBo.planLoadXmlMap.get(planid)==null)
			{
				_loadxml = new LoadXml(this.frameconn,planid);
				BatchGradeBo.planLoadXmlMap.put(planid,_loadxml);
			}
			else
				_loadxml=(LoadXml)BatchGradeBo.planLoadXmlMap.get(planid);
			htxml =_loadxml.getDegreeWhole();
			htxml.put("KeepDecimal", KeepDecimal); // 小数位
			
			
			
			
			PerEvaluationBo bo=new PerEvaluationBo(this.getFrameconn(),planid,"",this.userView);
			bo.setCode(code);
			Hashtable ht=bo.getPlanParamSet();
			if(ht.get("formulaSql")!=null)
				map.put("formulaSql",(String)ht.get("formulaSql"));		
			
			/************优先取总体评价等级分类 EvalClass***************/
			String EvalClass = (String)ht.get("EvalClass");            //在总体评价中的等级分类ID
			if(EvalClass==null || EvalClass.trim().length()<=0 || "0".equals(EvalClass.trim()))
				EvalClass = (String)ht.get("EvalClass");													
			if(EvalClass!=null && EvalClass.trim().length()>0)
				map.put("EvalClass",EvalClass);
						
			String GradeClass = (String)ht.get("GradeClass");					//在启动计划中的等级分类ID							
			if(GradeClass!=null && GradeClass.trim().length()>0)
				map.put("GradeClass",GradeClass);
			if(ht.get("NodeKnowDegree")!=null)
				map.put("NodeKnowDegree",(String)ht.get("NodeKnowDegree"));
			if(ht.get("WholeEval")!=null)
				map.put("WholeEval",(String)ht.get("WholeEval"));
			if(ht.get("WholeEvalMode")!=null)                            //总体评价录入方式 0：录入等级 1：录入分值
				map.put("WholeEvalMode",(String)ht.get("WholeEvalMode"));
			if(ht.get("UnLeadSingleAvg")!=null)
				map.put("UnLeadSingleAvg",(String)ht.get("UnLeadSingleAvg"));
			
			if(ht.get("isvalidate")!=null)
				map.put("isvalidate",(String)ht.get("isvalidate"));
			if(ht.get("scoreRangeList")!=null)
				map.put("scoreRangeList",(ArrayList)ht.get("scoreRangeList"));
			
			if(ht.get("PointScoreFromKeyEvent")!=null)  //只针对共性指标
				map.put("PointScoreFromKeyEvent",(String)ht.get("PointScoreFromKeyEvent"));
			if(ht.get("zeroByNull")!=null)
				map.put("zeroByNull",(String)ht.get("zeroByNull"));
			String showRule=(String)this.getFormHM().get("showRule");
			
			if(showRule!=null&& "cal".equalsIgnoreCase(showRule)){
				boolean isByModelFlag = SingleGradeBo.getByModel(planid, this.getFrameconn());
				if(isByModelFlag){
					bo.calculatePlanByModel(this.getUserView(),map,1);
				}else{
					bo.calculatePlan(this.getUserView(),map,1);
				}
			}
			
			this.getFormHM().put("flag", "1");
			this.getFormHM().put("code", code);
			this.getFormHM().put("zeroByNull", zeroByNull);
		}
		catch(Exception e)
		{
			this.getFormHM().put("flag", "0");
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
