/*
 * Created on 2005-7-8
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.dutyinfo;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DeleteDutydetailInfoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		 ArrayList selfinfolist=(ArrayList)this.getFormHM().get("selectedlist");
		 String setname = (String)this.getFormHM().get("setname");
	        if(selfinfolist==null||selfinfolist.size()==0)
	            return;
	        RecordVo vo = (RecordVo)selfinfolist.get(0);
	        if(!vo.getModelName().equalsIgnoreCase(setname))
	        	return;
	        ContentDAO dao=new ContentDAO(this.getFrameconn());
	        try
	        {
	            dao.deleteValueObject(selfinfolist);
	        }
		    catch(SQLException sqle)
		    {
		      sqle.printStackTrace();
		      throw GeneralExceptionHandler.Handle(sqle);
		    }

	}

}
