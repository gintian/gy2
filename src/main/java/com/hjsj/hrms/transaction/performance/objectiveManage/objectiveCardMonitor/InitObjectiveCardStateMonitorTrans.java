package com.hjsj.hrms.transaction.performance.objectiveManage.objectiveCardMonitor;

import com.hjsj.hrms.businessobject.performance.objectiveManage.ObjectiveCardTypeBo;
import com.hjsj.hrms.businessobject.performance.objectiveManage.SetUnderlingObjectiveBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Title:InitObjectiveCardStateMonitorTrans.java</p>
 * <p>Description:目标卡状态</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-04-25 14:15:22</p>
 * @author JinChunhai
 * @version 1.0
 */

public class InitObjectiveCardStateMonitorTrans extends IBusiness
{
	public void execute() throws GeneralException 
	{
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String opt=(String)hm.get("opt");
			String itemid=(String)hm.get("a_code");
						
			String convertPageEntry=(String)this.getFormHM().get("convertPageEntry");
			
			if(convertPageEntry==null || convertPageEntry.trim().length()<=0 || "1".equalsIgnoreCase(convertPageEntry))  // =1为正常的从 "目标卡状态页面" 进入，
			{
				
				String a0101="";
				String status="-1";
				String change="1";
				if(hm.get("change")!=null)
				{
					change=(String)hm.get("change");
					hm.remove("change");
				}
				String plan_id=(String)this.getFormHM().get("p_id");
				SetUnderlingObjectiveBo bo = new SetUnderlingObjectiveBo(this.getFrameconn(),this.getUserView());
				ArrayList planList=bo.getPlanList();
				if(planList==null||planList.size()<=0)
					throw GeneralExceptionHandler.Handle(new Exception("没有符合条件的考核计划！"));
				if("0".equals(change))
				{
					if(planList!=null&&planList.size()>0)
					{
						CommonData cd=(CommonData)planList.get(0);
						plan_id=cd.getDataValue();
					}
				}
				
				if("2".equals(opt))
				{
					a0101=(String)this.getFormHM().get("a0101");
					status=(String)this.getFormHM().get("status");
				}
				String objectID=null;
				if(hm.get("objectID")!=null)
				{
					objectID=(String)hm.get("objectID");
					hm.remove("objectID");
				}
				if(plan_id==null)
					plan_id="-1";
				LoadXml parameter_content = new LoadXml(this.getFrameconn(), plan_id);
				Hashtable params = parameter_content.getDegreeWhole();
				// 目标管理审批模式:0-考核关系 1-汇报关系. 默认为 0
				String targetAppMode=(String)params.get("targetAppMode"); 
				String targetMakeSeries=(String)params.get("targetMakeSeries");
				int type=Integer.parseInt((targetAppMode==null|| "".equals(targetAppMode))?"0":targetAppMode);
				int level=1;
				level=Integer.parseInt(targetMakeSeries);
				RecordVo vo = new RecordVo("per_plan");
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				String planStatus="-1";
				if(plan_id!=null&&!"-1".equals(plan_id))
				{
					vo.setInt("plan_id",Integer.parseInt(plan_id));
					if(dao.isExistRecordVo(vo))
					{
				     	vo=dao.findByPrimaryKey(vo);
				    	if(vo!=null)
				    		planStatus=vo.getString("status");
					}
				}
				String SpByBodySeq="False";
				if(params.get("SpByBodySeq")!=null)
					SpByBodySeq=(String)params.get("SpByBodySeq");
				this.getFormHM().put("planStatus",planStatus);
				ArrayList personList=bo.getObjectInfoList(plan_id, a0101, itemid, status, type, this.getUserView(), targetMakeSeries,objectID,"");
				ArrayList leaderList =null;
				if("True".equalsIgnoreCase(SpByBodySeq)){
					int maxLay=bo.getMaxLeaderLay();
					leaderList=bo.getLeaderColoumnList(maxLay+10000);
					level=maxLay;
				}else{
					leaderList= bo.getLeaderColoumnList(level);
				}
				ArrayList allStatusList = bo.getAllStatusList();
				this.getFormHM().put("statusList",allStatusList);
				this.getFormHM().put("itemid",itemid);
				this.getFormHM().put("a0101",a0101);
				this.getFormHM().put("planList",planList);
				this.getFormHM().put("status",status);
				this.getFormHM().put("personList",personList);
				this.getFormHM().put("p_id",plan_id);
				this.getFormHM().put("leaderList", leaderList);
				this.getFormHM().put("level",level+"");
				
			}else if(("2".equalsIgnoreCase(convertPageEntry)) || ("3".equalsIgnoreCase(convertPageEntry)))  // =2为从 "MBO目标设定及审批统计表" 页面进入; =3为从 "MBO目标总结考评进度统计表" 页面进入;
			{
				
				ObjectiveCardTypeBo bo = new ObjectiveCardTypeBo(this.getFrameconn(),this.getUserView());	
				
				String checkCycle="all";
				String changeCycle="all";
				String noYearCycle="all";
				StringBuffer sqlStr = new StringBuffer();	
				if("2".equalsIgnoreCase(opt))
				{
					checkCycle=(String)this.getFormHM().get("checkCycle");
					changeCycle=(String)this.getFormHM().get("changeCycle");	
					noYearCycle=(String)this.getFormHM().get("noYearCycle");							
					
//					if(!checkCycle.equalsIgnoreCase("all"))
//					{
					if(!"all".equalsIgnoreCase(checkCycle))
						sqlStr.append(" and cycle = " + checkCycle );
											
						if((!"all".equalsIgnoreCase(changeCycle)))  // 年度
							sqlStr.append(" and theyear = '" + changeCycle +"' ");
						
						if("1".equalsIgnoreCase(checkCycle))  // 半年
						{
//							if(!changeCycle.equalsIgnoreCase("all"))
							{
//								sqlStr.append(" and theyear = '" + changeCycle +"' ");	
								if(!"all".equalsIgnoreCase(noYearCycle))
									sqlStr.append(" and thequarter = '" + noYearCycle +"' ");	
							}
							
						}else if("2".equalsIgnoreCase(checkCycle))  // 季度
						{
//							if(!changeCycle.equalsIgnoreCase("all"))
							{
//								sqlStr.append(" and theyear = '" + changeCycle +"' ");	
								if(!"all".equalsIgnoreCase(noYearCycle))
									sqlStr.append(" and thequarter = '" + noYearCycle +"' ");	
							}
							
						}else if("3".equalsIgnoreCase(checkCycle))  // 月度
						{
//							if(!changeCycle.equalsIgnoreCase("all"))
							{
//								sqlStr.append(" and theyear = '" + changeCycle +"' ");	
								if(!"all".equalsIgnoreCase(noYearCycle))
									sqlStr.append(" and themonth = '" + noYearCycle +"' ");	
							}
						}else if("7".equalsIgnoreCase(checkCycle))  // 不定期
						{
							String startTime = (String) this.getFormHM().get("startDate");
							String endTime = (String) this.getFormHM().get("endDate");
							
							if(startTime!=null && !"".equals(startTime))
							{
							    StringBuffer buf = new StringBuffer();
							    buf.append(Sql_switcher.year("start_date")+ ">"+ getDatePart(startTime,"y") +" or ");
							    buf.append("("+Sql_switcher.year("start_date")+ "="+ getDatePart(startTime,"y")+" and ");
							    buf.append(Sql_switcher.month("start_date")+ ">"+ getDatePart(startTime,"m") +") or ");
							    buf.append("("+Sql_switcher.year("start_date")+ "="+ getDatePart(startTime,"y")+" and ");
							    buf.append(Sql_switcher.month("start_date")+ "="+ getDatePart(startTime,"m") +" and ");
							    buf.append(Sql_switcher.day("start_date")+ ">="+ getDatePart(startTime,"d") +")");
							    sqlStr.append(" and ("+buf.toString()+") ");
							}		   
							if(endTime!=null && !"".equals(endTime))
							{
							    StringBuffer buf = new StringBuffer();
							    buf.append(Sql_switcher.year("end_date")+ "<"+ getDatePart(endTime,"y") +" or ");
							    buf.append("("+Sql_switcher.year("end_date")+ "="+ getDatePart(endTime,"y")+" and ");
							    buf.append(Sql_switcher.month("end_date")+ "<"+ getDatePart(endTime,"m") +") or ");
							    buf.append("("+Sql_switcher.year("end_date")+ "="+ getDatePart(endTime,"y")+" and ");
							    buf.append(Sql_switcher.month("end_date")+ "="+ getDatePart(endTime,"m") +" and ");
							    buf.append(Sql_switcher.day("end_date")+ "<="+ getDatePart(endTime,"d") +")");
							    sqlStr.append(" and ("+buf.toString()+") ");
							}							
						}
//					}					
				}
								
				if(!"3".equalsIgnoreCase(opt))
				{
					ArrayList personList = new ArrayList();				
					if("2".equalsIgnoreCase(convertPageEntry))  // =2为从 "MBO目标设定及审批统计表" 页面进入;
						personList=bo.getMBOTableList(itemid , sqlStr.toString());   // 获得页面展现List				
					else if("3".equalsIgnoreCase(convertPageEntry))  // =3为从 "MBO目标总结考评进度统计表" 页面进入;
						personList=bo.getMBOScoreList(itemid , sqlStr.toString());   // 获得页面展现List				
					
					ArrayList checkCycleList=bo.getCheckCycleList();     // 获得考核周期		
					ArrayList changeCycleList=bo.getChangeCycleList(convertPageEntry);     // 获得考核周期下的年度数据											
					
					this.getFormHM().put("orgItemid",itemid);
					this.getFormHM().put("sqlStr",sqlStr.toString());
					this.getFormHM().put("noYearCycle",noYearCycle);
					this.getFormHM().put("changeCycle",changeCycle);				
					this.getFormHM().put("personList",personList);	
					this.getFormHM().put("checkCycleList",checkCycleList);	
					this.getFormHM().put("changeCycleList",changeCycleList);
				}
				
				if("3".equalsIgnoreCase(opt))
					checkCycle=(String)this.getFormHM().get("checkCycle");
				
				if((checkCycle!=null) && (checkCycle.trim().length()>0) && !("all".equalsIgnoreCase(checkCycle)))
				{					
					ArrayList noYearCycleList=bo.getNoYearCycleList(checkCycle);     // 获得考核周期下的非年度数据					
					this.getFormHM().put("noYearCycleList",noYearCycleList);
				}
									
				this.getFormHM().put("checkCycle",checkCycle);								
				
			}											
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	
	public String getDatePart(String mydate, String datepart)
	{
		String str = "";
		if ("y".equalsIgnoreCase(datepart))
			str = mydate.substring(0, 4);
		else if ("m".equalsIgnoreCase(datepart))
		{
			if ("0".equals(mydate.substring(5, 6)))
				str = mydate.substring(6, 7);
			else
				str = mydate.substring(5, 7);
		} else if ("d".equalsIgnoreCase(datepart))
		{
			if ("0".equals(mydate.substring(8, 9)))
				str = mydate.substring(9, 10);
			else
				str = mydate.substring(8, 10);
		}
		return str;
	}

}
