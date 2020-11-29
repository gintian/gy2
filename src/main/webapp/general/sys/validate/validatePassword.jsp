<%@page import="com.hrms.hjsj.sys.ConstantParamter"%>
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.hjsj.sys.Des"%>

<link href="/css/css1.css" rel="stylesheet" type="text/css">
				 				 
<html>
<head>

<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>

</head>
<style>
	.table1{
	width:450;
	height:100;    	
	font-size:14px;
	BORDER-BOTTOM:#94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid; 
}
.td1{
    BORDER-BOTTOM:#94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 0pt solid; 
    BORDER-TOP: #94B6E6 1pt solid; 
}
.td2{
    BORDER-BOTTOM:#94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 0pt solid; 
    BORDER-RIGHT: #94B6E6 0pt solid; 
    BORDER-TOP: #94B6E6 1pt solid; 
}
#inputcode{
    height:25px;
    border-left:#94B6E6 1pt solid;
    border-top:#94B6E6 1pt solid;
    border-bottom:#94B6E6 1pt solid;
    border-right: #94B6E6 1pt solid;
}
</style>
<%
UserView userview=(UserView)session.getAttribute(WebConstant.userView); 
String password = userview.getPassWord();
Des des = new Des();
if(ConstantParamter.isEncPwd())
	password = des.DecryPwdStr(password);
%>  
	<body>
        <div style="position:absolute; top:15px; left:10px; ">
		<table class="table1" align="center" cellspacing="0" cellpadding="0" >
		  <tr style="background-color:#f4f7f7;" height="25%">
					<td align="left">
						&nbsp;&nbsp;密码校验
					</td>
		 </tr>
		<tr height="35%">
		 <td height="35%">
			<table align="center" width="450" height="35%" cellspacing="0" cellpadding="2" >
				<tr height="100%" width="450">
					<td align="right" class="td2"  width="27.5%" height="100%">
						&nbsp;请输入您的密码：
					</td>
					<td align="left" class="td1"  width="55%" height="100%">
					   <input type="password" id="inputcode" size="10">
					</td>
				</tr>
		    </table>
		  </td>
	    </tr>
		<tr height="40%">
			<td valign="top" align="center">
				&nbsp;&nbsp;提示：请输入您的登录密码，校验认证通过以后，才能进入该模块。
			</td>
		</tr>
	 </table>
	 </div>
	 <div style="position:absolute; top:120px;" >
		<table  align="center" border="0" width="450px">
          <tr>
            <td align="center">
            	<input type="button" class="mybutton" name="button" value="<bean:message key="button.ok"/>" onclick="sub();" />
            	&nbsp;&nbsp;&nbsp;&nbsp;
            	<input type="button" class="mybutton" name="button" value="<bean:message key="button.cancel"/>" onclick="window.close();" />
            </td>
          </tr>          
		</table>
    </div>
  </body>
</html>
<script type="text/javascript">

	function sub()
	{
		var contentvo=new Object();
		var inputvalue=document.getElementById("inputcode").value;
		var pasword="<%=password%>";
		if(inputvalue!=pasword){
				alert("密码输入不正确");
				return false;
		}else{
			contentvo.content = "<%=password%>";
		}
	    contentvo.inputcode = document.getElementById("inputcode").value;
	    window.returnValue=contentvo;
	    window.close();
	}
</script>
