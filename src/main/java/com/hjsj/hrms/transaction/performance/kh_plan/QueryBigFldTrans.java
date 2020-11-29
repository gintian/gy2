package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * <p>Title:QueryBigFldTrans.java</p>
 * <p>Description:查询大字段</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-07-09 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class QueryBigFldTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {
    	
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String planID = (String)hm.get("planID");
		String fieldName = (String)hm.get("fieldName");
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		StringBuffer strsql = new StringBuffer();
		strsql.append("select ");
		strsql.append(fieldName);
		strsql.append(" from per_plan where plan_id=");
		strsql.append(planID);
	
		String bigField="";
		
		try
		{
		    this.frowset  = dao.search(strsql.toString());
		    if(this.frowset.next())
		    {
		    	bigField = this.frowset.getString(fieldName);
		    	if(bigField==null)
		    		bigField="";
		    }
		    
		} catch (SQLException e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		
		this.getFormHM().put("bigField", bigField);	
    }

}
