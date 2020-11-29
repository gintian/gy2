package com.hjsj.hrms.transaction.kq.options;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class DeleteDurationTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList dlist = (ArrayList)this.getFormHM().get("selectedlist");
		if(dlist==null||dlist.size()==0)
            return;
		String ky_year =(String)this.getFormHM().get("kq_year");
		/*if(ky_year==null||ky_year.length()<=0)
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("error.kq.noyear"),"",""));
		}*/
		String yes="";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		StringBuffer sb= new StringBuffer();
		try
		{ 
			 for(int i=0;i<dlist.size();i++)
			 {
				    RecordVo vo=(RecordVo)dlist.get(i);
	            	String mm =vo.getString("finished");
	                String duration = vo.getString("kq_duration");
	                ky_year=vo.getString("kq_year");
	            	if("0".equals(mm)){
	            	  sb.delete(0,sb.length());	
	            	  sb.append("delete from kq_duration where kq_duration ='");
	            	  sb.append(duration.toString());
	            	  sb.append("' and kq_year='");
	            	  sb.append(ky_year);
	            	  sb.append("'");	            	 
	            	  dao.delete(sb.toString(),new ArrayList());
	            	  
	            	 

	            	}else
	            	{
	            		throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("error.kq.notdelete"),"",""));
	            	}
	        }
			 
		  sb.delete(0,sb.length());
       	  sb.append("select kq_year from kq_duration where kq_year='");
       	  sb.append(ky_year);
       	  sb.append("'");
       	  
       	  
       	  this.frowset=dao.search(sb.toString());
       	  if(this.frowset.next())
       	  {
       		  yes=this.getFrowset().getString("kq_year");
       	  }
		 }catch(Exception exx)
	     {
  	       exx.printStackTrace();
  	       throw GeneralExceptionHandler.Handle(exx);
  	     }finally{
  	    	 
  	    	 this.getFormHM().put("mess","2");
  	       if(yes==null|| "".equals(yes))
  	    	  this.getFormHM().put("kq_year","");
  	     }
	}

}
