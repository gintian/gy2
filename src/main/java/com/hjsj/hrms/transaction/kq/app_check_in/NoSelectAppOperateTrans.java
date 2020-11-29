package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class NoSelectAppOperateTrans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList idlist=(ArrayList)this.getFormHM().get("idlist");		
		String table=(String)this.getFormHM().get("table");
		if(idlist==null||idlist.size()<=0)
		{
			return ;
		}
		if("q11".equalsIgnoreCase(table)|| "q13".equalsIgnoreCase(table)|| "q15".equalsIgnoreCase(table))
			appOperate(table,idlist);
		else if("q05".equalsIgnoreCase(table))
		{
			q05Operate(table,idlist);
		}
	}
   private void appOperate(String table,ArrayList idlist)
   {
	   String up="update "+table+" set state='0' where "+table+"01=?";
		ArrayList list=new ArrayList();
		for(int i=0;i<idlist.size();i++)
		{
			ArrayList olist=new ArrayList();
			String id=idlist.get(i).toString();
			olist.add(id);
			list.add(olist);
		}
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			dao.batchUpdate(up,list);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
   }
   private void q05Operate(String table,ArrayList idlist)
   {
	   String up="update "+table+" set state='0' where q03z0=? and nbase=? and a0100=?";
	   ArrayList list=new ArrayList();
		for(int i=0;i<idlist.size();i++)
		{
			ArrayList olist=new ArrayList();
			String id=idlist.get(i).toString();
			String[] ids=id.split("`");			
			olist.add(ids[0]);
			olist.add(ids[1]);
			olist.add(ids[2]);
			list.add(olist);
		}
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			dao.batchUpdate(up,list);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
   }
}
