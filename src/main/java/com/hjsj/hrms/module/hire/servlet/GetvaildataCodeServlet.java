package com.hjsj.hrms.module.hire.servlet;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import org.mortbay.util.ajax.JSON;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
@Deprecated
public class GetvaildataCodeServlet extends HttpServlet {
	
	 @Override
     public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	        doPost(request, response);
	    }
	    
	    @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
	    	PrintWriter out = null;
	        try {
	           HttpSession session = request.getSession();
	           String validatecode = (String) session.getAttribute("validatecode");
	           // session中的验证码取后即销毁，避免安全漏洞
			   session.removeAttribute("validatecode");
	           validatecode = PubFunc.encrypt(SafeCode.encode(validatecode));
	           out = response.getWriter();
	           out.write(JSON.toString(validatecode));
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }finally{
	        	if(out!=null){
	        		out.close();
	        	}
	        }
	    }

}
