package com.hjsj.hrms.test;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

public class ValidateServlet extends HttpServlet {
	
	private ServletContext context;
	private HashMap users=new HashMap();
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("GBK");
        String targetId = request.getParameter("id");
        System.out.println("====>"+targetId);
        if ((targetId != null) && !users.containsKey(targetId.trim())) 
        {
            response.setContentType("text/xml");
            response.setHeader("Cache-Control", "no-cache");
            response.getWriter().write("valid"); 
        } 
        else 
        {
            response.setContentType("text/xml");
            response.setHeader("Cache-Control", "no-cache");
            response.getWriter().write("invalid"); 
        }
        response.getWriter().write(targetId);
		
	}
	public void init(ServletConfig arg0) throws ServletException {
		this.context=arg0.getServletContext();
		users.put("greg","account data");
		users.put("duke","account data");
	}

	

}
