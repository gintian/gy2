/**
 * 
 */
package com.hjsj.hrms.transaction.gz.gz_accounting.changeinfo;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 *<p>Title:设置变动记录生效</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-8-7:下午03:01:57</p> 
 *@author cmq
 *@version 4.0
 */
public class SetChangeInfoValidTrans extends IBusiness {

	public void execute() throws GeneralException {
		String salaryid=(String)this.getFormHM().get("salaryid");		
		try
		{
			SalaryTemplateBo templatebo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			templatebo.importDataIntoGzTable();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}
