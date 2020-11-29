package com.hjsj.hrms.transaction.performance.objectiveManage.manageKeyMatter;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:LoadKeyMatterOrgTreeTrans.java</p>
 * <p>Description:加载关键事件机构树交易类</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-10-01 09:21:35</p>
 * @author JinChunhai
 * @version 5.0
 */

public class LoadKeyMatterOrgTreeTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
	
//		ArrayList dblist = userView.getPrivDbList();
		String userbase = "";//目前关键事件里的记录是在职人员库的
//		if (dblist.size() > 0)
//	    	userbase = dblist.get(0).toString();
//		else
		    userbase = "usr";
		this.getFormHM().put("userbase", userbase);
	
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
		String objecType=(String)hm.get("objecType");
		
//		String objecType = (String) this.getFormHM().get("objectType");
		
		if((objecType==null) || (objecType.trim().length()<=0))
			objecType="2";
			
		if(objecType!=null && "1".equals(objecType))
		{
		    this.getFormHM().put("flag", "0");
		    this.getFormHM().put("loadtype", "1");
		}else
		{
		    this.getFormHM().put("flag", "1");
		    this.getFormHM().put("loadtype", "0");
		}
	
    }
}
