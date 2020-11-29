package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.performance.achivement.dataCollection.DataFormulaBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Title:ShowCalRuleTrans.java</p>
 * <p>Description:评估计算规则</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-09-18 11:11:11</p>
 * @author JinChunhai
 * @version 5.0
 */

public class ShowCalRuleTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String code =(String)hm.get("code");			
			String planStatus =(String)hm.get("planStatus");
			this.getFormHM().put("planStatus", planStatus);
			String planid=(String)hm.get("planid");
			CheckPrivSafeBo bo = new CheckPrivSafeBo(this.frameconn,this.userView);
			boolean _flag = bo.isHavePriv(this.userView, planid);
			if(!_flag){
				return;
			}
			PerEvaluationBo pe=new PerEvaluationBo(this.getFrameconn(),planid,"");
			ArrayList knowList=pe.getKnowList();
			ArrayList bodySetList=pe.getPerMainBodySetList(planid);
			String opt=(String)hm.get("opt");
		
			if(hm.get("b_showRule")!=null)
				this.getFormHM().put("showRule", (String)hm.get("b_showRule"));
			else
				this.getFormHM().put("showRule","cal");
			
			Hashtable planParamSet=pe.getPlanParamSet();			
			String NodeKnowDegree=((String)planParamSet.get("NodeKnowDegree")).toLowerCase();   //是否有了解程度			
			String ThrowHighCount=(String)planParamSet.get("ThrowHighCount");   //去掉最大值数
			String ThrowLowCount=(String)planParamSet.get("ThrowLowCount");    //去掉最小值数
			String KeepDecimal=(String)planParamSet.get("KeepDecimal");     //计算结果保留小数位
			String UseWeight=(String)planParamSet.get("UseWeight");        //是否使用权重
			String UseKnow=(String)planParamSet.get("UseKnow");          //是否过滤了解程度
			String KnowText_value=(String)planParamSet.get("KnowText");
			String EstBodyText_value=(String)planParamSet.get("EstBodyText");   //去最值的ID
			String AppUseWeight=(String)planParamSet.get("AppUseWeight");
			String wholeEval=(String)planParamSet.get("WholeEval");
			String throwBaseNum=(String)planParamSet.get("ThrowBaseNum");
			String pointScoreFromKeyEvent=(String)planParamSet.get("PointScoreFromKeyEvent"); //指标评分优先取自关键事件
			String scoreWay=(String)planParamSet.get("scoreWay"); // 打分途径 0 cs/bs都能打分 | 1 仅BS能打分，CS不能打分							
			String CheckInvalidGrade=(String)planParamSet.get("CheckInvalidGrade"); //无效标度代码
			String InvalidGrade=(String)planParamSet.get("InvalidGrade");           //是否选择使用无效票数, (True, False；默认为False)			
			String scopeIsValidate=(String)planParamSet.get("isvalidate");     //指标分值范围的有效性 dml 2011年10月25日11:00:23
			ArrayList scoreRangeList=(ArrayList)planParamSet.get("scoreRangeList"); //已定义指标分值范围的指标dml 2011年10月25日11:00:23			
			String isShowValPrecision = "false";
			String evalCanNewPoint =(String)planParamSet.get("EvalCanNewPoint");
			String dataGatherMode = (String)planParamSet.get("scoreflag");
			String zeroByNull = (String)planParamSet.get("zeroByNull");
			ArrayList weightList=pe.getWeightList(planid);
			String temp=(String)planParamSet.get("UnLeadSingleAvg");
			String zeroflag = "1";
			String flag = "0";
			String lead = "0";
			
			if("1".equals(temp)){
				zeroflag="0";
			} else {
				for(int i=0;i<weightList.size();i++){
					LazyDynaBean bean = (LazyDynaBean)weightList.get(i);
					if(bean.get("flag") !=null && ("1".equals(bean.get("flag")) || "0".equals(bean.get("flag"))) )
						flag = (String) bean.get("flag");
					if(bean.get("lead") !=null && ("1".equals(bean.get("lead")) || "0".equals(bean.get("lead"))) )
						lead=(String) bean.get("lead");
					if("1".equals(flag) || "1".equals(lead)){
						zeroflag="0";
						break;
					}
				}
			}
			this.getFormHM().put("zeroflag",zeroflag);
			
			PerformanceImplementBo pb=new PerformanceImplementBo(this.getFrameconn());
			RecordVo planvo=pb.getPlanVo(planid);
			String template_id = planvo.getString("template_id");
			String byModel = planvo.getString("bymodel")==null?"":planvo.getString("bymodel");//判断是绩效还是能力素质 郭峰
			String method=planvo.getInt("method")==2?"2":"1";
			RecordVo templateVo=pb.getTemplateVo(template_id);
			String templateStatus=templateVo!=null?templateVo.getString("status"):"0";
			//  && evalCanNewPoint.equalsIgnoreCase("true") 且 评估打分允许新增考核指标
			if("4".equals(dataGatherMode) && "0".equals(templateStatus))//打分按加扣分处理  且 分值模板
				isShowValPrecision = "true";
			
			String handEval =(String)planParamSet.get("HandEval");
			if(handEval!=null && "TRUE".equalsIgnoreCase(handEval))//启动录入结果
				isShowValPrecision = "true";
			
			String per_comTable = "per_grade_template"; // 绩效标准标度
			if(String.valueOf(planvo.getInt("busitype"))!=null && String.valueOf(planvo.getInt("busitype")).trim().length()>0 && planvo.getInt("busitype")==1)
				per_comTable = "per_grade_competence"; // 能力素质标准标度			
			ArrayList gradeList=pe.getGradeList(per_comTable);
			if(opt!=null&& "2".equalsIgnoreCase(opt)){
				this.getFormHM().put("isvalidate", scopeIsValidate);
			}
			HashMap hasDefine=new HashMap();
			DataFormulaBo db0=new DataFormulaBo(this.frameconn,this.userView,planid);
			hasDefine=db0.hasDefine(scoreRangeList);
			ArrayList pointlist=new ArrayList();
			pointlist=db0.getlist(planid,hasDefine);
			ArrayList rangelist=new ArrayList();
			if(pointlist!=null&&pointlist.size()>1){
				rangelist=(ArrayList)pointlist.get(1);
			}
			String showRange="true";
			if("4".equals(dataGatherMode) && "0".equals(templateStatus))//打分按加扣分处理  且 分值模板
				showRange = "false";
			if(handEval!=null && "TRUE".equalsIgnoreCase(handEval))//启动录入结果
				showRange = "false";
			
			if(db0.isShowScoreFromKey())
				this.getFormHM().put("isShowScoreFromKey","1");
			else
				this.getFormHM().put("isShowScoreFromKey","0"); 
			
			if("4".equals(dataGatherMode) && "1".equals(templateStatus) && scoreWay!=null && "0".equalsIgnoreCase(scoreWay))//打分按加扣分处理  且 权重模板 且 数据采集方式
			{
				isShowValPrecision = "true";
				showRange = "false";
			}
			
			this.getFormHM().put("pointScoreFromKeyEvent",pointScoreFromKeyEvent);
			this.getFormHM().put("gradeList",gradeList);
			this.getFormHM().put("checkInvalidGrade",CheckInvalidGrade);
			this.getFormHM().put("invalidGrade", InvalidGrade);
			this.getFormHM().put("method",method);
			this.getFormHM().put("isShowValPrecision", isShowValPrecision);
			this.getFormHM().put("nodeKnowDegree",NodeKnowDegree);
			this.getFormHM().put("appUseWeight", "true".equalsIgnoreCase(AppUseWeight)?"1":"0");
			this.getFormHM().put("useWeight",UseWeight);
			this.getFormHM().put("knowText_value",KnowText_value);
			this.getFormHM().put("useKnow",UseKnow);
			this.getFormHM().put("estBodyText_value",EstBodyText_value);
			this.getFormHM().put("keepDecimal",KeepDecimal);
			this.getFormHM().put("throwHighCount",ThrowHighCount);
			this.getFormHM().put("throwLowCount",ThrowLowCount);
			this.getFormHM().put("wholeEval",wholeEval);
			this.getFormHM().put("throwBaseNum",throwBaseNum);			
			this.getFormHM().put("planid",planid);
			this.getFormHM().put("bodySetList",bodySetList);
			this.getFormHM().put("knowList",knowList);			
			this.getFormHM().put("isvalidate",scopeIsValidate);
			this.getFormHM().put("rangelist",rangelist);
			this.getFormHM().put("showRange", showRange);
			this.getFormHM().put("code", code);
			this.getFormHM().put("zeroByNull", zeroByNull);
			this.getFormHM().put("byModel", byModel);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
