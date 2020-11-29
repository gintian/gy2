package com.hjsj.hrms.transaction.kq.options.machine;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class DeleteKqMachineTrans  extends IBusiness {

	public void execute() throws GeneralException
	{
	   ArrayList selectedinfolist=(ArrayList)this.getFormHM().get("selectedinfolist");
	   ArrayList deletelist=new ArrayList();
		StringBuffer delStr=new StringBuffer();
		delStr.append("delete from kq_machine_location");
		delStr.append(" where location_id=?");
	    ContentDAO dao=new ContentDAO(this.getFrameconn());
	    try
	    {
	    	for(int i=0;i<selectedinfolist.size();i++)
	        {
				
	    	    LazyDynaBean rec=(LazyDynaBean)selectedinfolist.get(i);   
	       	    String location_id=rec.get("location_id").toString();
	       	    ArrayList list=new ArrayList();
	       	    list.add(location_id);
	       	    deletelist.add(list );  	    
	       	          	              	
	        }
	    	dao.batchUpdate(delStr.toString(),deletelist);	 
	    }catch(Exception e)
	    {
	    	throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("delete.error"),"",""));
	    }
	}

}
