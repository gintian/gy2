package com.hjsj.hrms.transaction.kq.options.manager;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveEditCardTrans extends IBusiness{
	
    public void execute() throws GeneralException
    {
    	String a0100=(String)this.getFormHM().get("a0100");
    	String nbase=(String)this.getFormHM().get("nbase");
    	String kq_cardno=(String)this.getFormHM().get("kq_cardno");
    	String old_cardno=(String)this.getFormHM().get("old_cardno");
    	String flag="xxx";
    	if(kq_cardno==null||kq_cardno.length()<=0)
    	{
    		ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.getFrameconn());
 		    String org_id=managePrivCode.getPrivOrgId();  
    		
    		KqParameter kq_paramter = new KqParameter(this.getFormHM(),this.userView,org_id,this.getFrameconn());
    		kq_cardno=kq_paramter.getCardno();
    	}
    	if(kq_cardno==null||kq_cardno.length()<=0)
    		return;
    	String cardno=(String)this.getFormHM().get("cardno");
    	if(cardno==null||cardno.length()<=0)
    	{
    		cardno="";
    	}
    	StringBuffer sql_emp=new StringBuffer();
		sql_emp.append("update "+nbase+"A01 set");
		sql_emp.append(" "+kq_cardno+"='"+cardno+"'");
		sql_emp.append(" where a0100='"+a0100+"'");
		String up="update kq_cards set status='1' where card_no='"+cardno+"'";
		String up_old="update kq_cards set status='-1' where card_no='"+old_cardno+"'";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
        try
        {
        	dao.update(sql_emp.toString());
        	dao.update(up);
        	dao.update(up_old);
        	flag="ok";
        }catch(Exception e)
        {
        	throw GeneralExceptionHandler.Handle(e);
        }
        this.getFormHM().put("flag", flag);
    }

}
