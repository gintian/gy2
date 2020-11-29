package com.hjsj.hrms.transaction.general.card;

import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class PrintCardsTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException 
	{
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String userbase=(String)hm.get("userbase");
		String inforkind=(String)hm.get("inforkind");
		String tabid=(String)hm.get("tabid");
		this.getFormHM().put("userbase", userbase);
		this.getFormHM().put("inforkind", inforkind);
		this.getFormHM().put("tabid", tabid);
		String dbType="1";
		switch(Sql_switcher.searchDbServer())
	    {
			  case Constant.MSSQL:
		      {
		    	  dbType="1";
				  break;
		      }
			  case Constant.ORACEL:
			  { 
				  dbType="2";
				  break;
			  }
			  case Constant.DB2:
			  {
				  dbType="3";
				  break;
			  }
	    }
		this.getFormHM().put("dbType", dbType);
	}

}
