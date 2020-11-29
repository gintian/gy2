package com.hjsj.hrms.transaction.kq.options;

import com.hjsj.hrms.businessobject.kq.options.KqItem;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class DeleteItemTypeTrans extends IBusiness {

	public void execute() throws GeneralException
	{
		String mkey=(String)this.getFormHM().get("codeitemid");
		StringBuffer stsql=new StringBuffer();
		StringBuffer sbl=new StringBuffer(); 
		StringBuffer sb=new StringBuffer();
        ContentDAO dao=new ContentDAO(this.getFrameconn()); 
        String cc="";
        String tes="";
        try
        {     
           if(!(mkey==null))
           {
        	  sb.append("select codeitemid from codeitem where codeitemid='");
         	  sb.append(mkey.toString());
         	  sb.append("' and codesetid='27' and flag=1");
         	  this.frowset=dao.search(sb.toString());
         	  if(this.frowset.next())
         		tes=this.frowset.getString("codeitemid");
         	  
              KqItem kqItem = new KqItem(this.getFrameconn());
              kqItem.resetChildIdForParent(mkey);
              
         	  if(tes==null|| "".equals(tes))
         	  {	 
         		 sbl.delete(0,sbl.length());
         		 sbl.append("select codeitemid from codeitem where codeitemid like '");
        	     sbl.append(mkey.toString());
        	     sbl.append("%' and codesetid='27' and flag=0");
        	     this.frowset=dao.search(sbl.toString());
        	     while(this.frowset.next())
        	     {
        	         cc=this.frowset.getString("codeitemid");
        	         stsql.delete(0,stsql.length());
        	         stsql.append("delete from codeitem where codeitemid='");
    		         stsql.append(cc);
    		         stsql.append("' and codesetid='27' and flag=0");
	                 dao.delete(stsql.toString(),new ArrayList());
	                 
	                 sbl.delete(0,sbl.length());
	                 sbl.append("delete from kq_item where item_id='");
	                 sbl.append(cc);
	                 sbl.append("'");
	                 dao.delete(sbl.toString(),new ArrayList());
        	     }
	             
         	  }else
         	  {
	             throw new GeneralException(ResourceFactory.getProperty("error.kq.nde"));
         	  }
           }
	        
        }
        catch(Exception exx)
        {
   	       exx.printStackTrace();
   	       throw GeneralExceptionHandler.Handle(exx);
        }

	}

}
