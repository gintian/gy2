package com.hjsj.hrms.transaction.general.template.goabroad.collect;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class DeleteStatCodeTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		ArrayList selectedinfolist=(ArrayList)this.getFormHM().get("selectedinfolist");
		if(selectedinfolist==null||selectedinfolist.size()==0)
			  return;
		String subset=(String)this.getFormHM().get("subset");
		String nbase=(String)this.getFormHM().get("nbase");
		ArrayList list=new ArrayList();
		 for(int i=0;i<selectedinfolist.size();i++)    	
		 {
			  ArrayList cur_list=new ArrayList();			  
    	      LazyDynaBean rec=(LazyDynaBean)selectedinfolist.get(i); 
    	      cur_list.add(rec.get("a0100").toString());
              cur_list.add(rec.get("i9999").toString());
              list.add(cur_list);
    	 }
		 StringBuffer sql=new StringBuffer();
		 sql.append("delete from "+nbase+subset);
		 sql.append(" where a0100=? and i9999=?");
		 ContentDAO dao =new ContentDAO(this.getFrameconn());
		 try
		 {
			dao.batchUpdate(sql.toString(),list); 
		 }catch(Exception e)
		 {
			 e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		 }
	}

}
