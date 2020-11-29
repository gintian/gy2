<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.frame.utility.AdminCode" %>
<script language="javascript">

function editsort(targetsortid,index)
	{
		var target_url="/report/actuarial_report/validate_rule/target_sort.do?b_edit=edit&targetsortid="+targetsortid;
	    var return_vo= window.showModalDialog(target_url,1, 
	        "dialogWidth:540px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
	    if(return_vo!=null)
	    {
		  var in_obj=document.getElementById(index);  
		  in_obj.innerHTML=return_vo.mess;
	    }else
	    {
		  var in_obj=document.getElementById(index);  
	    }
	}
	
</script>
<%
	int i=0;
	
	try
	{
%>
<hrms:themes />
<html:form action="/report/actuarial_report/validate_rule/target_sort">
<br>

<table width="85%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap >
		<bean:message key="targetsortlist.menscope"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="targetsortlist.target"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="targetsortlist.setup"/>&nbsp;
	    </td>
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="targetsortForm" property="targetsortForm.list" indexes="indexes"  pagination="targetsortForm.pagination" pageCount="10" scope="session">
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow" id="<bean:write name="element" property="string(id)" filter="true"/>">
          <%}
          else
          {%>
          <tr class="trDeep" id="<bean:write name="element" property="string(id)" filter="true"/>">
          <%
          }
          i++; 
          
          %>  
            <td align="left" class="RecordRow" width="100" nowrap>
                  &nbsp;<bean:write name="element" property="codeitemdesc" filter="true"/>&nbsp;
	    </td>
            <td align="left" class="RecordRow" width="400"  id="<%=i %>" nowrap>
                 <bean:write name="element" property="targetcontent" filter="true"/>&nbsp;
               
	    </td>
            <td align="left" class="RecordRow" width="100" nowrap>
                &nbsp;
                  <a href="javascript:editsort('<bean:write name="element" property="codeitemid" filter="true"/>',<%=i %>)" >
			 <img src="/images/edit.gif" border=0>    
			</a> 
	    </td> 
	   </tr>
        </hrms:extenditerate>
        
</table>

</html:form> 
<%
	}
	catch(OutOfMemoryError error)
	{
	}
	catch(Exception ex)
	{
	}
%>
