package com.hjsj.hrms.transaction.hire.employSummarise.personnelEmploy;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class GetDbnameListTrans extends IBusiness {

	public void execute() throws GeneralException {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			ArrayList dbnameList=new ArrayList();
			this.frowset=dao.search("select * from dbname");
			while(this.frowset.next())
			{
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("pre",this.frowset.getString("pre"));
				abean.set("dbname",this.frowset.getString("dbname"));
				dbnameList.add(abean);
			}
			this.getFormHM().put("dbnameList",dbnameList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
