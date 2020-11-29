/*
 * Created on 2006-12-21
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.kq.options;

import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hrms.frame.dao.ContentDAO;
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
public class SaveKqRestTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		String[] rest_weeks =(String[])this.getFormHM().get("rest_weeks");
			
		ArrayList nameList = new ArrayList();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer ssql=new StringBuffer();
		try{
			        ManagePrivCode managePrivCode=new ManagePrivCode(this.userView,this.getFrameconn());
	    	        String b0110=managePrivCode.getUNB0110();  
	            	ssql.append("delete  from kq_restofweek  where b0110 = ?");
	            	nameList.add(b0110);
	        	   	dao.delete(ssql.toString(),nameList);
	        	   	nameList.clear();
	        	   	if(rest_weeks!=null){
	        	    	String mm="";
	    			 for(int i=0;i<rest_weeks.length;i++){
	    				mm+=(rest_weeks[i]+",");
	    			   }
	    			 ssql.delete(0,ssql.length());
	            	  ssql.append("insert into kq_restofweek (b0110,rest_weeks)values(?,?)");
	            	  nameList.add(b0110);
	            	  nameList.add(mm);
	        	      dao.insert(ssql.toString(),nameList);
	        	   	}
			
        	}catch(Exception exx)
		    {
      	       exx.printStackTrace();
      	       throw GeneralExceptionHandler.Handle(exx);
      	    }
		
	}

}
