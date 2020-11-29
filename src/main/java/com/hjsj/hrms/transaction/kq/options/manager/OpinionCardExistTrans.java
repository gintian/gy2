package com.hjsj.hrms.transaction.kq.options.manager;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class OpinionCardExistTrans extends IBusiness{
	public void execute() throws GeneralException
    {
		String card_no=(String)this.getFormHM().get("card_no");
    	if(card_no==null||card_no.length()<=0)
    	{
    		this.getFormHM().put("exist_falg","true");
    		return;
    	}
    	String exist_falg="false";
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	StringBuffer sql=new StringBuffer();
     	sql.append("select card_no from kq_cards");
     	sql.append(" where card_no='"+card_no+"'");
     	try
     	{
     		this.frowset=dao.search(sql.toString());
     		if(this.frowset.next())
     		{
     			exist_falg="true";
     		}
     	}catch(Exception e)
     	{
     		e.printStackTrace();
     	}   
    	
    	this.getFormHM().put("exist_falg",exist_falg);
	}

}
