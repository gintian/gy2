package com.hjsj.hrms.transaction.hire.jp_contest;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * 
 *<p>Title:Init_JP_Pos.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 19, 2007</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class Init_JP_Pos extends IBusiness 
{

	public void execute() throws GeneralException 
	{	
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			this.initJPTable("ZP_APPLY_FILE",dao);
			this.initJPTable("ZP_APPLY_JOBS",dao);
			this.initJPTable("Z07",dao);
			this.initIDGenerator("Z07.Z0700",dao);
			this.initIDGenerator("ZP_APPLY_JOBS.ID",dao);
			this.initIDGenerator("ZP_APPLY_FILE.FILEID",dao);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}
	
	public int initIDGenerator(String sequence_name,ContentDAO dao)
	{
		int ret=0;
		try
		{
			StringBuffer sb = new StringBuffer();
			sb.append(" update ID_FACTORY ");
			sb.append(" set CURRENTID=0 ");
			sb.append(" where SEQUENCE_NAME ='"+sequence_name+"'");
			ret = dao.update(sb.toString());
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return ret;
	}
	
	public int initJPTable(String tablename,ContentDAO dao)
	{
		int ret=0;
		try
		{
			StringBuffer sb = new StringBuffer();
			sb.append(" delete  "+tablename);
			ret = dao.update(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		return ret;
	}

}
