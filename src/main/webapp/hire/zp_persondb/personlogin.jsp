<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.hire.ZppersondbForm"%>
<%
  String css_url="/css/css1.css";
%>  
<script language="javascript">
    function exeButtonAction(actionStr,target_str)
   {
       target_url=actionStr;
       window.open(target_url,target_str); 
   }
</script>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<script language="JavaScript" src="/js/function.js"></script>
<hrms:themes></hrms:themes>
<html:form action="/hire/zp_persondb/personlogin"> 
   <br>
  <br>
  <br>   
<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap colspan="2">
		<bean:message key="label.zp_persondb.userlogin"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
       <tr>
           <td align="right"  nowrap>
              <bean:message key="hire.zp_persondb.username"/>
           </td>
           <td align="left"  nowrap>
            <html:text   name="zppersondbForm" property="username" styleClass="textColorWrite" maxlength="20" size="20"/>  
           </td>
       </tr>
         <tr>
           <td align="right"  nowrap>
               <bean:message key="hire.zp_persondb.password"/>
           </td>
           <td align="left"  nowrap>
              <html:password   name="zppersondbForm" property="password" styleClass="textColorWrite" maxlength="20" size="20"/>          
           </td>
        </tr>
        <tr>
           <td align="right"  nowrap>
              <hrms:submit styleClass="mybutton"  property="b_entry">
                  <bean:message key="button.ok"/>
	      </hrms:submit> 
           </td>
           <td align="left"  nowrap>
                <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeButtonAction('/hire/zp_persondb/personinfoenroll.do?b_new=link','il_body')">
           </td>
        </tr>
  </table>
</html:form>
