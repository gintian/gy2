package com.hjsj.hrms.taglib.kq;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
/**
 * 
 * <p>Title:自动分配班组，得到班组名称</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jan 19, 2010:3:04:07 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class KqHostGroup extends BodyTagSupport{
	private String value;
//	private String hostname;
	public int doEndTag() throws JspException
	{
		Connection conn=null;		
		RowSet rs = null;
		try
		{
//			hostname=hostname.toUpperCase();
			conn=AdminDb.getConnection();
			StringBuffer sql=new StringBuffer();
//			sql.append("select codesetid from fielditem where fieldsetid='A01' and itemid='"+hostname+"'");
			ContentDAO dao=new ContentDAO(conn);
//			String codesetid="";
			String groupName="";
//			rs=dao.search(sql.toString());
//			while(rs.next())
//			{
//				codesetid=rs.getString("codesetid");
//			}
//			if(!codesetid.equals("")||!codesetid.equals("0"))
//			{
//				sql.setLength(0);
//				sql.append("select codeitemdesc from codeitem where codesetid='"+codesetid+"'");
//				sql.append(" and codeitemid='"+value+"'");
//				rs=dao.search(sql.toString());
//				while(rs.next())
//				{
//					groupName=rs.getString("codeitemdesc");
//				}
//				pageContext.getOut().println(groupName);
//			}else
//			{
//				pageContext.getOut().println(value);
//			}
			sql.append("select name from kq_shift_group where group_id='"+value+"'");
			rs=dao.search(sql.toString());
			while(rs.next())
			{
				groupName = rs.getString("name");
			}
			pageContext.getOut().println(groupName);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			
		       	if(rs!=null)
					try {
						rs.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    
			try{
			 if (conn != null)
	             conn.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
	          
		}
		return SKIP_BODY;
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
//	public String getHostname() {
//		return hostname;
//	}
//	public void setHostname(String hostname) {
//		this.hostname = hostname;
//	}
	
}
