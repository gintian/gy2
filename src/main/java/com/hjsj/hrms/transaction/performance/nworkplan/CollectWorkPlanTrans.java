package com.hjsj.hrms.transaction.performance.nworkplan;

import com.hjsj.hrms.businessobject.performance.nworkplan.NworkPlanBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * CollectWorkPlanTrans.java
 * Description: 提取上周期计划
 * Copyright (c) Department of Research and Development/Beijing/北京世纪软件有限公司.
 * All Rights Reserved.
 * @version 1.0  
 * Mar 7, 2013 6:06:34 PM Jianghe created
 */
public class CollectWorkPlanTrans extends IBusiness{
	public void execute() throws GeneralException 
	{
		try {
			String state=(String)this.getFormHM().get("state");
			String p0100=(String)this.getFormHM().get("p0100");
			String currentYear=(String)this.getFormHM().get("currentYear");
			String currentMonth=(String)this.getFormHM().get("currentMonth");
			String summarizeFields=(String)this.getFormHM().get("summarizeFields"); 
			String planFields=(String)this.getFormHM().get("planFields"); 
			String personPage=(String)this.getFormHM().get("personPage"); 
			String isChuZhang=(String)this.getFormHM().get("isChuZhang"); 
			NworkPlanBo bo = new NworkPlanBo(this.getFrameconn(),this.userView,(String)this.userView.getHm().get("nbase"),(String)this.userView.getHm().get("a0100"));
			ArrayList zongjieFieldsList = bo.getJihuaOrZongjieFieldsList("","",null,"",summarizeFields);
			ArrayList jihuaFieldsList = bo.getJihuaOrZongjieFieldsList("","",null,"",planFields);
			String message = bo.collectRecord(state,p0100,currentYear,currentMonth,jihuaFieldsList,zongjieFieldsList,personPage,isChuZhang);
			this.getFormHM().put("message", message);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
