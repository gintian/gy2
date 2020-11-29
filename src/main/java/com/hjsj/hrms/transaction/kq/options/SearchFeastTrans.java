/*
 * Created on 2006-12-26
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.kq.options;

import com.hjsj.hrms.valueobject.common.MonthDayView;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Administrator
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SearchFeastTrans extends IBusiness {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 * 
	 */
	
	private void getList(String name) throws GeneralException
	{
		ArrayList flist=new ArrayList();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer ssql=new StringBuffer();
		
		try{
				    flist.clear();
				    ssql.delete(0,ssql.length());
    	            ssql.append("select * from kq_feast where feast_id=");
    	            ssql.append(name);
    	            ssql.append(" order by feast_id");
	        	   	this.frowset=dao.search(ssql.toString());
	        	   	String weeksstr=null;
	        	   	while(this.frowset.next())
	        	   	{	        	   		
	        	   		if(!(this.frowset.getString("feast_dates")==null|| "".equals(this.frowset.getString("feast_dates"))))
	        	   		{
	        	   			  weeksstr=this.frowset.getString("feast_dates");
	        	   			  if(!("".equals(weeksstr)||weeksstr!=null))
	        	   			  {
	        	   			    String[] array=StringUtils.split(weeksstr,",");
	        	   			    for(int i=0;i<array.length; i++)
	        	   			    {
	        	   			     
	        	   			     if(array[i].length()==10)
	        	   			     {
		       	        	        String yy=array[i].substring(0,4);
		       	        	        String mm=array[i].substring(5,7);
		       	        	        String tt=array[i].substring(8,10);
		       	        	        MonthDayView monthdayview=new MonthDayView(mm,tt,yy);
		       	        	        flist.add(monthdayview);
	        	   			      }
	        	   			    if(array[i].length()==5)
	        	   			     {
		      
		       	        	        String mm=array[i].substring(0,2);
		       	        	        String tt=array[i].substring(3,5);
		       	        	        MonthDayView monthdayview=new MonthDayView(mm,tt,null);
		       	        	        flist.add(monthdayview);
	        	   			      }
	        	   			     
	        	   			    }
	        	   			  }
	        	     	}
	        	   
	        	    this.getFormHM().put("feastList",flist);	        	   	
			}
     
        }catch(Exception exx)
          {
      	      exx.printStackTrace();
      	      throw GeneralExceptionHandler.Handle(exx);
      	  }
	}
	private ArrayList getLst() throws GeneralException
	{
		ArrayList tlist=new ArrayList();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer ssql=new StringBuffer();
		try{
			 
    	         ssql.append("select * from kq_feast order by feast_id");
	        	 this.frowset=dao.search(ssql.toString());
	        	 while(this.frowset.next())
	        	 {	        	   		
	        	   			
	        	   	tlist.add(this.frowset.getString("feast_id"));
	        	     	
	        	  }
	        	  this.getFormHM().put("tlist",tlist);	        	   	
			
     
        }catch(Exception exx)
          {
      	      exx.printStackTrace();
      	      throw GeneralExceptionHandler.Handle(exx);
      	  }
        return tlist;
	}
	
	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");        
        String fid=(String)hm.get("feast_ida");
        
		String feast_id = (String) this.getFormHM().get("feast_name");
		if(!(fid==null|| "".equals(fid)))
		{
			this.getList(fid);
		}else
		{
			if(this.getLst().size()!=0)
			{
				this.getList(this.getLst().get(0).toString());
			}else{
				this.getFormHM().put("feastList",new ArrayList());
			}
		}
		ArrayList tlist=new ArrayList();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer ssql=new StringBuffer();
		try{
			 
    	            ssql.append("select * from kq_feast order by feast_id");
	        	   	this.frowset=dao.search(ssql.toString());
	        	   	while(this.frowset.next())
	        	   	{	        	   		
	        	   			CommonData dataobj = new CommonData(this.frowset.getString("feast_id"),this.frowset.getString("feast_name"));
	        	   			tlist.add(dataobj);
	        	     	
	        	   	}
	        	    this.getFormHM().put("tlist",tlist);	        	   	
			
	        	    this.getFormHM().put("feast_id",fid);
        }catch(Exception exx)
          {
      	      exx.printStackTrace();
      	      throw GeneralExceptionHandler.Handle(exx);
      	  }


	}

}
