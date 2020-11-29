package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryPkgBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class TempSortGzTableDataTrans extends IBusiness {


	public void execute() throws GeneralException {
		String return_vo= (String)this.getFormHM().get("return_vo");
		String salaryid=(String)this.getFormHM().get("salaryid");
		try {
		//xuj 2009-10-21 增加人员顺序同步功能
		//<---
		/**薪资类别*/
		SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
		/**人员同步*/
		gzbo.syncGzEmp(this.userView.getUserName(),salaryid);
		SalaryPkgBo salaryPkgBo = new SalaryPkgBo(this.getFrameconn(),this.userView);
		salaryPkgBo.synSalaryTable(salaryid, gzbo.getGz_tablename());
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			this.getFormHM().put("return_vo", return_vo);
			this.getFormHM().put("salaryid", salaryid);
		}
		
	}

}
