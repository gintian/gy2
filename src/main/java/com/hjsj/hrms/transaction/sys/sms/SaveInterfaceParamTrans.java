package com.hjsj.hrms.transaction.sys.sms;

import com.hjsj.hrms.businessobject.sys.Sms_Parameter;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveInterfaceParamTrans extends IBusiness {

    private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {
		
		String infor = "no";
		 
        try
        {
        	String saveStr = (String) this.getFormHM().get("saveStr");
        	if (null != saveStr && !"".equals(saveStr))
        	    saveStr = SafeCode.keyWord_reback(saveStr);
        
        	Sms_Parameter sparam = new Sms_Parameter(this.getFrameconn());
        	sparam.saveCommPort(saveStr);
        	
        	infor = "ok";
        } catch (Exception ex){
        	ex.printStackTrace();
        	throw GeneralExceptionHandler.Handle(ex);
        }
        	
        this.getFormHM().put("infor",infor);
	}
}
