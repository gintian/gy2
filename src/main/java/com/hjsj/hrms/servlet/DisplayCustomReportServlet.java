package com.hjsj.hrms.servlet;

import com.hjsj.hrms.businessobject.report.user_defined_reoprt.UserdefinedReport;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.constant.WebConstant;
import com.hrms.struts.valueobject.UserView;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * <p>Title:DisplayCustomReportServlet</p>
 * <p>Description:显示自定制报表</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-3-16</p>
 * @author wangzhongjun
 * @version 1.0
 * 
 */
public class DisplayCustomReportServlet extends HttpServlet {

    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		response.setContentType("text/html;charset=gb2312"); 
		request.setCharacterEncoding("GBK");
		response.setCharacterEncoding("GBK");
		PrintWriter writer = response.getWriter();
		
		//是否加有权限
		String isprivstr=request.getParameter("ispriv");
		//自定义表格id
		String report_id=request.getParameter("id"); 
		
		// 输入的参数
		String inputParam = request.getParameter("inputParam");
		
		boolean ispriv=false;
		if(isprivstr!=null&& "1".equals(isprivstr))
			ispriv=true;
		
		//从session中获得userView
		HttpSession session = request.getSession();
		UserView userView = (UserView) session.getAttribute(WebConstant.userView);
		//获得数据库连接
		Connection conn = null;
		//填充页面内容
		String context="";
		String str[] = new String[3];
		//文件扩展名
		String ext = "";
		UserdefinedReport userdefinedReport = null;
		try {
			conn = AdminDb.getConnection();		
			userdefinedReport=new UserdefinedReport(userView,conn,report_id,ispriv);	
			//获得所有下拉列表的名称
			String hiddenValueName = request.getParameter("hiddenValueName");
			if (hiddenValueName != null && hiddenValueName.length() > 0) {
				String []names = hiddenValueName.split(",");
				userdefinedReport.setNames(names);
				HashMap map = new HashMap();
				if (names != null && names.length > 0) {
					for (int i = 0; i < names.length; i++) {
						String value = "";
						if (names[i].contains(":M")) {
							String[] nas = names[i].split(":");
							String[] strs = request.getParameterValues(nas[0]);
							for (int j = 0; j < strs.length; j++) {
								if (j == 0) {
									value += strs[j];
								} else {
									value += "," +strs[j];
								}
							}
							map.put(nas[0], value);
						} else {
							value = (String) request.getParameter(names[i]);
							map.put(names[i], value);
						}
										
					}
				}
				userdefinedReport.setPublicParamMap(map);
			}
			
			// 保存输入参数
			if (inputParam != null && inputParam.length() > 0) {
				HashMap map = new HashMap();
				HashMap map2 = userdefinedReport.getPublicParamMap();
				
				if (map2 == null) {
					map2 = new HashMap();
				}
				
				
				String names[] = userdefinedReport.getNames();
				
				String[] params = inputParam.split(",");
				
				String[] strNames = null;
				int index = 0;
				if (names == null) {
					strNames = new String[params.length];
				} else {
					strNames = new String[params.length + names.length];
					for (int i = 0; i < names.length; i++) {
						strNames[i] = names[i];
					}
					index = names.length;
				}
				
				for (int i = 0; i < params.length; i++) {
					if (params[i] != null) {
						String[] param = params[i].split(":");
						map.put(param[0], param[1]);
						map2.put(param[0], param[1]);
						strNames[index + i] = param[0];
					}
				}
				userdefinedReport.setInputParamMap(map);
				userdefinedReport.setPublicParamMap(map2);
				userdefinedReport.setNames(strNames);
			}
			
			ext=userdefinedReport.getExt();	
			String filename = "";
			if(ext.indexOf("htm")!=-1)
			{
				String url = request.getSession().getServletContext().getRealPath("/system/options/customreport/html");
				   if("weblogic".equals(SystemConfig.getPropertyValue("webserver")))
				   {
				  	  url=session.getServletContext().getResource("/system/options/customreport/html").getPath();//.substring(0);
				      if(url.indexOf(':')!=-1)
				  	  {
						 url=url.substring(1);   
				   	  }
				  	  else
				   	  {
						 url=url.substring(0);      
				   	  }
				      int nlen=url.length();
				  	  StringBuffer buf=new StringBuffer();
				   	  buf.append(url);
				  	  buf.setLength(nlen-1);
				   	  url=buf.toString();
				   }

				str=userdefinedReport.analyseUserdefinedHtmlReport(url);
			} else if (ext.indexOf("xls") != -1 || ext.indexOf("xlt") != -1){
				filename=userdefinedReport.analyseUserdefinedExcelReport();
				request.setAttribute("filename", filename);
			} else if (ext.indexOf("mht")!=-1)
			{
				String url = request.getSession().getServletContext().getRealPath("/system/options/customreport/html");
				   if("weblogic".equals(SystemConfig.getPropertyValue("webserver")))
				   {
				  	  url=session.getServletContext().getResource("/system/options/customreport/html").getPath();//.substring(0);
				      if(url.indexOf(':')!=-1)
				  	  {
						 url=url.substring(1);   
				   	  }
				  	  else
				   	  {
						 url=url.substring(0);      
				   	  }
				      int nlen=url.length();
				  	  StringBuffer buf=new StringBuffer();
				   	  buf.append(url);
				  	  buf.setLength(nlen-1);
				   	  url=buf.toString();
				   }

				str=userdefinedReport.analyseUserdefinedMhtReport(url);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//关闭数据库连接
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		//将内容打印到页面
		if (ext.indexOf("htm")!=-1 || ext.indexOf("mht")!=-1) {
			request.setAttribute("html", context);
			request.setAttribute("filename",str[0]);
			request.getRequestDispatcher("/system/options/customreport/html/"+str[0]).forward(request, response);
		} else if (ext.indexOf("xls") != -1 || ext.indexOf("xlt") != -1) {
			request.setAttribute("html", userdefinedReport.getHtmlcontent());
			request.getRequestDispatcher("/system/options/customreport/displayExcelFromServlet.jsp").forward(request, response);
		}
	}
    /**
     * 
     */
    public DisplayCustomReportServlet() {
        super();
    }
    
    private void writeToServlet(String filename, HttpServletResponse response) throws Exception {
    	if (filename == null) {
            throw new ServletException("Parameter 'filename' must be supplied");
        }     
        //考虑到中文的文件名称
        filename=SafeCode.decode(filename);
        //  Check the file exists
        filename=new String(filename.getBytes("GB2312"),"GBK");
        File file = new File(System.getProperty("java.io.tmpdir"), filename);
        if (!file.exists()) {
            //return; 
        	throw new ServletException("File '" + file.getAbsolutePath() + "' does not exist");
        }
        response.setContentType("application/vnd.ms-excel");
        String name=new String(file.getName().getBytes("gb2312"),"ISO8859_1");
//       	response.setHeader("Content-Disposition","inline;filename=" + name);
        
        /**显示对象文件*/
        ServletUtilities.sendTempFile(file,response);
        ServletOutputStream servletoutputstream=response.getOutputStream();
        BufferedInputStream bis = null;
        FileInputStream fin = null;
        try {
        	fin = new FileInputStream(file);
        	bis = new BufferedInputStream(fin);
	        int len;
	        byte buf[] = new byte[1024];
			while ((len = bis.read(buf)) != -1) {
				servletoutputstream.write(buf,0,len);
			}
			
			if (servletoutputstream != null)
				servletoutputstream.flush();
			
        } catch(Exception e) {
        	e.printStackTrace();
        } finally {
			PubFunc.closeIoResource(servletoutputstream);
			PubFunc.closeIoResource(bis);
			PubFunc.closeIoResource(fin);
        }
    }

}
