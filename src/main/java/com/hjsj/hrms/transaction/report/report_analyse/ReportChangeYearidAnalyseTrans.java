/**
 * 
 */
package com.hjsj.hrms.transaction.report.report_analyse;

import com.hjsj.hrms.businessobject.report.reportanalyse.ReportPDBAnalyse;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

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
		
		String userName ="";
		String userID  ="";
		if(userView.getStatus()==4&&userView.getS_userName()!=null) //自助用户关联业务用户
		{
			try
			{
				UserView _userview=new UserView(userView.getS_userName(),userView.getS_pwd()!=null?userView.getS_pwd():"",this.getFrameconn());
				_userview.canLogin();
				userName= _userview.getUserName();
				userID= _userview.getUserId();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			userName= userView.getUserName();
			userID= userView.getUserId();
		}
		String unitCode = (String)this.getFormHM().get("unitCode");
		String tabid = (String)this.getFormHM().get("tabid");
		String yearid = (String)this.getFormHM().get("yearid");
		if(!userView.isHaveResource(IResourceConstant.REPORT,tabid))
			throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
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
