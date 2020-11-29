package com.hjsj.hrms.transaction.performance.achivement.kpiOriginalData;

import com.hjsj.hrms.businessobject.performance.achivement.kpiOriginalData.KpiOriginalDataBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:CheckDelKpiTargetTrans.java</p>
 * <p>Description:校验要删除的KPI指标交易类</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-07-25 10:09:23</p>
 * @author JinChunhai
 * @version 5.0
 */

public class CheckDelKpiTargetTrans extends IBusiness
{

    public void execute() throws GeneralException
    {

//		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
//		String opt = (String) hm.get("opt");
//		String delStr = (String) hm.get("deletestr");

    	String msg = "nouseded";
		String opt = (String) this.getFormHM().get("opt");
		String delStr = (String) this.getFormHM().get("deletestr");	
		delStr=delStr.replaceAll("／", "/");
		String delString = delStr.substring(0, delStr.length() - 1);
		String[] matters = delString.split("/");
		
		KpiOriginalDataBo bo = new KpiOriginalDataBo(this.getFrameconn(),this.userView);		
		msg = bo.checkKpiTarget(matters);
				
		this.getFormHM().put("msg",SafeCode.encode(msg));
		this.getFormHM().put("delStr",delStr);
		this.getFormHM().put("opt",opt);
		
    }
    
}