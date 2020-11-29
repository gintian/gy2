package com.hjsj.hrms.transaction.gz.templateset.moneystyle;

import com.hjsj.hrms.businessobject.gz.templateset.moneystyle.MoneyStyleSetBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class DeleteMoneyStyleDetailTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String nitemids=(String)this.getFormHM().get("selectID");
			String nstyleid=(String)this.getFormHM().get("styleid");
			MoneyStyleSetBo bo = new MoneyStyleSetBo(this.getFrameconn());
			bo.deleteMoneyStyleDetail(nitemids.substring(1),nstyleid);
			this.getFormHM().put("styleid",nstyleid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
