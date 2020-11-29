package com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.transaction;

import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryAccountBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 *<p>Title:ReExtendLastGzTrans</p> 
 *<p>Description:重发薪资数据</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:20015-9-26:下午01:12:56</p> 
 *@author lis
 */
public class ReExtendLastGzTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		String salaryid=(String)this.getFormHM().get("salaryid");
		salaryid=SafeCode.decode(salaryid); //解码
		salaryid =PubFunc.decrypt(salaryid); //解密
		
		String bosdate=(String)this.getFormHM().get("bosdate");
		String count=(String)this.getFormHM().get("count");
		try
		{
			SalaryAccountBo salaryAccountBo=new SalaryAccountBo(this.getFrameconn(),this.userView,Integer.parseInt(salaryid));
			salaryAccountBo.reExtendTheLastOne(bosdate,count);
			ContentDAO dao=new ContentDAO(this.frameconn);
			String sql = "update t_hr_pendingtask set Pending_status='4' where (Pending_status='0' or Pending_status='3') and Receiver = '"+this.userView.getUserName()+"' and ext_flag like '%_"+salaryid+"'";//薪资重发，把当前用户、当前薪资类别的待办全置成无效 zhaoxg add 2015-4-13
			dao.update(sql);
			bosdate=bosdate.replaceAll("\\.","-");
			this.getFormHM().put("ff_bosdate", SafeCode.encode(PubFunc.encrypt(bosdate)));
			this.getFormHM().put("count", SafeCode.encode(PubFunc.encrypt(count)));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
