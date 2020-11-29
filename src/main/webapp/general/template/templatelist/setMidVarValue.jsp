<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="java.util.*,com.hrms.frame.codec.SafeCode"%>
<html>
<head>
<%
    String midName = SafeCode.decode(request.getParameter("var"));
    String type = request.getParameter("type").toUpperCase();
    String length = request.getParameter("alength");
    String str = "";
    String str1 = "";
    if (type.equalsIgnoreCase("A")) {
        str = "请输入字符型变量值(直接输入字符串)";
        str1 = "缺省值为空";
    } else if (type.equalsIgnoreCase("D")) {
        str = "请输入日期型变量值(格式:YYYY.MM.DD)";
        str1 = "缺省值为系统日期";
    } else if (type.equalsIgnoreCase("N")) {
        str = "请输入数值型变量值";
        str1 = "缺省值为0";
    }

    UserView userView = (UserView) session.getAttribute(WebConstant.userView);
    String bosflag = "";
    if (userView != null) {
        bosflag = userView.getBosflag();
    }
%>

<title>sdfdsf</title>
</head>
 <link href="/css/css1.css" rel="stylesheet" type="text/css">
 <script language="javascript" src="/js/validateDate.js"></script>
 <script language="javascript" src="/js/function.js"></script>
 <hrms:themes></hrms:themes>
 <%
     if ("hcm".equals(bosflag)) {
 %>
<link href="/general/template/template.css" rel="stylesheet" type="text/css"/>
<%
    }
%>
<body>

		<table width="393" style="margin-top:5px;margin-left:-2px">
			<%
	    if (!"hcm".equals(bosflag)) {
	%>
<tr height="10"><td ></td></tr>

	<%
	    }
	%>
			<tr>	
				<td width="100%" align='center'>
					<table width="100%" border="0" cellspacing="0" align="center"
						cellpadding="0" class="ListTableF" >
						<thead>
							<tr>
								<td align="left" class="TableRow" nowrap>
									<%=str%>
								</td>
							</tr>
						</thead>
						<tr class="trShallow">
							<td align="left" class="RecordRow" style="padding-bottom:8px" nowrap>
								变量[<%=midName%>]的值(<%=str1%>)
								<br>

								<input type='text' name='midvar' value='' size='60' class="text4"/>
								
							<br>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr height="35">
				<td align="center" valign="middle">
					<input type='button' name="ok" Class="mybutton"
						value='<bean:message key="button.ok"/>' onclick="enter()" />

					<input type='button' name="cancel" Class="mybutton"
						value='<bean:message key="button.cancel"/>'
						onclick="closewindow()" />
				</td>
			</tr>
		</table>

		<script language='javascript' >
function closewindow()
{
	window.close();
}

function enter()
{
	var type='<%=type%>';
	var alength=<%=length%>;
	var value=document.getElementsByName("midvar")[0].value;
	if(trim(value).length>0)
	{
		if(type=='N')
		{
			if(!checkIsNum2(value))
			{
				alert("请输入数值型变量值");
				return;
			}
		}
		if(type=='A')
		{
			if(alength!=0)
			{
				if(value.replace(/[^\x00-\xff]/g,"**").length>alength)
				{
					alert("输入字符超出指定长度"+alength);
					return;
				}
			}
		}
		if(type=='D')
		{
				if(!validate2(document.getElementsByName("midvar")[0],"日期","YYYY.MM.DD"))
				{
					return;
				}
				
				if(value.length==7)
					value=value+".01";
				if(value.length==4)
					value=value+".01.01";	
		}
		
	}
	else 
		value="##";
	window.returnValue=value;
	window.close();
}
</script>



</body>
</html>