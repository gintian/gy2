/**
 *xuanz
 * 2019年8月19日上午10:57:39
 */
package com.hjsj.hrms.module.kq.kqdata.transaction;

import com.hjsj.hrms.module.kq.kqdata.businessobject.KqExceptService;
import com.hjsj.hrms.module.kq.kqdata.businessobject.impl.KqExceptServiceImpl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * @author xuanz
 *
 */
public class KqExceptTrans extends IBusiness {

	
	@Override
	public void execute() throws GeneralException {
		String  guidkey=(String) this.getFormHM().get("guidkey");
		String  startDate=(String) this.getFormHM().get("startDate");
		String  endDate=(String) this.getFormHM().get("endDate");
		KqExceptService kqExceptService =new KqExceptServiceImpl(this.getUserView(), this.getFrameconn());
		this.getFormHM().put("tableConfig",kqExceptService.getKqExceptTableConfig(guidkey, startDate, endDate));
	}

}
