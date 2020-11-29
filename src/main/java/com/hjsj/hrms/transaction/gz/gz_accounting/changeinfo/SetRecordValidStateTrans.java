/**
 * 
 */
package com.hjsj.hrms.transaction.gz.gz_accounting.changeinfo;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 *<p>Title:设置记录有效标识</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-8-7:下午05:32:21</p> 
 *@author cmq
 *@version 4.0
 */
public class SetRecordValidStateTrans extends IBusiness {

	public void execute() throws GeneralException {
		String salaryid=(String)this.getFormHM().get("salaryid");
		String flag=(String)this.getFormHM().get("flag");
		String chgtype=(String)this.getFormHM().get("chgtype");
		String a0100=(String)this.getFormHM().get("a0100");
		String dbname=(String)this.getFormHM().get("dbname");
		
		try
		{
			SalaryTemplateBo templatebo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			templatebo.setRecordValid(chgtype, flag, dbname, a0100);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
