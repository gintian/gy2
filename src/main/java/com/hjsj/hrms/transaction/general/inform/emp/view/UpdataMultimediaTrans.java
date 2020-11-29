package com.hjsj.hrms.transaction.general.inform.emp.view;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 *<p>Title:UpdataMultimediaTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 1, 2007</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class UpdataMultimediaTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		
		String filetitle=(String)this.getFormHM().get("filetitle");
		String I9999=(String)this.getFormHM().get("i9999");
		String kind=(String)this.getFormHM().get("kind");
		String A0100=(String)this.getFormHM().get("a0100");
		this.getFormHM().put("filetitle","");
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
	    //insert(vo,  file);
    	StringBuffer updatesql=new StringBuffer();
    	if("6".equals(kind))
    	{
    		String userbase=(String)this.getFormHM().get("dbname");
    		RecordVo vo = new RecordVo(userbase.toLowerCase()+ "a00");
    		if ( vo== null) 
    			return;
    		updatesql.append("update " + userbase.toLowerCase()+ "a00");
    		updatesql.append(" set Title='");
        	updatesql.append(filetitle);
        	updatesql.append("' where a0100='");
        	updatesql.append(A0100);
        	updatesql.append("' and i9999=");
        	updatesql.append(I9999);
    	}else if("0".equals(kind))
    	{
    		RecordVo vo = new RecordVo("k00");
    		if ( vo== null) 
    			return;
    		updatesql.append("update k00 ");
    		updatesql.append(" set Title='");
        	updatesql.append(filetitle);
        	updatesql.append("' where e01a1='");
        	updatesql.append(A0100);
        	updatesql.append("' and i9999=");
        	updatesql.append(I9999);
    	}else if("9".equals(kind))
    	{
    		RecordVo vo = new RecordVo("H00");
    		if ( vo== null) 
    			return;
    		updatesql.append("update H00 ");
    		updatesql.append(" set Title='");
        	updatesql.append(filetitle);
        	updatesql.append("' where H0100='");
        	updatesql.append(A0100);
        	updatesql.append("' and i9999=");
        	updatesql.append(I9999);
    	}else
    	{
    		RecordVo vo = new RecordVo("b00");
    		if ( vo== null) 
    			return;
    		updatesql.append("update b00 ");
    		updatesql.append(" set Title='");
        	updatesql.append(filetitle);
        	updatesql.append("' where b0110='");
        	updatesql.append(A0100);
        	updatesql.append("' and i9999=");
        	updatesql.append(I9999);
    	}

    	try{
           dao.update(updatesql.toString());
        }catch(Exception ee)
        {
        	ee.printStackTrace();
			throw GeneralExceptionHandler.Handle(ee);	
        }
	}

}