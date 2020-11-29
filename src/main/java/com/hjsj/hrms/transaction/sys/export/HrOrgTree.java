package com.hjsj.hrms.transaction.sys.export;

import com.hjsj.hrms.businessobject.sys.export.HrSyncBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 
 *<p>Title:HrOrgTree.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Mar 24, 2008</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class HrOrgTree  extends IBusiness {

	public void execute() throws GeneralException {
		HrSyncBo hsb = new HrSyncBo(this.frameconn);
		String dbtemp = hsb.getTextValue(HrSyncBo.BASE);
		if(dbtemp==null|| "".equals(dbtemp)){
			
		}else{
			if(dbtemp.indexOf(",")==-1)
				this.getFormHM().put("dbname",dbtemp);
			else{
				this.getFormHM().put("dbname",dbtemp.substring(0,dbtemp.indexOf(",")));
			}
		}
		
	}
	
	
}
