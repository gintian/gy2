package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_field.KhFieldBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchSortFieldClassTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
			String pointsetid = (String)map.get("pointsetid");
			String subsys_id = (String)map.get("subsys_id");
			KhFieldBo bo = new KhFieldBo(this.getFrameconn(),this.userView);
			ArrayList  list = bo.getFieldClassToSort(pointsetid, subsys_id);
			this.getFormHM().put("list",list);
			this.getFormHM().put("subsys_id",subsys_id);
			this.getFormHM().put("pointsetid",pointsetid);
			this.getFormHM().put("sorttype","1");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
