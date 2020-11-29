package com.hjsj.hrms.transaction.kq.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SavaData_SrcTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String itemid=(String)this.getFormHM().get("items");
		String data=(String)this.getFormHM().get("sdata_src");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
	    StringBuffer ssql=new StringBuffer();
	    
	    try{
	    	 ssql.append("update kq_item set sdata_src='");
	              ssql.append(data);
	              ssql.append("' where item_id='");
	              ssql.append(itemid);
	              ssql.append("'");

	              dao.update(ssql.toString());
	    }  catch(Exception sqle)
        {
	        sqle.printStackTrace();
	        throw GeneralExceptionHandler.Handle(sqle);            
        }
		

	}

}
