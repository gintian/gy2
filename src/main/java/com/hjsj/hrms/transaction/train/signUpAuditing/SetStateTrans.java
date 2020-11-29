package com.hjsj.hrms.transaction.train.signUpAuditing;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SetStateTrans extends IBusiness {

	public void execute() throws GeneralException {
		String[] selected=(String[])this.getFormHM().get("selected");
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String state=(String)hm.get("state");
		
		ContentDAO dao=null;
		try
		{
			dao=new ContentDAO(this.getFrameconn());
			for(int i=0;i<selected.length;i++)
			{
				String temp=selected[i];
				temp = PubFunc.keyWord_reback(temp);
				String[] temp_str=temp.split("/");
				dao.update("update r40 set r4013='"+state+"' where R4001='"+PubFunc.decrypt(SafeCode.decode(temp_str[0]))+"' and R4005='"+PubFunc.decrypt(SafeCode.decode(temp_str[1]))+"' and  NBase='"+PubFunc.decrypt(SafeCode.decode(temp_str[2]))+"'");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
