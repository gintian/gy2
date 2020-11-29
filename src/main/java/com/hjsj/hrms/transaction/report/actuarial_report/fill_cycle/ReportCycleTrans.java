package com.hjsj.hrms.transaction.report.actuarial_report.fill_cycle;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ReportCycleTrans extends IBusiness {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		 if (Sql_switcher.searchDbServer() == Constant.MSSQL){
			 SQLExecute();
		 }
		 else if(Sql_switcher.searchDbServer() == Constant.ORACEL){
			 OrcaleExecute();
			 
		 }
		 else if(Sql_switcher.searchDbServer() == Constant.DB2){
			 db2Execute();
		 }else{
			 SQLExecute();
		 }
	      	
	}
	/**
	 * Orcale执函数
	 *
	 */
	public void OrcaleExecute() throws GeneralException
	{
		StringBuffer strsql = new StringBuffer();
		strsql.append("select id,name,bos_date,theyear,kmethod,status from tt_cycle");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList list = new ArrayList();
		try {
		     ResultSet rs = dao.search("SELECT id,name,bos_date,theyear,kmethod,status from tt_cycle order by bos_date desc");
			
			 while (rs.next()) {
				RecordVo vo = new RecordVo("tt_cycle");
				vo.setInt("id", rs.getInt("id"));

				String temp = rs.getString("name");
				if (temp == null || "".equals(temp)) {
					vo.setString("name", "");
				} else {
					//vo.setString("topic", temp.substring(0,temp.length()>50?50:temp.length())+"...");;
					vo.setString("name", temp);
				}

				
				temp = PubFunc.FormatDate(rs.getDate("bos_date"));
				if (temp == null || "".equals(temp)) {
					vo.setString("bos_date", "...");
				} else {

					vo.setDate("bos_date",temp);
				}
				vo.setString("theyear", rs.getString("theyear"));
				vo.setInt("kmethod", rs.getInt("kmethod"));
				vo.setString("status", rs.getString("status"));

				list.add(vo);
			}
			if(rs!=null)rs.close();
			
			this.getFormHM().put("cyclelist", list);
		}
		catch(OutOfMemoryError error)
		{
			
			System.out.println("------>ReportCycleTrans---->OutOfMemoryError-->");
		}
		catch (SQLException sqle) {
			sqle.printStackTrace();
			throw GeneralExceptionHandler.Handle(sqle);
		} finally {

		}
	}
	/**
	 * SQL执行函数
	 */
	public void SQLExecute() throws GeneralException
	{
		StringBuffer strsql = new StringBuffer();
		strsql.append("select id,name,bos_date,theyear,kmethod,status from tt_cycle order by theyear desc, bos_date desc");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList list = new ArrayList();
		
		try 
		{
			this.frowset = dao.search(strsql.toString());
			while (this.frowset.next()) 
			{
				RecordVo vo = new RecordVo("tt_cycle");
//				id=this.getFrowset().getString("id");
//				if(!(this.userView.isHaveResource(IResourceConstant.TT_CYCLE,id)))
//				{
//							continue;
//				}
				vo.setInt("id", this.getFrowset().getInt("id"));

				String temp = this.getFrowset().getString("name");
				if (temp == null || "".equals(temp)) {
					vo.setString("name", "");
				} else {
					//vo.setString("topic", temp.substring(0,temp.length()>50?50:temp.length())+"...");;
					vo.setString("name", temp);
				}

				
				temp = PubFunc.FormatDate(this.getFrowset().getDate("bos_date"));
				if (temp == null || "".equals(temp)) {
					vo.setString("bos_date", "...");
				} else {

					vo.setDate("bos_date",temp);
				}
				vo.setString("theyear", this.getFrowset().getString("theyear"));
				vo.setInt("kmethod", this.getFrowset().getInt("kmethod"));
				vo.setString("status", this.getFrowset().getString("status"));
				list.add(vo);
			}

			this.getFormHM().put("cyclelist", list);
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ee);				
		}
	}
	
	/**
	 * db2执行函数
	 * @throws GeneralException
	 */
	public void db2Execute() throws GeneralException
	{
		StringBuffer strsql = new StringBuffer();
		strsql.append("select id,name,bos_date,theyear,kmethod,status from tt_cycle order by bos_date desc");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList list = new ArrayList();
		
		try 
		{
			this.frowset = dao.search(strsql.toString());
			while (this.frowset.next()) 
			{
				RecordVo vo = new RecordVo("tt_cycle");
//				id=this.getFrowset().getString("id");
//				if(!(this.userView.isHaveResource(IResourceConstant.TT_CYCLE,id)))
//				{
//							continue;
//				}
				vo.setInt("id", this.getFrowset().getInt("id"));

				String temp = this.getFrowset().getString("name");
				if (temp == null || "".equals(temp)) {
					vo.setString("name", "");
				} else {
					//vo.setString("topic", temp.substring(0,temp.length()>50?50:temp.length())+"...");;
					vo.setString("name", temp);
				}

				
				temp = PubFunc.FormatDate(this.getFrowset().getDate("bos_date"));
				if (temp == null || "".equals(temp)) {
					vo.setString("bos_date", "...");
				} else {

					vo.setDate("bos_date",temp);
				}
				vo.setString("theyear", this.getFrowset().getString("theyear"));
				vo.setInt("kmethod", this.getFrowset().getInt("kmethod"));
				vo.setString("status", this.getFrowset().getString("status"));
				list.add(vo);
			}

			this.getFormHM().put("cyclelist", list);
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ee);				
		}
	}


}
