/*
 * Created on 2006-12-27
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.kq.options;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AddFeastTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String feast_id =(String)this.getFormHM().get("feast_id");
		if(feast_id==null|| "".equals(feast_id))
			return;
		String yes=(String)this.getFormHM().get("fyear");
		String feast_dates="";
		if(yes==null|| "".equals(yes))
		{
			feast_dates=(String)this.getFormHM().get("fmonth")+"-"+(String)this.getFormHM().get("fday");
		}else{
		   feast_dates =(String)this.getFormHM().get("fyear")+"-"+(String)this.getFormHM().get("fmonth")+"-"+(String)this.getFormHM().get("fday");
		}
		if(feast_dates.length()==0|| "".equals(feast_dates))
			return;
	    ArrayList nameList = new ArrayList();
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    StringBuffer ssql=new StringBuffer();
	    try{
		         StringBuffer sb=new StringBuffer();
		         sb.append("select * from kq_feast where feast_id='");
		         sb.append(feast_id);
		         sb.append("'");
		         this.frowset=dao.search(sb.toString());
		     
		         while(this.frowset.next())
		         {
		             String tt = this.frowset.getString("feast_dates");
		           if(!(tt==null|| "".equals(tt)))
		           {
		        	   String[] array =null;
		        	   array=StringUtils.split(tt,","); 
		   		        for(int j=0;j<array.length; j++)
		   			    {
		   		    	    if(feast_dates.equals(array[j]))
		   		    	     {
		   		    	        throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("error.kq.sdate"),"",""));
	                         }
		   		    	    
		   			    }
		   		        
		               String gg =tt+feast_dates+",";
            	       ssql.append("update kq_feast set feast_dates='");
            	       ssql.append(gg);
            	       ssql.append("' where feast_id =");
            	       ssql.append(feast_id);
            	         
        	           dao.update(ssql.toString(),nameList);
        	   	         
		           }
		           else
		           {
		        	  ssql.append("update kq_feast set feast_dates='");
       	              ssql.append(feast_dates);
       	              ssql.append(",' where feast_id =");
       	              ssql.append(feast_id);
       	              dao.update(ssql.toString(),nameList);
		          }
		       }

    	  }catch(Exception exx)
	     {
  	       exx.printStackTrace();
  	       throw GeneralExceptionHandler.Handle(exx);
  	     }
	}

}
