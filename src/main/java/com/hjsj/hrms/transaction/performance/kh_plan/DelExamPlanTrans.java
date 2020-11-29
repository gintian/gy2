package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:DelExamPlanTrans.java</p>
 * <p>Description:删除考核计划交易类</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-11-14 10:28:35</p> 
 * @author JinChunhai
 * @version 5.0
 */

public class DelExamPlanTrans extends IBusiness
{

	public void execute() throws GeneralException
	{

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String delStr = (String) hm.get("deletestr");
		delStr = delStr.replaceAll("／", "/");
		delStr = delStr.substring(0, delStr.length() - 1);
		try
		{
			String[] plans = delStr.split("/");
			StringBuffer ids = new StringBuffer();
			for (int i = 0; i < plans.length; i++)
			{
				ids.append(plans[i]);
				ids.append(",");
			}
			ids.setLength(ids.length() - 1);
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			StringBuffer strSql = new StringBuffer();
			strSql.append("select name from per_plan where plan_id  in (");
			strSql.append(ids.toString());
			strSql.append(")");
			RowSet rs = dao.search(strSql.toString());
			StringBuffer context = new StringBuffer();
			context.append("删除指标：");
			while(rs.next()){
				context.append(rs.getString("name")+",");
			}
			if(context.length()>0){
				this.getFormHM().put("@eventlog", context.toString());
			}
			this.delExamPlans(plans, "per_plan");
			this.delExamPlans(plans, "p04");
			this.delExamPlans(plans, "per_plan_body");
			this.delExamPlans(plans, "per_object");
			this.delExamPlans(plans, "per_mainbody");
			this.delExamPlans(plans, "per_dyna_rank");
			this.delExamPlans(plans, "per_dyna_bodyrank");
			this.delPendingTask(plans);
			DbWizard dbw=new DbWizard(this.frameconn);
			
			for (int i = 0; i < plans.length; i++)
			{
				String tableName = "PER_POINTPRIV_" + plans[i];
				if(dbw.isExistTable(tableName, false))
					dbw.dropTable(tableName);
				
				tableName = "PER_ITEMPRIV_" + plans[i];
				if(dbw.isExistTable(tableName, false))
					dbw.dropTable(tableName);
				
				tableName = "per_table_" + plans[i];
				if(dbw.isExistTable(tableName, false))
					dbw.dropTable(tableName);
				
				tableName = "per_table_" + plans[i];
				if(dbw.isExistTable(tableName, false))
					dbw.dropTable(tableName);
				
				tableName = "per_BodyScore_" + plans[i];
				if(dbw.isExistTable(tableName, false))
					dbw.dropTable(tableName);
			}
		}catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void delExamPlans(String[] plans, String tableName)
	{

		ContentDAO dao = new ContentDAO(this.getFrameconn());

		StringBuffer ids = new StringBuffer();
		for (int i = 0; i < plans.length; i++)
		{
			ids.append(plans[i]);
			ids.append(",");
		}
		ids.setLength(ids.length() - 1);
		try
		{
			StringBuffer strSql = new StringBuffer();
			strSql.append("delete from " + tableName + " where plan_id  in (");
			strSql.append(ids.toString());
			strSql.append(")");

			dao.delete(strSql.toString(), new ArrayList());

		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 删除对应的待办表内的待办信息  zhaoxg add 2014-9-3
	 * @param plans
	 */
	public void delPendingTask(String[] plans)
	{

		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			for (int i = 0; i < plans.length; i++)
			{
				dao.delete("delete from t_hr_pendingtask where ext_flag like '%_"+plans[i]+"' and pending_type='33'", new ArrayList());
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}
