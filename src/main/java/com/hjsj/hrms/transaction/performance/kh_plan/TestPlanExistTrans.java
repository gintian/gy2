package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:TestPlanExistTrans.java</p>
 * <p>Description:测试考核计划是否存在</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-11-06 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class TestPlanExistTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {

		String planId = (String) this.getFormHM().get("thePlan");
		ExamPlanBo bo = new ExamPlanBo(this.getFrameconn());
	
		String flag = "1";
		if (bo.isExist(planId))
		    flag = "0";// 编辑
		else
		    flag = "1";// 新增
	
		this.getFormHM().put("flag", flag);
    }

}
