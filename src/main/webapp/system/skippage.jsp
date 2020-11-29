<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc" %>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<!--单点登录 -->
<html>
<HEAD>
<META NAME="GENERATOR" Content="Microsoft Visual Studio 6.0">
</HEAD>
<BODY >
</BODY>
<%
String ticket = request.getParameter("ticket");
String auth = request.getParameter("auth");
String user = request.getParameter("user");
String ship=request.getParameter("ship");
String id=request.getParameter("id");
String username=request.getParameter("username");
%>
<% 
  String bucc_url="http://nw.bucc.cn";
  //bucc_url="http://192.168.1.19";
  String hrp_logon_url=SystemConfig.getProperty("hrp_logon_url");
  String hr_url="http://hr.bucc.cn:8080";
 // hr_url="http://192.168.100.19:8080";

   if(ticket!=null&&ticket.length()>0&&(user==null ||user.length()<=0))
   {

       session.setAttribute("ticket",ticket);
       String location = 
	   ""+bucc_url+"/acl_users/anz_sso_plugin/validate?service="+hr_url+"/system/skippage.jsp&ticket="+ticket;
       response.sendRedirect(location);//第二次取用户名
   }else if(user!=null &&user.length()>0 && auth!=null&&auth.equals("yes"))//第三次登陆
   {
      ship=(String)session.getAttribute("ship");
      id=(String)session.getAttribute("id");      
      username=(String)session.getAttribute("username");  
      if(ship!=null&&ship.length()>0&&id!=null&&id.length()>0)
      {
         String url="";
         if(ship.equals("warn"))
            url="/system/warn/result_manager.do?b_query=link&warn_wid="+ id+"&appfwd=1";
         else if(ship.equals("board"))
            url="/selfservice/welcome/welcome.do?b_view=link&a_id="+ id+"&appfwd=1";
         else if(ship.equals("ykcard")){
            username=PubFunc.convert64BaseToString(username);
            url="/general/inform/synthesisbrowse/synthesiscard.do?b_query=link&tabid="+id+"&appfwd=1&username="+username+"";
            
         }else
         {
            
             out.println("<SCRIPT type=text/javascript>");
             out.println("alert('登陆失败');");
             out.println("window.location.href ='/hrms/templates/index/employLogon.jsp'");
             out.println("</SCRIPT>");  
             return;
         }
         user=PubFunc.convertTo64Base(user);
         out.println("<form name=\"form1\" id=\"form1\"  action=\""+url+"\" method=\"post\">");
         out.println("<input type=\"hidden\" name=\"etoken\" value=\""+user+"\">");
         out.println("<input type=\"hidden\" name=\"validatepwd\" value=\"false\">");
         out.println("</form>");
         out.println("<script language=\"javascript\">");
         out.println(" document.form1.submit();");
         out.println("</script>");
      }else
      {
          out.println("<SCRIPT type=text/javascript>");
          out.println("alert('登陆失败');");
          out.println("window.location.href ='/hrms/templates/index/employLogon.jsp'");
          out.println("</SCRIPT>");  
      }   
  }else if( auth!=null&&auth.equals("no"))
  {
       out.println("<SCRIPT type=text/javascript>");
       out.println("alert('登陆失败');");
       out.println("window.location.href ='/hrms/templates/index/employLogon.jsp'");
       out.println("</SCRIPT>");  
  }else{ 
      session.setAttribute("ship",ship);
      session.setAttribute("id",id);
      session.setAttribute("username",username);
      String location = 
	  ""+bucc_url+"/acl_users/anz_sso_plugin/login?service="+hr_url+"/system/skippage.jsp";
      response.sendRedirect(location);//首先去取ticket       
  } 
%>
</HTML>
