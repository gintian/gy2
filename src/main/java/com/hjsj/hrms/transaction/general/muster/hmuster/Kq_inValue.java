package com.hjsj.hrms.transaction.general.muster.hmuster;

import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class Kq_inValue  extends IBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {
		String condition=(String)this.getFormHM().get("condition");
		//condition = PubFunc.keyWord_reback(condition);
		condition=SafeCode.decode(condition);
		condition=condition.replaceAll("%20"," ");
		this.getFormHM().put("condition",condition);
	}

}
