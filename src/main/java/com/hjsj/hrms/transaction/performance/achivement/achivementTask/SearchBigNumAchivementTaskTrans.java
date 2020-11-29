package com.hjsj.hrms.transaction.performance.achivement.achivementTask;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;

/**
 * <p>Title:SearchBigNumAchivementTaskTrans.java</p>
 * <p>Description>:业绩任务书</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Dec 17, 2010 14:15:35 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class SearchBigNumAchivementTaskTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		RowSet rowSet = null;
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());			
			String opt = (String)this.getFormHM().get("opt");
			
			String strSql = ("select target_id,count(*) num from per_target_point where 1 = 1 GROUP BY target_id");					
			rowSet=dao.search(strSql);
			
			ArrayList list=new ArrayList();
			while(rowSet.next())
			{
//				LazyDynaBean bean = new LazyDynaBean();
				
				String target_id=rowSet.getString("target_id")!=null?rowSet.getString("target_id"):"";
				String num=rowSet.getString("num")!=null?rowSet.getString("num"):"";
				
//				bean.set("target_id", target_id);
//				bean.set("num", num);	
				
				list.add(num);
			}			
			this.getFormHM().put("list", list);
			
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
