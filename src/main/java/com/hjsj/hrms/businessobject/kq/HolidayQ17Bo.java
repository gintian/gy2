/**
 * 
 */
package com.hjsj.hrms.businessobject.kq;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *<p>Title:HolidayQ71Bo</p> 
 *<p>Description:根据申请日期在假期子集中Q71查询，
 * 查询申请日期在起始日期、终止日期范围内的记录</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-2-13:14:49:03</p> 
 *@author cmq
 *@version 4.0
 */
public class HolidayQ17Bo {
	private Connection conn;
	/**假期类型*/
	private String type;
	/**申请日期*/
	private String app_date;
	/**当前用户*/
	private UserView userView;
    private String date_type;
	public String getDate_type() {
		return date_type;
	}
	public void setDate_type(String date_type) {
		this.date_type = date_type;
	}
	public HolidayQ17Bo(String type,String app_date,UserView userView,Connection conn) {
		this.userView=userView;
		this.type=type;
		this.conn=conn;
		this.app_date=app_date;
	}
	/**
	 * 查询可休假期天数
	 * 只考滤一条记录的情况，当年假期有效
	 */
	public float findRestDays()
	{
		float days=0;
		StringBuffer strsql=new StringBuffer();
		strsql.append("select q1707 from Q17 where q1709=? and nbase=? and a0100=? and ");
		strsql.append(Sql_switcher.dateValue(app_date));
		strsql.append(" between q17z1 and q17z3");
		ContentDAO dao=new ContentDAO(this.conn);
		ArrayList list=new ArrayList();
		list.add(this.type);
		list.add(this.userView.getDbname());
		list.add(this.userView.getA0100());
		RowSet rs=null;
		try
		{
			rs=dao.search(strsql.toString(),list);
			if(rs.next())
			{
				days=rs.getFloat("q1707");
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}finally
		{
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		KqUtilsClass kqUtilsClass=new KqUtilsClass();
		days=kqUtilsClass.round(days+"",1);
		return days;
	}
	/**
	 * 取得可休假期的描述性内容
	 */
	public String findRestDescription()
	{
		StringBuffer buf=new StringBuffer();
		float days=findRestDays();
		if(days>0)
		{
			buf.append(ResourceFactory.getProperty("kq.rest.desc"));
			buf.append(":");
			buf.append(days);
			if(this.date_type!=null&& "T".equals(this.date_type))
				buf.append(ResourceFactory.getProperty("kq.class.hour"));
			else
				buf.append(ResourceFactory.getProperty("kq.rest.day"));
		}
		return buf.toString();
	}
}
