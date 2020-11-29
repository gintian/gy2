package com.hjsj.hrms.transaction.kq.options;

import com.hjsj.hrms.valueobject.common.FeastType;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class DeleteFeastTypeTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		
		ArrayList feastlist=(ArrayList)this.getFormHM().get("selectedlist");
        if(feastlist==null||feastlist.size()==0)
            return;
        StringBuffer fes=null;
        ContentDAO dao=new ContentDAO(this.getFrameconn());  
        try
        {
         
             for(int i=0;i<feastlist.size();i++){
            	fes=new StringBuffer();
            	FeastType vo=(FeastType)feastlist.get(i);
            	Integer mm =Integer.valueOf(vo.getFeast_id());
            	fes.append("delete from kq_feast where feast_id =");
            	fes.append(mm.intValue());
            	dao.delete(fes.toString(),new ArrayList());
            }
                      
        }
	    catch(Exception ee)
	    {
	      ee.printStackTrace();
	      throw GeneralExceptionHandler.Handle(ee);
	    }

	}

}
