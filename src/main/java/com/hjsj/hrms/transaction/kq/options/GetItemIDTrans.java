package com.hjsj.hrms.transaction.kq.options;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class GetItemIDTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
        String base_id=(String)hm.get("item_id");
        this.getFormHM().put("codeitemid", base_id);

	}

}
