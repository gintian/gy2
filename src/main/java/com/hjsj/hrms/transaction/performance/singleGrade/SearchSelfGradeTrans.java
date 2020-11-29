package com.hjsj.hrms.transaction.performance.singleGrade;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Title:SearchSelfGradeTrans.java</p>
 * <p>Description:初始化自我评价</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-10-29 11:28:36</p>
 * @author JinChunhai
 * @version 1.0
 */

public class SearchSelfGradeTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		
		if(this.userView.getA0100()==null|| "".equals(this.userView.getA0100()))
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("employ.no.use.model")));
		//非在职人员不允许使用改功能
		if(!"USR".equalsIgnoreCase(userView.getDbname())) {
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("notUsr.no.use.func")));
		}
		ArrayList dblist=new ArrayList();
		ArrayList objectList=new ArrayList();
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String model=(String)hm.get("model"); //  0：绩效考核  1：民主评测
		this.getFormHM().put("model",model);
		this.getFormHM().put("performanceType",model);
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			
		//	ExamPlanBo bo = new ExamPlanBo(this.frameconn);
		//	String controlByKHMoudle = bo.getControlByKHMoudle(); // 考核计划按模板权限控制, True,False(默认)
			
			String organization=userView.getUserOrgId();
		/*	ArrayList dblist2=userView.getPrivDbList(); 	
			boolean isUsr=false;   //是否有在职人员库的权限
			for(Iterator t=dblist2.iterator();t.hasNext();)
			{
				String temp=(String)t.next();
				if(temp.equals("Usr"))
				{
					isUsr=true;
					break;
				}
			}
			if(!isUsr)
				return;
			else
			{  */
				String _str="";
				if(Sql_switcher.searchDbServer()==Constant.ORACEL)
					_str=" pms.level_o";
				else
					_str=" pms.level ";
				  //得到绩效考核计划列表
	            String perPlanSql="select plan_id,name,status,parameter_content,object_type,template_id from per_plan where ( status=4 or status=6 ) ";
	            //if(!userView.isSuper_admin())
	            perPlanSql+="and ( method is null or method=1 )" ;
	            //以下代码考虑到 即使不能自我评价，但也能填写自己的绩效报告和 个人目标
	            perPlanSql+=" and ( plan_id in (select plan_id from per_mainbody where  object_id='"+userView.getA0100()+"'  )"; 	
	            perPlanSql+=" or plan_id in (select pm.plan_id from per_mainbody pm,per_mainbodyset pms ";
	            perPlanSql+=" where pm.body_id=pms.body_id and "+_str+"=5 and mainbody_id='"+userView.getA0100()+"' and pm.object_id<>pm.mainbody_id )  ) ";
	           
	            if(!"USR".equalsIgnoreCase(userView.getDbname()))
					perPlanSql+=" and 1=2 ";
	            
	            perPlanSql+=" order by "+Sql_switcher.isnull("a0000", "999999999")+" asc,plan_id desc";
	            this.frowset=dao.search(perPlanSql);
	        
	            CommonData vo1=new CommonData("0"," ");
	       		dblist.add(vo1);
	       		
	       		LoadXml loadXml=null; //new LoadXml();
	            while(this.frowset.next())
	            {
	            /*
	            	if(controlByKHMoudle!=null && controlByKHMoudle.trim().length()>0 && controlByKHMoudle.equalsIgnoreCase("True"))
					{
						String template_id = this.getFrowset().getString("template_id");				
						if(!(userView.isSuper_admin()) && template_id!=null && template_id.trim().length()>0)
						{
							//  写权限 template_id  读权限 template_id+"R"
							if(!userView.isHaveResource(IResourceConstant.KH_MODULE,template_id))					
								continue;					
						}
					}
	            */	
	                String name=this.getFrowset().getString("name");
	                String plan_id=this.getFrowset().getString("plan_id");
	                int object_type=this.getFrowset().getInt("object_type");
	                if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id)==null)
					{
						
						loadXml = new LoadXml(this.getFrameconn(),plan_id);
						BatchGradeBo.getPlanLoadXmlMap().put(plan_id,loadXml);
					}
					else
					{
						loadXml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id);
					}
	                Hashtable htxml = loadXml.getDegreeWhole();
					String performanceType=(String)htxml.get("performanceType");
					String SelfEvalNotScore=(String)htxml.get("SelfEvalNotScore");  ///自我评价不显示打分
			        String allowUploadFile = (String)htxml.get("AllowUploadFile"); //是否支持附件上传
					this.getFormHM().put("allowUploadFile", allowUploadFile);
	                if(model.equals(performanceType))
	                {
	                	if("FALSE".equalsIgnoreCase((String)htxml.get("HandEval")))
	                	{
	                		if(!validateSelfScore(plan_id,htxml,dao,object_type))
	                			continue;
	                		CommonData vo=new CommonData(plan_id,name);
	                		 //特殊处理，为了前台可以选中制定的考核计划 解决项目bug:32960   haosl add  2017-11-28
	                		 if(!StringUtils.isBlank(plan_id) && plan_id.equals(hm.get("plan_id_db"))) {
	                			 dblist.add(1,vo);
	                		 }else {
	                			 dblist.add(vo);
	                		 }
	                	}
	                }
	            }
                if(hm.containsKey("plan_id_db")){
                    hm.remove("plan_id_db");
                }
	            BatchGradeBo batchGradeBo=new BatchGradeBo(this.getFrameconn());
	            ArrayList aList=batchGradeBo.addGradeStaus(dblist,this.getUserView().getA0100(),1);
	            dblist=aList;	    
	            
	            if(dblist.size()>1&&hm.get("plan_id_db")!=null)
	            {
	            	String plan_id=(String)hm.get("plan_id_db");
	            	 //将待办置为已阅
					PendingTask pt = new PendingTask();
					String pendingCode = getPendingCode(plan_id,this.userView.getA0100());			
					if(pendingCode!=null && pendingCode.trim().length()>0)
					{				
						pt.updatePending("P", pendingCode, 2, ResourceFactory.getProperty("performance.singlegrade.planGrade"),this.userView);
					}	
					hm.remove("plan_id_db");
	            }
		//	}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		finally
		{
			this.getFormHM().put("individualPerformance","");
			this.getFormHM().put("notMark","");
			this.getFormHM().put("targetDeclare","");
			this.getFormHM().put("dblist",dblist);
			this.getFormHM().put("dbpre","0");
			this.getFormHM().put("object_id",this.getUserView().getA0100());
			this.getFormHM().put("objectList",objectList);
			this.getFormHM().put("gradeHtml"," ");
			this.getFormHM().put("personalComment"," ");
			this.getFormHM().put("scoreflag"," ");
			this.getFormHM().put("isSelfMark","1");
			this.getFormHM().put("employRecordUrl", "");
			this.getFormHM().put("plan_descript_content", "");
		}
	}
	
	
	
	public boolean validateSelfScore(String plan_id,Hashtable htxml,ContentDAO dao,int object_type)
	{
		boolean flag=true;
		try
		{
			int n=0;
			String _sql="";
			if(object_type==2)
				_sql="select count(mainbody_id) from per_mainbody where object_id='"+userView.getA0100()+"' and mainbody_id='"+userView.getA0100()+"' and plan_id="+plan_id;
			else
			{
				String _str="";
				if(Sql_switcher.searchDbServer()==Constant.ORACEL)
					_str=" pms.level_o";
				else
					_str=" pms.level ";
				_sql="select count(pm.mainbody_id) from per_mainbody pm,per_mainbodyset pms where pm.plan_id="+plan_id+"   ";
				_sql+=" and pm.body_id=pms.body_id and "+_str+"=5 and pm.mainbody_id='"+userView.getA0100()+"' and pm.object_id<>pm.mainbody_id "; 
			}
			RowSet rowSet=dao.search(_sql);
			if(rowSet.next())
			{
				 if(rowSet.getInt(1)==0)
				 {
					 String SummaryFlag=((String)htxml.get("SummaryFlag")).toLowerCase();     //个人总结报告
					 String SelfEvalNotScore=((String)htxml.get("SelfEvalNotScore")).toLowerCase(); //自我评价不显示打分
					 if("False".equalsIgnoreCase(SummaryFlag))
						 flag=false;
				 }
				 else
				 {
					 String SelfEvalNotScore=((String)htxml.get("SelfEvalNotScore")).toLowerCase(); //自我评价不显示打分
					 String SummaryFlag=((String)htxml.get("SummaryFlag")).toLowerCase();     //个人总结报告
					 if("True".equalsIgnoreCase(SelfEvalNotScore)&& "False".equalsIgnoreCase(SummaryFlag))
						 flag=false;
				 }
			}
			
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	
	
	
	/**
	* 取得需置为已办的id
	* @param mainBodyId
	* @param nbase 
	* @return
	*/
	public String getPendingCode(String plan_id,String mainBodyId)
	{
		String id = "";
		RowSet rowSet = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.frameconn);
							
			String sql="select task_id from per_task_pt where plan_id="+ plan_id +" and mainbody_id='"+ mainBodyId +"' and flag=2";						
			rowSet = dao.search(sql);
			if(rowSet.next())
				id=rowSet.getString("task_id");
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
		return id;
	}
	
}
