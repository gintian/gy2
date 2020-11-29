package com.hjsj.hrms.transaction.general.inform.emp.view;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SetOperPictureTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try
		{
			String A0100 = (String)this.getFormHM().get("a0100");
			String dbname = (String)this.getFormHM().get("dbname");
			this.getFormHM().put("a0100",A0100);
			this.getFormHM().put("dbname",dbname);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
