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
 * 类名称：GzReportTrans 
 * 类描述： 薪资发放报审和驳回功能
 * 创建人：zhaoxg
 * 创建时间：Sep 2, 2015 4:03:44 PM
 * 修改人：zhaoxg
 * 修改时间：Sep 2, 2015 4:03:44 PM
 * 修改备注： 
 * @version
 */
public class GzReportTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try{
			String salaryid=(String)this.getFormHM().get("salaryid");
			salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
			String opt=(String)this.getFormHM().get("opt");  //1:驳回 2：报审
		    SalaryAccountBo bo = new SalaryAccountBo(this.frameconn,this.userView,Integer.parseInt(salaryid));
			if("2".equals(opt)){
				String gz_module = (String) this.getFormHM().get("imodule");//薪资和保险区分标识  1：保险  否则是薪资
				gz_module = PubFunc.decrypt(SafeCode.decode(gz_module));
			    String accountingdate = (String)this.getFormHM().get("appdate"); //业务日期
			    accountingdate = PubFunc.decrypt(SafeCode.decode(accountingdate));
			    String accountingcount = (String)this.getFormHM().get("count"); //发放次数
			    accountingcount = PubFunc.decrypt(SafeCode.decode(accountingcount));
				bo.gzDataReport(gz_module,accountingdate, accountingcount);
			}
			else if("1".equals(opt))
			{
				String selectGzRecords = (String)this.getFormHM().get("selectGzRecords");
				selectGzRecords = selectGzRecords.replaceAll("＃", "#").replaceAll("／", "/"); 
				String rejectCause = (String)this.getFormHM().get("rejectCause");
				bo.gzDataReportReject(selectGzRecords, rejectCause);
			}
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
