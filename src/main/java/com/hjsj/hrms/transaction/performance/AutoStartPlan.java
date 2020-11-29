package com.hjsj.hrms.transaction.performance;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * <p>Title:AutoStartPlan.java</p>
 * <p>Description>:自动启动考核计划</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2013-06-25 下午03:56:27</p>
 * <p>@version: 7.0</p>
 * <p>@author: JinChunhai
 */

public class AutoStartPlan implements Job 
{

//	ExamPlanBo khPlanBo = null;
	
	public void execute(JobExecutionContext context) throws JobExecutionException 
	{
		Connection conn = null;
		RowSet rowSet =null;
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
					buf.append(" where template_id = '"+ template_id +"' and status = '3' and (busitype is null or busitype='' or busitype='0') ");
					buf.append(" order by create_date desc ");
					rowSet = dao.search(buf.toString());
					if (rowSet.next())
					{
						String plan_id = isNull(rowSet.getString("plan_id")); 
					//	RecordVo planVo = khPlanBo.getPerPlanVo(plan_id);
					//	String object_type = planVo.getString("object_type"); 
					//	String method = planVo.getString("method");
						
						// 检查per_plan表中有没有启动或分发的时间字段，若没有就创建  
					    editArticleDate(conn);
					    String creatDate = PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss"); // 系统当前日期
						creatDate=Sql_switcher.dateValue(creatDate);			    
						dao.update("update per_plan set status=4,execute_date="+creatDate+",execute_user='su' where plan_id="+plan_id);
						// 清空退回原因
						dao.update("update per_mainbody set reasons=null where plan_id ="+plan_id+" and status<>'1' and status<>'2' and object_id in (select object_id from per_object where plan_id = "+plan_id+" and sp_flag='03') ");				
						
						
						// 新建根据计算公式计算出总体评价的临时表并进行操作 
						PerformanceImplementBo bo = new PerformanceImplementBo(conn);
						LoadXml xml = new LoadXml(conn,plan_id);
						Hashtable params = xml.getDegreeWhole();
						String totalAppFormula =(String)params.get("TotalAppFormula"); // 总体评价的计算公式，默认为空
						if(totalAppFormula!=null && totalAppFormula.trim().length()>0)
							bo.creatTempWholeEvalTable(plan_id);
										
						String tablename = "per_result_" + plan_id;
						Table table = new Table(tablename);
						DbWizard dbWizard = new DbWizard(conn);
						boolean flag = false;
						if (!dbWizard.isExistField(tablename, "Body_id",false))
						{
							Field obj = new Field("Body_id");
							obj.setDatatype(DataType.INT);
							obj.setKeyable(false);
							table.addField(obj);
							flag = true;
						}
						if (!dbWizard.isExistField(tablename, "A0000", false))
						{
							Field obj = new Field("A0000");
							obj.setDatatype(DataType.INT);					
							obj.setKeyable(false);
							table.addField(obj);
							flag = true;
						}
						if (flag)
							dbWizard.addColumns(table);// 更新列
						
						String insertSql = "insert into per_result_" +plan_id+"(a0000,id,b0110,e0122,e01a1,object_id,a0101,body_id)"
					 		+"select a0000,id,b0110,e0122,e01a1,object_id,a0101,body_id from per_object where plan_id="+plan_id
					 		+" and object_id not in (select object_id from per_result_" +plan_id+")";
						dao.insert(insertSql, new ArrayList());
						 
						PerEvaluationBo pb = new PerEvaluationBo(conn);					 
						// 更新per_result_planid表中调整后的表结构的"子集"字段的值  JinChunhai 2011.03.08
						pb.updateSubset(plan_id);						
						// 更新per_result_planid表中调整后的表结构的"引入计划"的字段的值  JinChunhai 2011.03.08
						pb.updateResultTable(plan_id);
						 
						 					 
						//加载动态参数			
						LoadXml _loadxml = new LoadXml(conn,plan_id);
						BatchGradeBo.getPlanLoadXmlMap().put(plan_id,_loadxml);
							 
						BatchGradeBo bco=new BatchGradeBo(conn,plan_id);
						bco.setNoShowOneMark(true);
						bco.setLoadStaticValue(true);
						String templateId = bco.getPlanVo().getString("template_id");
						BatchGradeBo.getPlan_perPointMap().remove(plan_id);
						bco.getPerPointList(templateId,plan_id);
						BatchGradeBo.getPlan_perPointMap2().remove(plan_id);
						bco.getPerPointList2(templateId,plan_id);
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
	
	// 检查per_plan表中有没有启动或分发的时间字段，若没有就创建  
    public void editArticleDate(Connection conn) throws GeneralException
	{
		try
		{			
			Table table = new Table("per_plan");
			DbWizard dbWizard = new DbWizard(conn);
			DBMetaModel dbmodel = new DBMetaModel(conn);
			boolean flag = false;
						
			if (!dbWizard.isExistField("per_plan", "execute_user", false))
			{
				Field obj = new Field("execute_user");	
				obj.setDatatype(DataType.STRING);
				obj.setLength(50);
				obj.setKeyable(false);
				table.addField(obj);
				flag = true;
			}
			if (!dbWizard.isExistField("per_plan", "execute_date", false))
			{
				Field obj = new Field("execute_date");	
				obj.setDatatype(DataType.DATE);
				obj.setKeyable(false);
				table.addField(obj);
				flag = true;
			}
			if (!dbWizard.isExistField("per_plan", "distribute_user", false))
			{
				Field obj = new Field("distribute_user");	
				obj.setDatatype(DataType.STRING);
				obj.setLength(50);
				obj.setKeyable(false);
				table.addField(obj);
				flag = true;
			}
			if (!dbWizard.isExistField("per_plan", "distribute_date", false))
			{
				Field obj = new Field("distribute_date");	
				obj.setDatatype(DataType.DATE);
				obj.setKeyable(false);
				table.addField(obj);
				flag = true;
			}
			
			if(flag)
			{
				dbWizard.addColumns(table);// 更新列		
			    dbmodel.reloadTableModel("per_plan");
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
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
