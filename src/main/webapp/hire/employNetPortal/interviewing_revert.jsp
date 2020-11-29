<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*,javax.servlet.http.Cookie" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<hrms:themes></hrms:themes>
<LINK href="/css/employNetStyle.css" type=text/css rel=stylesheet>
<script type="text/javascript">
<!--
    <%
    if(request.getParameter("entery")!=null&&request.getParameter("entery").equals("3"))
    {   
    %>
        alert("提交成功！");
        window.parent.close();
    <%
    }
    %>
//-->
</script>
<script language="JavaScript" src="/hire/employNetPortal/employNetPortal.js"></script>
<body style="text-algin:center;"><br>
<html:form action="/hire/employNetPortal/search_zp_position">
<input type="hidden" name="loginName" value=""/>
<input type="hidden" name="password" value=""/>
<input type="hidden" name="operate" value="init"/>
<input type="hidden" name="interviewingCodeValue" value=""/>
<html:hidden name="employPortalForm" property="interviewingRevertItemid"/>
<html:hidden name="employPortalForm" property="dbName"/>
<div style="position:absolute;top:25%;left:25%;right:20%;margin:0 auto;">
<table width="100%" border="0" cellspacing="2" align="center"  cellpadding="1" class=border01>

<thead>
<tr>
<td colspan="2" class=red12 style="PADDING-LEFT: 20px; PADDING-BOTTOM: 1px" vAlign=bottom align=left background=/images/r_titbg01.gif height=25 nowrap> 
面试通知回复(欢迎您：${employPortalForm.userName})
<br></td>
</tr>
<tr>
<td colspan="2" class=red12 style="PADDING-LEFT: 20px; PADDING-BOTTOM: 1px" vAlign=bottom align=left background=/images/r_titbg01.gif height=25 nowrap> 
<img src="/images/icon_speaker.gif" border="0"/>&nbsp;&nbsp;感谢您回复我们的面试通知，请选择回复状态并提交，以方便我们进行面试安排工作。
<br></td>
</tr>
<tr>
<td class=red12 width="30%" style="PADDING-LEFT:20px; PADDING-BOTTOM: 1px" vAlign=bottom align=left height=25 nowrap> 
<font color="black">选择</font>
</td>
<td class=red12 width="70%" style="PADDING-LEFT:20px; PADDING-BOTTOM: 1px" vAlign=bottom align=left height=25 nowrap> 
<font color="black">状态</font>
</td>
</tr>
</thead>
<logic:iterate id="element" name="employPortalForm" property="interviewingRevertItemCodeList" offset="0">
<tr>
<td  width="30%" style="PADDING-LEFT: 20px;" class="tdValue" bgcolor='#ffffff'>
<logic:equal value="${employPortalForm.interviewingCodeValue}" name="element" property="codeitemid">
<input type="radio" name="codevalue" value="<bean:write name="element" property="codeitemid"/>" checked/>
</logic:equal>
<logic:notEqual value="${employPortalForm.interviewingCodeValue}" name="element" property="codeitemid">
<input type="radio" name="codevalue" value="<bean:write name="element" property="codeitemid"/>"/>
</logic:notEqual>
</td>
<td  width="70%"  class="tdValue" bgcolor='#ffffff'>
 &nbsp;&nbsp;&nbsp;<bean:write name="element" property="codeitemdesc"/>
</td>
</tr>
</logic:iterate>
<tr>
<td colspan="2" align="left">
<a href="javascript:submitInterview();"> <IMG  src="/images/tj.gif" border="0" ></a>
</td>
</tr>
</table>
</div>
</html:form>
</body>