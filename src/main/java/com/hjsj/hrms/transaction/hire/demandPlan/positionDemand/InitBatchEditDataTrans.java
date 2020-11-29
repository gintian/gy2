package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class InitBatchEditDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			ArrayList fieldList=new ArrayList();
			this.frowset=dao.search("select *  from  t_hr_busiField where fieldsetid='Z03' and itemtype<>'M' and state=1 and itemid<>'Z0301' and itemid<>'Z0319' and itemid<>'Z0307'  " +
					"and itemid<>'Z0311' and itemid<>'Z0325' and itemid<>'Z0321'  and itemid<>'Z0309' ");
			while(this.frowset.next())
			{
				LazyDynaBean abean=new LazyDynaBean();
				if("Z0101".equalsIgnoreCase(this.frowset.getString("itemid")))
					continue;
				abean.set("itemid",this.frowset.getString("itemid"));
				abean.set("itemdesc",this.frowset.getString("itemdesc"));
				abean.set("itemtype",this.frowset.getString("itemtype"));
				abean.set("itemlength",this.frowset.getString("itemlength"));
				abean.set("codesetid",this.frowset.getString("codesetid"));
				abean.set("decimalwidth",this.frowset.getString("decimalwidth"));
				fieldList.add(abean);
			}
			
			this.getFormHM().put("fieldList",fieldList);
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
