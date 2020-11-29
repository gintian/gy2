<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	   css_url="/css/css1.css";
	}
	int i=0;
	String func_id="";
	
	String a0 = PubFunc.encryption("0");
	String a1 = PubFunc.encryption("1");
	String a2 = PubFunc.encryption("2");
%>
<HTML>
<HEAD>
<TITLE>
</TITLE>

<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<style>
	.noup{
		border-top-width:0px;
	}
	.nodown{
		border-bottom-width:0px;
	}
</style>   
</HEAD>
<hrms:themes />
<body> 
<center>
<table border="0">
	<tr><td height="60">&nbsp;</td></tr>
</table>
<!--
<hrms:funcpanel height="0" cols="3" width="400" cellspacing="5" title="performance.workdiary.my.info" icon_height="40" icon_width="40">
	<hrms:funcitem url="/performance/workdiary/myworkdiaryshow.do?b_search=link&state=0" icon="/images/jgbm.gif" label="performance.workdiary.daily" function_id="01050"/>
	<hrms:funcitem url="/performance/workdiary/myworkweekshow.do?b_query=link&state=1" icon="/images/card.gif" label="performance.workdiary.weekly" function_id="01051"/>
	<hrms:funcitem url="/performance/workdiary/myworkweekshow.do?b_query=link&state=2" icon="/images/mc.gif" label="performance.workdiary.monthly" function_id="01052"/>
</hrms:funcpanel>
-->
<% 
if(userView.hasTheFunction("0105") || userView.hasTheFunction("061001"))
{
%>
	<table border="0" width="50%" align="center" class="ListTableF">
		<tr>
			<td colspan="3" class="TableRow" align="center"> <bean:message key="performance.workdiary.my.info"/> </td>
		</tr>
		<tr>
		   <hrms:priv func_id="01050">
			<td align="center" class="framestyle9 nodown"> <%i++;func_id="1"; %>
				<a href="/performance/workdiary/myworkdiaryshow.do?b_search=link&state=<%=a0 %>" style="display:block; width:40px;height:40px;"><img src="/images/jgbm.gif" border="0" width="40" height="40" title="<bean:message key="performance.workdiary.daily"/>"/></a> 
				<br>
				<hrms:priv func_id="01050">
					<a href="/performance/workdiary/myworkdiaryshow.do?b_search=link&state=<%=a0 %>"><bean:message key="performance.workdiary.daily"/></a> 
		  		</hrms:priv>
			</td>
		  </hrms:priv>
		  <hrms:priv func_id="01051">
			<td align="center" class="framestyle9 nodown"><%i++;func_id="2"; %>
				<a href="/performance/workdiary/myworkweekshow.do?b_query=link&state=<%=a1 %>" style="display:block; width:40px;height:40px;"><img src="/images/card.gif" border="0" width="40" height="40" title="<bean:message key="performance.workdiary.weekly"/>"/></a> 
				<br>
				<hrms:priv func_id="01051">
					<a href="/performance/workdiary/myworkweekshow.do?b_query=link&state=<%=a1 %>"><bean:message key="performance.workdiary.weekly"/></a> 
				</hrms:priv>
			</td>
		  </hrms:priv>
		  <hrms:priv func_id="01052">
			<td align="center" class="framestyle9 nodown"><%i++;func_id="3"; %>
				<a href="/performance/workdiary/myworkweekshow.do?b_query=link&state=<%=a2 %>" style="display:block; width:40px;height:40px;"><img src="/images/mc.gif" border="0" width="40" height="40" title="<bean:message key="performance.workdiary.monthly"/>"/></a> 
				<br>
				<hrms:priv func_id="01052">
					<a href="/performance/workdiary/myworkweekshow.do?b_query=link&state=<%=a2 %>"><bean:message key="performance.workdiary.monthly"/></a> 
				</hrms:priv>
			</td>
		  </hrms:priv>
		</tr>
		
	</table>
	<!-- 
	<hrms:funcitem url="/performance/workdiary/myworkdiaryshow.do?b_query=link&state=<%=a0 %>" icon="/images/jgbm.gif" label="performance.workdiary.daily" function_id="01050"/>
	 -->
<% 
}else
{
	out.write("您没有操作此功能的权限！");
}
%>
	 </center>
<BODY>
</HTML>
<% 
if(i==1)
{
  RequestDispatcher rd=null;
    if(func_id.equals("1"))
      rd =request.getRequestDispatcher("/performance/workdiary/myworkdiaryshow.do?b_search=link&state="+a0);   
    else if(func_id.equals("2"))
      rd=request.getRequestDispatcher("/performance/workdiary/myworkweekshow.do?b_query=link&state="+a1); 
    else if(func_id.equals("3"))
      rd=request.getRequestDispatcher("/performance/workdiary/myworkweekshow.do?b_query=link&state="+a2);        
   rd.forward(request,response);   
} 
%>

