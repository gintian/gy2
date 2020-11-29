package com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.transaction;

import com.hjsj.hrms.module.gz.gzspcollect.businessobject.GzSpCollectBo;
import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryAccountBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：GzAppealTrans 
 * 类描述： 薪资发放报批
 * 创建人：zhaoxg
 * 创建时间：Sep 11, 2015 2:44:42 PM
 * 修改人：zhaoxg
 * 修改时间：Sep 11, 2015 2:44:42 PM
 * 修改备注： 
 * @version
 */
public class GzAppealTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {

		String appealObject=(String)this.getFormHM().get("appealObject");//报批给appealObject
		appealObject = PubFunc.decrypt(SafeCode.decode(appealObject));
		String salaryid=(String)this.getFormHM().get("salaryid");//薪资类别号
		salaryid = PubFunc.decrypt(SafeCode.decode(salaryid));
	    String accountingdate = (String)this.getFormHM().get("appdate"); //业务日期
	    accountingdate = PubFunc.decrypt(SafeCode.decode(accountingdate));
	    String accountingcount = (String)this.getFormHM().get("count"); //次数
	    accountingcount = PubFunc.decrypt(SafeCode.decode(accountingcount));
		//是否来自待办 0 否 1是。
		String fromPending = this.getFormHM().containsKey("fromPending")?(String) this.getFormHM().get("fromPending"):"0";

		if(appealObject.trim().length()==0||salaryid.trim().length()==0||accountingdate.trim().length()==0||accountingcount.length()==0)
	    	return;
		SalaryAccountBo bo = new SalaryAccountBo(this.frameconn,this.userView,Integer.parseInt(salaryid));
// 2017-4-12 zhanghua 没看到这有啥用
//		SalaryTemplateBo gzbo=bo.getSalaryTemplateBo();
//		
//		String tableName = gzbo.getGz_tablename();
//	    StringBuffer filtersql = new StringBuffer();//前台过滤条件
//	    filtersql.append(gzbo.getfilter(tableName));
	    
	    accountingdate = accountingdate.replaceAll("\\.", "-");
	    String[] temps=accountingdate.split("-");
	    LazyDynaBean busiDate = new LazyDynaBean();
	    busiDate.set("year", temps[0]);
	    busiDate.set("month", temps[1]);
	    busiDate.set("count", accountingcount); 
	    bo.gzDataAppeal(appealObject, busiDate);

		//如果从待办进入，则查询剩余可审批的数据条数
		if("1".equals(fromPending)){
			GzSpCollectBo spbo = new GzSpCollectBo(this.userView,this.frameconn);
			int pengdingNum=spbo.getRemainderNumber(this.userView.getUserName()+"_salary_"+salaryid,salaryid,accountingdate,accountingcount);
			this.getFormHM().put("lastNumber",pengdingNum);
		}
	}

}
