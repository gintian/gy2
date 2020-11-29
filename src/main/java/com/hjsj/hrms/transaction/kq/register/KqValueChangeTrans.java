package com.hjsj.hrms.transaction.kq.register;

import com.hjsj.hrms.businessobject.kq.machine.KqValueChangeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class KqValueChangeTrans extends IBusiness{

	public void execute() throws GeneralException {
		KqValueChangeBo kq = new KqValueChangeBo(this.getFrameconn());
		HashMap kqItem_hash = kq.count_Leave();
		this.getFormHM().put("kqItem_hash", kqItem_hash);
	}

}
