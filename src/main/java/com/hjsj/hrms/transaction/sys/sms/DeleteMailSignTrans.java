package com.hjsj.hrms.transaction.sys.sms;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class DeleteMailSignTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList dlist = (ArrayList)this.getFormHM().get("selist");
		 if(dlist==null||dlist.size()==0)
	            return;
		 StringBuffer sb=new StringBuffer();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
		  for(int i=0;i<dlist.size();i++)
    	  {
    	     LazyDynaBean rec=(LazyDynaBean)dlist.get(i);   
    	     String sms_id=rec.get("sms_id").toString();
    	     sb.setLength(0);
    	     sb.append("delete from t_sys_smsbox where sms_id='");
    	     sb.append(sms_id);
    	     sb.append("'");

    	     dao.delete(sb.toString(),new ArrayList());
    	 }

		}catch(Exception exx)
		{
			exx.printStackTrace();
		}
	}


}
