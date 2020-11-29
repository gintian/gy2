package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Date;
/**
 *<p>Title:SyncGzSpEmpTrans</p> 
 *<p>Description:薪资审批人员同步</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-9-14:下午01:20:22</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class SyncGzSpEmpTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		String salaryid=(String)this.getFormHM().get("salaryid");
		String bosdate=(String)this.getFormHM().get("bosdate");
		try
		{
			//如果用户没有当前薪资类别的资源权限   20140926  dengcan
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,null);
			/**薪资类别*/
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			gzbo.syncGzTableStruct();
			if(bosdate==null|| "".equalsIgnoreCase(bosdate))
				bosdate=PubFunc.FormatDate(new Date(), "yyyy.MM.dd");			
			/**同步人员顺序*/
			gzbo.syncGzSpEmp(salaryid,bosdate);
			this.getFormHM().put("salaryid",salaryid);
			// zgd 2015-1-21 update 薪资审批增加人员排序 同步人员顺序走新order start
			this.getFormHM().put("order_by", SafeCode.encode(PubFunc.encrypt("sync")));
			// zgd 2015-1-21 update 薪资审批增加人员排序 同步人员顺序走新order end
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		
	}
}











