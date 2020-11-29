package com.hjsj.hrms.transaction.general.email_template;

import com.hjsj.hrms.businessobject.general.email_template.EmailTemplateBo;
import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class BrowseEmailTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
			String id=(String)map.get("id");
		//	String nbase=(String)map.get("nbase");
			String a0100=(String)map.get("a0100");
			//String i9999=(String)map.get("i");
			String[] temp=a0100.split("~");
			String salaryid=(String)this.getFormHM().get("salaryid");
			EmailTemplateBo bo = new EmailTemplateBo(this.getFrameconn());
			String tableName=this.userView.getUserName()+"_salary_"+salaryid;
			SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			String manager=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			if(manager.length()==0||this.userView.getUserName().equalsIgnoreCase(manager))
				 tableName=this.userView.getUserName()+"_salary_"+salaryid;
			else
				 tableName=manager+"_salary_"+salaryid;
			HashMap hm=bo.getBrowseEmailContent(id,temp[0].substring(3),temp[0].substring(0,3),tableName,temp[1]);
			ArrayList attachlist = bo.getBrowseEmailAttach(id);
			this.getFormHM().put("subject",(String)hm.get("subject"));
			this.getFormHM().put("address",(String)hm.get("address"));
			this.getFormHM().put("content",(String)hm.get("content"));
			this.getFormHM().put("a0101",(String)hm.get("a0101"));
			this.getFormHM().put("attachlist",attachlist);
			this.getFormHM().put("attachSize",attachlist==null?"0":(attachlist.size()+""));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
