package com.hjsj.hrms.transaction.kq.options.manager;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class OpinionCardUseTrans extends IBusiness{
	
    public void execute() throws GeneralException
    {
    	String card_no=(String)this.getFormHM().get("card_no");
    	String a0100=(String)this.getFormHM().get("a0100");
    	String nbase=(String)this.getFormHM().get("nbase");
    	String kq_cardno=(String)this.getFormHM().get("kq_cardno");
    	if(card_no==null||card_no.length()<=0)
    	{
    		return;
    	}
    	if(kq_cardno==null||kq_cardno.length()<=0)
    		throw GeneralExceptionHandler.Handle(new GeneralException("","考勤卡号没有定义，请先定义！","",""));
    	if(a0100==null||a0100.length()<=0||nbase==null||nbase.length()<=0)
    		throw GeneralExceptionHandler.Handle(new GeneralException("","传递参数有错误，请刷新页面，重试！","",""));
    	String old_cardno=(String)this.getFormHM().get("old_cardno");
    	String flag="true";
    	old_cardno=old_cardno!=null?old_cardno:"";
    	if(old_cardno.equals(card_no))
    	{
    		this.getFormHM().put("flag",flag);
    		return;
    	}
    		
    	StringBuffer sql=new StringBuffer();
    	sql.append("select count(card_no) cardNum");
    	sql.append(" from kq_cards");
	    sql.append(" where card_no='"+card_no+"'");
	    sql.append(" and status='0'");
    	//作废卡号数量
		int cardNum = 0;
    	ContentDAO dao=new ContentDAO(this.getFrameconn());    	
    	try
    	{	
    		//  linbz  20160426   判断该卡号是否作废
    		this.frowset = dao.search(sql.toString());
        	if(this.frowset.next()){
        		cardNum = this.frowset.getInt("cardNum");
        	}
        	if(cardNum > 0){
        		flag="cancellation";
        		return;
        	}
        	//判断该卡号在kq_cards中是否正在使用
        	sql = new StringBuffer("");
        	sql.append("select card_no from kq_cards");
    	    sql.append(" where card_no='"+card_no+"'");
    	    sql.append(" and status='1'");
    		this.frowset=dao.search(sql.toString());
    		if(this.frowset.next())
    		{
    		  	flag="false";
    		  	return;
    		}
    		//判断该卡号在人员主集中是否存在
			sql = new StringBuffer("");
			sql.append("select "+kq_cardno+" from ");
			sql.append(" "+nbase+"A01");
			sql.append(" where "+kq_cardno+"='"+card_no+"'");
			this.frowset=dao.search(sql.toString());
    		if(this.frowset.next())
    		{
    		  	flag="false";
    		  	return;
    		}
    		
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	} finally {
    		this.getFormHM().put("flag",flag);
    	}
    }
    
}
