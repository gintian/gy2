package com.hjsj.hrms.transaction.kq.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Date;

public class SaveDurationTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		    RecordVo rv = (RecordVo)this.getFormHM().get("duration");
	        if(rv==null)
	            return; 
	        String flag=(String)this.getFormHM().get("flag");
	        String start=(String)this.getFormHM().get("start");
	        String end=(String)this.getFormHM().get("end");
	        String yue=(String)this.getFormHM().get("yue");
	        ContentDAO dao=new ContentDAO(this.getFrameconn());  
	        try
	        {        
	        	if(start!=null||start.length()>0)
	        	{
	        		start=start.replaceAll("\\.","-");
	        	}
	        	if(end!=null||end.length()>0)
	        	{
	        		end=end.replaceAll("\\.","-");
	        	}
		        if("1".equals(flag))
		        {
		        	Date end_date=DateUtils.getDate(end,"yyyy-MM-dd");
		        	Date start_date=DateUtils.getDate(start,"yyyy-MM-dd");
		        	rv.setDate("kq_end",end_date);
		        	rv.setDate("kq_start",start_date);
		        	rv.setString("gz_duration",yue);
		           dao.updateValueObject(rv);
		        }
		        
	        }catch(Exception exx)
	        {
	   	       exx.printStackTrace();
	   	       throw GeneralExceptionHandler.Handle(exx);
	   	     }

	}

}
