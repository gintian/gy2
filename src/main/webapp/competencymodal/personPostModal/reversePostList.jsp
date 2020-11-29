<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,	
				 com.hjsj.hrms.actionform.competencymodal.personPostModal.PersonPostModalForm,			 
				 com.hrms.hjsj.sys.DataDictionary,
				 com.hrms.hjsj.sys.FieldItem,
				 com.hrms.struts.taglib.CommonData,
				 com.hjsj.hrms.utils.ResourceFactory,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant" %>
				 
<%  
		PersonPostModalForm personPostModalForm=(PersonPostModalForm)session.getAttribute("personPostModalForm");
		String onlyFild=(String)personPostModalForm.getOnlyFild();	  // 人员唯一性指标
					
		UserView userView=(UserView)session.getAttribute(WebConstant.userView);
		
%>
				 
<html>
  <head>
     <link href="/css/css1.css" rel="stylesheet" type="text/css">
  </head>
  <script type="text/javascript" src="/js/constant.js"></script>
  
<style>

.myfixedDiv
{ 
	overflow:auto; 
	height:expression(document.body.clientHeight-150);
	width:expression(document.body.clientWidth-10); 
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
}
</style>
<hrms:themes></hrms:themes>
<script type="text/javascript">

// 返回统计页面
function backPostMatch()
{
   	document.personPostModalForm.action="/competencymodal/personPostModal/personPostMatch.do?b_query=link&signLogo=changeSubSetMenu";
	document.personPostModalForm.submit();
}
 
</script>

<html:form action="/competencymodal/personPostModal/reverseResultList">
   	<br/>
   	<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0">	
   		<tr>
   			<td width='100%'>
			   	<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">	
			   	  	<thead>
				   	     <%
							 	FieldItem fielditem = DataDictionary.getFieldItem("E0122");			  			 	
					     %>
				          <tr>				            
				            <td align="center"  class="TableRow" nowrap>
				        		<bean:message key="b0110.label"/>
				            </td>       				   		  		 
				   			<td align="center"   class="TableRow" nowrap>
				        		<%=fielditem.getItemdesc()%>
				            </td> 
				            <td align="center"   class="TableRow" nowrap>
				        		<bean:message key="e01a1.label"/>
				            </td> 
				            <td align="center"  class="TableRow" nowrap>
				        		<bean:message key="kq.card.emp.name"/>
				            </td>
				            <% if(onlyFild!=null && onlyFild.trim().length()>0){%>
				            <td align="center"  class="TableRow" nowrap>
				        		<%= DataDictionary.getFieldItem(onlyFild).getItemdesc()%>
				            </td>
				            <% }%>
				            <td align="center"  class="TableRow" nowrap>
				        		<bean:message key="lable.performance.evaluation.ppd"/>
				            </td>
				            <td align="center"  class="TableRow" nowrap>
				        		<bean:message key="lable.performance.evaluation.ppresult"/>
				            </td> 				         
				         </tr>
			     	</thead>
			   
				   	<% int i=1; %>
				    <hrms:extenditerate id="element" name="personPostModalForm" property="personListForm.list" indexes="indexes"  pagination="personListForm.pagination" pageCount="20" scope="session">
				   		<%
				          if(i%2==1)
				          {
				          %>
				          <tr class="trShallow">
				          <%}
				          else
				          {%>
				          <tr class="trDeep">
				          <%
				          }
				          %>					         	
					         	<td align="left" class="RecordRow" nowrap>
					         		&nbsp;<bean:write name="element" property="b0110" filter="false"/>
					         	</td>					            					         
					   			<td align="left" class="RecordRow" nowrap>
					        		&nbsp;<bean:write name="element" property="e0122" filter="false"/>
					            </td> 
					            <td align="left" class="RecordRow" nowrap>
					        		&nbsp;<bean:write name="element" property="e01a1" filter="false"/>
					            </td> 
					            <td align="left" class="RecordRow" nowrap>
					        		&nbsp;<bean:write name="element" property="a0101" filter="false"/>
					            </td>
					            
					            <%-- 
					            <td align="left" class="RecordRow" nowrap>
					        		&nbsp;
					        		<a href="javascript:selectObjectMage('<bean:write name="element" property="object_id" filter="true"/>');">
										<bean:write name="element" property="a0101" filter="false"/>
					           		</a>
					            </td>					            					            
					            --%>
					            
					            <% if(onlyFild!=null && onlyFild.trim().length()>0){%>
					            <td align="right" width="150" class="RecordRow" nowrap>
					        		<bean:write name="element" property="onlyFild" filter="false"/>&nbsp;
					            </td> 
					            <% }%>
					            
					            <td align="right" class="RecordRow" nowrap>
					        		<bean:write name="element" property="mateSurmise" filter="false"/>&nbsp;
					            </td> 
					            <td align="center" class="RecordRow" nowrap>
					        		<bean:write name="element" property="resultdesc" filter="false"/>
					            </td>  
					        
				         </tr>
				         
				         <% i++; %>
				   	</hrms:extenditerate>
			    </table> 
    		</td>
		</tr>
		<tr>
			<td width='100%'>
			   	<table width="100%" align="center" class="RecordRowP">
					<tr>
						<td valign="bottom" class="tdFontcolor">
							<bean:message key="label.page.serial"/>
							<bean:write name="personPostModalForm" property="personListForm.pagination.current" filter="true" />
							<bean:message key="label.page.sum"/>
							<bean:write name="personPostModalForm" property="personListForm.pagination.count" filter="true" />
							<bean:message key="label.page.row"/>
							<bean:write name="personPostModalForm" property="personListForm.pagination.pages" filter="true" />
							<bean:message key="label.page.page"/>
						</td>
					    <td align="right" nowrap class="tdFontcolor">
							<p align="right">
							<hrms:paginationlink name="personPostModalForm" property="personListForm.pagination" nameId="personListForm" propertyId="roleListProperty">
							</hrms:paginationlink>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td width='100%' style="height:35px" align="center"> 					
      			<input type="button" name="addbutton" value="<bean:message key="kq.search_feast.back"/>" class="mybutton" onclick="backPostMatch();">     			  			
    		</td>
		</tr>
	</table>
   </html:form>
</html>
