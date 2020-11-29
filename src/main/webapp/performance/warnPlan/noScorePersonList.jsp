<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="java.util.*,
				 com.hrms.hjsj.sys.DataDictionary,
				 com.hrms.hjsj.sys.FieldItem"%>

<hrms:themes />
<script language="JavaScript" src="/ajax/basic.js"></script>
<%
 	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
 	%>
<style>
.keyMatterDiv 
{ 
	overflow:auto; 
	height:expression(document.body.clientHeight-150);
	width:100%;	
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
}
.MyListTable 
{
	border:0px solid #C4D8EE;
	border-collapse:collapse; 
	BORDER-BOTTOM: medium none; 
    BORDER-LEFT: medium none; 
    BORDER-RIGHT: medium none; 
    BORDER-TOP: medium none; 
    margin-top:-1px;
    margin-left:-1px;
    margin-right:-1px;
}   
</style>


<html:form action="/performance/warnPlan/noScorePersonList">

<br>
<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0"  >
	<tr align="center">
		<td align="left" height='25' nowrap >                	 		
	       	<strong><font size='3'><bean:write name="warnPlanForm" property="plan_name" filter="true" /></font></strong>
	 	</td>
	</tr>
	<tr>		
		<td align="left" nowrap>
			<div class="keyMatterDiv">	   	
				<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="MyListTable" >
					<thead>
				    	<tr class="trDeep" >   
					    	<%    
					    		FieldItem fielditem = DataDictionary.getFieldItem("E0122");
					    	%>
					      	<td align="center" class="TableRow" width='25%' nowrap >&nbsp;<%=fielditem.getItemdesc()%>&nbsp;</td>
					
					      	<td align="center" class="TableRow" width='25%' nowrap >      
					      		<bean:message key="lable.performance.perMainBody"/>
					      	</td>
					      	<td align="center" class="TableRow" width='50%' nowrap >      
					     	 	<bean:message key="lable.performance.perObject"/>    
					      	</td>              
				   		</tr>   
					</thead>
					<% int i=0; %>
				    <hrms:extenditerate id="element" name="warnPlanForm" property="setlistform.list" indexes="indexes"  pagination="setlistform.pagination" pageCount="20" scope="session">
				        <%
				          if(i%2==0){
				        %>
				          <tr class="trShallow">
				        <%}else{%>
				          <tr class="trDeep">
				        <%
				          }
				          i++;          
				        %>          
				         	<td align="left" class="RecordRow" nowrap >
				              &nbsp;<bean:write name="element" property="e0122" filter="false"/>        
				         	</td>  
				            <td align="left" class="RecordRow" nowrap >
				              &nbsp;<bean:write  name="element" property="userName" filter="false"/>         
				            </td>  
				            <td align="left" class="RecordRow" >
				              &nbsp;<bean:write  name="element" property="objScoreStatus" filter="false"/>         
				            </td>
				               
				         </tr>
					</hrms:extenditerate> 
				</table>
			</div>									
		</td>
	</tr>	 	 	
</table>



<table width="90%" align="center" class="RecordRowP">
	<tr>
		<td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
			<bean:write name="warnPlanForm" property="setlistform.pagination.current" filter="true" />
			<bean:message key="label.page.sum"/>
			<bean:write name="warnPlanForm" property="setlistform.pagination.count" filter="true" />
			<bean:message key="label.page.row"/>
			<bean:write name="warnPlanForm" property="setlistform.pagination.pages" filter="true" />
			<bean:message key="label.page.page"/>
		</td>
	    <td align="right" nowrap class="tdFontcolor">	               
			<p align="right">
		    <hrms:paginationlink name="warnPlanForm" property="setlistform.pagination"
				nameId="setlistform" propertyId="roleListProperty">
			</hrms:paginationlink>
		</td>
	</tr>
</table>

<table width="90%" align="center">
	<tr>
		<td align="left" style="height:35px"> 
		<%if("hcm".equals(userView.getBosflag())){ %>
			<html:button styleClass="mybutton" property="bc_btn1" onclick="window.location.replace('/templates/index/hcm_portal.do?b_query=link');">
				<bean:message key="button.return" />
			</html:button>
			<%}else{ %>
			<html:button styleClass="mybutton" property="bc_btn1" onclick="window.location.replace('/templates/index/portal.do?b_query=link');">
				<bean:message key="button.return" />
			</html:button>
			<%} %>
		</td>
	</tr>
</table>

</html:form>
