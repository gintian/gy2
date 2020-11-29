package com.hjsj.hrms.transaction.performance.evaluation.dealWithBusiness;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:SearchKhObjsTrans.java</p>
 * <p>Description:生成评语模板/保存评语</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-06-25 11:11:11</p>
 * @author JinChunhai
 * @version 1.0 
 */

public class SaveRemarkTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
    	
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String planId = (String) hm.get("planid");
		String objectIDs = (String) hm.get("objectIDs");
		String remarkTemp = (String) hm.get("template");
		
		PerEvaluationBo bo = new PerEvaluationBo(this.getFrameconn(),planId,"");
		bo.saveRemark(planId, objectIDs, remarkTemp);	
    }
    
}
