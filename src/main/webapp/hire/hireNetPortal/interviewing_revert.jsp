<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*,javax.servlet.http.Cookie" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<LINK href="/css/hireNetStyle.css" type=text/css rel=stylesheet>
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
<script language="JavaScript" src="/hire/hireNetPortal/employNetPortal.js"></script>
<body style="text-algin:center;"><br>
<html:form action="/hire/hireNetPortal/search_zp_position">
<input type="hidden" name="loginName" value=""/>
<input type="hidden" name="password" value=""/>
<input type="hidden" name="operate" value="init"/>
<input type="hidden" name="interviewingCodeValue" value=""/>
<html:hidden name="employPortalForm" property="interviewingRevertItemid"/>
<html:hidden name="employPortalForm" property="dbName"/>
<table width="80%" border="0" cellspacing="0" align="center"  cellpadding="1" class=hj_zhaopin_tablehead>

<thead>
<tr>
<td colspan="2" class='hj_zhaopin_list_tab_titleone' vAlign=bottom align=left height=25 nowrap> 
面试通知回复(欢迎您：${employPortalForm.userName})
<br></td>
</tr>
<tr>
<td colspan="2" class='hj_zhaopin_list_tab_titleone'  style="PADDING-LEFT: 20px; PADDING-BOTTOM: 1px;background-image:url(/images/board_bottom.jpg) repeat-x;" vAlign=bottom align=left height=25 nowrap> 
<img src="/images/icon_speaker.gif" border="0"/>&nbsp;&nbsp;感谢您回复我们的面试通知，请选择回复状态并提交，以方便我们进行面试安排工作。
<br></td>
</tr>
<tr>
<td class=hj_zhaopin_list_tab_titleone_1 width="30%"  vAlign=bottom align=center height=25 nowrap> 
<font color="black">选择</font>
</td>
<td class=hj_zhaopin_list_tab_titleone width="70%" vAlign=bottom align=center height=25 nowrap> 
<font color="black">状态</font>
</th>
</tr>
</thead>
<% int i=0; %>
<logic:iterate id="element" name="employPortalForm" property="interviewingRevertItemCodeList" offset="0">
<%
		String	styleClass="hj_zhaopin_list_tab_titletwo";
 %>
<tr>
<td  width="30%" align="center" class="<%=styleClass%>" bgcolor='#ffffff'>
<logic:equal value="${employPortalForm.interviewingCodeValue}" name="element" property="codeitemid">
<input type="radio" name="codevalue" value="<bean:write name="element" property="codeitemid"/>" checked/>
</logic:equal>
<logic:notEqual value="${employPortalForm.interviewingCodeValue}" name="element" property="codeitemid">
<input type="radio" name="codevalue" value="<bean:write name="element" property="codeitemid"/>"/>
</logic:notEqual>
</td>
<td  width="70%"  align="center" class="hj_zhaopin_list_tab_titletwo_1" bgcolor='#ffffff'>
 &nbsp;&nbsp;&nbsp;<bean:write name="element" property="codeitemdesc"/>
</td>
</tr>
<%i++; %>
</logic:iterate>
<tr>
<td colspan="2" align="left" class='hj_zhaopin_list_tab_titletwo' >
<a href="javascript:submitInterview();"> <IMG  src="/images/zp_tj.gif" border="0" ></a>
</td>
</tr>
</table>
</html:form>
</body>