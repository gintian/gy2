package com.hjsj.hrms.transaction.kq.options.manager;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class CreateCardTrans extends IBusiness{
	public void execute() throws GeneralException
    {
		String card_no=(String)this.getFormHM().get("card_no");
    	if(card_no==null||card_no.length()<=0)
    	{
    		return;
    	}
    	String flag="false";
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	try
    	{
    		RecordVo vo_card=new RecordVo("kq_cards");
    		vo_card.setString("card_no",card_no);
    		vo_card.setString("status","-1");
    		dao.addValueObject(vo_card);
    		flag="true";
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	this.getFormHM().put("flag",flag);
    }
}
