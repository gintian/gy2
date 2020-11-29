package com.hjsj.hrms.transaction.performance.kh_system.kh_template;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_template.KhTemplateBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class IsTemplateUsedTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			//String templateid=(String)this.getFormHM().get("templateid");
			String type=(String)this.getFormHM().get("type");
			String templateid=(String)this.getFormHM().get("templatesetid");
			String subsysid=(String)this.getFormHM().get("subsys_id");
			KhTemplateBo bo = new KhTemplateBo(this.getFrameconn(),"1");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			boolean bool=bo.templateIsUsed(templateid, dao);
			//针对职称测评表打分，如果有提交打分的人，则不允许修改 haosl 2019-6-10
            boolean bool2 = bo.templateIsUsedByJobtile(templateid,dao);
			this.getFormHM().put("type",type);
			this.getFormHM().put("tt",templateid);
			this.getFormHM().put("subsys_id",subsysid);
			this.getFormHM().put("msg",bool?"1":"0");
            this.getFormHM().put("msg2",bool2?"1":"0");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
