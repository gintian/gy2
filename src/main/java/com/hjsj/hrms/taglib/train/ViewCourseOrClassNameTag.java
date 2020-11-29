package com.hjsj.hrms.taglib.train;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.sql.RowSet;
import java.sql.Connection;
/**
 * 显示培训班级或课程的名字
 * <p>Title:ViewCourseOrClassNameTag.java</p>
 * <p>Description>:ViewCourseOrClassNameTag.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Mar 16, 2011 5:08:21 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: s.xin
 */
public class ViewCourseOrClassNameTag extends BodyTagSupport{
    private String id;//班级或课程的id
    private String sort;//2为课程；3为班级
	public int doStartTag() throws JspException
	{
		Connection conn=null;
		RowSet rs=null;
		try{
		   conn=AdminDb.getConnection();
		   if(sort==null||sort.length()<=0)
			   return SKIP_BODY;
		   ContentDAO dao=new ContentDAO(conn);
		   String sql="";
		   if("2".equals(sort))
		   {
			   sql="select r4101,r1302 from r41,r13 where r1301=r4105 and r4101='"+id+"'";
			   rs=dao.search(sql);
			   if(rs.next())
			   {
				   pageContext.getOut().println(rs.getString("r1302"));
			   }
		   }else if("3".equals(sort))
		   {
			   sql="select r3130 from r31 where r3101='"+id+"'";
			   rs=dao.search(sql);
			   if(rs.next())
			   {
				   pageContext.getOut().println(rs.getString("r3130"));
			   }
		   }
		}catch(Exception e)
		{
			e.printStackTrace();
			
		}
		finally
		{
			try{
			 if(rs!=null)
				 rs.close();
			 if (conn != null)
	             conn.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
	          
		}
		return EVAL_BODY_BUFFERED;  
		
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
		

}
