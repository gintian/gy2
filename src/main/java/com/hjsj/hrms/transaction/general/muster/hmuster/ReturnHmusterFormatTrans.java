package com.hjsj.hrms.transaction.general.muster.hmuster;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class ReturnHmusterFormatTrans extends IBusiness {
	public void execute() throws GeneralException {
		
		/**  由于form属性为session，所以返回格式页面时清空该页面的属性值    **/
		
		this.getFormHM().put("clearFormat","1");

	}

}
