package com.hjsj.hrms.transaction.competencymodal.personPostModal;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:LoadOrgTreeTrans.java</p>
 * <p>Description:加载人岗匹配机构树交易类</p>
 * <p>Company:hjsj</p>
 * <p>create time:2012-01-10 11:11:11</p>
 * @author JinChunhai
 * @version 5.0
 */

public class LoadOrgTreeTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {
	
//		ArrayList dblist = userView.getPrivDbList();
		String userbase = "";//目前人岗匹配里的记录是在职人员库的
//		if (dblist.size() > 0)
//	    	userbase = dblist.get(0).toString();
//		else
		    userbase = "usr";
		this.getFormHM().put("userbase", userbase);
		
		
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
		String planId = (String)hm.get("plan_id");
		hm.remove("plan_id");
		if(planId==null || planId.trim().length()<=0)
			planId = "";
//		System.out.println("考核计划号："+plan_id);
		this.getFormHM().put("planId",planId);
/*		
//		String objecType = (String) this.getFormHM().get("objectType");
		
		if((objecType==null) || (objecType.trim().length()<=0))
			objecType="2";
			
		if(objecType!=null && objecType.equals("1"))
		{
		    this.getFormHM().put("flag", "0");
		    this.getFormHM().put("loadtype", "1");
		}else
		{
		    this.getFormHM().put("flag", "1");
		    this.getFormHM().put("loadtype", "0");
		}		
*/	
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
		String onlyFild = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
		this.getFormHM().put("onlyFild",onlyFild);
    }
    
}