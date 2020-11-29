package com.hjsj.hrms.taglib.kq;

import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hrms.frame.utility.AdminDb;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.sql.Connection;
import java.util.ArrayList;

public class KqCourseMessTag extends BodyTagSupport{

	public int doStartTag() throws JspException
	{
		Connection conn=null;
		try{
		conn=AdminDb.getConnection();
		ArrayList list=RegisterDate.getKqDayList(conn);
		if(list!=null&&list.size()>0)
		{
			pageContext.getOut().println("当前考勤期间：");
			pageContext.getOut().println(list.get(0).toString());
			pageContext.getOut().println("-");
			pageContext.getOut().println(list.get(1).toString());
		}
		}catch(Exception e)
		{
			e.printStackTrace();
			
		}
		finally
		{
			try{
			 if (conn != null)
	             conn.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
	          
		}
		return EVAL_BODY_BUFFERED;  
		
	}

}
