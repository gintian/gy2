package com.hjsj.hrms.transaction.performance.kh_result;

import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class ShowOrgTreeTrans extends IBusiness{
	public void execute() throws GeneralException {
		try
		{
			//非在职人员不允许使用改功能
			if(!"USR".equalsIgnoreCase(userView.getDbname())) {
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("notUsr.no.use.func")));
			}
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String isCloseButton="0";
			if(map.get("isClose")!=null)
			{
				isCloseButton=(String)map.get("isClose");
				map.remove("isClose");
			}
			this.getFormHM().put("isCloseButton", isCloseButton);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
