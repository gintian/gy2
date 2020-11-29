package com.hjsj.hrms.transaction.kq.options;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Calendar;

public class InitAddDurationTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		   ContentDAO dao=new ContentDAO(this.getFrameconn());
	       StringBuffer sb =new StringBuffer();
	       StringBuffer sbc =new StringBuffer();
	       try{
	       sb.append("select max(kq_year) as mm from kq_duration");
  	        this.frowset = dao.search(sb.toString());  
  	        this.frowset.first(); 
  	        String tt=this.frowset.getString("mm");
  	        if(!(tt==null||tt.length()<0))
  	        {
  	        	sbc.append("select count(kq_duration) as cc from kq_duration where kq_year='");
  	        	sbc.append(tt);
  	        	sbc.append("'");
  	        	 this.frowset = dao.search(sbc.toString());  
  	  	        this.frowset.first(); 
  	  	        String cc=this.frowset.getString("cc");
  	             Integer year = Integer.valueOf(tt); 
	    	     int nyear =year.intValue()+1;
	    	     this.getFormHM().put("kyear",String.valueOf(nyear));
	    	     this.getFormHM().put("text","0");
	    	     this.getFormHM().put("count",cc);
	    	}
  	        else
  	        {
	    	   Calendar t=Calendar.getInstance();
	    		int y=t.get(t.YEAR);
	    		this.getFormHM().put("kyear",String.valueOf(y));
	    		this.getFormHM().put("text","1");
	    	     this.getFormHM().put("count","12");

	    	}
	       }catch(Exception exx)
	       {
	  	       exx.printStackTrace();
	  	       throw GeneralExceptionHandler.Handle(exx);
	  	    }
          this.getFormHM().put("one_len","1");
          this.getFormHM().put("dat","1");
          this.getFormHM().put("month","1");
          this.getFormHM().put("radio","1");
	}

}
