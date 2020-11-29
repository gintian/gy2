/**
 * 
 */
package com.hjsj.hrms.transaction.report.edit_report.reportanalyse;


import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.reportanalyse.ReportPDBAnalyse;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jul 5, 2006:4:29:13 PM</p>
 * @author zhangfengjin
 * @version 1.0
 * 
 */
public class ReportChangeCountidAnalyseTrans extends IBusiness {


	public void execute() throws GeneralException {

		String unitCode = (String)this.getFormHM().get("unitCode");
		String tabid = (String)this.getFormHM().get("tabid");
		String yearid = (String)this.getFormHM().get("yearid");
		String countid = (String)this.getFormHM().get("countid");
		
		ReportPDBAnalyse rpdba = new ReportPDBAnalyse(this.getFrameconn());
		
		rpdba.changeReportCountid(tabid,yearid,countid,unitCode,userView.getUserName(),userView.getUserId(),"");
		
		//报表数据信息
		String rgdbinfo = rpdba.getReportGridDBInfo();
		if(rgdbinfo.charAt(rgdbinfo.length()-1) =='@'){
			rgdbinfo= rgdbinfo.substring(0,rgdbinfo.length()-1);
		}
		TnameBo tbo=rpdba.getTnameBo();
	//	System.out.println("表格数据="+rgdbinfo);
		this.getFormHM().put("rows",String.valueOf(tbo.getColInfoBGrid().size()));
		this.getFormHM().put("cols",String.valueOf(tbo.getRowInfoBGrid().size()));
		this.getFormHM().put("info" , rgdbinfo);
	}

}
