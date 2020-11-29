package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * <p>Title:SearchComputFormulaTrans.java</p>
 * <p>Description:绩效评估 计算公式</p>
 * <p>Company:HJHJ</p>
 * <p>Create time:2010-07-21</p>
 * @author JinChunhai
 * @version 4.2
 */

public class SearchComputFormulaTrans extends IBusiness
{
	
	public void execute() throws GeneralException
	{
		
		String planid = (String) this.getFormHM().get("planid");
		String busitype = (String) this.getFormHM().get("busitype");  // 业务分类字段 =0(绩效考核); =1(能力素质)
		
		CheckPrivSafeBo bo = new CheckPrivSafeBo(this.frameconn,this.userView);
		boolean _flag = bo.isHavePriv(this.userView, planid);
		if(!_flag){
			return;
		}
				
		//总分计算公式
		ArrayList exprrelatelist = new ArrayList();
					
		try
		{						
			LoadXml loadxml = new LoadXml(this.frameconn, planid);
			Hashtable htxml=loadxml.getDegreeWhole();
			
			PerEvaluationBo pb = new PerEvaluationBo(this.getFrameconn(),this.userView);			
			exprrelatelist = pb.getExprrelatelist(planid,busitype,loadxml,"comput");
						
			this.getFormHM().put("exprrelatelist", exprrelatelist);
			
			ArrayList formulalist = loadxml.getRelatePlanValue("Formula", "Caption");
			String formula = "";
			if (formulalist!=null && formulalist.size() > 0)
			{
				if((formulalist.get(0).toString())!=null && (formulalist.get(0).toString()).length()>0)
					formula = formulalist.get(0).toString();
				else
					formula = "[本次得分]";
			}
			else
				formula = "[本次得分]";
			this.getFormHM().put("formula", formula);
			//总分纠偏公式
			ArrayList evaluationFormulalist = loadxml.getRelatePlanValue("ReviseScore", "Caption");
			String scoreDeviationFormula = "";
			if (evaluationFormulalist!=null && evaluationFormulalist.size() > 0)
			{
				if((evaluationFormulalist.get(0).toString())!=null && (evaluationFormulalist.get(0).toString()).length()>0)
					scoreDeviationFormula = evaluationFormulalist.get(0).toString();
				else
					scoreDeviationFormula = "[本次得分]";
			}
			else
				scoreDeviationFormula = "[本次得分]";
			String deviationScore="0";//是否使用总分纠偏公式  0不使用 1使用
			ArrayList usedList= loadxml.getRelatePlanValue("ReviseScore", "Used");
			if (usedList!=null && usedList.size() > 0)
			{
				if((usedList.get(0).toString())!=null && (usedList.get(0).toString()).length()>0)
					deviationScore = usedList.get(0).toString();
			}

			this.getFormHM().put("formula", formula);
			this.formHM.put("scoreDeviationFormula", scoreDeviationFormula);
			this.getFormHM().put("deviationScore", deviationScore);
			//考核系数计算公式		
			String xiFormula=(String)htxml.get("xiFormula");
			this.getFormHM().put("expr", xiFormula);
			
			//等级计算公式
			String gradeFormula = "0";
			String procedureName = "";		
			String customizeGrade="";
			String gradeFormulaExpre=(String)htxml.get("GradeFormula");
			if(gradeFormulaExpre!=null && gradeFormulaExpre.length()>0)
			{
				if(gradeFormulaExpre.indexOf(":")!=-1) // 说明为以前规则的存储方式
					gradeFormulaExpre = gradeFormulaExpre.replaceAll(":",";");					
				String[] gradeFormulaExpres =  gradeFormulaExpre.split(";");
				gradeFormula  = gradeFormulaExpres[0];
				if("1".equals(gradeFormula) && gradeFormulaExpre.split(";").length==2)
					procedureName = gradeFormulaExpres[1];
				else if(("2".equals(gradeFormula) || "3".equals(gradeFormula)) && gradeFormulaExpre.split(";").length==2)
					customizeGrade = gradeFormulaExpres[1];				
				
				if("2".equals(gradeFormula) && (gradeFormulaExpre.indexOf("执行存储过程")!=-1))
					customizeGrade = gradeFormulaExpres[1]+":"+gradeFormulaExpres[2];	
			/*
				if(gradeFormulaExpre.indexOf(":")!=-1) // 说明为以前规则的存储方式
				{
					String[] gradeFormulaExpres =  gradeFormulaExpre.split(":");
					gradeFormula  = gradeFormulaExpres[0];
					if(gradeFormula.equals("1"))
					{
						gradeFormula = "2";
						procedureName = gradeFormulaExpres[1];
						
					}else if(gradeFormula.equals("2") || gradeFormula.equals("3"))
					{
						if(gradeFormula.equals("2"))
							gradeFormula = "4";
						else if(gradeFormula.equals("3"))
							gradeFormula = "5";
						customizeGrade=gradeFormulaExpres[1];	
					}else
						gradeFormula = "1";
					
				}else  // 为新规则的存储方式
				{
					String[] gradeFormulaExpres =  gradeFormulaExpre.split(";");
					gradeFormula  = gradeFormulaExpres[0];
					if(gradeFormula.equals("2") || gradeFormula.equals("3"))					
						procedureName = gradeFormulaExpres[1];						
					else if(gradeFormula.equals("4") || gradeFormula.equals("5"))					
						customizeGrade=gradeFormulaExpres[1];						
					else if(gradeFormula.equals("6") || gradeFormula.equals("7"))
					{
						String[] pdNamecmGrade =  gradeFormulaExpres[1].split("|");
						procedureName = pdNamecmGrade[0];	
						customizeGrade = pdNamecmGrade[1];	
					}
				}
			*/
			}
			this.getFormHM().put("customizeGrade", customizeGrade);
			this.getFormHM().put("gradeFormula", gradeFormula);
			this.getFormHM().put("procedureName", procedureName);	
		
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}			
	
}
