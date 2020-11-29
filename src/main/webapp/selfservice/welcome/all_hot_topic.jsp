<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	//判断当前用户是否自助用户（4）还是业务用户（0）
	int status=0;
	status = userView.getStatus();
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
	session.setAttribute("status",status);
	String returnvalue = request.getParameter("returnvalue")==null?"":request.getParameter("returnvalue");
%>
<script language="javascript" src="/js/validate.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script type="text/javascript">
function backHome(home)
{
	var status=document.getElementById("status").value;
       if(home=='5'){
       		if("hcm"=='<%=hcmflag%>'){
       			document.location="/templates/index/hcm_portal.do?b_query=link";  
       		}else{
				if(status=="4"){
          			document.location="/general/tipwizard/tipwizard.do?br_selfinfo=link";      		
				}else if(status=="0"){
          			document.location="/templates/index/portal.do?b_query=link";      		
				}
       		}
       	}
}

</script>
<html:form action="/selfservice/welcome/hot_topic">
<input type="hidden" id="status" value="${sessionScope.status}">
<br>
<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<tr>
<td align=left class=TableRow>
<img src="/images/forumme.gif">&nbsp; <logic:equal value="rese" name="welcomeForm" property="discriminateFlag">热点调查</logic:equal>
 <logic:equal value="train" name="welcomeForm" property="discriminateFlag">培训评估</logic:equal>
</td>
</tr>
<logic:iterate id="element" name="welcomeForm" property="moreList" offset="0">
<tr>
<td align="left" class="RecordRow" nowrap>
<a href="<bean:write name='element' property='url'/>&home=<bean:write name="welcomeForm" property="home"/>" target="_blank"><bean:write name='element' property='name'/></a>
</td>
</tr>
</logic:iterate>
<tr>
<td align='center' style="padding-top:4px">
<%
if(returnvalue.equals("dxt"))
{
%>
<input type="button" name="b_return" value="<bean:message key="button.return"/>" class="mybutton"  onclick="hrbreturn('selfinfo','il_body','welcomeForm')">
<%}else{ %>
<input type='button' class='mybutton' value="<bean:message key="button.return"/>" onclick="backHome('<bean:write name="welcomeForm" property="home"/>');"/>
<%} %>
</td>
</tr>
</table>
</html:form>