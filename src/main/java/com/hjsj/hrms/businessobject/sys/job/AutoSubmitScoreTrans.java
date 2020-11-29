package com.hjsj.hrms.businessobject.sys.job;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:AutoSubmitScoreTrans.java</p>
 * <p>Description>:固定积分自动提交</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2013-08-08 下午15:56:27</p>
 * <p>@version: 7.0</p>
 * <p>@author: JinChunhai
 */

public class AutoSubmitScoreTrans implements Job 
{

	@Override
    public void execute(JobExecutionContext context) throws JobExecutionException
	{
		Connection conn = null;
		RowSet rowSet = null;
		RowSet rs =null;
		try 
		{
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);			
			String creatDate = PubFunc.getStringDate("yyyy-MM-dd"); // 获取当前时间	
			
			// 生日认可
			StringBuffer buf = new StringBuffer("");
			buf.append("select a0100,e0127,a0101 from usrA01 where 1=1 ");	
			buf.append(" and ("+Sql_switcher.month("a0111")+ "="+ getDatePart(creatDate,"m") +") ");
			buf.append(" and ("+Sql_switcher.day("a0111")+ "="+ getDatePart(creatDate,"d") +")");			
			rowSet = dao.search(buf.toString());			
		//	ArrayList list = new ArrayList();						
			while (rowSet.next())
			{
			//	ArrayList list1 = new ArrayList();
				String a0100 = isNull(rowSet.getString("a0100")); 
			//	list1.add(a0100);								
				int i9999 = this.getI9999(conn, a0100);
			//	list1.add(new Integer(i9999));
				String e0127 = isNull(rowSet.getString("e0127")); 
			//	list1.add(e0127);
				String a0101 = isNull(rowSet.getString("a0101")); 
			//	list1.add(a0101);				
			//	list.add(list1);	
				
				StringBuffer inSql = new StringBuffer();
				inSql.append("insert into usrA32 ");
				inSql.append(" (a0100,i9999,A3208,A3209,A3201,C3201,A3202,C3202,A3204,A320E) values ");
				inSql.append(" ('"+a0100+"',"+i9999+",'"+e0127+"','"+a0101+"', ");						
				if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
					inSql.append("to_date('" + creatDate + "','yyyy-mm-dd hh24:mi:ss'),");
				} else {
					inSql.append("'" + creatDate + "',");
				}
				inSql.append("'0101',");
				inSql.append("'QQZSHGJZG3M',");
				inSql.append("'生日认可',");
				inSql.append("'200',");			
				inSql.append("NEWID() )");
				dao.insert(inSql.toString(), new ArrayList());								
			}
			
			
			// 入职周年认可
			StringBuffer str = new StringBuffer("");
			str.append("select a0100,e0127,a0101 from usrA01 where 1=1 ");	
			str.append(" and ("+Sql_switcher.month("c0183")+ "="+ getDatePart(creatDate,"m") +") ");
			str.append(" and ("+Sql_switcher.day("c0183")+ "="+ getDatePart(creatDate,"d") +")");			
			rowSet = dao.search(str.toString());			
		//	ArrayList listYear = new ArrayList();						
			while (rowSet.next())
			{
			//	ArrayList list1 = new ArrayList();
				String a0100 = isNull(rowSet.getString("a0100")); 
			//	list1.add(a0100);								
				int i9999 = this.getI9999(conn, a0100);
			//	list1.add(new Integer(i9999));
				String e0127 = isNull(rowSet.getString("e0127")); 
			//	list1.add(e0127);
				String a0101 = isNull(rowSet.getString("a0101")); 
			//	list1.add(a0101);				
			//	listYear.add(list1);
				
				StringBuffer strSql = new StringBuffer();
				strSql.append("insert into usrA32 ");
				strSql.append(" (a0100,i9999,A3208,A3209,A3201,C3201,A3202,C3202,A3204,A320E) values ");
				strSql.append(" ('"+a0100+"',"+i9999+",'"+e0127+"','"+a0101+"', ");
				if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
					strSql.append("to_date('" + creatDate + "','yyyy-mm-dd hh24:mi:ss'),");
				} else {
					strSql.append("'" + creatDate + "',");
				}
				strSql.append("'0102',");
				strSql.append("'QQZSHGJZG3N',");
				strSql.append("'周年认可',");
				strSql.append("'200',");			
				strSql.append("NEWID() )");
				dao.insert(strSql.toString(), new ArrayList());
			}																				
		
		}catch (Exception e) 
		{
			e.printStackTrace();
		}finally
		{
			try 
			{
				if(rs!=null) {
					rs.close();
				}
				if(rowSet!=null) {
					rowSet.close();
				}
				if(conn!=null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * 取I9999
	 */
	public int getI9999(Connection conn, String a0100)
	{
		int count = 0;
		StringBuffer buf = new StringBuffer("select " + Sql_switcher.isnull("max(i9999)", "0") + " n from ");
		buf.append("usrA32 where a0100='" + a0100 + "'");
		try
		{
			ContentDAO dao = new ContentDAO(conn);
			RowSet rs = dao.search(buf.toString());
			if (rs.next())
			{
				double number = rs.getDouble(1);
				
//				System.out.println(number);
				
				int num = (int)number;
//				if(number == 0.0)
//					num = Integer.parseInt(number);				
				count = num + 1;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return count;
	}
	
	public String isNull(String str) 
	{
		if (str == null || str.trim().length() <= 0 || " ".equalsIgnoreCase(str) || "".equalsIgnoreCase(str)) {
			return "";
		} else {
			return str;
		}
	}
	
	/**
	 * 分解当前系统时间
	 */
	public String getDatePart(String mydate, String datepart)
	{
		String str = "";
		if ("y".equalsIgnoreCase(datepart)) {
			str = mydate.substring(0, 4);
		} else if ("m".equalsIgnoreCase(datepart))
		{
			if ("0".equals(mydate.substring(5, 6))) {
				str = mydate.substring(6, 7);
			} else {
				str = mydate.substring(5, 7);
			}
		} else if ("d".equalsIgnoreCase(datepart))
		{
			if ("0".equals(mydate.substring(8, 9))) {
				str = mydate.substring(9, 10);
			} else {
				str = mydate.substring(8, 10);
			}
		}
		return str;
	}
	
}
