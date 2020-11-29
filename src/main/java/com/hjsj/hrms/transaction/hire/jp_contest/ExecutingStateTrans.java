package com.hjsj.hrms.transaction.hire.jp_contest;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 *<p>Title:ExecutingStateTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 19, 2007</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class ExecutingStateTrans extends IBusiness{
	public void execute () throws GeneralException{
		try{
			
			String ids = "";
			if(((String)this.getFormHM().get("selectIds")) != null && ((String)this.getFormHM().get("selectIds")).trim().length()>0)
				ids = (String)this.getFormHM().get("selectIds");
			if(ids.indexOf(",") != -1){
				ids=ids.substring(1);
			}
			String state = (String)this.getFormHM().get("state");
			String sql = "update Z07 set Z0713 ='"+state+"' where Z0700 in("+ids+")";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			int i = dao.update(sql);
			if(i>0)
			{
				this.getFormHM().put("have","yes");
			}else
			{
				this.getFormHM().put("have","no");
			}

		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	

}
