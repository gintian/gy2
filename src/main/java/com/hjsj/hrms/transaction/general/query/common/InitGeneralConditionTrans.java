package com.hjsj.hrms.transaction.general.query.common;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class InitGeneralConditionTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			ArrayList selectedlist=new ArrayList();
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search("select id,name from gwhere");
			int num=1;
			while(this.frowset.next())
			{
				selectedlist.add(new CommonData(this.frowset.getString(1),num+"."+this.frowset.getString(2)));
				num++;
			}
			this.getFormHM().put("selectedCondlist",selectedlist);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
