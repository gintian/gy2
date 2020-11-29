package com.hjsj.hrms.transaction.report.report_analyse;
import com.hjsj.hrms.businessobject.report.ReportExcelBo;
import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.TnameExtendBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

/**
 * 
 *<p>Title:</p> 
 *<p>Description:输出excel</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 20, 2007</p> 
 *@author dengcan
 *@version 4.0
 */
public class ExportReportExcelTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String tabid=(String)this.getFormHM().get("tabid");
			if(!userView.isHaveResource(IResourceConstant.REPORT,tabid))
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
			String unitcode=(String)this.getFormHM().get("unitCode");
			String yearid=(String)this.getFormHM().get("yearid");
			String countid=(String)this.getFormHM().get("countid");
			String reportTypes=(String)this.getFormHM().get("reportTypes");
			String weekid="";
			if("6".equals(reportTypes))
				weekid=(String)this.getFormHM().get("weekid");
			String scopeid = (String)this.getFormHM().get("scopeid");
			UserView _userview=null;
			if(userView.getStatus()==4&&userView.getS_userName()!=null) //自助用户关联业务用户
			{
				_userview=new UserView(userView.getS_userName(),userView.getS_pwd()!=null?userView.getS_pwd():"",this.getFrameconn());
				_userview.canLogin();
			}
			
			ReportExcelBo bbo=null;
			if(userView.getStatus()==4&&userView.getS_userName()!=null) //自助用户关联业务用户
				bbo=new ReportExcelBo(_userview,tabid,unitcode,"3",this.getFrameconn(),yearid,countid,weekid);
			else
				bbo=new ReportExcelBo(this.getUserView(),tabid,unitcode,"3",this.getFrameconn(),yearid,countid,weekid);
			if("8".equals(reportTypes))
			{
				TnameExtendBo tnameExtendBo = new TnameExtendBo(this.getFrameconn());
				TnameBo tnameBo=new TnameBo(this.getFrameconn(),tabid);
				bbo.setResultList(tnameExtendBo.getReportAnalyseResult(unitcode, yearid,
						countid, tabid, tnameBo,"8"));
				bbo.setNarch("8");
			}
			if(scopeid!=null&&scopeid.length()>0&&!"0".equals(scopeid)){
				TnameExtendBo tnameExtendBo = new TnameExtendBo(this.getFrameconn());
				TnameBo tnameBo=new TnameBo(this.getFrameconn(),tabid);
				tnameBo.setScopeid(scopeid);
				bbo.setResultList(tnameExtendBo.getReportAnalyseResult(unitcode, yearid,
						countid, tabid, tnameBo,reportTypes));
			}
			String outName=bbo.executReportExcel();
			outName = PubFunc.encrypt(outName);  //add by wangchaoqun on 2014-9-16
			this.getFormHM().put("outName",outName);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

}
