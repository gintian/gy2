package com.hjsj.hrms.transaction.kq.register.report;

import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.interfaces.report.ReportParseXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class SelectKqReportParTran extends IBusiness{
	public void execute()throws GeneralException
	{
		String userbase=(String)this.getFormHM().get("userbase");
		String code=(String)this.getFormHM().get("code");
		String coursedate=(String)this.getFormHM().get("coursedate");		
		String kind=(String)this.getFormHM().get("kind");
		String relatTableid=(String)this.getFormHM().get("relatTableid");
		String report_id=(String)this.getFormHM().get("report_id");
		String report_sql="Select content from kq_report where report_id='"+report_id+"'";
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
		this.getFormHM().put("parsevo",parsevo);
		this.getFormHM().put("report_id",report_id);		
        this.getFormHM().put("userbase",userbase);
		this.getFormHM().put("code",code);
		this.getFormHM().put("coursedate",coursedate);
		this.getFormHM().put("kind",kind);	
		this.getFormHM().put("relatTableid", relatTableid);
	}

}
