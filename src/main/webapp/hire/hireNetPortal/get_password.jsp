<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
 <head>
 <base target="_self" />
 </head>
<LINK href="/css/newHireStyle.css" type=text/css rel=stylesheet>
<%@ page import="com.hjsj.hrms.actionform.hire.employNetPortal.EmployPortalForm,
                 org.apache.commons.beanutils.LazyDynaBean,
                 java.util.*,com.hrms.hjsj.sys.ResourceFactory"%>
<script language="JavaScript" src="../../js/validate.js"></script>
<script language="JavaScript" src="../../js/function.js"></script>
<%
   String dbName=(String)request.getParameter("dbname");
   String userNameCloumn=(String)request.getParameter("userC");
   String passWordCloumn=(String)request.getParameter("passC");
 %>
 <script language="JavaScript" src="/hire/hireNetPortal/employNetPortal.js"></script>
 <script type="text/javascript">
 window.name="hireGetPassword"
function closeTheWindow()
{
 	var browserName=navigator.appName;
 	if (browserName=="Microsoft Internet Explorer") 
 	{
  	window.close(); 
 	} 
 	else if (browserName=="Netscape") 
 	{
 	window.top.close() 
 	} 
}
</script>


<body >
<div id="biaodan">
<!-- 用于进行servlet提交 -->

</div>
<html:form action="/hire/hireNetPortal/search_zp_position">
<table   cellspacing="0"  align=center cellpadding="0" style="margin-top:20px;" width='90%' >
<tr>
<td>&nbsp;&nbsp;&nbsp;</td>
<td style='BORDER:  #d0d0d0 1pt solid'>
	<table  border="0px" cellspacing="0"  align=center cellpadding="0" style="top:40px;margin-left：40px;margin-top:0px" width='100%' class="ListTable">
			<tr>
              <td class="TableRow" width='100%' colspan='3'><font style="font-size:10pt">&nbsp;请输入在本站的注册邮箱，系统将把您的密码发送到该邮箱</font></td>
              </tr>
              <tr  height="10px">
              <td width='100%' colspan='3'>&nbsp;&nbsp;&nbsp;</td>
              </tr>
              <tr height="40px">
						<td id="desc" style='TEXT-ALIGN:right;' height="30" width="35%">
					
						
							<font class='FontStyle' style="font-size:10pt">请输入注册邮箱:&nbsp;&nbsp;</font>
						</td>
						<td align="left"  height="30" width="65%" style="border:none;" colspan=2 >

							<input id="zzee" type="text" name="ZE" value="" size="30" onkeypress="if ((event.keyCode == 13)) event.keyCode=0;" />
						</td>
			</tr>
			<tr>
			 <td style='TEXT-ALIGN:right;' height="30" width="35%">
			     <font class='FontStyle' style="font-size:10pt">验证码:&nbsp;&nbsp;</font>
			 </td>
			 <td>
			      <input class="s_input" id="validatecode" size="8" type="text" value="" name="validatecode" />
			 </td>
			 <td style="padding-top: 15px;">
			     <img align="absMiddle" src="/servlet/vaildataCode?channel=0&codelen=4" id="vaildataCode">
				 <img align="absMiddle" src="/images/refresh.png" height="15" width="15" title="换一张" onclick="validataCodeReload()">
			 </td>
			</tr>
			<tr>
			<td colspan=3>
				&nbsp;&nbsp;&nbsp;&nbsp;
			</td>
			</tr>
	</table>
	</td>
	</tr>
	<tr>
						<td style="TEXT-ALIGN:center" colspan="2" >
						<br>
						      <input type="hidden" value="<%=session.getAttribute("validatecode")%>" id="validates"/>
							<img src="/images/hire/sure.gif"  onclick='sendmail("<%=dbName%>","<%=userNameCloumn%>","<%=passWordCloumn%>");' id="sendmails" border="0" style="cursor:hand"/>
							 <img src="/images/hire/close.gif" style="cursor:hand;" border="0" onclick="closeTheWindow();"/>
						</td>
					</tr>
	</table>
</html:form>
</body>
<script  language='javascript' >
<%
    EmployPortalForm employPortalForm=(EmployPortalForm)session.getAttribute("employPortalForm");
    String info=employPortalForm.getValidateInfo();
    employPortalForm.setValidateInfo("");
    if(info!=null&&!info.equals("")){
        if(info.equals("1")){
   	%>
    	alert("已将您的密码发送到您的注册邮箱，请您注意查收！");
    	this.window.close();
   <%
        }else{
%>
    alert('<%=info%>');
<%      
        }
    }
%>
</script>
</html>