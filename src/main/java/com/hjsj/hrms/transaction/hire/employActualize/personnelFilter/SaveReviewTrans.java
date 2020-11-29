package com.hjsj.hrms.transaction.hire.employActualize.personnelFilter;

import com.hjsj.hrms.businessobject.hire.EmployActualize;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class SaveReviewTrans extends IBusiness {

	public void execute() throws GeneralException {
		String summary=(String)this.getFormHM().get("summary");
		String dbName=(String)this.getFormHM().get("dbName");
		ArrayList list=(ArrayList)this.getFormHM().get("selectedList");
		EmployActualize employActualize=new EmployActualize(this.getFrameconn());
		ArrayList id_list=new ArrayList();
		for(int i=0;i<list.size();i++)
		{
			LazyDynaBean abean=(LazyDynaBean)list.get(i);
			id_list.add((String)abean.get("id"));
		}
		employActualize.saveDescription(id_list,dbName,summary);

	}

}
