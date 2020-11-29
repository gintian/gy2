package com.hjsj.hrms.transaction.performance.implement;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:PlanDataMaintenanceTrans.java</p>
 * <p>Description:计划数据维护</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-10-28 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class PlanDataMaintenanceTrans extends IBusiness
{

    public void execute() throws GeneralException
    {
		String plan_id = (String)this.getFormHM().get("plan_id");
		ContentDAO dao = new ContentDAO(this.frameconn);
		String sql = "DELETE FROM per_mainbody WHERE plan_id="+plan_id+" and not (object_id IN (SELECT object_id FROM per_object WHERE plan_id="+plan_id+"))";
		String resultFlag="1";
		try
		{
		    dao.delete(sql, new ArrayList());
		    String table = "PER_POINTPRIV_"+plan_id;
		    sql="DELETE FROM "+table+" WHERE NOT EXISTS(SELECT * FROM per_mainbody B WHERE plan_id="+plan_id+" and "+table+".mainbody_id = B.mainbody_id AND "+table+".object_id = B.object_id)";
		    dao.delete(sql, new ArrayList());
		} catch (SQLException e)
		{
		    resultFlag="0";
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		this.getFormHM().put("resultFlag", resultFlag);
    }
}
