package com.hjsj.hrms.transaction.general.statics;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class DeleteStaticTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String id = (String)this.getFormHM().get("id");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList alist=new ArrayList();
		StringBuffer sql=new StringBuffer();
		try{
			 sql.append("delete from sname where Id=?");
			 alist.add(id);
			 dao.delete(sql.toString(),alist);
			 sql.setLength(0);
			 alist.clear();
			 sql.append("delete  from slegend where Id = ?");
			 alist.add(id);
			 dao.delete(sql.toString(),alist);
			 
	  }
	  catch(Exception exx)
	  {
	  	       exx.printStackTrace();
	  	       throw GeneralExceptionHandler.Handle(exx);
	  }
	}
}
