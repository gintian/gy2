package com.hjsj.hrms.transaction.performance.markStatus;

import com.hjsj.hrms.businessobject.performance.markStatus.ScoreStatusBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * <p>Title:SelectScoreStatusListTrans.java</p>
 * <p>Description:考评进度统计表</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-06-30 11:11:11</p>
 * @author JinChunhai
 * @version 5.0
 */

public class SelectScoreStatusListTrans extends IBusiness
{
	
	private String level = "1";              // 部门层级
	
	public void execute() throws GeneralException 
	{
		
		RowSet rowSet = null;
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String b_select=(String)hm.get("b_select");
						
			String checkPlanId=PubFunc.decrypt((String)this.getFormHM().get("checkPlanId"));    // 打分页面选中的考核计划id
			String consoleType=(String)this.getFormHM().get("consoleType");    // 业务平台进入标志  1.业务平台进入 2.自助平台进入
			String selectFashion=(String)this.getFormHM().get("selectFashion");  // 查询方式 1:按考核主体  2:考核对象
			String department=(String)this.getFormHM().get("department");  // 考核主体/对象 的所属部门
			
			String scoreType = "all";
			String e0122Level = "all";
			if(b_select!=null && "query".equalsIgnoreCase(b_select))
			{
				scoreType = (String)this.getFormHM().get("scoreType");    // 评分状态
				e0122Level = (String)this.getFormHM().get("e0122Level");    // 部门层级
			}						
			if(e0122Level!=null && e0122Level.trim().length()>0)
				this.level = e0122Level;
			
			ScoreStatusBo sb=new ScoreStatusBo(this.getFrameconn(),this.userView);
						
			// 获得某编号的考核计划的所有信息
			RecordVo planVo=sb.getPlanVo(checkPlanId);			
			
			LinkedHashMap map = new LinkedHashMap();
			if("1".equalsIgnoreCase(selectFashion))
			{
				//当按考核主体统计时新建临时表并进行的一些操作				 
				sb.operaTempTable(checkPlanId,this.level);
				
				//考核主体时：统计未评分、正评分、已评分的记录				 
				map = sb.getMainbodyMap();				
			
			}else if("2".equalsIgnoreCase(selectFashion))
			{
				//当按考核对象统计时新建临时表并进行的一些操作				 
				sb.operaTempObjectTable(checkPlanId,this.level);
				
				//考核对象时：统计未评分、正评分、已评分的记录				 
				map = sb.getObjectMap();	
			}			
			
			this.getFormHM().put("personScoreMap",map);
			this.getFormHM().put("planName",planVo.getString("name"));
			this.getFormHM().put("e0122Level",this.level);
			this.getFormHM().put("scoreType",scoreType);
			this.getFormHM().put("e0122LevelList",sb.getE0122LevelList());
			this.getFormHM().put("scoreTypeList",sb.getScoreTypeList());
			
			if(rowSet!=null)
				rowSet.close();			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}		
	}	
}
