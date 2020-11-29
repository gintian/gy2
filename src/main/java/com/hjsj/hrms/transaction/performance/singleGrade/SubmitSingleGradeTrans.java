package com.hjsj.hrms.transaction.performance.singleGrade;

import com.hjsj.hrms.businessobject.performance.PerEvaluationBo;
import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.singleGrade.SingleGradeBo;
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
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 *<p>Title:SubmitSingleGradeTrans.java</p> 
 *<p>Description:保存或提交自我评分/单人考评</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-06-09 10:15:35</p> 
 *@author Administrator
 *@version 5.0
 */

public class SubmitSingleGradeTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String errorPrefix = "";
			String clientname = SystemConfig.getPropertyValue("clientName");
			if("gs".equalsIgnoreCase(clientname)){
				String plan_name=(String)this.getFormHM().get("plan_name");
				plan_name = PubFunc.keyWord_reback(plan_name);
				int index = plan_name.lastIndexOf("(");
				if(index!=-1)
					plan_name = plan_name.substring(0,index);
				String object_name=(String)this.getFormHM().get("object_name");
				errorPrefix = object_name+" 在 "+plan_name+"计划下 ";
			}
			String object_id=(String)this.getFormHM().get("object_id");
			String templateId=(String)this.getFormHM().get("templateId");
			String scoreflag=(String)this.getFormHM().get("scoreflag");
			String userValue=(String)this.getFormHM().get("userValue");
			String plan_id=(String)this.getFormHM().get("plan_id");
			CheckPrivSafeBo _bo = new CheckPrivSafeBo(this.frameconn,this.userView);
			boolean isPriv = _bo.isPlanIdPriv(plan_id);
			if(!isPriv){
				return;
			}
			ArrayList appraiseArrayList = (ArrayList)this.getFormHM().get("appraiseArrayList");			
			SingleGradeBo singleGradeBo=new SingleGradeBo(this.frameconn,plan_id);
			
			if(this.getUserView().getA0100()==null)
				return;
			String mainBodyId=this.getUserView().getA0100();
			String flag=(String)this.getFormHM().get("flag");// 1保存  2提交
			String nodeKnowDegree=(String)this.getFormHM().get("nodeKnowDegree");
			String wholeEval=(String)this.getFormHM().get("wholeEval");
			LoadXml loadxml = new LoadXml(this.getFrameconn(), plan_id);
			Hashtable params = loadxml.getDegreeWhole();
			String wholeEvalFlag = (String) params.get("WholeEvalMode");
			String limitation=(String)this.getFormHM().get("limitation");
			String nodeKnowDegree_value=(String)this.getFormHM().get("nodeKnowDegree_value");
			String wholeEval_value=(String)this.getFormHM().get("wholeEval_value");
			String gradeClass=(String)this.getFormHM().get("gradeClass");
			String scoreBySumup=(String)this.getFormHM().get("scoreBySumup");
			String status=(String)this.getFormHM().get("status");
			String wholeEvalScore = (String)this.getFormHM().get("wholeEvalScore");//总体评价分值
			String info=" ";
			String score="0";
			
			String autoScore="0";  //是否是自动保存
			if(this.getFormHM().get("autoScore")!=null)
				autoScore=(String)this.getFormHM().get("autoScore");
						
			BatchGradeBo batchGradeBo=new BatchGradeBo(this.getFrameconn(),plan_id);
			String showOneMark=batchGradeBo.getShowOneMark();
			batchGradeBo.getDynaRankInfoMap(plan_id);
			batchGradeBo.getObjectInfoMap(plan_id);
			
			boolean isByModelFlag  = SingleGradeBo.getByModel(plan_id,this.getFrameconn());
			
			 HashMap pointMap = new HashMap();
			 //兼容能力素质支持一个评估计划适应多个岗位进行评估
			 if(isByModelFlag && SingleGradeBo.isHaveMatchByModel(object_id, this.getFrameconn())){
				 pointMap = batchGradeBo.getCompetencyPointprivMap(String.valueOf(plan_id),this.userView.getA0100());
			 }else{
				 pointMap=batchGradeBo.getPointprivMap(String.valueOf(plan_id),this.userView.getA0100());   //得到指标权限信息
			 }
			HashMap a_pointmap=(HashMap)pointMap.get(object_id);					
			
			batchGradeBo.setNoShowOneMark(true);
			batchGradeBo.setLoadStaticValue(true);
			//得到具有某考核对象的指标权限map	
			batchGradeBo.setObject_id(object_id);
			ArrayList pointList=batchGradeBo.getPerPointList(templateId,plan_id);		
			ArrayList _list=(ArrayList)pointList.get(1);
			StringBuffer newUserValue=new StringBuffer("");
			String[] temps=userValue.replaceAll("／", "/").split("/");
			int j=0;
			 
			String oneMardStr="";
			if("false".equalsIgnoreCase(showOneMark))  //不显示统一评分指标
			{
				oneMardStr=singleGradeBo.getOneMarkPointStr(templateId);
			}
			 
			if(temps!=null && temps.length>0)
			{
				for(int i=0;i<_list.size();i++)
				{
					String[] temp=(String[])_list.get(i);
					if(a_pointmap.get(temp[0])!=null&& "1".equals((String)a_pointmap.get(temp[0])))
					{					 
						if("false".equalsIgnoreCase(showOneMark))  //不显示统一评分指标
						{
							if(oneMardStr.toLowerCase().indexOf("'"+temp[0].toLowerCase()+"'")==-1)
							{
								newUserValue.append("/"+temps[j]);
							 	j++;
							}
							else
								newUserValue.append("/null"); 
						}
						else
						{	 
							newUserValue.append("/"+temps[j]);
						 	j++;
						}					 
					}
					else
						newUserValue.append("/null"); 
				}
				if(newUserValue!=null && newUserValue.toString().trim().length()>0)
					userValue=newUserValue.substring(1);				
			}
			if("true".equals(nodeKnowDegree))
			{
				if(nodeKnowDegree_value.trim().length()==0)
					nodeKnowDegree_value="null";
				userValue+="/"+nodeKnowDegree_value;
			
			}
			if("true".equals(wholeEval)&&"0".equals(wholeEvalFlag))
			{
				if(wholeEval_value.trim().length()==0)
					wholeEval_value="null";
				userValue+="/"+wholeEval_value;
			} else if("true".equals(wholeEval)&&"1".equals(wholeEvalFlag)){
				userValue+="/"+null;
			}
			
			if(!"-1".equals(limitation)&&("2".equals(flag)|| "8".equals(flag)))
			{
				info=singleGradeBo.isOverLimitation(gradeClass,limitation,userValue,plan_id,mainBodyId,object_id,templateId,scoreflag,wholeEval,wholeEval_value);				
			}
			String KeepDecimal="0";
			
			
			Hashtable htxml=new Hashtable();
			//LoadXml loadxml = new LoadXml(this.getFrameconn(),plan_id);  // batchGradeBo.getLoadxml();
			ArrayList gradeScopeList = loadxml.getPerGradeScopeList("ScoreScope");	 // 得到主体评分范围所有设置	
			htxml=loadxml.getDegreeWhole();
			String performanceType=(String)htxml.get("performanceType");  // 考核形式 0：绩效考核 1：民主评测 
			if(info.indexOf("不")==-1)
			{				
				String[] userid={object_id};
				HashMap usersValue=new HashMap();
				usersValue.put(object_id,userValue);	
								
			//	if(loadxml==null)
			//		loadxml=new LoadXml(this.getFrameconn(),plan_id);
				
				HashMap fineMaxMap=(HashMap)htxml.get("fineMaxMap");
				String WholeEval=(String)htxml.get("WholeEval");
				String wholeEvalMode = (String)htxml.get("WholeEvalMode");//总体评价采集方式: 0-录入等级，1-录入分值
				String fineMax=(String)htxml.get("fineMax");
				String bFineRestrict=(String)htxml.get("FineRestrict");
				KeepDecimal=(String)htxml.get("KeepDecimal");  //小数位	
				HashMap BadlyMap=(HashMap)htxml.get("BadlyMap");
				String BadlyRestrict=(String)htxml.get("BadlyRestrict");
				String BadlyMax=(String)htxml.get("BadlyMax");
				String isShowSubmittedScores=(String)htxml.get("isShowSubmittedScores");  //提交后的分数是否显示
				String isShowTotalScore=(String)htxml.get("ShowTotalScoreSort"); // 显示总分
				 
				String showDeductionCause=(String)htxml.get("showDeductionCause");        //显示扣分原因(Ture, False(默认))
				String MustFillCause=(String)htxml.get("MustFillCause");                  //评分说明是否必填
				ArrayList MustFillOptionsList=(ArrayList)htxml.get("MustFillOptionsList"); // 评分说明必填高级设置	
				String SameAllScoreNumLess = (String)htxml.get("SameAllScoreNumLess");// 总分相同的人数不能等于和多余=0不控制，在0-1之间，按百分比控制，比1大按个数控制，
				 
				 //判读是否定义了描述性评议项   陈旭光 2014-12-20
	            PerEvaluationBo bo =new PerEvaluationBo(this.frameconn, plan_id, ""); 
	           
				// 保存
				if("1".equals(performanceType) && bo.isProAppraise())
				{
					batchGradeBo.SetDescription(plan_id,object_id,this.userView.getA0100(),appraiseArrayList);
				}
				String aa_info="";
				if(!"False".equals(bFineRestrict)&& "2".equals(flag))
					aa_info=batchGradeBo.validateMaxvalueNum(fineMax,object_id,gradeClass,templateId,plan_id,usersValue,userid,fineMaxMap,batchGradeBo.getObjectNums(this.userView.getA0100(),plan_id),this.userView.getA0100(),WholeEval,wholeEval_value,1);
				else
					aa_info="success";
				if("success".equals(aa_info))
				{
					if(!"False".equals(BadlyRestrict)&& "2".equals(flag))
						aa_info=batchGradeBo.validateMaxvalueNum(BadlyMax,object_id,gradeClass,templateId,plan_id,usersValue,userid,BadlyMap,batchGradeBo.getObjectNums(this.userView.getA0100(),plan_id),this.userView.getA0100(),WholeEval,wholeEval_value,2);
					else
						aa_info="success";
				}
				if("success".equals(aa_info))
				{				
					String a_info=batchGradeBo.validateScore(templateId,plan_id,usersValue,userid);
					
					if("success".equals(a_info))
					{
						String a_info1="";
					    if("2".equals(flag)&& "True".equalsIgnoreCase(scoreBySumup))  //如果是提交，并且设置了 个人总结没填写，主体为其打分时不能提交
					    {
					    	a_info1=batchGradeBo.isWriteSummary("'"+object_id+"'",plan_id);
					    	if(!StringUtils.isEmpty(a_info1)){
					    		String message = a_info1.replace("<br>", "");
					    		throw (new GeneralException(message));
					    	}
					    }
					    
					    //总体评价必填判断
					    if("2".equals(flag)&&a_info1.trim().length()==0)
					    {
					    	 
							if("0".equals(wholeEvalMode)&&("true".equalsIgnoreCase((String)htxml.get("WholeEval"))|| "true".equalsIgnoreCase((String)htxml.get("DescriptiveWholeEval"))))  //总体评价 or 总体评价描述
							{
								if("True".equalsIgnoreCase((String)htxml.get("MustFillWholeEval"))) //总体评价必填
								{
									String whole_id=wholeEval_value; 
									a_info1=validateWholeEval(this.getUserView().getA0100(),object_id,plan_id,whole_id,htxml); 
								}
							} 
					    }					    					    
					    
						if(a_info1.trim().length()==0)
						{
							//保存总体评价分值
							dao.update("update per_mainbody set whole_score="+wholeEvalScore+" where plan_id="+plan_id+" and object_id='"+object_id+"' and mainbody_id='"+this.userView.getA0100()+"'");
							String info0="";
							if("2".equals(flag)&&(!"true".equalsIgnoreCase(batchGradeBo.getDynaBodyToPoint())))
								info0=batchGradeBo.isMustScore(userid, this.userView.getA0100(), plan_id, usersValue);
							if(info0.length()==0)
							{
								info=batchGradeBo.insertGradeResult(userid,flag,plan_id,templateId,usersValue,mainBodyId,status,scoreflag);
								if("0".equals(info)&&batchGradeBo.getError_info().length()>0)
									throw (new GeneralException(errorPrefix+ResourceFactory.getProperty(batchGradeBo.getError_info())));	
								
								score = String.valueOf(batchGradeBo.getObjectTotalScore(Integer.parseInt(plan_id),mainBodyId,templateId,object_id,this.userView));
								// 在此把主体给对象打的总分计算出来后写到 per_mainbody 表的 score 字段中 	JinChunhai 2012.07.05
								if("2".equals(flag) && (SameAllScoreNumLess!=null && SameAllScoreNumLess.trim().length()>0 && !"0".equals(SameAllScoreNumLess)))
								{
									
									score=PubFunc.round(score,Integer.parseInt(KeepDecimal));
									dao.update("update per_mainbody set score='"+score+"' where plan_id="+Integer.parseInt(plan_id)+"  and mainbody_id='"+mainBodyId+"' and object_id='"+object_id+"'");	
																									 								
									// 控制总分相同对象个数，								
								    String errorInfo = batchGradeBo.validateSameScoreAllNumLess(plan_id,object_id,mainBodyId,flag,SameAllScoreNumLess);
								    if(errorInfo.length()>0){
								    	dao.update("update per_mainbody set status=1 where plan_id="+plan_id+" and object_id='"+object_id+"' and mainbody_id='"+this.userView.getA0100()+"' ");
								    	throw (new GeneralException(errorPrefix+ResourceFactory.getProperty(errorInfo)));	
								    }
								}
								if("2".equals(flag)&& "True".equalsIgnoreCase(showDeductionCause)&& "True".equalsIgnoreCase(MustFillCause))
								{
									/* <MustFillOptions>
								       // Flag：高于Up  低于Down   IsValid: 是否有效，默认为 False 
								      	  <MustFillOption Flag="Up" IsValid="True" DegreeId="A" />
									      <MustFillOption Flag="Down"  IsValid="True" DegreeId="A" />
								         </MustFillOptions>  JinChunhai 2011.11.11
								    */	
									boolean isNull=false;	
									if(MustFillOptionsList!=null && MustFillOptionsList.size()>0)
									{
										for(int i=0;i<MustFillOptionsList.size();i++)
										{
											LazyDynaBean abean = (LazyDynaBean)MustFillOptionsList.get(i);										
											String IsValid=(String)abean.get("IsValid");
                                            String DegreeId=(String)abean.get("DegreeId");
                                            String Flag=(String)abean.get("Flag");
                                            //是高级规则的，如果没有设置标度则还是认为是无效规则。 haosl 2019年6月24日
                                            if(!"Required".equalsIgnoreCase(Flag) && "true".equalsIgnoreCase(IsValid) && StringUtils.isNotBlank(DegreeId))
												isNull=true;
										}																		
									}
									if(MustFillOptionsList!=null && MustFillOptionsList.size()>0 && isNull)
									{
										// 查询计划类别
										int method = 0;
										String methodSql = "select method from per_plan where plan_id=" + plan_id;
										frowset = dao.search(methodSql);
										if (frowset.next()) {
											method = frowset.getInt("method");
										}
										
										StringBuffer sql_buf = new StringBuffer();
										sql_buf.append("select pt.reasons,pp.pointname from per_table_"+plan_id+" pt,per_point pp,per_template_point ptp,per_template_item pi ");
										sql_buf.append(" where pi.item_id=ptp.item_id ");
										sql_buf.append(" and pp.point_id=pt.point_id and ptp.point_id=pt.point_id and template_id='"+ templateId +"' ");
										sql_buf.append(" and pt.object_id='"+object_id+"'");
										sql_buf.append(" and pt.mainbody_id='"+this.userView.getA0100()+"'");
										sql_buf.append(" and ( (1=2 ");
										for(int i=0;i<MustFillOptionsList.size();i++)
										{
											LazyDynaBean abean = (LazyDynaBean)MustFillOptionsList.get(i);
											String Flag=(String)abean.get("Flag");
											String IsValid=(String)abean.get("IsValid");
											String DegreeId=(String)abean.get("DegreeId");
											if("Up".equalsIgnoreCase(Flag)&& "true".equalsIgnoreCase(IsValid))
											{
												sql_buf.append(" or UPPER(pt.degree_id)<'"+DegreeId.toUpperCase()+"'");
												continue;
											}else if("Down".equalsIgnoreCase(Flag)&& "true".equalsIgnoreCase(IsValid)){
												sql_buf.append(" or UPPER(pt.degree_id)>'"+DegreeId.toUpperCase()+"'");
												continue;
											}
										}
										sql_buf.append(" ) ") ;
										
										if (method == 1) {
											for(int i=0;i<MustFillOptionsList.size();i++) {
												LazyDynaBean abean = (LazyDynaBean)MustFillOptionsList.get(i);
												String Flag=(String)abean.get("Flag");
												String IsValid=(String)abean.get("IsValid");
												String DegreeId="";	
												
													// 等于多少分无需说明
													if ("Exclude".equalsIgnoreCase(Flag) && "true".equalsIgnoreCase(IsValid)) {
														DegreeId=(String)abean.get("DegreeId");	
														if(DegreeId!=null&&DegreeId.trim().length()>0)
														{
															sql_buf.append(" and UPPER(pt.degree_id)<>'" + DegreeId.toUpperCase()+"'"); 
														}
														continue;
													}
													// 必填指标
													if ("Required".equalsIgnoreCase(Flag) && "true".equalsIgnoreCase(IsValid)) {
														DegreeId=(String)abean.get("PointId");	
														if (!"".equals(DegreeId)) {
															String[] ids = DegreeId.split(",");
															sql_buf.append(" and pt.point_id in(");
															for (int k = 0; k < ids.length; k++) {
																String id = ids[k].substring(2);
																sql_buf.append("'").append(id).append("',");
															}
															sql_buf.replace(sql_buf.length() - 1, sql_buf.length(), ")");
														}
													}
												
											}
										}
										sql_buf.append(") and "+Sql_switcher.length("pt.degree_id")+">0");
										sql_buf.append(" order by ptp.seq ");
										this.frowset=dao.search(sql_buf.toString());
										StringBuffer sqlStr = new StringBuffer();
										StringBuffer strSql = new StringBuffer();
										sqlStr.append("考核指标：\n\n");
										while(this.frowset.next())
										{
											String reasons = Sql_switcher.readMemo(this.frowset,"reasons");
											String pointname = this.frowset.getString("pointname");
											if(reasons==null || reasons.trim().length()<=0)
								    	    {												
												strSql.append("    "+pointname+"； \n");																								
								    		}
										}
										sqlStr.append(strSql.toString());
										sqlStr.append("   \n必须填写评分说明！");
										if(strSql.toString().trim().length()>0)
										{
											dao.update("update per_mainbody set status=1 where plan_id="+plan_id+" and object_id='"+object_id+"' and mainbody_id='"+this.userView.getA0100()+"' ");
											throw (new GeneralException(errorPrefix+ResourceFactory.getProperty(sqlStr.toString())));
										}
									}																		
									else
									{									
										this.frowset=dao.search("select reasons from per_table_"+plan_id+" where object_id='"+object_id+"' and mainbody_id='"+this.userView.getA0100()+"' ");									
										while(this.frowset.next())
										{
											String reasons=Sql_switcher.readMemo(this.frowset,"reasons");
											if(reasons==null || reasons.trim().length()<=0)
											{
												isNull=true;
												break;
											}
										}
										if(isNull)
										{
											dao.update("update per_mainbody set status=1 where plan_id="+plan_id+" and object_id='"+object_id+"' and mainbody_id='"+this.userView.getA0100()+"' ");
											throw (new GeneralException(errorPrefix+ResourceFactory.getProperty("请填写评分说明!")));
										}
									}																											
								}																								
								String operate="";
								if("1".equals(flag))
									operate=ResourceFactory.getProperty("button.temporary.save");
								else
									operate=ResourceFactory.getProperty("lable.welcomeinv.sumbit");
								if("1".equals(info))
								{
									info=operate+ResourceFactory.getProperty("lable.performance.success")+"！";
									
									if("2".equals(flag)) //绩效报告和绩效目标有文本内容或附件，在提交评分时把绩效报告和绩效目标一起提交
									{
											setArticleState(plan_id,object_id,"usr",batchGradeBo);
									}																											
									
									this.getFormHM().put("BlankScoreOption", batchGradeBo.getBlankScoreOption());
									
									if("true".equalsIgnoreCase(isShowTotalScore))
									{
										if("2".equals(flag) && "false".equalsIgnoreCase(isShowSubmittedScores) && (gradeScopeList==null || gradeScopeList.size()<=0))
										{
											score="*";
										}
										else
											score=String.valueOf(batchGradeBo.getObjectTotalScore(Integer.parseInt(plan_id),mainBodyId,templateId,object_id,this.userView));
										
									}else if(gradeScopeList!=null && gradeScopeList.size()>0)
									{
										score=String.valueOf(batchGradeBo.getObjectTotalScore(Integer.parseInt(plan_id),mainBodyId,templateId,object_id,this.userView));
									}
									
								/*	if(flag.equals("2")&&object_id.equalsIgnoreCase(this.userView.getA0100()))
									{
										batchGradeBo.setArticleState(plan_id,object_id,"usr");
									}*/
									
									// 提交时把待办置为已办  2011.06.28 JinChunhai
									if("2".equals(flag))
									{	
										//自我评分
										String mitiScoreMergeSelfEval = (String) htxml.get("mitiScoreMergeSelfEval"); // 显示自我评价
										if("false".equalsIgnoreCase(mitiScoreMergeSelfEval)) {
											if(object_id.equals(mainBodyId)) {
							            		String ext_flag = "PERPF_"+plan_id+"_SELF";
							            		String upSql = "update t_hr_pendingTask set pending_status=1 where receiver='"+"Usr"+userView.getA0100()+"' and ext_flag='"+ext_flag+"'";
							            		dao.update(upSql);
							            		String sql = "select pending_id from t_hr_pendingtask  where Pending_type='33' and  pending_status<>1 and ext_flag = '"+ext_flag+"' and Receiver='Usr"+mainBodyId+"'";
							            		this.frowset = dao.search(sql);
							            		if(this.frowset.next()) {
							            			PendingTask pe = new PendingTask();
							            			pe.deletePending("P", "PER"+this.frowset.getString("pending_id"), 1, "清除分数");
							            		}
											}else {
												PendingTask pt = new PendingTask();
												LazyDynaBean bean = new LazyDynaBean();
												bean.set("oper", "start");
												bean.set("sql", "select plan_id from per_mainbody where plan_id=" + plan_id + " and mainbody_id<>object_id and  mainbody_id='" + userView.getA0100() + "' and  "+ Sql_switcher.isnull("status", "0")+"<>4 and  "+ Sql_switcher.isnull("status", "0")+"<>2 and  "+ Sql_switcher.isnull("status", "0")+"<>7");
												LazyDynaBean _bean=PerformanceImplementBo.updatePendingTask(this.getFrameconn(), this.userView,"Usr"+this.userView.getA0100(),plan_id,bean,"2");
												if("update".equals(_bean.get("selfflag"))){
													pt.updatePending("P","PER"+_bean.get("selfpending_id"), 1, "评分已提交！",this.userView);
		//											pt.updatePending("P","PER"+_bean.get("pending_id"), 1, "计划打分",this.userView);
												}
											}
										}
										else {
											PendingTask pt = new PendingTask();
											LazyDynaBean bean = new LazyDynaBean();
											bean.set("oper", "start");
											bean.set("sql", "select plan_id from per_mainbody where plan_id=" + plan_id + " and  mainbody_id='" + userView.getA0100() + "' and  "+ Sql_switcher.isnull("status", "0")+"<>4 and  "+ Sql_switcher.isnull("status", "0")+"<>2 and  "+ Sql_switcher.isnull("status", "0")+"<>7");
											LazyDynaBean _bean=PerformanceImplementBo.updatePendingTask(this.getFrameconn(), this.userView,"Usr"+this.userView.getA0100(),plan_id,bean,"2");
											if("update".equals(_bean.get("selfflag"))){
												pt.updatePending("P","PER"+_bean.get("selfpending_id"), 1, "评分已提交！",this.userView);
	//											pt.updatePending("P","PER"+_bean.get("pending_id"), 1, "计划打分",this.userView);
											}
										} 
										
//										String signLogo = "true";  // 如果只有自评，（包括团队负责人），即不需要给其它对象评分的标志 true 只有自评  false 不是
//								        String str = "select object_id, mainbody_id, body_id from per_mainbody where plan_id=" + plan_id + " and mainbody_id = '" + mainBodyId + "'";			        
//								        this.frowset = dao.search(str);
//									    while (this.frowset.next())	
//									    {			    	
//									    	String body_id = this.frowset.getString("body_id");
//								    		if((!body_id.equalsIgnoreCase("5")) && (!body_id.equalsIgnoreCase("-1")))
//								    		{
//								    			signLogo = "false";
//								    		}	
//									    }									    
//									    if(signLogo.equalsIgnoreCase("true"))
//										{									    	
//							                String mitiScoreMergeSelfEval = (String) htxml.get("mitiScoreMergeSelfEval"); // 显示自我评价
//											String SelfEvalNotScore=(String)htxml.get("SelfEvalNotScore");  // 自我评价界面不显示打分模板
//									    	
//											if ((mitiScoreMergeSelfEval.equalsIgnoreCase("true")) && (SelfEvalNotScore.equalsIgnoreCase("true")))
//											{
//												
//											}else
//											{
//												PendingTask pt = new PendingTask();
//												String pendingCode = getPendingCode(plan_id,mainBodyId);			
//												if(pendingCode!=null && pendingCode.trim().length()>0)
//												{				
//													pt.updatePending("P", pendingCode, 1, "评分已提交！",this.userView);
//												}
//											}				    							
//										}
									}
								}
								else
									info=operate+ResourceFactory.getProperty("lable.performance.lost")+"！";
							}
							else
								info=info0;
						}
						else
						{
							info=a_info1;
							if("2".equals(flag) && "false".equalsIgnoreCase(isShowSubmittedScores) && (gradeScopeList==null || gradeScopeList.size()<=0))
							{
								score="*";
							}
							else
							{
								score=String.valueOf(batchGradeBo.getObjectTotalScore(Integer.parseInt(plan_id),mainBodyId,templateId,object_id,this.userView));
							}
						}						
					}
					else
						info=a_info;														
				}
				else
					info=aa_info;								
			}
			
			if(!"*".equals(score))
			{
				BigDecimal b = new BigDecimal(score);
				BigDecimal one = new BigDecimal("1");
				score= b.divide(one, Integer.parseInt(KeepDecimal), BigDecimal.ROUND_HALF_UP).toString();								
				
				if("2".equals(flag) && gradeScopeList!=null && gradeScopeList.size()>0)
				{
					String str = "select body_id from per_object where plan_id=" + plan_id + " and object_id = '" + object_id + "'";			        
			        this.frowset = dao.search(str);
			        String body_id = "";
				    if(this.frowset.next())					    			    	
				    	body_id = this.frowset.getString("body_id");
				    
				    if(body_id!=null && body_id.trim().length()>0)
				    {
						for (int i = 0; i < gradeScopeList.size(); i++)
						{
						    LazyDynaBean bean = (LazyDynaBean) gradeScopeList.get(i);
						    String bodyId = (String) bean.get("BodyId");
						    String upScope = (String) bean.get("UpScope");
						    String downScope = (String) bean.get("DownScope");						    
						    if(body_id.equalsIgnoreCase(bodyId))
						    {
						    	if((downScope!=null && downScope.trim().length()>0) && (Double.parseDouble(score)<Double.parseDouble(downScope)))
						    	{
						    		dao.update("update per_mainbody set status=1 where plan_id="+plan_id+" and object_id='"+object_id+"' and mainbody_id='"+this.userView.getA0100()+"' ");
						    		throw (new GeneralException(errorPrefix+ResourceFactory.getProperty("您的评分低于评分下限值"+ downScope +"！")));
						    	}
						    	else if((upScope!=null && upScope.trim().length()>0) && (Double.parseDouble(score)>Double.parseDouble(upScope)))
						    	{
						    		dao.update("update per_mainbody set status=1 where plan_id="+plan_id+" and object_id='"+object_id+"' and mainbody_id='"+this.userView.getA0100()+"' ");
						    		throw (new GeneralException(errorPrefix+ResourceFactory.getProperty("您的评分高于评分上限值"+ upScope +"！")));	
						    	}
						    	break;
						    }
						}
				    }
				}
				dao.update("update per_mainbody set score='"+score+"' where plan_id="+Integer.parseInt(plan_id)+"  and mainbody_id='"+mainBodyId+"' and object_id='"+object_id+"'");
			}
			
			this.getFormHM().put("flag",flag);
			this.getFormHM().put("info",SafeCode.encode(info));
			
			boolean _flag=true;
			if(SystemConfig.getPropertyValue("clientName")!=null&& "bjga".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
				_flag=false;
			if("2".equals(flag)&&isNeedEvaluating(plan_id,this.userView.getA0100()).trim().length()==0&&_flag)
			{
				 
				StringBuffer otherInfo=new StringBuffer("");
				String perPlanSql = "select plan_id,name,status from per_plan where Method=1 and ( status=4 or status=6 ) and plan_id<>"+plan_id+" ";
				if (!userView.isSuper_admin())
					perPlanSql += "and plan_id in (select plan_id from per_mainbody where mainbody_id='"
							+ userView.getA0100() + "' )";
				perPlanSql += " order by " + Sql_switcher.isnull("a0000", "999999999") + " asc,plan_id desc ";
				this.frowset = dao.search(perPlanSql);
				CommonData vo=null;
				ArrayList dblist=new ArrayList();
				while (this.frowset.next()) 
				{
					String name = this.getFrowset().getString("name");
					String planid = this.getFrowset().getString("plan_id");
					vo = new CommonData(planid, name);
					dblist.add(vo);
				}
				
				LoadXml aloadxml=null;
				for(int i=0;i<dblist.size();i++)
				{
					vo=(CommonData)dblist.get(i);
					
					
					if(BatchGradeBo.getPlanLoadXmlMap().get(vo.getDataValue())==null)
					{
						aloadxml = new LoadXml(this.getFrameconn(),vo.getDataValue());
						BatchGradeBo.getPlanLoadXmlMap().put(vo.getDataValue(),aloadxml);
					}
					else
					{
						aloadxml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(vo.getDataValue());
					}
					Hashtable xhtxml = aloadxml.getDegreeWhole();
					String _performanceType=(String)xhtxml.get("performanceType");
					if(performanceType.equals(_performanceType))
					{
						String _str=isNeedEvaluating(vo.getDataValue(),this.userView.getA0100());
						if(_str.length()>0)
							otherInfo.append("#  "+_str);
					}
				}
				if(otherInfo.length()>0)
					this.getFormHM().put("otherInfo",SafeCode.encode(otherInfo.toString()));
				else
					this.getFormHM().put("otherInfo","");
			}
			else
				this.getFormHM().put("otherInfo","");

/*
			if(!score.equals("*"))
			{
				BigDecimal b = new BigDecimal(score);
				BigDecimal one = new BigDecimal("1");
				score= b.divide(one, Integer.parseInt(KeepDecimal), BigDecimal.ROUND_HALF_UP).toString();
			}
*/
			
			// 向根据计算公式计算出总体评价的临时表中写入指标分数 JinChunhai 2012.11.13
			String totalAppFormula =(String)htxml.get("TotalAppFormula"); // 总体评价的计算公式，默认为空
			String totalAppValue = "";
			if("true".equalsIgnoreCase((String)htxml.get("WholeEval")) && totalAppFormula!=null && totalAppFormula.trim().length()>0)
			{
				String totalscore = String.valueOf(batchGradeBo.getObjectTotalScore(Integer.parseInt(plan_id),mainBodyId,templateId,object_id,this.userView));
				batchGradeBo.setTempWholeEvalTableScore(object_id,mainBodyId,totalscore);	
				String[] userid = new String[1];
				userid[0] = object_id;
				totalAppValue = batchGradeBo.getFormulaSqlToValue(this.userView,("'"+object_id+"'"),mainBodyId,totalAppFormula,gradeClass,userid);
			}
			
			this.getFormHM().put("totalAppValue",SafeCode.encode(totalAppValue));
			this.getFormHM().put("score",score);
			this.getFormHM().put("autoScore",autoScore);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	
	public String isNeedEvaluating(String planid,String mainbodyID)
	{
		String str="";
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RecordVo _vo = new RecordVo("per_plan");
			_vo.setInt("plan_id", Integer.parseInt(planid));
			_vo = dao.findByPrimaryKey(_vo);
			LoadXml loadxml=null;
			if(BatchGradeBo.planLoadXmlMap.get(planid)==null)
			{
				loadxml = new LoadXml(this.getFrameconn(),planid);
				BatchGradeBo.planLoadXmlMap.put(planid,loadxml);
			}
			else
				loadxml=(LoadXml)BatchGradeBo.planLoadXmlMap.get(planid);
			Hashtable htxml = new Hashtable();
			htxml = loadxml.getDegreeWhole();
			String mitiScoreMergeSelfEval=(String)htxml.get("mitiScoreMergeSelfEval"); 
			  
			String _str="";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
    			_str="pms.level_o";
			else
    			_str="pms.level "; 
			String sql = "select pm.status from per_mainbody pm,per_mainbodyset pms  where  pm.body_id=pms.body_id   and  pm.plan_id=" + planid + " and pm.mainbody_id='" + mainbodyID + "' "; // and pm.status<>4   ";
			if (!"True".equalsIgnoreCase(mitiScoreMergeSelfEval))
			{
				if (_vo.getInt("object_type")==2) // 考核人员
					sql += " and pm.object_id<>'" +mainbodyID+ "'";
		    	else
		    		sql += " and ( "+_str+" is null or "+_str+"<>5 ) ";
			}  
			
			frowset = dao.search(sql);
			boolean isNoMark = false;
			boolean isMarking = false;
			boolean isMarked = false;
			int n = 0;
			String planname=_vo.getString("name");
			while (frowset.next())
			{
				 
			    int a_status = frowset.getInt("status");
			    if (a_status == 0)
				isNoMark = true;
			    else if (a_status == 1||a_status == 4)
				isMarking = true;
			    else if (a_status == 2 || a_status == 7)
				isMarked = true;
			    n++;
			}
			if (n > 0)
			{
			    if (!isNoMark && !isMarking && isMarked)
			    {
	
			    } else
			    { 
			    	str= ResourceFactory.getProperty("kh.field.plan") + "：" +planname+ "  " + ResourceFactory.getProperty("performance.batchgrade.notSubed") + "!";					  
			    }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}		
	
	
//	将考核对象文档记录设为提交状态
	public void setArticleState(String plan_id,String object_id,String nbase, BatchGradeBo batchGradeBo)
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			RecordVo vo=new RecordVo("per_plan");
			vo.setInt("plan_id",Integer.parseInt(plan_id));
			vo=dao.findByPrimaryKey(vo);
			int object_type=vo.getInt("object_type");
			String _a0100="";
			String _nbase="";
			if(object_type==2&&object_id.equalsIgnoreCase(this.getUserView().getA0100()))			
			{
				_a0100=object_id;
				_nbase=nbase;
			}
			else
			{
			//	this.getUserView().getA0100()
				String _str=" pms.level";
				if(Sql_switcher.searchDbServer()==Constant.ORACEL)
					_str=" pms.level_o";
				
				String sql="select pmb.mainbody_id from per_mainbody pmb,per_mainbodyset pms where pmb.body_id=pms.body_id and "+_str+"=5 "
					+" and  pmb.plan_id="+plan_id+" and pmb.object_id='"+object_id+"'";
				this.frowset=dao.search(sql);
				String mainbody_id="";
				if(this.frowset.next())
					mainbody_id=this.frowset.getString(1);
				if(mainbody_id.length()>0&&mainbody_id.equalsIgnoreCase(this.getUserView().getA0100()))
				{
					_a0100=mainbody_id;
					_nbase=nbase;
				}
			}
			if(_a0100!=null&&_a0100.length()>0)
			{ 
				batchGradeBo.setArticleState(plan_id,_a0100,_nbase,1);
				batchGradeBo.setArticleState(plan_id,_a0100,_nbase,2);
			}		
		}
		catch(Exception e)
		{
			e.printStackTrace();
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
	
	
	//校验总体评价必填
	public String validateWholeEval(String mainbody_id,String object_id,String planid,String whole_id,Hashtable planParam)
	{
		String info="";
		try
		{
			String totalAppFormula =(String)planParam.get("TotalAppFormula"); // 总体评价的计算公式，默认为空
			String wholeEval =((String)planParam.get("WholeEval"));
			
			if("true".equalsIgnoreCase(wholeEval) && (whole_id.trim().length()<=0 || "null".equalsIgnoreCase(whole_id)) && (totalAppFormula==null || totalAppFormula.trim().length()<=0))
				info=ResourceFactory.getProperty("lable.statistic.wholeeven")+"为必填项!";
			if(info.length()==0&& "true".equalsIgnoreCase((String)planParam.get("DescriptiveWholeEval")))
			{
				ContentDAO dao=new ContentDAO(this.frameconn);
				String desc="";
				RowSet rowSet=dao.search("select description from per_mainbody where plan_id="+planid+" and mainbody_id='"+mainbody_id+"'  and object_id='"+object_id+"'");
				if(rowSet.next())
					desc=Sql_switcher.readMemo(rowSet,"description");
				if(desc==null||desc.trim().length()==0)
					info=ResourceFactory.getProperty("lable.statistic.wholeeven")+"描述为必填项!";
				if(rowSet!=null)
					rowSet.close();
			}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return info;
	}
	
}
