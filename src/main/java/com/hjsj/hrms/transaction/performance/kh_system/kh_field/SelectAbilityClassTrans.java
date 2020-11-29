package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_field.AbilityClassBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SelectAbilityClassTrans.java</p>
 * <p>Description:能力素质课程</p>
 * <p>Company:HJHJ</p>
 * <p>create time:2011-11-11 11:11:11</p>
 * @author JinChunhai
 * @version 5.0
 */

public class SelectAbilityClassTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String personStation = (String)map.get("personStation");
			
			String point_id = (String)map.get("point_id");
			if(point_id==null || "null".equalsIgnoreCase(point_id) || point_id.trim().length()<=0)
				point_id = (String)this.getFormHM().get("point_id");
			String pointsetid = (String)map.get("pointsetid");
			if(pointsetid==null || "null".equalsIgnoreCase(pointsetid) || pointsetid.trim().length()<=0)
				pointsetid = (String)this.getFormHM().get("pointsetid");
			String subsys_id = (String)map.get("subsys_id");
			if(subsys_id==null || "null".equalsIgnoreCase(subsys_id) || subsys_id.trim().length()<=0)
				subsys_id = (String)this.getFormHM().get("subsys_id");
			
			AbilityClassBo bo = new AbilityClassBo(this.getFrameconn(),this.userView);
			
			// 新建能力素质课程表	  在启动时会自动维护此表，所以在此处去掉		 
			bo.builtPointCourseTable();			
				
			// 取得与能力指标关联的培训课程
		    ArrayList pointCourseList = bo.searchPointCourseList(point_id);
						
			this.getFormHM().put("fieldinfolist", pointCourseList);
			this.getFormHM().put("point_id", point_id);
			this.getFormHM().put("pointsetid", pointsetid);
			this.getFormHM().put("subsys_id", subsys_id);
			this.getFormHM().put("personStation", personStation);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	
}
