package com.hjsj.hrms.taglib.kq;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;

public class KqClassName  extends BodyTagSupport {
	private String classid;

	public String getClassid() {
		return classid;
	}

	public void setClassid(String classid) {
		this.classid = classid;
	}
	public int doStartTag() throws JspException
	{
		return super.doStartTag();  
		
	}
	public int doEndTag() throws JspException 
	{
		Connection conn=null;		
		RowSet rs = null;
		try{
			conn=AdminDb.getConnection();
			StringBuffer sql=new StringBuffer();
			if(this.classid==null||this.classid.length()<=0)
				return SKIP_BODY;
			sql.append("select name from kq_class where class_id='"+this.classid+"'");
			ContentDAO dao=new ContentDAO(conn);
			String name="";
			rs=dao.search(sql.toString());
			if(rs.next())
			    name=rs.getString("name");
			pageContext.getOut().println(name);
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
}
