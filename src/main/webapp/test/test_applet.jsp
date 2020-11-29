<%@ page language="java" contentType="text/html; charset=GB18030"
    pageEncoding="GB18030"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GB18030">
<title>Insert title here</title>
</head>
<body>
   <jsp:plugin  type="applet" name="test" archive="hj_client.jar,struts_extends.jar,hessian-3.0.20.jar,command.jar,rowset.jar,jsuite_swing_all.jar" code="com.hjsj.hrms.client.gz.report.GzBannerReportApplet.class"  width="475"  height="350" 
   codebase="/hrms/client">  
       <jsp:params>  
           <jsp:param  name="MESSAGE"  value="Your  Message  Here"  />  
           </jsp:params>  
           <jsp:fallback>
                 <p>Unable to start plugin.</p>
           </jsp:fallback>	           
   </jsp:plugin>
	<input type="button" name="aaa" value="aaa" >
</body>
</html>