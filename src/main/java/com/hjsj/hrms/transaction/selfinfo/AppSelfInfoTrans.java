package com.hjsj.hrms.transaction.selfinfo;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class AppSelfInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String userbase = userView.getDbname();//(String)this.getFormHM().get("userbase");
		String A0100 = userView.getA0100();//(String)this.getFormHM().get("a0100");
		String setname=(String)this.getFormHM().get("setname");
		String I9999=(String)this.getFormHM().get("i9999");
		 ContentDAO dao=new ContentDAO(this.getFrameconn());
	     try
	     {
			StringBuffer statesql=new StringBuffer();
			statesql.append("update ");
			statesql.append(userbase);
			statesql.append(setname);
			statesql.append(" set state='4' where a0100='");
			statesql.append(A0100);
			statesql.append("'");
			if(!"A01".equalsIgnoreCase(setname))
			{
				statesql.append(" and i9999=");
			    statesql.append(I9999);
			}
			//System.out.println(statesql.toString());
			dao.update(statesql.toString());
		 }
		 catch(Exception sqle)
		 {
		    sqle.printStackTrace();
		   throw GeneralExceptionHandler.Handle(sqle);
		 }
	}

}
