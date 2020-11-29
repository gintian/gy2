package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_field.KhFieldBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * <p>Title:InitGradeTemplateTrans.java</p>
 * <p>Description:新建标准标度</p>
 * <p>Company:HJHJ</p>
 * <p>Create time:2008-07-21</p> 
 * @author JinChunhai
 * @version 1.0
 */

public class InitGradeTemplateTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
		try
		{
			String subsys_id = (String)this.getFormHM().get("subsys_id");
			KhFieldBo bo = new KhFieldBo(this.getFrameconn());
			ArrayList gradeList = bo.getGradeTemplateList(subsys_id);
			this.getFormHM().put("gradeList",gradeList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
