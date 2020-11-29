package com.hjsj.hrms.transaction.performance;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.valueobject.UserView;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Title:AutoComputPlan.java</p>
 * <p>Description>:自动计算考核计划</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2013-06-25 下午03:56:27</p>
 * <p>@version: 7.0</p>
 * <p>@author: JinChunhai
 */

public class AutoComputPlan implements Job 
{

	public void execute(JobExecutionContext context) throws JobExecutionException 
	{
		Connection conn = null;
		RowSet rowSet = null;
		RowSet rs =null;
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
					buf.append(" where template_id = '"+ template_id +"' and status = '4' and (busitype is null or busitype='' or busitype='0') ");
					buf.append(" order by create_date desc ");
					rowSet = dao.search(buf.toString());
					if (rowSet.next())
					{
						String plan_id = isNull(rowSet.getString("plan_id")); 
											
						HashMap map = new HashMap();
						LoadXml loadXml = new LoadXml(conn,plan_id);
						Hashtable param = loadXml.getDegreeWhole();					
						map.put("ThrowHighCount", (String) param.get("ThrowHighCount"));
						map.put("ThrowLowCount", (String) param.get("ThrowLowCount"));
						map.put("KeepDecimal", (String) param.get("KeepDecimal"));
						map.put("UseWeight", (String) param.get("UseWeight"));
						map.put("UseKnow", (String) param.get("UseKnow"));
						map.put("KnowText", (String) param.get("KnowText"));
						map.put("AppUseWeight", (String) param.get("AppUseWeight"));
						map.put("EstBodyText", (String) param.get("EstBodyText"));
						map.put("ThrowBaseNum", (String) param.get("ThrowBaseNum"));
						if (param.get("formulaSql") != null)
							map.put("formulaSql", (String) param.get("formulaSql"));
						
						String EvalClass = (String)param.get("EvalClass");            //在计划参数中的等级分类ID
						if(EvalClass==null || EvalClass.trim().length()<=0 || "0".equals(EvalClass.trim()))
							EvalClass = (String)param.get("GradeClass");					//等级分类ID											
						if(EvalClass!=null && EvalClass.trim().length()>0)
							map.put("EvalClass",EvalClass);
						if (param.get("GradeClass") != null)
							map.put("GradeClass", (String) param.get("GradeClass"));
						if (param.get("NodeKnowDegree") != null)
							map.put("NodeKnowDegree", (String) param.get("NodeKnowDegree"));
						if (param.get("WholeEval") != null)
							map.put("WholeEval", (String) param.get("WholeEval"));
						if (param.get("UnLeadSingleAvg") != null)
							map.put("UnLeadSingleAvg", (String) param.get("UnLeadSingleAvg"));
						
						
						String password = "";
						rs = dao.search("select Password from operuser where UserName='su'");
						if (rs.next())
						{
							password = isNull(rs.getString("Password"));
						}
						UserView userView = new UserView("su",password,conn);					
						PerEvaluationBo bo = new PerEvaluationBo(conn,plan_id,"",userView);					
						bo.calculatePlan(userView,map,1);										
					}
				}
			}											
		
		}catch (Exception e) 
		{
			e.printStackTrace();
		}finally
		{
			PubFunc.closeResource(rs);
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

}
