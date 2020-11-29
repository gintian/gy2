package com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.transaction;

import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryAccountBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 *<p>Title:ReSetGzDateTrans</p> 
 *<p>Description:重置业务日期</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-9-21:下午03:06:27</p> 
 *@author cmq
 *@version 4.0
 */
public class ReSetGzDateTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		String salaryid=(String)this.getFormHM().get("salaryid");
		salaryid=SafeCode.decode(salaryid); //解码
		salaryid =PubFunc.decrypt(salaryid); //解密
		
		String year=(String)this.getFormHM().get("year");//年
		String month=(String)this.getFormHM().get("month");//月
		String count=(String)this.getFormHM().get("count");//次数
		try
		{
			//新建工资同月不能多次  -----北京移动
			if(SystemConfig.getPropertyValue("noManyTimes_gzPlay")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("noManyTimes_gzPlay")))
			{
				if(Integer.parseInt(count.trim())>1)
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz_new.gz_accounting.bjyd_cannot_create")));//因设置了每月仅发第一次薪资，不允许再新建薪资表
			}
			if(month.length()==1)
				month="0"+month;
			SalaryAccountBo accountBo=new SalaryAccountBo(this.getFrameconn(), this.userView, Integer.valueOf(salaryid));			
			accountBo.reLoadHistoryData(year, month, count);//重置业务日期
			this.getFormHM().put("ff_bosdate", SafeCode.encode(PubFunc.encrypt(year+"-"+month)));
			this.getFormHM().put("count", SafeCode.encode(PubFunc.encrypt(count)));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
}
