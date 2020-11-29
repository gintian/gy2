package com.hjsj.hrms.transaction.report.org_maintenance;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class SetReportTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String value=(String)this.getFormHM().get("value");    //没选中的表id
			String value2=(String)this.getFormHM().get("value2");  //选中的表id
			String unitcode=(String)this.getFormHM().get("unitcode");
			String analysereportflag = (String)this.getFormHM().get("analysereportflag"); 
			
			String report="";
			String analysereports="";
			StringBuffer needAddNod=new StringBuffer("");  //新增有功能权限的节点
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.frowset=dao.search("select * from tt_organization where unitcode='"+unitcode+"'");
			if(this.frowset.next())
			{
				report=Sql_switcher.readMemo(this.frowset,"report");
				analysereports=Sql_switcher.readMemo(this.frowset,"analysereports");
				
			}
			
			
			String[] temps=value2.split(",");
			for(int i=0;i<temps.length;i++)
			{
				if(report.indexOf(","+temps[i]+",")!=-1)
				{
					report=report.replaceAll(","+temps[i]+",",",");
					needAddNod.append(","+temps[i]);
				}
				if(analysereportflag!=null&& "1".equals(analysereportflag)){
					if(analysereports.indexOf(","+temps[i]+",")==-1)
					{
						analysereports+=temps[i]+",";
					}
				}
			}
			temps=value.split(",");
			for(int i=0;i<temps.length;i++)
			{
				if(report.indexOf(","+temps[i]+",")==-1)
					report+=temps[i]+",";
				if(analysereports.indexOf(","+temps[i]+",")!=-1)
					analysereports=analysereports.replace(","+temps[i]+",", ",");
			}
			
			if(",,".equals(report))
				report=report.substring(1);
			if(report.length()>1&&report.charAt(0)!=',')
				report=","+report;
			analysereports = analysereports.replace(",,", ",");
			String[] zxgflag = analysereports.split(",");//一个表都没选的时候清空   zhaoxg 2013-4-17
			if(zxgflag.length==0){
				analysereports = "";
			}
			if(analysereportflag!=null&& "1".equals(analysereportflag)){
				dao.update("update tt_organization set analysereports='"+analysereports+"' where unitcode='"+unitcode+"'");
				//更新所有子节点的 report 字段里的值
				this.frowset=dao.search("select * from tt_organization where unitcode like '"+unitcode+"%'");
				while(this.frowset.next())
				{
					 String a_analysereports=Sql_switcher.readMemo(this.frowset,"analysereports");
					String a_unitcode=this.frowset.getString("unitcode");
					updateAnalyseReport_str(a_analysereports,analysereports,dao,a_unitcode);
					
				}
			}else{
				dao.update("update tt_organization set report='"+report+"' where unitcode='"+unitcode+"'");
				//删除节点以下 所有与表相关联的 treport_ctrl记录
				temps=report.split(",");
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i]!=null&&temps[i].trim().length()>0)
					{
						String deleteSql = "delete from treport_ctrl where unitcode like '"+unitcode+"%' and tabid='"+temps[i]+"'";
						dao.delete(deleteSql,new ArrayList());
					}
				}
				//新增 
				temps=needAddNod.toString().split(",");
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i]!=null&&temps[i].trim().length()>0)
					{
						String insertSql = "insert into treport_ctrl (unitcode , tabid ,status) values( '"+ unitcode +"' , "+ temps[i] +" , -1 )";
						dao.update(insertSql);
					}
				}
				
				//更新所有子节点的 report 字段里的值
				this.frowset=dao.search("select * from tt_organization where unitcode like '"+unitcode+"%'");
				while(this.frowset.next())
				{
					String a_report=Sql_switcher.readMemo(this.frowset,"report");
					String a_unitcode=this.frowset.getString("unitcode");
					updateReport_str(report,a_report,dao,a_unitcode);
					
				}
			}
			
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	
	
	
	public void updateReport_str(String value,String report,ContentDAO dao,String unitcode)
	{
		try
		{
			
			String[] temps=value.split(",");
			for(int i=0;i<temps.length;i++)
			{
				if(temps[i]!=null&&temps[i].trim().length()>0&&report.indexOf(","+temps[i]+",")==-1)
					report+=temps[i]+",";
			}
			
			if(",,".equals(report))
				report=report.substring(1);
			if(report.length()>1&&report.charAt(0)!=',')
				report=","+report;
			dao.update("update tt_organization set report='"+report+"' where unitcode='"+unitcode+"'");
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void updateAnalyseReport_str(String value,String analysereports,ContentDAO dao,String unitcode)
	{
		try
		{
			
			String[] temps=value.split(",");
			for(int i=0;i<temps.length;i++)
			{
				if(temps[i]!=null&&temps[i].trim().length()>0&&analysereports.indexOf(","+temps[i]+",")==-1)
					analysereports+=temps[i]+",";
			}
			
			if(",,".equals(analysereports))
				analysereports=analysereports.substring(1);
			if(analysereports.length()>1&&analysereports.charAt(0)!=',')
				analysereports=","+analysereports;
			dao.update("update tt_organization set analysereports='"+analysereports+"' where unitcode='"+unitcode+"'");
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
