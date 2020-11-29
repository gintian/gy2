package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:RefreshMainBodyTypeTrans.java</p>
 * <p>Description:刷新主体类别定义</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-02-17 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class RefreshMainBodyTypeTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String planId = (String) hm.get("plan_id");
		String object_type = (String) hm.get("object_type");
		String bodyTypeIds = (String) hm.get("bodyids");
		hm.remove("plan_id");
		hm.remove("object_type");
		hm.remove("bodyids");
		/*
	     * 考核主体类别
	     */
		ExamPlanBo bo = new ExamPlanBo(this.frameconn);
		this.getFormHM().put("bodyTypeIds",bodyTypeIds);
		ArrayList setlist = bo.searchCheckBody2(planId,object_type,bodyTypeIds);
		this.getFormHM().put("MainbodyTypeList", setlist);
    }
}
