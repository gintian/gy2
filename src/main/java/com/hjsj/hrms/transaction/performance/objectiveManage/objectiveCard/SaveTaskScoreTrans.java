package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCard;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.batchGrade.TotalScoreControlBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectCardBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 *<p>Title:</p> 
 *<p>Description:保存或提交任务分数</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jun 10, 2008</p> 
 *@author dengcan
 *@version 4.0
 */

public class SaveTaskScoreTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			String object_id=PubFunc.decryption((String)this.getFormHM().get("object_id"));
			String planid=PubFunc.decryption((String)this.getFormHM().get("planid"));
			String flag=(String)this.getFormHM().get("flag");     //1:保存 或 2:提交 8:打分完成
			String body_id=(String)this.getFormHM().get("body_id");
			ArrayList valueList=(ArrayList)this.getFormHM().get("valueList");
			String model=(String)this.getFormHM().get("model");
			String isEmail=(String)this.getFormHM().get("isEmail");
			String isShowHistoryTask=(String)this.getFormHM().get("isShowHistoryTask");
			String saveType=(String)this.getFormHM().get("saveType");
			String wholeEvalScore = (String)this.getFormHM().get("wholeEvalScore");//总体评价分值
			if(isShowHistoryTask==null|| "".equals(isShowHistoryTask))
				isShowHistoryTask="0";
		//	ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),planid,object_id,this.getUserView(),model,body_id,"2");
			ObjectCardBo bo=new ObjectCardBo(this.getFrameconn(),planid,object_id,this.getUserView());
			RecordVo perObject_vo=bo.getPerObject_vo();
			bo.setIsShowHistoryTask(isShowHistoryTask);
			bo.setWholeEvalScore(wholeEvalScore);  //总体评价分值
			Hashtable planParam = bo.getPlanParam();
			String GradeClass = (String)planParam.get("GradeClass");					//等级分类ID
			String EvalClass = (String)planParam.get("EvalClass");            //在计划参数中的等级分类ID
			if(EvalClass==null || EvalClass.trim().length()<=0 || "0".equals(EvalClass.trim()))
				EvalClass = (String)planParam.get("GradeClass");
			LoadXml loadxml = new LoadXml(this.getFrameconn(),planid);  // batchGradeBo.getLoadxml();
			HashMap dynaMap=null;
			if(perObject_vo!=null&&perObject_vo.getString("body_id")!=null&&!"".equals(perObject_vo.getString("body_id")))
			{				
				dynaMap = loadxml.getDynaItem(bo.getPlan_vo().getInt("plan_id")+"", this.getFrameconn(),perObject_vo.getString("body_id"));
			}
			if(dynaMap!=null&&dynaMap.size()>0)
				bo.initData();  //初始化一些数据，为加扣分项目作判断
 						
			ArrayList gradeScopeList = loadxml.getPerGradeScopeList("ScoreScope");	 // 得到主体评分范围所有设置							
			
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			bo.setIsEmail(isEmail);
			String info="";
			if("2".equals(flag)|| "8".equals(flag))
			{

				info=bo.validateFollowPointMustFill(1);	
				if(info.length()==0){
					// 查看是否设置了【评分说明必填高级规则】如果设置了up或down，则可以继续 chent 20151128 start
					boolean isSetUporDown = false;
					ArrayList MustFillOptionsList = (ArrayList) planParam.get("MustFillOptionsList");
					if (MustFillOptionsList != null && MustFillOptionsList.size() > 0) {
						for (int i = 0; i < MustFillOptionsList.size(); i++) {
							LazyDynaBean abean = (LazyDynaBean) MustFillOptionsList
									.get(i);
							String Flag = (String) abean.get("Flag");
							String IsValid = (String) abean.get("IsValid");
							if (("Up".equalsIgnoreCase(Flag) && "true".equalsIgnoreCase(IsValid)) || ("Down".equalsIgnoreCase(Flag) && "true".equalsIgnoreCase(IsValid))) {
								isSetUporDown = true;
							} 
						}
					}
					// 查看是否设置了【评分说明必填高级规则】如果设置了up或down，则可以继续 chent 20151128 end
					if("True".equalsIgnoreCase((String)planParam.get("MustFillCause")) && !isSetUporDown) //评分说明必填
					{
						if(!bo.isAllScoreExplain(planid, object_id, this.userView.getA0100()))
						{
							info="请填写评分说明再提交!";
						}
					}
				}
			}
			if(("2".equals(flag)|| "8".equals(flag))&&info.length()==0)
			{
				if("true".equalsIgnoreCase(bo.getAllowLeaderTrace()))
				{
					
					this.frowset=dao.search("select trace_sp_flag from per_object where plan_id="+planid+" and object_id='"+object_id+"'");
					if(this.frowset.next())
					{
						if(this.frowset.getString(1)==null||!"03".equals(this.frowset.getString(1)))
							info="考核对象跟踪指标没有批准，不允许提交评分结果";
					}					
				}				
			}
			String wholeEvalMode = (String)planParam.get("WholeEvalMode");//总体评价采集方式: 0-录入等级，1-录入分值
			
			if(("2".equals(flag)|| "8".equals(flag))&&info.length()==0)
			{				
				if("0".equals(wholeEvalMode)&&("true".equalsIgnoreCase((String)planParam.get("WholeEval"))|| "true".equalsIgnoreCase((String)planParam.get("DescriptiveWholeEval"))))  //总体评价 or 总体评价描述
				{
					if("True".equalsIgnoreCase((String)planParam.get("MustFillWholeEval"))) //总体评价必填
					{
						String whole_id="";
						if(valueList!=null)
						{
							for(int i=0;i<valueList.size();i++)
							{
								ArrayList tempList=new ArrayList();
								String[] temp=((String)valueList.get(i)).split(":");
								if("whole_id".equalsIgnoreCase(temp[0]))
								{
									if(temp.length==2)
										whole_id=temp[1];
									continue;
								}
							}
						}
						if(valueList!=null)
				     		info=validateWholeEval(this.getUserView().getA0100(),object_id,planid,whole_id,planParam);
						
					}
				}
				
				if(info.length()==0&& "True".equalsIgnoreCase((String)planParam.get("ScoreBySumup")))
				{
					info= isWriteSummary(object_id,planid,bo);					
				}				
			}
			
			
			if(info.trim().length()==0)
			{
				String pendingCode=(String)this.getFormHM().get("pendingCode");
				//普天集团
				if("true".equalsIgnoreCase(bo.getEvaluateCard_mail()))
				{
					
					if(SystemConfig.getPropertyValue("clientName")!=null&& "bjpt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
					{							
						bo.setPri_pendingCode(pendingCode);
					}
					
				}
				
				
				//判断是否给能打分的所有考核对象打完分  pjf  2013.12.28
//				boolean b = bo.exsitObjectNoScore(planid);
//				if(pendingCode!=null&&pendingCode.length()>0&&"2".equals(flag) && b)
//				{
//					PendingTask pt = new PendingTask();
//					pt.updatePending("P", pendingCode, 1, "目标评分",this.userView);
//				}
				info=bo.saveTaskScore(valueList,flag,body_id);
				// 总评分
				String score = "0.0";							
				String ascore="";
				if(valueList!=null&&valueList.size()>0)
				{
					score =bo.getScore(this.getUserView().getA0100(),object_id);								
					ascore=bo.getaScore();
				}
				
				
				// 国网研究院计算总体评价调用存储过程  JinChunhai 2013.03.11
				String totalAppFormula =(String)planParam.get("TotalAppFormula"); // 总体评价的计算公式，默认为空
				String haveFormula = "false";
				String totalAppValue = "";
				if("0".equals(wholeEvalMode) && "true".equalsIgnoreCase((String)planParam.get("WholeEval")) && totalAppFormula!=null && totalAppFormula.trim().length()>0)
				{										
					BatchGradeBo batchGradeBo = new BatchGradeBo(this.getFrameconn(),planid);
				//	String totalscore = bo.getScore(this.getUserView().getA0100(),object_id);
					batchGradeBo.setTempWholeEvalTableScore(object_id,this.getUserView().getA0100(),score);	
					String[] userid = new String[1];
					userid[0] = object_id;
					totalAppValue = batchGradeBo.getFormulaSqlToValue(this.userView,("'"+object_id+"'"),this.getUserView().getA0100(),totalAppFormula,EvalClass,userid);
					haveFormula = "true";
				}
				if(info.trim().length()==0&&"1".equals(wholeEvalMode)){
					String value=wholeEvalScore;
					if(wholeEvalScore==null||wholeEvalScore.trim().length()==0) //20141203 dengcan  总体评价分值不填时SQL执行有问题 
						value="null";
					dao.update("update per_mainbody set whole_score="+value+" where plan_id="+planid+" and object_id='"+object_id+"' and mainbody_id='"+this.userView.getA0100()+"'");
				}
				// 总分分布强制控制  JinChunhai 2013.06.19									
				String isControlTatalScore = (String)planParam.get("MutiScoreGradeCtl");         //是否进行总分控制  true|false
				String CheckGradeRange = (String)planParam.get("CheckGradeRange");  //多人打分等级控制是按所有主体还是单个主体。(0:所有，1: 单个)
				String mainbodyGradeCtl = (String)planParam.get("MainbodyGradeCtl");  //主体类别
				String mainbodyFlag = "false";  //主体类别是否需要进行强制分布
				String realBody_id = "";
				this.frowset=dao.search("select distinct body_id from per_mainbody where plan_id="+planid+" and  mainbody_id='"+this.userView.getA0100()+"' and object_id='"+object_id+"'");
				while(this.frowset.next()){
					realBody_id = this.frowset.getString("body_id");
				}
				if(mainbodyGradeCtl !=null && !"".equals(mainbodyGradeCtl)) {
					String[] mainbodyGradeCtlType = mainbodyGradeCtl.split(",");
					for(int k=0;k<mainbodyGradeCtlType.length;k++){
						if(realBody_id.equals(mainbodyGradeCtlType[k])){
							mainbodyFlag = "true";
							break;
						}
					}
				}
				if(info.trim().length()==0&&("2".equals(flag)|| "8".equals(flag))&& "true".equalsIgnoreCase(isControlTatalScore)&&GradeClass!=null&&GradeClass.trim().length()>0&& "true".equalsIgnoreCase(mainbodyFlag))
				{					
					TotalScoreControlBo tsc = new TotalScoreControlBo(this.getFrameconn(),planid,GradeClass);					
					HashMap objectTotalScoreMap = bo.getObjectTotalScore(planid,this.userView.getA0100());
					info = tsc.isOverTotalScoreControl(objectTotalScoreMap,planid,this.userView.getA0100(),CheckGradeRange);						
					if(info.length()>0)
					{
						dao.update("update per_mainbody set status=1 where plan_id="+planid+" and object_id='"+object_id+"' and mainbody_id='"+this.userView.getA0100()+"'");
					}
				}
				
				//总分相同对象个数，
				if(info.trim().length()==0&&valueList!=null&&valueList.size()>0)
				{
			    	info=bo.validateSameScoreAllNumLess(flag);
				}
				// 判断主体的评分是否在限制范围内  JinChunhai 2011.11.25
				if(info.trim().length()==0 && "2".equals(flag) && gradeScopeList!=null && gradeScopeList.size()>0&&valueList!=null&&valueList.size()>0)
				{
					String str = "select body_id from per_object where plan_id=" + planid + " and object_id = '" + object_id + "'";			        
			        this.frowset = dao.search(str);
			        String objectBody_id = "";
				    if(this.frowset.next())					    			    	
				    	objectBody_id = this.frowset.getString("body_id");
				    
				    if(objectBody_id!=null && objectBody_id.trim().length()>0)
				    {
						for (int i = 0; i < gradeScopeList.size(); i++)
						{
						    LazyDynaBean bean = (LazyDynaBean) gradeScopeList.get(i);
						    String bodyId = (String) bean.get("BodyId");
						    String upScope = (String) bean.get("UpScope");
						    String downScope = (String) bean.get("DownScope");						    
						    if(objectBody_id.equalsIgnoreCase(bodyId))
						    {
						    	if((downScope!=null && downScope.trim().length()>0) && (Double.parseDouble(score)<Double.parseDouble(downScope)))
						    	{
						    		dao.update("update per_mainbody set status=1 where plan_id="+planid+" and object_id='"+object_id+"' and mainbody_id='"+this.userView.getA0100()+"' ");
						    		//throw (new GeneralException(ResourceFactory.getProperty("您的评分低于评分下限值"+ downScope +"！")));
						    		info = "您的评分低于评分下限值"+ downScope +"！";
						    	}
						    	else if((upScope!=null && upScope.trim().length()>0) && (Double.parseDouble(score)>Double.parseDouble(upScope)))
						    	{
						    		dao.update("update per_mainbody set status=1 where plan_id="+planid+" and object_id='"+object_id+"' and mainbody_id='"+this.userView.getA0100()+"' ");
						    		//throw (new GeneralException(ResourceFactory.getProperty("您的评分高于评分上限值"+ upScope +"！")));	
						    		info = "您的评分高于评分上限值"+ upScope +"！";
						    	}
						    	break;
						    }
						}
				    }
				}
				this.getFormHM().put("score",score);
				this.getFormHM().put("ascore", ascore);
				this.getFormHM().put("haveFormula", haveFormula);
				this.getFormHM().put("totalAppValue", totalAppValue);

			}
			else
				info+="\r\n 提交失败!";
			this.getFormHM().put("info",SafeCode.encode(info));
			this.getFormHM().put("flag",flag);
			
			this.getFormHM().put("planid", planid);
			this.getFormHM().put("body_id", body_id);
			this.getFormHM().put("model", model);
			this.getFormHM().put("object_id", object_id);
			if("zglt".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
				this.getFormHM().put("clientName", "zglt");
			else
				this.getFormHM().put("clientName", "1");
			this.getFormHM().put("saveType", saveType);	
			this.getFormHM().put("wholeEvalScore", wholeEvalScore);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	
	
	//校验总体评价必填
	public String validateWholeEval(String mainbody_id,String object_id,String planid,String whole_id,Hashtable planParam)
	{
		String info="";
		try
		{
			if("true".equalsIgnoreCase((String)planParam.get("WholeEval"))&&whole_id.trim().length()==0)
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
						 
						info.append(a0101);  
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
