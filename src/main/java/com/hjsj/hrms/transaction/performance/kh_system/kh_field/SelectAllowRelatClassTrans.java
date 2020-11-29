package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_field.AbilityClassBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SelectAllowRelatClassTrans.java</p>
 * <p>Description:可以关联的能力素质课程</p>
 * <p>Company:HJHJ</p>
 * <p>create time:2011-11-11 11:11:11</p>
 * @author JinChunhai
 * @version 5.0
 */

public class SelectAllowRelatClassTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
						
			String point_id = (String)this.getFormHM().get("point_id");
			String pointsetid = (String)this.getFormHM().get("pointsetid");
			String subsys_id = (String)this.getFormHM().get("subsys_id");
			
			String itemize = (String)this.getFormHM().get("itemize");			
			String coursename = (String)this.getFormHM().get("coursename");			
			String courseintro = (String)this.getFormHM().get("courseintro");			
			
			AbilityClassBo bo = new AbilityClassBo(this.getFrameconn(),this.userView);								
				
			// 取得与能力指标关联的培训课程
		    ArrayList allowPointCourseList = bo.searchAllowPointCourseList(point_id,itemize,coursename,courseintro);
						
			this.getFormHM().put("fieldinfolist", allowPointCourseList);
			this.getFormHM().put("point_id", point_id);
			this.getFormHM().put("pointsetid", pointsetid);
			this.getFormHM().put("subsys_id", subsys_id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	
}
