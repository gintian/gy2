package com.hjsj.hrms.transaction.kq.register.report;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveKqReportOneTypeTran extends IBusiness {

	public void execute()throws GeneralException
	{
		String tabid=(String)this.getFormHM().get("tabid");		
		StringBuffer sql=new StringBuffer();
		String flaginfo=(String)this.getFormHM().get("flaginfo");
		String report_id=(String)this.getFormHM().get("report_id");
		String report_name=(String)this.getFormHM().get("report_name");
		sql.append("select Tabid,CName from Muster_Name");
		sql.append(" where nModule='81'");
		sql.append(" and Tabid='"+tabid+"'");
		String CName="";
		if(flaginfo==null||flaginfo.length()<=0)
		{
			flaginfo="";
		}
		ContentDAO dao=new ContentDAO(this.getFrameconn());	
		try
		{
			this.frowset=dao.search(sql.toString());
			if(this.frowset.next())
			{
				CName=this.frowset.getString("CName")!=null?this.frowset.getString("CName"):"";
			}
			if("0".equals(flaginfo))
			{
				addReport(CName,tabid);
			}else if("1".equals(flaginfo))
			{
				if(report_id!=null&&report_id.length()>0)
				{
					updateReport(CName,tabid,report_id,report_name);
				}
				
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	public void updateReport(String CName,String tabid,String report_id,String report_name)throws GeneralException
	{
		StringBuffer update = new StringBuffer();
		update.append("update kq_report set ");
		update.append("name='"+report_name+"',tab_id='"+tabid+"'");
		update.append(" where report_id='"+report_id+"'");
		StringBuffer update_Muster = new StringBuffer();
		update_Muster.append("update Muster_Name set ");
		update_Muster.append(" CName='"+report_name+"'");
		update_Muster.append(" where nModule='81' and Tabid='"+tabid+"'");
		ContentDAO dao=new ContentDAO(this.getFrameconn());	
		try
		{
			dao.update(update.toString());
			dao.update(update_Muster.toString());
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	public void addReport(String CName,String tabid)throws GeneralException
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());	
			IDGenerator idg=new IDGenerator(2,this.getFrameconn());
			String table="kq_report";
	  	    RecordVo vo=new RecordVo(table);        	  
	  	    String insertid=idg.getId((table+".report_id").toUpperCase());
	  	    vo.setString("report_id",insertid);
	  	    vo.setString("name",CName);
	  	    vo.setString("flag","0");
	  	    vo.setInt("tab_id",Integer.parseInt(tabid));
	  	    dao.addValueObject(vo);
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
