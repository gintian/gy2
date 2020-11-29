<%@ page contentType="text/html; charset=UTF-8"%>

<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html>
<head>
<title></title>
<style type="text/css">
<!--
.tabpos {
	position: absolute;
	left: 520px;
	top: 280px;
}
-->
</style>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script language="JavaScript" src="/js/calendar.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
</head>
   <script language="JavaScript1.2">
   <!--设置IE工具条和菜单条都瞧不见?
    function pf_ChangeFocus() 
    { 
      key = window.event.keyCode;
      if ( key==0xD && event.srcElement.tagName!='TEXTAREA') /*0xD*/
      {
   	window.event.keyCode=9;
      }
    }   
   
//-->
   </script>

<script language="javascript">
  function exeButtonAction(actionStr,target_str)
  {
    target_url=actionStr;
    window.open(target_url); 
  }  
</script>
<hrms:themes></hrms:themes>
<body   bgcolor="#FFFFFF" text="#000000" style="margin:0 0 0 0">
<html:form  action="/hire/zp_persondb/employHireLogon" >
<br>
<table width="500" border="0" cellpadding="0" cellspacing="0" align="center">
   <tr height="20">
       		<!-- <td width=10 valign="top" class="tableft"></td>
       		<td width=130 align=center class="tabcenter">&nbsp;<bean:message key="label.zp_person.applyuseraccount"/>&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td> --> 
       		<td  align=center class="TableRow">&nbsp;<bean:message key="label.zp_person.applyuseraccount"/>&nbsp;</td>             	      
  </tr> 

   <tr>
         <td  class="framestyle9">
           <br>
           <div align="center"><bean:write name="hireLoginForm" property="messageReturn"  filter="true"/></div>
           <br>
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">     
                 <tr class="list3">
                    <td align="right" nowrap width="30%"><bean:message key="label.login.username"/></td>
                    <td width="30%"><html:text  property="userName"  styleClass="shuoming" maxlength="${zppersondbForm.usermaxlenth}"/></td>
                    <td>&nbsp;</td>    
                    <td rowspan="3">  
                         <input type="image" src="/images/login.gif">   
                         <input type="image" src="/images/register.gif"    onclick="exeButtonAction('/hire/zp_persondb/applyaccount.do','il_body');return false;">                        
                    </td>
                  </tr>
                  <tr class="list3"> 
                     <td width="30%" align="right"><bean:message key="hire.zp_persondb.password"/></td>
                     <td width="30%"><html:password  property="passWord" styleClass="shuoming" value=""/></td>
                     <td>&nbsp;</td>    
                  </tr>                                     
                 </table>   
                 <br>  
              </td>
          </tr> 
  </table>
</html:form>
</body>
</html>
