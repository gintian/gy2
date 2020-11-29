package com.hjsj.hrms.transaction.performance.workplan.workplanview;

import com.hjsj.hrms.businessobject.performance.WorkPlanViewBo;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SaveContentTrans.java</p>
 * <p>Description:保存计划和总结</p>
 * <p>Company:hjsj</p>
 * <p>create time:2012-06-15 11:28:36</p>
 * @author JinChunhai
 * @version 6.0
 */

public class SaveContentTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
		ContentDAO dao = null;
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String flag = (String)map.get("flag"); // flag=1 保存 flag=2 报批 =3撤回
			map.remove("flag");
			String appbody_id = (String)map.get("appbody_id"); // 审批人编号
			
			String nbase = (String)this.getFormHM().get("nbase");
			String a0100 = (String)this.getFormHM().get("a0100");
			String addORupdate = (String)this.getFormHM().get("addORupdate"); // 新增或编辑日志的标志参数						
			String planContent=(String)this.getFormHM().get("planContent");
			String p0100=(String)this.getFormHM().get("p0100");
			String log_type=(String)this.getFormHM().get("log_type");
			String workType=(String)this.getFormHM().get("workType");
			String state=(String)this.getFormHM().get("state");
			ArrayList editContentList=(ArrayList)this.getFormHM().get("editContentList");
			LazyDynaBean leaderCommandsBean=(LazyDynaBean)this.getFormHM().get("leaderCommandsBean");
			String copyToStr=(String)this.getFormHM().get("copyToStr");
			WorkPlanViewBo bo = new WorkPlanViewBo(this.getFrameconn(),this.getUserView(),state,nbase,a0100);
			String month_num=(String)this.getFormHM().get("month_num");
			String year_num=(String)this.getFormHM().get("year_num");
			String quarter_num=(String)this.getFormHM().get("quarter_num");
			String week_num=(String)this.getFormHM().get("week_num");
			String day_num=(String)this.getFormHM().get("day_num");
			String userStatus=(String)this.getFormHM().get("userStatus");
			String sp_level=(String)this.getFormHM().get("sp_level");
			String recordGradeName=(String)this.getFormHM().get("recordGradeName");
			
			if(!"3".equals(flag)){
			    bo.savePlan(addORupdate, flag, appbody_id, p0100, planContent, editContentList, leaderCommandsBean, Integer.parseInt(log_type), copyToStr, year_num, quarter_num, month_num, week_num, day_num,userStatus,sp_level,recordGradeName);
			    //报批
			    if("2".equals(flag)){
			    	PendingTask imip=new PendingTask();
				    String pre_pendingID = (String)this.getFormHM().get("pendingCode");
				    if(pre_pendingID!=null&&pre_pendingID.trim().length()>0)
					{ 
				    	//System.out.println(pre_pendingID);
						imip.updatePending("W",pre_pendingID,1,"",this.userView);
					}
				    bo.sendPending(p0100,"3",new ContentDAO(this.getFrameconn()));
			    }
		    
		    }else{//撤回 2016/1/25 wangjl
		    	dao = new ContentDAO(this.getFrameconn());
		    	ArrayList<String> plist = new ArrayList<String>();
		    	plist.add(p0100);
		    	String sql = "update p01 set p0115='01',curr_user=NULL where p0100=?";
				dao.update(sql,plist);
				//删除抄送人员
				dao.delete("delete from per_diary_actor where p0100="+p0100, new ArrayList());
				this.getFormHM().put("p0115", "01");
		    }
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(dao);
		}
		
	}

}
