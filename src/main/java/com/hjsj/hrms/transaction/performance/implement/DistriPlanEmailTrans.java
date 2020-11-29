package com.hjsj.hrms.transaction.performance.implement;

import com.hjsj.hrms.businessobject.attestation.AttestationUtils;
import com.hjsj.hrms.businessobject.dingtalk.DTalkBo;
import com.hjsj.hrms.businessobject.general.email_template.EmailTemplateBo;
import com.hjsj.hrms.businessobject.hire.HireOrderBo;
import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.sys.EMailBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.sendmessage.weixin.WeiXinBo;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Title:DistriPlanEmailTrans.java</p>
 * <p>Description:计划实施过程中发送邮件</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-10-29 11:28:36</p>
 * @author JinChunhai
 * @version 1.0
 */

public class DistriPlanEmailTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {   
    	
	    //情况一： 360计划 启动时候 考核对象类别为人员和部门都发送邮件给考核主体
	    //情况二： 目标计划 分发和启动时候 考核对象类别为人员时给考核对象发邮件，考核对象类别为团队给团队负责人发邮件
    	StringBuffer somepeople=new StringBuffer("向以下人员发送邮件失败："+"\r\n");//保存邮箱地址不存在的人员信息
    	String isnull="";
    	String contentname="";
		String plan_ids = (String) this.getFormHM().get("plan_id");
		String oper = (String) this.getFormHM().get("oper");// start || distribute
		String contentUrl="";
		String khPlan_ids = (String) this.getFormHM().get("khPlan_ids"); //  需全部启动的计划id
		this.getFormHM().put("khPlan_ids", khPlan_ids);
		
