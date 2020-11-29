package com.hjsj.hrms.transaction.dutyinfo.sduty;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.List;

public class CheckOrgType extends IBusiness{
	
	public void execute() throws GeneralException {
		  String orgid = (String)this.getFormHM().get("orgid");
		  
		  try{
			  
			  String sql = " select '1' from vorganization where codeitemid = '"+orgid.substring(2)+"'";
			  
			  List rs = ExecuteSQL.executeMyQuery(sql);
			  if(rs.size()>0)
				  this.getFormHM().put("orgtype", "vorg");
			  else
				  this.getFormHM().put("orgtype", "org");
		  }catch (Exception e) {
			 e.printStackTrace(); 
		  }
	}

}
