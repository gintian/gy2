/*
 * Created on 2006-4-27
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_sumup;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ResetZpjobTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		RecordVo rv=(RecordVo)this.getFormHM().get("zpSumupvo");
		String plan_id_value = (String)this.getFormHM().get("plan_id_value");
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        ArrayList list = new ArrayList();
        ArrayList infoList = new ArrayList();
        try
        {
            this.getFormHM().put("resource_id_name","");
            rv.setString("description","");
            rv.setString("real_invite_amount","");         
        }
        catch(Exception sqle)
        {
  	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);            
        }
        finally
        {
            this.getFormHM().put("zpSumupDetailslist",list);
            this.getFormHM().put("infoList",infoList);
            this.getFormHM().put("zpSumupvo",rv);
        }

	}

}
