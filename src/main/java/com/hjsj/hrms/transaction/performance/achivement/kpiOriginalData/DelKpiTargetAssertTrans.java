package com.hjsj.hrms.transaction.performance.achivement.kpiOriginalData;

import com.hjsj.hrms.businessobject.performance.achivement.kpiOriginalData.KpiOriginalDataBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:DelKpiTargetAssertTrans.java</p>
 * <p>Description:删除/撤销KPI指标交易类</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-07-25 10:09:23</p>
 * @author JinChunhai
 * @version 5.0
 */

public class DelKpiTargetAssertTrans extends IBusiness
{

    public void execute() throws GeneralException
    {

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String opt = (String) hm.get("opt");
		String delStr = (String) hm.get("deletestr");
		delStr = delStr.replaceAll("／", "/");
		delStr = delStr.substring(0, delStr.length() - 1);
		String[] matters = delStr.split("/");
		
		KpiOriginalDataBo bo = new KpiOriginalDataBo(this.getFrameconn(),this.userView);
		
		bo.delAboKpiTarget(opt,matters);
		
    }
    
}
