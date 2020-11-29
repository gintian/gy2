package com.hjsj.hrms.transaction.hire.employSummarise.personnelEmploy;

import com.hjsj.hrms.businessobject.hire.EmployActualize;
import com.hjsj.hrms.businessobject.hire.PersonnelEmploy;
import com.hrms.hjsj.sys.Constant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SetPersonnelState extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			ArrayList list=(ArrayList)this.getFormHM().get("selectedlist");
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String toDbname=(String)hm.get("toDbname");
			String state=(String)hm.get("state");
			StringBuffer a0100s=new StringBuffer("");
			for(int i=0;i<list.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)list.get(i);
				a0100s.append(",'");
				a0100s.append((String)abean.get("a0100"));
				a0100s.append("'");
			}
		
			List infoSetList=userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
			
			EmployActualize employActualize=new EmployActualize(this.getFrameconn());
			String dbname=employActualize.getZP_DB_NAME();  //应用库前缀	
			if(list.size()>0)
			{
				PersonnelEmploy personnelEmploy=new PersonnelEmploy(this.getFrameconn());
				personnelEmploy.setState(a0100s.substring(1),list,state,dbname,toDbname,infoSetList);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
