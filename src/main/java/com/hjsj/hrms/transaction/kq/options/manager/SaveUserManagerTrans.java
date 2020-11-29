package com.hjsj.hrms.transaction.kq.options.manager;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SaveUserManagerTrans extends IBusiness {

	public void execute() throws GeneralException {		 
		 try
		 {
			/**控制显示前台的页签*/ 			
			ArrayList fieldlist=(ArrayList)this.getFormHM().get("fieldlist");
			ArrayList userlist=(ArrayList)this.getFormHM().get("list");
			String kq_type=(String)this.getFormHM().get("kq_type");
			if(fieldlist==null||userlist==null||kq_type==null)
				return;
			saveManage(userlist,kq_type);
		 }
		 catch(Exception ex)
		 {
			 ex.printStackTrace();
			 throw GeneralExceptionHandler.Handle(ex);
		 }
	}
	private void saveManage(ArrayList userlist,String kq_type)throws Exception
	{
        DynaBean dbean=null;
        String name=null;    
        ArrayList list=new ArrayList();
        try
        {
	        ContentDAO dao=new ContentDAO(this.getFrameconn());
			String nbase=null;
			for(int i=0;i<userlist.size();i++)
		    {
				dbean=(LazyDynaBean)userlist.get(i);
				name=(String)dbean.get("a0100");			
				nbase=(String)dbean.get("nbase");
				String t3=(String)dbean.get("t3");
				StringBuffer updateA01 = new StringBuffer();
				updateA01.append("update "+nbase+"A01 set");
				updateA01.append(" "+kq_type+"='"+t3+"'");
				updateA01.append(" where a0100='"+name+"'");
				Calendar now = Calendar.getInstance();
				Date cur_d=now.getTime();
				String q03z0Str=DateUtils.format(cur_d,"yyyy.MM.dd");
				StringBuffer updateQ03=new StringBuffer();
				updateQ03.append("update q03 set");
				updateQ03.append(" q03z3='"+t3+"'");
				updateQ03.append(" where a0100='"+name+"'");
				updateQ03.append(" and nbase='"+nbase+"'");
				updateQ03.append(" and q03z0>='"+q03z0Str+"'");
				//list.add(updateA01.toString());
				//list.add(updateQ03.toString());				
				dao.update(updateA01.toString());
				dao.update(updateQ03.toString());
			}
			//dao.batchUpdate(list);
        }
        catch(Exception ee)
        {
        	ee.printStackTrace();
        	throw GeneralExceptionHandler.Handle(ee);
        }
	}
}
