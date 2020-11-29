/**
 * 
 */
package com.hjsj.hrms.transaction.report.report_analyse;

import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.reportanalyse.ReportPDBAnalyse;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

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
		try
		{
			String unitCode = (String)this.getFormHM().get("unitCode");
			String tabid = (String)this.getFormHM().get("tabid");
			String yearid = (String)this.getFormHM().get("yearid");
			String countid = (String)this.getFormHM().get("countid");
			String reportTypes=(String)this.getFormHM().get("reportTypes");
			/*
			System.out.println("unitCode=" + unitCode);
			System.out.println("tabid=" + tabid);
			System.out.println("yearid=" + yearid);
			System.out.println("countid=" + countid);
			*/
			
			ReportPDBAnalyse rpdba = new ReportPDBAnalyse(this.getFrameconn());
			rpdba.setReportTypes(reportTypes);
			String weekid="";
			if(reportTypes!=null&& "6".equals(reportTypes))
				weekid=(String)this.getFormHM().get("weekid");
			
			
			if(userView.getStatus()==4&&userView.getS_userName()!=null) //自助用户关联业务用户
			{
				UserView _userview=new UserView(userView.getS_userName(),userView.getS_pwd()!=null?userView.getS_pwd():"",this.getFrameconn());
				_userview.canLogin();
				rpdba.changeReportCountid(tabid,yearid,countid,unitCode,_userview.getUserName(),_userview.getUserId(),weekid);
			}
			else
				rpdba.changeReportCountid(tabid,yearid,countid,unitCode,userView.getUserName(),userView.getUserId(),weekid);
			
			//报表数据信息
			String rgdbinfo = rpdba.getReportGridDBInfo();
			if(rgdbinfo.charAt(rgdbinfo.length()-1) =='@'){
				rgdbinfo= rgdbinfo.substring(0,rgdbinfo.length()-1);
			}
			
			TnameBo tbo=rpdba.getTnameBo();
			//System.out.println("表格数据="+rgdbinfo);
			this.getFormHM().put("rows",String.valueOf(tbo.getColInfoBGrid().size()));
			this.getFormHM().put("cols",String.valueOf(tbo.getRowInfoBGrid().size()));
			
			this.getFormHM().put("info" , rgdbinfo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
