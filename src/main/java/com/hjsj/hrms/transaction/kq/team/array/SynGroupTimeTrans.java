package com.hjsj.hrms.transaction.kq.team.array;

import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SynGroupTimeTrans extends IBusiness{
    public void execute()throws GeneralException{

    	String session_data =(String)this.getFormHM().get("session_data");
        HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
    	String syn_uro = (String)hm.get("syn_uro");
    	this.getFormHM().put("session_data",session_data);
        ArrayList datelist=RegisterDate.getKqDayList(this.getFrameconn()); 
 	    String start_date=(String)datelist.get(0);  
	    String end_date=(String)datelist.get(datelist.size()-1);	   	   
	    this.getFormHM().put("syn_uro",syn_uro);
	    this.getFormHM().put("syc_type","1");	    
	    this.getFormHM().put("start_date",start_date);
	    this.getFormHM().put("end_date",end_date);	    
	}    
  

}
