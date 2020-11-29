package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.businessobject.performance.ComputFormulaBo;
import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hjsj.hrms.businessobject.performance.singleGrade.SingleGradeBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Title:SaveComputFormulaTrans.java</p>
 * <p>Description:绩效评估 计算公式</p>
 * <p>Company:HJHJ</p>
 * <p>Create time:2010-07-21</p> 
 * @author JinChunhai
 * @version 5.0
 */

public class SaveComputFormulaTrans extends IBusiness
{
	public void execute() throws GeneralException
	{
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String isReCalcu = (String) hm.get("isReCalcu");
		String gjsj_mula = (String) hm.get("gjsj_mula");
		hm.remove("isReCalcu");
		
		String deviationScore=(String) this.formHM.get("deviationScore");//是否使用总分纠偏公式  0不使用 1使用
		String total_formula = (String)this.getFormHM().get("formula");  // 总分计算公式
		String scoreDeviationFormula=(String)this.getFormHM().get("scoreDeviationFormula"); //总计算纠偏公式
		total_formula=total_formula.trim().length()==0?"[本次得分]":total_formula;
		total_formula = PubFunc.keyWord_reback(total_formula);
		scoreDeviationFormula=scoreDeviationFormula.length()==0?"[本次得分]":scoreDeviationFormula;
		scoreDeviationFormula = PubFunc.keyWord_reback(scoreDeviationFormula);
		String xishu_formula = (String)this.getFormHM().get("expr");  // 等级系数计算公式
		xishu_formula = PubFunc.keyWord_reback(xishu_formula);		
		
		String gradeFormula = (String) this.getFormHM().get("gradeFormula");		
		if("2".equals(gradeFormula) && "check_ed".equals(gjsj_mula))
		{
			gradeFormula="3";
		}else if("3".equals(gradeFormula) && "check_ed".equals(gjsj_mula))
		{
			gradeFormula="3";
		}else if("3".equals(gradeFormula) && (!"check_ed".equals(gjsj_mula)))
		{
			gradeFormula="2";
		}
		
		String procedureName = (String) this.getFormHM().get("procedureName");  // 等级计算公式
		String customizeGrade = (String) this.getFormHM().get("customizeGrade"); // 等级计算公式
		procedureName = PubFunc.keyWord_reback(procedureName);		
		customizeGrade = PubFunc.keyWord_reback(customizeGrade);		
	
		String planid = (String) this.getFormHM().get("planid");
		String khObjWhere2 = (String) this.getFormHM().get("khObjWhere2");
		khObjWhere2 = PubFunc.keyWord_reback(khObjWhere2);	
		
		ComputFormulaBo bo = new ComputFormulaBo("total_formula",this.frameconn,planid,this.userView);
		LoadXml loadXml = new LoadXml(this.getFrameconn(),planid);
	 
		String total_sql = bo.getSqlByFormula(total_formula);//总分计算公式转化为相应的sql语句
		String total_deviation_sql = bo.getSqlByFormula(scoreDeviationFormula);//总分纠偏计算公式转化为相应的sql语句
		bo = new ComputFormulaBo("xishu_formula",this.frameconn,planid,this.userView);
		String xishu_sql = bo.getSqlByFormula(xishu_formula);	
		
	//	LoadXml loadXml = new LoadXml(this.getFrameconn(), planid, "");
		
	    if("2".equals(gradeFormula) || "3".equals(gradeFormula))
	    	loadXml.saveAttribute("PerPlan_Parameter", "GradeFormula", gradeFormula + ";" + customizeGrade);
	    else
	    	loadXml.saveAttribute("PerPlan_Parameter", "GradeFormula", gradeFormula + ";" + procedureName);
	    
		loadXml.saveAttribute("PerPlan_Parameter", "xiFormula", xishu_formula);	
		
		ArrayList list = new ArrayList();
		LazyDynaBean bean = new LazyDynaBean();
		bean.set("Caption", total_formula);
		bean.set("Value", total_sql);
		list.add(bean);
		ArrayList idlist = new ArrayList();
		idlist.add("Caption");
		idlist.add("Value");
		loadXml.saveRelatePlanValue("Formula", idlist, list);//保存总分计算公式至xml
		if("1".equals(deviationScore)){
			list = new ArrayList();
			bean = new LazyDynaBean();
			bean.set("Used", "1");
			bean.set("Caption", scoreDeviationFormula);
			bean.set("Value", total_deviation_sql);
			list.add(bean);
			idlist = new ArrayList();
			idlist.add("Used");
			idlist.add("Caption");
			idlist.add("Value");
			loadXml.saveRelatePlanValue("ReviseScore", idlist, list);//保存总分纠偏计算公式至xml
		}else{
			list = new ArrayList();
			bean = new LazyDynaBean();
			bean.set("Used", "0");
			bean.set("Caption", scoreDeviationFormula);
			bean.set("Value", total_deviation_sql);
			list.add(bean);
			idlist = new ArrayList();
			idlist.add("Used");
			idlist.add("Caption");
			idlist.add("Value");
			loadXml.saveRelatePlanValue("ReviseScore", idlist, list);//保存总分纠偏计算公式至xml
		}

		
		//算总分
		ContentDAO dao = new ContentDAO(this.frameconn);
		try
		{
			String tablename = "per_result_" + planid;
			if (isReCalcu != null && "ok".equalsIgnoreCase(isReCalcu))
			{
				
				String sql="";
				
				loadXml = new LoadXml(this.getFrameconn(),planid);
				Hashtable param=loadXml.getDegreeWhole();
				//计算规则
				HashMap map=new HashMap();
				map.put("ThrowHighCount",(String)param.get("ThrowHighCount"));
				map.put("ThrowLowCount",(String)param.get("ThrowLowCount"));
				map.put("KeepDecimal",(String)param.get("KeepDecimal"));
				map.put("UseWeight",(String)param.get("UseWeight"));
				map.put("UseKnow",(String)param.get("UseKnow"));
				map.put("KnowText",(String)param.get("KnowText"));
				map.put("AppUseWeight",(String)param.get("AppUseWeight"));
				map.put("EstBodyText",(String)param.get("EstBodyText"));
				map.put("ThrowBaseNum",(String)param.get("ThrowBaseNum"));
				if(param.get("PointScoreFromKeyEvent")!=null)
					map.put("PointScoreFromKeyEvent", (String)param.get("PointScoreFromKeyEvent"));
				if(param.get("formulaSql")!=null)
					map.put("formulaSql",(String)param.get("formulaSql"));	
				if(param.get("formulaDeviationSql")!=null)
					map.put("formulaDeviationSql",(String)param.get("formulaDeviationSql"));	
				if("1".equals(deviationScore)){//对总分纠偏计算
					map.put("deviationScore","1");
				}
				String EvalClass = (String)param.get("EvalClass");            //在计划参数中的等级分类ID
				if(EvalClass==null || EvalClass.trim().length()<=0 || "0".equals(EvalClass.trim()))
					EvalClass = (String)param.get("GradeClass");					//等级分类ID											
				if(EvalClass!=null && EvalClass.trim().length()>0)
					map.put("EvalClass",EvalClass);
				String GradeClass = (String)param.get("GradeClass");					//等级分类ID								
				if(GradeClass!=null && GradeClass.trim().length()>0)
					map.put("GradeClass",GradeClass);
				if(param.get("NodeKnowDegree")!=null)
					map.put("NodeKnowDegree",(String)param.get("NodeKnowDegree"));
				if(param.get("WholeEval")!=null)
					map.put("WholeEval",(String)param.get("WholeEval"));
				if(param.get("UnLeadSingleAvg")!=null)
					map.put("UnLeadSingleAvg",(String)param.get("UnLeadSingleAvg"));
				PerEvaluationBo ebo=new PerEvaluationBo(this.getFrameconn(),planid,"",this.userView);
				boolean isByModelFlag = SingleGradeBo.getByModel(planid, this.getFrameconn());
				if(isByModelFlag){
					ebo.calculatePlanByModel(this.getUserView(),map,1);
				}else{
					ebo.calculatePlan(this.getUserView(),map,1);
				}
				
				/*
				String sql = "update " + tablename + " set score = " + total_sql;
				if (khObjWhere2.length() > 0)
					sql += " where 1=1 " + khObjWhere2;
				dao.update(sql);
				PerEvaluationBo perEvaluationBo = new PerEvaluationBo(this.getFrameconn(), planid, "", this.userView);
				perEvaluationBo.updateGroupFields();				
				
				//接下来计算等级和系数 
				Hashtable htxml = loadXml.getDegreeWhole();
				String gradeID = (String)htxml.get("GradeClass");
				//如果系数没有设置公式,先算等级,再算系数。
				if(xishu_sql.trim().length()==0)
				{
					//目前浪潮按默认的走 先算等级再算系数 系数由等级算
					if(gradeID!=null)
						perEvaluationBo.setGradeValue(gradeID);
				}else//如果系数设置了公式，先算系数再算等级 这个时候等级应设为用存储过程算 才存储过程中用到系数
				{					
					sql = "update " + tablename + " set exX_object = " + xishu_sql;
					if (khObjWhere2.length() > 0)
						sql += " where 1=1 " + khObjWhere2;
					dao.update(sql);
					
					//目前浪潮按默认的走 先算等级再算系数 系数由等级算
					if(gradeID!=null)
						perEvaluationBo.setGradeValue(gradeID);
				}
			*/
				
				
				
				//初始化备注字段
				sql="update per_result_"+planid+" set evalremark=null  ";
				if (khObjWhere2.length() > 0)
					sql += " where 1=1 " + khObjWhere2;
				dao.update(sql);
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
