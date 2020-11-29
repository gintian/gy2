package com.hjsj.hrms.module.gz.tax.transaction;

import com.hjsj.hrms.module.gz.tax.businessobject.TaxMxBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * @author wangjl
 * 所得税管理保存修改
 *
 */
public class TaxUpdateTrans extends IBusiness{

	private static final long serialVersionUID = 1L;

	@Override
	public void execute() throws GeneralException {
		try {
			String salaryid =  (String) this.getFormHM().get("salaryid");
			salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
			String tablename =  (String) this.getFormHM().get("tablename");
			@SuppressWarnings("unchecked")//要修改的全部内容
			ArrayList dateList =  (ArrayList) this.getFormHM().get("dateList");
			@SuppressWarnings("unchecked")//所有列id
			ArrayList<String> datafields = (ArrayList<String>) this.getFormHM().get("datafields");
			TaxMxBo bo = new TaxMxBo(frameconn, userView,salaryid);
			String flag = bo.updateTaxData(PubFunc.decrypt(tablename),dateList,datafields);
			this.getFormHM().put("flag", flag);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
