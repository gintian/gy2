package com.hjsj.hrms.transaction.kq.machine;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class ViewMachineTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException 
	{
		 HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		 String location_id=(String)hm.get("id");
		 RecordVo vo=new RecordVo("kq_machine_location");
		 ContentDAO dao=new ContentDAO(this.getFrameconn());	     
	     try
	     {
	    	 vo.setString("location_id",location_id);
		   	 vo=dao.findByPrimaryKey(vo);
	     }catch(Exception e)
	     {
	    	 e.printStackTrace();
	    	 throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.machine.error"),"",""));
	     }
	     this.getFormHM().put("machine",vo);
	}

}
