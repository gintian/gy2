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
	boolean version = false;
	if(userView.getVersion()>=50){//版本号大于等于50才显示这些功能
		version = true;
	}
%>
<script type="text/javascript">
<!--
	function backDate(){
		var backdate=showModalDialog('/report/org_maintenance/reportunittree.do?br_backdate=link','_blank','dialogHeight:300px;dialogWidth:350px;center:yes;help:no;resizable:no;status:no;');
		
		if(backdate&&backdate.length>9) {
			
			searchReportUnitForm.action="/report/org_maintenance/reportunittree.do?b_query2=link&backdate="+backdate;
			searchReportUnitForm.target="il_body"
			searchReportUnitForm.submit();
		}else
			return false;
	}
	function openwin(url)
	{
	   window.open(url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+screen.availHeight+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
	}
//-->
</script>


<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<hrms:themes/>
<style>
<!--
	body {
	margin-left: 0px;
	margin-top: 0px;
	margin-right: 0px;
	margin-bottom: 0px;
	}
-->
</style>
<body  border="0" cellspacing="0"  cellpadding="0" style="overflow:hidden;">
<html:form action="/report/org_maintenance/reportunittree"> 

    <table width="1000" border="0" cellspacing="0"  align="center" cellpadding="0" >
    	<%if(version){ %>
    	<tr align="left">
		<td valign="top" align="left">
		<div class="toolbar" style="padding-left: 2">
			<a href="###" onclick="return backDate();"><img src="/images/quick_query.gif" alt="历史时点查询" border="0" align="middle"></a>               
		</div>
		</td>
		</tr>  
    	<%} %>
    </table>
</html:form>
</body>