package com.hjsj.hrms.transaction.performance.implement;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:ValidateDisOrStartTrans.java</p>
 * <p>Description:校验批量分发或启动考核计划</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-10-12 10:52:59</p>
 * @author JinChunhai
 * @version 5.0
 */

public class ValidateDisOrStartTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		
		String plan_ids = (String) this.getFormHM().get("plan_ids"); // 要批量分发或启动的计划编号串
		String logo = (String) this.getFormHM().get("logo");         // start || distribute
		String mode = (String) this.getFormHM().get("mode");         // 启动方式 0：打分方式 1：录入结果方式
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		String flag = "1";
		String info = "";
		String planId_s = ""; // 筛选出符合条件的计划号
		try
		{
			if(plan_ids==null || plan_ids.trim().length()<=0)
				return;
			
			plan_ids = plan_ids.substring(0, plan_ids.length() - 1);
			String[] planIds = plan_ids.replaceAll("／", "/").split("/");		
			
			// 分发考核计划
			if(logo!=null && logo.trim().length()>0 && "distribute".equalsIgnoreCase(logo))
				info += "不满足分发条件的考核计划有：\n";	
			else
				info += "不满足启动条件的考核计划有：\n";	
			
			PerformanceImplementBo pb = new PerformanceImplementBo(this.getFrameconn());
			int count = 0;
			CheckPrivSafeBo cpbo = new CheckPrivSafeBo(this.frameconn,this.userView);
			for (int i = 0; i < planIds.length; i++)
			{
				String plan_id = planIds[i];
				boolean _flag = cpbo.isHavePriv(this.userView, plan_id);
				if(!_flag){
					return;
				}
				RecordVo vo = pb.getPerPlanVo(plan_id);
				String method = String.valueOf(vo.getInt("method")); // 1:360度考核计划 2:目标评估
				//为了兼容以前的程序，如果method为空，也认为是360计划 2013.11.21 pjf
				if("0".equals(method)){
					method = "1";
				}
				String status = String.valueOf(vo.getInt("status")); // 考核计划状态
				String object_type = String.valueOf(vo.getInt("object_type"));  //1:部门 2：人员
				
				String isDistribute = "1";
				// 目标管理计划 对象类别非人员 考核主体类别没有设置"团队负责人"时 考核实施不需要"分发",
				if("2".equals(method) && !"2".equals(object_type))
				{
				    String sql = "select body_id from per_plan_body where plan_id=" + plan_id + " and body_id=-1";
				    this.frowset=dao.search(sql);
				    if(this.frowset.next())
				    	isDistribute = "1";
				    else
				    	isDistribute = "0";
				}
				
				// 分发考核计划
				if(logo!=null && logo.trim().length()>0 && "distribute".equalsIgnoreCase(logo))
				{														
					// 满足条件  1：目标计划  2：计划状态为 已发布或暂停  3：目标管理计划 对象类别非人员 考核主体类别没有设置"团队负责人"时 考核实施不需要"分发",
					if("2".equalsIgnoreCase(method) && ("3".equalsIgnoreCase(status) || "5".equalsIgnoreCase(status)) && "1".equalsIgnoreCase(isDistribute))
					{
						count++;
						planId_s+=plan_id+"/";
					}else
					{
						flag="0";																							
						info += "    " + vo.getString("name") + "  [id=" + plan_id + "].\n";
					}	
					
				}else // 启动计划
				{
					// 满足条件  1：360计划：计划状态为 已发布或暂停  2:目标计划：计划状态为 分发 3：目标管理计划 对象类别非人员 考核主体类别没有设置"团队负责人"时 考核实施不需要"分发",
					if("1".equalsIgnoreCase(method) && ("3".equalsIgnoreCase(status) || "5".equalsIgnoreCase(status)))
					{
						count++;
						planId_s+=plan_id+"/";
						
					}else if("2".equalsIgnoreCase(method) && "8".equalsIgnoreCase(status))
					{
						count++;
						planId_s+=plan_id+"/";
						
					}else if("2".equalsIgnoreCase(method) && ("3".equalsIgnoreCase(status) || "5".equalsIgnoreCase(status)) && "0".equalsIgnoreCase(isDistribute))
					{
						count++;
						planId_s+=plan_id+"/";
					}
					else
					{
						flag="0";																							
						info += "    " + vo.getString("name") + "  [id=" + plan_id + "].\n";
					}
				}
			}
			if(logo!=null && logo.trim().length()>0 && "distribute".equalsIgnoreCase(logo))
			{
				if(count==0)
					info = "所选考核计划都不满足分发条件，请检查！\n";	
				else
					info += "\n是否继续分发符合条件的考核计划？";	
			}else
			{
				if(count==0)
					info = "所选考核计划都不满足启动条件，请检查！\n";	
				else
					info += "\n是否继续启动符合条件的考核计划？";
			}
			this.getFormHM().put("flag", flag);
		    this.getFormHM().put("info", SafeCode.encode(info));
		    this.getFormHM().put("plan_ids", planId_s);
		    this.getFormHM().put("logo", logo);
		    if(mode==null || mode.trim().length()<=0 || "0".equalsIgnoreCase(mode))
		    	this.getFormHM().put("mode", "score");
		    else
		    	this.getFormHM().put("mode", "result");
		    this.getFormHM().put("count", String.valueOf(count));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	
}
