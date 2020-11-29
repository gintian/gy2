package com.hjsj.hrms.transaction.media;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class UpdateMultMediaInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String userbase=(String)this.getFormHM().get("userbase");
		String A0100=(String)this.getFormHM().get("a0100");
		String filetitle=(String)this.getFormHM().get("filetitle");
		String I9999=(String)this.getFormHM().get("i9999");
		RecordVo vo = new RecordVo(userbase.toLowerCase()+ "a00");
		if ( vo== null) 
			return;
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
	    //insert(vo,  file);
    	StringBuffer updatesql=new StringBuffer();
    	updatesql.append("update " + userbase.toLowerCase()+ "a00");
    	updatesql.append(" set Title='");
    	updatesql.append(filetitle);
    	updatesql.append("' where a0100='");
    	updatesql.append(A0100);
    	updatesql.append("' and i9999=");
    	updatesql.append(I9999);
    	try{
           dao.update(updatesql.toString());
        }catch(Exception ee)
        {
        	ee.printStackTrace();
			throw GeneralExceptionHandler.Handle(ee);	
        }
	}

}
