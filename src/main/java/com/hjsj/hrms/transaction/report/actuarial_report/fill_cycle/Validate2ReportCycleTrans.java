package com.hjsj.hrms.transaction.report.actuarial_report.fill_cycle;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.Calendar;
import java.util.HashMap;

/** 
 *<p>Title:Validate2ReportCycleTrans.java</p> 
 *<p>Description:精算报表、填报周期 校验</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Apr 28, 2010</p> 
 *@author JinChunhai
 *@version 4.0
 */

public class Validate2ReportCycleTrans extends IBusiness 
{
	
	public void execute() throws GeneralException 
	{
		
		HashMap hm=this.getFormHM();
		String date=(String)hm.get("date");
		String year=(String)hm.get("year");
		String type=(String)hm.get("type");
		String reportcycle_id =hm.get("reportcycle_id")==null?"":(String)hm.get("reportcycle_id");
		
				
		String creatDate = PubFunc.getStringDate("yyyy-MM-dd"); // 系统当前日期				
		if(date==null || date.trim().length()<=0 || "".equals(date))
			date = creatDate;
		
		if(year==null || year.trim().length()<=0 || "".equals(year))
		{
			Calendar c =  Calendar.getInstance();
			year =String.valueOf(c.get(Calendar.YEAR));
		}
		
		StringBuffer strsql = new StringBuffer();
		strsql.append("select id,name,bos_date,theyear,kmethod,status from tt_cycle where theyear='"+year+"' ");		
		
		if(Sql_switcher.searchDbServer()==Constant.ORACEL)		
			strsql.append(" and bos_date=to_date('"+date+"','yyyy-mm-dd') ");
		else
			strsql.append(" and bos_date='"+date+"' ");
		
		strsql.append(" and kmethod='"+type+"' ");		
		if(reportcycle_id!=null && reportcycle_id.trim().length()>0)		
			strsql.append(" and id!="+reportcycle_id);
								
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String flag ="false";
		try 
		{
			this.frowset = dao.search(strsql.toString());
			while (this.frowset.next()) 
			{
				flag ="true";
			}
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ee);				
		}
		this.getFormHM().put("flag",flag);
	}
	
}