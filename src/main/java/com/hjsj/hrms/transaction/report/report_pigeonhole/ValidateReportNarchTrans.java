package com.hjsj.hrms.transaction.report.report_pigeonhole;

import com.hjsj.hrms.businessobject.report.report_pigeonhole.ReportPigeonholeBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

public class ValidateReportNarchTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String operate=(String)this.getFormHM().get("operate");					 // 1:表类  2：单表
			String selectedIDs=(String)this.getFormHM().get("selectedIDs");
			String narch=(String)this.getFormHM().get("narch");
			ReportPigeonholeBo bo=new ReportPigeonholeBo(this.getFrameconn());
			ArrayList list=bo.getUpdateReportTypeList(selectedIDs,operate,narch);
			StringBuffer info=new StringBuffer("");
			for(int i=0;i<list.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)list.get(i);
				info.append("#"+(String)abean.get("name"));
			}
			if(info.length()>0)
			{
				String temp=info.substring(1).replaceAll("\r\n","");
				this.getFormHM().put("info",SafeCode.encode(temp));
			
			}
			else
				this.getFormHM().put("info","");
			this.getFormHM().put("operate",operate);
			this.getFormHM().put("selectedIDs",selectedIDs);
			this.getFormHM().put("narch",narch);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}


	}

}
