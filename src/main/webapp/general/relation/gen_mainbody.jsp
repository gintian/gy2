<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*, 
				org.apache.commons.beanutils.LazyDynaBean,
				com.hrms.hjsj.sys.DataDictionary,
				com.hrms.hjsj.sys.FieldItem,
				com.hjsj.hrms.actionform.general.relation.GenRelationForm" %>
<style>
.td_no_t_r
{ 
    BORDER-TOP: 0pt;  
    BORDER-RIGHT: 0pt; 
}
.td_no_t
{ 
    BORDER-TOP: 0pt;  
}
</style>
<script language="javascript" src="/general/relation/gen_relation.js"></script>
<script type="text/javascript">
   document.body.style.marginTop="0px";
</script>
<html:form action="/general/relation/relationmainbodylist"> 
<%
	GenRelationForm myForm=(GenRelationForm)session.getAttribute("genRelationForm");	
	HashMap joinedObjs = (HashMap)myForm.getJoinedObjs();
	String codeset=myForm.getCodeset();
	String code=myForm.getCode();
	String operate=myForm.getOperate();
	%>  
	<html:hidden property="isDelMainbody" name="genRelationForm" />
	<table width="100%" style="margin-top:-3px;">
		<tr><td>
			<table id='a_table'  width="100%" border="0" cellspacing="0" align="center" cellpadding="0"  style="border:0px; margin-top:0px;">
			   	  <thead>
			 		 <tr class='fixedHeaderTr'>
				         <td align="center" width="9%" class="TableRow td_no_t_r"  nowrap>				
							 <input type="checkbox" name="selbox" onclick="batch_select(this,'mainbodyID');" title='<bean:message key="label.query.selectall"/>'>
				         </td>   
				          <logic:equal name ="genRelationForm" property="actor_type" value="1">      
				         <td align="center" class="TableRow td_no_t_r" nowrap >
						   	<bean:message key="b0110.label"/>
					     </td>          
				         <td align="center" class="TableRow td_no_t_r"  nowrap >
							 <%
	         					FieldItem fielditem = DataDictionary.getFieldItem("E0122");
	         				 %>	         
			 				 <%=fielditem.getItemdesc()%>
					     </td>
					     <td align="center" class="TableRow td_no_t_r"  nowrap >
						 	 <bean:message key="e01a1.label"/>
					     </td>
					     </logic:equal>
					      <logic:equal name ="genRelationForm" property="actor_type" value="4">  
					      <td align="center" class="TableRow td_no_t_r"  width="150" nowrap >
						   	用户组
					     </td>
					     <td align="center" class="TableRow td_no_t_r"  width="150" nowrap >
                                                                           用户名
                         </td>     
					      </logic:equal>
					      <td align="center" class="TableRow td_no_t_r" nowrap >
						 	 <bean:message key="hire.employActualize.name"/>
					     </td>
					     <td align="center" class="TableRow td_no_t"  nowrap >
							 审批层级
					     </td> 	   	        	        
			         </tr>
			   	  </thead>
			   	  <%  int i=0; %>
			   	  <hrms:extenditerate id="element" name="genRelationForm" property="genMainbodyForm.list" indexes="indexes"  pagination="genMainbodyForm.pagination" pageCount="10" scope="session">
			   	  	<bean:define id="oid" name="element" property="object_id" />
			   	  	<bean:define id="mid" name="element" property="mainbody_id" />
			   	  	<% 
			   	  			  	
					     		LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
					     		String object_id=(String)abean.get("object_id");
			   	  	
			   	  	i++;
			   	  	   if(i%2==1){ %>
			   	  	   <tr class='trShallow' >
			   	  	   <% } else { %>	   
			   	  	   	<tr class='trDeep'  >
			   	  	   <% } %>
			
				         <td align="center"  width="9%"  class="RecordRow td_no_t_r" nowrap>
						  	<input type='checkbox' name='mainbodyID' value='${mid}'  />
				         </td>         
				         <td align="left" class="RecordRow td_no_t_r"    nowrap >
						    <bean:write name="element" property="b0110" filter="true"/> &nbsp;
					     </td>
					      
					     <logic:equal name ="genRelationForm" property="actor_type" value="1">          
				         <td align="left" class="RecordRow td_no_t_r"   nowrap>
						    <bean:write name="element" property="e0122" filter="true"/> &nbsp;
					     </td>   
					     <td align="left" class="RecordRow td_no_t_r"   nowrap>
						    <bean:write name="element" property="e01a1" filter="true"/> &nbsp;
				         </td>  
				         </logic:equal>
				          
				         
                                  
				         <td align="left" class="RecordRow td_no_t_r"   nowrap >
						    <bean:write name="element" property="a0101" filter="true"/><!-- 用户名 -->
					     </td> 
					     
					      <logic:equal name ="genRelationForm" property="actor_type" value="4">            
                         <td align="left" class="RecordRow td_no_t_r"   nowrap>
                            &nbsp;<bean:write name="element" property="username" filter="true"/>
                         </td>  <!-- 姓名 -->
                         </logic:equal>  
                            
				         <td align="center" class="RecordRow td_no_t" nowrap>				  
							<bean:write name="element" property="sp_grade" filter="true"/><!-- 审批层级 -->
					     </td>         	        	        
			         </tr>   	  
			   	   </hrms:extenditerate>  	  
				</table>
				<table width="100%" align="center" class="RecordRowP" cellspacing="0" cellpadding="0">
					<tr>
						<td valign="bottom" align="left" class="tdFontcolor">
							 <bean:message key="label.page.serial"/>
							<bean:write name="genRelationForm"
								property="genMainbodyForm.pagination.current" filter="true" />
								<bean:message key="label.page.sum"/>
							<bean:write name="genRelationForm"
								property="genMainbodyForm.pagination.count" filter="true" />
						<bean:message key="label.page.row"/>
							<bean:write name="genRelationForm"
								property="genMainbodyForm.pagination.pages" filter="true" />
							<bean:message key="label.page.page"/>
						</td>
						<td align="right" nowrap class="tdFontcolor">
							<p align="right">
								<hrms:paginationlink name="genRelationForm"
									property="genMainbodyForm.pagination" nameId="genMainbodyForm"
									propertyId="roleListProperty">
								</hrms:paginationlink>
						</td>
					</tr>
				</table>	
			</td>
		</tr>
		<tr>
			<td>
			
					<hrms:priv func_id="9A510703">	
						<input type='button' value='<bean:message key='button.delete'/>' onclick="delMainBody2('${genRelationForm.objSelected}','<%=codeset%>','<%=code%>','<%=operate%>');" class="mybutton" style="magrin-top:0px;"/>
					</hrms:priv>	
				
			</td>
		</tr>
	</table>	
</html:form>
