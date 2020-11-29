package com.hjsj.hrms.transaction.performance.nworkplan.week;

import com.hjsj.hrms.businessobject.performance.nworkplan.NworkPlanBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * CollectWeekWorkPlanTrans.java
 * Description: 提取上周周报计划
 * Copyright (c) Department of Research and Development/Beijing/北京世纪软件有限公司.
 * All Rights Reserved.
 * @version 1.0  
 * Mar 8, 2013 9:54:25 AM Jianghe created
 */
public class CollectWeekWorkPlanTrans extends IBusiness{

	public void execute() throws GeneralException {
		try {
			String state=(String)this.getFormHM().get("state");
			String p0100=(String)this.getFormHM().get("p0100");
			String summarizeFields=(String)this.getFormHM().get("summarizeFields"); 
			String planFields=(String)this.getFormHM().get("planFields"); 
			String personPage=(String)this.getFormHM().get("personPage"); 
			String isChuZhang=(String)this.getFormHM().get("isChuZhang"); 
			String summarizeTime=(String)this.getFormHM().get("summarizeTime");
			//2013-03-31－－2013-04-06
			summarizeTime=summarizeTime.replaceAll("－", "-"); 
			String planYear_start=(String)this.getFormHM().get("planYear_start");
			String planMonth_start=(String)this.getFormHM().get("planMonth_start");
			String planDay_start=(String)this.getFormHM().get("planDay_start");
			String planYear_end=(String)this.getFormHM().get("planYear_end");
			String planMonth_end=(String)this.getFormHM().get("planMonth_end");
			String planDay_end=(String)this.getFormHM().get("planDay_end");
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String s_dString = planYear_start+"-"+planMonth_start+"-"+planDay_start;
			String e_dString = planYear_end+"-"+planMonth_end+"-"+planDay_end;
			Date jhS_date = format.parse(s_dString);
			Date jhE_date=format.parse(e_dString);
			NworkPlanBo bo = new NworkPlanBo(this.getFrameconn(),this.userView,(String)this.userView.getHm().get("nbase"),(String)this.userView.getHm().get("a0100"));
			ArrayList zongjieFieldsList = bo.getJihuaOrZongjieFieldsList("","",null,"",summarizeFields);
			ArrayList jihuaFieldsList = bo.getJihuaOrZongjieFieldsList("","",null,"",planFields);
			String message = bo.collectRecordWeek(state, p0100, summarizeTime, jhS_date, jhE_date, jihuaFieldsList, zongjieFieldsList, personPage, isChuZhang);
			this.getFormHM().put("message", message);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
