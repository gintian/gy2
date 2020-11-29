<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page language="java" %>
<%@ page import="com.hjsj.hrms.interfaces.decryptor.TripleDES"%>
<%@ page import="java.util.StringTokenizer"%>
<%@ page import="com.hjsj.hrms.interfaces.certificate.CaCertificate"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ page import="com.hrms.frame.codec.SafeCode"%>

<html>
<head>
   <title>WEB应用架构开发测试系统</title>
</head>
<body>
           <% 
                String flag=request.getParameter("flag");
		String user_ID=request.getParameter("user_ID");
		System.out.println(flag);
		System.out.println(user_ID);
		int securitytype=-1;
		String username="";
		String password="";
		if(!(flag==null||flag.equals("")))
		{
		   securitytype=Integer.parseInt(flag);
		}
		try
		{
		     switch(securitytype)
		     {
			case 0:
			      CaCertificate certificate=new CaCertificate("1");
			      Object caobject=request.getAttribute("javax.servlet.request.X509Certificate");
			      username=certificate.jitBjga_CaCertificate(caobject);		
				  break;
			case 1:
                            //String Decryptio=new TripleDES("EncryptionString").EncryptionStringData("王光艳,110101700815032");
	                    String DecryptionStringData=new TripleDES("EncryptionString").DecryptionStringData(user_ID);
	                    System.out.println(DecryptionStringData);		  
		            StringTokenizer Stok = new StringTokenizer(DecryptionStringData, ",");
		            password=Stok.nextToken();
		            username=Stok.nextToken();
		            //if(password!=null && password.length()>=2)
		            //   password=password.substring(0,2);
	                    System.out.println("用户名：" + password);
	                       break;
	             }
	              password=SafeCode.encode(password);
	              username=SafeCode.encode(username);
	              session.setAttribute("username",username);
		      session.setAttribute("password",password);
		   			
	    	      response.sendRedirect("/templates/index/employLogon.do?logon.x=link&username="+username+"&password="+password);
	       }catch(Exception ex)
		{
			ex.printStackTrace();
		    session.setAttribute("errMsg",ex.getMessage());
		    response.sendRedirect("/templates/info/failure_04.jsp");			
		}
	
          %>
	 
</body>
</html>