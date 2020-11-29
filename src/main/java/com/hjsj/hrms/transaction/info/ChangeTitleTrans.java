/**
 * 
 */
package com.hjsj.hrms.transaction.info;

import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * @author Owner
 *
 */
public class ChangeTitleTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
//		 TODO Auto-generated method stub
		 String codeitemid=(String)this.getFormHM().get("codeitemid");
		 String uplevel=(String)this.getFormHM().get("uplevel");
		 int level=Integer.valueOf(uplevel).intValue();
		 if(codeitemid.length()<1){
			 this.getFormHM().put("name", "");
		 }else
		 this.getFormHM().put("name", this.codeToName(codeitemid, level));
		 
	}
	private String codeToName(String codeitemid,int uplevel){
		CodeItem item = null;
		if(uplevel>0){
			item = AdminCode.getCode("UM", codeitemid, uplevel);
		}else{
			item = AdminCode.getCode("UM", codeitemid);
		}
		return item.getCodename();
	}
}
