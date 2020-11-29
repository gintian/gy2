package com.hjsj.hrms.transaction.general.template;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class ReturnRoleListTrans extends IBusiness {
	
	 public void execute() throws GeneralException {
	        ArrayList list=(ArrayList)this.getFormHM().get("selectedlist");	 
	        this.getFormHM().put("relist", list);
	     
	 }
	 
  
}
