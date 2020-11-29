package com.hjsj.hrms.transaction.performance.singleGrade;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class DeleteReportWorkTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String id=(String)hm.get("id");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String planid=(String)hm.get("planid");
			/*
			String planid=(String)hm.get("planid");
			String objectid=(String)hm.get("objectid");
			StringBuffer sql = new StringBuffer();
			sql.append("update ");
			sql.append("per_result_"+planid);
			sql.append(" set affix=null,ext=null where object_id='");
			sql.append(objectid+"'");
			dao.delete(sql.toString(),new ArrayList());
			*/
			dao.update("delete from  per_article where article_id="+id);
			
			this.getFormHM().put("dbpre",planid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
