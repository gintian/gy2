package com.hjsj.hrms.transaction.performance.implement;

import com.hjsj.hrms.businessobject.attestation.AttestationUtils;
import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.util.*;

/**
 *<p>Title:SendWaitTaskTrans.java</p> 
 *<p>Description:分发和启动计划发待办任务</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2011-06-09 10:15:35</p> 
 *@author JinChunhai
 *@version 5.0
 */

public class SendWaitTaskTrans extends IBusiness
{
	private RecordVo plan_vo=null;  // 某考核计划的信息vo	
	   
	public void execute() throws GeneralException
    {				
		try
		{			 
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String plan_ids = (String) this.getFormHM().get("plan_id");
			String oper = (String) this.getFormHM().get("oper");// start启动 || distribute分发	
			String logoSign = (String)this.getFormHM().get("signLogo"); // 批量分发或启动的标志
			String startFlag = null;
			if(this.getFormHM().get("startFlag") != null){//是否手动录分
				startFlag = (String)this.getFormHM().get("startFlag");
			}
			String[] planIds = null;
			if(logoSign!=null && logoSign.trim().length()>0 && "batchDisOrStart".equalsIgnoreCase(logoSign))
			{
				if(plan_ids==null || plan_ids.trim().length()<=0)
					return;				 
				planIds = plan_ids.replaceAll("／", "/").split("/");
			}else
			{
				planIds = new String[1];
				planIds[0] = plan_ids;
			}		
			
//			String pending_system = SystemConfig.getPropertyValue("pending_system"); // 待办系统
//			if(pending_system!=null && pending_system.trim().length()>0)
//			{	 
	
	
				//情况一： 360计划 启动时候 考核对象类别为人员和部门都发送待办任务给考核主体
			    //情况二： 目标计划 分发和启动时候 考核对象类别为人员时给考核对象发待办任务，考核对象类别为团队时给团队负责人发待办任务
					
					PerformanceImplementBo pb = new PerformanceImplementBo(this.getFrameconn());													
					if(planIds!=null && planIds.length>0)
					{	
						String markingMode = "1";  // 计划表现方式： 1 计划下拉框 2 平铺计划
						this.frowset = dao.search("select str_value from constant where constant='PER_PARAMETERS'");
					    if ( this.frowset.next())
					    {
					    	String str_value = this.frowset.getString("str_value");
					    	if (str_value == null || (str_value != null && "".equals(str_value)))
					    	{
				
					    	} else
					    	{
					    		Document doc =  PubFunc.generateDom(str_value);
					    		String xpath = "//Per_Parameters";
					    		XPath xpath_ = XPath.newInstance(xpath);
					    		Element ele = (Element) xpath_.selectSingleNode(doc);
					    		Element child;
					    		if (ele != null)
					    		{
					    			child = ele.getChild("Plan");
									if (child != null)
									    markingMode = child.getAttributeValue("MarkingMode");						    		    			
					    		}
					    	}
					    }
					    PendingTask pe = new PendingTask();
						for (int k = 0; k < planIds.length; k++)
						{
							String plan_id = planIds[k];
                            if("start".equalsIgnoreCase(oper) && !StringUtils.isEmpty(startFlag) && "result".equalsIgnoreCase(startFlag)){//手工录分时启动不发待办
                                //如果是启动录入结果，判断如果有打分待办则删除掉。
                                RowSet rs = null;
                                try {
                                    ArrayList values = new ArrayList();
                                    String extFlag_ = "PERPF_"+plan_id+"%";
                                    values.add(extFlag_);
                                    String str = "select pending_id from t_hr_pendingtask  where Pending_type='33' and ext_flag like ?";
                                    rs = dao.search(str,values);
                                    while(rs.next()){
                                        pe.updatePending("P", "PER"+rs.getString("pending_id"), 100, "清除打分待办", this.userView);
                                    }
                                    String delSql = "delete from t_hr_pendingtask where pending_type='33' and ext_flag like ?";
                                    dao.delete(delSql,values);
                                    continue;
                                }catch (Exception e){
                                    e.printStackTrace();
                                }finally {
                                    PubFunc.closeDbObj(rs);
                                }

                            }
							//找出启动计划时各任务指定的评分人如不为某对象目标卡的评分人，系统自动将其添加成一级考核主体类别下的考核主体
							if("start".equals(oper))
								addMainBody(plan_id,dao);	
							
							// 获得需要的计划参数
							LoadXml loadXml=null; //new LoadXml();
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
                            HashMap<String,String> objectIdMap=new HashMap<String, String>();
                            if(!"true".equalsIgnoreCase((String) htxml.get("mitiScoreMergeSelfEval")))//如果不显示自我评价 则获取所有考核对象id
                                objectIdMap=this.getObjectIdFromPlanId(plan_id);

			                // 先从页面获取(目标卡未审批时启动会提示勾选此选项)，没有值才选择计划参数
			                String noApproveTargetCanScore = (String) formHM.get("noApproveTargetCanScore");// 目标卡未审批也允许打分 True, False, 默认为 False
			                if (noApproveTargetCanScore == null || "".equals(noApproveTargetCanScore)) {
			                	noApproveTargetCanScore = (String)htxml.get("NoApproveTargetCanScore");
			                }
			                String GradeByBodySeq=(String)htxml.get("GradeByBodySeq"); //按考核主体顺序号控制评分流程(True, False默认为False)	
			                String performanceType = (String) htxml.get("performanceType");	//考核形式（0：绩效考核  1：民主评测）		                			                
							ArrayList<String> emailReceivorList = new ArrayList<String>(); // 待办任务接收人列表
							this.plan_vo = getPlanVo(plan_id);
							String method = String.valueOf(this.plan_vo.getInt("method"));
							String object_type = String.valueOf(this.plan_vo.getInt("object_type"));   //1部门 2：人员
							if("3".equals(object_type) || "4".equals(object_type)){
								object_type = "1";
							}
	
							String sql = "";
							if("True".equalsIgnoreCase(GradeByBodySeq) && "start".equalsIgnoreCase(oper) && "2".equalsIgnoreCase(method))  //按考核主体顺序号控制评分流程
							{
				    			String Ext_flag="PERZD_"+plan_id;
				    			String str = "select * from t_hr_pendingtask  where Pending_type='33' and  pending_status<>1 and ext_flag like '"+Ext_flag+"%'";
				    			RowSet rs = dao.search(str);
				    			while(rs.next()){
				    				pe.updatePending("P", "PER"+rs.getString("pending_id"), 1, "计划分发", this.userView);
				    			}
								String sql1="update t_hr_pendingtask set pending_status=1 where Pending_type='33' and  pending_status<>1 and ext_flag like '"+Ext_flag+"%'";
								dao.update(sql1);
								
								String temp="";
					    		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
					    			temp=" pms.level_o";
								else
									temp=" pms.level ";
								if(noApproveTargetCanScore!=null && "false".equalsIgnoreCase(noApproveTargetCanScore))
						    		sql ="select pm.mainbody_id,pm.object_id,"+temp+" from per_mainbody pm,per_object po,per_mainbodyset pms,per_plan_body ppb where pm.body_id=pms.body_id and pm.object_id=po.object_id and pm.plan_id=po.plan_id and pm.plan_id=" + plan_id + " and pm.plan_id=ppb.plan_id and pm.body_id=ppb.body_id and ("+Sql_switcher.isnull("ppb.isgrade", "0")+" <>'1') and ("+ Sql_switcher.isnull("pm.STATUS", "0") +"=0 or pm.status=0 or pm.status='1') and (po.sp_flag='03') ";
						    	else		    			
						    		sql ="select mainbody_id,object_id,"+temp+" from per_mainbody pm,per_mainbodyset pms,per_plan_body ppb where pm.body_id=pms.body_id and pm.plan_id=" + plan_id + " and pm.plan_id=ppb.plan_id and pm.body_id=ppb.body_id and ("+Sql_switcher.isnull("ppb.isgrade", "0")+" <>'1') and ("+ Sql_switcher.isnull("pm.STATUS", "0") +"=0 or pm.status=0 or pm.status='1') ";		    			    		
								sql+=" and nullif(pm.seq,0) is not null and pm.seq=(select min(seq) from per_mainbody a where plan_id="+plan_id+" and a.object_id=pm.object_id and nullif(seq,0) is not null group by object_id  ) " ;
																
								this.frowset = dao.search(sql);
							    while (this.frowset.next())	
								{								    	
							    	emailReceivorList.add("Usr" +  this.frowset.getString(1) + "~" + this.frowset.getString(2) + "`" + this.frowset.getString(3));	 		    										  			
								}										
							}
							else
							{								
							    //	  情况一： 360计划 启动时候 考核对象类别为人员和部门都发送邮件给考核主体
							    if("1".equals(method) && "start".equalsIgnoreCase(oper)) {
							    	sql ="select distinct mainbody_id from per_mainbody where plan_id=" + plan_id + " and ("+ Sql_switcher.isnull("STATUS", "0") +"=0 or status=0 or status='1') ";
							    	String SelfEvalNotScore=(String)htxml.get("SelfEvalNotScore");
							    	String SummaryFlag=(String)htxml.get("SummaryFlag");
							    	// 自我评价界面不显示打分模板时，并且没有显示绩效报告时，不给本人发代办
							    	if("true".equalsIgnoreCase(SelfEvalNotScore) && "false".equalsIgnoreCase(SummaryFlag)){
							    		sql+="and mainbody_id<>object_id ";
							        }
							    }
							    else if("2".equals(method) && ("start".equalsIgnoreCase(oper) || "distribute".equalsIgnoreCase(oper)))
							    {
							        //情况二： 目标计划 分发和启动时候 考核对象类别为人员时给考核对象发邮件，考核对象类别为团队给团队负责人发邮件
							    	if("2".equals(object_type))//1部门 2：人员
							    	{
							    		pb.creatTempTable(plan_id); // 新建临时表并进行操作
							    		if("start".equalsIgnoreCase(oper)){//如果是启动状态，判断是否有分发时产生的待办，如果有，就更新成已办。防止分发之后直接启动chent 2015.08.06
											String upSql = "update t#_per_Email set sp_flag='03' where plan_id="+plan_id;
											dao.update(upSql);
							    			String Ext_flag="PERZD_"+plan_id;
							    			String str = "select * from t_hr_pendingtask  where Pending_type='33' and  pending_status<>1 and ext_flag like '"+Ext_flag+"%'";
							    			RowSet rs = dao.search(str);
							    			while(rs.next()){
							    				pe.updatePending("P", "PER"+rs.getString("pending_id"), 1, "计划分发", this.userView);
							    			}
											String sql1="update t_hr_pendingtask set pending_status=1 where Pending_type='33' and  pending_status<>1 and ext_flag like '"+Ext_flag+"%'";
											dao.update(sql1);
										}
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
								    		sql ="select pm.mainbody_id,pm.object_id from per_mainbody pm,per_object po where pm.object_id=po.object_id and pm.plan_id=po.plan_id and pm.plan_id=" + plan_id + " and ("+ Sql_switcher.isnull("pm.STATUS", "0") +"=0 or pm.status=0) and (po.sp_flag is null or po.sp_flag='01') and pm.body_id=-1";
								    	}else
							    		{
								    		String Ext_flag="PERZD_"+plan_id;
											String sql1="update t_hr_pendingtask set pending_status=1 where Pending_type='33' and  pending_status<>1 and ext_flag like '"+Ext_flag+"%'";
											dao.update(sql1);
											if(noApproveTargetCanScore!=null && "false".equalsIgnoreCase(noApproveTargetCanScore))//跟per_plan_body表关联去掉不打分的主体 zhaoxg add 2015-12-5
								    			sql ="select pm.mainbody_id,pm.object_id from per_mainbody pm,per_object po,per_plan_body ppb where pm.object_id=po.object_id and pm.plan_id=po.plan_id and pm.plan_id=" + plan_id + " and pm.plan_id=ppb.plan_id and pm.body_id=ppb.body_id  and ("+Sql_switcher.isnull("ppb.isgrade", "0")+" <>'1') and ("+ Sql_switcher.isnull("pm.STATUS", "0") +"=0 or pm.status=0 or pm.status='1') and (po.sp_flag='03') ";
								    		else		    			
								    			sql ="select mainbody_id,object_id from per_mainbody pm,per_plan_body ppb where pm.plan_id=" + plan_id + " and pm.plan_id=ppb.plan_id and pm.body_id=ppb.body_id and ("+ Sql_switcher.isnull("STATUS", "0") +" <>1) and ("+ Sql_switcher.isnull("STATUS", "0") +"=0 or status=0 or status='1')";		    			    		
							    		}
							    	}
							    }				    
							    this.frowset = dao.search(sql);
							    while (this.frowset.next())
							    {
							        String recerver = this.frowset.getString(1);
							    	if("1".equals(method) && "start".equalsIgnoreCase(oper))
							    		emailReceivorList.add("Usr" +  recerver);
								    else if("2".equals(method) && ("start".equalsIgnoreCase(oper) || "distribute".equalsIgnoreCase(oper)))
								    {

										//打分的时候需要判断：目标卡计划，不勾选按顺序打分的话，也需要判断是否已经打过分,打过分则给最近一个没有打过分的主体发代办
										if("start".equalsIgnoreCase(oper)){
											RowSet rsScored = null;
											try {
												StringBuffer scoredSql = new StringBuffer();
												String tempLevel = "";
												if(Sql_switcher.searchDbServer()==Constant.ORACEL){
													tempLevel += "pms.level_o";
												}else {
													tempLevel += "pms.level";
												}
												scoredSql.append("select pm.mainbody_id,"+tempLevel+",pms.body_id from per_mainbody pm,per_mainbodyset pms,per_plan_body ppb ");
												scoredSql.append("where pm.body_id=pms.body_id and pm.object_id=? and pm.plan_id=? and pm.plan_id=ppb.plan_id and pm.Body_id=ppb.body_id and ppb.isgrade<>1");
												scoredSql.append(" and ("+Sql_switcher.isnull("pm.status","0")+"=0 or "+Sql_switcher.isnull("pm.status","0")+"=1) order by "+tempLevel+" desc");
												List scoredList = new ArrayList();
												scoredList.add(recerver);
												scoredList.add(plan_id);
												rsScored = dao.search(scoredSql.toString(),scoredList);
												if(rsScored.next()){
													recerver = rsScored.getString("mainbody_id");
												}
											}catch (Exception e){
												e.printStackTrace();
											}finally {
												PubFunc.closeDbObj(rsScored);
											}
										}

//								    	if(object_type.equals("2")){
								    		String tempsql = "select B.ISGRADE,B.OPT from per_mainbodyset A,per_plan_body B where A.Body_Id=B.Body_Id and B.plan_id="+plan_id;
								    		if(Sql_switcher.searchDbServer()==Constant.ORACEL){
								    			tempsql += " and A.level_o=5";
								    		}else {
								    			tempsql += " and A.level=5";
								    		}
								    		RowSet rs = dao.search(tempsql);
								    		boolean is_self = false;//是否设置了本人考核主体，没设置不给本人发送待办
								    		String isgrade = "";
								    		if(rs.next()){
								    			isgrade = rs.getString("isgrade");
								    			is_self = true;
								    		}
								    		if(!("1".equals(isgrade)||"".equals(isgrade))){//确认或者不打分 则过滤掉 zhaoxg add 2016-5-18
								    			if("2".equals(object_type)){
								    				emailReceivorList.add("Usr" +  recerver);
								    			}else{
								    				emailReceivorList.add("Usr" +  recerver + ":" +this.frowset.getString(2));
								    			}
//								    			emailReceivorList.add("Usr" +  this.frowset.getString(1));	
								    		}else{
								    			if(is_self&& "distribute".equalsIgnoreCase(oper)){//分发且设置了本人主体，则给本人发送制定目标卡的待办，否则给上级发送 zhaoxg 2017-2-20
									    			if("2".equals(object_type)){
									    				emailReceivorList.add("Usr" +  recerver);
									    			}else{
									    				emailReceivorList.add("Usr" +  recerver + ":" +this.frowset.getString(2));
									    			}
								    			}
								    		}
//								    	}else
//								    		emailReceivorList.add("Usr" +  this.frowset.getString(1) + ":" +this.frowset.getString(2));				    	
								    }
							    }
							    
							    // 主体类别没有选中本人时，给最近的打分的主体发送待办 lium  分发也这个逻辑了，zhaoxg add 2017-2-20
							    if ("2".equals(method) && ("start".equalsIgnoreCase(oper)|| "distribute".equalsIgnoreCase(oper))) {
							    	String temp = Sql_switcher.searchDbServer() == Constant.ORACEL ? " pms.level_o" : " pms.level ";

							    	// 判断主体为本人的记录是否存在
							    	String mainBodySelfSql = null;
							    	if ("2".equals(object_type)) {
							    		mainBodySelfSql = "SELECT 1 FROM per_mainbody pm,per_mainbodyset pms,per_plan_body ppb WHERE pm.body_id=pms.body_id AND "+temp+"=5 AND pm.plan_id=? and pm.plan_id=ppb.plan_id  and pm.body_id=ppb.body_id ";
							    	} else {
							    		mainBodySelfSql = "SELECT 1 FROM per_mainbody pm,per_plan_body ppb WHERE pm.body_id=-1 AND pm.plan_id=? and pm.plan_id=ppb.plan_id and pm.body_id=ppb.body_id ";
							    	}
							    	if("start".equalsIgnoreCase(oper)) {
							    		mainBodySelfSql += "and ("+Sql_switcher.isnull("ppb.isgrade", "0")+" <>'1')";
							    	}
							    	frowset = dao.search(mainBodySelfSql, Arrays.asList(new Object[] {Integer.valueOf(plan_id)}));
							    	if (!frowset.next()) {
							    		int property = 10;
							    		String targetMakeSeries = (String) htxml.get("targetMakeSeries"); // 目标卡制订支持几级审批
							    		if (targetMakeSeries != null && targetMakeSeries.trim().length() > 0) {
							    			property = Integer.parseInt(targetMakeSeries);
							    		}
							    		
							    		String level_str = "";
							    		switch (property) { // 不是本人，不在审批流程中的主体
								    		case 1: level_str = "1"; break;
								    		case 2: level_str = "1,0"; break;
								    		case 3: level_str = "1,0,-1"; break;
								    		case 4: level_str = "1,0,-1,-2"; break;
								    		default: level_str = "1,0,-1,-2";
							    		}
							    		
							    		StringBuffer strSql = new StringBuffer("");
							    		if (noApproveTargetCanScore != null && "false".equalsIgnoreCase(noApproveTargetCanScore)) {
							    			strSql.append("select pm.mainbody_id,pm.object_id," + temp + " from per_mainbody pm");
							    			// "确认"的主体无需待办 lium
							    			strSql.append(" LEFT JOIN per_plan_body ppb ON pm.plan_id = ppb.plan_id AND pm.body_id = ppb.body_id,");
							    			strSql.append(" per_object po,per_mainbodyset pms ");
							    			strSql.append(" where pm.body_id=pms.body_id and pm.object_id=po.object_id and pm.plan_id=po.plan_id ");
							    			strSql.append(" AND "+Sql_switcher.isnull("ppb.opt", "0")+" <> 1 and ("+Sql_switcher.isnull("ppb.isgrade", "0")+" <>'1') ");
							    			strSql.append(" and pm.plan_id=" + plan_id + " and (" + Sql_switcher.isnull("pm.STATUS", "0") + "=0 or pm.status=0 or pm.status='1') ");
							    			switch(Sql_switcher.searchDbServer())
							    			{
							    			  case Constant.MSSQL:
							    			  {
							    				strSql.append(" and " + temp + " in (" + level_str + ") and (po.sp_flag='03') order by pm.object_id, pms.level desc");
							    			  	break;
							    			  }
							    			  case Constant.DB2:
							    			  {
							    				strSql.append(" and " + temp + " in (" + level_str + ") and (po.sp_flag='03') order by pm.object_id, pms.level desc");
							    			  	break;
							    			  }
							    			  case Constant.ORACEL:
							    			  {
							    				strSql.append(" and " + temp + " in (" + level_str + ") and (po.sp_flag='03') order by pm.object_id, pms.level_o desc");
							    			  	break;
							    			  }
							    			}
							    		} else {
							    			strSql.append("select pm.mainbody_id,pm.object_id," + temp);
							    			// "确认"的主体无需待办 lium
							    			strSql.append(" from per_mainbody pm LEFT JOIN per_plan_body ppb ON pm.plan_id = ppb.plan_id AND pm.body_id = ppb.body_id,");
							    			strSql.append(" per_object po,per_mainbodyset pms where pm.body_id=pms.body_id and pm.object_id=po.object_id and pm.plan_id=po.plan_id ");
							    			strSql.append(" AND "+Sql_switcher.isnull("ppb.opt", "0")+" <> 1 and ("+Sql_switcher.isnull("ppb.isgrade", "0")+" <>'1') ");
							    			strSql.append(" and pm.plan_id=" + plan_id + " and " + temp + " in (" + level_str + ") ");
							    			//分发时需要判断考核对象的目标卡状态为未报 haosl 2018-9-13
							    			if("distribute".equalsIgnoreCase(oper)) {
							    				strSql.append(" and "+Sql_switcher.isnull("po.sp_flag","'01'")+"='01'"); 
							    			}
							    			switch(Sql_switcher.searchDbServer())
							    			{
							    			  case Constant.MSSQL:
							    			  {
							    				strSql.append(" and (" + Sql_switcher.isnull("pm.STATUS", "0") + "=0 or pm.status=0 or pm.status='1') order by pm.object_id,pms.level desc");
							    			  	break;
							    			  }
							    			  case Constant.DB2:
							    			  {
							    				strSql.append(" and (" + Sql_switcher.isnull("pm.STATUS", "0") + "=0 or pm.status=0 or pm.status='1') order by pm.object_id, pms.level desc");
							    			  	break;
							    			  }
							    			  case Constant.ORACEL:
							    			  {
							    				strSql.append(" and (" + Sql_switcher.isnull("pm.STATUS", "0") + "=0 or pm.status=0 or pm.status='1') order by pm.object_id, pms.level_o desc");
							    			  	break;
							    			  }
							    			}
							    			
							    		}
							    		this.frowset = dao.search(strSql.toString());
							    		String t_ObjectId="";
							    		while (this.frowset.next()) {
//							    			//如果是分发，则只给最近的那个考核主体发制定代办，比如上级和上上级，就只给上级发代办  haosl 2018-6-25
                                            if("distribute".equalsIgnoreCase(oper)) {
                                                if(this.frowset.getString("object_id").equalsIgnoreCase(t_ObjectId)) {
                                                    continue;
                                                }else{
                                                    t_ObjectId=this.frowset.getString("object_id");
                                                }
                                            }
                                            if("2".equals(object_type)) {
                                                emailReceivorList.add("Usr" +  this.frowset.getString(1) + "~" + this.frowset.getString(2) + "`" + this.frowset.getString(3));
                                            } else {
                                                emailReceivorList.add("Usr" +  this.frowset.getString(1) + ":" +this.frowset.getString(2));
                                            }
							    		}
							    	}
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
						    			sql ="select pm.mainbody_id,pm.object_id,"+temp+" from per_mainbody pm,per_object po,per_mainbodyset pms,per_plan_body ppb where pm.body_id=pms.body_id and pm.object_id=po.object_id and pm.plan_id=po.plan_id and pm.plan_id=" + plan_id + " and pm.plan_id=ppb.plan_id and pm.body_id=ppb.body_id and ("+Sql_switcher.isnull("ppb.isgrade", "0")+" <>'1') and ("+ Sql_switcher.isnull("pm.STATUS", "0") +"=0 or pm.status=0 or pm.status='1') and (po.sp_flag='03') ";
							    	else		    			
							    		sql ="select mainbody_id,object_id,"+temp+" from per_mainbody pm,per_mainbodyset pms,per_plan_body ppb where pm.body_id=pms.body_id and pm.plan_id=" + plan_id + " and pm.plan_id=ppb.plan_id and pm.body_id=ppb.body_id and ("+Sql_switcher.isnull("ppb.isgrade", "0")+" <>'1') and ("+ Sql_switcher.isnull("pm.STATUS", "0") +"=0 or pm.status=0 or pm.status='1') ";		    			    		
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
										default: level_str="5,1,0,-1,-2";
									}
									String temp="";
						    		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
						    			temp=" pms.level_o";
									else
										temp=" pms.level ";
																
									StringBuffer strSql=new StringBuffer("");							
									if(noApproveTargetCanScore!=null && "false".equalsIgnoreCase(noApproveTargetCanScore))
									{
										strSql.append("select pm.mainbody_id,pm.object_id,"+temp+" from per_mainbody pm");
										// "确认"的主体无需待办 lium
										strSql.append(" LEFT JOIN per_plan_body ppb ON pm.plan_id = ppb.plan_id AND pm.body_id = ppb.body_id,");
										strSql.append(" per_object po,per_mainbodyset pms "); 
										strSql.append(" where pm.body_id=pms.body_id and pm.object_id=po.object_id and pm.plan_id=po.plan_id "); 
										strSql.append(" AND "+Sql_switcher.isnull("ppb.opt", "0")+" <> 1");
										strSql.append(" and pm.plan_id=" + plan_id + " and ("+ Sql_switcher.isnull("pm.STATUS", "0") +"=0 or pm.status=0 or pm.status='1') "); 
										strSql.append(" and "+temp+" not in ("+level_str+") and (po.sp_flag='03') "); 
									}
									else
									{
										strSql.append("select pm.mainbody_id,pm.object_id,"+temp+" ");
										// "确认"的主体无需待办 lium
										strSql.append(" from per_mainbody pm LEFT JOIN per_plan_body ppb ON pm.plan_id = ppb.plan_id AND pm.body_id = ppb.body_id,");
										strSql.append(" per_mainbodyset pms where pm.body_id=pms.body_id ");
										strSql.append(" AND "+Sql_switcher.isnull("ppb.opt", "0")+" <> 1");
										strSql.append(" and pm.plan_id="+plan_id+" and "+temp+" not in ("+level_str+") ");							
										strSql.append(" and ("+ Sql_switcher.isnull("pm.STATUS", "0") +"=0 or pm.status=0 or pm.status='1') ");
									}									
									this.frowset=dao.search(strSql.toString());			    	
									while (this.frowset.next())	
								    {					    	
								  	
									    emailReceivorList.add("Usr" +  this.frowset.getString(1) + "~" + this.frowset.getString(2) + "`" + this.frowset.getString(3));
								 				    							    				
								    }		
									
						    	}																
								
						    }
														 			    
						    // 获得待办链接名称
						    String title = "";		    		    			
							if(("1".equals(method)) && ("start".equalsIgnoreCase(oper)))
							{
								title=getDBStr("4", this.plan_vo.getString("name"),this.userView.getUserFullName());
								if(title==null || "".equals(title) || title.trim().length()<=0)
									title=this.plan_vo.getString("name")+"_(评分)";
							}
							else if(("2".equals(method)) && ("distribute".equalsIgnoreCase(oper)))
							{
								title=getDBStr("5", this.plan_vo.getString("name"),this.userView.getUserFullName());
								if(title==null || "".equals(title) || title.trim().length()<=0)
									title=this.plan_vo.getString("name")+"_(设定)";
							}
							else if(("2".equals(method)) && ("start".equalsIgnoreCase(oper)))
							{
								title=getDBStr("4", this.plan_vo.getString("name"),this.userView.getUserFullName());
								if(title==null || "".equals(title) || title.trim().length()<=0)
									title=this.plan_vo.getString("name")+"_(评分)";
							}			
							
							if("distribute".equals(oper)){//被暂停过的计划  启动或分发 直接修改待办表

								String _title= title.replace("(设定)", "(审批)");//wangrd  20150720 已进入审核的待办 会存在误改为（设定）的情况，考虑计划名称会更改的情况，还得更新名称
								RowSet rs=dao.search("select * from t_hr_pendingtask where pending_type='33' and ext_flag = 'PERSP_"+plan_id+"' and pending_status<>'1' and pending_status<>'0'");
								while(rs.next()){
									//改成创建新的代办任务，清除旧任务，以满足第三方代办 zhanghua 2018-10-18
									LazyDynaBean bean = new LazyDynaBean();
									bean.set("title", _title);
									bean.set("url", rs.getString("pending_url"));
									bean.set("oper", oper);
									LazyDynaBean _bean=PerformanceImplementBo.updatePendingTask(this.getFrameconn(), this.userView, rs.getString("receiver"),this.plan_vo.getString("plan_id"),bean,"1");
									if("add".equals(_bean.get("flag"))){
										pe.insertPending("PER"+_bean.get("pending_id"),"P",_title,this.userView.getDbname()+this.userView.getA0100(),rs.getString("receiver"),rs.getString("pending_url"),0,1,"目标制订",this.userView);
									}
									//pe.updatePending("P", "PER"+rs.getString("pending_id"), 0, "计划分发", this.userView);
								}

								dao.update("update t_hr_pendingtask set pending_status='1',pending_title='"+_title+"' where pending_type='33' and ext_flag = 'PERSP_"+plan_id+"' and pending_status='1' and pending_status='0'");
								
							}else if("start".equals(oper)){
//								RowSet rs=dao.search("select * from t_hr_pendingtask where pending_type='33' and ext_flag = 'PERPF_"+plan_id+"' and pending_status<>'1'");
//								while(rs.next()){
//									pe.updatePending("P", "PER"+rs.getString("pending_id"), 0, "计划启动", this.userView);
//								}
								//改成创建新的代办任务，清除旧任务，以满足第三方代办 zhanghua 2018-10-18
								dao.update("update t_hr_pendingtask set pending_status='1',pending_title='"+title+"' where pending_type='33' and ext_flag like 'PERPF_"+plan_id+"%' and pending_status<>'1' and pending_status<>'9'");
							}
							
							// 获得当前计划的所有考核主体 
			                HashMap mainbodyidMap = new HashMap();  
			                if(("2".equals(method)) && ("start".equalsIgnoreCase(oper)))
			                	mainbodyidMap = pb.getMainbodyidMap(plan_id);
			                
			                // 如果只有自评，（包括团队负责人），即不需要给其它对象评分的标志 
			                HashMap selfMainbodyMap = new HashMap();  
			                if(("1".equals(method)) && ("start".equalsIgnoreCase(oper)))
			                	selfMainbodyMap = pb.getSelfMainbodyMap(plan_id);	                	                
			                
			                // 发送待办前先判断是否已给此考核对象或考核主体发送过待办 
//			                HashMap haveOrnoTaskMap = judgePendingTask("Usr",oper);
			                String url = "";
						    for (int i = 0; i < emailReceivorList.size(); i++)
						    {	
						    	String body_id = "5";
						    	url = "";
								String prea0100 = emailReceivorList.get(i);
								String preaObject_id = prea0100.substring(3);
								if("2".equals(method) && ("start".equalsIgnoreCase(oper) || "distribute".equalsIgnoreCase(oper)))
							    {
									if((prea0100.indexOf("~")!=-1) && (prea0100.indexOf("`")!=-1))			    	
							    	{
							    		String preMainBody_id = emailReceivorList.get(i);
							    		prea0100 = preMainBody_id.substring(0,preMainBody_id.indexOf("~"));
							    		preaObject_id = preMainBody_id.substring(preMainBody_id.indexOf("~")+1,preMainBody_id.indexOf("`"));
							    		body_id = preMainBody_id.substring(preMainBody_id.indexOf("`")+1);
							    		
							    	}else if(!"2".equals(object_type))
							    	{		
							    		String preMainBody_id = emailReceivorList.get(i);
							    		prea0100 = preMainBody_id.substring(0,preMainBody_id.indexOf(":"));
							    		preaObject_id = preMainBody_id.substring(preMainBody_id.indexOf(":")+1);					    		
							    	}				    				    	
							    }
								// 此处控制目标计划启动时，若考核对象不参与评分则不给对象发待办
								if(("2".equals(method)) && ("start".equalsIgnoreCase(oper)))
								{
									if(mainbodyidMap.get(prea0100.substring(3))==null)
										continue;
								}
								// 360计划 启动时候 考核对象类别为人员和部门都发送待办任务给考核主体
							    if(("1".equals(method)) && ("start".equalsIgnoreCase(oper)))
							    {	
							    	String mailTogoLink = (String)htxml.get("MailTogoLink"); // 评分邮件通知、待办任务界面，360默认为1：多人考评界面 2：单人考评界面 3：不发邮件。目标默认为1：目标评分 3：不发邮件
							    	// 如果计划是能力素质计划（busitype=1），且mailTogoLink参数=3（属性值=“无”?）
							    	// 则将mailTogoLink赋值为2 by lium
							    	double busitype = plan_vo.getDouble("busitype");
							    	mailTogoLink = busitype == 1 && "3".equals(mailTogoLink) ? "2" : mailTogoLink;
							    	
								    if(selfMainbodyMap.get(prea0100.substring(3))!=null)
									{						    	
						                String mitiScoreMergeSelfEval = (String) htxml.get("mitiScoreMergeSelfEval"); // 显示自我评价
										//String SelfEvalNotScore=(String)htxml.get("SelfEvalNotScore");  // 自我评价界面不显示打分模板
						                
										if("1".equalsIgnoreCase(mailTogoLink) && "1".equalsIgnoreCase(markingMode))
										{
											url = "/selfservice/performance/batchGrade.do?b_query=link&linkType=1&model=0&modelEmail=0&returnflag=8&plan_id_db="+plan_id;
											
										}else if("1".equalsIgnoreCase(mailTogoLink) && "2".equalsIgnoreCase(markingMode))
										{
											url = "/selfservice/performance/batchGrade.do?b_tileFrame=link&model=0&returnflag=8&operate=aaa" + plan_id + "&modelEmail=0&linkType=1&planContext=all";																																
										
										}else if("2".equalsIgnoreCase(mailTogoLink))
										{
											url = "/selfservice/performance/singleGrade.do?b_query=link&returnflag=8&fromModel=frontPanel&model=0&to_plan_id=" + plan_id + "&bint=int&model=0&appfwd=1";																														
										}else if("3".equalsIgnoreCase(mailTogoLink) && "1".equalsIgnoreCase(markingMode))
										{
											url = "/selfservice/performance/batchGrade.do?b_query=link&linkType=1&model=0&modelEmail=0&returnflag=8";																								
											
										}else if("3".equalsIgnoreCase(mailTogoLink) && "2".equalsIgnoreCase(markingMode))
										{
											url = "/selfservice/performance/batchGrade.do?b_tileFrame=link&model=0&returnflag=8&operate=aaa" + plan_id + "&modelEmail=0&linkType=1&planContext=all";																																
										
										}
                                        String SelfEvalNotScore=(String)htxml.get("SelfEvalNotScore");
                                        // 自我评价界面不显示打分模板时，不给本人发代办
						                if(!"true".equalsIgnoreCase(SelfEvalNotScore)&&(objectIdMap.containsKey(preaObject_id)
						                		|| "false".equalsIgnoreCase(mitiScoreMergeSelfEval))) {
						                	//自评
						                	/**
						                	 * 不显示显示自我评价时，本人需要自评并且需要给别人打分时需要发两条代办，一条自评代办，一条给别人评分的代办;只有自评是则只发一条自评代办
											 *	将自我评分和对其他对象评分的代办标题区分 为其他人打分标题是*_(评分)，为自己打分标题为*_(自评) ext_flag="PERPF_"+plan_id+"_SELF";
						                	 */
											String ptitle=this.plan_vo.getString("name")+"_(自评)";
											String purl = "/selfservice/performance/selfGrade.do?b_query=link&returnflag=8&bint=int&model=0&plan_id_db="+plan_id;																															
											LazyDynaBean bean = new LazyDynaBean();
											bean.set("title", ptitle);
											bean.set("url", purl);
											bean.set("oper", oper);
											LazyDynaBean _bean=PerformanceImplementBo.updatePendingTask(this.getFrameconn(), this.userView, prea0100,this.plan_vo.getString("plan_id"),bean,"7");
											if("add".equals(_bean.get("flag"))){
												pe.insertPending("PER"+_bean.get("pending_id"),"P",ptitle,this.userView.getDbname()+this.userView.getA0100(),prea0100,purl,0,1,"计划评分",this.userView);	
											}
											//360仅自我评分时单独处理，发送代办  ext_flag="PERPF_"+plan_id+"_SELF";
											if(objectIdMap.containsKey(preaObject_id))
												continue;
						                }

									}else if("1".equalsIgnoreCase(mailTogoLink) && "1".equalsIgnoreCase(markingMode))
									{
										url = "/selfservice/performance/batchGrade.do?b_query=link&returnflag=8&linkType=1&model=0&modelEmail=0&plan_id_db="+plan_id;
										
									}else if("1".equalsIgnoreCase(mailTogoLink) && "2".equalsIgnoreCase(markingMode))
									{
										url = "/selfservice/performance/batchGrade.do?b_tileFrame=link&returnflag=8&model=0&operate=aaa" + plan_id + "&modelEmail=0&linkType=1&planContext=all";																														
									
									}else if("2".equalsIgnoreCase(mailTogoLink))
									{
										url = "/selfservice/performance/singleGrade.do?b_query=link&returnflag=8&fromModel=frontPanel&model=0&to_plan_id=" + plan_id + "&bint=int&model=0&appfwd=1";																														
									}else if("3".equalsIgnoreCase(mailTogoLink) && "1".equalsIgnoreCase(markingMode))
									{
										url = "/selfservice/performance/batchGrade.do?b_query=link&linkType=1&model=0&modelEmail=0&returnflag=8";																								
										
									}else if("3".equalsIgnoreCase(mailTogoLink) && "2".equalsIgnoreCase(markingMode))
									{
										url = "/selfservice/performance/batchGrade.do?b_tileFrame=link&model=0&returnflag=8&operate=aaa" + plan_id + "&modelEmail=0&linkType=1&planContext=all";																																
									
									}
								    
							    }else if(("2".equals(method)) && ("distribute".equalsIgnoreCase(oper)) && ("2".equals(object_type))) // 目标计划
								{
									url = "/performance/objectiveManage/objectiveCard.do?b_query=query&zglt=0&returnflag=8&entranceType=0&body_id="+body_id+"&model=2&opt=1&planid=" + PubFunc.encryption(plan_id) + "&object_id=" + PubFunc.encryption(preaObject_id);																								
									//url = "/performance/objectiveManage/setUnderlingObjective/underling_objective_tree.do?b_query=link&returnflag=8&entranceType=0&plan_id="+PubFunc.encryption(plan_id);																								
								
								}else if(("2".equals(method)) && ("distribute".equalsIgnoreCase(oper)) && (!"2".equals(object_type)))
								{
									url = "/performance/objectiveManage/objectiveCard.do?b_query=query&zglt=0&returnflag=8&entranceType=0&body_id="+body_id+"&model=1&opt=1&planid=" + PubFunc.encryption(plan_id) + "&object_id=" + PubFunc.encryption(preaObject_id);																								
									
								}else if(("2".equals(method)) && ("start".equalsIgnoreCase(oper)) && ("2".equals(object_type)))
								{
									// 把展现单个目标卡的链接换成展现计划打分列表的链接																							
									url = "/performance/objectiveManage/objectiveEvaluate/objective_evaluate_list.do?b_init=init&returnflag=8&planid="+plan_id+"&opt=4&entranceType=0&isSort=1";
									
								}else if(("2".equals(method)) && ("start".equalsIgnoreCase(oper)) && (!"2".equals(object_type)))
								{
									// 把展现单个目标卡的链接换成展现计划打分列表的链接																							
									url = "/performance/objectiveManage/orgPerformance/org_performance_list.do?b_init=init&returnflag=8&planid="+plan_id+"&opt=4&entranceType=0&isSort=1";
								}
								if("1".equals(performanceType)){//民主测评，链接写死  zhaoxg add 2014-12-18
									String mitiScoreMergeSelfEval = (String) htxml.get("mitiScoreMergeSelfEval"); // 显示自我评价
									//String SelfEvalNotScore=(String)htxml.get("SelfEvalNotScore");  // 自我评价界面不显示打分模板
                                    if ("true".equalsIgnoreCase(mitiScoreMergeSelfEval) ||
                                            ("false".equalsIgnoreCase(mitiScoreMergeSelfEval) && (objectIdMap.size() > 0 && !objectIdMap.containsKey(preaObject_id)))) {
										if("2".equals(object_type))//人员就走班子成员测评，非人员就走领导班子测评   zhaoxg add
											url = "/selfservice/performance/singleGrade.do?b_query=link&model=1&fromModel=menu&optObject=2&returnflag=8&to_plan_id="+plan_id+"&bint=int&appfwd=1";
										else
											url = "/selfservice/performance/singleGrade.do?b_query=link&model=1&fromModel=menu&optObject=1&returnflag=8&to_plan_id="+plan_id+"&bint=int&appfwd=1";
									}else{
										url = "/selfservice/performance/selfGrade.do?b_query=link&returnflag=8&bint=int&model=1&plan_id_db="+plan_id;	
									}
								}
								if("start".equalsIgnoreCase(oper)){
									ObjectCardBo ocbo=new ObjectCardBo(this.frameconn, userView, plan_id);
									ocbo.setUserView(userView);
									ocbo.setConn(getFrameconn());
									ocbo.setPlan_id(plan_id);
									if(ocbo.isOpenGrade_Members()){
										ocbo.cleanGradePendingTask(plan_id, "0","");//启动时清除评价人代办 zhanghua
									}
								}
							    
								
								if("start".equalsIgnoreCase(oper) && !StringUtils.isEmpty(startFlag) && "result".equalsIgnoreCase(startFlag)){//手工录分时启动不发待办
									continue;
								}
								
								String _title = title;
								if(("2".equals(method)) && ("distribute".equalsIgnoreCase(oper))) {// 分发时，title增加考核对象区分。场景：有时一个人处于考核计划的多个考核对象或领导给员工设定目标卡的待办没法区分。
									
									String objName = "";
									if("2".equals(object_type)) {// 人员
										objName = this.getUsrA0101(prea0100.substring(0,3), preaObject_id);
												
									} else {// 部门等
										String un = AdminCode.getCodeName("UN", preaObject_id);
										String um = AdminCode.getCodeName("UM", preaObject_id);
										String K = AdminCode.getCodeName("@K", preaObject_id);
										if(StringUtils.isNotEmpty(un)) {
											objName = un;
										} else if(StringUtils.isNotEmpty(um)) {
											objName = um;
										} else if(StringUtils.isNotEmpty(K)) {
											objName = K;
										}
									}
									_title += ("("+objName+"的目标卡)");
								}
								
							    // 发送待办并向待办库中加入新的待办
								sendMessageToPT(_title, prea0100.substring(3), "Usr", url, oper, preaObject_id);	
						    }

						}
					}					    		    					    		    				
//				}
		
		} catch (Exception e)
		{
		    e.printStackTrace();
		    this.getFormHM().put("resultFlag", "0");
		    throw GeneralExceptionHandler.Handle(e);
		}
		this.getFormHM().put("resultFlag", "1");
    }
	
	/**
	 * 发送待办并向待办库中加入新的待办
	 * @param title
	 * @param content
	 * @param appealObject_id
	 * @param nbase
	 * @param url
	 * @param opt  1:目标卡制订  2：目标卡评估 4:目标评估（只修改状态，不发送新信息）  3:目标卡批准.  5.驳回操作
	 */
	public void sendMessageToPT(String title,String appealObject_id,String nbase,String url,String oper, String preaObject_id)
	{
		try
		{
			PendingTask pe = new PendingTask();
			
			String method=String.valueOf(this.plan_vo.getInt("method"));
			String pendingType="";
			if(("1".equals(method)) && ("start".equalsIgnoreCase(oper)))
				pendingType="计划打分";
			else if(("2".equals(method)) && ("distribute".equalsIgnoreCase(oper)))
				pendingType="目标制订";
			else if(("2".equals(method)) && ("start".equalsIgnoreCase(oper)))
				pendingType="目标评估";
			if("distribute".equalsIgnoreCase(oper)){//分发状态为distribute1 原来的distribute为交办时候用的 防止无法区分待办是处于制定状态还是审批状态   zhaoxg update 2014-11-3
				oper = "distribute1";
			}
//			String pendingCode=getPendingCode(this.userView.getA0100());
			
			// 发送待办前先判断是否已给此考核对象或考核主体发送过待办						
			LazyDynaBean bean = new LazyDynaBean();
			bean.set("title", title);
			bean.set("url", url);
			bean.set("oper", oper);
			
			// 分发产生的待办，待办表中ext_flag字段改为“PERZD_planId_object_id(加密)”。同时要兼容老的“PERZD_planId”的情况。chent 20170725 start
			HashMap map = PerformanceImplementBo.isHavePendingtask(nbase+appealObject_id, this.getFrameconn(), "PERZD_"+this.plan_vo.getString("plan_id"));//先判断旧的没有object_id的情况。没有则走新的带有object_id的逻辑;有则按照就逻辑走。
			if(map.size() == 0){
				bean.set("object_id", preaObject_id);
			}
			LazyDynaBean _bean=PerformanceImplementBo.updatePendingTask(this.getFrameconn(), this.userView, nbase+appealObject_id,this.plan_vo.getString("plan_id"),bean,"4");
			if("add".equals(_bean.get("flag"))){
				pe.insertPending("PER"+_bean.get("pending_id"),"P",title,this.userView.getDbname()+this.userView.getA0100(),nbase+appealObject_id,url,0,1,pendingType,this.userView);	
			}
//			if("update".equals(_bean.get("flag"))){
//				pe.updatePending("P", "PER"+_bean.get("pending_id"), 0, "目标制订", this.userView);
//			}
			// 分发产生的待办，待办表中ext_flag字段改为“PERZD_planId_object_id(加密)”。同时要兼容老的“PERZD_planId”的情况。chent 20170725 end
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 根据a0100获取username
	 * @return
	 */
    public String getUserName(String a0100){
    	String username="";
    	try{
            AttestationUtils utils=new AttestationUtils();
            LazyDynaBean fieldbean=utils.getUserNamePassField();
            String username_field=(String)fieldbean.get("name");
    		ContentDAO dao = new ContentDAO(this.getFrameconn());
    		String sql="select "+username_field+" as username from UsrA01 where A0100='"+a0100+"'";
    		RowSet rs=dao.search(sql);
    		if(rs.next()){
    			username=rs.getString("username");
    		}  		
    	}catch(Exception e)
		{
			e.printStackTrace();
		}
    	return username;
    }
    /**   
     * @Title: getUsrA0101   
     * @Description: 取得人员姓名   
     * @param @param nbase
     * @param @param a0100
     * @param @return 
     * @return String 
     * @throws   
    */
    public String getUsrA0101(String nbase,String a0100) {
        String a0101="";
        if (nbase==null || a0100==null){
            return "";
        }
        RowSet rset=null;
        String strsql="select * from "+nbase+"A01 where a0100='"+a0100+"'";
        try{
        	ContentDAO dao = new ContentDAO(this.getFrameconn());
            rset=dao.search(strsql);
            if (rset.next()){
                a0101= rset.getString("a0101");
            }
        }
        catch(Exception e){           
            e.printStackTrace();  
        } finally {
            PubFunc.closeDbObj(rset);
        }
        return a0101;
    } 
	/**
	 * 保存代办id纪录
	 * @param object_id
	 * @param nbase
	 * @param appealObject_id
	 */
	public void insertPendingCode(String nbase,String appealObject_id,String pendingCode,String oper)
	{
		RowSet rowSet = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.frameconn);
		
			String method=String.valueOf(this.plan_vo.getInt("method"));
			StringBuffer buf=new StringBuffer();

			/**先删除当前用户，当前正在处于起草状态的记录*/			
/*			buf.append("delete from per_task_pt where plan_id=? and mainbody_id=? and flag=3");
			ArrayList paralist=new ArrayList();
			paralist.add(new Integer(this.plan_vo.getInt("plan_id")));				
			paralist.add(appealObject_id);
			dao.update(buf.toString(),paralist);						
*/						
			/**增加一条记录*/
			int maxid=0;
/*			IDGenerator idg = new IDGenerator(2,this.frameconn);
			String temp=IDGenerator.getKeyId("per_task_pt", "id", 1);
			if(temp.indexOf(".")!=-1)
				temp=temp.substring(0,temp.indexOf("."));
			maxid=Integer.parseInt(temp);							
*/			
			
	        rowSet = dao.search("select max(id) from per_task_pt");
	        while(rowSet.next())
	        {
	        	String id = rowSet.getString(1);
	        	if((id!=null) && (id.trim().length()>0) && (id.indexOf(".")!=-1))
	        		id=id.substring(0,id.indexOf("."));
	        	if((id!=null) && (id.trim().length()>0))
	        		maxid=Integer.parseInt(id);	
	        }
	        ++maxid;	        						
			
			RecordVo vo=new RecordVo("per_task_pt");
			vo.setInt("id", maxid);
			if("2".equals(method))	  // 当计划类型为 目标计划时 object_id=mainbody_id  360计划 object_id=null
				vo.setString("object_id", appealObject_id);
			vo.setString("mainbody_id",appealObject_id);
			vo.setString("nbase",nbase);
			vo.setString("task_id",pendingCode);
			vo.setInt("plan_id", this.plan_vo.getInt("plan_id"));
			if("start".equalsIgnoreCase(oper))  // 启动 flag=2
				vo.setInt("flag",2);
			else if("distribute".equalsIgnoreCase(oper)) // 分发 flag=1
				vo.setInt("flag",1);
			
			dao.addValueObject(vo);					
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 判断是否已发送过待办	 
	 * @param nbase
	 * @param appealObject_id
	 * @param oper
	 */
	public HashMap<String, String> judgePendingTask(String nbase, String oper)
	{
		HashMap<String, String> mainbodyMap = new HashMap<String, String>();
		RowSet rowSet = null;							
		try
		{
			ContentDAO dao = new ContentDAO(this.frameconn);		
			String method = String.valueOf(this.plan_vo.getInt("method"));
												
//			String sql = "select flag from per_task_pt where nbase='"+nbase+"' and plan_id='"+ this.plan_vo.getInt("plan_id") +"' and mainbody_id='"+ appealObject_id +"'";
			String sql = "select flag,mainbody_id from per_task_pt where nbase='"+nbase+"' and plan_id='"+ this.plan_vo.getInt("plan_id") +"' ";
			rowSet=dao.search(sql);				
		    while (rowSet.next())	
		    {		    	
		    	String flag = rowSet.getString("flag");				    					
				if((flag!=null && flag.trim().length()>0) && (flag.indexOf(".")!=-1))
					flag=flag.substring(0,flag.indexOf("."));				
		    	
		    	if(("1".equals(method)) && ("start".equalsIgnoreCase(oper)))
		    		mainbodyMap.put((oper + rowSet.getString("mainbody_id")), "true");
			    else if(("2".equals(method)) && ("distribute".equalsIgnoreCase(oper)) && ((flag!=null && flag.trim().length()>0) && ("1".equalsIgnoreCase(flag))))
			    	mainbodyMap.put((oper + rowSet.getString("mainbody_id")), "true");
	    		else if(("2".equals(method)) && ("start".equalsIgnoreCase(oper)) && ((flag!=null && flag.trim().length()>0) && ("2".equalsIgnoreCase(flag))))
	    			mainbodyMap.put((oper + rowSet.getString("mainbody_id")), "true");  			
		    }			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return mainbodyMap;
	}
	
	/**
	 * plan_id 计划号
	 * 获得某编号的考核计划的所有信息
	 */
	public RecordVo getPlanVo(String planid)
	{
		RecordVo vo=new RecordVo("per_plan");
		try
		{
			vo.setInt("plan_id",Integer.parseInt(planid));
			ContentDAO dao = new ContentDAO(this.frameconn);
			vo=dao.findByPrimaryKey(vo);
			if(vo.getInt("method")==0)
				vo.setInt("method",1);		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return vo;
	}	
    
	/**
	 * 实现待办自定义
	 * @param plan_name	 
	 * @return
	 */
	public String getDBStr(String opt,String plan_name,String optName)
	{
		String str="";
		try
		{
			String dStr=SystemConfig.getPropertyValue("PendingMessage"+opt);
			if(dStr==null|| "".equals(dStr))
				return str;
			String astr=dStr;
//			astr=astr.replaceAll("\\[考核对象\\]", objectName);
			astr=astr.replaceAll("\\[计划名称\\]", plan_name);
			astr=astr.replaceAll("\\[操作用户名称\\]", optName);
			str=astr;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
	
	//待办信息在应用系统的唯一标识代号
	public String getPendingCode(String a0100)
	{
		Date d=new Date();
		return "IMIS-003-"+d.getTime()+a0100+Math.round(Math.ceil(Math.random()*10));
	}		    
	
    /**
     * 找出启动计划时各任务指定的评分人如不为某对象目标卡的评分人，系统自动将其添加成一级考核主体类别下的考核主体
     * （此类考核主体在数据库记录中会有相应标识 per_mainbody.Un_planned=1，方便后续程序逻辑判断）
     * 
     * @param planid
     */
    private void addMainBody(String planid,ContentDAO dao) {
    	RowSet rowSet = null;
    	ArrayList<Object> list = new ArrayList<Object>();
    	StringBuffer sbf = new StringBuffer();
    	try {
    		int body_id = 0;//一级考核主体类别下的考核主体
    		ObjectCardBo bo=new ObjectCardBo(getFrameconn(), getUserView(), planid);
    		if(!bo.isOpenGrade_Members())
    			return;
    		String level="level";
    		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
    			level="level_O";
    		String sql = "select pm.body_id body_id from per_plan_body pb left join per_mainbodyset pm on  pb.body_id = pm.body_id where  pb.plan_id = ? and pm."+level+" = ?";
    		list.add(new Integer(planid));
    		list.add(new Integer(1));
    		rowSet = dao.search(sql,list);
			if(rowSet.next()) {
				body_id = rowSet.getInt("body_id");
			}
			list.clear();
			//查出所有需要添加的人员
			
			String pobject="a0100";
			if (bo.getPlan_vo().getInt("object_type") == 1
					|| bo.getPlan_vo().getInt("object_type") == 3
					|| bo.getPlan_vo().getInt("object_type") == 4)
				pobject="b0110";
			
			sbf.append("select UsrA01.b0110,UsrA01.e0122,UsrA01.e01a1,p."+pobject+" as object_id,g.A0100 as mainbody_id,g.a0101 from ");
			sbf.append(" per_grade_members g INNER JOIN p04 p ON p.p0400 = g.p0400  inner join UsrA01 on g.A0100=usrA01.a0100  where p.plan_id = ? and ");
			sbf.append("not EXISTS (select 1 from per_mainbody where plan_id = ? and object_id = p."+pobject+" and mainbody_id = g.A0100 )");
			list.add(planid);
			list.add(planid);
			rowSet = dao.search(sbf.toString(),list);
			//将这些人插入到per_mainbody
			String sql2 = "insert into per_mainbody (id,b0110,e0122,e01a1,object_id,mainbody_id,a0101,body_id,plan_id,status,un_planned) values(?,?,?,?,?,?,?,?,?,?,?)";
			ArrayList<ArrayList<Object>> recordList2 = new ArrayList<ArrayList<Object>>();
			while (rowSet.next())
			{
				ArrayList<Object> tempList2 = new ArrayList<Object>();

				IDGenerator idg = new IDGenerator(2, this.frameconn);
				String id = idg.getId("per_mainbody.id");
				tempList2.add(new Integer(id));
				tempList2.add(rowSet.getString("b0110"));
				tempList2.add(rowSet.getString("e0122"));
				tempList2.add(rowSet.getString("e01a1"));
				tempList2.add(rowSet.getString("object_id"));
				tempList2.add(rowSet.getString("mainbody_id"));
				tempList2.add(rowSet.getString("a0101"));
				tempList2.add(new Integer(body_id));
				tempList2.add(new Integer(planid));
				tempList2.add(new Integer(0));
				tempList2.add(new Integer(1));
				recordList2.add(tempList2);

			}
			dao.batchInsert(sql2, recordList2);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PubFunc.closeDbObj(rowSet);
		}
    }
	/**
	 * 获取计划中所有考核对象id
	 * @param plan_id
	 * @return
	 * @author ZhangHua
	 * @date 16:29 2017/12/20
	 */
	private HashMap<String,String> getObjectIdFromPlanId(String plan_id){
		HashMap<String,String> map=new HashMap<String, String>();
		try{
			ContentDAO dao = new ContentDAO(this.frameconn);
			String sqlStr="select object_id from per_mainbody pm1 where plan_id=? and object_id=mainbody_id and (select count(mainbody_id) from per_mainbody pm2 where plan_id=? and pm2.mainbody_id=pm1.mainbody_id)=1";
			ArrayList list=new ArrayList();
			list.add(plan_id);
			list.add(plan_id);
			RowSet rs=dao.search(sqlStr,list);
			while(rs.next()){
				map.put(rs.getString("object_id"),"1");
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return  map;
	}
		
}
