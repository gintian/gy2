package com.hjsj.hrms.transaction.performance.warnPlan;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchSelfNoScoreorCardTrans.java</p>
 * <p>Description:展示预警提醒自助用户登录平台如果本人没有按规定期限内评分或制定及审批目标卡的绩效计划</p>
 * <p>Company:hjsj</p>
 * <p>create time:2012-05-28 09:41:14</p>
 * @author JinChunhai
 * @version 6.0
 */

public class SearchSelfNoScoreorCardTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		
		try
		{		
			
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");		
			String plan_ids = (String)hm.get("plan_ids"); // 预警计划编号	
			plan_ids = PubFunc.keyWord_reback(plan_ids);
			ArrayList planList = new ArrayList();
			
			RecordVo vo = null;
			if(plan_ids!=null && plan_ids.trim().length()>0)
			{
				String[] matters = plan_ids.split(";");
				for (int i = 0; i < matters.length; i++)
				{
				    String plan_id = matters[i];
				    if(plan_id.indexOf("@")!=-1)
				    	vo = getPlanVo(plan_id.substring(plan_id.indexOf("@")+1,plan_id.length()));
				    else if(plan_id.indexOf("$")!=-1)
				    	vo = getPlanVo(plan_id.substring(plan_id.indexOf("$")+1,plan_id.length()));
				    else if(plan_id.indexOf("*")!=-1)
				    	vo = getPlanVo(plan_id.substring(plan_id.indexOf("*")+1,plan_id.length()));
				    
				    LazyDynaBean abean = new LazyDynaBean();
				    abean.set("plan_id", plan_id.substring(plan_id.indexOf("@")+1,plan_id.length()));
				    if(plan_id.indexOf("@")!=-1)
				    {
				    	if("0".equalsIgnoreCase(plan_id.substring(0,plan_id.indexOf("@"))))
				    		abean.set("name", vo.getString("name")+" 您处于预警期间内，请进行评分");
				    	else
				    		abean.set("name", vo.getString("name")+" 您已延期 "+plan_id.substring(0,plan_id.indexOf("@"))+" 天未完成评分");
				    }else if(plan_id.indexOf("$")!=-1)
				    {
				    	if("0".equalsIgnoreCase(plan_id.substring(0,plan_id.indexOf("$"))))
				    		abean.set("name", vo.getString("name")+" 您处于预警期间内，请进行目标卡制订");
				    	else
				    		abean.set("name", vo.getString("name")+" 您已延期 "+plan_id.substring(0,plan_id.indexOf("$"))+" 天未完成目标卡制订");
				    }
				    else if(plan_id.indexOf("*")!=-1)
				    {
				    	if("0".equalsIgnoreCase(plan_id.substring(0,plan_id.indexOf("*"))))
				    		abean.set("name", vo.getString("name")+" 您处于预警期间内，请进行目标卡审批");
				    	else
				    		abean.set("name", vo.getString("name")+" 您已延期 "+plan_id.substring(0,plan_id.indexOf("*"))+" 天未完成目标卡审批");
				    }
				    planList.add(abean);
				}
			}
			
			this.getFormHM().put("planList",planList);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * plan_id 计划号
	 * 获得某编号的考核计划的所有信息
	 */
	public RecordVo getPlanVo(String plan_id)
	{
		RecordVo vo=new RecordVo("per_plan");
		try
		{
			vo.setInt("plan_id",Integer.parseInt(plan_id));
			ContentDAO dao = new ContentDAO(this.frameconn);
			vo=dao.findByPrimaryKey(vo);					
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return vo;
	}
}