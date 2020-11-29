package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchProAppraiseTrans.java</p>
 * <p>Description:描述性评议项</p>
 * <p>Company:hjsj</p>
 * <p>create time:2014-08-16 13:28:11</p> 
 * @author JinChunhai
 * @version 7.0
 */

public class SearchProAppraiseTrans extends IBusiness
{
	
	public void execute() throws GeneralException
	{
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String plan_id = (String) hm.get("plan_id");
		
		ExamPlanBo bo = new ExamPlanBo(plan_id,this.frameconn);
		// 创建临时表并插入数据	
		String tempTable = "t#des_review";
		if (bo.isExist(plan_id))// 更新操作
		{
			bo.createTempTable(tempTable);
		}
		ArrayList list = bo.getTempData(tempTable);
	    this.getFormHM().put("extproList",list);
	    this.getFormHM().put("addDescription","");
	    				
	}
	
}