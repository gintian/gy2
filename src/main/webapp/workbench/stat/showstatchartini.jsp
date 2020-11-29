<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>

<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
	  userName = userView.getUserId();
	   css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	}
	String date = DateStyle.getSystemDate().getDateString();
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>人力资源信息管理系统</title>

<script language="JavaScript" src="/js/validate.js"></script>
       <SCRIPT LANGUAGE=javascript>
   	/*******************************
   	 *设置统计信息
   	 *******************************/
    	function statset()
    	{
    	   target_url="/workbench/stat/statset.do?b_search=link&target=mil_body&isoneortwo=1";
    	   newwindow=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=170,left=220,width=400,height=468'); 
       }
   </SCRIPT> 
<script language="JavaScript">
function pf_ChangeFocus(e) 
			{
				  e=e?e:(window.event?window.event:null);//xuj update 2011-5-11 兼容firefox、chrome
			      var key = window.event?e.keyCode:e.which;
			      var t=e.target?e.target:e.srcElement;
			      if ( key==0xD && t.tagName!='TEXTAREA') /*0xD*/
			      {    
			   		   if(window.event)
			   		   	e.keyCode=9;
			   		   else
			   		   	e.which=9;
			      }
			   //按F5刷新问题,重复提交问题,右键菜单也设法去掉
			   if ( key==116)
			   {
			   		if(window.event){
			   		   	e.keyCode=0;
			   		   	e.returnValue=false;
			   		}else{
			   		   	e.which=0;
			   		   	e.preventDefault();
			   		}
			   }   
			   if ((e.ctrlKey)&&(key==82))//屏蔽 Ctrl+R  
			   {    
			        if(window.event){
			   		   	e.keyCode=0;
			   		   	e.returnValue=false;
			   		}else{
			   		   	e.which=0;
			   		   	e.preventDefault();
			   		}
			   } 
			}
function pf_return(form,element) 
{
	document.forms[form].elements[element].focus();
	return false;
}
</script>
   <link href="<%=css_url%>" rel="stylesheet" type="text/css">
<style>

</style>   
</head>

<body onKeyDown="return pf_ChangeFocus(event);" >
<html:form action="/workbench/stat/statshow">
<logic:equal name="statForm" property="istwostat" value="2">
    <table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1">
 <br>
 <br>
 <tr>  
     <td align="center"  nowrap>
         <bean:message key="workbench.stat.statidinfo"/>
     </td>        
  </tr>    
 </table> 
</logic:equal>
<logic:notEqual name="statForm" property="istwostat" value="2">
<br>
<table  align="center">
<tr align="left">  

    <td valign="top"  nowrap>
     <hrms:priv func_id="04010101">
      <a href="javascript:statset()"><bean:message key="workbench.stat.statsettitle"/></a>     
    </hrms:priv>
    </td>

      <td valign="top"  nowrap>
      &nbsp;&nbsp;<a href="/workbench/stat/showstatchart.do?chart_type=12">立体直方图</a>
    </td>
    <td valign="top"  nowrap>
      &nbsp;&nbsp;<a href="/workbench/stat/showstatchart.do?chart_type=11">平面直方图</a>
    </td>
    <td valign="top"  nowrap>
      &nbsp;&nbsp;<a href="/workbench/stat/showstatchart.do?chart_type=5">立体圆饼图</a>
    </td>
    <td valign="top"  nowrap>
      &nbsp;&nbsp;<a href="/workbench/stat/showstatchart.do?chart_type=20">平面圆饼图</a>
    </td>
  </tr>
  </table>

<br>
<table   width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
          <tr>
            <td align="center">
	 	<hrms:chart name="statForm" title="${statForm.snamedisplay}" scope="session" legends="list" data="" width="670" height="530" chart_type="${statForm.chart_type}">
	 	</hrms:chart>
            </td>
          </tr>          
</table>
</logic:notEqual>

</html:form>
</body>
</html>