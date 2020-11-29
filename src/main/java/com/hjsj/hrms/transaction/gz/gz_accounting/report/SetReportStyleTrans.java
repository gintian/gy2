package com.hjsj.hrms.transaction.gz.gz_accounting.report;

import com.hjsj.hrms.businessobject.gz.SalaryReportBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Nov 22, 2007</p> 
 *@author dengcan
 *@version 4.0
 */
public class SetReportStyleTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String opt=(String)this.getFormHM().get("opt");  // 1:文字排列方式 2:显示格式 3日期格式 4列标题
			String rsid=(String)this.getFormHM().get("rsid"); //报表种类编号 
			String rsdtlid=(String)this.getFormHM().get("rsdtlid"); //报表编号 
			String salaryid=(String)this.getFormHM().get("salaryid");
			SalaryReportBo gzbo=new SalaryReportBo(this.getFrameconn(),salaryid);
			
			String itemid=(String)this.getFormHM().get("itemid");
			String value=(String)this.getFormHM().get("value");
			if("1".equals(opt))
				gzbo.updateReportItemVo(rsdtlid,itemid,"align",value);
			else if("2".equals(opt)|| "3".equals(opt))
				gzbo.updateReportItemVo(rsdtlid,itemid,"itemfmt",value);
			else if("4".equals(opt))
				gzbo.updateReportItemVo(rsdtlid,itemid,"itemdesc",value);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
