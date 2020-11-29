package com.hjsj.hrms.module.gz.salaryaccounting.updisk.transaction;

import com.hjsj.hrms.module.gz.salaryaccounting.updisk.businessobject.BankDiskSetBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 
 * 项目名称：hcm7.x 类名称：DeleteBankTemplateTrans 类描述：编辑银行报盘交易类 创建人：sunming 创建时间：2015-9-7
 * 
 * @version
 */
public class DeleteBankTemplateTrans extends IBusiness{

	@Override
    public void execute() throws GeneralException {
		try
		{
			String bank_id = (String)this.getFormHM().get("bankid");
			String salaryid=(String)this.getFormHM().get("salaryid");
			salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
			BankDiskSetBo bo = new BankDiskSetBo(this.getFrameconn(), Integer
					.parseInt(salaryid), this.userView);
			bo.deleteBankInfo("gz_bank",bank_id);
			bo.deleteBankInfo("gz_bank_item",bank_id);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		
	}

}
