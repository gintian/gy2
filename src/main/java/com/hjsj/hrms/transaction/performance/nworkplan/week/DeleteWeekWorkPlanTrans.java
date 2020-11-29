package com.hjsj.hrms.transaction.performance.nworkplan.week;

import com.hjsj.hrms.businessobject.performance.nworkplan.NworkPlanBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * DeleteWeekWorkPlanTrans.java
 * Description: 删除周报记录
 * Copyright (c) Department of Research and Development/Beijing/北京世纪软件有限公司.
 * All Rights Reserved.
 * @version 1.0  
 * Mar 8, 2013 9:58:41 AM Jianghe created
 */
public class DeleteWeekWorkPlanTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String p0100 = (String)map.get("p0100");
			String record_num = (String)map.get("record_num");
			NworkPlanBo bo = new NworkPlanBo(this.getFrameconn(),this.userView,(String)this.userView.getHm().get("nbase"),(String)this.userView.getHm().get("a0100"));
			bo.deleteRecord(p0100,record_num);
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
