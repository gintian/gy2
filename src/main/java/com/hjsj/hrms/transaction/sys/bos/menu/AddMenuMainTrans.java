package com.hjsj.hrms.transaction.sys.bos.menu;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class AddMenuMainTrans extends IBusiness {

	public void execute() throws GeneralException {
			HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String parentid=(String)hm.get("menuid");
		if("root".equals(parentid)){
			parentid="-1";
		}
			this.getFormHM().put("parentid", parentid);
			this.getFormHM().put("validate","false"); //添加默认为否 changxy 20160621
	}


}
