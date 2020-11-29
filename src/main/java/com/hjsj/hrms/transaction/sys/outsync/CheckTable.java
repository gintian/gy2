package com.hjsj.hrms.transaction.sys.outsync;

import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class CheckTable extends IBusiness{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {
		DbWizard dbw = new DbWizard(this.frameconn);
		if (!dbw.isExistTable("t_org_view", false))
			this.formHM.put("org_table", "0");
		if (!dbw.isExistTable("t_hr_view", false))
			this.formHM.put("hr_table", "0");
	}
	

}
