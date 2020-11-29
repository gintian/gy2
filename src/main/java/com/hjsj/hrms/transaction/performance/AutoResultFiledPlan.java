package com.hjsj.hrms.transaction.performance;

import com.hjsj.hrms.businessobject.performance.ResultFiledBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.SystemConfig;
import org.apache.commons.beanutils.LazyDynaBean;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * <p>Title:AutoResultFiledPlan.java</p>
 * <p>Description>:自动归档考核计划</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2013-08-08 下午13:56:27</p>
 * <p>@version: 7.0</p>
 * <p>@author: JinChunhai
 */

public class AutoResultFiledPlan implements Job 
{

	public void execute(JobExecutionContext context) throws JobExecutionException 
	{
		Connection conn = null;
		RowSet rowSet = null;
		try 
		{
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			
			String autoTemplateid = SystemConfig.getPropertyValue("autoTemplateid"); // 模板编号
			if(autoTemplateid!=null && autoTemplateid.trim().length()>0) 
			{
				String[] matters = autoTemplateid.split(",");
				for (int i = 0; i < matters.length; i++)
				{
					String template_id = matters[i];
				
					StringBuffer buf = new StringBuffer("");
					buf.append("select plan_id,name,create_date from per_plan ");	
					buf.append(" where template_id = '"+ template_id +"' and status = '6' and (busitype is null or busitype='' or busitype='0') ");
					buf.append(" order by create_date desc ");
					rowSet = dao.search(buf.toString());
					if (rowSet.next())
					{
						String plan_id = isNull(rowSet.getString("plan_id")); 
											
						ResultFiledBo bo = new ResultFiledBo(plan_id, conn, "1");						
						bo.generateTempTable("0"); // 生成归档的临时表
						String setName = bo.getSetName();
						ArrayList list = bo.getPoints(setName,"0");		
						ArrayList sourceCodes = new ArrayList();
						ArrayList sourceNames = new ArrayList();
						ArrayList destCodes = new ArrayList();
						ArrayList destTypes = new ArrayList();
						for (int j = 0; j < list.size(); j++)
						{
							LazyDynaBean abean = (LazyDynaBean)list.get(j);
							String id = (String)abean.get("id");	
							String name = (String)abean.get("name");
							String destFldId = (String)abean.get("destFldId");
							String destType = (String)abean.get("destType");
							sourceCodes.add(id);
							sourceNames.add(name);
							destCodes.add(nullValue(destFldId));
							destTypes.add(nullValue(destType));
						}
						
					//	System.out.println("自动归档数据："+sourceCodes+"---------"+sourceNames+"---------"+destCodes+"---------"+destTypes+"---------"+setName);
						
						// 生成新的归档方案
					    bo.genetateXML(sourceCodes, sourceNames, destCodes, destTypes, setName);
					    // 将当前的记录保存到子集里				
					    bo.save(sourceCodes, sourceNames, destCodes, destTypes, setName, "su", "2");						
					    
					}
				}
			}											
		
		}catch (Exception e) 
		{
			e.printStackTrace();
		}finally
		{
			PubFunc.closeResource(rowSet);
			PubFunc.closeResource(conn);
		}
	}
	
	public String isNull(String str) 
	{
		if (str == null || str.trim().length() <= 0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str))
			return "";
		else
			return str;
	}
	public String nullValue(String str) 
	{
		if (str == null || str.trim().length() <= 0)
			return "noValue";
		else
			return str;
	}
}
