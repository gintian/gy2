package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_field.AbilityClassBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:CancelAbilityClassTrans.java</p>
 * <p>Description:撤销关联的培训课程</p>
 * <p>Company:HJHJ</p>
 * <p>create time:2011-11-11 11:11:11</p>
 * @author JinChunhai
 * @version 5.0
 */

public class CancelAbilityClassTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String point_id = (String)this.getFormHM().get("point_id");
			String classIds = (String)map.get("classIds");
			classIds = classIds.replaceAll("／", "/");
			AbilityClassBo bo = new AbilityClassBo(this.getFrameconn(),this.userView);
			
			// 撤销关联的培训课程		 
			bo.cancelAbilityClass(point_id,classIds);			
									
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	
}