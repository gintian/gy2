/**
 * 
 */
package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 *<p>Title:ReExtendLastGzTrans</p> 
 *<p>Description:重发薪资数据</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-9-26:下午01:12:56</p> 
 *@author cmq
 *@version 4.0
 */
public class ReExtendLastGzTrans extends IBusiness {

	public void execute() throws GeneralException {
		String salaryid=(String)this.getFormHM().get("salaryid");
		String bosdate=(String)this.getFormHM().get("bosdate");
		String count=(String)this.getFormHM().get("count");
		
		try
		{
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			gzbo.reExtendTheLastOne(bosdate,count);
			ContentDAO dao=new ContentDAO(this.frameconn);
			String sql = "update t_hr_pendingtask set Pending_status='4' where (Pending_status='0' or Pending_status='3') and Receiver = '"+this.userView.getUserName()+"' and ext_flag like '%_"+salaryid+"'";//薪资重发，把当前用户、当前薪资类别的待办全置成无效 zhaoxg add 2015-4-13
			dao.update(sql);

		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
