package com.hjsj.hrms.transaction.general.inform.emp.view;

import com.hjsj.hrms.businessobject.general.info.EmpMaintenanBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 *<p>Title:SearchDataTableTrans</p> 
 *<p>Description:显示＆隐藏指标</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-9-4:下午02:03:54</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class HideFieldTrans extends IBusiness {
	
	public  void execute()throws GeneralException
	{
			EmpMaintenanBo embo = new EmpMaintenanBo(this.getFrameconn());
			String fieldstr = (String)this.getFormHM().get("hidefieldstr");
			String setname = (String)this.getFormHM().get("setname");
			if(fieldstr!=null)
			{
				embo.dishidefield(fieldstr,setname);
			}

	}

}
