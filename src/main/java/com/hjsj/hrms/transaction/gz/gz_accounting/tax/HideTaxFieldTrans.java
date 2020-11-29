package com.hjsj.hrms.transaction.gz.gz_accounting.tax;

import com.hjsj.hrms.businessobject.gz.TaxMxBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author ${FengXiBin}
 *@version 4.0
  */
public class HideTaxFieldTrans extends IBusiness {
	
	public void execute() throws GeneralException {
		try
		{
			TaxMxBo taxbo=new TaxMxBo(this.getFrameconn());
			String hidestr = (String)this.getFormHM().get("hidefield");
			this.getFormHM().remove("hidefield");
			if(hidestr!=null && hidestr.trim().length()>0)
			{
				String[] hide = taxbo.gethidestr(hidestr) ;
				taxbo.hideTaxField(hide);
			}
		}catch(Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		
	}

}
