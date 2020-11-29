package com.hjsj.hrms.transaction.performance.achivement.kpiOriginalData;

import com.hjsj.hrms.businessobject.performance.achivement.kpiOriginalData.KpiOriginalDataBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:ComBackKpiOriginalDataTrans.java</p>
 * <p>Description:生效或退回KPI原始数据</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-07-29 15:45:28</p>
 * @author JinChunhai
 * @version 5.0
 */

public class ComBackKpiOriginalDataTrans extends IBusiness
{

    public void execute() throws GeneralException
    {

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String opt = (String) hm.get("opt");
		String comparestr = (String) hm.get("comparestr");
		comparestr = comparestr.replaceAll("／", "/");
		comparestr = comparestr.substring(0, comparestr.length() - 1);
		String[] matters = comparestr.split("/");
		
		KpiOriginalDataBo bo = new KpiOriginalDataBo(this.getFrameconn(),this.userView);
		bo.comBackData(opt,matters);
		
    }
    
}
