package com.hjsj.hrms.transaction.hire.employActualize.personnelFilter;

import com.hjsj.hrms.businessobject.hire.EmployActualize;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;


public class ReviewTrans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList list=(ArrayList)this.getFormHM().get("selectedList");
		EmployActualize employActualize=new EmployActualize(this.getFrameconn());
		String summary="";
		if(list.size()==1)
		{
			LazyDynaBean abean=(LazyDynaBean)list.get(0);
			String  id=(String)abean.get("id");
			String[]  ids=id.split("/");
			summary=employActualize.getDescription(ids[0],ids[1]);
		}
		this.getFormHM().put("summary",summary);
		
	}

}
