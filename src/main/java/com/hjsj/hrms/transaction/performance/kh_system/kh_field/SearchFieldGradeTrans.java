package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_field.KhFieldBo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchFieldGradeTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
			String point_id = (String)map.get("point_id");
			if(!this.userView.isHaveResource(IResourceConstant.KH_FIELD, point_id)&&!this.userView.isHaveResource(IResourceConstant.KH_FIELD, point_id+"R") && !"-1".equals(point_id)&&!"".equals(point_id))//根节点的point_id是-1  郭峰修改
				throw GeneralExceptionHandler.Handle(new Exception("您没有该指标类别的资源权限!"));
			String subsys_id = (String)this.getFormHM().get("subsys_id"); 
			KhFieldBo bo = new KhFieldBo(this.getFrameconn());
			ArrayList fieldGradeList = bo.getFieldGrade(point_id,subsys_id);
			this.getFormHM().put("fieldGradeList",fieldGradeList);
			this.getFormHM().put("grade_point_id", point_id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
