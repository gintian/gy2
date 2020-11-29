package com.hjsj.hrms.servlet.sys;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;

public class RefreshDataServlet extends HttpServlet{
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException 
	{
		doPost(arg0, arg1);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{

		Connection con=null;
		try{
	
		   String path = req.getSession().getServletContext().getRealPath("/js");
		   if("weblogic".equals(SystemConfig.getPropertyValue("webserver")))
		   {
		  	  path=req.getSession().getServletContext().getResource("/js").getPath();//.substring(0);
		      if(path.indexOf(':')!=-1)
		  	  {
				 path=path.substring(1);   
		   	  }
		  	  else
		   	  {
				 path=path.substring(0);      
		   	  }
		      int nlen=path.length();
		  	  StringBuffer buf=new StringBuffer();
		   	  buf.append(path);
		  	  buf.setLength(nlen-1);
		   	  path=buf.toString();
		   }
		   con=(Connection)AdminDb.getConnection();
		   
		   if (PubFunc.isProcessing) {
			   throw new GeneralException("业务正在执行,请稍后再试");
		   }
		   PubFunc.syncRefreshDataDirectory(path, con);
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally
		{
			if(con!=null)
			{
				try
				{
					if (con != null) {
						con.close();
					}
				}
				catch(Exception ee)
				{
					ee.printStackTrace();
				}
			}
		}
	}

}
