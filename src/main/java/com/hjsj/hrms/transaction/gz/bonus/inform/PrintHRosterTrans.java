package com.hjsj.hrms.transaction.gz.bonus.inform;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
/**
 * <p>
 * Title:PrintHRosterTrans.java
 * </p>
 * <p>
 * Description:高级花名册数据录入
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2009-07-15 13:00:00
 * </p>
 * 
 * @author FanZhiGuo
 * @version 1.0
 * 
 */
public class PrintHRosterTrans extends IBusiness
{

    public void execute() throws GeneralException
    {

	String sql = (String) this.getFormHM().get("sql");
	sql = SafeCode.decode(sql);

	sql = sql.substring(0, sql.indexOf("order"));
	try
	{
	    ArrayList dbList = new ArrayList();
	    String sql1 = "select distinct dbase from (" + sql + ") b";
	    ContentDAO dao = new ContentDAO(this.getFrameconn());
	    RowSet rset = dao.search(sql1);
	    while (rset.next())
	    {
		String dbpri = rset.getString("dbase");
		dbList.add(dbpri);
	    }

	    for (int i = 0; i < dbList.size(); i++)
	    {
		String dbpri = (String) dbList.get(i);
		String sql2 = "select distinct b.a0100 from (" + sql + " and a.dbase='" + dbpri + "') b";
		rset = dao.search(sql2);
		ArrayList list = new ArrayList();
		while (rset.next())
		{
		    ArrayList list1 = new ArrayList();
		    String a0100 = rset.getString("a0100");
		    list1.add(a0100);
		    list.add(list1);
		}
		
		String delSql = "delete from " + this.getUserView().getUserName() + dbpri + "result";
		dao.update(delSql);
		String addsql = "insert into " + this.getUserView().getUserName() + dbpri + "result(a0100) values(?)";
		dao.batchInsert(addsql, list);
	    }
	} catch (Exception ex)
	{
	    ex.printStackTrace();
	    throw GeneralExceptionHandler.Handle(ex);
	}
    }
}
