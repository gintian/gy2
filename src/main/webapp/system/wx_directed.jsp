<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="com.hrms.struts.valueobject.UserView" %>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

//微信企业号菜单id   1我的薪酬 2。。。
String menuid = request.getParameter("menuid");
String url = "";

//http://218.240.54.124/ykcard/employeeselfcard.do?b_card=infoself&isMobile=1&isIOS=1&return=0&userbase=Usr&flag=notself&b0110=0101&a0100=00000009&pre=Usr&etoken=Y21xdGVzdCw=&appfwd=1
UserView userView = (UserView)session.getAttribute("userView");
if("1".equals(menuid)){
	String a0100 = userView.getA0100();
	String dbpre = userView.getDbname();
	String b0110 = userView.getUserOrgId();
	//request.getRequestDispatcher("/ykcard/employeeselfcard.do?b_card=infoself&isMobile=1&isIOS=1&return=0&userbase="+dbpre+"&flag=notself&b0110="+b0110+"&a0100="+a0100+"&pre="+dbpre).forward(request,response);
	url = "/ykcard/employeeselfcard.do?b_card=infoself&isMobile=1&isIOS=1&return=0&userbase="+dbpre+"&flag=notself&b0110="+b0110+"&a0100="+a0100+"&pre="+dbpre;
	//out.print(" <script type=\"text/javascript\">window.location.href=\"/ykcard/employeeselfcard.do?b_card=infoself&isMobile=1&isIOS=1&return=0&userbase="+dbpre+"&flag=notself&b0110="+b0110+"&a0100="+a0100+"&pre="+dbpre+";\"</script>");
}else if("".equals(menuid)){

}

out.print(" <script type=\"text/javascript\">window.location.href=\""+url+";\"</script>");
%>

