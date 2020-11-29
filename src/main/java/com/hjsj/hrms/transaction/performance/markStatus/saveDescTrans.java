package com.hjsj.hrms.transaction.performance.markStatus;

import com.hjsj.hrms.businessobject.performance.markStatus.MarkStatusBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class saveDescTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
    		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
     	//	String  status=(String)hm.get("status");
    		String  planID=(String)hm.get("planID");
    		String  objectID=(String)hm.get("objectID");
    		String  mainbodyID=(String)hm.get("mainbodyID");
    		String  operater=(String)hm.get("operater");
		
    		String  description="";
	    	if(this.getFormHM().get("description")!=null)
	    		description=this.getFormHM().get("description").toString();
	    	String  isNoMark=this.getFormHM().get("isNoMark").toString();
	    	if("4".equals(isNoMark))
			{
				
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				if("4".equals(operater))
				{
			    	dao.delete("delete from per_table_"+planID+" where mainbody_id='"+mainbodyID+"' ",new ArrayList());
				}
				else
				{
					dao.delete("delete from per_table_"+planID+" where mainbody_id='"+mainbodyID+"' and object_id='"+objectID+"' ",new ArrayList());
				}

			}
	    	if("0".equals(isNoMark))
	    		 description="";
	    	MarkStatusBo markStatusBo=new MarkStatusBo(this.getFrameconn(),this.getUserView());
	    	markStatusBo.saveOrUpdateDesc_status(planID,objectID,mainbodyID,description,isNoMark,operater);
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
