package com.hjsj.hrms.transaction.kq.register.report;

import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.interfaces.report.ReportParseXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class KqReportPutOneTypeTran extends IBusiness{
	public void execute()throws GeneralException
	{
		String report_id=(String)this.getFormHM().get("report_id");
		String report_sql="Select name,content from kq_report where report_id='"+report_id+"'";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ReportParseVo parsevo=new ReportParseVo();
		String report_name="";
		try
	    {
	    	this.frowset=dao.search(report_sql);
	    	if(this.frowset.next())
	    	{	    		
	    		String content=this.getFrowset().getString("content");
	    		report_name=this.getFrowset().getString("name");
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
		this.getFormHM().put("parsevo",parsevo);
		this.getFormHM().put("report_id",report_id);
		this.getFormHM().put("report_name",report_name);
	}

}
