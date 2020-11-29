package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchNoCanSaveDegreesTrans.java</p>
 * <p>Description:结果全相同时不能保存的标度</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-06-11 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class SearchNoCanSaveDegreesTrans extends IBusiness
{
	
	public void execute() throws GeneralException
	{
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String degrees = (String) hm.get("degrees");
		hm.remove("degrees");

		String busitype = (String) this.getFormHM().get("busitype");
		String per_comTable = "per_grade_template"; // 绩效标准标度
		if(busitype!=null && busitype.trim().length()>0 && "1".equalsIgnoreCase(busitype))
			per_comTable = "per_grade_competence"; // 能力素质标准标度
		ExamPlanBo bo = new ExamPlanBo(this.frameconn);
		ArrayList list = bo.getGradeTemplateDegreeList(degrees,per_comTable);
		this.getFormHM().put("noCanSaveDegreesList", list);
	}

}
