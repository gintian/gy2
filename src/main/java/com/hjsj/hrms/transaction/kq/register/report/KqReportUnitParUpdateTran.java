package com.hjsj.hrms.transaction.kq.register.report;

import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.interfaces.report.ReportParseXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 个人考勤薄页面设置
 * @author Owner
 * wangyao
 */
public class KqReportUnitParUpdateTran extends IBusiness{

	public void execute() throws GeneralException {
		String username=(String)this.getFormHM().get("username");
		String unita0100=(String)this.getFormHM().get("unita0100");
		String userbaseunit=(String)this.getFormHM().get("userbaseunit");
		ReportParseVo parsevo=(ReportParseVo)this.getFormHM().get("parsevo");		
		String report_unitid=(String)this.getFormHM().get("report_unitid");
		String report_sql="Select content from kq_report where report_id='"+report_unitid+"'";
		ContentDAO dao = new ContentDAO(this.getFrameconn());		
		try
	    {
	    	this.frowset=dao.search(report_sql);
	    	if(this.frowset.next())
	    	{	    		
	    		String content=this.getFrowset().getString("content");
	    		String report_name="";
	    		if(content!=null&&content.length()>0)
	    		{
	    			ReportParseXml parseXml = new ReportParseXml();
	    			//String kq_xpath="/kq_reports/kq_report[@value='"+report_id+"']";
	    			String kq_xpath="/kq_reports/kq_report";	    			
	    			content=parseXml.WriteOutParseXml(content,kq_xpath,parsevo);
	    			updateXmlContent(content,report_unitid);	
	    			report_name=parsevo.getName();
	    			String updatesql="update kq_report set name='"+report_name+"' where report_id='"+report_unitid+"'";
	    			try{
	    			      dao.update(updatesql);
	    			}catch(Exception e)
	    			{
	    				e.printStackTrace();   
	    			}
	    		}
	    		
	    	}
	    }catch(Exception e)
	    {
	    	e.printStackTrace();
	    }	
	    this.getFormHM().put("report_unitid",report_unitid);	  
        this.getFormHM().put("username",username);
		this.getFormHM().put("unita0100",unita0100);
		this.getFormHM().put("userbaseunit",userbaseunit);
		
	}
	/**
	 * 修改考勤报表xml配置文件
	 * @param content 前台传过来的数据，经解析生成xml文件
	 * 
	 * */
	
	public void updateXmlContent(String content,String report_id)
	{
	   String updatesql="update kq_report set content=? where report_id=?";
	   ArrayList contentlist=new ArrayList();
	   contentlist.add(content);
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
