package com.hjsj.hrms.transaction.performance.implement.kh_mainbody;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:PasteMainBodyTrans.java</p>
 * <p>Description:考核实施/粘贴考核主体</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-06-10 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class PasteMainBodyTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {
    	
		HashMap mainBodyCopyed = (HashMap)this.getFormHM().get("MainBodyCopyed");//被复制的考核主体信息
		if(mainBodyCopyed==null || mainBodyCopyed.size()==0)
		    return;
		
		String planId = (String)this.getFormHM().get("planid");//被粘贴的计划
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String objectIds = (String) hm.get("objectIDs");//被粘贴的考核对象,可以是多个
		
		PerformanceImplementBo bo = new PerformanceImplementBo(this.getFrameconn(), this.getUserView());
		String[] objs = objectIds.split("@");
		for(int i=0;i<objs.length;i++)
		{
		    String obj = (String)objs[i];
		    bo.pasteKhMainBody(mainBodyCopyed,planId,obj);
		}	
		
		this.getFormHM().put("MainBodyCopyed", mainBodyCopyed);
		
    }

}
