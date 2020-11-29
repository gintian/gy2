package com.hjsj.hrms.transaction.sys.options.customreport;

import com.hjsj.hrms.businessobject.report.user_defined_reoprt.UserdefinedReport;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 显示自定义报表
 * <p>Title:DisplayCustomReportTrans.java</p>
 * <p>Description>:DisplayCustomReportTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Mar 11, 2010 11:27:30 AM</p>
 * <p>@version: 4.0</p>
 * <p>@author: s.xin
 */
public class DisplayCustomReportTrans extends IBusiness {

	public void execute() throws GeneralException {
		String isprivstr=(String)this.getFormHM().get("ispriv");//是否加有权限
		String report_id=(String)this.getFormHM().get("id");//自定义表格id
		boolean ispriv=false;
		if(isprivstr!=null&& "1".equals(isprivstr))
			ispriv=true;
		UserdefinedReport userdefinedReport=new UserdefinedReport(userView,this.getFrameconn(),report_id,ispriv);		
		String ext=userdefinedReport.getExt();
		String filename="";
		if(ext.indexOf("xls")!=-1 || ext.indexOf("xlt")!=-1)
		{
			filename=userdefinedReport.analyseUserdefinedExcelReport();
		}		
		this.getFormHM().put("filename", filename);
		this.getFormHM().put("ext", ext);
		
	}

}
