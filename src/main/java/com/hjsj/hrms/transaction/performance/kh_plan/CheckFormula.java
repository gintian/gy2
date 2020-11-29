package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * CheckFormula.java
 * Description: 公式检查
 * Copyright (c) Department of Research and Development/Beijing/北京世纪软件有限公司.
 * All Rights Reserved.
 * @version 1.0  
 * Nov 12, 2012 4:15:43 PM Jianghe created
 */
public class CheckFormula extends IBusiness
{
	
	public void execute() throws GeneralException
	{
				
		String errorInfo="ok";
		
		String formula = (String) this.getFormHM().get("formula");
		String templateId = (String) this.getFormHM().get("templateId");
		formula = formula != null && formula.trim().length() > 0 ? formula : "";
		formula = SafeCode.decode(formula);	
		formula = PubFunc.keyWord_reback(formula);
		ExamPlanBo bo = new ExamPlanBo(this.getFrameconn(),this.userView);
		errorInfo = bo.testformula(formula,templateId);
				
		errorInfo=SafeCode.encode(errorInfo);
		this.getFormHM().put("errorInfo", errorInfo);

	}
	
}