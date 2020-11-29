package com.hjsj.hrms.transaction.performance.implement;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Title:ImportMainbodyStd.java</p>
 * <p>Description:目标管理计划引入标准考核关系的主体</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-08-01 11:11:11</p> 
 * @author JinChunhai
 * @version 1.0
 */

public class ImportMainbodyStd extends IBusiness
{
	
	public void execute() throws GeneralException
	{
		
		HashMap hm = (HashMap) this.getFormHM();
		String objectIds = (String) hm.get("objectIDs");// 被粘贴的考核对象,可以是多个
		String[] objs = objectIds.split("@");

		String planid = (String) this.getFormHM().get("planid");
		PerformanceImplementBo pb = new PerformanceImplementBo(this.getFrameconn());
		RecordVo vo = pb.getPerPlanVo(planid);
		String object_type = String.valueOf(vo.getInt("object_type")); // 1部门 2：人员

		ContentDAO dao = new ContentDAO(this.frameconn);
		PerformanceImplementBo bo = new PerformanceImplementBo(this.getFrameconn());
		RowSet rowSet = null;
		if ("2".equals(object_type))
		{
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < objs.length; i++)
			{
				String obj = (String) objs[i];
				buf.append(",'" + obj + "'");
			}

			StringBuffer sql = new StringBuffer("select * from per_mainbody_std ");
			sql.append("where per_mainbody_std.body_id in (select body_id  from per_plan_body where plan_id=" + planid + ")");
			sql.append(" and object_id in (" + buf.substring(1) + ")");

			LazyDynaBean abean = null;
			ArrayList list = new ArrayList();
			try
			{
				rowSet = dao.search(sql.toString());
				while (rowSet.next())
				{
					String mainbody_id = rowSet.getString("mainbody_id");
					String object_id = rowSet.getString("object_id");
					String body_id = rowSet.getString("body_id");
					abean = new LazyDynaBean();
					abean.set("mainbody_id", mainbody_id);
					abean.set("object_id", object_id);
					abean.set("body_id", body_id);
					list.add(abean);
				}

			} catch (SQLException e)
			{
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}

			for (int i = 0; i < list.size(); i++)
			{
				abean = (LazyDynaBean) list.get(i);
				String mainbody_id = (String) abean.get("mainbody_id");
				String object_id = (String) abean.get("object_id");
				String body_id = (String) abean.get("body_id");
				bo.selMainBody("'" + mainbody_id + "'", planid, body_id, object_id,"false");
			}
			if(buf.length()>0)
				bo.executeSubordinateRecord(planid,buf.substring(1));
		} else
		{
			LazyDynaBean abean = null;
			
			for (int i = 0; i < objs.length; i++)
			{
				ArrayList list = new ArrayList();
				String object_id = (String) objs[i];
				StringBuffer sql = new StringBuffer("select * from per_mainbody_std ");
				sql.append("where per_mainbody_std.body_id in (select body_id  from per_plan_body where plan_id=" + planid + ")");
				sql.append(" and object_id = (select mainbody_id from per_mainbody where plan_id=" + planid + " and body_id=-1 and object_id='" + object_id + "')");

				try
				{
					rowSet = dao.search(sql.toString());
					while (rowSet.next())
					{
						// object_id:teamLeader:mainbody_id=1:1:n
						String mainbody_id = rowSet.getString("mainbody_id");
						String teamLeader = rowSet.getString("object_id");
						String body_id = rowSet.getString("body_id");
						abean = new LazyDynaBean();
						abean.set("mainbody_id", mainbody_id);
						abean.set("object_id", object_id);
						abean.set("body_id", body_id);
						abean.set("teamLeader", teamLeader);
						list.add(abean);
					}

				} catch (SQLException e)
				{
					e.printStackTrace();
					throw GeneralExceptionHandler.Handle(e);
				}

				for (int j = 0; j < list.size(); j++)
				{
					abean = (LazyDynaBean) list.get(j);
					String mainbody_id = (String) abean.get("mainbody_id");
					String body_id = (String) abean.get("body_id");
					bo.selMainBody("'" + mainbody_id + "'", planid, body_id, object_id,"false");
				}
			}
		}
		
		// 目标考核时，引入考核关系后，修改指定对象的Kh_relations为“非标准” -- >刘蒙
		// Kh_relations: 0=标准、1=非标准
		int method = vo.getInt("method"); // 考核方法
		if (method == 2) {
			String updateSql = "UPDATE per_object SET Kh_relations=1 WHERE plan_id=" + planid + " AND object_id=?";
			
			// 构建批量更新的参数值集合。objs --> objectIds
			List values = new ArrayList();
			for (int i = 0; i < objs.length; i++) {
				List value = new ArrayList();
				value.add(objs[i]);
				values.add(value);
			}
			
			try {
				dao.batchUpdate(updateSql, values);
			} catch (SQLException e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
		}

		try
		{
			if (rowSet != null)
				rowSet.close();
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
