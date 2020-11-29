package com.hjsj.hrms.transaction.kq.register.empchange;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class EmpChangeUpdateDateTrans extends IBusiness{
	public void execute()throws GeneralException
	   {
		
   	    String userbase = (String) this.getFormHM().get("userbase");
   	    String a0100=(String)this.getFormHM().get("a0100");
   	    String change_date=(String)this.getFormHM().get("curdate");
   	    String status=(String)this.getFormHM().get("changestatus");   	   
   	    if(userbase!=null&&userbase.length()>0)
   	    {
   	    	if(a0100!=null&&a0100.length()>0)
   	    	{
   	    		updateDate(userbase,a0100,status,change_date);
   	    	}
   	    }
   	    this.getFormHM().put("changestatus",status);
	   }
    public void updateDate(String userbase,String a0100,String status,String change_date)
    {
    	StringBuffer sql=new StringBuffer();
    	String date=Sql_switcher.dateValue(change_date);  
    	sql.append("update kq_employ_change set change_date="+date+"");
    	sql.append("where nbase='"+userbase+"' and a0100='"+a0100+"' and status="+status+"");
    	    	
    	ContentDAO dao = new ContentDAO(this.getFrameconn());
    	try
    	{
    	  dao.update(sql.toString());
    	}catch(Exception e)
    	{
    	   e.printStackTrace();	
    	}
    }
}
