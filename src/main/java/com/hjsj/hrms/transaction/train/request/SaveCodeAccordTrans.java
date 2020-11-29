package com.hjsj.hrms.transaction.train.request;

import com.hjsj.hrms.businessobject.train.TrainClassArchiveBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * <p>Title:SaveCodeAccordTrans.java</p>
 * <p>Description:保存代码对应</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-07-04 13:00:00</p>
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class SaveCodeAccordTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
	String accordCodes = (String)this.getFormHM().get("strParm");
	HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	String destCode = (String) hm.get("destCode");
	String sourceField = (String) hm.get("sourceField");
	TrainClassArchiveBo bo = new TrainClassArchiveBo("", this.getFrameconn());
	bo.saveCodeAccord(destCode, accordCodes,sourceField);
    }

}
