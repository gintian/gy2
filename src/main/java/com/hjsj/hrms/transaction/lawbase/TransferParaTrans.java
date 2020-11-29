/*
 * Created on 2006-3-22
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * @author wxh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TransferParaTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub

		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String basetype=(String)hm.get("basetype");
		this.getFormHM().put("basetype",basetype);
        String base_id=(String)hm.get("base_id");
        if(!"root".equals(base_id)){
        	base_id = PubFunc.decrypt(SafeCode.decode(base_id));
        }
        this.getFormHM().put("base_id", base_id);
              
	}

}
