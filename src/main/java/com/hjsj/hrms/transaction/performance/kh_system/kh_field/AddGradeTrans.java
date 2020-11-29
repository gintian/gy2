package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_field.KhFieldBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.HashMap;

/**
 * <p>Title:AddGradeTrans.java</p>
 * <p>Description:新增或编辑标准标度</p>
 * <p>Company:HJHJ</p>
 * <p>Create time:2008-07-21</p> 
 * @author JinChunhai
 * @version 1.0
 */

public class AddGradeTrans extends IBusiness
{
	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String type=(String)map.get("type");
			String gradeid=(String)map.get("gradeid");
			String gradecode="";
			String gradedesc="";
			String gradevalue="";
			String top_value="";
			String bottom_value="";
			if("1".equals(type))//new
			{
				
			}
			else//edit
			{
				String subsys_id = (String)this.getFormHM().get("subsys_id");
				KhFieldBo bo = new KhFieldBo(this.getFrameconn());
				LazyDynaBean bean = bo.getGradeInfoById(gradeid,subsys_id);
				gradeid=(String)bean.get("gradeid");
				gradedesc=(String)bean.get("gradedesc");
				top_value=(String)bean.get("top_value");
				bottom_value=(String)bean.get("bottom_value");
				gradevalue=(String)bean.get("gradevalue");
			}
			this.getFormHM().put("type",type);
			this.getFormHM().put("grade_id", gradeid);
			this.getFormHM().put("hiddenGradeid",gradeid);
			this.getFormHM().put("gradevalue", gradevalue);
			this.getFormHM().put("bottom_value", bottom_value);
			this.getFormHM().put("gradedesc", gradedesc);
			this.getFormHM().put("top_value", top_value);
			this.getFormHM().put("isClose","2");
			this.getFormHM().put("isrefresh", "1");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
	    }
		
	}

}
