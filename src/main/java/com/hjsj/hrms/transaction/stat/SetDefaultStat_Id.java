package com.hjsj.hrms.transaction.stat;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 保存默认统计项
 *<p>Title:SetDefaultStat_Id.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 25, 2007</p> 
 *@author sunxin
 *@version 4.0
 */
public class SetDefaultStat_Id extends IBusiness {

	 /* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException 
	{
		String stat_id=(String)this.getFormHM().get("statid");	  
	    Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
	    sysbo.setValue(Sys_Oth_Parameter.STAT_ID,stat_id);
	    sysbo.saveParameter();
	   
	}
	

}
