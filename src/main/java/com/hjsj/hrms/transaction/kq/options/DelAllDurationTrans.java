package com.hjsj.hrms.transaction.kq.options;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class DelAllDurationTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
	   HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 		
	   String yer=(String)hm.get("kq_year");
	   
	   StringBuffer sb=new StringBuffer();
	   ContentDAO dao=new ContentDAO(this.getFrameconn());
		try{
			 sb.append("select finished from kq_duration");
			 sb.append(" where kq_year='"+yer+"' and finished='1'");
			 this.frowset=dao.search(sb.toString());
			 if(!this.frowset.next())
			 {
				 sb=new StringBuffer();
				 sb.append("delete from kq_duration where kq_year='");
				 sb.append(yer);
				 sb.append("' and finished='0'");				
				 dao.delete(sb.toString(),new ArrayList()); 
			 }else
			 {
				 throw new GeneralException(ResourceFactory.getProperty("error.kq.delall"));
			 }
			 

		}catch(Exception sqle)
	    {
		    sqle.printStackTrace();
		     throw GeneralExceptionHandler.Handle(sqle);
		 }
            
		this.getFormHM().put("kq_year",String.valueOf(Integer.parseInt(yer)-1));
        
	}

}
