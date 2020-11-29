package com.hjsj.hrms.transaction.general.deci.leader;

import com.hjsj.hrms.businessobject.general.deci.leader.LeaderParam;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class GetFieldBySetNameTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String setname=(String)this.getFormHM().get("tablename");
			String id=(String)this.getFormHM().get("idv");			
			LeaderParam leaderParam=new LeaderParam(this.getFrameconn(),this.userView);		
			ArrayList codesetlist=leaderParam.getFieldBySetNameTrans(setname,this.userView);	
			this.getFormHM().put("codesetlist",codesetlist);
			this.getFormHM().put("idv",id);
			this.getFormHM().put("tablename",setname);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
   	        throw GeneralExceptionHandler.Handle(ex);   
		}
	}


}
