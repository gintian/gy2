package com.hjsj.hrms.transaction.standarduty;

import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class CheckItemLength extends IBusiness{
	public void execute() throws GeneralException {
		 
		    String sitemid = this.getFormHM().get("svalue").toString();
		    String titemid = this.getFormHM().get("tvalue").toString();
		    
		    int slength = DataDictionary.getFieldItem(sitemid).getItemlength();
		    int tlength = DataDictionary.getFieldItem(titemid).getItemlength();
		    
		    if(slength>tlength)
		    	this.getFormHM().put("mess", "N");
		    else
		    	this.getFormHM().put("mess", "Y");
		    
	}
   
}
