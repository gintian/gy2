package com.hjsj.hrms.transaction.performance.implement;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.interfaces.general.PendingTask;
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
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/** 
 *<p>Title:StartPausePlanTrans.java</p> 
 *<p>Description:启动 或 暂停计划</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Apr 28, 2008</p> 
 *@author JinChunhai
 *@version 4.0
 */

public class StartPausePlanTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		
		// 检查per_plan表中有没有启动或分发的时间字段，若没有就创建  
	    editArticleDate();
		
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String creatDate = PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss"); // 系统当前日期
		creatDate=Sql_switcher.dateValue(creatDate);
		try
		{
			String opt=(String)hm.get("opt");  // start:启动  pause:暂停
			String desc=(String)hm.get("desc");  // score:启动打分  result:启动录入结果 distribute:分发
			String degree=(String)hm.get("degree");  //等级分类
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String  planid=(String)this.getFormHM().get("planid");
			LoadXml loadXml=new LoadXml(this.getFrameconn(),planid,"");
			String noApproveTargetCanScore=(String)this.getFormHM().get("noApproveTargetCanScore");
			if("true".equalsIgnoreCase(noApproveTargetCanScore))
				noApproveTargetCanScore="True";
			else
				noApproveTargetCanScore="False";
			loadXml.saveAttribute("PerPlan_Parameter","NoApproveTargetCanScore",noApproveTargetCanScore);
						
			String status="4";
			if("pause".equals(opt))
			{
				status="5";
				dao.update("update per_plan set status="+status+" where plan_id="+planid);
//				dao.update("update t_hr_pendingtask set pending_status='9' where pending_type='33' and ext_flag like '%_"+planid+"' and pending_status<>'2'");
				PendingTask pt = new PendingTask();
				RowSet rs=dao.search("select * from t_hr_pendingtask where pending_type='33' and ext_flag like '%_"+planid+"%' and pending_status<>'1'");
				while(rs.next()){					
					LazyDynaBean be = new LazyDynaBean();
					LazyDynaBean temp_bean=PerformanceImplementBo.updatePendingTask(this.frameconn, this.userView,"Usr"+this.userView.getA0100(),rs.getString("pending_id"),be,"3");
					if("update".equals(temp_bean.get("selfflag"))){
						//改成创建新的代办任务，清除旧任务，以满足第三方代办 zhanghua 2018-10-18
						pt.updatePending("P", "PER"+temp_bean.get("selfpending_id"), 100, "计划暂停", this.userView);
					}
				}
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
					if(desc!=null && desc.trim().length()>0 && "result".equals(desc))  //启动录入结果
					{
						loadXml.saveAttribute("PerPlan_Parameter","HandEval","True");
						loadXml.saveAttribute("PerPlan_Parameter","GradeClass",degree);
						//反馈后，暂停计划，重新启动打分，再到评估中去计算，计算完后状态直接为结果反馈了,解决：在启动的时候将反馈字段置为0，feedback：1-显示
						dao.update("update per_plan set feedback=0,status=4,execute_date="+creatDate+",execute_user='"+this.userView.getUserFullName()+"' where plan_id="+planid);
						
						// 清空退回原因
						if("True".equalsIgnoreCase(noApproveTargetCanScore))
							dao.update("update per_mainbody set reasons=null where plan_id="+planid+" and status<>'1' and status<>'2' ");
						else
							dao.update("update per_mainbody set reasons=null where plan_id ="+planid+" and status<>'1' and status<>'2' and object_id in (select object_id from per_object where plan_id = "+planid+" and sp_flag='03') ");
					}
					else
					{
						
							
//						if(bo.validateObjectIsMainbody(planid))
//						{
						    //360计划校验计划中考核主体与指标权限对应! //不在此验证了，改在选择等级之前进行验证 
//							if(method.equals("1"))
//							    if(!bo.validateMainbody_Priv(planid))
//								throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("performance.implement.info1")+"!"));
//							
//							dao.update("update per_plan set status=4 where plan_id="+planid);
//							loadXml.saveAttribute("PerPlan_Parameter","HandEval","False");
//							loadXml.saveAttribute("PerPlan_Parameter","GradeClass",degree);
//						    if(bo.validateMainbody_Priv(planid))
//							{
//								dao.update("update per_plan set status=4 where plan_id="+planid);
//								loadXml.saveAttribute("PerPlan_Parameter","HandEval","False");
//								loadXml.saveAttribute("PerPlan_Parameter","GradeClass",degree);
//							}
//							else
//								throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("performance.implement.info1")+"!"));
//						}
//						else
//							throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("performance.implement.info2")+"!"));
						
//						if(method.equals("1") && bo.validateObjectIsMainbody(planid)==false)//360计划才验证是否有考核主体
//							throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("performance.implement.info2")+"!"));
						dao.update("update per_plan set feedback=0,status=4,execute_date="+creatDate+",execute_user='"+this.userView.getUserFullName()+"' where plan_id="+planid);
						
						// 清空退回原因
						if("True".equalsIgnoreCase(noApproveTargetCanScore))
							dao.update("update per_mainbody set reasons=null where plan_id="+planid+" and status<>'1' and status<>'2' ");
						else
							dao.update("update per_mainbody set reasons=null where plan_id ="+planid+" and status<>'1' and status<>'2' and object_id in (select object_id from per_object where plan_id = "+planid+" and sp_flag='03') ");
					
						loadXml.saveAttribute("PerPlan_Parameter","HandEval","False");
						loadXml.saveAttribute("PerPlan_Parameter","GradeClass",degree);
					}
				}
				
				// 新建根据计算公式计算出总体评价的临时表并进行操作 JinChunhai 2012.11.13
				LoadXml xml = new LoadXml(this.getFrameconn(),planid);
				Hashtable params = xml.getDegreeWhole();
				String totalAppFormula =(String)params.get("TotalAppFormula"); // 总体评价的计算公式，默认为空
				if(totalAppFormula!=null && totalAppFormula.trim().length()>0)
					bo.creatTempWholeEvalTable(planid);
								
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
				//结果表增加字段“reviseScore”
				if (!dbWizard.isExistField(tablename, "reviseScore", false))
				{
					Field obj = new Field("reviseScore");
					obj.setDatatype(DataType.FLOAT);
					obj.setLength(8);
					obj.setDecimalDigits(2);
					//obj.setKeyable(true);
					table.addField(obj);
					flag = true;
				}
				//结果表增加字段“whole_score”
				if (!dbWizard.isExistField(tablename, "whole_score", false))
				{
					Field obj = new Field("whole_score");
					obj.setDatatype(DataType.FLOAT);
					obj.setLength(12);
					obj.setDecimalDigits(5);
					//obj.setKeyable(true);
					table.addField(obj);
					flag = true;
				}
				//结果表新增考核结果确认
				if (!dbWizard.isExistField("per_result_"+planid, "confirmflag",false)) {
    				Field field = new Field("confirmflag");
    				field.setDatatype(DataType.INT);
    				field.setLength(2);
    				table.addField(field);
    				flag=true;
    			}
				if (flag)
					dbWizard.addColumns(table);// 更新列
				//添加新增的考核对象
				 String insertSql = "insert into per_result_" +planid+"(a0000,id,b0110,e0122,e01a1,object_id,a0101,body_id)"
			 		+"select a0000,id,b0110,e0122,e01a1,object_id,a0101,body_id from per_object where plan_id="+planid
			 		+" and object_id not in (select object_id from per_result_" +planid+")";
				 dao.insert(insertSql, new ArrayList());
				 //可能是重新启动的计划，这时候要清空以前的确认结果
				 dao.update("update " + tablename + " set confirmflag = 0");
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
