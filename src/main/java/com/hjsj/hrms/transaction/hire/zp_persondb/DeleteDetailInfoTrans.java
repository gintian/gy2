package com.hjsj.hrms.transaction.hire.zp_persondb;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class DeleteDetailInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList selfinfolist=(ArrayList)this.getFormHM().get("selectedlist");	
        if(selfinfolist==null||selfinfolist.size()==0)
            return;
       
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        try
        {
           dao.deleteValueObject(selfinfolist);
        }
	    catch(Exception sqle)
	    {
	       sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }

    }

}
