package com.hjsj.hrms.transaction.performance.implement.kh_mainbody;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:CopyMainBodyTrans.java</p>
 * <p>Description:考核实施/复制考核主体</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-06-10 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class CopyMainBodyTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {
    	
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String objectID = (String) hm.get("objectID");//被复制的考核对象
		String planID = (String)this.getFormHM().get("planid");
		//复制主体可以跨计划复制,所以要记录下对象和计划的编号
		HashMap mainBodyCopyed = new HashMap();
		mainBodyCopyed.put("objectID", objectID);
		mainBodyCopyed.put("planID", planID);
		this.getFormHM().put("MainBodyCopyed", mainBodyCopyed);
		
    }
    
}
