package com.hjsj.hrms.transaction.performance.objectiveManage.manageKeyMatter;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

/**
 * <p>Title:DelKeyMatterTrans.java</p>
 * <p>Description:获得指标名称</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-07-21 13:00:00</p>
 * @author JinChunhai
 * @version 1.0
 * 
 */

public class GetPointNameTrans extends IBusiness
{
    public void execute() throws GeneralException
    {
		String point_id = (String)this.getFormHM().get("pointid");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String pointName = "";
		try
		{
			String sql="select pointname from per_point where upper(point_id)='"+point_id.toUpperCase()+"'";
			this.frowset=dao.search(sql);
			if(this.frowset.next())		
				pointName=this.frowset.getString(1)==null?"":this.frowset.getString(1);
		} catch (SQLException e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
	
		this.getFormHM().put("PointName", pointName);
		this.getFormHM().put("PointId", point_id);
    }

}
