package com.hjsj.hrms.transaction.performance.workplan.workplanteam;

import com.hjsj.hrms.businessobject.performance.WorkPlanViewBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

/**
 * 驳回未填写的工作纪实
 * @author JinChunhai
 */

public class RebutWorkPlanTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
		try
		{	
			String returnURL = (String)this.getFormHM().get("returnURL");
						
			// 如果是未填进来驳回 就先新增一条记录
			String opt = (String)this.getFormHM().get("opt");			
			String nbase = (String)this.getFormHM().get("nbase");
			String a0100 = (String)this.getFormHM().get("a0100");									
			String log_type = (String)this.getFormHM().get("log_type");			
			String state = (String)this.getFormHM().get("state");
			WorkPlanViewBo bo = new WorkPlanViewBo(this.getFrameconn(),this.getUserView(),state,nbase,a0100);
			String p0100 = bo.getP0100()+"";			
			String year_num = (String)this.getFormHM().get("year_num");
			String quarter_num = (String)this.getFormHM().get("quarter_num");
			String month_num = (String)this.getFormHM().get("month_num");
			String week_num = (String)this.getFormHM().get("week_num");
			String day_num = (String)this.getFormHM().get("day_num");				
			
		//	bo.savePlan("add", "1", "", p0100, "", new ArrayList(), new ArrayList(), Integer.parseInt(log_type), "", year_num, quarter_num, month_num, week_num, day_num);		    
						
			bo.savePlan("add", "1", "", p0100, "", new ArrayList(), new LazyDynaBean(), Integer.parseInt(log_type), "", year_num, quarter_num, month_num, week_num, day_num,"","","");
			
			
			ContentDAO dao = new ContentDAO(this.frameconn);						
			RecordVo vo = new RecordVo("p01");
			vo.setInt("p0100",Integer.parseInt(p0100));
			vo = dao.findByPrimaryKey(vo);						
			vo.setString("p0115","07");
			vo.setDate("p0116","");
			vo.setString("p0117",null);			
			vo.setString("curr_user",null);						
			dao.updateValueObject(vo);
			
			this.getFormHM().put("returnURL", returnURL);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}