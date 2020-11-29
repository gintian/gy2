package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SendEmailBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SearchGzEmailTemplateTrans.java</p>
 * <p>Decsription:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-9-7 10:30:26</p>
 * @author LiZhenWei
 * @version 4.0
 */
public class SearchGzEmailTemplateTrans extends IBusiness{

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap map =(HashMap)this.getFormHM().get("requestPamaHM");
			String code=(String)map.get("code");
			String salaryid=(String)map.get("salaryid");
			String input_type=(String)map.get("input_type");
			String num=(String)map.get("num");
			SendEmailBo bo = new SendEmailBo(this.getFrameconn());
			ArrayList templateList = bo.getEmailTemplateList();
			this.getFormHM().put("templateList",templateList);
			this.getFormHM().put("code",code);
			this.getFormHM().put("salaryid",salaryid);
			this.getFormHM().put("input_type",input_type);
			this.getFormHM().put("num",num);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}


}
