<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	   css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	    css_url="/css/css1.css";
         //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
%>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<html>
<head>
</head>
<body>
<br><br><br><br><br>
<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1">
  <tr>
    <td align="center" class="RecordRow" nowrap>
      点击左边机构或职位，对所属人员进行信息录入。
    </td>    
  </tr>
  <tr>
    <td align="right" class="RecordRow" nowrap>
      <br>
      图例：<img src="/images/unit.bmp">-单位
           <img src="/images/branch.bmp">-部门
           <img src="/images/duty.bmp">-职位>
    </td>    
  </tr>
</table>
</body>
</html>