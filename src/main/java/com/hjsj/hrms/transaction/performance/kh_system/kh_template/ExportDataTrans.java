package com.hjsj.hrms.transaction.performance.kh_system.kh_template;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_template.KhTemplateBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * <p>Title:ExportDataTrans.java</p>
 * <p>Description>:导入模板文件</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-8-5 上午11:11:15</p>
 * <p>@version: 4.0</p>
 * <p>@author: JinChunhai
 */

public class ExportDataTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
		try
		{
			String selectid=(String)this.getFormHM().get("selectid");
			String type=(String)this.getFormHM().get("type");
			String subsys_id = (String)this.getFormHM().get("subsys_id");
			String pointsetid = (String)this.getFormHM().get("pointsetid");
			//FormFile file = (FormFile)this.getFormHM().get("templatefile");
			KhTemplateBo bo = new KhTemplateBo(this.getFrameconn());
			//bo.importData(file.getInputStream(), subsys_id);
			HashMap mp=null;
			/**导入指标*/
			bo.setPointsetid(pointsetid);
			if("1".equals(type))
			{
				mp=bo.importPointData(SafeCode.decode(selectid),this.getUserView(),subsys_id);
			}
			else
			{
				mp=bo.importData2(SafeCode.decode(selectid),this.getUserView(),subsys_id);
			}
			this.getFormHM().put("countTemplate", (String)mp.get("countTemplate"));
			this.getFormHM().put("countPoint", (String)mp.get("countPoint"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
