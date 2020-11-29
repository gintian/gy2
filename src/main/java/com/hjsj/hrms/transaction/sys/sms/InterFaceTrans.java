package com.hjsj.hrms.transaction.sys.sms;

import com.hjsj.hrms.businessobject.sys.Sms_Parameter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.List;

public class InterFaceTrans extends IBusiness {

	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {	
		List list=null;
		try
		{
		    Sms_Parameter sparam=new Sms_Parameter(this.getFrameconn());
		    list= sparam.queryCommPort();
		    this.getFormHM().put("ywList", list);
		} catch (Exception ex){
  			ex.printStackTrace();
  			throw GeneralExceptionHandler.Handle(ex);
  		}
	}
}
