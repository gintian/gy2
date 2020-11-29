package com.hjsj.hrms.transaction.performance.batchGrade;

import com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo;
import com.hjsj.hrms.businessobject.performance.singleGrade.SingleGradeBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

public class ExcecuteBatchGradeTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		try
		{
			
			//long time=System.currentTimeMillis();
			//////////////////////////////////
			
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String plan_id="";
			String titleName="";
		    String plan_descript="";            //计划说明
			String model=(String)hm.get("model");
			if(!"query".equals((String)hm.get("b_Desc")))
			{
				plan_id=(String)hm.get("b_Desc");	
				titleName="";					
			}
			else
			{
				plan_id=(String)this.getFormHM().get("dbpre");	
				titleName=(String)this.getFormHM().get("titleName");
			}
			String current=(String)this.getFormHM().get("current");      //页数
			if(hm.get("selectNewPlan")!=null&& "true".equals((String)hm.get("selectNewPlan")))
			{
				current="1";
				hm.remove("selectNewPlan");
			}
			CheckPrivSafeBo _bo = new CheckPrivSafeBo(this.frameconn,this.userView);
			boolean flag = _bo.isPlanIdPriv(plan_id);
			if(!flag){
				return;
//				throw GeneralExceptionHandler.Handle(new GeneralException(""));
			}
			if(!"0".equals(plan_id))
			{		
				int object_type=2; // 1:部门 2：人员
				BatchGradeBo batchGradeBo=new BatchGradeBo(this.getFrameconn(),this.userView,plan_id);
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				this.frowset=dao.search("select pp.template_id,pt.status,pp.object_type,pp.descript from per_plan pp,per_template pt where pp.template_id=pt.template_id and pp.plan_id="+plan_id);
				this.frowset.next();
				String template_id=this.frowset.getString(1);
				String status=this.frowset.getString(2);		//权重分值表识 0：分值 1：权重
				object_type=this.frowset.getInt(3);
				plan_descript=Sql_switcher.readMemo(this.frowset,"descript");
				if(status==null|| "".equals(status))
					status="0";
				
				
			//	System.out.println("current0="+(System.currentTimeMillis()-time));
				String isShowTotalScore=batchGradeBo.getIsShowTotalScore();
				String isShowOrder=batchGradeBo.getIsShowOrder();
				String isAutoCountTotalOrder=batchGradeBo.getIsAutoCountTotalOrder();
				batchGradeBo.setObject_type(String.valueOf(object_type));
				//if(isAutoCountTotalOrder.equalsIgnoreCase("true")&&(isShowTotalScore.equalsIgnoreCase("true")||isShowOrder.equalsIgnoreCase("true")))
				{
					batchGradeBo.getDynaRankInfoMap(plan_id);
					batchGradeBo.getObjectInfoMap(plan_id);
					HashMap objectTotalScoreMap=batchGradeBo.getObjectTotalScore(Integer.parseInt(plan_id),this.userView.getA0100(),template_id,this.userView);
					batchGradeBo.setObjectTotalScoreMap(objectTotalScoreMap);
				}
				
			//	System.out.println("current1="+(System.currentTimeMillis()-time));
				
				
				String totalRecordCount=String.valueOf(batchGradeBo.getPerPlanObjects(Integer.parseInt(plan_id),this.userView.getA0100()));  //考核对象个数
				current=getCurrent(batchGradeBo.getScoreNumPerPage(),totalRecordCount,current);
				
			    //将待办置为已阅
				PendingTask pt = new PendingTask();
				String pendingCode = getPendingCode(plan_id,this.userView.getA0100());			
				if(pendingCode!=null && pendingCode.trim().length()>0)
				{				
					pt.updatePending("P", pendingCode, 2, "计划打分",this.userView);
				}	
				
				ArrayList list=batchGradeBo.getBatchGradeHtml(template_id,Integer.parseInt(plan_id),this.userView.getA0100(),status,titleName,Integer.parseInt(current));
				
				String DegreeShowType=batchGradeBo.getDegreeShowType();
				String html=(String)list.get(0);
				String isKnowWhole=(String)list.get(1);
				String fineMax=(String)list.get(2);
				String dataArea=(String)list.get(3);
				String gradeStatus=(String)list.get(4);
				String scoreflag=(String)list.get(5);
				String dataArea2=(String)list.get(6);
				String wholeEval=(String)list.get(7);
				String pointDeformity=(String)list.get(8);
				String noGradeItem=(String)list.get(9);
				if(batchGradeBo.getPointContrl().length()>0)
					this.getFormHM().put("pointContrl",batchGradeBo.getPointContrl().substring(1));
				else
					this.getFormHM().put("pointContrl","");
				
				//查看日志填报情况
				String showDayWeekMonth = batchGradeBo.getShowDayWeekMonth();
				String dayWeekMonthFlag = "false";
				if(showDayWeekMonth !=null && !"".equals(showDayWeekMonth)) {
					String[] empRecordType = showDayWeekMonth.split(",");
					dayWeekMonthFlag = "true";
					for(int i=0;i<empRecordType.length;i++){
						if("1".equals(empRecordType[i]))
							this.getFormHM().put("showDay", empRecordType[i]);
						if("2".equals(empRecordType[i]))
							this.getFormHM().put("showWeek", empRecordType[i]);
						if("3".equals(empRecordType[i]))
							this.getFormHM().put("showMonth", empRecordType[i]);
					}
				}
				this.getFormHM().put("dayWeekMonthFlag", dayWeekMonthFlag);
				
				//显示历次得分表
				String showHistoryScore = batchGradeBo.getShowHistoryScore();
				this.getFormHM().put("showHistoryScore", showHistoryScore);
				
				
				/* 得到某计划某人的考评对象集合 */
			//	ArrayList objectList=batchGradeBo.getPerPlanObjects(Integer.parseInt(plan_id),this.userView.getA0100(),Integer.parseInt(current),scoreNumPerPage);			
				ArrayList objectList=batchGradeBo.getObjectList();
				
				StringBuffer userIDs=new StringBuffer("");
				StringBuffer fillCtrs=new StringBuffer("");
				StringBuffer userNames=new StringBuffer("");
				for(Iterator t=objectList.iterator();t.hasNext();)
				{
					String[] temp=(String[])t.next();
					userIDs.append("/");
					userIDs.append("a"+temp[0]);
					
					fillCtrs.append("/");
					fillCtrs.append(temp[3]);
					
					userNames.append("/");
					userNames.append(temp[1]);
				}
				if(userIDs.length()>0)
					this.getFormHM().put("userIDs",userIDs.substring(1));
				else
					this.getFormHM().put("userIDs","");
				if(fillCtrs.length()>0)
					this.getFormHM().put("fillCtrs",fillCtrs.substring(1));
				else
					this.getFormHM().put("fillCtrs","");
				if(userNames.length()>0)
					this.getFormHM().put("userNames",userNames.substring(1));
				else
					this.getFormHM().put("userNames","");
				
				SingleGradeBo singleGradeBo=new SingleGradeBo(this.frameconn);
				LoadXml loadxml=batchGradeBo.getLoadxml();  //new LoadXml(this.getFrameconn(),plan_id);
				String targetDeclare=singleGradeBo.getTargetDeclare(plan_id,loadxml);
				String individualPerformance=singleGradeBo.getIndividualPerformance(plan_id,"",this.getUserView().getA0100(),loadxml,object_type,1);
				
				
				StringBuffer a_span=batchGradeBo.getSpan_ids();
				String span_ids="";
				if(a_span.length()>0)
					span_ids=a_span.substring(1);
				
				this.getFormHM().put("script_code",batchGradeBo.getScript_code().toString());
								
			//	ExamPlanBo ebo = new ExamPlanBo(this.frameconn);
			//	String controlByKHMoudle = ebo.getControlByKHMoudle(); // 考核计划按模板权限控制, True,False(默认)
				//////////////////////
//				 得到绩效考核计划列表
				String perPlanSql = "select plan_id,name,status,parameter_content,template_id from per_plan where ( status=4 or status=6 ) and (busitype is null or busitype<>'1')";
				if (!userView.isSuper_admin())
				{
					perPlanSql += "  and plan_id in (select plan_id from per_mainbody where mainbody_id='"
							+ userView.getA0100() + "' ) ";
				}
				if(!"USR".equalsIgnoreCase(userView.getDbname()))
					perPlanSql+=" and 1=2 ";
				
			//	perPlanSql+=" and mainbody_id<>object_id ";
			//	perPlanSql+=" )";
				perPlanSql += " and  "+Sql_switcher.isnull("Method", "1")+"=1  order by "+Sql_switcher.isnull("a0000", "999999999")+" asc,plan_id desc";
				this.frowset = dao.search(perPlanSql);
				ArrayList dblist=new ArrayList();
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
					String aplan_id = this.getFrowset().getString("plan_id");					
					if(BatchGradeBo.getPlanLoadXmlMap().get(aplan_id)==null)
					{						
						aloadxml = new LoadXml(this.getFrameconn(),aplan_id);
						BatchGradeBo.getPlanLoadXmlMap().put(aplan_id,aloadxml);
					}
					else
					{
						aloadxml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(aplan_id);
					}
					
					Hashtable htxml = aloadxml.getDegreeWhole();
					String performanceType=(String)htxml.get("performanceType");	              
					
					if(model.equals(performanceType))
	                {
	                //	if(loadXml.getHandEval(xmlContent).equalsIgnoreCase("FALSE"))
						if("FALSE".equalsIgnoreCase((String)htxml.get("HandEval")))
						{
	                		CommonData vo = new CommonData(aplan_id, name);
	                		dblist.add(vo);
	                	}
	                }
				}
				dblist=batchGradeBo.addGradeStaus(dblist,this.getUserView().getA0100(),3);	
				this.getFormHM().put("dblist", dblist);
				
				String topStr = "";
			    topStr = " select topscore from per_template where template_id=( select template_id from per_plan where plan_id="+plan_id+") ";
			    this.frowset=dao.search(topStr);
			    float topscore = 0;
				if(this.frowset.next())
				{
					topscore = (float)frowset.getFloat("topscore");
				}
				this.getFormHM().put("topscore",String.valueOf(topscore));
				this.getFormHM().put("wholeEvalMode",batchGradeBo.getWholeEvalMode());   //总体评价得分
				
				Hashtable htxml =loadxml.getDegreeWhole();
				this.getFormHM().put("showSumRow", (String)htxml.get("ShowSumRow"));
				this.getFormHM().put("evalOutLimitStdScore", ((String)htxml.get("EvalOutLimitStdScore")).toLowerCase());
				this.getFormHM().put("degreeShowType",DegreeShowType);
				this.getFormHM().put("isPage",batchGradeBo.getScoreNumPerPage());
				ArrayList pageList=getPageList(batchGradeBo.getScoreNumPerPage(),totalRecordCount);
				this.getFormHM().put("pageList",pageList);
				this.getFormHM().put("current",current);
				this.getFormHM().put("performanceType",batchGradeBo.getPerformanceType());
				this.getFormHM().put("span_ids",span_ids);
				this.getFormHM().put("individualPerformance",individualPerformance);
				this.getFormHM().put("targetDeclare",targetDeclare);
				this.getFormHM().put("tableHtml",html);
				this.getFormHM().put("template_id",template_id);
				this.getFormHM().put("isKnowWhole",isKnowWhole);
				this.getFormHM().put("gradeStatus",gradeStatus);
				this.getFormHM().put("scoreflag",scoreflag);
				this.getFormHM().put("wholeEval",wholeEval);
				this.getFormHM().put("pointDeformity",pointDeformity);
				this.getFormHM().put("dbpre",plan_id);
				this.getFormHM().put("plan_descript",plan_descript!=null&&plan_descript.trim().length()>0?"1":"0");
				this.getFormHM().put("noGradeItem",noGradeItem);
				this.getFormHM().put("isAutoCountTotalOrder",batchGradeBo.getIsAutoCountTotalOrder());
				this.getFormHM().put("isShowOrder",batchGradeBo.getIsShowOrder());
				this.getFormHM().put("isShowTotalScore",batchGradeBo.getIsShowTotalScore());
				this.getFormHM().put("isEntiretySub",batchGradeBo.getIsEntiretySub());
				this.getFormHM().put("paramTable",htxml);
				String str = SystemConfig.getPropertyValue("clientName");
				if(str!=null&&("zglt".equalsIgnoreCase(str.trim())|| "hkyh".equalsIgnoreCase(str.trim())))
					this.getFormHM().put("plan_descript_content", batchGradeBo.getDescript(plan_id).replaceAll(" ", "&nbsp;&nbsp;"));
				
				String mustFillWholeEval=(String)htxml.get("MustFillWholeEval");  //总体评价必填      2014.01.07   pjf
				this.getFormHM().put("mustFillWholeEval", mustFillWholeEval);
				
				//考核对象唯一性指标
				String onlyFild = "";
				Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.frameconn);
				if("2".equals(String.valueOf(object_type)))
				{
					onlyFild = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
//					String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");
//					if(uniquenessvalid.equals("0"))
//						onlyFild ="";
				}else
				{
					RecordVo unit_code_field_constant_vo=ConstantParamter.getRealConstantVo("UNIT_CODE_FIELD",this.frameconn);
					if(unit_code_field_constant_vo!=null)
					{
						onlyFild=unit_code_field_constant_vo.getString("str_value");	
					}				
				}
				this.getFormHM().put("onlyFild",onlyFild);
			}
			else
			{
				this.getFormHM().put("plan_descript","");
				this.getFormHM().put("plan_descript_content"," ");
				this.getFormHM().put("span_ids","");
				this.getFormHM().put("targetDeclare","");
				this.getFormHM().put("individualPerformance","");
				
				this.getFormHM().put("script_code","");
			}
			
			
			
			/////////////////////////////////////
			//System.out.println("current="+(System.currentTimeMillis()-time));
			BatchGradeBo bo=new BatchGradeBo(this.getFrameconn(),this.userView,plan_id);
			String mainBodyID = (String) this.getFormHM().get("mainBodyID");
			ArrayList userlist  = bo.getObjectsList(Integer.parseInt(plan_id),this.userView.getA0100(),current);
			this.getFormHM().put("objectList",userlist);
		}
		catch(Exception e)
		{
			e.printStackTrace();
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
	
	public String getCurrent(String scoreNumPerPage,String totalRecordCount,String current)
	{
		String aCurrent=current;
		int count=getCount(scoreNumPerPage,totalRecordCount);
		if(Integer.parseInt(current)<=count)
		{
			return aCurrent;
		}
		else
		{
			aCurrent="1";
		}
		return aCurrent;
	}
	
	
	public int getCount(String scoreNumPerPage,String totalRecordCount)
	{
		int count=1;
		int a_scoreNumPerPage=Integer.parseInt(scoreNumPerPage);
		int a_totalRecordCount=Integer.parseInt(totalRecordCount);
		if(!"0".equals(scoreNumPerPage))
		{
			if(a_totalRecordCount%a_scoreNumPerPage==0)
			{
				count=a_totalRecordCount/a_scoreNumPerPage;
			}
			else
			{
				count=a_totalRecordCount/a_scoreNumPerPage+1;
			}
		}
		return count;
	}
	
	
	public ArrayList getPageList(String scoreNumPerPage,String totalRecordCount)
	{
		
		ArrayList list=new ArrayList();
		if("0".equals(scoreNumPerPage))
			return list;
		
		int count=getCount(scoreNumPerPage,totalRecordCount);
		for(int i=1;i<=count;i++)
		{
			CommonData dataobj = new CommonData(String.valueOf(i),"第"+i+"页");
			list.add(dataobj);
		}
		return list;
	}
	
	
	
	
}
