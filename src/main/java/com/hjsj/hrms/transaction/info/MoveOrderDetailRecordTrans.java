package com.hjsj.hrms.transaction.info;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class MoveOrderDetailRecordTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String ordernum=(String)this.getFormHM().get("ordernum");
        ArrayList selfinfolist=(ArrayList)this.getFormHM().get("selectedlist");
		String userbase=(String)this.getFormHM().get("userbase");
		String a0100=(String)this.getFormHM().get("a0100");
		String setname=(String)this.getFormHM().get("setname");
   	    if(selfinfolist==null||selfinfolist.size()==0)
	        return;
   	    if(ordernum==null || ordernum.length()==0)
   	    	return;
   	    try{
   	    	ContentDAO dao=new ContentDAO(this.getFrameconn());
   	    	StringBuffer searchSql=new StringBuffer();
   	    	searchSql.append("select i9999 from ");
   	    	searchSql.append(userbase + setname);
   	    	searchSql.append(" where a0100='");
   	    	searchSql.append(a0100);
   	    	searchSql.append("'");
   	    	this.frowset=dao.search(searchSql.toString());
   	    	int i9999=0;
   	    	for(int i=0;i<Integer.parseInt(ordernum)&&this.frowset.next();i++)
   	    	{
   	    		if(i<Integer.parseInt(ordernum)-1)
   	    			continue;
   	    		if(i==Integer.parseInt(ordernum)-1)
   	    		{
   	    			i9999=this.frowset.getInt("i9999");
   	    			searchSql.delete(0,searchSql.length());
   	    			searchSql.append("update ");
   	    			searchSql.append(userbase + setname);
   	    			searchSql.append(" set i9999=i9999 + ");
   	    			searchSql.append(selfinfolist.size());
   	    			searchSql.append(" where a0100='");
   	    			searchSql.append(a0100);
   	    			searchSql.append("' and i9999>=");
   	    			searchSql.append(i9999);
   	    			dao.update(searchSql.toString());
   	    		}
   	    	}
            if(i9999!=0)
            {
            	for(int i=0;i<selfinfolist.size();i++)
       	    	{
       	    	
       	    	}
            }
   	    	
   	    	
   	    }catch(Exception e)
   	    {
   	    	e.printStackTrace();
   	    	throw GeneralExceptionHandler.Handle(e); 
   	    }
        System.out.println(ordernum);
	}

}
