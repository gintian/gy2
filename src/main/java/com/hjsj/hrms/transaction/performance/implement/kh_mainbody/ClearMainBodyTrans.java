package com.hjsj.hrms.transaction.performance.implement.kh_mainbody;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:ClearMainBodyTrans.java</p>
 * <p>Description:考核实施/清除考核主体</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-06-01 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class ClearMainBodyTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {
    	
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String delStr = (String) hm.get("deletestr");
		String[] objects = delStr.split("@");
		String 	plan_id = (String)this.getFormHM().get("planid");
		
		PerformanceImplementBo bo = new PerformanceImplementBo (this.getFrameconn(),this.getUserView());
		bo.clearKhMainBody(objects,plan_id);

    }

}
