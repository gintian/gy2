/**
 * 
 */
package com.hjsj.hrms.transaction.report.edit_report.reportanalyse;

import com.hjsj.hrms.businessobject.report.reportanalyse.ReportPDBAnalyse;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:改变年份数据连动</p>
 * <p>Description:AJAX实现</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 5, 2006:2:32:44 PM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ReportChangeYearidAnalyseTrans extends IBusiness {

	public void execute() throws GeneralException {

		String userName = userView.getUserName();
		String userID  = userView.getUserId();
		
		String unitCode = (String)this.getFormHM().get("unitCode");
		String tabid = (String)this.getFormHM().get("tabid");
		String yearid = (String)this.getFormHM().get("yearid");
		
		ReportPDBAnalyse rpdba = new ReportPDBAnalyse(this.getFrameconn());
		
		//改变年份后的联动
		String temp = rpdba.changeReportYearid(tabid,unitCode,yearid,userName,userID);
		
		if(temp.charAt(temp.length()-1) =='@'){
			temp= temp.substring(0,temp.length()-1);
		}
		
		//报表数据信息
		String rgdbinfo = rpdba.getReportGridDBInfo();
		
		if(rgdbinfo.charAt(rgdbinfo.length()-1) =='@'){
			rgdbinfo= rgdbinfo.substring(0,rgdbinfo.length()-1);
		}
		
		String info = temp + "$$" + rgdbinfo;
		
		//System.out.println("info=" + info);
		
		this.getFormHM().put("info" , info);
		
	}

}
