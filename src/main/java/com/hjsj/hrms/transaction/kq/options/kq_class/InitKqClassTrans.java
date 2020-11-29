package com.hjsj.hrms.transaction.kq.options.kq_class;

import com.hjsj.hrms.businessobject.kq.options.kq_class.KqClassConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class InitKqClassTrans extends IBusiness implements KqClassConstant{

	public void execute() throws GeneralException
	{
		String class_id="";
		String sql="select "+this.kq_class_id+" from "+this.kq_class_table+" where "+this.kq_class_id+"<>0 order by "+this.kq_class_id;    	
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	try
    	{
    		this.frowset=dao.search(sql);
    		if(this.frowset.next())
    		{
    			class_id=this.frowset.getString(this.kq_class_id);
    		}
    		
    	}catch(Exception e)
    	{
    	   e.printStackTrace();
    	   throw GeneralExceptionHandler.Handle(e);
    	}
		this.getFormHM().put("class_id", class_id);
	}

}
