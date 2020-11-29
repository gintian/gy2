package com.hjsj.hrms.transaction.hire.jp_contest;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.Date;

/**
 * 
 *<p>Title:Create_JP_Pos.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 19, 2007</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class Create_JP_Pos extends IBusiness {
	
	public void execute() throws GeneralException {
		try 
		{
			IDGenerator idg = new IDGenerator(2, this.getFrameconn());
			String Z0700 = Integer.parseInt(idg.getId("Z07.Z0700"))+"";
			//ContentDAO dao = new ContentDAO(this.getFrameconn());
			//HashMap hm=this.getFormHM();
			//String tablename=(String)hm.get("tablename");  
			//cat.debug("table name="+tablename);
			//this.insert(tablename,Z0700,dao);
			//String Z0705 = this.getCreateTimeDate(tablename,Z0700,dao);
			//String strZ0700="00000001";
			//strZ0700=StringUtils.leftPad(String.valueOf(Z0700), 10,"0");
			this.getFormHM().put("z0700",Z0700);
			this.getFormHM().put("Z0705",new Date().toLocaleString());
			this.getFormHM().put("Z0711",userView.getUserOrgId());
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}

	public void insert(String tablename,String Z0700,ContentDAO dao)
	{
		
//		StringBuffer sb = new StringBuffer();
//		sb.append("insert into "+tablename);
//		sb.append("(Z0700,Z0713)");
//		sb.append(" values ");
//		sb.append("("+Z0700+",'01')");
//		System.out.println(sb.toString());
		StringBuffer sb = new StringBuffer();
		sb.append("insert into "+tablename);
		sb.append("(Z0700,Z0705,Z0713)");
		sb.append(" values ");
		sb.append("("+Z0700+",");
		sb.append(Sql_switcher.sqlNow()+",");
		sb.append("'01')");
		try
		{
			dao.update(sb.toString());
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public String getCreateTimeDate(String tablename,String Z0700,ContentDAO dao)
	{
		RowSet rs;
		String time = "";
		StringBuffer sb = new StringBuffer();
//		String declare_tax = "";
//		String tax_date = "";
		sb.append(" select Z0705 from "+tablename);
		sb.append(" where Z0700="+Z0700);
		//System.out.println(sb.toString());
		try
		{
			rs = dao.search(sb.toString());
			if(rs.next())
			{
				time = rs.getDate("Z0705").toString();	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return time;
	}
}