<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	String edition=request.getParameter("edition");
	String zp_pos_id_value=request.getParameter("zp_pos_id_value");

	String url="/hire/zp_person/search_zp_position.do?b_requery=link";
	String target="mil_body";
	if(edition!=null)
	{
		url="/hire/employNetPortal/search_zp_position.do?b_query=link&operate=init";
		target="i_body";
	}
%>

<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";	
%>
 
<script language="javascript">
   function exeButtonAction(actionStr,target_str)
  {
  
  	target_url=actionStr;
    window.open(target_url,'i_body'); 
  /*  target_url=actionStr;
    window.open(target_url); */
  }  
   function exeButtonAction1(actionStr,target_str)
  {
    target_url=actionStr;
    window.open(target_url,target_str); 
  } 
  function validates()
  {
      if(hireLoginForm.userName.value=='')
    {
      alert("用户名不能为空!");
      hireLoginForm.userName.focus();
      return false;
    }
    return true;
   
  } 
</script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>


<hrms:themes></hrms:themes>
<logic:equal name="zppersonForm" property="loginFlag" value="2"> 
 <body bgcolor="#FFFFFF" text="#000000" style="margin:0 0 0 0">
 <table align="center" cellspacing="0" cellpadding="0" class="tabpos">
 <tr>
 <td width="100%" align="center">
   <br>
   <br>
    <bean:message key="label.zp_person.alreadyapply"/>    
 </td>
 </tr>
 <tr>
   <td width="100%" align="center">
      <br>
    <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeButtonAction1('<%=url%>','<%=target%>')"> 
  </td>
  </tr>
 </table>
 </body>
 </logic:equal>
 <logic:equal name="zppersonForm" property="loginFlag" value="1"> 
 <body bgcolor="#FFFFFF" text="#000000" style="margin:0 0 0 0">
 <table align="center" cellspacing="0" cellpadding="0" class="tabpos">
 <tr>
 <td width="100%" align="center">
   <br>
   <br>
    <bean:message key="label.zp_person.applysuccess"/>    
 </td>
 </tr>
 <tr>
   <td width="100%" align="center">
      <br>
    <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeButtonAction1('<%=url%>','<%=target%>')"> 
  </td>
  </tr>
 </table>
 </body>
 </logic:equal>
  <logic:equal name="zppersonForm" property="loginFlag" value="0"> 
   <body bgcolor="#FFFFFF" text="#000000" style="margin:0 0 0 0">
  <html:form action="/hire/zp_persondb/apply_zp_positionLogin" onsubmit="return validates()" target="i_body" >
   <br>
<table width="500" border="0" cellpadding="0" cellspacing="0" align="center">
   <tr height="20">
       		<!--  <td width=10 valign="top" class="tableft"></td>
       		<td width=130 align=center class="tabcenter">&nbsp;<bean:message key="label.zp_person.userlogin"/>&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td>-->  
       		<td align=center class="TableRow">&nbsp;<bean:message key="label.zp_person.userlogin"/>&nbsp;</td>            	      
  </tr> 
 
   <tr>
         <td  class="framestyle9">
           <br>
           <div align="center"><bean:write name="hireLoginForm" property="messageReturn"  filter="true"/></div>
           <br>
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">     
                 <tr class="list3">
                    <td align="right" nowrap width="30%"><bean:message key="label.login.username"/></td>
                    <td width="30%"><html:text property="userName"  styleClass="shuoming"/></td>
                    <td>&nbsp;</td>    
                    <td rowspan="3">  
                            <input type="image" src="/images/login.gif">                      
                            <input type="image" src="/images/register.gif"    onclick="exeButtonAction('/hire/zp_persondb/applyaccount.do','il_body');return false;">  
                    </td>
                  </tr>
                  <tr class="list3"> 
                     <td width="30%" align="right"><bean:message key="label.login.password"/></td>
                     <td width="30%"><html:password property="passWord" maxlength="8"  styleClass="shuoming"/></td>
                     <td>&nbsp;</td>    
                  </tr>                  
                 </table>   
                 <br>  
              </td>
          </tr>
 
  </table>
 </html:form>
 </body>
  </logic:equal>

