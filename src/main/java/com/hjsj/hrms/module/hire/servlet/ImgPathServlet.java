package com.hjsj.hrms.module.hire.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class ImgPathServlet extends HttpServlet {
	
	 @Override
     public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	        doPost(request, response);
	    }
	    
	    @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
	    	PrintWriter out = null;
	        try {
	        	String img_url = request.getParameter("img_url");
	        	img_url = img_url.replaceAll("／", "/");
	        	if(img_url.contains("../")){
	        		img_url = img_url.replace("../", "");
	        	}
	        	String pajs=request.getSession().getServletContext().getRealPath(img_url);
	        	  File file = new File(pajs);
	        	  String s= "该图片不在服务器上,是否继续保存?";
	        	  response.setCharacterEncoding("UTF-8");
	        	boolean exists = file.exists();
	        	if(!exists){
	        		  out = response.getWriter();
	   	              out.write(s);
	        	}
	          
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }finally{
	        	if(out!=null){
	        		out.close();
	        	}
	        }
	    }
}

