/*
 * Created on 2005-10-9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.resource_plan;

import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SearchOrgDeptPosTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
    	String pretype=(String)hm.get("pretype");
    	RecordVo vo = (RecordVo)this.getFormHM().get("zpplanDetailsvo");
		if("UM".equalsIgnoreCase(pretype))
		{	
			if(vo.getString("dept_id") != null && !"".equals(vo.getString("dept_id"))){
			   this.getFormHM().put("posparentcode",vo.getString("dept_id"));
			   this.getFormHM().put("pos_id_value","");
			}
		}

	}

}
