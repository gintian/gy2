package com.hjsj.hrms.transaction.sys.sms;

import com.hjsj.hrms.businessobject.sys.SmsBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class ReSendSmsTran extends IBusiness {

	public void execute() throws GeneralException {
	
		ArrayList dlist = (ArrayList)this.getFormHM().get("selist");
		if(dlist==null||dlist.size()<=0)
			return;
		try
		{
			  ArrayList list=new ArrayList();
			  for(int n=0;n<dlist.size();n++)
			  {
				  LazyDynaBean vo=(LazyDynaBean)dlist.get(n); 
				  LazyDynaBean lbean=new LazyDynaBean();
				  lbean.set("sender",(String)vo.get("sender"));
				  lbean.set("receiver",(String)vo.get("receiver"));
				  lbean.set("phone_num",(String)vo.get("mobile_no"));
				  lbean.set("msg",(String)vo.get("msg"));
				  lbean.set("sms_id",(String)vo.get("sms_id"));
				  list.add(lbean);
			  }
			  SmsBo sbms_bo=new SmsBo(this.getFrameconn());
			  sbms_bo.batchSendMessage(list);
			  this.getFormHM().clear();
			  this.getFormHM().put("message",ResourceFactory.getProperty("label.sms.success"));			  
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);			
		}
	}

}
