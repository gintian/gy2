package com.hjsj.hrms.transaction.kq.options.manager;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.options.kqcrad.KqCardLength;
import com.hjsj.hrms.businessobject.kq.options.kqcrad.KqCrads;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class EditCardTrans extends IBusiness{
	
    public void execute() throws GeneralException
    {
    	String a0100=(String)this.getFormHM().get("a0100");
    	String nbase=(String)this.getFormHM().get("nbase");
    	String kq_cardno=(String)this.getFormHM().get("kq_cardno");
    	a0100 = PubFunc.decrypt(a0100);
    	nbase = PubFunc.decrypt(nbase);
    	if(kq_cardno==null||kq_cardno.length()<=0)
    	{
    		ManagePrivCode managePrivCode=new ManagePrivCode(this.userView,this.getFrameconn());
    		String userOrgId=managePrivCode.getPrivOrgId();  
    		String org_id=userOrgId;
    		KqParameter kq_paramter = new KqParameter(this.getFormHM(),this.userView,org_id,this.getFrameconn());
    		kq_cardno=kq_paramter.getCardno();
    	}
    	if(kq_cardno==null||kq_cardno.length()<=0)
    		return;
    	String cardno=(String)this.getFormHM().get("cardno");
    	cardno = PubFunc.decrypt(cardno);
    	KqCrads kqCrads=new KqCrads(this.getFrameconn());
    	if(cardno==null||cardno.length()<=0)
    	{
    		cardno=getCardno(nbase,a0100,kq_cardno);
    	}
    
    	ArrayList card_list=kqCrads.getCardList("-1"); 
    	String card_message=getCardMessage(nbase,a0100,kq_cardno,cardno);
    	this.getFormHM().put("card_message",card_message);
        this.getFormHM().put("card_list",card_list);
    	this.getFormHM().put("cardno",cardno);
    	this.getFormHM().put("old_cardno",cardno);
    	this.getFormHM().put("a0100",a0100);
    	this.getFormHM().put("nbase",nbase);
    	this.getFormHM().put("kq_cardno",kq_cardno);
    	this.getFormHM().put("new_cardno","");
    	KqCardLength kqCardLength=new KqCardLength(this.getFrameconn());
    	int int_id_len=kqCardLength.tack_CardLen();
    	this.getFormHM().put("id_len",int_id_len+"");
    	this.getFormHM().put("flag", "");
    }
    
    private String getCardMessage(String nbase,String a0100,String kq_cardno,String cardno)throws GeneralException
    {
    	KqCrads kqCrads=new KqCrads(this.getFrameconn());
    	StringBuffer sql=new StringBuffer();
    	StringBuffer card_message=new StringBuffer();
    	sql.append("select a0100,a0101,b0110,e0122,"+kq_cardno+" from ");
		sql.append(" "+nbase+"A01");
		sql.append(" where a0100 ='"+a0100+"' and ("+kq_cardno+"='"+cardno+"'"+"or "+kq_cardno+" is NULL )");

		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			this.frowset=dao.search(sql.toString());
    		if(this.frowset.next())
    		{
    		    String b0110=this.frowset.getString("b0110");
    		    if (b0110 != null && !"".equals(b0110.trim()))
    		        card_message.append("&nbsp;&nbsp;"+kqCrads.getCodeitemDesc(b0110)+"<br>");

    		    String e0122=this.frowset.getString("e0122");
    		    if (e0122 != null && !"".equals(e0122.trim()))
    		        card_message.append("&nbsp;&nbsp;"+kqCrads.getCodeitemDesc(e0122)+"<br>");
    		        
        		String a0101=this.frowset.getString("a0101");
        		if (a0101 != null && !"".equals(a0101.trim()))
        		    card_message.append("&nbsp;&nbsp;"+a0101+"<br>"); 
    		}
		}catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(e);
		}
		return card_message.toString();
    }
    
    private String getCardno(String nbase,String a0100,String kq_cardno)throws GeneralException
    {
    	
    	StringBuffer sql=new StringBuffer();
    	sql.append("select "+kq_cardno+" from ");
		sql.append(" "+nbase+"A01");
		sql.append(" where a0100 ='"+a0100+"'");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String cardno="";
		try
		{
			this.frowset=dao.search(sql.toString());
    		if(this.frowset.next())
    		{
    			cardno=this.frowset.getString(kq_cardno)!=null?this.frowset.getString(kq_cardno):"";
    		}
		}catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(e);
		}
		return cardno;
    }
}
