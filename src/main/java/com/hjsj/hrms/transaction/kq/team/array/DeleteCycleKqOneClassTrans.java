package com.hjsj.hrms.transaction.kq.team.array;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class DeleteCycleKqOneClassTrans extends IBusiness{

	public void execute() throws GeneralException
	{
		String id=(String)this.getFormHM().get("id");
		if(id==null||id.length()<=0)
			return;
		String sql="delete from kq_shift_class where id="+id;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			ArrayList list=new ArrayList();
		   dao.delete(sql,list);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
