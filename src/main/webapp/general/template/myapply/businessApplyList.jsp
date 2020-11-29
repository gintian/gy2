<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				com.hjsj.hrms.actionform.general.template.MyApplyForm,
				com.hrms.hjsj.utils.Sql_switcher,
				com.hrms.struts.valueobject.UserView" %>
<%
    int i=0;
    String operationcode="";
    MyApplyForm templateForm=(MyApplyForm)session.getAttribute("businessApplyForm");
    operationcode=templateForm.getOperationcode();
    String type=templateForm.getType();
 
%>
<script>
function edit(tabId)
{
	alert(tabId);
}
function _refrash()
{
	businessApplyForm.action="/general/template/myapply/businessApplyList.do?b_query=link&operationcode=<%=(request.getParameter("type") != null ? request.getParameter("type") : "")%>";
	businessApplyForm.submit(); 
}
</script>

<hrms:themes />
<html:form action="/general/template/myapply/businessApplyList">
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">		
   	  <thead>
        <tr>
         <td align="center" class="TableRow" nowrap width="60">
		    <bean:message key="report.number"/>
         </td>           
         <td align="center" class="TableRow" nowrap>
		    <bean:message key="myapply.bussinessname"/>
         </td>         
         <td align="center" class="TableRow" nowrap >
		   <bean:message key="column.operation"/>
	     </td>        
         </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="businessApplyForm" property="setlistform.list" indexes="indexes"  pagination="setlistform.pagination" pageCount="${businessApplyForm.pagerows}" scope="session">
            <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow" onclick='tr_onclick(this,"#F3F5FC");'>
          <%}
          else
          {%>  
          <tr class="trDeep" onclick='tr_onclick(this,"#E4F2FC");'>
          <%
          }
          i++;          
          %>  
        <td align="center" class="RecordRow" nowrap>
            <bean:write name="element" property="string(tabid)" filter="true"/>
	    </td>          
        <td align="left" class="RecordRow" nowrap>
            <bean:write name="element" property="string(name)" filter="true"/>
	    </td>
       
	     <td align="center" class="RecordRow" nowrap>
	     	<% if(operationcode!=null&&operationcode.trim().length()>0){ %>
	     	 <a href="/general/template/myapply/busiTemplate.do?b_query=link&ins_id=0&returnflag=11&tabid=<bean:write name="element" property="string(tabid)" filter="true"/>" target="_parent"><img src="/images/edit.gif" border="0"></a>            	       
	     	
	     	<% }else{ %>
			  <a href="/general/template/myapply/busiTemplate.do?b_query=link&ins_id=0&returnflag=6&tabid=<bean:write name="element" property="string(tabid)" filter="true"/>" target="_parent"><img src="/images/edit.gif" border="0"></a>            	            
			<% } %>
		</td>        
       </tr>
    </hrms:extenditerate> 
</table>
 <table width="70%" class="RecordRowP" align="center"> 
    <tr>
       <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
          <bean:write name="businessApplyForm" property="setlistform.pagination.current" filter="true" />
          <bean:message key="label.page.sum"/>
          <bean:write name="businessApplyForm" property="setlistform.pagination.count" filter="true" />
          <bean:message key="label.page.row"/>
          <bean:write name="businessApplyForm" property="setlistform.pagination.pages" filter="true" />
          <bean:message key="label.page.page"/>
                            每页显示<html:text styleClass="text4" property="pagerows" name="businessApplyForm" size="3"></html:text>条&nbsp;&nbsp;<a href="javascript:_refrash();">刷新</a>
       </td>
       <td  align="right" nowrap class="tdFontcolor">
          <p align="right">
           <hrms:paginationlink name="businessApplyForm" property="setlistform.pagination"
                   nameId="setlistform">
           </hrms:paginationlink>
       </td>
    </tr>  
 </table>
</html:form>