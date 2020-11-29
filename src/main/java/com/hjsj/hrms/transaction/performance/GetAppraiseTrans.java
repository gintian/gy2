package com.hjsj.hrms.transaction.performance;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class GetAppraiseTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String  planFlag=(String)hm.get("picFlag");
		String  operator=(String)hm.get("planFlag");   //1:员工考核结果   2:本人考核结果
		
		String  userID="";
		if("2".equals(operator))
			userID=this.getUserView().getA0100();
		else
			userID=hm.get("objectId").toString();
		String  appraise="";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			{
				DBMetaModel dbmodel=new DBMetaModel(this.getFrameconn());
				dbmodel.reloadTableModel("per_result_"+planFlag);
		    }
			this.frowset=dao.search("select * from per_result_"+planFlag+" where object_id='"+userID+"'");			
			if(this.frowset.next())
			{
				appraise=Sql_switcher.readMemo(this.frowset,"appraise");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		this.getFormHM().put("drawingFlag","2");
		this.getFormHM().put("planNum",planFlag);
		this.getFormHM().put("appraise",appraise.replaceAll("\r\n","<br>"));

	}

}
