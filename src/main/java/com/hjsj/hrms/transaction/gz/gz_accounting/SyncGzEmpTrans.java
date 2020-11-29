package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryPkgBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 *<p>Title:SyncGzEmpTrans</p> 
 *<p>Description:工资发放的人员同步</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-6-30:上午09:44:45</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class SyncGzEmpTrans extends IBusiness{
	

	public void execute() throws GeneralException {
		String salaryid=(String)this.getFormHM().get("salaryid");
		try
		{
			/**薪资类别*/
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			gzbo.syncGzTableStruct();
			/**人员同步*/
			gzbo.syncGzEmp(this.userView.getUserName(),salaryid);
			
			SalaryPkgBo salaryPkgBo = new SalaryPkgBo(this.getFrameconn(),this.userView);
			salaryPkgBo.synSalaryTable(salaryid, gzbo.getGz_tablename());
			
			this.getFormHM().put("order_by", SafeCode.encode(PubFunc.encrypt("sync")));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}


}
