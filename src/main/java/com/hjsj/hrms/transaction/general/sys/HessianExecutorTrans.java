/**
 * 
 */
package com.hjsj.hrms.transaction.general.sys;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:HessianExecutorTrans</p>
 * <p>Description:执行远端发启的命令</p> 
 * <p>Company:hjsj</p> 
 * create time at:Jan 10, 20073:15:28 PM
 * @author chenmengqing
 * @version 4.0
 */
public class HessianExecutorTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap map=this.getFormHM();
		try
		{
			String type=(String)map.get("type");
			if(type==null|| "".equals(type))
				throw new GeneralException(ResourceFactory.getProperty(""));


			ContentDAO dao=new ContentDAO(this.getFrameconn());
			if("1".equalsIgnoreCase(type)) //insert update create ,
			{
				String sql=(String)map.get("sql");				
				if(sql==null|| "".equals(sql))
					throw new GeneralException(ResourceFactory.getProperty("error.hessian.type"));				
				int count=dao.update(sql);
				this.getFormHM().put("record",new Integer(count));
			}
			else if("3".equals(type))
			{
				ArrayList list=(ArrayList)map.get("sqllist");
				if(list==null||list.size()==0)
					throw new GeneralException(ResourceFactory.getProperty("error.hessian.sql"));				
				dao.batchUpdate(list);
			}
			else  //select 查询语句
			{
				String sql=(String)map.get("sql");				
				if(sql==null|| "".equals(sql))
					throw new GeneralException(ResourceFactory.getProperty("error.hessian.sql"));				
				RowSet rowset=dao.search(sql);
				this.getFormHM().put("rowset",rowset);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
