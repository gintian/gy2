<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*, 
				org.apache.commons.beanutils.LazyDynaBean,
				com.hrms.hjsj.sys.DataDictionary,
				com.hrms.hjsj.sys.FieldItem,
				com.hjsj.hrms.actionform.performance.options.PerRelationForm" %>

<script language="javascript" src="/performance/options/kh_relation/per_relation.js"></script>
<html:form action="/performance/options/kh_relation/mainBodyList"> 
<%
	PerRelationForm myForm=(PerRelationForm)session.getAttribute("perRelationForm");	
	HashMap joinedObjs = (HashMap)myForm.getJoinedObjs();
	%>  
	<table width="100%">
		<tr><td>
			<table id='a_table' width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
			   	  <thead>
			 		 <tr >
				         <td align="center" class="TableRow" nowrap>				
							 <input type="checkbox" name="selbox" onclick="batch_select(this,'mainbodyID');" title='<bean:message key="label.query.selectall"/>'>
				         </td>         
				         <td align="center" class="TableRow" nowrap >
						   	<bean:message key="b0110.label"/>
					     </td>          
				         <td align="center" class="TableRow" nowrap >
							 <%
	         					FieldItem fielditem = DataDictionary.getFieldItem("E0122");
	         				 %>	         
			 				 <%=fielditem.getItemdesc()%>
					     </td>
					     <td align="center" class="TableRow" nowrap >
						 	 <bean:message key="e01a1.label"/>
					     </td>
					      <td align="center" class="TableRow" nowrap >
						 	 <bean:message key="hire.employActualize.name"/>
					     </td>
					     <td align="center" class="TableRow" nowrap >
							 <bean:message key="lable.performance.perMainBodySort"/>
					     </td> 	   	        	        
			         </tr>
			   	  </thead>
			   	  <%  int i=0; %>
			   	  <hrms:extenditerate id="element" name="perRelationForm" property="perMainbodyForm.list" indexes="indexes"  pagination="perMainbodyForm.pagination" pageCount="10" scope="session">
			   	  	<bean:define id="oid" name="element" property="object_id" />
			   	  	<bean:define id="mid" name="element" property="mainbody_id" />
			   	  	<bean:define id="m_bodyid" name="element" property="body_id" />
			   	  	<% 
			   	  			  	
					     		LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
					     		String object_id=(String)abean.get("object_id");
			   	  	
			   	  	i++;
			   	  	   if(i%2==1){ %>
			   	  	   <tr class='trShallow' >
			   	  	   <% } else { %>	   
			   	  	   	<tr class='trDeep'  >
			   	  	   <% } %>
			
				         <td align="center" class="RecordRow" nowrap>
						  	&nbsp;<input type='checkbox' name='mainbodyID' value='${mid}'  />
				         </td>         
				         <td align="left" class="RecordRow"    nowrap >
						   &nbsp; <bean:write name="element" property="b0110" filter="true"/>
					     </td>          
				         <td align="left" class="RecordRow"   nowrap>
						   &nbsp; <bean:write name="element" property="e0122" filter="true"/>
					     </td>   
					     <td align="left" class="RecordRow"   nowrap>
						    &nbsp;<bean:write name="element" property="e01a1" filter="true"/>
				         </td>         
				         <td align="left" class="RecordRow"   nowrap >
						    &nbsp;<bean:write name="element" property="a0101" filter="true"/>
					     </td>          
				         <td align="center" class="RecordRow" nowrap>				  
							<logic:equal name="perRelationForm" property="selfBodyId" value="${m_bodyid}">
								<html:select name="element" property="body_id" size="1" disabled="true" onchange="setType('${oid}','${mid}','body',this,'${m_bodyid}','${perRelationForm.selfBodyId}')" style="width:160px">
						  	  		<html:optionsCollection property="bodyTypes" value="dataValue" label="dataName"/>
								</html:select>	
							</logic:equal>	
							<logic:notEqual name="perRelationForm" property="selfBodyId" value="${m_bodyid}">
								    <%	if(joinedObjs.get(object_id)!=null){%>
									<html:select name="element" property="body_id" size="1" disabled="true" onchange="setType('${oid}','${mid}','body',this,'${m_bodyid}','${perRelationForm.selfBodyId}')" style="width:160px">
						  	  			<html:optionsCollection property="allBodyTypes" value="dataValue" label="dataName"/>
									</html:select>	
								<%}else{ %>
									<html:select name="element" property="body_id" size="1"  onchange="setType('${oid}','${mid}','body',this,'${m_bodyid}','${perRelationForm.selfBodyId}')" style="width:160px">
						  	  			<html:optionsCollection property="bodyTypes" value="dataValue" label="dataName"/>
									</html:select>	
								<%}%>
							</logic:notEqual>	
					     </td>         	        	        
			         </tr>   	  
			   	   </hrms:extenditerate>  	  
				</table>
				<table width="100%" align="center" class="RecordRowP">
					<tr>
						<td valign="bottom" align="left" class="tdFontcolor">
							 <bean:message key="label.page.serial"/>
							<bean:write name="perRelationForm"
								property="perMainbodyForm.pagination.current" filter="true" />
								<bean:message key="label.page.sum"/>
							<bean:write name="perRelationForm"
								property="perMainbodyForm.pagination.count" filter="true" />
						<bean:message key="label.page.row"/>
							<bean:write name="perRelationForm"
								property="perMainbodyForm.pagination.pages" filter="true" />
							<bean:message key="label.page.page"/>
						</td>
						<td align="right" nowrap class="tdFontcolor">
							<p align="right">
								<hrms:paginationlink name="perRelationForm"
									property="perMainbodyForm.pagination" nameId="perMainbodyForm"
									propertyId="roleListProperty">
								</hrms:paginationlink>
						</td>
					</tr>
				</table>	
			</td>
		</tr>
		<tr>
			<td style="height:35px">
				<logic:equal name="perRelationForm" property="enableFlag" value="1">
					<hrms:priv func_id="326060713">	
						<input type='button' value='<bean:message key='button.delete'/>' onclick="delMainBody2('${perRelationForm.objSelected}');" class="mybutton" />
					</hrms:priv>	
				</logic:equal>
			</td>
		</tr>
	</table>	
</html:form>