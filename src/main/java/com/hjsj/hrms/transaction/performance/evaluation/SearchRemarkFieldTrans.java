package com.hjsj.hrms.transaction.performance.evaluation;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class SearchRemarkFieldTrans extends IBusiness
{
	
	public void execute() throws GeneralException
	{
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String planid = (String) hm.get("planid");
		hm.remove("planid");		
		String objectid = (String) hm.get("objectid");
		hm.remove("objectid");		
		String fieldid = (String)hm.get("fieldid");
		hm.remove("fieldid");
		
		
		String remarkFieldValue = "";//字段中的值
		String remarkFieldName = "";//字段的名字
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			String strSql = "select "+fieldid+" from PER_RESULT_" + planid +" where object_id = '"+objectid+"'";
			this.frowset = dao.search(strSql);
			if (this.frowset.next())
				remarkFieldValue = this.frowset.getString(fieldid)==null?"": this.frowset.getString(fieldid);
			
			String fieldNameSql = "select itemdesc from fielditem where itemid='"+fieldid+"'";
			this.frowset = dao.search(fieldNameSql);
			if(this.frowset.next())
				remarkFieldName = this.frowset.getString("itemdesc");
			
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally
		{
			this.getFormHM().put("remarkFieldValue", remarkFieldValue);
			this.getFormHM().put("remarkFieldName", remarkFieldName);
		}

	}
}
