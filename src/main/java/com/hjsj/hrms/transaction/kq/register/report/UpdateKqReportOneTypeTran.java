package com.hjsj.hrms.transaction.kq.register.report;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class UpdateKqReportOneTypeTran extends IBusiness {

	public void execute()throws GeneralException
	{
		StringBuffer sql=new StringBuffer();
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");		
		String report_id=(String)hm.get("report_id");
		sql.append("select Tabid,CName from Muster_Name");
		sql.append(" where nModule='81'");
		CommonData vo=null;
		ArrayList list=new ArrayList();
		String tabid="";
		String report_name="";
		ContentDAO dao=new ContentDAO(this.getFrameconn());	
		try
		{
			this.frowset=dao.search(sql.toString());
			while(this.frowset.next())
			{
			   	vo=new CommonData(this.frowset.getString("Tabid"),this.frowset.getString("CName")!=null?this.frowset.getString("CName"):"");
			   	list.add(vo);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		String str_SQL="select name,tab_id from kq_report where report_id='"+report_id+"'";
		try
		{
		    this.frowset=dao.search(str_SQL);
		    if(this.frowset.next())
		    {
		    	tabid=this.frowset.getString("tab_id");
		    	report_name=this.frowset.getString("name");
		    }
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		this.getFormHM().put("musterlist",list);
		this.getFormHM().put("tabid",tabid);
		this.getFormHM().put("flaginfo","1");//0增加1修改
		this.getFormHM().put("report_name",report_name);//0增加1修改
	}

}
