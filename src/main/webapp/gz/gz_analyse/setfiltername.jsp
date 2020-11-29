<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.codec.SafeCode,com.hjsj.hrms.utils.PubFunc"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
	  userName = userView.getUserFullName();
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	}
	String setname = (String)request.getParameter("setname");
	setname=setname!=null&&setname.trim().length()>0?SafeCode.decode(setname):"";	
	setname=PubFunc.keyWord_reback(setname);
%>

<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
<hrms:themes />
<html>
  <head>
  <title>用户名：<%=userName%></title>
  </head>
  <body>
    <table border="0" align="center">
    	<tr> 
    		<td>请输入人员筛选条件的名称</td>
    	</tr>
    	<tr> 
    		<td><input type="text" name="name" id="name" size="35" value="<%=setname%>"></td>
    	</tr>
    	<tr> 
    		<td align="center">
    			<input type="button" value="确定" onclick="getName();" Class="mybutton">&nbsp;&nbsp;&nbsp;&nbsp;
    			<input type="button" value="取消" onclick="parent.window.close();" Class="mybutton">
    		</td>
    	</tr>
    </table>
  </body>
</html>
<script language="javascript">
function getName(){
	var name = document.getElementById("name").value;
	if(name!=null&&name.length>0){
		if(window.showModalDialog){
			window.returnValue=name;
		}else {
	 		parent.window.opener.save_(name);
		}
		parent.window.close();
	}else{
		parent.window.close();
	}
} 
</script>
