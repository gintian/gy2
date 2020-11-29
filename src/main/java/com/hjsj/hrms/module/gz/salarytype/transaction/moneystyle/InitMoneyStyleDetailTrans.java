package com.hjsj.hrms.module.gz.salarytype.transaction.moneystyle;

import com.hjsj.hrms.module.gz.salarytype.businessobject.MoneyStyleSetBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * 项目名称 ：ehr7.x
 * 类名称：InitMoneyStyleDetailTrans
 * 类描述：初始化货币详细
 * 创建人： lis
 * 创建时间：2015-12-3
 */
public class InitMoneyStyleDetailTrans extends IBusiness{

	@Override
    public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM();
			String nstyleid=(String)hm.get("nstyleid");
			String columns="nstyleid,nitemid,cname";
			MoneyStyleSetBo bo = new MoneyStyleSetBo(this.getFrameconn());
			this.getFormHM().put("moneyDetailList",bo.getMoneyStyleDetailInfo(nstyleid));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
