package com.hjsj.hrms.transaction.performance.evaluation.dealWithBusiness;

import com.hjsj.hrms.businessobject.performance.ResultFiledBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchCodeAccordTrans.java</p>
 * <p>Description:初始化代码对应</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-07-04 11:11:11</p>
 * @author JinChunhai
 * @version 1.0 
 */

public class SearchCodeAccordTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {
    	
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String sourceField = (String) hm.get("sourceField");
		String destCode = (String) hm.get("destCode");
		String planId = (String) hm.get("planid");
		ResultFiledBo bo = new ResultFiledBo(planId,this.getFrameconn());
		ArrayList list = bo.getNoAccordCodes(sourceField,destCode);
		this.getFormHM().put("SourceCodes", list);//源代码为代码对应存储表中没有建立对应关系的代码
		list = bo.getTargetCodes(destCode);
		this.getFormHM().put("TargetCodes", list);
		list=bo.getHaveAccordCodes(destCode);
		this.getFormHM().put("AccordCodes", list);	
		
    }

}
