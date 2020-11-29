package com.hjsj.hrms.transaction.performance.evaluation.dealWithBusiness;

import com.hjsj.hrms.businessobject.performance.ResultFiledBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:SaveCodeAccordTrans.java</p>
 * <p>Description:保存代码对应</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-07-04 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class SaveCodeAccordTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {
    	
		String accordCodes = (String)this.getFormHM().get("strParm");
		accordCodes = PubFunc.keyWord_reback(accordCodes);
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String destCode = (String) hm.get("destCode");
		//String planId = (String) hm.get("planid");
		ResultFiledBo bo = new ResultFiledBo(this.getFrameconn());
		bo.saveCodeAccord(destCode, accordCodes);
		
    }

}
