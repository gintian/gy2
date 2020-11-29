package com.hjsj.hrms.transaction.kq.machine;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class DeleteKqRuleDataTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		String rule_id=(String)this.getFormHM().get("rule_id");		
		if(rule_id==null||rule_id.length()<=0)
		{
			return;
		}
		String del="delete from kq_data_rule where rule_id='"+rule_id+"'";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			dao.update(del);
		}catch(Exception e)
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.work.error"),"",""));	
		}
		this.getFormHM().put("rule_id","");
	}

}
