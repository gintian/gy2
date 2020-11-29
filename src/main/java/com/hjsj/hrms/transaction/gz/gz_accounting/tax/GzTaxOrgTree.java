package com.hjsj.hrms.transaction.gz.gz_accounting.tax;

import com.hjsj.hrms.businessobject.gz.TaxMxBo;
import com.hjsj.hrms.businessobject.gz.gz_analyse.HistoryDataBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

public class GzTaxOrgTree extends IBusiness {

	public void execute() throws GeneralException 
	{
		try
		{
			HashMap reqhm = (HashMap)this.getFormHM().get("requestPamaHM");
			String is_back = (String)reqhm.get("is_back");
			String salaryid = (String)reqhm.get("salaryid");
			if(salaryid!=null && salaryid.length()>0)
			{
				this.getFormHM().put("salaryid",salaryid);
			}else
			{
				this.getFormHM().put("salaryid","");
			}
			if(is_back!=null && is_back.length()>0)
			{
				this.getFormHM().put("is_back",is_back);
			}else
			{
				this.getFormHM().put("is_back","not");
			}
			
			
			String returnFlag=(String)reqhm.get("returnFlag");
			reqhm.remove("returnFlag");
			returnFlag=returnFlag==null?"0":returnFlag;
				
			String year="0000";
			String month ="00";
			String operOrg ="00";
			if("1".equals(returnFlag))
			{
			    year  = (String)reqhm.get("theyear");
			    month  = (String)reqhm.get("themonth");
			    operOrg = (String)reqhm.get("operOrg");
			    reqhm.remove("theyear");
			    reqhm.remove("themonth");
			    reqhm.remove("operOrg");			   	
			}			
			String filterByMdule="0";
			//liuy 2015-7-7 10838：高级花名册：非su用户登录，个人所得税高级花名册，前台取不出数据来
			TaxMxBo taxbo=new TaxMxBo(this.getFrameconn(),this.userView);
			if(taxbo.hasModulePriv())
			{
				filterByMdule="1";
			}
			HistoryDataBo bo = new HistoryDataBo(this.getFrameconn(),this.userView);
			bo.syncSalaryTaxArchiveStrut();
			this.getFormHM().put("fromTable","gz_tax_mx");
			
			this.getFormHM().put("filterByMdule", filterByMdule);
			this.getFormHM().put("returnFlag",returnFlag);
			this.getFormHM().put("theyear",year);	
			this.getFormHM().put("themonth",month);
			this.getFormHM().put("operOrg",operOrg);
			
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
}
