package com.hjsj.hrms.transaction.kq.options.manager.kqcard;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.options.kqcrad.KqCardLength;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * 换卡
 * 
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 31, 2008</p> 
 *@author sxin
 *@version 5.0
 */

public class ChangeCardTrans extends IBusiness{
	
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
    	
    	String sql="select card_no from kq_cards where status='-1'";
    	sql += " and " + Sql_switcher.length("card_no") + "=" + id_len;
		sql += " and " + Sql_switcher.isnull("card_no", "'####'") + "<>'####'";
    	ContentDAO dao = new ContentDAO(this.getFrameconn());
    	ArrayList card_list=new ArrayList();
    	try
    	{
    		this.frowset=dao.search(sql);
    		CommonData vo=null;
    		vo = new CommonData();
			vo.setDataName("");
			vo.setDataValue("");
			card_list.add(vo);
    		while(this.frowset.next())
        	{
    			vo = new CommonData();
    			vo.setDataName(this.frowset.getString("card_no"));
    			vo.setDataValue(this.frowset.getString("card_no"));
    			card_list.add(vo);
        	}
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
        this.getFormHM().put("card_list",card_list);
        this.getFormHM().put("old_card","");
        this.getFormHM().put("kq_cardno",kq_cardno);
        this.getFormHM().put("flag", "xxx");
    }

}
