package com.hjsj.hrms.transaction.ykcard;

import com.hjsj.hrms.businessobject.ykcard.RecordConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 记录方式
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jul 4, 2007:10:20:07 AM</p> 
 *@author sunxin
 *@version 4.0
 */
public class RecordConstantTrans extends IBusiness {
   public void execute() throws GeneralException {
	   RecordConstant recordConstant=new RecordConstant(this.getFrameconn());
	   String str_value=recordConstant.searchConstant();
	   this.getFormHM().put("recardconstant",str_value);
   }

}
