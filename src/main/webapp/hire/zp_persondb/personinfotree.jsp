<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.hire.ZppersondbForm"%>
<%
  String css_url="/css/css1.css";
%>  
<script language="JavaScript">
	function addInfo()
	{
		zppersondbForm.action='/hire/zp_interface/applyuseraccount.jsp?operate=add&clear=1';
   		zppersondbForm.target="i_body";
   		zppersondbForm.submit();
	}

</script>


<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<hrms:themes></hrms:themes>
<script language="JavaScript" src="/js/function.js"></script>
<html:form action="/hire/zp_persondb/personinfoenroll"> 
   <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" background="/images/back1.jpg">
      <% ZppersondbForm zppersondbForm=(ZppersondbForm)session.getAttribute("zppersondbForm");
      if(zppersondbForm.getZpsetlist().size()>0){%>  
         <tr>
           <td align="right">
             <a href="/hire/zp_persondb/upenrollinfophoto.do?a0100=${zppersondbForm.a0100}&i9999=I9999&setname=A00" target="mil_body" > <hrms:ole name="zppersondbForm" dbpre="zppersondbForm.userbase" a0100="a0100" scope="session" height="120" width="80"/></a>
           </td>
          </tr>    
       <%}%>  
      <logic:iterate  id="setlist"   name="zppersondbForm"  property="zpsetlist"> 
         <tr>
           <td align="right"  nowrap>
             <logic:equal name="setlist" property="fieldsetid" value="A01">   
                <a href="/hire/zp_persondb/personinfoenroll.do?b_enroll=link&a0100=${zppersondbForm.a0100}&i9999=I9999&actiontype=${zppersondbForm.actiontype}&useraccount=${zppersondbForm.useraccount}&setname=A01" target="mil_body"><bean:write  name="setlist" property="customdesc"/></a>
             </logic:equal>
              <logic:notEqual name="setlist" property="fieldsetid" value="A01">   
                <a href="/hire/zp_persondb/searchdetailenrollinfo.do?b_search=search&a0100=${zppersondbForm.a0100}&setname=${setlist.fieldsetid}" target="mil_body"><bean:write  name="setlist" property="customdesc"/></a>
             </logic:notEqual>
            </td>
            </tr>
         </logic:iterate>
         <tr>
           <td align="right">
              <a href="/hire/zp_persondb/searchdetailenrollinfo.do?b_media=link&a0100=${zppersondbForm.a0100}&setname=${setlist.fieldsetid}&userbase=${zppersondbForm.userbase}" target="mil_body"><bean:message key="hire.zp_persondb.certificate"/></a>
           </td>
          </tr>  
          <logic:equal name="zppersondbForm" property="isHandWork" value="1">
          	<tr>
	           <td align="right">
	             <a href="/hire/employActualize/personnelFilter/hirePositionList.do?b_query=link&a0100=${zppersondbForm.a0100}&userbase=${zppersondbForm.userbase}"  target="mil_body" ><font color='red'> <bean:message key="hire.choose.position"/></font></a> 
	           </td>
          </tr> 
          	<tr>
	           <td align="right">
				  <a href="javascript:addInfo()" ><font color='red'><bean:message key="hire.new.personinfo"/></font></a> 
	           </td>
          </tr> 
          </logic:equal>
          
   </table>
</html:form>
