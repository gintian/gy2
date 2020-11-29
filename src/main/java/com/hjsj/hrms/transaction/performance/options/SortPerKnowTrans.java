package com.hjsj.hrms.transaction.performance.options;

import com.hjsj.hrms.businessobject.performance.options.PerKnowBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SortPerKnowTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		PerKnowBo bo = new PerKnowBo(this.frameconn);
		hm.put("sortlist",bo.sortList(this.frameconn));
		
	}

}
