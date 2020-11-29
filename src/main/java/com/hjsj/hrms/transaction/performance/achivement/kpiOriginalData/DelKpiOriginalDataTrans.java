package com.hjsj.hrms.transaction.performance.achivement.kpiOriginalData;

import com.hjsj.hrms.businessobject.performance.achivement.kpiOriginalData.KpiOriginalDataBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:DelKpiTargetAssertTrans.java</p>
 * <p>Description:删除KPI原始数据</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-07-25 10:09:23</p>
 * @author JinChunhai
 * @version 5.0
 */

public class DelKpiOriginalDataTrans extends IBusiness
{

    public void execute() throws GeneralException
    {

//		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
//		String delStr = (String) hm.get("deletestr");
    	
    	String msg="nohave03";
		String delStr=(String)this.getFormHM().get("deletestr");	
		delStr = delStr.replaceAll("／", "/");
		delStr = delStr.substring(0, delStr.length() - 1);
		String[] matters = delStr.split("/");
		
		KpiOriginalDataBo bo = new KpiOriginalDataBo(this.getFrameconn(),this.userView);
		
		msg = bo.delDataValue(matters);
		
		this.getFormHM().put("msg",SafeCode.encode(msg));
		
    }
    
}