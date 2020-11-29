package com.hjsj.hrms.transaction.performance.nworkplan.week;

import com.hjsj.hrms.businessobject.performance.nworkplan.NworkPlanBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * AddOrUpdateWeekWorkPlanTrans.java
 * Description: 新增或者修改周报展示页面
 * Copyright (c) Department of Research and Development/Beijing/北京世纪软件有限公司.
 * All Rights Reserved.
 * @version 1.0  
 * Mar 8, 2013 9:54:03 AM Jianghe created
 */
public class AddOrUpdateWeekWorkPlanTrans extends IBusiness{
	
	public void execute() throws GeneralException {
		 try {
			
			 HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
				String planYear_start=(String)this.getFormHM().get("planYear_start");
				String planMonth_start=(String)this.getFormHM().get("planMonth_start");
				String planDay_start=(String)this.getFormHM().get("planDay_start");
				String planYear_end=(String)this.getFormHM().get("planYear_end");
				String planMonth_end=(String)this.getFormHM().get("planMonth_end");
				String planDay_end=(String)this.getFormHM().get("planDay_end");
				String summarizeYear=(String)this.getFormHM().get("summarizeYear");
				String summarizeTime=(String)this.getFormHM().get("summarizeTime");
				if(summarizeTime!=null)
			    	summarizeTime=summarizeTime.replaceAll("－", "-"); 
				//1 新增 2 修改
				String type=(String)map.get("type");
				String log_type=(String)map.get("log_type");
				String p0100=(String)this.getFormHM().get("p0100");
				String record_num=(String)map.get("record_num");
				NworkPlanBo bo = new NworkPlanBo(this.getFrameconn(),this.userView,(String)this.userView.getHm().get("nbase"),(String)this.userView.getHm().get("a0100"));
				if("1".equals(log_type)){
					String planFields = (String)this.getFormHM().get("planFields");
					ArrayList jihuaFieldsList = bo.getJihuaOrZongjieFieldsList(p0100,record_num,type,log_type,planFields);
					this.getFormHM().put("jihuaFieldsList", jihuaFieldsList);
				}else{
					String summarizeFields = (String)this.getFormHM().get("summarizeFields");
					ArrayList zongjieFieldsList = bo.getJihuaOrZongjieFieldsList(p0100,record_num,type,log_type,summarizeFields);
					this.getFormHM().put("zongjieFieldsList", zongjieFieldsList);
				}
				this.getFormHM().put("p0100", p0100);
				this.getFormHM().put("type", type);
				this.getFormHM().put("log_type", log_type);
				this.getFormHM().put("planYear_start", planYear_start);
				this.getFormHM().put("planMonth_start", planMonth_start);
				this.getFormHM().put("planDay_start", planDay_start);
				this.getFormHM().put("planYear_end", planYear_end);
				this.getFormHM().put("planMonth_end", planMonth_end);
				this.getFormHM().put("planDay_end", planDay_end);
				this.getFormHM().put("summarizeYear", summarizeYear);
				this.getFormHM().put("summarizeTime", summarizeTime);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

		
	}
}
