package com.hjsj.hrms.transaction.kq.options.manager.kqcard;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.options.kqcrad.KqCardLength;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SearchSendCardTrans extends IBusiness{
	
    public void execute() throws GeneralException
    {
    	String kq_cardno=(String)this.getFormHM().get("kq_cardno");
    	if(kq_cardno==null||kq_cardno.length()<=0)
    	{
    		ManagePrivCode managePrivCode=new ManagePrivCode(this.userView,this.getFrameconn());
    		String org_id=managePrivCode.getPrivOrgId();   
    		KqParameter kq_paramter = new KqParameter(this.getFormHM(),this.userView,org_id,this.getFrameconn());
    		kq_cardno=kq_paramter.getCardno();
    	}
    	int id_len=0;
    	KqCardLength kqCardLength=new KqCardLength(this.getFrameconn());
    	id_len=kqCardLength.tack_CardLen();
    	ArrayList card_list=kqCardLength.getCardList("-1",id_len);    	
        this.getFormHM().put("card_list",card_list);
        this.getFormHM().put("card_no","");
        this.getFormHM().put("kq_cardno",kq_cardno);
        this.getFormHM().put("id_len",id_len+"");
        this.getFormHM().put("flag", "xxx");
    }


}
