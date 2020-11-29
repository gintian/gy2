package com.hjsj.hrms.transaction.performance.nworkplan;

import com.hjsj.hrms.businessobject.performance.nworkplan.NworkPlanBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * AppiaryWorkPlanTrans.java
 * Description: 报批月报
 * Copyright (c) Department of Research and Development/Beijing/北京世纪软件有限公司.
 * All Rights Reserved.
 * @version 1.0  
 * Mar 7, 2013 6:06:50 PM Jianghe created
 */
public class AppiaryWorkPlanTrans  extends IBusiness{
	public void execute() throws GeneralException 
	{
		try
		{
			String curr_user = (String)this.getFormHM().get("curr_user"); // 审批人编号
			String personPage = (String)this.getFormHM().get("personPage"); 
			String p0100 = (String)this.getFormHM().get("p0100"); 
			String state = (String)this.getFormHM().get("state"); 
			NworkPlanBo bo = new NworkPlanBo(this.getFrameconn(),this.userView,(String)this.userView.getHm().get("nbase"),(String)this.userView.getHm().get("a0100"));
			bo.appiaryRecord(p0100,curr_user,personPage);
			this.getFormHM().put("state", state);
		}catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
