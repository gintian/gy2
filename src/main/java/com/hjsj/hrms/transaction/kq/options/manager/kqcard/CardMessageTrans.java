package com.hjsj.hrms.transaction.kq.options.manager.kqcard;

import com.hjsj.hrms.businessobject.kq.options.kqcrad.KqCrads;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CardMessageTrans extends IBusiness{
	
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
    	String s_flag="0";
    	String a0100="";
    	KqCrads kqCrads=new KqCrads(this.getFrameconn());
    	RowSet rs = null;
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
        			String e0122=this.frowset.getString("e0122");
        			String b0110=this.frowset.getString("b0110");
            		String a0101=this.frowset.getString("a0101");
            		a0100=this.frowset.getString("a0100");
            		sql=new StringBuffer();
            		String whereIN=RegisterInitInfoData.getWhereINSql(this.userView,dbname);
            		sql.append("select * from "+dbname+"A01");
            		sql.append(" where a0100='"+a0100+"'");
            		sql.append(" and a0100 in(select a0100 "+whereIN+") "); 
            		rs=dao.search(sql.toString());
            		if(rs.next())
            		{
            			message.append("&nbsp;&nbsp;"+kqCrads.getCodeitemDesc(b0110)+"<br>");
            			message.append("&nbsp;&nbsp;"+kqCrads.getCodeitemDesc(e0122)+"<br>");
            			message.append("&nbsp;&nbsp;"+a0101+"<br>");
            	    	s_flag="1";
            		}else
            		{
            			message.append("该卡号人员不在您的管理权限范围内，不能替换!");
            			s_flag="2";
            		}
        			
        	    	break;
        		}
        	}
    	}catch(Exception e)
    	{
    		throw GeneralExceptionHandler.Handle(e);
    	}finally{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
    	if(!"1".equals(s_flag)&&!"2".equals(s_flag))
    	{
    		message.append("没有找到持卡人！请正确输入卡号。");
    	}
    	this.getFormHM().put("a0100",a0100);
    	this.getFormHM().put("nbase",dbname);
    	this.getFormHM().put("s_flag",s_flag);
    	this.getFormHM().put("message",message.toString());    	
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
