/**
 * 
 */
package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hrms.frame.dao.ContentDAO;
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

	public void execute() throws GeneralException {
		String salaryid=(String)this.getFormHM().get("salaryid");
		String year=(String)this.getFormHM().get("year");
		String month=(String)this.getFormHM().get("month");
		String count=(String)this.getFormHM().get("count");
		try
		{
			
			//新建工资同月不能多次  -----北京移动
			if(SystemConfig.getPropertyValue("noManyTimes_gzPlay")!=null&& "true".equalsIgnoreCase(SystemConfig.getPropertyValue("noManyTimes_gzPlay")))
			{
				if(Integer.parseInt(count.trim())>1)
					throw GeneralExceptionHandler.Handle(new Exception("因设置了每月仅发第一次薪资，不允许再新建薪资表!"));
			}
			
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);			
			gzbo.reLoadHistoryData(year, month, count);
			ContentDAO dao=new ContentDAO(this.frameconn);
			String sql = "update t_hr_pendingtask set Pending_status='4' where (Pending_status='0' or Pending_status='3') and Receiver = '"+this.userView.getUserName()+"' and ext_flag like '%_"+salaryid+"'";//重置业务日期，把当前用户、当前薪资类别的待办全置成已办 zhaoxg add 2014-12-15
			dao.update(sql);
			this.getFormHM().put("ff_bosdate", year+"-"+month);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
}
