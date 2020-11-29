package com.hjsj.hrms.transaction.performance.nworkplan;

import com.hjsj.hrms.businessobject.performance.nworkplan.NworkPlanBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * SaveMonthWorkPlanTrans.java
 * Description: 保存月报
 * Copyright (c) Department of Research and Development/Beijing/北京世纪软件有限公司.
 * All Rights Reserved.
 * @version 1.0  
 * Mar 7, 2013 6:07:55 PM Jianghe created
 */
public class SaveMonthWorkPlanTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String saveflag = (String)map.get("saveflag");
			NworkPlanBo bo = new NworkPlanBo(this.getFrameconn(),this.userView,(String)this.userView.getHm().get("nbase"),(String)this.userView.getHm().get("a0100"));
			String type=(String)this.getFormHM().get("type");
			String log_type = (String)this.getFormHM().get("log_type");
			String p0100=(String)this.getFormHM().get("p0100");
			String personPage=(String)this.getFormHM().get("personPage");
			String isChuZhang=(String)this.getFormHM().get("isChuZhang");
			String currentYear=(String)this.getFormHM().get("currentYear");
			String currentMonth=(String)this.getFormHM().get("currentMonth");
			String nextYear=(String)this.getFormHM().get("nextYear");
			String nextMonth=(String)this.getFormHM().get("nextMonth");
			
			
			String planFields=(String)this.getFormHM().get("planFields");
			String summarizeFields=(String)this.getFormHM().get("summarizeFields");
			ArrayList jihuaFieldsList = (ArrayList)this.getFormHM().get("jihuaFieldsList");
			ArrayList zongjieFieldsList = (ArrayList)this.getFormHM().get("zongjieFieldsList");
			if("1".equals(type)){
				//新增
				if("1".equals(log_type)){
					p0100 = bo.saveRecord(personPage,isChuZhang,type,log_type,p0100,"",jihuaFieldsList,currentYear,currentMonth,nextYear,nextMonth);
					jihuaFieldsList = bo.getJihuaOrZongjieFieldsList(p0100,"",type,log_type,planFields);
					this.getFormHM().put("jihuaFieldsList", jihuaFieldsList);
				}else{
					p0100 = bo.saveRecord(personPage,isChuZhang,type,log_type,p0100,"",zongjieFieldsList,currentYear,currentMonth,nextYear,nextMonth);
					zongjieFieldsList = bo.getJihuaOrZongjieFieldsList(p0100,"",type,log_type,summarizeFields);
					this.getFormHM().put("zongjieFieldsList", zongjieFieldsList);
				}
			}else{
				//修改
				String record_num=(String)map.get("record_num");
				if("1".equals(log_type)){
					p0100 = bo.saveRecord(personPage,isChuZhang,type,log_type,p0100,record_num,jihuaFieldsList,currentYear,currentMonth,nextYear,nextMonth);
				}else{
					p0100 = bo.saveRecord(personPage,isChuZhang,type,log_type,p0100,record_num,zongjieFieldsList,currentYear,currentMonth,nextYear,nextMonth);
				}
			}
			this.getFormHM().put("saveflag", saveflag);
			this.getFormHM().put("p0100", p0100);
		}catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
