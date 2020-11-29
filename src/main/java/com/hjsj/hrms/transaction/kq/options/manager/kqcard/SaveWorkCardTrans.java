package com.hjsj.hrms.transaction.kq.options.manager.kqcard;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 保存手工发卡
 * <p>Title:SaveWorkCardTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jan 6, 2007 1:59:31 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class SaveWorkCardTrans  extends IBusiness{
	
    public void execute() throws GeneralException
    {
    	String[] r_code=(String[])this.getFormHM().get("r_code");
    	String[] r_card=(String[])this.getFormHM().get("r_card");
    	String kq_cardno=(String)this.getFormHM().get("kq_cardno");
    	if(r_code==null||r_code.length<=0)
    	{
    	  return ;	
    	}
    	if(r_card==null||r_card.length<=0)
    	{
    	  return;	
    	}
    	ArrayList up_list=new ArrayList(); 
    	ArrayList cards_list=new ArrayList();
    	for(int i=0;i<r_code.length;i++)
    	{
    		ArrayList one_card=new ArrayList();
    		String o_code=r_code[i];
    		String o_card=r_card[i];    		
    		if(o_code==null||o_code.length()<=0)
    		{
    			continue;
    		}
    		if(o_card==null||o_card.length()<=0)
    		{
    			continue;
    		}
    		String[] o_codes=o_code.split("`");    		
    		StringBuffer sql_emp=new StringBuffer();
    		sql_emp.append("update "+o_codes[0]+"A01 set");
    		sql_emp.append(" "+kq_cardno+"='"+r_card[i]+"'");
    		sql_emp.append(" where a0100='"+o_codes[1]+"'");    		
    		up_list.add(sql_emp.toString());    		
    		one_card.add("1");    	
    		one_card.add(r_card[i]);
    		cards_list.add(one_card);
    	}
    	String flag="ok";
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	try
    	{
    		for(int i=0;i<up_list.size();i++)
    		{
    			dao.update(up_list.get(i).toString());
    		}
    		String up="update kq_cards set status=? where card_no=?";
    		dao.batchUpdate(up,cards_list);
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		flag="xx";
    	}
    	this.getFormHM().put("flag", flag);
    }

}
