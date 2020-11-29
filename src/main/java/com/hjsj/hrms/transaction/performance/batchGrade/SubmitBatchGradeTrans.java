package com.hjsj.hrms.transaction.performance.batchGrade;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.TotalScoreControlBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 *<p>Title:SubmitBatchGradeTrans.java</p> 
 *<p>Description:保存或提交 多人考评</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-06-09 10:15:35</p> 
 *@author Administrator
 *@version 5.0
 */

public class SubmitBatchGradeTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
		//	System.out.println("----batchGrade-------");
			String linkType=(String)this.getFormHM().get("linkType");
			String users=(String)this.getFormHM().get("users");
			users = users.replaceAll("／", "/");
			String[] userid=users.split("/");											//考核对象
			String flag=(String)this.getFormHM().get("flag");							//操作类型 1：保存  2：提交  3:保存（排序）  8打分完成
			String plan_id=(String)this.getFormHM().get("plan_id");						//考核计划
			CheckPrivSafeBo _bo = new CheckPrivSafeBo(this.frameconn,this.userView);
			boolean _flag = _bo.isPlanIdPriv(plan_id);
			if(!_flag){
				return;
			}
			String template_id=(String)this.getFormHM().get("template_id");				//考核计划相应模版id
			String isKnowWhole=(String)this.getFormHM().get("isKnowWhole");
			String status=(String)this.getFormHM().get("status");
		
			String scoreflag=(String)this.getFormHM().get("scoreflag");					// 2混合，1标度
			HashMap usersValue=new HashMap();
			StringBuffer object_ids=new StringBuffer("");
			for(int i=0;i<userid.length;i++)
			{		
				object_ids.append(",'"+userid[i]+"'");
				usersValue.put(userid[i],(String)this.getFormHM().get(userid[i]));		//各对象的评分结果
			}
			StringBuffer score_order=new StringBuffer("");
			
			BatchGradeBo batchGradeBo=new BatchGradeBo(this.getFrameconn(),plan_id);
			String isShowSubmittedPlan=batchGradeBo.getIsShowSubmittedPlan();           //提交后的计划是否需要显示True|False
			this.getFormHM().put("isShowSubmittedPlan",isShowSubmittedPlan.toLowerCase());
			
			String EvalClass = "";
			String totalAppFormula = "";
		//	batchGradeBo.setObjectType(plan_id);                                        //设置考核对象类型
			String info=ResourceFactory.getProperty("lable.performance.saveFail")+"!";
						
			String info0="";
			if(("2".equals(flag)|| "8".equals(flag))&&(!"true".equalsIgnoreCase(batchGradeBo.getDynaBodyToPoint())))
				info0=batchGradeBo.isMustScore(userid, this.userView.getA0100(), plan_id, usersValue);
			
			Hashtable htxml = new Hashtable();
			LoadXml loadxml = new LoadXml(this.getFrameconn(),plan_id);  // batchGradeBo.getLoadxml(); 
			htxml = loadxml.getDegreeWhole();
			
			if(info0.length()==0)
			{
				batchGradeBo.setNoShowOneMark(true);
				batchGradeBo.setLoadStaticValue(true);
				String a_info=batchGradeBo.validateScore(template_id,plan_id,usersValue,userid);
				if("success".equals(a_info))
				{		
					ContentDAO dao = new ContentDAO(this.frameconn);
					ArrayList gradeScopeList = loadxml.getPerGradeScopeList("ScoreScope");	 // 得到主体评分范围所有设置	
					HashMap fineMaxMap=(HashMap)htxml.get("fineMaxMap");
					String fineMax=(String)htxml.get("fineMax");
					String bFineRestrict=(String)htxml.get("FineRestrict");
						 
					String WholeEval=(String)htxml.get("WholeEval");
					totalAppFormula =(String)htxml.get("TotalAppFormula"); // 总体评价的计算公式，默认为空
					HashMap BadlyMap=(HashMap)htxml.get("BadlyMap");
					String BadlyRestrict=(String)htxml.get("BadlyRestrict");
					String BadlyMax=(String)htxml.get("BadlyMax");
					String KeepDecimal=(String)htxml.get("KeepDecimal");  //小数位	
					String GradeClass = (String)htxml.get("GradeClass");					//等级分类ID
					EvalClass = (String)htxml.get("EvalClass");            //在计划参数中的等级分类ID					 					
					if(EvalClass==null || EvalClass.trim().length()<=0 || "0".equals(EvalClass.trim()))
						EvalClass=(String)htxml.get("GradeClass");					//等级分类ID
					else
						EvalClass=(String)htxml.get("EvalClass");					
					String isControlTatalScore=(String)htxml.get("MutiScoreGradeCtl");         //是否进行总分控制  true|false
					String CheckGradeRange=(String)htxml.get("CheckGradeRange");  //多人打分等级控制是按所有主体还是单个主体。(0:所有，1: 单个)
					String ScoreBySumup=(String)htxml.get("ScoreBySumup");  //BS个人总结没填写，主体为其打分时不能提交
					String SameAllScoreNumLess = (String)htxml.get("SameAllScoreNumLess");// 总分相同的人数不能等于和多余=0不控制，在0-1之间，按百分比控制，比1大按个数控制，	
					String gradeSameNotSubmit = (String)htxml.get("GradeSameNotSubmit");  // 等级不同分数相同不能提交
					
					
					//如设置了“相同分数等级不同不能提交”参数，且评分人有计算按钮权限，提交时先执行计算操作。
					if("2".equals(flag)&& "true".equalsIgnoreCase(gradeSameNotSubmit)&&this.userView.hasTheFunction("06060103")){
						LoadXml loadXml = new LoadXml(this.getFrameconn(),plan_id);
						Hashtable param = loadXml.getDegreeWhole();
						HashMap map = new HashMap();
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
						
						EvalClass = (String)param.get("EvalClass");            //在计划参数中的等级分类ID
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
						PerEvaluationBo bo = new PerEvaluationBo(this.getFrameconn(),plan_id,"",this.userView);
						bo.setBatchComput("True");
						bo.setPresentMainbody_id(this.userView.getA0100());
						bo.setPriv_where(" and object_id in (select object_id from per_mainbody where plan_id = '"+plan_id+"' and mainbody_id = '"+this.userView.getA0100()+"') ");
						bo.calculatePlan(this.getUserView(),map,4);
					}
					
					String mainbodyGradeCtl = (String)htxml.get("MainbodyGradeCtl");  //主体类别
					String mainbodyFlag = "false";  //主体类别是否需要进行强制分布
					String bodyid = "";
					this.frowset=dao.search("select distinct body_id from per_mainbody where plan_id="+plan_id+" and  mainbody_id='"+this.userView.getA0100()+"'");
					while(this.frowset.next()){
						bodyid = this.frowset.getString("body_id");
					}
					if(mainbodyGradeCtl !=null && !"".equals(mainbodyGradeCtl)) {
						String[] mainbodyGradeCtlType = mainbodyGradeCtl.split(",");
						for(int k=0;k<mainbodyGradeCtlType.length;k++){
							if(bodyid.equals(mainbodyGradeCtlType[k])){
								mainbodyFlag = "true";
								break;
							}
						}
					}
					
					HashMap objStatusMap = new HashMap();
					if("2".equals(flag) && (SameAllScoreNumLess!=null && SameAllScoreNumLess.trim().length()>0 && !"0".equals(SameAllScoreNumLess)))
						objStatusMap = batchGradeBo.getObjStatusMap(plan_id,this.userView.getA0100(),object_ids.substring(1));
										
					String aa_info="";
					if(!"False".equals(bFineRestrict)&&("2".equals(flag)|| "8".equals(flag)))
						aa_info=batchGradeBo.validateMaxvalueNum(fineMax,template_id,plan_id,usersValue,userid,fineMaxMap,batchGradeBo.getObjectNums(this.userView.getA0100(),plan_id),this.userView.getA0100(),1,WholeEval,EvalClass);
					else
						aa_info="success";
					if("success".equals(aa_info))
					{
						if(!"False".equals(BadlyRestrict)&&("2".equals(flag)|| "8".equals(flag)))
							aa_info=batchGradeBo.validateMaxvalueNum(BadlyMax,template_id,plan_id,usersValue,userid,BadlyMap,batchGradeBo.getObjectNums(this.userView.getA0100(),plan_id),this.userView.getA0100(),2,WholeEval,EvalClass);
						else
							aa_info="success";
					}
					if("success".equals(aa_info))
					{
						String a_info1="";
						if(("2".equals(flag)|| "8".equals(flag))&& "True".equalsIgnoreCase(ScoreBySumup))  //如果是提交，并且设置了 个人总结没填写，主体为其打分时不能提交
						{
							a_info1=batchGradeBo.isWriteSummary(object_ids.substring(1),plan_id);
						}
						if(a_info1.trim().length()==0)
						{								
							String controlInfo="";							
							// 等级不同分数相同不能提交
							if(("2".equals(flag)|| "8".equals(flag))&&gradeSameNotSubmit!=null&&gradeSameNotSubmit.trim().length()>0&& "true".equalsIgnoreCase(gradeSameNotSubmit)&&this.userView.hasTheFunction("06060103"))
							{
								boolean isHave = batchGradeBo.isHaveSameScoreObjects(plan_id,object_ids.substring(1));
								if(isHave)
								{
									controlInfo = "存在等级不同总分相同的考核对象，不能提交！";
									info = controlInfo;
								}									
							}
							
							if(controlInfo.trim().length()==0)
							{
								//总分分布强制控制
								if(("2".equals(flag)|| "8".equals(flag))&& "true".equalsIgnoreCase(isControlTatalScore)&&GradeClass!=null&&GradeClass.trim().length()>0&& "true".equalsIgnoreCase(mainbodyFlag))
								{
									String  isSuccess=batchGradeBo.insertGradeResult(userid,"1",plan_id,template_id,usersValue,this.userView.getA0100(),status,scoreflag);
									if("1".equals(isSuccess))
									{
										TotalScoreControlBo totalControl=new TotalScoreControlBo(this.getFrameconn(),plan_id,GradeClass);
										batchGradeBo.getDynaRankInfoMap(plan_id);
										batchGradeBo.getObjectInfoMap(plan_id);
										HashMap objectTotalScoreMap=batchGradeBo.getObjectTotalScore(Integer.parseInt(plan_id),this.userView.getA0100(),template_id,this.userView);
										controlInfo=totalControl.isOverTotalScoreControl(objectTotalScoreMap,plan_id,this.userView.getA0100(),CheckGradeRange);
										info=controlInfo;
									}
									else if("2".equals(isSuccess))
									{
										controlInfo=ResourceFactory.getProperty("lable.performance.saveOtherInfo")+"!";
										info=controlInfo;
									}									
								}
							}
							if(controlInfo.trim().length()==0)
							{								
								String isSuccess=batchGradeBo.insertGradeResult(userid,flag,plan_id,template_id,usersValue,this.userView.getA0100(),status,scoreflag);
								if("1".equals(isSuccess))
								{
									if("1".equals(flag))
										info=ResourceFactory.getProperty("label.save.template.success")+"!";
									else if("8".equals(flag))
										info="打分完成!";
									else
										info=ResourceFactory.getProperty("kq.register.daily.success")+"!";
										
									if("2".equals(flag)&&("true".equalsIgnoreCase(((String)htxml.get("noteIdioGoal")).trim())|| "true".equalsIgnoreCase(((String)htxml.get("SummaryFlag")).trim())))
										setArticleState(plan_id,object_ids.substring(1),batchGradeBo);
									
									this.getFormHM().put("BlankScoreOption", batchGradeBo.getBlankScoreOption());
									String isAutoCountTotalOrder=batchGradeBo.getIsAutoCountTotalOrder();
									if("3".equals(flag))
										isAutoCountTotalOrder="true";
																		
									//将总体评价的分值写入per_mainbody表的whole_score字段中
									String wholeEvalMode = (String)this.getFormHM().get("wholeEvalMode");
									String scoretemp = "";
									if("1".equals(wholeEvalMode)){
										for(int i=0;i<userid.length;i++)
										{		
											scoretemp = (String)this.getFormHM().get("wholeEvalScore_"+userid[i]);
											String sqlStr = "update per_mainbody set whole_score="+scoretemp+" where plan_id="+plan_id+" and mainbody_id='"+this.userView.getA0100()+"' and object_id='"+userid[i]+"' ";
											dao.update(sqlStr);
										}
									}
									
									HashMap objectTotalScoreMap=null;								
									// 在此把主体给对象打的总分计算出来后写到 per_mainbody 表的 score 字段中 	JinChunhai 2012.07.05
									objectTotalScoreMap=batchGradeBo.getObjectTotalScore(Integer.parseInt(plan_id),this.userView.getA0100(),template_id,this.userView);	
									
									String sqlStr = "update per_mainbody set score=? where plan_id=? and mainbody_id=? and object_id=?";										
									ArrayList mainInfoList = new ArrayList();
									for(int i=0;i<userid.length;i++)
									{											
										String scoreAndOrder = (String)objectTotalScoreMap.get(userid[i]);											
										String score = scoreAndOrder.substring(0,scoreAndOrder.indexOf("/"));
										score=PubFunc.round(score,Integer.parseInt(KeepDecimal));
										
										ArrayList tempList = new ArrayList();
										tempList.add(score);
										tempList.add(new Integer(plan_id));
										tempList.add(this.userView.getA0100());
										tempList.add(userid[i]);	
										mainInfoList.add(tempList);
									}
									dao.batchUpdate(sqlStr, mainInfoList);
									if("2".equals(flag) && (SameAllScoreNumLess!=null && SameAllScoreNumLess.trim().length()>0 && !"0".equals(SameAllScoreNumLess)))
									{
									//	objectTotalScoreMap=batchGradeBo.getObjectTotalScore(Integer.parseInt(plan_id),this.userView.getA0100(),template_id,this.userView);	
										
									//	String sqlStr = "update per_mainbody set score=? where plan_id=? and mainbody_id=? and object_id=?";										
									//	ArrayList mainInfoList = new ArrayList();
										String object_idss = "";
										for(int i=0;i<userid.length;i++)
										{											
										/*	String scoreAndOrder = (String)objectTotalScoreMap.get(userid[i]);											
											String score = scoreAndOrder.substring(0,scoreAndOrder.indexOf("/"));
											score=PubFunc.round(score,Integer.parseInt(KeepDecimal));
											
											ArrayList tempList = new ArrayList();
											tempList.add(score);
											tempList.add(new Integer(plan_id));
											tempList.add(this.userView.getA0100());
											tempList.add(userid[i]);	
											mainInfoList.add(tempList);*/
											
											String objStatus = (String)objStatusMap.get(userid[i]);
											if(!"2".equalsIgnoreCase(objStatus))
												object_idss += "/"+userid[i];
										}
									//	dao.batchUpdate(sqlStr, mainInfoList);
									
											// 控制总分相同对象个数，
											if(object_idss!=null && object_idss.trim().length()>0)
												object_idss = object_idss.substring(1);
											String errorInfo = batchGradeBo.validateSameScoreAllNumLess(plan_id,object_idss,this.userView.getA0100(),flag,SameAllScoreNumLess);	
											if(errorInfo.length()>0)
												info = errorInfo;
										
									}

									if(info.indexOf("不能等于或多于")==-1)
									{
										//动态跟新页面 分数 和 排名									
										if(("true".equalsIgnoreCase(isAutoCountTotalOrder) && ("true".equalsIgnoreCase(batchGradeBo.getIsShowTotalScore()) || "true".equalsIgnoreCase(batchGradeBo.getIsShowOrder())))
												|| (gradeScopeList!=null && gradeScopeList.size()>0))
										{
											batchGradeBo.getDynaRankInfoMap(plan_id);
											batchGradeBo.getObjectInfoMap(plan_id);
											if(objectTotalScoreMap==null)
												objectTotalScoreMap=batchGradeBo.getObjectTotalScore(Integer.parseInt(plan_id),this.userView.getA0100(),template_id,this.userView);
											for(int i=0;i<userid.length;i++)
											{											
												String scoreAndOrder = (String)objectTotalScoreMap.get(userid[i]);											
												String score = scoreAndOrder.substring(0,scoreAndOrder.indexOf("/"));											
												if(("2".equals(flag) || "8".equals(flag)) && gradeScopeList!=null && gradeScopeList.size()>0)
												{
													String str = "select body_id,a0101 from per_object where plan_id=" + plan_id + " and object_id = '" + userid[i] + "'";			        
											        this.frowset = dao.search(str);
											        String body_id = "";
											        String objectName = "";
												    if(this.frowset.next())	
												    {
												    	body_id = this.frowset.getString("body_id");
												    	objectName = this.frowset.getString("a0101");
												    }
												    
												    if(body_id!=null && body_id.trim().length()>0)
												    {
														for (int k = 0; k < gradeScopeList.size(); k++)
														{
														    LazyDynaBean bean = (LazyDynaBean) gradeScopeList.get(k);
														    String bodyId = (String) bean.get("BodyId");
														    String upScope = (String) bean.get("UpScope");
														    String downScope = (String) bean.get("DownScope");						    
														    if(body_id.equalsIgnoreCase(bodyId))
														    {
														    	if((downScope!=null && downScope.trim().length()>0) && (Double.parseDouble(score)<Double.parseDouble(downScope)))
														    	{
														    		dao.update("update per_mainbody set status=1 where plan_id="+plan_id+" and object_id='"+userid[i]+"' and mainbody_id='"+this.userView.getA0100()+"' ");
														    		//throw (new GeneralException(ResourceFactory.getProperty("您对"+ objectName +"的评分低于评分下限值"+ downScope +"！")));
														    		info="您对"+ objectName +"的评分低于评分下限值"+ downScope +"！";
														    	}
														    	else if((upScope!=null && upScope.trim().length()>0) && (Double.parseDouble(score)>Double.parseDouble(upScope)))
														    	{
														    		dao.update("update per_mainbody set status=1 where plan_id="+plan_id+" and object_id='"+userid[i]+"' and mainbody_id='"+this.userView.getA0100()+"' ");
														    		//throw (new GeneralException(ResourceFactory.getProperty("您对"+ objectName +"的评分高于评分上限值"+ upScope +"！")));	
														    		info="您对"+ objectName +"的评分高于评分上限值"+ upScope +"！";
														    	}
														    	break;
														    }
														}
												    }
												}											
												score_order.append("#"+(String)objectTotalScoreMap.get(userid[i]));
												//将总分塞入per_mainbody中
												//dao.update("update per_mainbody set score='"+score+"' where plan_id="+Integer.parseInt(plan_id)+"  and mainbody_id='"+this.userView.getA0100()+"' and object_id='"+userid[i]+"'");
											}
										}
										
										// 提交时把待办置为已办   zhaoxg add 2014-8-29
										if("2".equals(flag))
										{
											PendingTask pt = new PendingTask();
											LazyDynaBean bean = new LazyDynaBean();
											String spByBodySeq = (String)htxml.get("mitiScoreMergeSelfEval"); // 多人打分时同时显示自我评价
											if("true".equalsIgnoreCase(spByBodySeq)) {
												bean.set("oper", "start");
												bean.set("sql", "select plan_id from per_mainbody where plan_id=" + plan_id + " and mainbody_id='" + userView.getA0100() + "' and  "+ Sql_switcher.isnull("status", "0")+"<>4 and  "+ Sql_switcher.isnull("status", "0")+"<>2 and  "+ Sql_switcher.isnull("status", "0")+"<>7");
												LazyDynaBean _bean=PerformanceImplementBo.updatePendingTask(this.getFrameconn(), this.userView,"Usr"+this.userView.getA0100(),plan_id,bean,"2");
												if("update".equals(_bean.get("selfflag"))){
													pt.updatePending("P","PER"+_bean.get("selfpending_id"), 1, "评分已提交！",this.userView);
												}											
											}else{
												bean.set("oper", "start");
												bean.set("sql", "select plan_id from per_mainbody where plan_id=" + plan_id + " and mainbody_id<>object_id and mainbody_id='" + userView.getA0100() + "' and  "+ Sql_switcher.isnull("status", "0")+"<>4 and  "+ Sql_switcher.isnull("status", "0")+"<>2 and  "+ Sql_switcher.isnull("status", "0")+"<>7");
												LazyDynaBean _bean=PerformanceImplementBo.updatePendingTask(this.getFrameconn(), this.userView,"Usr"+this.userView.getA0100(),plan_id,bean,"2");
												if("update".equals(_bean.get("selfflag"))){
													pt.updatePending("P","PER"+_bean.get("selfpending_id"), 1, "评分已提交！",this.userView);
												}	
								            	String sql = "select plan_id from per_mainbody where plan_id=" + plan_id + " and  mainbody_id='" + userView.getA0100() +"' and object_id='"+userView.getA0100()+"'";
								            	RowSet rowSet = dao.search(sql);
								            	if (rowSet.next()){
								            		String urlNew = "/selfservice/performance/selfGrade.do?b_query=link&returnflag=8&bint=int&model=0&plan_id_db="+plan_id;
								            		String ext_flag = "PERPF_"+plan_id+"_SELF";
								            		String upSql = "update t_hr_pendingTask set pending_url='"+urlNew+"' where receiver='"+"Usr"+userView.getA0100()+"' and ext_flag='"+ext_flag+"'";
								            		dao.update(upSql);
								            		//更新第三方代办状态
								            		HashMap map=PerformanceImplementBo.isHavePendingtask("Usr"+userView.getA0100(),this.frameconn,ext_flag);
								            		if(map.containsKey("pending_id")) {
								            			String pending_id = (String)map.get("pending_id");
								            			pt.updatePending("P","PER"+pending_id, 1, "评分已提交！",this.userView);
								            		}
								     		    }
								            }
//											String pendingCode = getPendingCode(plan_id,this.userView.getA0100());			
//											if(pendingCode!=null && pendingCode.trim().length()>0)
//											{				
//												pt.updatePending("P", pendingCode, 1, "计划打分",this.userView);
//											}										
										}
									}
								}
								else if("2".equals(isSuccess))
									info=ResourceFactory.getProperty("lable.performance.saveOtherInfo")+"!";
								else if("0".equals(isSuccess)&&batchGradeBo.getError_info().length()>0)
									info=batchGradeBo.getError_info();
							}
						}
						else
						{
							info=a_info1;
							String isAutoCountTotalOrder=batchGradeBo.getIsAutoCountTotalOrder();
							if("3".equals(flag))
								isAutoCountTotalOrder="true";
							if(("true".equalsIgnoreCase(isAutoCountTotalOrder) && ("true".equalsIgnoreCase(batchGradeBo.getIsShowTotalScore())|| "true".equalsIgnoreCase(batchGradeBo.getIsShowOrder())))
									|| (gradeScopeList!=null && gradeScopeList.size()>0))
							{
								HashMap objectTotalScoreMap=batchGradeBo.getObjectTotalScore(Integer.parseInt(plan_id),this.userView.getA0100(),template_id,this.userView);
								for(int i=0;i<userid.length;i++)
								{
									String scoreAndOrder = (String)objectTotalScoreMap.get(userid[i]);											
									String score = scoreAndOrder.substring(0,scoreAndOrder.indexOf("/"));											
									if(("2".equals(flag) || "8".equals(flag)) && gradeScopeList!=null && gradeScopeList.size()>0)
									{
										String str = "select body_id,a0101 from per_object where plan_id=" + plan_id + " and object_id = '" + userid[i] + "'";			        
								        this.frowset = dao.search(str);
								        String body_id = "";
								        String objectName = "";
									    if(this.frowset.next())	
									    {
									    	body_id = this.frowset.getString("body_id");
									    	objectName = this.frowset.getString("a0101");
									    }
									    
									    if(body_id!=null && body_id.trim().length()>0)
									    {
											for (int k = 0; k < gradeScopeList.size(); k++)
											{
											    LazyDynaBean bean = (LazyDynaBean) gradeScopeList.get(k);
											    String bodyId = (String) bean.get("BodyId");
											    String upScope = (String) bean.get("UpScope");
											    String downScope = (String) bean.get("DownScope");						    
											    if(body_id.equalsIgnoreCase(bodyId))
											    {
											    	if((downScope!=null && downScope.trim().length()>0) && (Double.parseDouble(score)<Double.parseDouble(downScope)))
											    	{
											    		dao.update("update per_mainbody set status=1 where plan_id="+plan_id+" and object_id='"+userid[i]+"' and mainbody_id='"+this.userView.getA0100()+"' ");
											    		//throw (new GeneralException(ResourceFactory.getProperty("您对"+ objectName +"的评分低于评分下限值"+ downScope +"！")));
											    		info="您对"+ objectName +"的评分低于评分下限值"+ downScope +"！";
											    	}
											    	else if((upScope!=null && upScope.trim().length()>0) && (Double.parseDouble(score)>Double.parseDouble(upScope)))
											    	{
											    		dao.update("update per_mainbody set status=1 where plan_id="+plan_id+" and object_id='"+userid[i]+"' and mainbody_id='"+this.userView.getA0100()+"' ");
											    		//throw (new GeneralException(ResourceFactory.getProperty("您对"+ objectName +"的评分高于评分上限值"+ upScope +"！")));	
											    		info="您对"+ objectName +"的评分高于评分上限值"+ upScope +"！";
											    	}
											    	break;
											    }
											}
									    }
									}
									score_order.append("#"+(String)objectTotalScoreMap.get(userid[i]));
									//将总分塞入per_mainbody中
									//dao.update("update per_mainbody set score='"+score+"' where plan_id="+Integer.parseInt(plan_id)+"  and mainbody_id='"+this.userView.getA0100()+"' and object_id='"+userid[i]+"'");
								}
							}
						}	
					}else
						info=aa_info;
				}else
				{
					info=a_info;
				}					
			}else
				info=info0;							
			
			
			// 向根据计算公式计算出总体评价的临时表中写入指标分数 JinChunhai 2012.11.13	
			String totalAppValue = "";
			if("true".equalsIgnoreCase((String)htxml.get("WholeEval")) && totalAppFormula!=null && totalAppFormula.trim().length()>0)
			{				
				StringBuffer objs = new StringBuffer("");				
				HashMap objectTotalScoreMap=batchGradeBo.getObjectTotalScore(Integer.parseInt(plan_id),this.userView.getA0100(),template_id,this.userView);
				for(int i=0;i<userid.length;i++)
				{
					objs.append(",'"+userid[i]+"'");
					String scoreAndOrder = (String)objectTotalScoreMap.get(userid[i]);											
					String score = scoreAndOrder.substring(0,scoreAndOrder.indexOf("/"));
					
					batchGradeBo.setTempWholeEvalTableScore(userid[i],this.userView.getA0100(),score);						
				}
				if(objs!=null && objs.toString().trim().length()>0)
				{
					totalAppValue = batchGradeBo.getFormulaSqlToValue(this.userView,objs.substring(1),this.userView.getA0100(),totalAppFormula,EvalClass,userid);
				//	totalAppValue += "#"+wholeEval;
				}
			}			
			this.getFormHM().put("totalAppValue",SafeCode.encode(totalAppValue));
			this.getFormHM().put("info",SafeCode.encode(info));
			this.getFormHM().put("flag",flag);
			if(score_order.length()>0)
				this.getFormHM().put("score_order",score_order.substring(1));
			else
				this.getFormHM().put("score_order","");
			if("2".equals(flag))  //提示 可执行状态下的考核计划 还有那些没提交
			{
				if(linkType!=null&& "liantong".equalsIgnoreCase(linkType)&&!(SystemConfig.getPropertyValue("clientName")!=null&& "bjga".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName"))))
				{
					this.getFormHM().put("otherInfo","");
				}
				else
				{	
					String _str=SafeCode.encode(getOtherSubInof(batchGradeBo));
					this.getFormHM().put("otherInfo",_str);
					if("2".equals(flag)&&_str.trim().length()==0&&(SystemConfig.getPropertyValue("clientName")!=null&& "bjga".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName"))))
					{
						if(info.equals(ResourceFactory.getProperty("kq.register.daily.success")+"!"))
							this.getFormHM().put("info","评议已完成，感谢您的参与!");
					}
				}
			}
		}
		catch(Exception e)
		{
		//	e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
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
	
	public String getOtherSubInof(BatchGradeBo batchGradeBo)
	{
		String info="";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList dblist=new ArrayList();
		try
		{
//			 得到绩效考核计划列表
			String perPlanSql = "select plan_id,name,status from per_plan where Method=1 and ( status=4 or status=6 )  and (busitype is null or busitype<>'1') ";
			if (!userView.isSuper_admin())
				perPlanSql += "and plan_id in (select plan_id from per_mainbody where mainbody_id='"
						+ userView.getA0100() + "' )";
			perPlanSql += " order by " + Sql_switcher.isnull("a0000", "999999999") + " asc,plan_id desc ";
			this.frowset = dao.search(perPlanSql);	
			
			while (this.frowset.next()) 
			{
				String name = this.getFrowset().getString("name");
				String plan_id = this.getFrowset().getString("plan_id");
				CommonData vo = new CommonData(plan_id, name);
				dblist.add(vo);
			}
			
			info=batchGradeBo.getOtherGradeStatus(dblist,this.getUserView().getA0100());
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return info;
	}
	
	

//	将考核对象文档记录设为提交状态
	public void setArticleState(String plan_id,String  objectids,BatchGradeBo batchGradeBo)
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RecordVo vo=new RecordVo("per_plan");
			vo.setInt("plan_id",Integer.parseInt(plan_id));
			vo=dao.findByPrimaryKey(vo);
			int object_type=vo.getInt("object_type");
			boolean flag=false;
			if(object_type==2&&objectids.indexOf("'"+this.getUserView().getA0100()+"'")!=-1)			
			{
				flag=true;
			//	dao.update("update per_article set state=1 where    plan_id="+plan_id+" and a0100='"+this.getUserView().getA0100()+"'   and lower(nbase)='usr'");
			}
			else
			{
			//	this.getUserView().getA0100()
				
				String _str=" pms.level";
				if(Sql_switcher.searchDbServer()==Constant.ORACEL)
					_str=" pms.level_o";
				
				String sql="select pmb.mainbody_id from per_mainbody pmb,per_mainbodyset pms where pmb.body_id=pms.body_id and "+_str+"=5 and pmb.mainbody_id='"+this.getUserView().getA0100()+"'"
					+" and  pmb.plan_id="+plan_id+" and pmb.object_id in ("+objectids+")";
				this.frowset=dao.search(sql);
				if(this.frowset.next())
				{
					flag=true;
					// dao.update("update per_article set state=1 where    plan_id="+plan_id+" and a0100='"+this.getUserView().getA0100()+"'   and lower(nbase)='usr'");
				}
			}
			if(flag)
			{ 
				batchGradeBo.setArticleState(plan_id,this.getUserView().getA0100(),"usr",1);
				batchGradeBo.setArticleState(plan_id,this.getUserView().getA0100(),"usr",2);
			}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	//是否可提交 
	public boolean isSub(String context,String plan_id,String a0100,String nbase,int article_type)
	{
		boolean flag=true;
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RowSet rowSet=dao.search("select count(*) from per_article where article_type="+article_type+" and  plan_id="+plan_id+" and fileflag=2 and a0100='"+a0100+ "' and lower(nbase)='"+nbase.toLowerCase()+"'");
			int n=0;
			if(rowSet.next())
				n=rowSet.getInt(1);
			if(n==0&&(context==null||context.trim().length()==0))
				flag=false;
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;
	}
	
	
	
	

}
