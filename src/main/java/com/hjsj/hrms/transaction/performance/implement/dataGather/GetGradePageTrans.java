package com.hjsj.hrms.transaction.performance.implement.dataGather;

import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hjsj.hrms.businessobject.performance.singleGradeBo_new;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:GetGradePageTrans.java</p> 
 *<p>Description:取得打分界面</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jun 20, 2008</p> 
 *@author JinChunhai
 *@version 4.0
 */

public class GetGradePageTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String object_id=(String)hm.get("object_id");
			object_id = PubFunc.decrypt(object_id);
			String mainbody_id=(String)hm.get("mainbody_id");
			String planId=(String)hm.get("planId");
			mainbody_id=mainbody_id==null?"":mainbody_id;
			mainbody_id = PubFunc.decrypt(mainbody_id);
			hm.remove("mainbody_id");
			
			singleGradeBo_new bo=new singleGradeBo_new(this.getFrameconn(),planId,this.getUserView());
			RecordVo plan_vo=bo.getPlan_vo();
			String gather_type = String.valueOf(plan_vo.getInt("gather_type"));
			String 	gradeHtml=bo.getGradeCardHtml(object_id, mainbody_id);	
			ArrayList objectsList=bo.getObjects(planId);
			
			String scoreflag=(String)bo.getPlanParam().get("scoreflag");  //=2混合，=1标度(默认值=混合)
			String busitype=(String) hm.get("busitype");//0 绩效  1 能力素质
			this.getFormHM().put("busitype",busitype);
			this.getFormHM().put("objectsList",objectsList);
			this.getFormHM().put("perPointNoGrade",bo.getPerPointNoGrade());
			this.getFormHM().put("isEntireysub",((String)bo.getPlanParam().get("isEntireysub")).toLowerCase());
			this.getFormHM().put("isScoreMainbody",(String)bo.getPerMainbodyBean(object_id,mainbody_id).get("fillctrl"));
			this.getFormHM().put("gather_type",String.valueOf(plan_vo.getInt("gather_type")));
			this.getFormHM().put("objectType",String.valueOf(plan_vo.getInt("object_type")));
			this.getFormHM().put("scoreflag",scoreflag);
			this.getFormHM().put("planParamSet", bo.getPlanParam());
			this.getFormHM().put("gradeHtml",gradeHtml);
			this.getFormHM().put("object_id",PubFunc.encrypt(object_id));
			this.getFormHM().put("mainbody_id",PubFunc.encrypt(mainbody_id));
			
			ExamPlanBo planbo = new ExamPlanBo(this.frameconn);
			this.getFormHM().put("planBodys",planbo.getPlanBodys(planId));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
