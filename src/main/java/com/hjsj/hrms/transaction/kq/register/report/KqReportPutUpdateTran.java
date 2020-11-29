package com.hjsj.hrms.transaction.kq.register.report;

import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.interfaces.report.ReportParseXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class KqReportPutUpdateTran extends IBusiness{
	public void execute()throws GeneralException
	{
		ReportParseVo parsevo=(ReportParseVo)this.getFormHM().get("parsevo");
		String report_id=(String)this.getFormHM().get("report_id");
		String report_name=(String)this.getFormHM().get("report_name");
		String report_sql="Select content from kq_report where report_id='"+report_id+"'";
		ContentDAO dao = new ContentDAO(this.getFrameconn());		
		try
	    {
	    	this.frowset=dao.search(report_sql);
	    	if(this.frowset.next())
	    	{	    		
	    		String content=this.getFrowset().getString("content");
	    		if(content!=null&&content.length()>0)
	    		{
	    			ReportParseXml parseXml = new ReportParseXml();
	    			parsevo.setName(report_name);	    			
	    			//String kq_xpath="/kq_reports/kq_report[@value='"+report_id+"']";
	    			String kq_xpath="/kq_reports/kq_report";
	    			
	    			content=parseXml.WriteOutParseXml(content,kq_xpath,parsevo);
	    			updateXmlContent(content,report_id,report_name);	    			
	    		}
	    		
	    	}
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    }		
	}
	/**
	 * 修改考勤报表xml配置文件
	 * @param content 前台传过来的数据，经解析生成xml文件
	 * 
	 * */
	
	public void updateXmlContent(String content,String report_id,String report_name)
	{
	   String updatesql="update kq_report set content=?,name=? where report_id=?";
	   ArrayList contentlist=new ArrayList();
	   contentlist.add(content);
	   contentlist.add(report_name);
	   contentlist.add(report_id);
	   ContentDAO dao = new ContentDAO(this.getFrameconn());
	   try{
	      dao.update(updatesql,contentlist);
	   }catch(Exception e)
	   {
		e.printStackTrace();   
	   }
	}
}
