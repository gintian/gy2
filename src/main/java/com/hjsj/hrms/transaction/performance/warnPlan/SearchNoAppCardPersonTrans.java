package com.hjsj.hrms.transaction.performance.warnPlan;

import com.hjsj.hrms.businessobject.performance.objectiveManage.SetUnderlingObjectiveBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Title:SearchNoAppCardPersonTrans.java</p>
 * <p>Description:展示预警提醒未完成目标卡制定及审批的人员</p>
 * <p>Company:hjsj</p>
 * <p>create time:2012-05-24 09:41:14</p>
 * @author JinChunhai
 * @version 6.0
 */

public class SearchNoAppCardPersonTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		
		String creatDate = PubFunc.getStringDate("yyyy-MM-dd"); // 系统当前日期
		try
		{
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");		
			String plan_id = (String)hm.get("plan_id"); // 预警计划编号
			String cardApp = (String)hm.get("cardApp"); // 预警计划编号
			
			String warn = "";
			if(cardApp!=null && cardApp.trim().length()>0 && ("targetAndApp".equalsIgnoreCase(cardApp) || "app".equalsIgnoreCase(cardApp)))
				warn = "warn";
			
			LoadXml parameter_content = new LoadXml(this.getFrameconn(), plan_id);
			Hashtable params = parameter_content.getDegreeWhole();
			// 目标管理审批模式:0-考核关系 1-汇报关系. 默认为 0
			String targetAppMode = (String)params.get("targetAppMode"); 
			String targetMakeSeries = (String)params.get("targetMakeSeries");
			int type = Integer.parseInt((targetAppMode==null|| "".equals(targetAppMode))?"0":targetAppMode);
			int level = 1;
			level = Integer.parseInt(targetMakeSeries);
			ArrayList warnRoleScopeList = (ArrayList)params.get("WarnRoleScopeList");
			String delayTime = ""; // 目标卡制定及审批延期多少天预警  
			if(warnRoleScopeList!=null && warnRoleScopeList.size()>0)
			{
				for (int i = 0; i < warnRoleScopeList.size(); i++)
		    	{
		    		LazyDynaBean bean = (LazyDynaBean) warnRoleScopeList.get(i);
		    		String opt = (String) bean.get("opt");
		    		if(opt!=null && opt.trim().length()>0 && "1".equalsIgnoreCase(opt))
		    		{
		    			delayTime = (String) bean.get("delayTime");
		    		}
		    	}
			}
			
			ArrayList objStatusList = new ArrayList();
			SetUnderlingObjectiveBo bo = new SetUnderlingObjectiveBo(this.getFrameconn(),this.getUserView());
			RecordVo planVo = bo.getPlanVo(plan_id);
			ArrayList objCardStatusList = bo.getObjectInfoList(plan_id, "", "UN", "-1", type, this.getUserView(), targetMakeSeries,null,warn);
			ArrayList leaderList = bo.getLeaderColoumnList(level);
	        
			LazyDynaBean bean = null;
			for(int i=0;i<objCardStatusList.size();i++)
			{
				bean=(LazyDynaBean)objCardStatusList.get(i);
				String flag = (String)bean.get("flag"); // 目标卡状态
								
				if(flag==null || flag.trim().length()<=0 || "01".equalsIgnoreCase(flag)) // 过滤未制订的目标卡
				{
					String distribute_date = String.valueOf(planVo.getDate("distribute_date")); // 计划分发时间
					if(distribute_date!=null && distribute_date.trim().length()>0)
					{
						DateFormat df = new SimpleDateFormat("yyyy-MM-dd");				
						Date nowDate = df.parse(creatDate);
						Date d2 = df.parse(String.valueOf(distribute_date));
						long diff = nowDate.getTime() - d2.getTime();
						long days = diff / (1000 * 60 * 60 * 24);
		
						// 判断是否超过预警期限
						if(days<Long.parseLong(delayTime))
							continue;
					}else
						continue;					
				}												
				else if(flag!=null && flag.trim().length()>0 && "02".equalsIgnoreCase(flag)) // 过滤报批的目标卡
				{					
					boolean haveMarked = false;
					String date = "";
					for(int k=level;k>=1;k--)
			        {
						date = (String)bean.get(String.valueOf(k)+"date");
						if(date!=null && date.trim().length()>0)
						{
							DateFormat df = new SimpleDateFormat("yyyy-MM-dd");				
							Date nowDate = df.parse(creatDate);
							Date d2 = df.parse(String.valueOf(date));
							long diff = nowDate.getTime() - d2.getTime();
							long days = diff / (1000 * 60 * 60 * 24);
			
							// 判断是否超过预警期限
							if(days>=Long.parseLong(delayTime))
								haveMarked = true;
						}	
			        }
					if(date==null || date.trim().length()<=0)
					{
						String report_date = (String)bean.get("report_date"); 
						if(report_date!=null && report_date.trim().length()>0)
						{
							DateFormat df = new SimpleDateFormat("yyyy-MM-dd");				
							Date nowDate = df.parse(creatDate);
							Date d2 = df.parse(String.valueOf(report_date));
							long diff = nowDate.getTime() - d2.getTime();
							long days = diff / (1000 * 60 * 60 * 24);
			
							// 判断是否超过预警期限
							if(days>=Long.parseLong(delayTime))
								haveMarked = true;
						}
					}
					if(!haveMarked) // 过滤掉未超过预警期限的考核对象
						continue;
				}
				else if(flag!=null && flag.trim().length()>0 && "03".equalsIgnoreCase(flag)) // 过滤已经批准的目标卡
					continue;
				
				
				objStatusList.add(bean);
			}
			
			
			this.getFormHM().put("setlist",objStatusList);
			this.getFormHM().put("plan_id",plan_id);
			this.getFormHM().put("plan_name",planVo.getString("name"));
			this.getFormHM().put("leaderList", leaderList);
			this.getFormHM().put("level",level+"");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}