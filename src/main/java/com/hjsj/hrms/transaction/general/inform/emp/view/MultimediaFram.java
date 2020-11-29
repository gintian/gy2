package com.hjsj.hrms.transaction.general.inform.emp.view;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:MultimediaFram</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-10-15:上午09:25:26</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class MultimediaFram extends IBusiness {

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
			String A0100 = (String)hm.get("a0100");
			String multimediaflag = (String)hm.get("multimediaflag");
			if(multimediaflag==null || "".equals(multimediaflag))
			{
				this.getFormHM().put("multimediaflag","");
			}
			String dbname = (String)this.getFormHM().get("dbname");
			this.getFormHM().put("a0100",A0100);
			this.getFormHM().put("dbname",dbname);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
