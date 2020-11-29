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
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<SCRIPT LANGUAGE=javascript>

function queding(){
	
	var te=$('a2');
	
	 window.returnValue=te.value;
		window.close();
	
}
function guanbi(){
		window.returnValue="bcc";
		window.close();
	
}

</SCRIPT> 
<hrms:themes></hrms:themes>
<html:form action="/general/approve/personinfo/showpersoninfo"> 
<br>
<br>
<table align="center" border="0" cellspacing="0" align="left" cellpadding="0"  width="500">
<tr>
<td>
<table  align="center" border="0" cellspacing="0" align="left" cellpadding="0"  width="500">
<tr height="20">
			<!--  <td width=10 valign="top" class="tableft"></td>
			<td width="150" align=center class="tabcenter">
				驳回原因&nbsp;
			</td>
			<td width=10 valign="top" class="tabright"></td>
			<td valign="top" class="tabremain" width="400"></td>-->
			<td  align=center class="TableRow">
				驳回原因&nbsp;
			</td>
		</tr>

</table>
</td>
</tr>
<tr>
<td class="RecordRow" nowrap>
<textarea cols=69 rows=8 name=a2 style="overflow:auto"></textarea>
</td>
</tr>
<tr>
<td>
<br>
<button name='qwu' class="mybutton" onclick="queding()">确定</button>&nbsp;
<button name='cls' class="mybutton" onclick="guanbi()">关闭</button>
</td>
</tr>
 </table>  

</html:form>
