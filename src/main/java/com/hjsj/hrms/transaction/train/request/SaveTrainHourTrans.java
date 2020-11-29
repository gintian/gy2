package com.hjsj.hrms.transaction.train.request;

import com.hjsj.hrms.businessobject.train.TransDataBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * <p>Title:SaveTrainHourTrans.java</p>
 * <p>Description:保存标准培训学时</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-08-25 13:00:00</p>
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SaveTrainHourTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
	HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
	String theHour = (String)hm.get("theHour");
	TransDataBo bo = new TransDataBo(this.getFrameconn());
	
	bo.saveStuHour(theHour);	
	
	String isAutoHour = (String)this.getFormHM().get("isAutoHour");
	if("1".equals(isAutoHour))
	    bo.autoCalculateHour(theHour,"");	
    }
}
