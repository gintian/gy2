package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/** 
 *<p>Title:BacthDistributetTrans.java</p> 
 *<p>Description:批量启动计划</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Oct 17, 2011</p> 
 *@author JinChunhai
 *@version 5.0
 */

public class BacthDistributetTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		
		// 检查per_plan表中有没有启动或分发的时间字段，若没有就创建  
	    editArticleDate();
		
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String creatDate = PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss"); // 系统当前日期
		creatDate=Sql_switcher.dateValue(creatDate);
		try
		{
			String opt=(String)hm.get("opt");  // start:启动  pause:暂停
			String desc=(String)hm.get("desc");  // score:启动打分  result:启动录入结果 distribute:分发
			String degree=(String)hm.get("degree");  //等级分类
			String plan_ids=(String)hm.get("plan_ids");  // 批量分发或启动时的计划编号串
			plan_ids = plan_ids.replaceAll("／", "/");
			String signLogo=(String)hm.get("signLogo");  // 批量分发或启动的标志
			String plan_id=(String)this.getFormHM().get("planid");
			
			String[] planIds = null;
			if(signLogo!=null && signLogo.trim().length()>0 && "batchDisOrStart".equalsIgnoreCase(signLogo))
			{
				if(plan_ids==null || plan_ids.trim().length()<=0)
					return;
				
//				plan_ids = plan_ids.substring(0, plan_ids.length() - 1);
				planIds = plan_ids.split("/");
			}else
			{
				planIds=new String[1];
				planIds[0] = plan_id;
			}
			
			if(planIds!=null && planIds.length>0)
			{				
				for (int k = 0; k < planIds.length; k++)
				{
					String planid = planIds[k];							
			
					LoadXml loadXml=new LoadXml(this.getFrameconn(),planid,"");
					String noApproveTargetCanScore=(String)hm.get("noApproveTargetCanScore");
					if(noApproveTargetCanScore!=null && noApproveTargetCanScore.trim().length()>0 && "true".equalsIgnoreCase(noApproveTargetCanScore))
						noApproveTargetCanScore="True";
					else
						noApproveTargetCanScore="False";
					loadXml.saveAttribute("PerPlan_Parameter","NoApproveTargetCanScore",noApproveTargetCanScore);
								
					String status="4";
					if("pause".equals(opt))
					{
						status="5";
						dao.update("update per_plan set status="+status+" where plan_id="+planid);
					}
					else if("distribute".equals(opt))
					{
					    status="8";
					    dao.update("update per_plan set status="+status+",distribute_date="+creatDate+",distribute_user='"+this.userView.getUserFullName()+"' where plan_id="+planid);				
					}
					else
					{
						PerformanceImplementBo bo=new PerformanceImplementBo(this.getFrameconn());
						String method = bo.getPlanVo(planid).getString("method");
					
		//				if(bo.validateIsObject(planid))
						{					
							if("result".equals(desc))  //启动录入结果
							{
								loadXml.saveAttribute("PerPlan_Parameter","HandEval","True");
								loadXml.saveAttribute("PerPlan_Parameter","GradeClass",degree);
								dao.update("update per_plan set status=4,execute_date="+creatDate+",execute_user='"+this.userView.getUserFullName()+"' where plan_id="+planid);
							}
							else
							{
								dao.update("update per_plan set status=4,execute_date="+creatDate+",execute_user='"+this.userView.getUserFullName()+"' where plan_id="+planid);
								loadXml.saveAttribute("PerPlan_Parameter","HandEval","False");
								loadXml.saveAttribute("PerPlan_Parameter","GradeClass",degree);
							}
						}
						
						String tablename = "per_result_" + planid;
						Table table = new Table(tablename);
						DbWizard dbWizard = new DbWizard(this.frameconn);
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
						
						 String insertSql = "insert into per_result_" +planid+"(a0000,id,b0110,e0122,e01a1,object_id,a0101,body_id)"
					 		+"select a0000,id,b0110,e0122,e01a1,object_id,a0101,body_id from per_object where plan_id="+planid
					 		+" and object_id not in (select object_id from per_result_" +planid+")";
						 dao.insert(insertSql, new ArrayList());
						 
						 PerEvaluationBo pb = new PerEvaluationBo(this.getFrameconn(),this.userView);
						 
						 // 更新per_result_planid表中调整后的表结构的"子集"字段的值  JinChunhai 2011.03.08
						 pb.updateSubset(planid);
							
						 // 更新per_result_planid表中调整后的表结构的"引入计划"的字段的值  JinChunhai 2011.03.08
						 pb.updateResultTable(planid);
						 
						 				 				
					}
					//加载动态参数			
					LoadXml _loadxml=new LoadXml(this.getFrameconn(),planid);
					BatchGradeBo.getPlanLoadXmlMap().put(planid,_loadxml);
					 
					BatchGradeBo bo=new BatchGradeBo(this.getFrameconn(),planid);
					bo.setNoShowOneMark(true);
					bo.setLoadStaticValue(true);
					String template_id= bo.getPlanVo().getString("template_id");
					BatchGradeBo.getPlan_perPointMap().remove(planid);
					bo.getPerPointList(template_id,planid);
					BatchGradeBo.getPlan_perPointMap2().remove(planid);
					bo.getPerPointList2(template_id,planid);
					
				}
			}
			 
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	
	// 检查per_plan表中有没有启动或分发的时间字段，若没有就创建  
    public void editArticleDate() throws GeneralException
	{
		try
		{			
			Table table = new Table("per_plan");
			DbWizard dbWizard = new DbWizard(this.frameconn);
			DBMetaModel dbmodel = new DBMetaModel(this.getFrameconn());
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

}
