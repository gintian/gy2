<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
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
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" ></meta>
<title>诚聘英才</title>
<script language="JavaScript" src="/js/validate.js"></script>

<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
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
<link href="<%=css_url%>" rel="stylesheet" type="text/css" id="skin"></link>
<LINK href="/css/newHireStyle.css" type=text/css rel=stylesheet></LINK>
<style  id="iframeCss">
.f12white {
	font-size: 12px;
	line-height: 140%;
	color: #ffffff;
	text-decoration: none;
	font-family: "Microsoft Sans Serif";
	font-weight:bold;
}
a:link {
	font-size: 12px;
	color: #0F0FFF;
	text-decoration: none;
}
a:visited {
	font-size: 12px;
	color: #0F0FFF;
	text-decoration: none;
}
a:hover {
	font-size: 12px;
	color: #0F0FFF;
	text-decoration: none;
}
/*菜单背景颜色*/
.MenuRow {
	border: 0px;
	BORDER-BOTTOM: 0pt solid; 
	BORDER-LEFT: 0pt solid; 
	BORDER-RIGHT:0pt solid; 
	BORDER-TOP: 0pt solid;
	font-size: 20px;
	border-collapse:collapse; 
	height:22;
	background-color:#2EBEDD;
	text-align:center;
}
.MenuRow_1 {
	border: 0px;
	border-bottom:1px solid #fff; 
	BORDER-LEFT: 0pt solid; 
	BORDER-RIGHT:0pt solid; 
	BORDER-TOP: 0pt solid;
	font-size: 20px;
	border-collapse:collapse; 
	height:25;
	background-color:#2EBEDD;
	text-align:center;
	/*background-color:#FFFFFF*/
}
/*第一层菜单背景颜色*/
.firstMenuRow{
/* color:#FFFFFF;*/
 background-image:url(../images/search_middle.jpg);
 background-repeat:repeat-x;
 size:13pt;
 margin-top:300px;
 /*background-color:#006E6D*/
}
/*菜单字体*/
.MenuRowFont{
   color:#666;
   font-size:12px;
}
/*平铺菜单左侧圆角型图片*/
.MenuLeftHead
{
    background-image: url(../../images/search_left.jpg);
	background-repeat:no-repeat;
	background-position:center
}
/*平铺菜单右侧圆角型图片*/
.MenuRightHead
{
    background-image: url(../../images/search_right.jpg);
	background-repeat:no-repeat;
	/*background-color: #A2D9DC;
	background-color: #FFFFFF;*/
	background-position:center
}
</style>
<style type="text/css">
body { behavior: url(/hire/hireNetPortal/csshover.htc); }  
</style>
</head>
<body topmargin="0" bottommargin="0" onKeyDown="return pf_ChangeFocus();"  class="TotalBodyBackColor">
<table width="1000px" border="0" cellpadding="0" cellspacing="0" align="center"  class="bodyTableBCK">

<tr height='680'>

   <td width="100%" valign="top" style='ALIGN:center;' >    
        <%if(flag!=0){%>
       <hrms:insert parameter="HtmlBody"/>
      <%}else { %>
      	<bean:message key="label.sys.info"/>
      <%}%>
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
</html>