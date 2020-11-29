package com.hjsj.hrms.transaction.performance.batchGrade;

import com.hjsj.hrms.businessobject.performance.batchGrade.AnalysePlanParameterBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.singleGrade.DirectUpperPosBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Title:SearchBatchGradeTrans.java</p>
 * <p>Description:多人考评下拉框显示计划</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-10-29 11:28:36</p>
 * @author JinChunhai
 * @version 1.0
 */

public class SearchBatchGradeTrans extends IBusiness 
{

	public void execute() throws GeneralException
	{
		//非在职人员不允许使用改功能
		if(!"USR".equalsIgnoreCase(userView.getDbname())) {
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("notUsr.no.use.func")));
		}
		ArrayList dblist = new ArrayList();		
		String model="0";   //(String)this.getFormHM().get("model");   //  0：绩效考核  1：民主评测
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
		String linkType=(String)hm.get("linkType");
		
		String modelEmail=(String)hm.get("modelEmail");    //  发送邮件标志参数
		hm.remove("modelEmail");
		
		String returnflag=(String)hm.get("returnflag");
		this.getFormHM().put("returnflag",returnflag);
		
		try 
		{	
		//	ExamPlanBo ebo = new ExamPlanBo(this.frameconn);
		//	String controlByKHMoudle = ebo.getControlByKHMoudle(); // 考核计划按模板权限控制, True,False(默认)
			
			if(this.userView.getA0100()==null|| "".equals(this.userView.getA0100()))
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("employ.no.use.model")));
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String organization = userView.getUserOrgId();			
			// 得到绩效考核计划列表
			String perPlanSql = "select plan_id,name,status,parameter_content,template_id from per_plan where ( status=4 or status=6 ) ";
			if (!userView.isSuper_admin())
				perPlanSql += "and plan_id in (select plan_id from per_mainbody where mainbody_id='"
						+ userView.getA0100() + "' ) ";
			
			
	//		perPlanSql+=" and mainbody_id<>object_id ";
 
			if(!"USR".equalsIgnoreCase(userView.getDbname()))
				perPlanSql+=" and 1=2 ";
			perPlanSql += " and ( Method=1 or method is null ) and (busitype is null or busitype<>'1') order by "+Sql_switcher.isnull("a0000", "999999999")+" asc,plan_id desc";
			this.frowset = dao.search(perPlanSql);

			CommonData vo1 = new CommonData("0", " ");
			dblist.add(vo1);
			LoadXml aloadxml=null;
			while (this.frowset.next()) 
			{
			/*
				if(controlByKHMoudle!=null && controlByKHMoudle.trim().length()>0 && controlByKHMoudle.equalsIgnoreCase("True"))
				{
					String templateId = this.getFrowset().getString("template_id");				
					if(!(userView.isSuper_admin()) && templateId!=null && templateId.trim().length()>0)
					{
						//  写权限 templateId  读权限 templateId+"R"
						if(!userView.isHaveResource(IResourceConstant.KH_MODULE,templateId))					
							continue;					
					}
				}
			*/	
				String name = this.getFrowset().getString("name");
				String plan_id = this.getFrowset().getString("plan_id");
			//	String xmlContent =Sql_switcher.readMemo(this.frowset,"parameter_content");
            //  String performanceType=loadXml.getPerformanceType(xmlContent);
              
				if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id)==null)
				{
					aloadxml = new LoadXml(this.getFrameconn(),plan_id);
					BatchGradeBo.getPlanLoadXmlMap().put(plan_id,aloadxml);
				}
				else
				{
					aloadxml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id);
				}
				Hashtable htxml = aloadxml.getDegreeWhole();
				String performanceType=(String)htxml.get("performanceType");
				if(model.equals(performanceType))
                {
                //	if(loadXml.getHandEval(xmlContent).equalsIgnoreCase("FALSE"))
					if("FALSE".equalsIgnoreCase((String)htxml.get("HandEval")))
					{
                        CommonData vo = new CommonData(plan_id, name);
                        //特殊处理，为了前台可以选中制定的考核计划 解决项目bug:39081   haosl add  2017-11-28
                        if(!StringUtils.isBlank(plan_id) && plan_id.equals(hm.get("plan_id_db"))) {
                            dblist.add(1,vo);
                        }else {
                            dblist.add(vo);
                        }
                	}
                }
			}
			if(hm.containsKey("plan_id_db")) {
                hm.remove("plan_id_db");
            }
			DirectUpperPosBo bo=new DirectUpperPosBo();
			String flag=bo.getGradeFashion("0");
			AnalysePlanParameterBo _bo=new AnalysePlanParameterBo(this.getFrameconn());
			Hashtable ht=_bo.analyseParameterXml();
			String togetherCommit=(String)ht.get("TogetherCommit");
			userView.getHm().put("gradeFashion",flag);
			this.getFormHM().put("togetherCommit",togetherCommit);
			
			BatchGradeBo batchGradeBo=new BatchGradeBo(this.getFrameconn());
			ArrayList list = new ArrayList();
			
			if((modelEmail!=null) && (modelEmail.trim().length()>0) && ("0".equalsIgnoreCase(modelEmail)))
			{
				list=batchGradeBo.addGradeStaus(dblist,this.getUserView().getA0100(),0);
				this.getFormHM().put("modelEmail","true");
			}
			else
			{
				list=batchGradeBo.addGradeStaus(dblist,this.getUserView().getA0100(),3);
				this.getFormHM().put("modelEmail","false");
			}
			
			dblist=list;
			this.getFormHM().put("paramTable",ht);
			
		} catch (Exception e) 
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally 
		{
			this.getFormHM().put("plan_descript","");
			this.getFormHM().put("dblist", dblist);
			this.getFormHM().put("clear", "1");
			this.getFormHM().put("targetDeclare","");
			this.getFormHM().put("individualPerformance","");
			this.getFormHM().put("span_ids","");
			this.getFormHM().put("dbpre","0");
			this.getFormHM().put("linkType",linkType);
			
			this.getFormHM().put("script_code","");
		}

	}

}

/**
 * 
 * ArrayList dblist2=userView.getPrivDbList(); boolean isUsr=false;
 * //是否有在职人员库的权限 for(Iterator t=dblist2.iterator();t.hasNext();) { String
 * temp=(String)t.next(); if(temp.equals("Usr")) { isUsr=true; break; } }
 * if(!isUsr) return; else
 * 
 */
