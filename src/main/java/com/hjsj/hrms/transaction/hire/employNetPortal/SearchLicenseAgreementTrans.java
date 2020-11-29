package com.hjsj.hrms.transaction.hire.employNetPortal;

import com.hjsj.hrms.businessobject.hire.ParameterSetBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SearchLicenseAgreementTrans extends IBusiness {
	public void execute() throws GeneralException {
		try
		{
			ParameterSetBo bo=new ParameterSetBo(this.getFrameconn());
			String licenseAgreement=bo.getLicense_agreement();
			this.getFormHM().put("licenseAgreement", licenseAgreement);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
