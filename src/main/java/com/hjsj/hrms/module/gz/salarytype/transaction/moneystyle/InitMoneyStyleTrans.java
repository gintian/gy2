package com.hjsj.hrms.module.gz.salarytype.transaction.moneystyle;

import com.hjsj.hrms.module.gz.salarytype.businessobject.MoneyStyleSetBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * 项目名称 ：ehr7.x
 * 类名称：InitMoneyStyleTrans
 * 类描述：初始化币种
 * 创建人： lis
 * 创建时间：2015-12-3
 */
public class InitMoneyStyleTrans extends IBusiness{

	@Override
    public void execute() throws GeneralException {
		try
		{
			ArrayList moneyStyleList= new ArrayList();
			MoneyStyleSetBo bo= new MoneyStyleSetBo(this.getFrameconn());
			moneyStyleList=bo.getMoneyStyleList();
			this.getFormHM().put("moneyList",moneyStyleList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
