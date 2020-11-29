package com.hjsj.hrms.servlet.performance.kh_plan;

import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hjsj.hrms.servlet.DownLawBase;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p>Title:FileDownLoad.java</p>
 * <p>Description>:计划参数/考核指标说明 上传文件</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Sep 09, 2011 10:10:46 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class FileDownLoad extends HttpServlet
{
	
    public FileDownLoad()
    {
    	super();
    }
    public void goPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
    	this.doGet(request, response);
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

		String planId = request.getParameter("plan_id");
		UserView userView=(UserView)request.getSession().getAttribute(WebConstant.userView);
		String userId = userView.getUserId();
		ResultSet rs = null;
		String ext = "";
		ServletOutputStream sos = response.getOutputStream();  
		String name = "";
		Connection con = null;
		InputStream inputStream = null;
		try
		{
		    con = AdminDb.getConnection();
		    ContentDAO dao = new ContentDAO(con);
		    try
		    {
				ExamPlanBo bo = new ExamPlanBo(con);
				String tableName = "per_plan";
				if(!bo.isExist(planId))
					tableName="t#"+userId+"_per_file"; //"per_plan_file_" + planId + "_" + userId;
				String sql = "select thefile,file_ext from "+tableName+" where plan_id="+planId;
				rs = dao.search(sql);
				if (rs.next())
				{
					ext = rs.getString("file_ext").substring(1);
				    inputStream = rs.getBinaryStream("thefile");		    
				}
				
				if(inputStream==null)			
					return;
		
					
				String mime = ServletUtilities.getMimeType("." + ext);
				// 设置http头部声明为下载模式
				if (DownLawBase.checkCanOpenFile(ext))
				{
				    response.setContentType(mime);
				    name = "fileDown." + ext;// 加上这句，支持中文文件名，但IE6却多出了一个窗口
				    response.setHeader("Content-disposition", "attachment;filename=\"" + name + "\"");
				} else
				{
				    response.setContentType("APPLICATION/OCTET-STREAM");
				    response.setHeader("Content-Disposition", "attachment;   filename=\"extFile\"");
				}
				byte buf[] = new byte[512];
				int len;
				while ((len = inputStream.read(buf)) != -1)
				{
				    sos.write(buf, 0, len);
				}
			
		    } catch (SQLException e)
		    {
		    	e.printStackTrace();
		    }
		} catch (Exception e)
		{
		    e.printStackTrace();
		} finally
		{
			PubFunc.closeDbObj(rs);
			PubFunc.closeDbObj(con);
			
			PubFunc.closeIoResource(inputStream);
		    
		}

    }

}
