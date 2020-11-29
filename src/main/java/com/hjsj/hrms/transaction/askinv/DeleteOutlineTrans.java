/*
 * Created on 2005-5-26
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.askinv;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DeleteOutlineTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	 public void execute() throws GeneralException {
        ArrayList outlinelist=(ArrayList)this.getFormHM().get("selectedlist");
        
        
        if(outlinelist==null||outlinelist.size()==0)
            return;
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        try
        {
            dao.deleteValueObject(outlinelist);
            for(int i=0;i<outlinelist.size();i++)
            {
            	ArrayList list=new ArrayList();
            	list.add(((RecordVo)outlinelist.get(i)).getString("pointid").toString());
            	dao.delete("delete from investigate_result where pointid=?",list);
            }
        }
	    catch(Exception sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
	   
    	((RecordVo)this.getFormHM().get("outlineov")).clearValues();
    	((RecordVo)this.getFormHM().get("outlineov")).setString("status","1");
    	 ((RecordVo)this.getFormHM().get("outlineov")).setString("describestatus","0");
    	this.getFormHM().put("flag","1");
	    
		
    }

}
