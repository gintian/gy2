package com.hjsj.hrms.transaction.propose;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author chenmengqing
 */
public class DeleteProposeTrans extends IBusiness {

    /* 
     * @see com.hrms.struts.facade.transaction.IBusiness#execute()
     */
    public void execute() throws GeneralException {
        ArrayList proposelist=(ArrayList)this.getFormHM().get("selectedlist");
        if(proposelist==null||proposelist.size()==0)
            return;
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        try
        {
            dao.deleteValueObject(proposelist);
        }
	    catch(SQLException sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }

    }

}
