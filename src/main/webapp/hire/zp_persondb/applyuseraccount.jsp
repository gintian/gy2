<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
     String css_url="/css/css1.css";	
%> 
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="javascript">
  function validate()
  {
    var tag=true;    
    tag=checkpassword(hireLoginForm.passWord,hireLoginForm.okpassWord);
    if(tag==false)
    {
       hireLoginForm.passWord.focus();
       return false;
    }     
    if(trim(hireLoginForm.userName.value)=='')
    {
      alert(USERNAME_IS_NOT_EMPTY+"!");
      hireLoginForm.userName.focus();
      return false;
    }
    return tag;   
  }
</script>
<hrms:themes></hrms:themes>
<body>
<br>
<html:form action="/hire/zp_persondb/applyuseraccount" onsubmit="return validate()">
 <br>
<table width="500" border="0" cellpadding="0" cellspacing="0" align="center">
   <tr height="20">
       		<!-- <td width=10 valign="top" class="tableft"></td>
       		<td width=130 align=center class="tabcenter">&nbsp;<bean:message key="label.zp_person.applyuseraccount"/>&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td>   --> 
       		<td align=center class="TableRow">&nbsp;<bean:message key="label.zp_person.applyuseraccount"/>&nbsp;</td>           	      
  </tr> 
   <tr>
         <td class="framestyle9">
           <br>
            <div align="center"><bean:write name="hireLoginForm" property="messageReturn"  filter="true"/></div>
           <br>
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">     
                 <tr class="list3">
                    <td align="right" nowrap width="30%"><bean:message key="label.login.username"/></td>
                    <td width="30%"><html:text  property="userName" value="" styleClass="shuoming" maxlength="${zppersondbForm.usermaxlenth}"/></td>
                    <td>&nbsp;</td>    
                    <td rowspan="3">  
                         <input type="image" src="/images/ok.gif">                         
                    </td>
                  </tr>
                  <tr class="list3"> 
                     <td width="30%" align="right"><bean:message key="hire.zp_persondb.password" /></td>
                     <td width="30%"><html:password  property="passWord" maxlength="8" styleClass="shuoming" value=""/></td>
                     <td>&nbsp;</td>    
                  </tr>   
                  <tr class="list3"> 
                     <td width="30%" align="right"><bean:message key="hire.zp_persondb.okpassword"/></td>
                     <td width="30%"><html:password  property="okpassWord"  maxlength="8" styleClass="shuoming" value=""/></td>
                     <td>&nbsp;</td>    
                  </tr>                  
                 </table>   
                 <br>  
              </td>
          </tr>
 
  </table>
</html:form>
</body>
