package com.hjsj.hrms.transaction.kq.register.report;

import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.interfaces.report.ReportParseXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * 个人考勤薄页面设置
 * @author Owner
 * wangyao
 */
public class SelectKqReportParUnitTran extends IBusiness{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String userbaseunit=(String)this.getFormHM().get("userbaseunit");
		String unita0100=(String)this.getFormHM().get("unita0100");
		String username=(String)this.getFormHM().get("username");		
		String report_unitid=(String)this.getFormHM().get("report_unitid");
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String coursedate=(String)hm.get("coursedate");
		if ( coursedate != null && coursedate.length() > 0) {
			this.getFormHM().put("coursedate", coursedate);
		}
		String report_sql="Select content from kq_report where report_id='"+report_unitid+"'";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ReportParseVo parsevo=new ReportParseVo();
		try
	    {
	    	this.frowset=dao.search(report_sql);
	    	if(this.frowset.next())
	    	{	    		
	    		String content=this.getFrowset().getString("content");
	    		if(content!=null&&content.length()>0)
	    		{
	    			ReportParseXml parseXml = new ReportParseXml();
	    			String kq_xpath="/kq_reports/kq_report";
	    			parsevo=parseXml.ReadOutParseXml(content,kq_xpath);	    			
	    		}
	    		
	    	}
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    }	    		
        this.getFormHM().put("userbaseunit",userbaseunit);
		this.getFormHM().put("unita0100",unita0100);
		this.getFormHM().put("username",username);
		this.getFormHM().put("report_unitid",report_unitid);
		this.getFormHM().put("parsevo",parsevo);
	}

}
