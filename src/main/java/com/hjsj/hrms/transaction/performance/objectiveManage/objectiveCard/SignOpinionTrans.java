package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard;

import com.hjsj.hrms.businessobject.performance.objectiveManage.ParseXmlBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 * 
 *<p>Title:</p> 
 *<p>Description:签批</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Apr 21, 2009</p> 
 *@author dengcan
 *@version 4.2
 */
public class SignOpinionTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			String p0400=(String)this.getFormHM().get("p0400");
			String opinion=SafeCode.decode((String)this.getFormHM().get("opinion"));
			ParseXmlBo bo=new ParseXmlBo(this.getFrameconn());
			bo.insertContext(this.userView.getA0100(),this.userView.getDbname(),opinion,p0400,"opinions","","");
			
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
