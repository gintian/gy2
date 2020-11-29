<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hjsj.hrms.businessobject.sys.SysParamBo"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    int ver=userView.getVersion(); //锁版本
    
%>
<body onload='getApplyTempletID()' >

<script language='javascript' >
// /general/template/myapply/busiTemplate.do?b_query=link&ins_id=0&returnflag=6&tabid=11

function getApplyTempletID()
{
	  var hashvo=new ParameterSet();
	  var request=new Request({method:'post',asynchronous:false,onSuccess:returnInfo,functionId:'9028000413'});
}

function returnInfo(outparamters)
{
	var appeal_template=outparamters.getValue("appeal_template");
	if(trim(appeal_template).length==0||appeal_template=='-1')
	{
		alert("没有定义申诉业务模板!");
	}
	else
	{
		 <% if(ver<70){ %>
			document.location="/general/template/myapply/busiTemplate.do?b_query=link&ins_id=0&returnflag=6&businessModel=1&tabid="+appeal_template;
		 <% }else{ %>
			document.location="/module/template/templatemain/templatemain.html?b_query=link&return_flag=14&approve_flag=1&module_id=9&tab_id="+appeal_template;
		<% } %>
	}
}

</script>

</body>
</html>