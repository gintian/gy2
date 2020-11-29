package com.hjsj.hrms.transaction.hire.employActualize.personnelFilter;

import com.hjsj.hrms.businessobject.hire.EmployActualize;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

public class SetStateTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String info="";
		String state="";
		String flag="";
		String isMailField="";
		ArrayList idList=new ArrayList();
		EmployActualize employActualize=new EmployActualize(this.getFrameconn());
		String dbname=employActualize.getZP_DB_NAME();
		if(hm!=null)
		{
			ArrayList selectedList=(ArrayList)this.getFormHM().get("selectedList");
			state=(String)hm.get("state");
			flag=(String)hm.get("flag");   //1:XXXA01  2:Z05									
			hm.remove("flag");					
			String temp="";
			for(int i=0;i<selectedList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)selectedList.get(i);
				temp=(String)abean.get("id");
				idList.add(temp);
			}
		}
		else
		{
			String id=(String)this.getFormHM().get("id");
			state=(String)this.getFormHM().get("state");
			flag=(String)this.getFormHM().get("flag");   //1:XXXA01  2:Z05	
			if("2".equals(flag))
				isMailField=(String)this.getFormHM().get("isMailField");
			idList.add(id);
			
		}
		if(idList.size()>0)
		{
			String userid="";
			if(this.userView.getA0100()!=null&&!"".equals(this.userView.getA0100().trim()))
			{
				userid=this.userView.getDbname()+"`"+this.userView.getA0100();
			}
			else
			{
				userid=this.userView.getUserId();
			}
			info=employActualize.setState(idList,state,dbname,flag,userid,isMailField);
		}
		this.getFormHM().put("info",info);
	}

}
