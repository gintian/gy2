package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.businessobject.performance.ComputFormulaBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:TestComputFormulaTrans.java</p>
 * <p>Description:绩效评估 校验计算公式</p>
 * <p>Company:HJHJ</p>
 * <p>Create time:2010-07-21</p>
 * @author JinChunhai
 * @version 4.2
 */

public class TestComputFormulaTrans extends IBusiness
{
	
	public void execute() throws GeneralException
	{
		
		String gjsjformula = (String)this.formHM.get("gjsjformula");//先根据等级分类规则生成等级  参数		
		String type = (String) this.formHM.get("type");// total_formula 总分计算公式 ,xishu_formula 考核系数公式 ,total_xishu_formula 总分和系数都检查
		String planid = (String) this.formHM.get("planid");
		String errorInfo="ok";
		String deviationScore=(String) this.formHM.get("deviationScore");//是否使用总分纠偏公式  0不使用 1使用
		if("total_formula".equalsIgnoreCase(type) || "xishu_formula".equalsIgnoreCase(type) || "custom_formula".equalsIgnoreCase(type))
		{
			String formula = (String) this.formHM.get("formula");
			formula = formula != null && formula.trim().length() > 0 ? formula : "";
			formula = SafeCode.decode(formula);		
			formula = PubFunc.keyWord_reback(formula);						
			ComputFormulaBo bo = new ComputFormulaBo(type,this.frameconn,planid,this.userView);
			errorInfo = bo.testformula(formula);
			
		}else if("total_xishu_formula".equalsIgnoreCase(type))
		{
			//检验总分计算公式
			type = "total_formula";
			String formula = (String) this.formHM.get("total_formula");
			formula = formula != null && formula.trim().length() > 0 ? formula : "";
			formula = SafeCode.decode(formula);	
			formula = PubFunc.keyWord_reback(formula);									
			ComputFormulaBo bo = new ComputFormulaBo(type,this.frameconn,planid,this.userView);
			errorInfo = bo.testformula(formula);
			if(!"ok".equalsIgnoreCase(errorInfo))
			{
				errorInfo=SafeCode.encode(errorInfo);
				this.getFormHM().put("errorInfo", errorInfo);
				return;
			}
			//检验总分纠偏计算公式
			if("1".equals(deviationScore)){
				formula = (String) this.formHM.get("total_deviation_formula");
				formula = formula != null && formula.trim().length() > 0 ? formula : "";
				formula = SafeCode.decode(formula);	
				formula = PubFunc.keyWord_reback(formula);									
				bo = new ComputFormulaBo(type,this.frameconn,planid,this.userView);
				errorInfo = bo.testDeviationFormula(formula);
				if(!"ok".equalsIgnoreCase(errorInfo))
				{
					errorInfo=SafeCode.encode(errorInfo);
					this.getFormHM().put("errorInfo", errorInfo);
					return;
				}
				//------------勾选纠偏公式之后   在考核结果表里面增加reviseScore字段   zhaoxg 2014-3-17
				String tablename = "per_result_" + planid;
				Table table = new Table(tablename);
				DbWizard dbWizard = new DbWizard(this.frameconn);
				boolean flag = false;
				if (!dbWizard.isExistField(tablename, "reviseScore", false))
				{
					Field obj = new Field("reviseScore");
					obj.setDatatype(DataType.FLOAT);
					obj.setLength(8);
					obj.setDecimalDigits(2);
					table.addField(obj);
					flag = true;
				}
				if (flag)
					dbWizard.addColumns(table);// 更新列
			}
			//检验考核系数计算公式
			type = "xishu_formula";
			formula = (String) this.formHM.get("xishu_formula");
			formula = formula != null && formula.trim().length() > 0 ? formula : "";
			formula = SafeCode.decode(formula);		
			formula = PubFunc.keyWord_reback(formula);						
			bo = new ComputFormulaBo(type,this.frameconn,planid,this.userView);
			errorInfo = bo.testformula(formula);	
			
			//检验自定义等级计算公式
			type = "custom_formula";
			formula = (String) this.formHM.get("custom_formula");
			formula = formula != null && formula.trim().length() > 0 ? formula : "";
			formula = SafeCode.decode(formula);	
			formula = PubFunc.keyWord_reback(formula);									
			bo = new ComputFormulaBo(type,this.frameconn,planid,this.userView);
			errorInfo = bo.testformula(formula);
			
		}else if("PerformanceReport_nameFormula".equalsIgnoreCase(type))//绩效报告命名公式
		{
			String formula = (String) this.formHM.get("formula");
			formula = formula != null && formula.trim().length() > 0 ? formula : "";
			formula = SafeCode.decode(formula);	
			formula = PubFunc.keyWord_reback(formula);												
			ComputFormulaBo bo = new ComputFormulaBo(type,this.frameconn,planid,this.userView);
			errorInfo = bo.testformula2(formula);
		}
		errorInfo=SafeCode.encode(errorInfo);
		// 加载动态参数
		LoadXml loadxml = new LoadXml(this.frameconn, planid);
		BatchGradeBo.getPlanLoadXmlMap().put(planid, loadxml);
		this.getFormHM().put("errorInfo", errorInfo);
		this.getFormHM().put("gjsjformula", gjsjformula);
		
	}
	
}
