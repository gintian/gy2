package com.hjsj.hrms.transaction.sys.bos.func;

import com.hjsj.hrms.businessobject.sys.bos.func.FuncMainBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;

import java.util.ArrayList;
import java.util.HashMap;

public class PayFuncMainTrans extends IBusiness {

	public void execute() throws GeneralException {

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		Document doc = null;
		if (this.getFormHM().get("function_dom") != null) {
			doc = (Document) this.getFormHM().get("function_dom");
		} else {

		}
		String funcid = (String) hm.get("funcid");
		String ctrl_ver = (String)hm.get("ctrl_ver");
        String lockVersion = (String)this.formHM.get("lockVersion");

		FuncMainBo funcbo = new FuncMainBo(this.getFrameconn());
		funcbo.setLockVersion(lockVersion);
        if(ctrl_ver!=null && ctrl_ver.length()>0)
        	funcbo.setCtrl_ver(ctrl_ver);
		ArrayList sortList = funcbo.getFuncList(funcid, doc);
		
		
			this.getFormHM().put("sortlist",sortList);
		///hm.put("sortlist",sortList(tabid));
			this.getFormHM().put("functionid",funcid);
	}

}