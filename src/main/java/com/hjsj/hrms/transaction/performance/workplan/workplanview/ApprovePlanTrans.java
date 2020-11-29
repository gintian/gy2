package com.hjsj.hrms.transaction.performance.workplan.workplanview;

import com.hjsj.hrms.businessobject.performance.WorkPlanViewBo;
import com.hjsj.hrms.interfaces.general.PendingTask;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 批准或驳回计划或总结
 * @author JinChunhai
 */

public class ApprovePlanTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String flag = (String)map.get("flag"); // flag 1:批准 2:驳回			
			String p0100 = (String)this.getFormHM().get("p0100");
			String nnbase = (String)this.getFormHM().get("nbase");
			String aa0100 = (String)this.getFormHM().get("a0100");
			LazyDynaBean leaderCommandsBean=(LazyDynaBean)this.getFormHM().get("leaderCommandsBean");
			String userStatus=(String)this.getFormHM().get("userStatus");
			String recordGradeName=(String)this.getFormHM().get("recordGradeName");
			String sp_level=(String)this.getFormHM().get("sp_level");
			String sp_relation=(String)this.getFormHM().get("sp_relation");
			String addORupdate = (String)this.getFormHM().get("addORupdate"); // 新增或编辑日志的标志参数
			String state=(String)this.getFormHM().get("state");
			WorkPlanViewBo bo1 = new WorkPlanViewBo(this.getUserView(),this.getFrameconn(),state);
			// 如果是未填进来驳回 就先新增一条记录
			if(addORupdate!=null && addORupdate.trim().length()>0 && "add".equalsIgnoreCase(addORupdate))
			{			
				String nbase = (String)this.getFormHM().get("nbase");
				String a0100 = (String)this.getFormHM().get("a0100");									
				String planContent=(String)this.getFormHM().get("planContent");
				String log_type=(String)this.getFormHM().get("log_type");
				String workType=(String)this.getFormHM().get("workType");
				ArrayList editContentList=(ArrayList)this.getFormHM().get("editContentList");
				String copyToStr=(String)this.getFormHM().get("copyToStr");
				WorkPlanViewBo bo = new WorkPlanViewBo(this.getFrameconn(),this.getUserView(),state,nbase,a0100);
				String month_num=(String)this.getFormHM().get("month_num");
				String year_num=(String)this.getFormHM().get("year_num");
				String quarter_num=(String)this.getFormHM().get("quarter_num");
				String week_num=(String)this.getFormHM().get("week_num");
				String day_num=(String)this.getFormHM().get("day_num");
				
			    bo.savePlan(addORupdate, "1", "", p0100, planContent, editContentList, leaderCommandsBean, Integer.parseInt(log_type), copyToStr, year_num, quarter_num, month_num, week_num, day_num,userStatus,sp_level,recordGradeName);		    
			}
			
			
			ContentDAO dao = new ContentDAO(this.frameconn);						
			RecordVo vo = new RecordVo("p01");
			vo.setInt("p0100",Integer.parseInt(p0100));
			vo = dao.findByPrimaryKey(vo);
			PendingTask imip=new PendingTask();
			String pre_pendingID = (String)this.getFormHM().get("pendingCode");
			if(flag!=null && flag.trim().length()>0 && "1".equalsIgnoreCase(flag))
			{
				vo.setString("p0115","03");
				vo.setDate("p0116",new Date());
				vo.setString("p0117",this.userView.getUserFullName());
				vo.setString("curr_user",null);
				//批准置为已批
				//将旧的代办信息置为已处理状态 
				if(pre_pendingID!=null&&pre_pendingID.trim().length()>0)
				{ 
					imip.updatePending("W",pre_pendingID,1,"",this.userView);
				}

			}
			else
			{
				vo.setString("p0115","07");
				vo.setDate("p0116","");
				vo.setString("p0117",null);
				
				vo.setString("curr_user",bo1.getRejectTo(Integer.parseInt(p0100),aa0100,nnbase,sp_relation));
				
			}
			
			if(userStatus!=null && !"".equalsIgnoreCase(userStatus) && !"0".equalsIgnoreCase(userStatus)){
				//领导登录才可以保存领导批示
				// 领导批示
				WorkPlanViewBo bo = new WorkPlanViewBo(this.getUserView(),this.getFrameconn());
				if(leaderCommandsBean!=null)
				{
					String uvA0100 = userView.getA0100();
					String uvNbase = userView.getDbname();
					String uvA0101 = userView.getUserFullName();
					String uvFullName = userView.getUserFullName();
					String uvB0110 = userView.getUserOrgId();
					String uvE0122 = userView.getUserDeptId();
					String uvE01a1 = userView.getUserPosId();
					
					
					RecordVo revo = new RecordVo("per_diary_opinion");
					String value2 = "";
					if(leaderCommandsBean.get("value2")!=null){
						value2 = (String)leaderCommandsBean.get("value2");
					}
					int id = bo.getLastEdit(Integer.parseInt(p0100), uvA0100, uvNbase);
					int saveid = id;
					if(id==0){
						saveid = bo.getId();
					}
					revo.setInt("id",saveid);
					if(id!=0){
						revo = dao.findByPrimaryKey(revo);
					}
					revo.setString("p0100",p0100);
					revo.setString("b0110", uvB0110);
					revo.setString("e0122", uvE0122);
					revo.setString("e01a1", uvE01a1);
					revo.setString("nbase", uvNbase);
					revo.setString("a0100", uvA0100);
					revo.setString("a0101", uvA0101);
					String relation = "";
					if(WorkPlanViewBo.workParametersMap.get("sp_relation")!=null){
						relation = (String)WorkPlanViewBo.workParametersMap.get("sp_relation");
					}
					int uvSpgrade = Integer.parseInt(!"".equals(bo.whichCurrentLevel(uvNbase, uvA0100, nnbase, aa0100, dao,relation ))?bo.whichCurrentLevel(uvNbase, uvA0100, nnbase, aa0100, dao,relation ):"0");
					if(uvSpgrade==0){
						String [] strArray = bo.getInfo(uvNbase, uvA0100, nnbase, aa0100, dao, sp_relation);
						if(strArray!=null){
							uvSpgrade = Integer.parseInt(strArray[1]);
						}
					}
					revo.setInt("sp_grade", uvSpgrade);
					revo.setString("description", value2);
					revo.setString("pg_code", recordGradeName);
					revo.setDate("sp_date", new Date());
					if(id!=0){
						dao.updateValueObject(revo);
					}else{
						dao.addValueObject(revo);
					}				
				}
			}
			dao.updateValueObject(vo);
			//驳回置为待批
			if(pre_pendingID!=null&&pre_pendingID.trim().length()>0)
			{ 
				//System.out.println(pre_pendingID);
				imip.updatePending("W",pre_pendingID,1,"",this.userView);
			}
			//新建驳回待办 驳回给用户还是驳回给中间领导
			if(vo.getString("curr_user")!=null && !"".equals(vo.getString("curr_user"))){
				bo1.sendPending(p0100,"3",new ContentDAO(this.getFrameconn()));
			}else{
				bo1.sendRejectPending(p0100,"7",dao);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}