<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%@ page import="javax.servlet.http.Cookie"%>

<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
    int flag=0;
    EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock");
    if(lockclient!=null)
    {
    	if(lockclient.isHaveBM(30))
    		flag=1;
    }
    else
    {
    	flag=-1;
    }
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  userName = userView.getUserFullName();
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	}
	String date = DateStyle.getSystemDate().getDateString();
	
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" >
<title>诚聘英才</title>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script type="text/javascript" src="/js/hjsjUrlEncode.js"></script>
<script language="JavaScript">
function pf_ChangeFocus() 
{
   key = window.event.keyCode;

   
   if ( key==0xD && event.srcElement.tagName!='TEXTAREA'&&event.srcElement.type!='file') /*0xD*/
   {
   	window.event.keyCode=9;
   }
}
function pf_return(form,element) 
{
	document.forms[form].elements[element].focus();
	return false;
}
function redirectTO(cookieParameter1,cookieParameter2,formName,FactionURL,SactionURL)
{
       var bflag=false;
       var username="";
       var password="";
       var strCookie=document.cookie;
       if(strCookie!=null&&strCookie.length>0)
       {
         var arrCookie=strCookie.split("; ");
         for(var i=0;i<arrCookie.length;i++)
         { // 遍历cookie数组，处理每个cookie对
            var arr=arrCookie[i].split("=");
            if(arr[0]=='hjsj'+cookieParameter1)
            {
                username=unescape(arr[1]);
                bflag=true;
            }
             if(arr[0]=='hjsj'+cookieParameter2)
            {
                password=unescape(arr[1]);
                bflag=true;
            }
         }
       }
       if(document.forms[0])
       {
       if(bflag)
       {
         if(document.forms[0].name==formName)
		 {
            for(var i=0;i<document.forms[0].elements.length;i++)
	        {
			  if(document.forms[0].elements[i].name==cookieParameter1)
			  {
				 document.forms[0].elements[i].value=username;
			  }
			  if(document.forms[0].elements[i].name==cookieParameter2)
			  {
				 document.forms[0].elements[i].value=password;
			  }
			}
			//防止出现用户名不存在或者密码错误，这样会出现查不到人员姓名等错误，先进行检验
			var hashvo=new ParameterSet();
		    hashvo.setValue("loginName",username);
		    hashvo.setValue("password",password);
		    hashvo.setValue("sAction",getEncodeStr(SactionURL));
		    hashvo.setValue("fAction",getEncodeStr(FactionURL));
	     	var In_paramters="operate=ajax";  
	      	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnIn,functionId:'3000000159'},hashvo);	
		 }
		}
		else
		{
		   //document.forms[0].action=FactionURL;
		   //document.forms[0].submit();
		}
		}
}
function returnIn(outparameters)
{
  var sAction=getDecodeStr(outparameters.getValue("sAction"));
  var fAction=getDecodeStr(outparameters.getValue("fAction"));
  var info=outparameters.getValue("info");
  if(info==0)
  {
     // document.forms[0].action=fAction;
	  //document.forms[0].submit();
  }
  else
  {
    document.forms[0].action=sAction;
	document.forms[0].submit();
  }
}
</script>

<script language="javascript">
    var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
</script>
   <link href="<%=css_url%>" rel="stylesheet" type="text/css" id="skin">
</head>
<body topmargin="0" bottommargin="0" onKeyDown="return pf_ChangeFocus();"  class="TotalBodyBackColor">
<table width="100%" border="0" cellpadding="0" cellspacing="0" >
<tr><td >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
<td width="100%"  align="center">
<table width="85%"  align="center" border="0" cellpadding="0" cellspacing="0" >
  <tr>  
    <td width="100%" valign="top" align="center" >
	   <hrms:insert parameter="HtmlBanner"/>	   
    </td>
  </tr>  
  <tr height="450" >  
    <td width="100%" id="cms_pnl" align="center" valign="top" >    
        <%if(flag!=0){%>
       <hrms:insert parameter="HtmlBody"/>
      <%}else { %>
      	<bean:message key="label.sys.info"/>
      <%}%>
    </td>
  </tr>
  <tr >  
    <td valign="top" width="100%" align="center">
       <hrms:insert parameter="HtmlFooter"/>
    </td>
  </tr>
</table>
</td>
</tr>
</table>
<script language="javascript">
  initDocument();
   <%
    if(session.getAttribute("isLogin")==null)
    {
       if(request.getParameter("b_search")!=null)
       {
        session.setAttribute("isLogin","1");
    %>
     redirectTO('loginName','password','employPortalForm','/hire/employNetPortal/search_zp_position.do?br_disembark=link','/hire/employNetPortal/search_zp_position.do?b_interviewlogin=login');
     
    <%
       }
       else if(request.getParameter("b_query")!=null)
       {
        session.setAttribute("isLogin","1");
    %>
      redirectTO('loginName','password','employPortalForm','/hire/employNetPortal/search_zp_position.do?b_query=link&operate=init','/hire/employNetPortal/search_zp_position.do?b_login=login');
    <%
      }
	}
	%>
</script>
</body>
<script language="javascript">
	//解决IE文本框自带历史记录问题  jingq add 2014.12.31
	var inputs = document.getElementsByTagName("input");
	for ( var i = 0; i < inputs.length; i++) {
		if(inputs[i].getAttribute("type")=="text"){
			inputs[i].setAttribute("autocomplete","off");
		}
	}
</script> 
</html>