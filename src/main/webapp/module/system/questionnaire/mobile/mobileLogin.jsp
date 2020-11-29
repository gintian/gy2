<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@page import="com.hrms.frame.codec.SafeCode" pageEncoding="UTF-8"%>
<%@ page import="com.hrms.struts.constant.SystemConfig,com.hjsj.hrms.businessobject.sys.SysParamBo,com.hjsj.hrms.businessobject.sys.SysParamConstant"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width">
<title>移动eHR</title>
     <link rel="stylesheet" href="../../../../module/system/questionnaire/mobile/assets/css/login.css" type="text/css">
     <%
     	String planid = (String)request.getSession().getAttribute("planid");
     if(planid!=null){
    	 request.getSession().setAttribute("planid",planid);
     }else{
    	 request.getSession().setAttribute("planid",(String)request.getParameter("planid"));
     }
     %>
     <script type="text/javascript">
     	function reNew(str)
{
    var re;
	re=/%26amp;/g;
	str=str.replace(re,"&");
	re=/%26apos;/g;  
	str=str.replace(re,"'");
	re=/%26lt;/g;  
	str=str.replace(re,"<");
	re=/%26gt;/g;  
	str=str.replace(re,">");
	re=/%26quot;/g;  
	str=str.replace(re,"\"");
	re=/%25/g;
	str=str.replace(re,"%");
	re=/````/g;
	str=str.replace(re,",");
	return(str);		
}
function getValidStr(str) 
{
	str += "";
	if (str=="undefined" || str=="null" || str=="NaN")
		return "";
	else
		return reNew(str);
		
}
function decode(strIn)
{
	var intLen = strIn.length;
	var strOut = "";
	var strTemp;

	for(var i=0; i<intLen; i++)
	{
		strTemp = strIn.charAt(i);
		switch (strTemp)
		{
			case "~":{
				strTemp = strIn.substring(i+1, i+3);
				strTemp = parseInt(strTemp, 16);
				strTemp = String.fromCharCode(strTemp);
				strOut = strOut+strTemp;
				i += 2;
				break;
			}
			case "^":{
				strTemp = strIn.substring(i+1, i+5);
				strTemp = parseInt(strTemp,16);
				strTemp = String.fromCharCode(strTemp);
				strOut = strOut+strTemp;
				i += 4;
				break;
			}
			default:{
				strOut = strOut+strTemp;
				break;
			}
		}

	}
	return (strOut);
}
function getDecodeStr(str) {
	return ((str)?decode(getValidStr(str)):"");
}
     	function getPassword(logintype)
	    {
	    	var username=document.getElementsByName("username")[0].value;
			window.location.href="/gz/gz_analyse/historydata/salary_set_list.do?br_getpwd=link&logintype="+logintype+"&username="+username;	        
	    } 
     </script>
</head>
<body style="background-image:url(../../../../module/system/questionnaire/mobile/assets/images/login/bck.gif);	background-repeat:repeat-y repeat-y;">
	<html:form focus="username" action="/module/system/questionnaire/mobile/mobileLogin" >
	<div id="box">
	  <div id="logo"></div>
	  <div id="content">
		<table class="text" border="0" cellspacing="0" cellpadding="0" style="margin:auto;position: relative;top: 10px;">
					   <tr style="height: 40px">
					     <td >
					       用户名
						 </td>
					     <td >&nbsp;
					       <input class="username" type="text" name="username" size="20" TABINDEX="1">
					     </td>
					   </tr>
					   <tr style="height: 40px">
					     <td >
					       密　码
						 </td>
					     <td>&nbsp;
					          <input class="password" type="password" name="password" maxlength="20" size="20" TABINDEX="2">
					     	   <%
					     	   //String retrieving_password=SystemConfig.getPropertyValue("retrieving_password");
					     		  String   retrieving_password=SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, SysParamConstant.RETRIEVING_PASSWORD);
                                if(retrieving_password.equalsIgnoreCase("true"))
                                {
                                 %>
                                 	<label style="margin-left:14px;">
                                       <a href="###" onclick="getPassword('1');" style="font-size: 12px;color: white">忘记密码</a>
                               		</label>
                               <%} %>
					     </td>
					   </tr>
					   <tr style="height: 80px">
					     <td  colspan="2" align="center">&nbsp;
	                          <input name="logon" type="image" src="../../../../module/system/questionnaire/mobile/assets/images/login/hj_04.gif" TABINDEX="3">  
					     </td>
					   </tr>						   
	    </table>	
    					
	  </div>
	  <div id="copyright">
        
	  </div>
	</div>
	</html:form>
</body>
</html>