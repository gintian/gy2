package com.hjsj.hrms.transaction.performance.objectiveManage.manageKeyMatter;

import com.hjsj.hrms.businessobject.performance.objectiveManage.manageKeyMatter.KeyMatterBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:DelKeyMatterTrans.java</p>
 * <p>Description:删除关健事件交易类</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-06-01 13:00:00</p>
 * @author JinChunhai
 * @version 1.0
 */

public class DelKeyMatterTrans extends IBusiness
{

    public void execute() throws GeneralException
    {

//		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
//		String delStr = (String) hm.get("deletestr");
    	
    	String msg="nohave03";
		String delStr=(String)this.getFormHM().get("deletestr");
		delStr = delStr.substring(0, delStr.length() - 1);
		String[] matters = delStr.replaceAll("／", "/").split("/");
		
		KeyMatterBo bo = new KeyMatterBo(this.getFrameconn());
		msg = bo.delKeyMatters(matters);
		
		this.getFormHM().put("msg",SafeCode.encode(msg));		
		
    }
}
