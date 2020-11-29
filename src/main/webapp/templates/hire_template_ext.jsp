<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html; charset=UTF-8"%>
 <%
 response.setHeader("Pragma","No-cache"); 
 response.setHeader("Cache-Control","no-store,no-cache"); 
 response.setHeader("Expires", "0"); 
 response.setDateHeader("Expires", 0);  %>
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
    	int days=lockclient.getMayusedays();
    	if(days<=0&&days!=-1){
    		flag=2;//试用期限已过
    	}
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
<meta  http-equiv="Expires"  CONTENT="0">    
<meta  http-equiv="Cache-Control"  CONTENT="no-cache">    
<meta  http-equiv="Pragma"  CONTENT="no-cache">    
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" ></meta>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7;">
<title>诚聘英才</title>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script><!-- 2015.01.08 xxd 该js与时间控件冲突，故将该js注释，招聘外网页面需要用到该js时自行添加到子页面-->
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script language="JavaScript">
var IVersion=getBrowseVersion();
if(IVersion>=7||IVersion==0)
{
	document.writeln("<LINK href=\"/css/newHireStyle.css\" type=text/css rel=stylesheet></LINK>");
}else
{
	document.writeln("<LINK href=\"/css/ie6newHireStyle.css\" type=text/css rel=stylesheet></LINK>");
}
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
<style type="text/css">
body { behavior: url(/hire/hireNetPortal/csshover.htc); }  
</style>
</head>
<body topmargin="0" bottommargin="0"  class="TotalBodyBackColor">
<!--<body topmargin="0" bottommargin="0" onKeyDown="return pf_ChangeFocus();"  class="TotalBodyBackColor">  -->
<table width="1000px" border="0" cellpadding="0" cellspacing="0" align="center"  class="bodyTableBCK">
<tr>
    <td width="100%" valign="top" style='ALIGN:center'>
	   <hrms:insert parameter="HtmlBanner"/>	   
    </td>
  </tr>  
   <tr  valign="middle">  
    <td width="100%" valign="top" height='40px' style='ALIGN:center'>
	   <hrms:insert parameter="HtmlMenu"/>	   
    </td>
  </tr>  
  <script language="javascript">
   var h = document.body.clientHeight;
   var ih = h-124-40-45;
   document.write("<tr height='"+ih+"'>");
  </script>
    <td width="100%"   valign="top" class="body_td_class">    
       <%if(flag==0){%>
        		<bean:message key="label.sys.info"/>

      <%}else if(flag==2) { %>
      	        <bean:message key="error.test.used"/>
      <%}else{%>
      		 	<hrms:insert parameter="HtmlBody"/>
      <%} %>
    </td>
  </tr>
  <tr>  
    <td valign="top" >
       <hrms:insert parameter="HtmlFooter"/>
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
	function banBackSpace(e){
		   var ev = e || window.event;//获取event对象   
	       var obj = ev.target || ev.srcElement;//获取事件源   
	       
	       var t = obj.type || obj.getAttribute('type');//获取事件源类型  
	       //获取作为判断条件的事件类型
	       var vReadOnly = obj.getAttribute('readonly');
	       //处理null值情况
	       vReadOnly = (vReadOnly == "") ? false : vReadOnly;
		   //当敲Backspace键时，事件源类型为密码或单行、多行文本的，
		   //并且readonly属性为true或enabled属性为false的，则退格键失效
	       var flag1=(ev.keyCode == 8 && (t=="password" || t=="text" || t=="textarea")  && vReadOnly=="readonly")?true:false;
	       //当敲Backspace键时，事件源类型非密码或单行、多行文本的，则退格键失效
	       var flag2=(ev.keyCode == 8 && t != "password" && t != "text" && t != "textarea") ? true:false;        
		         
	       //判断
	       if(flag2){
	           return false;
	       }
	       if(flag1){   
	           return false;   
	       }  
	};
	window.onload=function(){
		//兼容ie和chrome
		document.onkeydown =banBackSpace;
		//兼容fixfox和safari
		document.onkeypress =banBackSpace;
	};
</script>
</body>
</html>