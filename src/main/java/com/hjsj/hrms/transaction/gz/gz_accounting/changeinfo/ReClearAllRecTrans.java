/**
 * 
 */
package com.hjsj.hrms.transaction.gz.gz_accounting.changeinfo;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 *<p>Title:全部清空</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-8-7:下午03:00:50</p> 
 *@author cmq
 *@version 4.0
 */
public class ReClearAllRecTrans extends IBusiness {

	public void execute() throws GeneralException {
		String chgtype=(String)this.getFormHM().get("chgtype");
		String salaryid=(String)this.getFormHM().get("salaryid");		
		try
		{
			if(chgtype==null|| "".equalsIgnoreCase(chgtype)|| "0".equalsIgnoreCase(chgtype))
				return;
			SalaryTemplateBo templatebo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			templatebo.setAllRecordValid(chgtype, "0");
			this.getFormHM().put("checkall", "0");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
