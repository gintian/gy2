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
		/*
		var backdate=showModalDialog('/org/orginfo/searchorgtree.do?b_backdate=link','_blank','dialogHeight:300px;dialogWidth:350px;center:yes;help:no;resizable:no;status:no;');
		if(backdate&&backdate.length>9) {
			statForm.action="/general/static/history/statshow.do?b_tree=link&backdate="+backdate+"&target=nil_body&action=&code=";
			statForm.target="il_body"
			statForm.submit();
		}else
			return false;
		*/
		var iHeight = 300;
		var iWidth = 350;
		var iTop = (window.screen.height-30-iHeight)/2;
		var iLeft = (window.screen.width-10-iWidth)/2; 
		var url = '/org/orginfo/searchorgtree.do?b_backdate=link';
		window.open(url,'blank','height='+iHeight+',innerHeight='+iHeight+',width='+iWidth+',innerWidth='+iWidth+',top='+iTop+',left='+iLeft+',toolbar=no,menubar=no,scrollbars=auto,resizeable=no,location=no,status=no');
	}
	/*
	 	兼容多浏览器，弹出改为open wangb 2019-12-11 bug 56206
	*/
	function openHistoryReturn(backdate){
		if(backdate&&backdate.length>9) {
			statForm.action="/general/static/history/statshow.do?b_tree=link&backdate="+backdate+"&target=nil_body&action=&code=";
			statForm.target="il_body"
			statForm.submit();
		}
	}
//-->
</script>


<hrms:themes></hrms:themes>
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
<body  border="0" cellspacing="0"  cellpadding="0">
<html:form action="/general/static/history/statshow"> 
    <table width="1000" border="0" cellspacing="0"  align="center" cellpadding="0" >
    	<%if(version){ %>
    	<tr align="left">
		<td valign="top" align="left">
		<div class="toolbar" style="padding-left: 10; vertical-align: middle;">
		<hrms:priv func_id="">
			<a href="###" onclick="return backDate();"><img style="height:100%;width:0px;" border="0" alt="" src="" ><img src="/images/quick_query.gif" alt="历史时点查询" border="0" align="middle"></a>               
		</hrms:priv>
		</div>
		</td>
		</tr>  
    	<%} %>
    </table>
</html:form>
</body>