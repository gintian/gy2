package com.hjsj.hrms.transaction.performance.nworkplan;

import com.hjsj.hrms.businessobject.performance.nworkplan.NworkPlanBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * AddOrUpdateMonthWorkPlanTrans.java
 * Description: 新增或者修改月报展示页面
 * Copyright (c) Department of Research and Development/Beijing/北京世纪软件有限公司.
 * All Rights Reserved.
 * @version 1.0  
 * Mar 7, 2013 6:07:05 PM Jianghe created
 */
public class AddOrUpdateMonthWorkPlanTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
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
		}catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
