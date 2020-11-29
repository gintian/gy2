package com.hjsj.hrms.transaction.sys.bos.func;

import com.hjsj.hrms.businessobject.sys.bos.func.FuncMainBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;

import java.util.HashMap;

public class SavePayFuncMainTrans extends IBusiness {

	public void execute() throws GeneralException {
		Document	doc = (Document) this.getFormHM().get("function_dom");
		String functionid = (String) this.getFormHM().get("functionid");
		HashMap hm  = (HashMap) this.getFormHM().get("requestPamaHM");
		String sorting = (String)hm.get("sorting");
		String ctrl_ver = (String)hm.get("ctrl_ver");
        String lockVersion = (String)this.formHM.get("lockVersion");
        
		FuncMainBo funcbo = new FuncMainBo(this.getFrameconn());
		funcbo.setLockVersion(lockVersion);
        if(ctrl_ver!=null && ctrl_ver.length()>0)
        	funcbo.setCtrl_ver(ctrl_ver);
		funcbo.saveSortFuncList(functionid,sorting, doc);
		
		
			this.getFormHM().put("sorting",sorting);
	}

}