package com.hjsj.hrms.module.template.utils;

import com.hjsj.hrms.module.kq.interfaces.KqAppInterface;
import com.hjsj.hrms.module.talentmarkets.competition.businessobject.impl.JobPreparationServiceImpl;
import com.hjsj.hrms.module.talentmarkets.utils.TalentMarketsUtils;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.util.ArrayList;

public class TemplateInterceptorAdapter {
	
	
	
	/**
	 * 删除单据记录
	 * @param recordList 删除的记录集
	 * @param tabid  模板ID
	 * @param paramBo
	 * @param userview
	 * @throws GeneralException
	 */
	public static void deleteRecords(ArrayList recordList,int tabid,TemplateParam paramBo,UserView userview) throws GeneralException
	{ 
		Connection conn = null;
		try{
			conn = AdminDb.getConnection(); 
			if(paramBo==null)
				paramBo=new TemplateParam(conn, userview, tabid);	
			
		    if(paramBo.getInfor_type()==1) //人员模板
			{
				//人才市场移动端内部竞聘人员报名模板tabid
				String applyTemplateTabId = TalentMarketsUtils.getApplyTemplate();
				//录用审批模板tabid
				String hireTemplateTabId= TalentMarketsUtils.getHireTemplate();
				if(StringUtils.equalsIgnoreCase(String.valueOf(tabid),applyTemplateTabId) || StringUtils.equalsIgnoreCase(String.valueOf(tabid),hireTemplateTabId)){
					BusinessService businessService = new JobPreparationServiceImpl();
					businessService.execution(recordList, tabid, "cancel", userview);
				}
		    	
		    	
			}
		    else if(paramBo.getInfor_type()==3) //岗位模板
			{
                //内部竞聘发布申请模板tabid
                String internalCompetitionTabId = TalentMarketsUtils.getReleasePostTemplate();
                if(StringUtils.equalsIgnoreCase(String.valueOf(tabid),internalCompetitionTabId)){
                    BusinessService businessService = new JobPreparationServiceImpl();
                    businessService.execution(recordList, tabid, "cancel", userview);
                }
		    	
			}
		    
		}
		catch(Exception e)
		{
			e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		}
		finally
		{
			PubFunc.closeDbObj(conn);
		} 
	}
	
	
	
	
	/**
	 * 人事异动流程流转前统一调用此方法
	 * @param task_id  任务ID
	 * @param paramBo 模板对象类
	 * @param opt  apply:流程发起时调用  
	 *             appeal:流程报批时调用   
	 *             approve:流程批准时调用   
	 *             reject:流程驳回时调用   
	 *             submit:流程提交入库时调用
	 * @throws GeneralException
	 */
	public static void preHandle(String tabname,int tabid,int task_id,TemplateParam paramBo,String opt,UserView userview,String whl) throws GeneralException
	{ 
		Connection conn = null;
		try{
			conn = AdminDb.getConnection();
			
			if(paramBo==null)
				paramBo=new TemplateParam(conn, userview, tabid);	
			
		    if(paramBo.getInfor_type()==1) //人员模板
			{
				//   kq_type:考勤方式 1:加班申请 q11  2:请假申请 q15   3：公出申请 q13
				 String kq_type = paramBo.getKq_type();
				 //新考勤模板调用逻辑
				 if(StringUtils.isNotBlank(kq_type) && ("1".equals(kq_type) || "2".equals(kq_type) || "3".equals(kq_type)))
				 {
					 String mapping = paramBo.getKq_field_mapping(); 
				     String kqTab = paramBo.getKq_setid();
					 
					 //业务类需实现  BusinessService.execution()接口
				     ArrayList recordVoList = TemplateInterceptorAdapter.getRecordList(tabname, 0, task_id, userview, whl);
				     BusinessService kqBusisService = new KqAppInterface();
				     kqBusisService.execution(recordVoList, tabid, opt, userview, kqTab, mapping);
				 }
			}
			else if(paramBo.getInfor_type()==3) //岗位模板
			{


				
			}
			
			 
			 
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		}
		finally
		{
			PubFunc.closeDbObj(conn);
		} 
	}
	
	
	
	
	/**
	 * 人事异动流程流转逻辑执行完后统一调用此方法
	 * @param task_id  任务ID
	 * @param ins_id   实例ID
	 * @param paramBo 模板对象类
	 * @param opt  apply:流程发起时调用  
	 *             appeal:流程报批时调用   
	 *             approve:流程批准时调用   
	 *             reject:流程驳回时调用   
	 *             submit:流程提交入库时调用
	 *             stop:流程终止
	 *             recall:流程撤回
	 * @throws GeneralException
	 */
	public static void afterHandle(int task_id,int ins_id,int tabid,TemplateParam paramBo,String opt,UserView userview) throws GeneralException
	{ 
		Connection conn = null;
		try{
			conn = AdminDb.getConnection(); 
			if(paramBo==null)
				paramBo=new TemplateParam(conn, userview, tabid);	
			
		    if(paramBo.getInfor_type()==1) //人员模板
			{
				//应聘人员申请模板tabid
				String applyTemplateTabId = TalentMarketsUtils.getApplyTemplate();
				//拟录用审批模板tabid
				String hireTemplateTabId = TalentMarketsUtils.getHireTemplate();
				//发布简历模板id
				String talentDisplayTemplateId = TalentMarketsUtils.getTalentDisplayTemplate();
				//撤销简历模板id
				String cancelTemplateId = TalentMarketsUtils.getCancelTemplate();
				if(StringUtils.equalsIgnoreCase(String.valueOf(tabid),applyTemplateTabId)||StringUtils.equalsIgnoreCase(String.valueOf(tabid),hireTemplateTabId)
				   ||StringUtils.equalsIgnoreCase(String.valueOf(tabid),talentDisplayTemplateId)||StringUtils.equalsIgnoreCase(String.valueOf(tabid),cancelTemplateId)){
					BusinessService businessService = new JobPreparationServiceImpl();
					ArrayList recordList = TemplateInterceptorAdapter.getRecordList("templet_"+tabid, ins_id,task_id, userview, "");
					businessService.execution(recordList, tabid, opt, userview);
				}
		    	
			}
		    else if(paramBo.getInfor_type()==3) //岗位模板
			{
				//内部竞聘发布申请模板tabid
				String internalCompetitionTabId = TalentMarketsUtils.getReleasePostTemplate();
				if(StringUtils.equalsIgnoreCase(String.valueOf(tabid),internalCompetitionTabId)){
					BusinessService businessService = new JobPreparationServiceImpl();
					ArrayList recordList = TemplateInterceptorAdapter.getRecordList("templet_"+tabid, ins_id,task_id, userview, "");
					businessService.execution(recordList, tabid, opt, userview);
				}
		    	
			}
		    
		}
		catch(Exception e)
		{
			e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		}
		finally
		{
			PubFunc.closeDbObj(conn);
		} 
	}
	
	
	/**
	 * 获得表单数据
	 * @param tabname
	 * @param task_id
	 * @param userview
	 * @return
	 * @throws GeneralException
	 */
	private static ArrayList getRecordList(String tabname,int ins_id,int task_id,UserView userview,String whl) throws GeneralException
	{
		ArrayList recordList=new ArrayList();
		Connection conn = null;
		try{
			conn = AdminDb.getConnection();
			ContentDAO dao=new ContentDAO(conn);
			String sql="";
			if(task_id==0&&ins_id==0)
			{
				if(tabname.toLowerCase().indexOf("g_templet_")!=-1)  //自助业务申请
				{
					sql="select * from "+tabname+"  where a0100='"+userview.getA0100()+"' and lower(basepre)='"+userview.getDbname().toLowerCase()+"'";
				}
				else //人事异动
				{
					sql="select * from "+tabname+"  where submitflag=1 "+whl;
				}
			}
			else
			{
				if(task_id!=0)
				{
					sql="select  "+tabname+".* from t_wf_task_objlink td,"+tabname+" where  "+tabname+".seqnum=td.seqnum ";
					sql+=" and td.ins_id="+tabname+".ins_id and td.submitflag=1 and td.state<>3 and td.task_id="+task_id;
					 
				}
				else if(ins_id!=0)
				{
					sql="select * from "+tabname+" where ins_id="+ins_id;
				}
			}
			recordList=dao.searchDynaList(sql);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		}
		finally
		{
			PubFunc.closeDbObj(conn);
		}  
		return recordList;
	}
	
	
}
