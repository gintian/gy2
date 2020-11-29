package com.hjsj.hrms.transaction.kq.options.manager;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 校验卡号是否使用过
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jun 17, 2008</p> 
 *@author sunxin
 *@version 4.0
 */
public class CheckCardnoUseTrans extends IBusiness{
	
    public void execute() throws GeneralException
    {
    	String kq_cardno=(String)this.getFormHM().get("kq_cardno");
    	if(kq_cardno==null||kq_cardno.length()<=0)
    	{
    		return;
    	}
    	String card_no=(String)this.getFormHM().get("card_no");
    	if(card_no==null||card_no.length()<=0)
    	{
    		return;
    	}
    	ArrayList db_list=getAllDbName();
    	if(db_list==null||db_list.size()<=0)
    		return;
    	String dbname="";
    	StringBuffer sql=null;
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	StringBuffer message=new StringBuffer();
    	try
    	{
    		for(int i=0;i<db_list.size();i++)
        	{
        		dbname=db_list.get(i).toString();
        		sql=new StringBuffer();
        		sql.append("select a0100,a0101,b0110,e0122 from ");
        		sql.append(" "+dbname+"A01");
        		sql.append(" where "+kq_cardno+"='"+card_no+"'");
        		
        		this.frowset=dao.search(sql.toString());
        		if(this.frowset.next())
        		{
        			String a0101=this.frowset.getString("a0101");
            		message.append("该卡号已被");        			
        			message.append("  "+a0101+"使用");        	    	
        	    	break;
        		}
        	}
    	}catch(Exception e)
    	{
    		throw GeneralExceptionHandler.Handle(e);
    	}
    	if(message!=null&&message.length()>0)
    	{
    		this.getFormHM().put("flag", "1");
    		this.getFormHM().put("message", message.toString());
    	}else
    	{
    		this.getFormHM().put("flag", "0");
    	}
    }
    public ArrayList getAllDbName()
    {
 	   String sql="select pre from dbname";
 	   ArrayList list=new ArrayList();
 	   try
 	   {
 		   ContentDAO dao=new ContentDAO(this.getFrameconn());
 		   this.frowset=dao.search(sql);
 		   while(this.frowset.next())
 		   {
 			   list.add(this.frowset.getString("pre"));
 		   }
 	   }catch(Exception e)
 	   {
 		e.printStackTrace();   
 	   }
 	   return list;
    }
}
