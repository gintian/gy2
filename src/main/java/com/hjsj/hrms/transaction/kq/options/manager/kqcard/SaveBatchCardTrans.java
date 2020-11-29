package com.hjsj.hrms.transaction.kq.options.manager.kqcard;

import com.hjsj.hrms.businessobject.kq.options.kqcrad.KqCardLength;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SaveBatchCardTrans extends IBusiness{
	
    public void execute() throws GeneralException
    {
    	String kq_cardno=(String)this.getFormHM().get("kq_cardno");
    	String order_flag=(String)this.getFormHM().get("order_flag");
    	
    	String[] r_code=(String[])this.getFormHM().get("r_code");    	
    	if(r_code==null||r_code.length<=0)
    	{
    	  return ;	
    	}
    	String id_len_str=(String)this.getFormHM().get("id_len");
		KqCardLength kqCardLength=new KqCardLength(this.getFrameconn());
		int id_len=0;
		if(id_len_str!=null&&id_len_str.length()<=0)
		{
			id_len=Integer.parseInt(id_len_str);
		}else
		{   			
	    	id_len=kqCardLength.tack_CardLen();
		}
		
    	ArrayList c_card_list= getCardsList(order_flag,r_code.length-1,id_len,kq_cardno);    	
    	ArrayList cards_list=new ArrayList();
    	ArrayList up_list=new ArrayList(); 
    	for(int i=0;i<r_code.length;i++)
    	{
    		ArrayList one_card=new ArrayList();
    		String o_code=r_code[i];    		   		
    		if(o_code==null||o_code.length()<=0)
    		{
    			continue;
    		}    		
    		String o_card=c_card_list.get(i-1).toString(); 
    		if(o_card==null||o_card.length()<=0)
    		{
    			continue;
    		}
    		String[] o_codes=o_code.split("`");    		
    		StringBuffer sql_emp=new StringBuffer();
    		sql_emp.append("update "+o_codes[0]+"A01 set");
    		sql_emp.append(" "+kq_cardno+"='"+o_card+"'");
    		sql_emp.append(" where a0100='"+o_codes[1]+"'");    		
    		up_list.add(sql_emp.toString());    		
    		one_card.add("1");   
    		one_card.add(o_card);
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
    		flag="xxx";
    	}
    	this.getFormHM().put("flag", flag);
    }
    /**
     * 得到卡号
     * @param order_flag
     * @param i
     * @return
     * @throws GeneralException
     */
    public ArrayList getCardsList(String order_flag,int i,int id_len,String kq_cardno)throws GeneralException
    {
    	ArrayList list=new ArrayList();    	
    	KqCardLength kqCardLength=new KqCardLength(this.getFrameconn());
    	if(order_flag!=null&& "1".equals(order_flag))
    	{
    		list=kqCardLength.getCardsListFromKqCards(i,id_len,"-1");
    		if(list==null||list.size()<i)
    		{
    			ArrayList re_list=new ArrayList();
    			int r=i-list.size();
    			//BUG 郑文龙 批量发卡 考号重复
    			re_list=kqCardLength.createCardsFromIdFac(r,id_len,kq_cardno);
    			for(int s=0;s<re_list.size();s++)
    			{
    				list.add(re_list.get(s));
    			}
    		}
    	}else
    	{
    		//BUG 郑文龙 批量发卡 考号重复
    		list=kqCardLength.createCardsFromIdFac(i,id_len,kq_cardno);
    	}
    	return list;
    }
}
