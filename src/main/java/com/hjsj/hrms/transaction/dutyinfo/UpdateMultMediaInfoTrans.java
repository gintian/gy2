package com.hjsj.hrms.transaction.dutyinfo;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 *<p>Title:UpdateMultMediaInfoTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 7, 2007</p> 
 *@author huaitao
 *@version 4.0
 */
public class UpdateMultMediaInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String code=(String)this.getFormHM().get("code");
		String kind = (String)this.getFormHM().get("kind");
		String filetitle=(String)this.getFormHM().get("filetitle");
		String I9999=(String)this.getFormHM().get("i9999");
		RecordVo vo = null;
		if("0".equalsIgnoreCase(kind))
			vo = new RecordVo("k00");
		else if("1".equalsIgnoreCase(kind))
			vo = new RecordVo("b00");
		if ( vo== null) 
			return;
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
	    //insert(vo,  file);
    	StringBuffer updatesql=new StringBuffer();
    	if("0".equalsIgnoreCase(kind))
    		updatesql.append("update k00 ");
		else if("1".equalsIgnoreCase(kind))
			updatesql.append("update b00 ");
    	updatesql.append(" set Title='");
    	updatesql.append(filetitle);
    	if("0".equalsIgnoreCase(kind))
    		updatesql.append("' where e01a1='");
    	else if("1".equalsIgnoreCase(kind))
    		updatesql.append("' where b0110='");
    	updatesql.append(code);
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
