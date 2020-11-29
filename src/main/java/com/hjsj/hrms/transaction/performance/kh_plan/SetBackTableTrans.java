package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SetBackTableTrans.java</p>
 * <p>Description:设置反馈表</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-03-29 11:11:11</p>
 * @author JinChunhai
 * @version 5.0
 */

public class SetBackTableTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String cards = (String) hm.get("cards");
//		String plan_id = (String) hm.get("plan_id");
		String templateId = (String) hm.get("templateId");
		hm.remove("cards");
		hm.remove("templateId");
		
		ExamPlanBo bo = new ExamPlanBo(this.frameconn);
		ArrayList cardList = bo.getBackTableList(cards,templateId);
		String showBackTablesInfo = "";
		if(cardList.size()==0)
			showBackTablesInfo = "0";
//			showBackTablesInfo = ResourceFactory.getProperty("jx.parameter.showBackTableInfo");
		else
			showBackTablesInfo = "1";
//			showBackTablesInfo = ResourceFactory.getProperty("jx.parameter.showBackTableSet");
		this.getFormHM().put("showBackTablesInfo", showBackTablesInfo);
		this.getFormHM().put("cardList", cardList);
		
	}

}
