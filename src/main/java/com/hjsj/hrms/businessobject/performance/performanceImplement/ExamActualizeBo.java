package com.hjsj.hrms.businessobject.performance.performanceImplement;

import com.hrms.frame.dao.ContentDAO;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * <p>Title:ExamActualizeBo.java</p>
 * <p>Description:设置主体权重</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 4, 2008:2:53:04 PM</p>
 * @author JinChunhai
 * @version 1.0
 */

public class ExamActualizeBo
{
	
	private String planid="";
	private Connection conn=null;
		
	public ExamActualizeBo(Connection a_con)
	{
		this.conn=a_con;
	}
	
	public ExamActualizeBo(Connection a_con,String planid)
	{
		this.planid=planid;
		this.conn=a_con;
		
	}
	/**
	 * 查询权重设置
	 * @param planid
	 * @return
	 */
	public ArrayList getPurviewList(String planid)
	{
		ArrayList list=new ArrayList();
		RowSet rowSet = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			String sql="select pmb.name,ppb.* from per_plan_body ppb,per_mainbodyset pmb where ppb.body_id=pmb.body_id  and ppb.plan_id="+planid+" order by pmb.seq";
			rowSet = dao.search(sql);
			while(rowSet.next())
			{
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("body_id", rowSet.getString("body_id"));
				abean.set("name",rowSet.getString("name"));				
				abean.set("rank",Double.toString(rowSet.getDouble("rank")));
				abean.set("pbOpt", new Integer(rowSet.getInt("opt"))); // 需要判断的主体打分确认标识 by 刘蒙
				
				list.add(abean);
			}
			
			if(rowSet!=null)
				rowSet.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 保存权重设置
	 * @param proportionList
	 */
	public void saveWeightValue(ArrayList proportionList)
	{
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			for(int i=0;i<proportionList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)proportionList.get(i);
				dao.update("update per_plan_body set rank="+(String)abean.get("rank")+"  where plan_id="+this.planid+" and body_id="+(String)abean.get("body_id"));
			}	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}	
}
