package com.hjsj.hrms.transaction.performance.singleGrade;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.singleGrade.SingleGradeBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * <p>Title:SearchSingleGradeTrans2.java</p>
 * <p>Description:初始化考评对象</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-10-29 11:28:36</p>
 * @author JinChunhai
 * @version 1.0
 */

public class SearchSingleGradeTrans2 extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		
		ArrayList a_objectList=new ArrayList();
		String planID="0";
		String returnflag="";
		try
		{
			planID=(String)this.getFormHM().get("dbpre");	
			if("0".equals(planID))
				return;
			
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String refresh=(String) hm.get("refresh");
//			if(!"1".equals(refresh))//refresh=1 提交后刷新页面
//			this.getFormHM().put("object_id","0");
			hm.remove("refresh");
			String model=(String)hm.get("model"); //  0：绩效考核  1：民主评测   2:团队考核 3：人员考核  4：单位
			returnflag=(String)hm.get("returnflag");
			
			BatchGradeBo batchGradeBo=new BatchGradeBo(this.getFrameconn(),planID);
			/* 得到某计划某人的考评对象集合 */
			ArrayList objectList=batchGradeBo.getPerplanObjects(Integer.parseInt(planID),this.userView.getA0100(),"2" /* model */);
			StringBuffer userIDs=new StringBuffer("");	
			CommonData vo1=new CommonData("0"," ");
			a_objectList.add(vo1); 
			String object_id = "";
			int k = 0;
			for(Iterator t=objectList.iterator();t.hasNext();)
			{
				k++;
				String[] temp=(String[])t.next();
				if(k==1){
					object_id = temp[0]+"/"+temp[2];
				}
				CommonData vo=new CommonData(temp[0]+"/"+temp[2],temp[1]);
				a_objectList.add(vo);
			}
			this.getFormHM().put("object_id",object_id);
			
			
			// 得到绩效考核计划列表  主要用于更新状态
			if("1".equals(refresh)){//refresh=1 提交后刷新页面
				SingleGradeBo singleGradeBo=new SingleGradeBo(this.frameconn,this.userView);
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				String optObject=(String)hm.get("optObject");   // 1：领导班子  2：班子成员
				String perPlanSql = "select plan_id,name,status,parameter_content,object_type,template_id from per_plan where ( status=4 or status=6 ) ";
				if (!userView.isSuper_admin())
				{	
					perPlanSql += "and plan_id in (select plan_id from per_mainbody where ";
		/*			if(model!=null&&(model.equals("2")||model.equals("3")||model.equals("4")))
					{
						
					}
					else
						perPlanSql += " mainbody_id<>object_id and ";
		*/		
					perPlanSql+=" mainbody_id='"+ userView.getA0100() + "' )";
				
					if(!"USR".equalsIgnoreCase(userView.getDbname()))
						perPlanSql+=" and 1=2 ";
				}
				
				perPlanSql += " and ( Method=1 or method is null ) order by "+Sql_switcher.isnull("a0000", "999999999")+" asc,plan_id desc";
				this.frowset = dao.search(perPlanSql);
				ArrayList dblist=new ArrayList();
				CommonData vo = new CommonData("0", " ");
				dblist.add(vo);
				int i = 0;
				LoadXml loadXml=null; //new LoadXml();
				
				while (this.frowset.next()) 
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
					String name = this.getFrowset().getString("name");
					String plan_id = this.getFrowset().getString("plan_id");
				//	String xmlContent =Sql_switcher.readMemo(this.frowset,"parameter_content");
					String object_type=this.frowset.getString("object_type");
					
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
				//	String performanceType=loadXml.getPerformanceType(xmlContent);
		            if("1".equals(model))
		            {
		            	if(optObject!=null && "1".equals(optObject) && !("1".equals(object_type) || "3".equals(object_type) || "4".equals(object_type))){
		            		continue;
		            	}
		            	if(optObject!=null && "2".equals(optObject) && !"2".equals(object_type)){
		            		continue;
		            	}
		            }
		            
		            if(model!=null&&("2".equals(model)|| "3".equals(model)|| "4".equals(model)))
		            {
		            	if("2".equals(model))
		            	{
		            		if(!"4".equals(object_type)&&!"1".equals(object_type))
		            			continue;
		            		else
		            		{
		            			CommonData vo2 = new CommonData(plan_id, name);
			            		dblist.add(vo2);
			            		i++;
		            		}
		            	}
		            	else
		            	{
			            	int objectType=Integer.parseInt(model)-1;
			            	if(objectType!=Integer.parseInt(object_type))
			            		continue;
			            	else
			            	{
			            		CommonData vo2 = new CommonData(plan_id, name);
			            		dblist.add(vo2);
			            		i++;
			            	}
		            	}
		            }
		            else if(model.equals(performanceType))
		            {
		            	
		            	//if(loadXml.getHandEval(xmlContent).equalsIgnoreCase("FALSE"))
		            	if("FALSE".equalsIgnoreCase((String)htxml.get("HandEval")))
		            	{
		            		CommonData vo2 = new CommonData(plan_id, name);
		            		dblist.add(vo2);
		            		i++;
	                	}
		            }
				} //遍历完所有的计划
				
				ArrayList aList=new ArrayList();
				if(model!=null&&("2".equals(model)|| "3".equals(model)|| "4".equals(model)))
					aList=singleGradeBo.getBatchGradeBo().addGradeStaus(dblist,this.getUserView().getA0100(),1,0);
				else
					aList=singleGradeBo.getBatchGradeBo().addGradeStaus(dblist,this.getUserView().getA0100(),2,1);
				
				dblist=aList;
				this.getFormHM().put("dblist",dblist);
			}
			
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
			this.getFormHM().put("objectList",a_objectList);	
			this.getFormHM().put("gradeHtml"," ");
			this.getFormHM().put("personalComment"," ");
			this.getFormHM().put("scoreflag"," ");
			this.getFormHM().put("goalComment","");
			this.getFormHM().put("employRecordUrl","");
			this.getFormHM().put("returnflag",returnflag);
		}		
		
	}

}
