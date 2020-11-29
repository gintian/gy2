package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SendEmailBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class TemplateHasExportDataTrans extends IBusiness{

	public void execute() throws GeneralException {

     try
      {
    	 //hashvo.setValue("id",id);
         //hashvo.setValue("code",code);
        // hashvo.setValue("salaryid",salaryid);
    	 String id=(String)this.getFormHM().get("id");
    	 String code=(String)this.getFormHM().get("code");
    	 String salaryid=(String)this.getFormHM().get("salaryid");
    	 String num=(String)this.getFormHM().get("num");
    	 SendEmailBo bo = new SendEmailBo(this.getFrameconn());
    	 String tableName=this.userView.getUserName()+"_salary_"+salaryid;
    	 boolean bool=bo.hasData(id,code,tableName);
    	 String flag="";
    	 if(bool)
    	 {
    		 flag="1";
    	 }
    	 else
    	 {
    		 flag="2";
    	 }
    	 this.getFormHM().put("code",code);
    	 this.getFormHM().put("salaryid",salaryid);
    	 this.getFormHM().put("nflag",flag);
    	 this.getFormHM().put("id",id);
    	 this.getFormHM().put("num",num);
    }catch(Exception e)
     {
    	  e.printStackTrace();
    	  throw GeneralExceptionHandler.Handle(e);
     }
		
	}

}