		String logoSign = (String)this.getFormHM().get("signLogo"); // 批量分发或启动的标志
		String[] planIds = null;
		if(logoSign!=null && logoSign.trim().length()>0 && "batchDisOrStart".equalsIgnoreCase(logoSign))
		{
			if(plan_ids==null || plan_ids.trim().length()<=0)
				return;
			
//			plan_ids = plan_ids.substring(0, plan_ids.length() - 1);
			planIds = plan_ids.replaceAll("／", "/").split("/");
		}else
		{
			planIds=new String[1];
			planIds[0] = plan_ids;
		}
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());		
		PerformanceImplementBo pb = new PerformanceImplementBo(this.getFrameconn());
		
		//启动需返回参数
		String startFlag = (String) this.getFormHM().get("startFlag");
		String degree = (String) this.getFormHM().get("degree");
		this.getFormHM().put("startFlag", startFlag);
		this.getFormHM().put("degree", degree);				
		try
		{
			/** 绩效发邮件时:
				分发:  只发起草状态的
				启动时：360度， 只发打分状态为 起草和正在编辑状态的  
			          目标，只发打分状态为 起草和正在编辑状态的本人，判断（1）不允许对未批准的目标卡评分，只发目标卡已批的，（2）允许对未批准的目标卡评分，不判断目标卡状态
			*/
			if(planIds!=null && planIds.length>0)
			{	
				String templateId = "";  // 邮件模板			
				String markingMode = "1";  // 计划表现方式： 1 计划下拉框 2 平铺计划
				
				this.frowset = dao.search("select str_value from constant where constant='PER_PARAMETERS'");
			    if ( this.frowset.next())
			    {
			    	String str_value = this.frowset.getString("str_value");
			    	if (str_value == null || (str_value != null && "".equals(str_value)))
			    	{
		
			    	} else
			    	{
			    		Document doc = PubFunc.generateDom(str_value);
			    		String xpath = "//Per_Parameters";
			    		XPath xpath_ = XPath.newInstance(xpath);
			    		Element ele = (Element) xpath_.selectSingleNode(doc);
			    		Element child;
			    		if (ele != null)
			    		{
			    			child = ele.getChild("Plan");
							if (child != null)
							{
							    markingMode = child.getAttributeValue("MarkingMode");						    
							}
			    			
			    			if("start".equalsIgnoreCase(oper))//启动 发邮件用 考评分需邮件通知的模板
			    			{
			    				child = ele.getChild("TargetAppraises");
			    				if (child != null)
			    				{
			    				    templateId = child.getAttributeValue("template");
			    				    if("-1".equals(templateId))
			    				    	throw GeneralExceptionHandler.Handle(new Exception("考评分需邮件通知模板没有设置!"));
			    				}
			    			}else if( "distribute".equalsIgnoreCase(oper))//分发 发邮件用 目标卡定制过程需邮件通知
			    			{
			    				child = ele.getChild("TargetCard");
			    				if (child != null)
			    				{
			    				    templateId = child.getAttributeValue("template");
			    				    if("-1".equals(templateId))
			    				    	throw GeneralExceptionHandler.Handle(new Exception("目标卡定制过程需邮件通知模板没有设置!"));
			    				}
			    			}
			    		}
			    	}
			    }		
			    EmailTemplateBo bo = new EmailTemplateBo(this.getFrameconn());
			    /** 得邮件指标 */
			    String emailfield = bo.getEmailField(templateId);
			    /** 得模板标题 */
			    String subject = bo.getEmailTemplateSubject(templateId);
			    /** 得包含邮件指标的主集，以便取得实际的邮件地址 */
			    String emailfieldset = bo.getEmailFieldSetId(emailfield);
			    /** 得模板项目列表 */
			    ArrayList list = bo.getTemplateFieldInfo(Integer.parseInt(templateId), 2);		
			    /** 取邮件模板内容 */
			    String contentTemp = bo.getEmailContent(Integer.parseInt(templateId));		
			    RecordVo vo = ConstantParamter.getConstantVo("SS_EMAIL");
			    if ("#".equals(vo.getString("str_value")))
			    	throw GeneralExceptionHandler.Handle(new Exception("系统没有设置邮件指标,运行错误!"));		
			    HireOrderBo hirebo = new HireOrderBo();
			    String fromAddr = hirebo.getFromAddr();		
			    EMailBo emailbo = new EMailBo(this.getFrameconn(), true, "Usr");
			    
				for (int k = 0; k < planIds.length; k++)
				{
					String plan_id = planIds[k];
					
					// 获得需要的计划参数
				    LoadXml loadXml = null; //new LoadXml();
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
	                
	                // 目标卡未审批也允许打分 True, False, 默认为 False
	                // 先从页面获取(目标卡未审批时启动会提示勾选此选项)，没有值才选择计划参数 lium
	                String noApproveTargetCanScore = (String) formHM.get("noApproveTargetCanScore");
	                if (noApproveTargetCanScore == null || "".equals(noApproveTargetCanScore)) {
	                	noApproveTargetCanScore = (String)htxml.get("NoApproveTargetCanScore");
	                }
	                String GradeByBodySeq = (String)htxml.get("GradeByBodySeq"); //按考核主体顺序号控制评分流程(True, False默认为False)						
	                String performanceType = (String) htxml.get("performanceType");	//考核形式（0：绩效考核  1：民主评测）	
					RecordVo plan_vo=pb.getPerPlanVo(plan_id);
					String method=String.valueOf(plan_vo.getInt("method"));
					String object_type=String.valueOf(plan_vo.getInt("object_type"));   //1部门 2：人员

					ArrayList emailReceivorList = new ArrayList();//邮件接收人列表					
					
				    String sql = "";
				    //	  情况一： 360计划 启动时候 考核对象类别为人员和部门都发送邮件给考核主体
				    if("1".equals(method) && "start".equalsIgnoreCase(oper))
				        sql ="select distinct mainbody_id from per_mainbody where plan_id=" + plan_id + " and ("+ Sql_switcher.isnull("STATUS", "0") +"=0 or status=0 or status='1') ";	
				    else if("2".equals(method) && ("start".equalsIgnoreCase(oper) || "distribute".equalsIgnoreCase(oper)))
				    {
				    	if("True".equalsIgnoreCase(GradeByBodySeq) && "start".equalsIgnoreCase(oper))
				    	{
				    		String temp="";
				    		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				    			temp=" pms.level_o";
							else
								temp=" pms.level ";
							if(noApproveTargetCanScore!=null && "false".equalsIgnoreCase(noApproveTargetCanScore))
					    		sql ="select  pm.mainbody_id,pm.object_id,"+temp+" from per_mainbody pm,per_object po,per_mainbodyset pms,per_plan_body ppb where pm.body_id=pms.body_id and pm.object_id=po.object_id and pm.plan_id=ppb.plan_id and pm.body_id=ppb.body_id and (ppb.isgrade is null or ppb.isgrade='' or ppb.isgrade='0') and pm.plan_id=po.plan_id and pm.plan_id=" + plan_id + " and ("+ Sql_switcher.isnull("pm.STATUS", "0") +"=0 or pm.status=0 or pm.status='1') and (po.sp_flag='03') ";
					    	else		    			
					    		sql ="select  mainbody_id,object_id,"+temp+" from per_mainbody pm,per_mainbodyset pms,per_plan_body ppb where pm.body_id=pms.body_id and pm.plan_id=" + plan_id + " and pm.plan_id=ppb.plan_id and pm.body_id=ppb.body_id and (ppb.isgrade is null or ppb.isgrade='' or ppb.isgrade='0') and ("+ Sql_switcher.isnull("pm.STATUS", "0") +"=0 or pm.status=0 or pm.status='1') ";		    			    		
							sql+=" and nullif(pm.seq,0) is not null and pm.seq=(select min(seq) from per_mainbody a where plan_id="+plan_id+" and a.object_id=pm.object_id and nullif(seq,0) is not null group by object_id  ) " ;
															
						//	this.frowset = dao.search(sql);
						//  while (this.frowset.next())	
						//	{
							    	
						//    	emailReceivorList.add("Usr" +  this.frowset.getString(1) + "~" + this.frowset.getString(2) + "`" + this.frowset.getString(3));	 		    	
								  			
						//	}				    						    		
				    	}
				    	else
				    	{
					        //情况二： 目标计划 分发和启动时候 考核对象类别为人员时给考核对象发邮件，考核对象类别为团队给团队负责人发邮件
					    	if("2".equals(object_type))
					    	{
					    		pb.creatTempTable(plan_id); // 新建临时表并进行操作
					    		
					    		if("distribute".equalsIgnoreCase(oper))
					    		{		
						    		sql ="select object_id from t#_per_Email where plan_id=" + plan_id + " and status='0' and (sp_flag is null or sp_flag='01') ";		
					    		}else
					    		{
					    			if(noApproveTargetCanScore!=null && "false".equalsIgnoreCase(noApproveTargetCanScore))
						    			sql ="select object_id from t#_per_Email where plan_id=" + plan_id + " and (status='0' or status='1') and (sp_flag='03')";
						    		else
						    			sql ="select object_id from t#_per_Email where plan_id=" + plan_id + " and (status='0' or status='1') ";
					    		}
					    	}
					    	else
					    	{	
					    		if("distribute".equalsIgnoreCase(oper))
					    		{		
						    		sql ="select pm.mainbody_id,pm.object_id from per_mainbody pm,per_object po where pm.object_id=po.object_id and pm.plan_id=po.plan_id and pm.plan_id=" + plan_id + " and ("+ Sql_switcher.isnull("STATUS", "0") +"=0 or pm.status=0) and (po.sp_flag is null or po.sp_flag='01') and pm.body_id=-1";
						    	}else
					    		{
					    			if(noApproveTargetCanScore!=null && "false".equalsIgnoreCase(noApproveTargetCanScore))
						    			sql ="select pm.mainbody_id,pm.object_id from per_mainbody pm,per_object po where pm.object_id=po.object_id and pm.plan_id=po.plan_id and pm.plan_id=" + plan_id + " and ("+ Sql_switcher.isnull("STATUS", "0") +"=0 or pm.status=0 or pm.status='1') and (po.sp_flag='03') and pm.body_id=-1";
						    		else		    			
						    			sql ="select mainbody_id,object_id from per_mainbody where plan_id=" + plan_id + " and ("+ Sql_switcher.isnull("STATUS", "0") +"=0 or status=0 or status='1') and body_id=-1";		    			    		
					    		}
					    	}
				    	
				    	}
				    	
				    	
				    }				    
				    this.frowset = dao.search(sql);
				    while (this.frowset.next())	
				    {
				    	if("1".equals(method) && "start".equalsIgnoreCase(oper))
				    		emailReceivorList.add("Usr" +  this.frowset.getString(1));	
					    else if("2".equals(method) && ("start".equalsIgnoreCase(oper) || "distribute".equalsIgnoreCase(oper)))
					    {
					    	if("True".equalsIgnoreCase(GradeByBodySeq) && "start".equalsIgnoreCase(oper))
					    		emailReceivorList.add("Usr" +  this.frowset.getString(1) + "~" + this.frowset.getString(2) + "`" + this.frowset.getString(3));	
					    	else if("2".equals(object_type))
					    		emailReceivorList.add("Usr" +  this.frowset.getString(1));	
					    	else
					    		emailReceivorList.add("Usr" +  this.frowset.getString(1) + ":" +this.frowset.getString(2));				    	
					    }				
				    }			
				    
				    				    
					 
				    // 目标计划启动需给不参与审批流程的考核主体发送待办和邮件通知
				    if("2".equals(method) && ("start".equalsIgnoreCase(oper)))
				    {
				    	if("True".equalsIgnoreCase(GradeByBodySeq))
				    	{
				    		String temp="";
				    		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				    			temp=" pms.level_o";
							else
								temp=" pms.level ";
				    		if(noApproveTargetCanScore!=null && "false".equalsIgnoreCase(noApproveTargetCanScore))
				    			sql ="select pm.mainbody_id,pm.object_id,"+temp+" from per_mainbody pm,per_object po,per_mainbodyset pms,per_plan_body ppb where pm.body_id=pms.body_id and pm.object_id=po.object_id and pm.plan_id=po.plan_id and pm.plan_id=" + plan_id + " and pm.plan_id=ppb.plan_id and pm.body_id=ppb.body_id and (ppb.isgrade is null or ppb.isgrade='' or ppb.isgrade='0') and ("+ Sql_switcher.isnull("pm.STATUS", "0") +"=0 or pm.status=0 or pm.status='1') and (po.sp_flag='03') ";
					    	else		    			
					    		sql ="select mainbody_id,object_id,"+temp+" from per_mainbody pm,per_mainbodyset pms,per_plan_body ppb where pm.body_id=pms.body_id and pm.plan_id=" + plan_id + " and pm.plan_id=ppb.plan_id and pm.body_id=ppb.body_id and (ppb.isgrade is null or ppb.isgrade='' or ppb.isgrade='0') and ("+ Sql_switcher.isnull("pm.STATUS", "0") +"=0 or pm.status=0 or pm.status='1') ";		    			    		
							sql+=" and nullif(pm.seq,0) is null" ;
							this.frowset = dao.search(sql);
						    while (this.frowset.next())	
							{	
						    	
						    	emailReceivorList.add("Usr" +  this.frowset.getString(1) + "~" + this.frowset.getString(2) + "`" + this.frowset.getString(3));	    											  			
							
							}								    		
				    	}
				    	else
				    	{				    					    	
					    	int property = 10;
					    	String targetMakeSeries = (String) htxml.get("targetMakeSeries"); // 目标卡制订支持几级审批
					    	if(targetMakeSeries!=null && targetMakeSeries.trim().length()>0)
					    		property = Integer.parseInt(targetMakeSeries);
					    	
					    	String level_str="";
							switch (property)
							{
								case 1:
									level_str="5,1";
									break;
								case 2:
									level_str="5,1,0";
									break;
								case 3:
									level_str="5,1,0,-1";
									break;
								case 4:
									level_str="5,1,0,-1,-2";
									break;
							}
							String temp="";
				    		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				    			temp=" pms.level_o";
							else
								temp=" pms.level ";
														
							StringBuffer strSql=new StringBuffer("");							
							if(noApproveTargetCanScore!=null && "false".equalsIgnoreCase(noApproveTargetCanScore))
							{
								strSql.append("select pm.mainbody_id,pm.object_id,"+temp+" from per_mainbody pm,per_object po,per_mainbodyset pms "); 
								strSql.append(" where pm.body_id=pms.body_id and pm.object_id=po.object_id and pm.plan_id=po.plan_id "); 
								strSql.append(" and pm.plan_id=" + plan_id + " and ("+ Sql_switcher.isnull("pm.STATUS", "0") +"=0 or pm.status=0 or pm.status='1') "); 
								strSql.append(" and "+temp+" not in ("+level_str+") and (po.sp_flag='03') "); 
							}
							else
							{
								strSql.append("select pm.mainbody_id,pm.object_id,"+temp+" "); 							
								strSql.append(" from per_mainbody pm,per_mainbodyset pms where pm.body_id=pms.body_id "); 
								strSql.append(" and pm.plan_id="+Integer.parseInt(plan_id)+" and "+temp+" not in ("+level_str+") ");							
								strSql.append(" and ("+ Sql_switcher.isnull("pm.STATUS", "0") +"=0 or pm.status=0 or pm.status='1') ");
							}							
							this.frowset=dao.search(strSql.toString());			    	
							while (this.frowset.next())	
						    {					    	
						//	    if(object_type.equals("2"))			    	
							    	emailReceivorList.add("Usr" +  this.frowset.getString(1) + "~" + this.frowset.getString(2) + "`" + this.frowset.getString(3));
						//	    else
						//	    	emailReceivorList.add("Usr" +  this.frowset.getString(1) + ":" + this.frowset.getString(2));				    							    				
						    }
						
				    	}
				    	
				    }				    
				    
				    // 获得当前计划的所有考核主体 
	                HashMap mainbodyidMap = new HashMap();  
	                if(("2".equals(method)) && ("start".equalsIgnoreCase(oper)))
	                	mainbodyidMap = pb.getMainbodyidMap(plan_id);
	                
	                // 如果只有自评，（包括团队负责人），即不需要给其它对象评分的标志 
	                HashMap selfMainbodyMap = new HashMap();  
	                if(("1".equals(method)) && ("start".equalsIgnoreCase(oper)))
	                	selfMainbodyMap = pb.getSelfMainbodyMap(plan_id);
	                
	                
	                ArrayList mainbodylist = new ArrayList();
	                String mailTogoLink = (String)htxml.get("MailTogoLink"); // 评分邮件通知、待办任务界面，360默认为1：多人考评界面 2：单人考评界面 3：不发邮件。目标一样
	                
				    for (int i = 0; i < emailReceivorList.size(); i++)
				    {
				    	boolean bflag = false;
				    	String body_id = "5";
						String prea0100 = (String) emailReceivorList.get(i);
						String preaObject_id = prea0100.substring(3);
						if("2".equals(method) && ("start".equalsIgnoreCase(oper) || "distribute".equalsIgnoreCase(oper)))
					    {
					    	if((prea0100.indexOf("~")!=-1) && (prea0100.indexOf("`")!=-1))			    	
					    	{
					    		String preMainBody_id = (String) emailReceivorList.get(i);
					    		prea0100 = preMainBody_id.substring(0,preMainBody_id.indexOf("~"));
					    		if("1".equals(mailTogoLink)){
						    		if(mainbodylist == null || mainbodylist.size()==0 ){
						    			mainbodylist.add(prea0100);
						    		}
						    		else if(mainbodylist != null && mainbodylist.size()>0) {
						    			for(int m=0;m<mainbodylist.size();m++){
						    				if(prea0100.equals(mainbodylist.get(m))){
						    					bflag=true;
						    					break;
						    				}
						    			}
						    			if(bflag)
							    			continue;
							    		else
					    					mainbodylist.add(prea0100);
						    		}
					    		}
					    		preaObject_id = preMainBody_id.substring(preMainBody_id.indexOf("~")+1,preMainBody_id.indexOf("`"));
 					    		body_id = preMainBody_id.substring(preMainBody_id.indexOf("`")+1);
					    		
					    	}else if(!"2".equals(object_type))
					    	{		
					    		String preMainBody_id = (String) emailReceivorList.get(i);
					    		prea0100 = preMainBody_id.substring(0,preMainBody_id.indexOf(":"));
					    		preaObject_id = preMainBody_id.substring(preMainBody_id.indexOf(":")+1);					    		
					    	}
					    }
						// 此处控制目标计划启动时，若考核对象不参与评分则不给对象发邮件
						if(("2".equals(method)) && ("start".equalsIgnoreCase(oper)))
						{
							if(mainbodyidMap.get(prea0100.substring(3))==null)
								continue;
						}
						LazyDynaBean abean=getUserNamePassword(prea0100);
						if(abean!=null)
						{
							String username=(String)abean.get("username");
							String password=(String)abean.get("password");
							String a0101=(String)abean.get("a0101");				
							StringBuffer content=new StringBuffer("");
							String emailContent = getFactContent(contentTemp, prea0100, list, this.userView, plan_vo, a0101);
							content.append(emailContent);
															
							// 360计划 启动时候 考核对象类别为人员和部门都发送邮件给考核主体
						    if(("1".equals(method)) && ("start".equalsIgnoreCase(oper)))
						    {	
						    	
						    	if(selfMainbodyMap.get(prea0100.substring(3))!=null)
								{							    	
					                String mitiScoreMergeSelfEval = (String) htxml.get("mitiScoreMergeSelfEval"); // 显示自我评价
									//String SelfEvalNotScore=(String)htxml.get("SelfEvalNotScore");  // 自我评价界面不显示打分模板
							    	
									if (("true".equalsIgnoreCase(mitiScoreMergeSelfEval))/* && (SelfEvalNotScore.equalsIgnoreCase("true"))*/)
									{
										if("1".equalsIgnoreCase(mailTogoLink) && "1".equalsIgnoreCase(markingMode))
										{
											content.append("<br><br><a target='_blank' href='"+this.userView.getServerurl()+"/selfservice/performance/batchGrade.do?b_query=link&linkType=1&model=0&modelEmail=0&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password))+"'>"+plan_vo.getString("name")+"_(评分)"+"</a>");																								
											contentUrl=this.userView.getServerurl()+"/selfservice/performance/batchGrade.do?b_query=link&linkType=1&model=0&modelEmail=0&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password))+"'>";
										}else if("1".equalsIgnoreCase(mailTogoLink) && "2".equalsIgnoreCase(markingMode))
										{
											content.append("<br><br><a target='_blank' href='"+this.userView.getServerurl()+"/selfservice/performance/batchGrade.do?b_tileFrame=link&model=0&operate=aaa" + plan_id + "&modelEmail=0&linkType=1&planContext=all&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password))+"'>"+plan_vo.getString("name")+"_(评分)"+"</a>");																																
											contentUrl=this.userView.getServerurl()+"/selfservice/performance/batchGrade.do?b_tileFrame=link&model=0&operate=aaa" + plan_id + "&modelEmail=0&linkType=1&planContext=all&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password));
										}else if("2".equalsIgnoreCase(mailTogoLink))
										{
											content.append("<br><br><a target='_blank' href='"+this.userView.getServerurl()+"/selfservice/performance/singleGrade.do?b_query=link&fromModel=frontPanel&model=0&to_plan_id=" + plan_id + "&bint=int&model=0&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password))+"'>"+plan_vo.getString("name")+"_(评分)"+"</a>");		
											contentUrl=this.userView.getServerurl()+"/selfservice/performance/singleGrade.do?b_query=link&fromModel=frontPanel&model=0&to_plan_id=" + plan_id + "&bint=int&model=0&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password));
										}										
									}else
									{
										content.append("<br><br><a target='_blank' href='"+this.userView.getServerurl()+"/selfservice/performance/selfGrade.do?b_query=link&bint=int&model=0&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password))+"'>"+plan_vo.getString("name")+"_(评分)"+"</a>");																															
										contentUrl=this.userView.getServerurl()+"/selfservice/performance/selfGrade.do?b_query=link&bint=int&model=0&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password));
									}				    	
									
								}else if("1".equalsIgnoreCase(mailTogoLink) && "1".equalsIgnoreCase(markingMode))
								{
									content.append("<br><br><a target='_blank' href='"+this.userView.getServerurl()+"/selfservice/performance/batchGrade.do?b_query=link&linkType=1&model=0&modelEmail=0&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password))+"'>"+plan_vo.getString("name")+"_(评分)"+"</a>");																								
									contentUrl=this.userView.getServerurl()+"/selfservice/performance/batchGrade.do?b_query=link&linkType=1&model=0&modelEmail=0&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password));
								}else if("1".equalsIgnoreCase(mailTogoLink) && "2".equalsIgnoreCase(markingMode))
								{
									content.append("<br><br><a target='_blank' href='"+this.userView.getServerurl()+"/selfservice/performance/batchGrade.do?b_tileFrame=link&model=0&operate=aaa" + plan_id + "&modelEmail=0&linkType=1&planContext=all&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password))+"'>"+plan_vo.getString("name")+"_(评分)"+"</a>");																																	
									contentUrl=this.userView.getServerurl()+"/selfservice/performance/batchGrade.do?b_tileFrame=link&model=0&operate=aaa" + plan_id + "&modelEmail=0&linkType=1&planContext=all&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password));
								}else if("2".equalsIgnoreCase(mailTogoLink))
								{
									content.append("<br><br><a target='_blank' href='"+this.userView.getServerurl()+"/selfservice/performance/singleGrade.do?b_query=link&fromModel=frontPanel&model=0&to_plan_id=" + plan_id + "&bint=int&model=0&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password))+"'>"+plan_vo.getString("name")+"_(评分)"+"</a>");		
									contentUrl=this.userView.getServerurl()+"/selfservice/performance/singleGrade.do?b_query=link&fromModel=frontPanel&model=0&to_plan_id=" + plan_id + "&bint=int&model=0&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password));
								}	 
								if("1".equals(performanceType)){//民主测评，链接写死  zhaoxg add 2014-12-18
									content.setLength(0);	
									content.append(emailContent);
									String mitiScoreMergeSelfEval = (String) htxml.get("mitiScoreMergeSelfEval"); // 显示自我评价
									//String SelfEvalNotScore=(String)htxml.get("SelfEvalNotScore");  // 自我评价界面不显示打分模板
									if (("true".equalsIgnoreCase(mitiScoreMergeSelfEval))/* && (SelfEvalNotScore.equalsIgnoreCase("true"))*/){
										if("2".equals(object_type)){//人员就走班子成员测评，非人员就走领导班子测评   zhaoxg add
											content.append("<br><br><a target='_blank' href='"+this.userView.getServerurl()+"/selfservice/performance/singleGrade.do?b_query=link&model=1&fromModel=menu&optObject=2&to_plan_id=" + plan_id + "&bint=int&model=0&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password))+"'>"+plan_vo.getString("name")+"_(评分)"+"</a>");	
											contentUrl=this.userView.getServerurl()+"/selfservice/performance/singleGrade.do?b_query=link&model=1&fromModel=menu&optObject=2&to_plan_id=" + plan_id + "&bint=int&model=0&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password));
										}else{
											content.append("<br><br><a target='_blank' href='"+this.userView.getServerurl()+"/selfservice/performance/singleGrade.do?b_query=link&model=1&fromModel=menu&optObject=1&to_plan_id=" + plan_id + "&bint=int&model=0&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password))+"'>"+plan_vo.getString("name")+"_(评分)"+"</a>");
											contentUrl=this.userView.getServerurl()+"/selfservice/performance/singleGrade.do?b_query=link&model=1&fromModel=menu&optObject=1&to_plan_id=" + plan_id + "&bint=int&model=0&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password));
										}
									}else{
										content.append("<br><br><a target='_blank' href='"+this.userView.getServerurl()+"/selfservice/performance/selfGrade.do?b_query=link&bint=int&model=1&plan_id_db="+plan_id+"&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password))+"'>"+plan_vo.getString("name")+"_(评分)"+"</a>");																															
										contentUrl=this.userView.getServerurl()+"/selfservice/performance/selfGrade.do?b_query=link&bint=int&model=1&plan_id_db="+plan_id+"&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password));
									}
								}
							    
						    }else if(("2".equals(method)) && ("distribute".equalsIgnoreCase(oper)) && ("2".equals(object_type))) // 目标计划
							{
								content.append("<br><br><a target='_blank' href='"+this.userView.getServerurl()+"/performance/objectiveManage/objectiveCard.do?b_query=query&zglt=0&returnflag=menu&entranceType=0&body_id=5&model=2&opt=1&planid=" + PubFunc.encryption(plan_id) + "&object_id=" + PubFunc.encryption(preaObject_id) + "&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password))+"'>"+plan_vo.getString("name")+"_(设定)"+"</a>");																								
								contentUrl=this.userView.getServerurl()+"/performance/objectiveManage/objectiveCard.do?b_query=query&zglt=0&returnflag=menu&entranceType=0&body_id=5&model=2&opt=1&planid=" + PubFunc.encryption(plan_id) + "&object_id=" + PubFunc.encryption(preaObject_id) + "&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password));
							}else if(("2".equals(method)) && ("distribute".equalsIgnoreCase(oper)) && (!"2".equals(object_type)))
							{
								content.append("<br><br><a target='_blank' href='"+this.userView.getServerurl()+"/performance/objectiveManage/objectiveCard.do?b_query=query&zglt=0&returnflag=menu&entranceType=0&body_id=5&model=1&opt=1&planid=" + PubFunc.encryption(plan_id) + "&object_id=" + PubFunc.encryption(preaObject_id) + "&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password))+"'>"+plan_vo.getString("name")+"_(设定)"+"</a>");																								
								contentUrl=this.userView.getServerurl()+"/performance/objectiveManage/objectiveCard.do?b_query=query&zglt=0&returnflag=menu&entranceType=0&body_id=5&model=1&opt=1&planid=" + PubFunc.encryption(plan_id) + "&object_id=" + PubFunc.encryption(preaObject_id) + "&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password));
							}else if(("2".equals(method)) && ("2".equalsIgnoreCase(mailTogoLink)) && ("start".equalsIgnoreCase(oper)) && ("2".equals(object_type)))
							{
								// 把展现单个目标卡的链接换成展现计划打分列表的链接
								content.append("<br><br><a target='_blank' href='"+this.userView.getServerurl()+"/performance/objectiveManage/objectiveCard.do?b_query=query&zglt=0&returnflag=menu&entranceType=0&body_id=" + body_id + "&model=4&opt=2&planid=" + PubFunc.encryption(plan_id) + "&object_id=" + PubFunc.encryption(preaObject_id) + "&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password))+"'>"+plan_vo.getString("name")+"_(评分)"+"</a>");																								
								contentUrl=this.userView.getServerurl()+"/performance/objectiveManage/objectiveCard.do?b_query=query&zglt=0&returnflag=menu&entranceType=0&body_id=" + body_id + "&model=4&opt=2&planid=" + PubFunc.encryption(plan_id) + "&object_id=" + PubFunc.encryption(preaObject_id) + "&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password));
								//	content.append("<br><br><a href='"+this.userView.getServerurl()+"/performance/objectiveManage/objectiveEvaluate/objective_evaluate_list.do?b_init=init&planid="+plan_id+"&opt=4&entranceType=0&isSort=1&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password))+"'>"+plan_vo.getString("name")+"_(评分)"+"</a>");											

							}else if(("2".equals(method)) && ("2".equalsIgnoreCase(mailTogoLink)) && ("start".equalsIgnoreCase(oper)) && (!"2".equals(object_type)))
							{
								// 把展现单个目标卡的链接换成展现计划打分列表的链接
								content.append("<br><br><a target='_blank' href='"+this.userView.getServerurl()+"/performance/objectiveManage/objectiveCard.do?b_query=query&zglt=0&returnflag=menu&entranceType=0&body_id=" + body_id + "&model=1&opt=2&planid=" + PubFunc.encryption(plan_id) + "&object_id=" + PubFunc.encryption(preaObject_id) + "&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password))+"'>"+plan_vo.getString("name")+"_(评分)"+"</a>");																								
								contentUrl=this.userView.getServerurl()+"/performance/objectiveManage/objectiveCard.do?b_query=query&zglt=0&returnflag=menu&entranceType=0&body_id=" + body_id + "&model=1&opt=2&planid=" + PubFunc.encryption(plan_id) + "&object_id=" + PubFunc.encryption(preaObject_id) + "&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password));
								//	content.append("<br><br><a href='"+this.userView.getServerurl()+"/performance/objectiveManage/objectiveEvaluate/objective_evaluate_list.do?b_init=init&planid="+plan_id+"&opt=4&entranceType=0&isSort=1&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password))+"'>"+plan_vo.getString("name")+"_(评分)"+"</a>");
							}else if(("2".equals(method)) && ("1".equalsIgnoreCase(mailTogoLink)) && ("start".equalsIgnoreCase(oper)) && ("2".equals(object_type)))
							{
								content.append("<br><br><a target='_blank' href='"+this.userView.getServerurl()+"/performance/objectiveManage/objectiveEvaluate/objective_evaluate_list.do?b_init=init&planid="+plan_id+"&opt=4&entranceType=0&isSort=0&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password))+"'>"+plan_vo.getString("name")+"_(评分)"+"</a>");																								
								contentUrl=this.userView.getServerurl()+"/performance/objectiveManage/objectiveEvaluate/objective_evaluate_list.do?b_init=init&planid="+plan_id+"&opt=4&entranceType=0&isSort=0&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password));
							}else if(("2".equals(method)) && ("1".equalsIgnoreCase(mailTogoLink)) && ("start".equalsIgnoreCase(oper)) && (!"2".equals(object_type)))
							{
								content.append("<br><br><a target='_blank' href='"+this.userView.getServerurl()+"/performance/objectiveManage/objectiveEvaluate/objective_evaluate_list.do?b_init=init&planid="+plan_id+"&opt=4&entranceType=0&isSort=0&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password))+"'>"+plan_vo.getString("name")+"_(评分)"+"</a>");																								
								contentUrl=this.userView.getServerurl()+"/performance/objectiveManage/objectiveEvaluate/objective_evaluate_list.do?b_init=init&planid="+plan_id+"&opt=4&entranceType=0&isSort=0&appfwd=1&etoken="+PubFunc.convertUrlSpecialCharacter(PubFunc.convertTo64Base(username+","+password));
							}
							
							String toAddr = this.getEmailValue(emailfield, emailfieldset, prea0100.substring(3), templateId, dao);
							
							if (toAddr == null || (toAddr != null && ("".equals(toAddr.trim()) || "无".equals(toAddr.trim())))){
								somepeople.append(a0101+"    :    "+"该考核对象的邮箱地址为空!"+"\r\n");
								isnull="yes";
								continue;
							}
							boolean b=emailFormat(toAddr);
							if(b==false){
								isnull="yes";
								somepeople.append(a0101+"  :  "+"该考核对象的邮箱地址格式不正确!"+"\r\n");	
								continue;
							}
					
							// 不进行邮件异常校验
							try
							{
								emailbo.sendEmail(subject, content.toString(), "", fromAddr, toAddr);
								
								String corpid = (String) ConstantParamter.getAttribute("wx","corpid");  
								if(corpid!=null&&corpid.length()>0){//推送微信公众号  zhaoxg add 2015-5-5
									WeiXinBo.sendMsgToPerson("Usr", prea0100.substring(3), subject, content.toString(), "http://www.hjsoft.com.cn:8089/UserFiles/Image/tongzhi.png", contentUrl);
								}
								String ddcorpid = (String) ConstantParamter.getAttribute("DINGTALK","corpid");  
								if(ddcorpid!=null&&ddcorpid.length()>0){//推送钉钉公众号  xus add 2017-6-2
									DTalkBo.sendMessage( prea0100.substring(3),"Usr", subject, content.toString(), "http://www.hjsoft.com.cn:8089/UserFiles/Image/tongzhi.png", contentUrl);
								}
							} catch (Exception e)
							{   
								e.printStackTrace();
							}
					    }
				    }		    
				}
			}
			this.getFormHM().put("plan_ids", plan_ids);
			contentname=DOWriteTxt(somepeople.toString());
			contentname = PubFunc.encrypt(contentname);
			
		} catch (Exception e)
		{
		    e.printStackTrace();
		    this.getFormHM().put("resultFlag", "0");
		    throw GeneralExceptionHandler.Handle(e);
		}
		this.getFormHM().put("resultFlag", "1");
		this.getFormHM().put("names", contentname);
		this.getFormHM().put("isnull", isnull);
    }

    public String getEmailValue(String emailField, String emailFieldSet, String a0100, String templateId, ContentDAO dao) throws SQLException
    {

		String emailValue = "";
		StringBuffer sql = new StringBuffer();
		if ("a01".equalsIgnoreCase(emailFieldSet))
		{
		    sql.append("select a." + emailField + " address from Usr" + emailFieldSet + " a where a.a0100='" + a0100 + "'");
		} else
		{
		    sql.append("select a." + emailField + " address from Usr" + emailFieldSet + " a where a.a0100=" + a0100 + " and i9999=(select max(i9999) i9999 from Usr" + emailFieldSet
			    + " b where b.a0100='" + a0100 + "')");
		}
		RowSet rs = dao.search(sql.toString());
		if (rs.next())
		    emailValue = rs.getString(1);
		
		if(rs!=null)
			rs.close();			
		return emailValue;
    }
    /**  
     *  获得用户名和密码
     */
    public LazyDynaBean getUserNamePassword(String value)
	{
		if(value==null||value.length()<=0)
		{
			return null;
		}
		String nbase=value.substring(0,3);
		String a0100=value.substring(3);
		AttestationUtils utils=new AttestationUtils();
		LazyDynaBean fieldbean=utils.getUserNamePassField();
		String username_field=(String)fieldbean.get("name");
	    String password_field=(String)fieldbean.get("pass");
	    
	    StringBuffer sql=new StringBuffer();
	    sql.append("select a0101,"+username_field+" username,"+password_field+" password,a0101 from "+nbase+"A01");
	    sql.append(" where a0100='"+a0100+"'");
	    List rs=ExecuteSQL.executeMyQuery(sql.toString());
	    
	    LazyDynaBean rec=null;
	    if(rs!=null&&rs.size()>0)
	    {
	    	rec=(LazyDynaBean)rs.get(0);	    	
	    }
	    return rec;
	}         	
	
	/**
	 * 取得实际发送内容
	 * @param content
	 * @param prea0100
	 * @param fieldList
	 * @return
	 */
	public String getFactContent(String content,String prea0100,ArrayList fieldList,UserView uv,RecordVo plan_vo,String objectName)
	{
		String fact_content=content;
		try
		{
			fact_content=fact_content.replaceAll("#发件人名称#", this.userView.getUserName());			
			fact_content=fact_content.replaceAll("#审批人名称#", "");
			fact_content=fact_content.replaceAll("#目标对象名称#", objectName);
			fact_content=fact_content.replaceAll("#收件人名称#",objectName); 					
			fact_content=fact_content.replaceAll("#考核计划名称#",plan_vo.getString("name"));
			fact_content=fact_content.replaceAll("#系统日期#",PubFunc.getStringDate("yyyy-MM-dd HH:mm:ss"));
            fact_content=fact_content.replaceAll("\\$sys:考核计划名称\\$",plan_vo.getString("name"));//替换考核计划名称
			EmailTemplateBo bo = new EmailTemplateBo(this.getFrameconn());
			String pre=prea0100.substring(0,3);
			String a0100=prea0100.substring(3);
			StringBuffer buf = new StringBuffer();
			StringBuffer table_name=new StringBuffer();
			HashSet name_set = new HashSet();
			StringBuffer where_sql=new StringBuffer();
			StringBuffer where_sql2= new StringBuffer();
			ContentDAO dao = new ContentDAO(this.frameconn);
			RowSet rs = null;
			for(int i=0;i<fieldList.size();i++)
			{
				LazyDynaBean bean = (LazyDynaBean)fieldList.get(i);
				String id=(String)bean.get("id");
				String fieldtitle=(String)bean.get("fieldtitle");
				String fieldtype=((String)bean.get("fieldtype")).trim();
				String fieldcontent=(String)bean.get("fieldcontent");
				String fieldid=(String)bean.get("fieldid");
				String dateformat=(String)bean.get("dateformat");
				String fieldlen=(String)bean.get("fieldlen");
				String ndec=(String)bean.get("ndec");
				String codeset=(String)(bean.get("codeset")==null?"":bean.get("codeset"));
				String nflag=(String)bean.get("nflag");
				String replace="";//要被替换的内容
				String factcontent="";
				String setid="";
				if("0".equals(nflag))
				{
					replace="\\$"+fieldid+":"+fieldtitle.trim()+"\\$";
				}
				if("1".equals(nflag))
				{
					replace="\\#"+fieldid+":"+fieldtitle.trim()+"\\#";
				}
				/**指标和公式项目处理不同*/
				if("0".equals(nflag))//指标
				{
					String fieldsetid="";
					if("A0101".equalsIgnoreCase(fieldcontent.trim())|| "B0110".equalsIgnoreCase(fieldcontent.trim())|| "E0122".equalsIgnoreCase(fieldcontent.trim())|| "e01a1".equalsIgnoreCase(fieldcontent.trim()))
						fieldsetid="a01";
					else
			    		fieldsetid=bo.getFieldSetId(fieldcontent.trim());
					if(fieldsetid==null||fieldsetid.length()<=0)
						continue;
					buf.append("select ");
					buf.append(fieldcontent);
					buf.append(" from ");
					buf.append(pre+fieldsetid);
					buf.append(" where ");
					/*if(isConf(fieldsetid))//是否用判断年月标识
					{
						buf.append("");
					}
					else
					{*/
						buf.append(pre+fieldsetid+".a0100='");
						buf.append(a0100+"'");
						if("a01".equalsIgnoreCase(fieldsetid))
						{
							
						}		
						else
						{
							buf.append(" and ");
						    buf.append(pre+fieldsetid+".i9999=(select max(i9999) from ");
						    buf.append(pre+fieldsetid);
						    buf.append(" where ");
						    buf.append(pre+fieldsetid+".a0100='");
						    buf.append(a0100+"')");
						}						
						rs=dao.search(buf.toString());
						while(rs.next())
						{			
							//  JinChunhai 2011.05.19  orcle库遇到日期型的字段后台会报错
							if(codeset!=null&&!"0".equalsIgnoreCase(codeset)&& "A".equalsIgnoreCase(fieldtype))  // 代码型
							{
								factcontent=rs.getString(fieldcontent.trim());
								
							}else if("N".equalsIgnoreCase(fieldtype.trim()))  // 数值型按格式显示
							{
								factcontent=rs.getString(fieldcontent.trim());
								
							}else if("D".equalsIgnoreCase(fieldtype.trim()))  // 日期型按格式显示
							{
								java.sql.Date dd=rs.getDate(fieldcontent.trim());
								if (dd != null) {
									SimpleDateFormat format=new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
									factcontent=format.format(dd);
								} else {
									factcontent = "";
								}
								
							}else 
							{
								factcontent=rs.getString(fieldcontent.trim());
							}
							
//							factcontent=rs.getString(fieldcontent.trim());
						}
						
						if(codeset!=null&&!"0".equalsIgnoreCase(codeset)&& "A".equalsIgnoreCase(fieldtype))//代码型
						{
							factcontent=AdminCode.getCodeName(codeset,factcontent);
						}
						
						/**日期型按格式显示*/
						if("D".equalsIgnoreCase(fieldtype.trim())&&!(dateformat==null|| "".equals(dateformat)))
							factcontent=bo.getYMDFormat(Integer.parseInt(dateformat),factcontent);
						/**数值型按格式显示*/
						if("N".equalsIgnoreCase(fieldtype.trim()))
							factcontent=bo.getNumberFormat(Integer.parseInt(fieldlen),Integer.parseInt(ndec),factcontent);
						if(factcontent==null||factcontent.trim().length()==0)
			    		{
			    			if("N".equalsIgnoreCase(fieldtype.trim()))
			    				factcontent="0.0";
			    			else
			    				factcontent=" ";
			    		}
						fact_content=fact_content.replaceAll(replace,factcontent);
					    buf.setLength(0);
						table_name.setLength(0);
						where_sql.setLength(0);
						where_sql2.setLength(0);
					
				}
				if("1".equals(nflag))//公式
				{
					String zh_sql=bo.getSql(fieldcontent,fieldtype,uv);
					if(zh_sql==null||zh_sql.length()<=0)
						continue;
					if("D".equalsIgnoreCase(fieldtype.trim()))//日期型是否直接查
					{
					    setid=bo.getFieldSetId(zh_sql.trim());
						buf.append("select ");
						buf.append(zh_sql);
						buf.append(" from ");
						if(setid!=null&&!"".equals(setid))
							buf.append(pre+setid);
						else
							buf.append(" usra01 ");
						buf.append(" where a0100='");
						buf.append(a0100+"'");
					}
					else
					{
			    		String[] temp_arr=zh_sql.split(",");
				    	if(temp_arr.length>1)
			    		{
				    		buf.append("select ");
				    		buf.append(zh_sql);
				    		buf.append(" from ");
			    			for(int j=0;j<temp_arr.length-1;j++)
			    			{
					    		int index=temp_arr[j].lastIndexOf("(")+1;
					    		String itemid=temp_arr[j].substring(index,temp_arr[j].length());
					    		String fieldsetid ="";
					    		if("B0110".equalsIgnoreCase(itemid)|| "E0122".equalsIgnoreCase(itemid)|| "e01a1".equalsIgnoreCase(itemid))
					    			fieldsetid="a01";
					    		else
					    	    	fieldsetid=bo.getFieldSetId(itemid);//item.getFieldsetid();
					    		String tableName=pre+fieldsetid;
					    		name_set.add(tableName);
					    		if(j!=0)
						    		where_sql2.append(" and ");
					     		where_sql2.append(tableName+".a0100='");
					      		where_sql2.append(a0100+"'");
							//
					    		if("a01".equalsIgnoreCase(fieldsetid))
					    		{
					    		}
					    		else
					    		{
					    			if(j!=0)
					    				where_sql.append(" and ");
				    	    		where_sql.append(tableName);
					        		where_sql.append(".i9999=(");
				         			where_sql.append("select max(i9999) from ");
					        		where_sql.append(tableName+" "+tableName);
					        		where_sql.append(" where ");
					        		where_sql.append(tableName+".a0100='");
					        		where_sql.append(a0100+"') ");
					    		}
					    	}
				    		buf.append(name_set.toString().substring(1,name_set.toString().length()-1));
				    		buf.append(" where ");
				    		buf.append(where_sql);
					    	if(where_sql.length()>0)
					    	      buf.append(" and ");
			    	    	buf.append(where_sql2);
			    		}
			    		else
			    		{
				    		buf.append(zh_sql);
				    	} 	
			    	}					
					rs=dao.search(buf.toString());
		    		while(rs.next())
		    		{
		    			if("D".equalsIgnoreCase(fieldtype))
		    			{
		    			/*
		    				if(setid==null || setid.trim().length()<=0 || setid.equals(""))
		    				{
		    	    			if(rs.getString(1)!=null)
		    	    			{
		    			             factcontent=rs.getString(1);
		    			    	}else
		    			    	{
		    			    		factcontent="";
		    		    		}
		    				}else */
		    				{
		    					if(rs.getDate(1)!=null)
		    					{
		    						java.sql.Date dd=rs.getDate(1);
			    			        SimpleDateFormat format=new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
			    			        factcontent=format.format(dd);
		    					}
		    					else
		    					{
		    						factcontent="";
		    					}
		    				}
		    			}
		    			else
	    	    			factcontent=rs.getString(1);
		    		}
		    		
		    		/**日期型按格式显示*/
					if("D".equalsIgnoreCase(fieldtype.trim())&&!(dateformat==null|| "".equals(dateformat)))
						factcontent=bo.getYMDFormat(Integer.parseInt(dateformat),factcontent);
					/**数值型按格式显示*/
					if("N".equalsIgnoreCase(fieldtype.trim()))
						factcontent=bo.getNumberFormat(Integer.parseInt(fieldlen),Integer.parseInt(ndec),factcontent);
					if(factcontent==null||factcontent.trim().length()==0)
		    		{
		    			if("N".equalsIgnoreCase(fieldtype.trim()))
		    				factcontent="0.0";
		    			else
		    				factcontent=" ";
		    		}
				    fact_content=fact_content.replaceAll(replace,factcontent);
			    	buf.setLength(0);
			    	table_name.setLength(0);
			    	name_set.clear();
		    		where_sql.setLength(0);
		    		where_sql2.setLength(0);
				}
			
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return fact_content;
	}

	//该方法用于向txt文件里写人内容
	 public String DOWriteTxt(String txt) {
		 String file=this.userView.getUserName()+(Math.random()*100)+"_export.txt";
		 FileOutputStream os  =null;
		  try {
		   os = new FileOutputStream(new File(System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+file));
		   os.write((txt + "\n").getBytes());
		  } catch (Exception e) {
		   e.printStackTrace();
		  }finally{
			  PubFunc.closeIoResource(os);
		  }
		  return file;
	}
	 
	 //改方法用于判断邮箱地址是否正确
	 public static boolean emailFormat(String email)  {  
		 boolean tag = true;      
			final String pattern1= "\\b(^['_A-Za-z0-9-]+(\\.['_A-Za-z0-9-]+)*@([A-Za-z0-9-])+(\\.[A-Za-z0-9-]+)*((\\.[A-Za-z0-9]{2,})|(\\.[A-Za-z0-9]{2,}\\.[A-Za-z0-9]{2,}))$)\\b";
			if(email!=null){
				final Pattern pattern = Pattern.compile(pattern1);     
				final Matcher mat = pattern.matcher(email);     
				if (!mat.find()) {          
					tag = false;    
				}     
			}
			else{
				tag = false;    
			}
			return tag;  
	}

	
}
