package com.hjsj.hrms.transaction.kq.kqself.net_signin;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
/**
 * 批准补签
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Aug 2, 2007:4:56:39 PM</p> 
 *@author dengcan
 *@version 4.0
 */
public class ApproveNetSignInTrans extends IBusiness{
	
	public void execute()throws GeneralException
	{
		ArrayList selectedinfolist=(ArrayList)this.getFormHM().get("selectedinfolist");
		if(selectedinfolist==null||selectedinfolist.size()<=0)
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("","请选择记录！","",""));
		}
		ArrayList list=new ArrayList();
		 for(int i=0;i<selectedinfolist.size();i++)
	     {
			 ArrayList one_list = new ArrayList();
			 LazyDynaBean rec=(LazyDynaBean)selectedinfolist.get(i); 
			 String a0100=rec.get("a0100").toString();
			 String nbase=rec.get("nbase").toString();
			 String card_no=rec.get("card_no").toString();
			 String work_date=rec.get("work_date").toString();
			 String work_time=rec.get("work_time").toString();
			 one_list.add(a0100);
			 one_list.add(nbase);
			 one_list.add(card_no);
			 one_list.add(work_date);
			 one_list.add(work_time);
			 list.add(one_list);
		}
		StringBuffer sql=new StringBuffer();
		sql.append("update kq_originality_data set");
		sql.append(" sp_flag='03' ");
		sql.append(" where a0100=? and nbase=? and card_no=?");
		sql.append(" and work_date=? and work_time=? and sp_flag='02'");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			dao.batchUpdate(sql.toString(),list);			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
