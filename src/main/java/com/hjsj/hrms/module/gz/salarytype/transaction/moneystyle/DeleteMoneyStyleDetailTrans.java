package com.hjsj.hrms.module.gz.salarytype.transaction.moneystyle;

import com.hjsj.hrms.module.gz.salarytype.businessobject.MoneyStyleSetBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 项目名称 ：ehr7.x
 * 类名称：DeleteMoneyStyleDetailTrans
 * 类描述：删除货币详细
 * 创建人： lis
 * 创建时间：2015-12-3
 */
public class DeleteMoneyStyleDetailTrans extends IBusiness{

	@Override
    public void execute() throws GeneralException {
		try
		{
			String nitemids=(String)this.getFormHM().get("selectIDs");
			String nstyleid=(String)this.getFormHM().get("styleid");
			MoneyStyleSetBo bo = new MoneyStyleSetBo(this.getFrameconn());
			bo.deleteMoneyStyleDetail(nitemids.substring(1),nstyleid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
