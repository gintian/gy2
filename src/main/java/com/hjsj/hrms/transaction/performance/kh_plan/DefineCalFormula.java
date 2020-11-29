package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * DefineCalFormula.java
 * Description: 定义考核等级计算公式
 * Copyright (c) Department of Research and Development/Beijing/北京世纪软件有限公司.
 * All Rights Reserved.
 * @version 1.0  
 * Nov 12, 2012 2:46:43 PM Jianghe created
 */
public class DefineCalFormula extends IBusiness{
	public void execute() throws GeneralException
	{
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		ExamPlanBo bo = new ExamPlanBo(this.frameconn);
		//String templateId = (String) hm.get("templateId");
		String templateId = (String) this.getFormHM().get("templateId");
		String status = (String) this.getFormHM().get("status");
		ArrayList pointList = new ArrayList();
		if(templateId!=null && templateId.length()>0){
			pointList = bo.getTemPointList(templateId);
		}
		this.getFormHM().put("pointList", pointList);
		this.getFormHM().put("status", status);
	} 		
}
