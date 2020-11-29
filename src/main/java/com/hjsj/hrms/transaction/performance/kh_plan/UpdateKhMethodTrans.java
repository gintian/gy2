package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

/**
 * <p>Title:UpdateKhMethodTrans.java</p>
 * <p>Description:更新考核方法</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-10-15 13:00:00</p>
 * @author JinChunhai
 * @version 1.0
 */

public class UpdateKhMethodTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {	  
    	
    	String planId = (String)this.getFormHM().get("planID");
    	String method=(String)this.getFormHM().get("method");
    	ContentDAO dao = new ContentDAO(this.frameconn);
    	String strSql="update per_plan set method="+method+" where plan_id="+planId;
		try
		{
		    dao.update(strSql);
		    
		} catch (SQLException e)
		{
		    e.printStackTrace();
		}
    }
}
