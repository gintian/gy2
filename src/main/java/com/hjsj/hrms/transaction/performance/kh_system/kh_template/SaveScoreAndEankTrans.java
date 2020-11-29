package com.hjsj.hrms.transaction.performance.kh_system.kh_template;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_template.KhTemplateBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveScoreAndEankTrans extends IBusiness{


	public void execute() throws GeneralException {
		try
		{
			String s_str= (String)this.getFormHM().get("s_str");
			String r_str = (String)this.getFormHM().get("r_str");
			String i_s_str=(String)this.getFormHM().get("i_s_str");
			String i_r_str=(String)this.getFormHM().get("i_r_str");
			String templateID=(String)this.getFormHM().get("id");
			String subsys_id=(String)this.getFormHM().get("subsys_id");
			String status=(String)this.getFormHM().get("status");
			KhTemplateBo bo = new KhTemplateBo(this.getFrameconn());
			String msg1=bo.saveScoreRand(s_str, r_str, i_s_str, i_r_str, status, templateID);
			if(msg1==null||msg1.trim().length()==0)
			{
				msg1="ok";
			}
			this.getFormHM().put("msg",SafeCode.encode(msg1));
			this.getFormHM().put("templateid", templateID);
			this.getFormHM().put("subsys_id", subsys_id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
