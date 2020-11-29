package com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.transaction;

import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryAccountBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：CreateNewSalaryTableTrans 
 * 类描述：新建薪资表
 * 创建人：zhaoxg
 * 创建时间：Jun 2, 2015 1:15:08 PM
 * 修改人：zhaoxg
 * 修改时间：Jun 2, 2015 1:15:08 PM
 * 修改备注： 
 * @version
 */
public class CreateNewSalaryTableTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		String salaryid=(String)this.getFormHM().get("salaryid");
		salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
		String year=(String)this.getFormHM().get("year");
		String month=(String)this.getFormHM().get("month");
		try
		{
			
			
			SalaryAccountBo bo = new SalaryAccountBo(this.frameconn,this.userView,Integer.parseInt(salaryid));
			if(month.length()==1)
				month="0"+month;
			this.getFormHM().put("ff_bosdate", SafeCode.encode(PubFunc.encrypt(year+"-"+month)));
			String conut="1";
			conut=bo.createNewGzTable(year, month);
			this.getFormHM().put("count", SafeCode.encode(PubFunc.encrypt(conut)));
			this.getFormHM().put("salaryid", salaryid);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
}
