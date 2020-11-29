package com.hjsj.hrms.transaction.performance.achivement.achivementTask;

import com.hjsj.hrms.businessobject.performance.achivement.Permission;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.HashMap;

/**
 *<p>Title:BatchUpdatePointValueTrans.java</p> 
 *<p>Description:批量修改</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 9, 2008</p> 
 *@author JinChunhai
 *@version 4.0
 */

public class BatchUpdatePointValueTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		RowSet rowSet = null;
		try
		{
			
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String target_id=(String)hm.get("target_id");
			String cycle=(String)hm.get("cycle");
			String pointId=(String)this.getFormHM().get("pointId");
			String point_value=(String)this.getFormHM().get("point_value");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			
			if(point_value.trim().length()>0)
			{
				String sqlStr="select * from per_target_list where target_id="+target_id;			
				rowSet=dao.search(sqlStr);			
				String object_type="";
				while(rowSet.next())
				{								
					object_type=rowSet.getString("object_type");
				}			
				
				String targetDataListSql=(String)this.getFormHM().get("targetDataListSql");
				rowSet=dao.search(targetDataListSql);			
				Permission p=new Permission(this.frameconn,this.userView);  //May 23 2011 JinChunhai修改
				while(rowSet.next())
				{
					String object_id=rowSet.getString("object_id");				
					String b0110=rowSet.getString("b0110")!=null?rowSet.getString("b0110"):"";
					String e0122=rowSet.getString("e0122")!=null?rowSet.getString("e0122"):"";
									
					boolean right = true;
					if(!"2".equals(object_type))  // 非2 团队
						right = p.getPrivPoint("", object_id, pointId);
					else if("2".equals(object_type))  // 2 人员
						right = p.getPrivPoint(b0110, e0122, pointId);
					if(right==true)
					{
						String sql="update per_target_mx set T_"+pointId+"="+point_value+" where target_id="+target_id+" and object_id="+object_id;
						if(!"-1".equals(cycle))
							sql+=" and kh_cyle='"+cycle+"'";
						dao.update(sql);
						
					}else
					{
						String sql="update per_target_mx set T_"+pointId+"=null where target_id="+target_id+" and object_id="+object_id;
						if(!"-1".equals(cycle))
							sql+=" and kh_cyle='"+cycle+"'";
						dao.update(sql);
					}					
				}
			}
			
			if(rowSet!=null)
				rowSet.close();
			
/*			
			if(point_value.trim().length()>0)
			{
				String sql="update per_target_mx set T_"+pointId+"="+point_value+" where target_id="+target_id;
				if(!cycle.equals("-1"))
					sql+=" and kh_cyle='"+cycle+"'";
				dao.update(sql);
			}			
*/			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
