package com.hjsj.hrms.transaction.performance.evaluation;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:SearchMergeModeTrans.java</p>
 * <p>Description>:汇总方式设置</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Feb 22, 2011 14:15:35 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class SearchMergeModeTrans extends IBusiness{
	
	public void execute() throws GeneralException
	{	
		try
		{
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			
			String mergeModePrams=(String)this.getFormHM().get("mergeModePrams");
			
			System.out.println(mergeModePrams+"------------------");
		
//			this.getFormHM().put("status", status);
		
		}catch (Exception e)
		{
			e.printStackTrace();
		}					
	}
}
