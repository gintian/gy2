package com.hjsj.hrms.transaction.report.report_analyse;

import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.reportanalyse.ReportAnalyseHtmlBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;

import java.util.HashMap;

/**
 * 
 * 
 *<p>Title:</p> 
 *<p>Description:归档数据 汇总</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Mar 2, 2009</p> 
 *@author dengcan
 *@version 4.2
 */
public class PigeonholeDataCollectTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)(this.getFormHM().get("requestPamaHM"));
			String tabid=(String)map.get("tabid");
			if(!userView.isHaveResource(IResourceConstant.REPORT,tabid))
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
			String unitcode=(String)map.get("unitCode");
			String yearid=(String)map.get("yearid");
			TnameBo tnameBo=null;
			if(userView.getStatus()==4&&userView.getS_userName()!=null) //自助用户关联业务用户
			{
				UserView _userview=new UserView(userView.getS_userName(),userView.getS_pwd()!=null?userView.getS_pwd():"",this.getFrameconn());
				_userview.canLogin();
				tnameBo=new TnameBo(this.getFrameconn(),tabid,_userview.getUserId(),_userview.getUserName(),"view");
			}
			else
				tnameBo=new TnameBo(this.getFrameconn(),tabid,this.getUserView().getUserId(),this.getUserView().getUserName(),"view");
			ReportAnalyseHtmlBo rahbo = new ReportAnalyseHtmlBo(this.getFrameconn());
			String reportHtml= rahbo.creatHtmlView(unitcode ,tabid , yearid , "1" ,tnameBo ,"8");
			String reportTitle="";
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search("select * from tname where tabid="+tabid);
			if(this.frowset.next())
				reportTitle=this.frowset.getString("name")+" "+yearid+"年度统计报表";
			
			this.getFormHM().put("reportTitle",reportTitle);
			this.getFormHM().put("reportHtml", reportHtml);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
