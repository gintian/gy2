<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.general.template.TemplateForm"%>

<%@ page import="com.hrms.struts.valueobject.UserView,
                com.hrms.struts.taglib.CommonData,
                java.util.*"%>


<%
    UserView userView = (UserView) request.getSession().getAttribute(WebConstant.userView);
         String bosflag= userView.getBosflag();
    
%>
<%
	TemplateForm templateForm=(TemplateForm)session.getAttribute("templateForm");
%>




<script language="javascript">
	 var model='<%=(request.getParameter("model"))%>';
	 function save()
	 {
	     /*
	 	   if(trim(document.getElementById("topic").value).length==0)
	 	   {
	 	   		if(model=='6')
		 	   		alert(general_template_checkinopinion+"!");
	 	   		else
	 	   			alert(general_template_countersignopinion+"!");
	 	   		return;
	 	   }
	 	   */
	 	   if(document.getElementById("topic").value.length>0)
			   returnValue=document.getElementById("topic").value;
		   else
		  	   returnValue=" ";
	  	   window.close();	
	 
	 }
	
</script>
 
<html:form action="/general/template/edit_page">
<% if (!"hcm".equals(bosflag)){  %>
<br>
<%} %>
	<table width="626" border="0" cellpadding="0" cellspacing="0" align="center">
		<tr height="20">
			<td align="left"  class="TableRow">&nbsp;
			<% if(request.getParameter("model").equals("6")){ %>
			 <bean:message key="general.template.checkinopinion"/> 
			<% }else{ %>
			 <bean:message key="general.template.countersignopinion"/> 
			<% } %>
			&nbsp;</td> 
		</tr>
		<tr>
			<td align="left"  nowrap>
                <html:textarea name="templateForm" property="topic"  style="width:100%;height:320px;display:block;" />
			</td>
			 
		</tr>
		<tr class="list3">
			<td align="left" >&nbsp;
			</td>
		</tr>
		<tr class="list3">
			<td align="center"  >
			<logic:notEqual name="templateForm" property="taskState" value="5"> 
			 	<button extra="button" onclick="save();">
            		<bean:message key="lable.func.main.save"/>
	 	        </button>
	 	     </logic:notEqual> 
		 &nbsp;
	 	        <button extra="button" onclick="window.close();">
            		<bean:message key="button.close"/>
	 	        </button>	
                
	 	    </td>
		</tr>
	</table>
</html:form>

