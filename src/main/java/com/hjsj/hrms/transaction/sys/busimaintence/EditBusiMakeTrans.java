package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hjsj.hrms.businessobject.sys.busimaintence.BusiSelStr;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 业务字段修改
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 8, 2008</p> 
 *@author sunxin
 *@version 4.0
 */
public class EditBusiMakeTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		String zijisel=(String)hm.get("zijisel");
		String id=(String) reqhm.get("id");
		BusiSelStr bss=new BusiSelStr();
		ArrayList syselist=new ArrayList();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
    	if(id!=null&&id.length()>0){
    		 syselist=bss.getSubsys(dao,id);
    	}
    	ArrayList zijilist=bss.getzijiStr(dao,id,"1");
    	hm.put("syselist",syselist);
    	hm.put("zijilist",zijilist);
    	hm.put("id", id);
    	hm.put("sysel", id);
    	if(zijisel==null||zijisel.length()<=0)
    	  hm.put("zijisel", "");
    	else
    	  hm.put("zijisel", zijisel);
	}

}
