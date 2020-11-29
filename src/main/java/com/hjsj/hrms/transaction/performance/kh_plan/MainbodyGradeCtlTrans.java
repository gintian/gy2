package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class MainbodyGradeCtlTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String planId = (String) hm.get("plan_id");
		String object_type = (String) hm.get("object_type1");
		String bodyTypeIds = (String) hm.get("bodyids");
		String mainbodyGradeCtl = (String) hm.get("mainbodyGradeCtl");
		String allmainbodyGradeCtl = (String) hm.get("allmainbodyGradeCtl");
		hm.remove("plan_id");
		hm.remove("object_type1");
		hm.remove("bodyids");
		hm.remove("mainbodyGradeCtl");
		hm.remove("allmainbodyGradeCtl");
		/*
	     * 考核主体类别
	     */
		ExamPlanBo bo = new ExamPlanBo(this.frameconn);
		this.getFormHM().put("mainbodybodyid", mainbodyGradeCtl);
		this.getFormHM().put("allmainbodybody", allmainbodyGradeCtl);
		ArrayList setlist = bo.searchMainbodyGradeBody(planId,object_type,bodyTypeIds,mainbodyGradeCtl,allmainbodyGradeCtl);
		this.getFormHM().put("mainbodyGradetypeList", setlist);

	}
	
}
