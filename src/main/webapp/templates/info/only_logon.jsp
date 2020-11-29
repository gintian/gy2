<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ page import="com.hrms.struts.constant.SystemConfig,com.hrms.hjsj.sys.ResourceFactory"%>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%
// 在标题栏显示当前用户和日期 2004-5-10 
String userName = null;
String css_url="/css/css1.css";
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
String browser="Trident";
if(userView != null)
{
  userName = userView.getUserFullName();
  css_url=userView.getCssurl();
  if(css_url==null||css_url.equals(""))
    css_url="/css/css1.css";
    /*cmq added at 20121201 for jquery easyui $函数冲突问题*/
    browser=userView.getBrower();
}
String date = DateStyle.getSystemDate().getDateString();
int flag=1;
String webserver=SystemConfig.getPropertyValue("webserver");
if(webserver.equalsIgnoreCase("websphere"))
    flag=2;


    String title = SystemConfig.getPropertyValue("frame_index_title");
    if(StringUtils.isBlank(title))
        title="贵州银行人力资源系统";

String bosflag = userView.getBosflag();
String logout_flag="26";
if(bosflag.equalsIgnoreCase("ul"))
{
    logout_flag="7";
}else if(bosflag.equalsIgnoreCase("el"))
{
	logout_flag="14";             
}                   
else if(bosflag.equalsIgnoreCase("hl"))
{
	logout_flag="26";                
}
else if(bosflag.equalsIgnoreCase("hl4"))
{
	logout_flag="21";              
}
else if(bosflag.equalsIgnoreCase("el4"))
{
	logout_flag="25";              
}    
else if(bosflag.equalsIgnoreCase("bi"))
{
	logout_flag="45";        
}
else if(bosflag.equalsIgnoreCase("il"))
{
	logout_flag="55";           
}  
else if(bosflag.equalsIgnoreCase("epmgw"))
{
	logout_flag="47";             
}else if(bosflag.equalsIgnoreCase("hcm"))
{
	logout_flag="30";            
}
 %>
<title><%=title%></title>
<link rel="icon" href="favicon.ico" type="image/x-icon" />
<link rel="shortcut icon" href="favicon.ico" type="image/x-icon">
<LINK rel="bookmark" href="favicon.ico"  type="image/x-icon">
<link href="/css/css1.css" rel="stylesheet" type="text/css">
<hrms:themes />
  <br>
  <br>  
  <table width="500" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable">
          <tr height="20">
       		<td align="left" class="TableRow">&nbsp;<bean:message key="label.information"/>&nbsp;</td>
          </tr> 
          <tr >
                      <td align="left" valign="middle" nowrap style="height:120">
                      <p>&nbsp;<font size="3">当前另一用户已使用此帐号登录</font></p>
                      <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" name="radioid" value="1" checked="checked"/>注销另一使用此帐号的用户</p>
                      <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" name="radioid" value="2"/>返回登录界面并使用不同帐号登录</p>
                      </td>
                    </tr>
                    <tr >
                      <td align="center" style="height:35">
              				<input type="button" name="btnreturn" value="确定" onclick="doAction();" class="mybutton">
                      </td>
                    </tr>   
          
  </table> 
<script type="text/javascript">
//<!--
function doAction(){
	var _radios = document.getElementsByName('radioid');
	var _radiovalue = 1;
	for(var i=0;i<_radios.length;i++){
		var _radio=_radios[i];
		if(_radio.checked){
			_radiovalue=_radio.value;
			break;
		}
	}
	//alert(_radiovalue);
	if(1==_radiovalue){
		onlyLogon();
	}else if(2==_radiovalue){
		logout();
	}
}
function onlyLogon(){
	if(confirm("确定要注销另一使用此帐号的用户吗？"))
	  {
	      var url = "/templates/index/hrlogon.jsp";
	      <%if(userView.isFirstLogin()){ %>
	      url="/system/security/first_pwd_change.jsp";
	      <%}else{%>
	      url="/servler/sys/logout?flag=only_logon_one<%=logout_flag %>";
	      <%} %>
	      window.location.href=url;
	      //newwin=window.open(url,"_parent","toolbar=no,location=0,directories=0,status=no,menubar=no,scrollbars=no,resizable=yes","true");
	      //window.opener=null;//不会出现提示信息
	      //parent.window.close();    
	  }
}

function logout()
{
  if(confirm("确定要返回登录界面并使用不同帐号登录吗？"))
  {
      var url = "/templates/index/hrlogon.jsp";
      url="/servler/sys/logout?flag=<%=logout_flag %>";
      newwin=window.open(url,"_parent","toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
      //window.opener=null;//不会出现提示信息
      //parent.window.close();    
  }
} 
//-->
</script>
