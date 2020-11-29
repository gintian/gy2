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
public class DeleteItemTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	 public void execute() throws GeneralException {
        ArrayList itemlist=(ArrayList)this.getFormHM().get("selectedlist");
        if(itemlist==null||itemlist.size()==0)
            return;
        
        //声明要点表ArrayList对象
        
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        try
		{
	        for(int i=0;i<itemlist.size();i++)
	        {
	        	ArrayList pointlist=new ArrayList();
	        	pointlist.add(((RecordVo)itemlist.get(i)).getString("itemid").toString());
	         	dao.delete("delete from investigate_point where itemid=?",pointlist);
	        	dao.delete("delete from investigate_result where itemid=?",pointlist);
	     	 	dao.delete("delete from investigate_content where itemid=?",pointlist);
	        }
	        dao.deleteValueObject(itemlist);
        }
	    catch(Exception sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
	    ((RecordVo)this.getFormHM().get("itemov")).clearValues();
	    ((RecordVo)this.getFormHM().get("itemov")).setString("status","0");
    }


}
