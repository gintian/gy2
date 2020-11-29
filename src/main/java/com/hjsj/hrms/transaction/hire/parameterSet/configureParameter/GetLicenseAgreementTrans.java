package com.hjsj.hrms.transaction.hire.parameterSet.configureParameter;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class GetLicenseAgreementTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
			String flag=(String)hm.get("flag");
			String l_p_type="l";
			String parameterName="";
			if(flag==null|| "l".equalsIgnoreCase(flag))
			{
				parameterName="ZP_LICENSE_AGREEMENT";
			}
			else
			{
				l_p_type="p";
				parameterName="ZP_PROMPT_CONTENT";
			}
			String licenseAgreement="";
			String sql="select str_value from constant where UPPER(constant)='"+parameterName+"'";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql);
			while(this.frowset.next())
			{
				licenseAgreement=this.frowset.getString("str_value");
			}
			this.getFormHM().put("l_p_type", l_p_type);
			this.getFormHM().put("flag", flag);
			this.getFormHM().put("licenseAgreement", licenseAgreement);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
