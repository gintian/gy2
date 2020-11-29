package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.TotalScoreControlBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardByItemBo;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class BatchSubScoreTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			String records=(String)this.getFormHM().get("records");
			records = records.replaceAll("／", "/");
			String model=(String)this.getFormHM().get("model");
			String flag=(String)this.getFormHM().get("flag");//=0判断是否有要提交的记录=1直接提交
			String[] _records=records.split("/");
			HashMap planvoMap=new HashMap();
			ContentDAO dao = new ContentDAO(this.frameconn);
			StringBuffer str=new StringBuffer("");
			
			ArrayList objectList = new ArrayList();
			
			if("0".equals(flag))
			{
				String isHas="0";
				for(int i=0;i<_records.length;i++)
				{
					String temp=_records[i];
					String[] temps=temp.split("-");					
					temps[0]=PubFunc.decrypt(temps[0].trim());
					temps[1]=PubFunc.decrypt(temps[1].trim());
					
					String object_id=temps[0].trim();
					objectList.add(object_id);
					String planid=temps[1].trim();
					RecordVo planVo=null;
					if(planvoMap.get(planid)!=null)
						planVo=(RecordVo)planvoMap.get(planid);
					else
					{
						planVo=new RecordVo("per_plan");
						planVo.setInt("plan_id",Integer.parseInt(planid)); 
						planVo=dao.findByPrimaryKey(planVo);
						planvoMap.put(planid,planVo);
					}
					LoadXml loadxml=null;
					if(BatchGradeBo.getPlanLoadXmlMap().get(planid)==null)
					{
						loadxml=new LoadXml(this.frameconn,planid);
						BatchGradeBo.getPlanLoadXmlMap().put(planid,loadxml);
					}
					else
						loadxml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(planid);		
					Hashtable planParam=loadxml.getDegreeWhole();
					String NoApproveTargetCanScore=(String)planParam.get("NoApproveTargetCanScore");   //目标卡未审批也允许打分 True, False, 默认为 False
					int status=planVo.getInt("status");  // 4：启动
					String leaderid = "";
					if(status==4||status==6)
					{
						boolean isHaveLeader=false;
						if(!"2".equals(planVo.getString("object_type")))
						{
							this.frowset=dao.search("select mainbody_id from per_mainbody where plan_id="+planid+" and object_id='"+temps[0].trim()+"' and body_id='-1'");
							while(this.frowset.next())
							{
								isHaveLeader=true;
								leaderid=this.frowset.getString("mainbody_id");
							}
						}
						String sp_flag="03";
						String obj_name="";
						this.frowset=dao.search("select * from per_object where plan_id="+planid+" and object_id='"+temps[0].trim()+"'");
						if(this.frowset.next())
						{
							String _sp_flag=this.frowset.getString("sp_flag")==null?"01":this.frowset.getString("sp_flag");
							obj_name=this.frowset.getString("a0101"); 
							if("false".equalsIgnoreCase(NoApproveTargetCanScore))
							{ 
									sp_flag=_sp_flag;
							} 
							if(!"2".equals(planVo.getString("object_type"))&&!isHaveLeader)
							{
								sp_flag="03";
							}
						}
						
						
						if(sp_flag!=null&& "03".equalsIgnoreCase(sp_flag))
						{
							
							StringBuffer sql=new StringBuffer("select per_mainbody.whole_grade_id,per_mainbody.mainbody_id,per_mainbody.a0101,per_mainbodyset.name,per_mainbodyset.");
							if(Sql_switcher.searchDbServer()==Constant.ORACEL)
								sql.append("level_o aa");
							else
								sql.append("level aa");
							sql.append(",per_mainbody.fillctrl,per_mainbody.status from per_mainbody,per_mainbodyset where ");
							sql.append(" per_mainbody.plan_id="+planid+" and per_mainbody.object_id='"+temps[0].trim()+"'  ");
							sql.append(" and per_mainbody.body_id=per_mainbodyset.body_id and (per_mainbody.status is null or ( per_mainbody.status!=2 and per_mainbody.status!=3)) and  per_mainbody.mainbody_id='"+this.userView.getA0100()+"' ");
							String info="";
							this.frowset=dao.search(sql.toString());
							if(this.frowset.next())
							{
							   isHas="1";
							   break;
							}
						}
					}
				}
				this.getFormHM().put("isHas", isHas);
				this.getFormHM().put("records",records);
				this.getFormHM().put("model",model);
			}
			else
			{
				boolean validateNot = false;
				ObjectCardByItemBo itemBo=new ObjectCardByItemBo();
				for(int i=0;i<_records.length;i++)
				{
					String temp=_records[i];
					String[] temps=temp.split("-");
					temps[0]=temps[0].trim();
					temps[1]=temps[1].trim();				
					temps[0]=PubFunc.decrypt(temps[0].trim());
					temps[1]=PubFunc.decrypt(temps[1].trim());
					
					
					String object_id=temps[0].trim();
					objectList.add(object_id);
					String planid=temps[1].trim();
					RecordVo planVo=null;
					if(planvoMap.get(planid)!=null)
						planVo=(RecordVo)planvoMap.get(planid);
					else
					{
						planVo=new RecordVo("per_plan");
						planVo.setInt("plan_id",Integer.parseInt(planid)); 
						planVo=dao.findByPrimaryKey(planVo);
						planvoMap.put(planid,planVo);
					}
					LoadXml loadxml=null;
					if(BatchGradeBo.getPlanLoadXmlMap().get(planid)==null)
					{
						loadxml=new LoadXml(this.frameconn,planid);
						BatchGradeBo.getPlanLoadXmlMap().put(planid,loadxml);
					}
					else
						loadxml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(planid);		
					Hashtable planParam=loadxml.getDegreeWhole();
					String NoApproveTargetCanScore=(String)planParam.get("NoApproveTargetCanScore");   //目标卡未审批也允许打分 True, False, 默认为 False
					int status=planVo.getInt("status");  // 4：启动
					String leaderid = "";
					if(status==4||status==6)
					{
						boolean isHaveLeader=false;
						if(!"2".equals(planVo.getString("object_type")))
						{
							this.frowset=dao.search("select mainbody_id from per_mainbody where plan_id="+planid+" and object_id='"+temps[0].trim()+"' and body_id='-1'");
							while(this.frowset.next())
							{
								isHaveLeader=true;
								leaderid=this.frowset.getString("mainbody_id");
							}
						}
						String sp_flag="03";
						String obj_name="";
						this.frowset=dao.search("select * from per_object where plan_id="+planid+" and object_id='"+temps[0].trim()+"'");
						if(this.frowset.next())
						{
							String _sp_flag=this.frowset.getString("sp_flag")==null?"01":this.frowset.getString("sp_flag");
							obj_name=this.frowset.getString("a0101"); 
							if("false".equalsIgnoreCase(NoApproveTargetCanScore))
							{ 
									sp_flag=_sp_flag;
							} 
							if(!"2".equals(planVo.getString("object_type"))&&!isHaveLeader)
							{
								sp_flag="03";
							}
						}
						
						
						if(sp_flag!=null&& "03".equalsIgnoreCase(sp_flag))
						{
							
							StringBuffer sql=new StringBuffer("select per_mainbody.whole_grade_id,per_mainbody.mainbody_id,per_mainbody.a0101,per_mainbodyset.name,per_mainbodyset.");
							if(Sql_switcher.searchDbServer()==Constant.ORACEL)
								sql.append("level_o aa");
							else
								sql.append("level aa");
							sql.append(",per_mainbody.fillctrl,per_mainbody.status from per_mainbody,per_mainbodyset where ");
							sql.append(" per_mainbody.plan_id="+planid+" and per_mainbody.object_id='"+temps[0].trim()+"'  ");
							sql.append(" and per_mainbody.body_id=per_mainbodyset.body_id and (per_mainbody.status is null or ( per_mainbody.status!=2 and per_mainbody.status!=3)) and  per_mainbody.mainbody_id='"+this.userView.getA0100()+"' ");
							String info="";
							this.frowset=dao.search(sql.toString());
							if(this.frowset.next())
							{
								String mainbody_id=this.frowset.getString("mainbody_id");
								String level=this.frowset.getString("aa"); 
								String whole_grade_id=this.frowset.getString("whole_grade_id");
								String fillctrl=this.frowset.getString("fillctrl")!=null?this.frowset.getString("fillctrl"):"0";
								String mainbody_a0101=this.frowset.getString("a0101");
								String _status=this.frowset.getString("status")!=null?this.frowset.getString("status"):"0";
								if(whole_grade_id==null|| "-1".equals(whole_grade_id))
									whole_grade_id="";
								this.frowset=dao.search("select * from per_target_evaluation where plan_id="+planid+" and object_id='"+temps[0].trim()+"' and mainbody_id='"+mainbody_id+"'");
								ArrayList valueList=new ArrayList();
								while(this.frowset.next())
								{
									String _value=this.frowset.getString("score");
									if(_value==null)
										_value="0";
									valueList.add("s_"+this.frowset.getString("p0400")+":"+_value);
								}
								
								ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),planid,temps[0].trim(),this.getUserView(),model,level,"2");
								bo.initData();  //初始化一些数据，为加扣分项目作判断
								
								
								
								info=bo.validateFollowPointMustFill(1); 
								if(info.length()==0)
								{
									if("true".equalsIgnoreCase(bo.getAllowLeaderTrace()))
									{
										
										this.frowset=dao.search("select trace_sp_flag from per_object where plan_id="+planid+" and object_id='"+object_id+"'");
										if(this.frowset.next())
										{
											if(this.frowset.getString(1)==null||!"03".equals(this.frowset.getString(1)))
												info="考核对象跟踪指标没有批准，不允许提交评分结果!";
										} 
									} 
								}
								
								
								if(info.length()==0&& "True".equalsIgnoreCase((String)planParam.get("ScoreBySumup")))
								{
									info= isWriteSummary(object_id,planid,bo);
								}
								
								
								 
								
								
								if(info.length()==0)
								{ 
									if("True".equalsIgnoreCase((String)planParam.get("MustFillWholeEval"))) //总体评价必填
									{
										
										info=validateWholeEval(dao,mainbody_id,object_id,planid,whole_grade_id,planParam);
									} 
								}
								
								if(info.length()==0&& "0".equals(_status)) //没有打分
								{
									if(!"0".equals((String)planParam.get("BlankScoreOption"))) // 指标未打分时，0 按未打分处理，1 计为最高分，默认值为按未打分处理 2用下面的参数
									{
										if("1".equals(bo.getTemplate_vo().getString("status"))&&planParam.get("VerifySameScore")!=null&& "true".equalsIgnoreCase((String)planParam.get("VerifySameScore")))
										{
											info="打分分数相同，不让提交!";
										}
										else
										{
											
											//程序要默认写入分值，暂没写
											ArrayList tempList=itemBo.getP04ResultList(bo,object_id,planid,this.userView);
											StringBuffer _sql=new StringBuffer("insert into per_target_evaluation (id,plan_id,object_id,mainbody_id,p0400,score,amount,degree_id) values (?,?,?,?,?,?,?,?)");
											if(tempList.size()>0)
											{
												dao.delete("delete from per_target_evaluation where plan_id="+planid+" and object_id='"+object_id+"' and mainbody_id='"+this.userView.getA0100()+"'",new ArrayList());
												dao.batchInsert(_sql.toString(),tempList); 
												bo.getScore(this.getUserView().getA0100(),object_id);
											}
										}
										
									}
									
								}
								
								
								if(info.length()==0)//isEntireysub
								{
									if("True".equalsIgnoreCase((String)planParam.get("isEntireysub"))) //所有项目必填
									{
										if(!bo.isAllScore(planid, mainbody_id, object_id))
										{
											info="提交时需为所有项目打分!";
										}
									}
								}
								
								if(info.length()==0)
								{
										if("1".equals(fillctrl.trim()))
										{
											if(bo.validateMustFill(planid, mainbody_id, object_id))
											{
												info="没有打分不能提交!"; 
											}
										}
								}
								
								if(info.length()==0)
								{
									if("True".equalsIgnoreCase((String)planParam.get("MustFillCause"))) //评分说明必填
									{
										if(!bo.isAllScoreExplain(planid, object_id, mainbody_id))
										{
											info="请填写评分说明再提交!";
										}
									}
								}
								
								// 总分分布强制控制  JinChunhai 2013.06.19									
								String GradeClass = (String)planParam.get("GradeClass");					// 等级分类ID
								String isControlTatalScore = (String)planParam.get("MutiScoreGradeCtl");         //是否进行总分控制  true|false
								String mainbodyGradeCtl = (String)planParam.get("MainbodyGradeCtl");  //主体类别
								String mainbodyFlag = "false";  //主体类别是否需要进行强制分布
								String body_id = "";
								this.frowset=dao.search("select distinct body_id from per_mainbody where plan_id="+planid+" and  mainbody_id='"+mainbody_id+"'");
								while(this.frowset.next()){
									body_id = this.frowset.getString("body_id");
								}
								if(mainbodyGradeCtl !=null && !"".equals(mainbodyGradeCtl)) {
									String[] mainbodyGradeCtlType = mainbodyGradeCtl.split(",");
									for(int k=0;k<mainbodyGradeCtlType.length;k++){
										if(body_id.equals(mainbodyGradeCtlType[k])){
											mainbodyFlag = "true";
											break;
										}
									}
								}
								String CheckGradeRange = (String)planParam.get("CheckGradeRange");  //多人打分等级控制是按所有主体还是单个主体。(0:所有，1: 单个)
								if(info.trim().length()==0&& "true".equalsIgnoreCase(isControlTatalScore)&&GradeClass!=null&&GradeClass.trim().length()>0&& "true".equalsIgnoreCase(mainbodyFlag))
								{	
									for(int k=0;k<objectList.size();k++){
										String temp_id = (String)objectList.get(k);
										dao.update("update per_mainbody set status=2 where  plan_id="
												+ planid + " and object_id='"
												+ temp_id + "' and mainbody_id='"
												+ mainbody_id + "'");
									}//批量提交之前先将字段设为提交状态
									TotalScoreControlBo tsc = new TotalScoreControlBo(this.getFrameconn(),planid,GradeClass);
									tsc.setObjectList(objectList);
									HashMap objectTotalScoreMap = bo.getObjectTotalScore(planid,mainbody_id);
									info = tsc.isOverTotalScoreControl(objectTotalScoreMap,planid,mainbody_id,CheckGradeRange);						
									if(info.trim().length()>0)
									{
										validateNot = true;										
									}
									if(validateNot && (i==(_records.length-1)))
									{
										dao.update("update per_mainbody set status=1 where plan_id="+planid+" and mainbody_id='"+mainbody_id+"'");
									}
								}
								
								if(info.trim().length()==0&&valueList!=null&&valueList.size()>0)
								{
							    	info=bo.validateSameScoreAllNumLess("2");
								}
								//总体评价必填  以后再添加功能
								if(info.trim().length()==0)
								{
									dao.update("update per_mainbody set status=2 where plan_id="+planid+" and mainbody_id='"+mainbody_id+"' and object_id='"+object_id+"'");
									
									//发待办   start  zhaoxg add 2014-9-4
									/** 先判断是否可以置为已办*/
									PendingTask pt = new PendingTask();
									LazyDynaBean be = new LazyDynaBean();
									String temp_sql="";
									if("False".equalsIgnoreCase(NoApproveTargetCanScore)){
										temp_sql="select pm.* from per_mainbody pm,per_object po where pm.object_id=po.object_id and po.plan_id='"+planid+"' and pm.plan_id='"+planid+"' and pm.mainbody_id='"+this.userView.getA0100()+"' and po.sp_flag='03'  and "+Sql_switcher.isnull("status", "0")+"<>2";
									}else{
										temp_sql="select * from per_mainbody  where plan_id='"+planid+"' and "+Sql_switcher.isnull("status", "0")+"<>2 and mainbody_id='"+this.userView.getA0100()+"'";
									}	
									be.set("oper", "start");
									be.set("sql", temp_sql);
									LazyDynaBean temp_bean=PerformanceImplementBo.updatePendingTask(this.frameconn, this.userView,"Usr"+this.userView.getA0100(),planid,be,"2");
									if("update".equals(temp_bean.get("selfflag"))){
										//pendingCode 是以PER 开头，不是以P开头 haosl update 2019.10.19
										pt.updatePending("P", "PER"+temp_bean.get("selfpending_id"), 1, "计划打分", this.userView);
									}
									
									ObjectCardBo _bo=new ObjectCardBo(this.getFrameconn(),planid,object_id,this.getUserView());
									LoadXml parameter_content = null;
									if (BatchGradeBo.getPlanLoadXmlMap().get(planid) == null) {
										parameter_content = new LoadXml(this.frameconn,planid);
										BatchGradeBo.getPlanLoadXmlMap().put(planid,parameter_content);
									} else {
										parameter_content = (LoadXml) BatchGradeBo.getPlanLoadXmlMap().get(planid);
									}
									Hashtable params = parameter_content.getDegreeWhole();
									String GradeByBodySeq = "false";// 按考核主体顺序号控制评分流程(True,
																	// False默认为False)
									if (params.get("GradeByBodySeq") != null)
										GradeByBodySeq = (String) params.get("GradeByBodySeq");
									ArrayList list = null;
									StringBuffer buf = new StringBuffer("");
									if ("true".equalsIgnoreCase(GradeByBodySeq)) {
										list = _bo.getUpLevelInfo(planid,object_id, buf);
									} else {
										list = _bo.getUpLevelInfo(planid, object_id,body_id);
										if (list.size() == 0 && !"-2".equals(body_id)) {
											String followBodyId = _bo.getfollowBodyid(body_id);
											int currentLevel = _bo.getCurrentLevel(followBodyId);
											// 目标卡制订支持几级审批
											String targetMakeSeries = (String) planParam.get("targetMakeSeries");

											for (int t = currentLevel + 1; t <= Integer.parseInt(targetMakeSeries); t++) {
												ArrayList appealObjectList = _bo.getUpLevelInfo(planid,object_id, followBodyId);
												if (appealObjectList.size() > 0) {
													list = appealObjectList;
													break;
												}
												followBodyId = _bo.getfollowBodyid(followBodyId);
											}
										}
									}
									LazyDynaBean abean = null;
									for (int j = 0; j < list.size(); j++) {
										abean = (LazyDynaBean) list.get(j);
										String appealObject_id = (String) abean.get("appealObject_id");
										
										/**评分给下个审批人推送待办  zhaoxg add 2014-9-1 */
										LazyDynaBean bean = new LazyDynaBean();
										String _title=planVo.getString("name")+"_(评分)";
										String _sql="";
										if("False".equalsIgnoreCase(NoApproveTargetCanScore)){
											_sql="select pm.* from per_mainbody pm,per_object po where pm.object_id=po.object_id and po.plan_id='"+planid+"' and pm.plan_id='"+planid+"' and pm.mainbody_id='"+this.userView.getA0100()+"' and po.sp_flag='03'  and "+Sql_switcher.isnull("status", "0")+"<>2";
										}else{
											_sql="select * from per_mainbody  where plan_id='"+planid+"' and "+Sql_switcher.isnull("status", "0")+"<>2 and mainbody_id='"+this.userView.getA0100()+"'";
										}	
										String href="";
				                        if ("2".equals(planVo.getString("object_type")))
				                        	href = "/performance/objectiveManage/objectiveEvaluate/objective_evaluate_list.do?b_init=init&plan_id=" + planid + "&returnflag=8&opt=1&entranceType=0&isSort=0";
				                        else
				                        	href = "/performance/objectiveManage/orgPerformance/org_performance_list.do?b_init=init&plan_id=" + planid + "&returnflag=8&opt=1";
										bean.set("oper", "start");
										bean.set("title", _title);
										bean.set("url", href);
										bean.set("sql", _sql);
										LazyDynaBean _bean=PerformanceImplementBo.updatePendingTask(this.frameconn, this.userView,"Usr"+appealObject_id,planid,bean,"1");
										if("add".equals(_bean.get("flag"))){
											pt.insertPending("PER"+_bean.get("pending_id"),"P",_title,this.userView.getDbname()+this.userView.getA0100(),"Usr"+appealObject_id,href,0,1,"计划打分",this.userView);	
										}	
										if("update".equals(_bean.get("selfflag"))){
											pt.updatePending("P", "P"+_bean.get("selfpending_id"), 1, "计划打分", this.userView);
										}
										/*String corpid = (String) ConstantParamter.getAttribute("wx","corpid");  
										if(corpid!=null&&corpid.length()>0){//推送微信公众号  zhaoxg add 2015-5-5
											WeiXinBo.sendMsgToPerson("Usr", appealObject_id, _title, "", "http://www.hjsoft.com.cn:8089/UserFiles/Image/tongzhi.png", "");
										}*/
									}
									
									
									//本人或团队负责人将绩效报告提交
									if(object_id.equalsIgnoreCase(userView.getA0100())||this.userView.getA0100().equals(leaderid))
									{
										bo.setArticleState(planid, userView.getA0100(), "USR");
									}  
								}
								else
								{
									str.append("\r\n "+obj_name+" "+info);
								}
							}
							
						}
						
					} 
				}
				this.getFormHM().put("info",SafeCode.encode(str.toString()));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	

	//校验总体评价必填
	public String validateWholeEval(ContentDAO dao,String mainbody_id,String object_id,String planid,String whole_id,Hashtable planParam)
	{
		String info="";
		try
		{
			if("true".equalsIgnoreCase((String)planParam.get("WholeEval"))&&whole_id.trim().length()==0)
				info=ResourceFactory.getProperty("lable.statistic.wholeeven")+"为必填项!";
			if(info.length()==0&& "true".equalsIgnoreCase((String)planParam.get("DescriptiveWholeEval")))
			{ 
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
	
	
	/**
     * 判断考核对象是否填写了个人总结
     * 
     * @param objectIDs
     * @param plan_id
     * @return
     */
	public String isWriteSummary(String object_id, String plan_id,ObjectCardBo bo)
	{
	
		StringBuffer info = new StringBuffer("");
		ContentDAO dao = new ContentDAO(this.getFrameconn()); 
		try
		{
			String id=object_id;
			HashMap map=new HashMap();
		    String object_typ=bo.getPlan_vo().getString("object_type");
		    if("1".equals(object_typ)|| "3".equals(object_typ)|| "4".equals(object_typ))
		    {
		    		 
		    			id=bo.getUn_functionary();  
		    			map.put(id,object_id);
		    	 	
		    }
		    
				HashMap menMap = new HashMap();
				frowset = dao.search("select * from per_article where plan_id=" + plan_id + " and Article_type=2 and (state=1 or  state=2) and a0100='"+id+"' and lower(nbase)='usr'  ");
				while (frowset.next())
				{
					String a0100=frowset.getString("a0100");
					if("1".equals(object_typ)|| "3".equals(object_typ)|| "4".equals(object_typ))
					{
						if(map.get(a0100)!=null)
							a0100=(String)map.get(a0100);
					}
				    if (frowset.getInt("fileflag") == 1)
				    {
				    	menMap.put(a0100, "1");
				    } else
				    	menMap.put(a0100, "1");
				}
		
				frowset = dao.search("select * from per_object where plan_id=" + plan_id + " and object_id='"+object_id+"'");
				while (frowset.next())
				{
				    String a0101 = frowset.getString("a0101");
				    String a0100 = frowset.getString("object_id");
				    if (menMap.get(a0100) == null)
				    {
						 
						 
						info.append(ResourceFactory.getProperty("performance.batchgrade.noPersonalSummary"));
						 
				    }
				}
		
		   
		
		} catch (Exception e)
		{
		    e.printStackTrace();
		} 
		return info.toString();
	}

}
