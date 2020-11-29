<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_budget.budget_rule.definition.BudgetDefForm" %>
<%
	BudgetDefForm budgetDefForm = (BudgetDefForm)session.getAttribute("budgetDefForm");
%>
<style id=iframeCss>

div{
	cursor:hand;font-size:12px;
   }
a{
text-decoration:none;color:black;font-size:12px;
}

a.a1:active {
	color: #003100;
	text-decoration: none;
}
a.a1:hover {
	color: #FFCC00;
	text-decoration: none;
}
a.a1:visited {	
	text-decoration: none;
}
a.a1:link {
	color: #003100;
	text-decoration: none;
}
</style>
<hrms:themes />
<script language="JavaScript" src="/gz/gz_budget/budget_rule/definition/budgetDef.js"></script>
<html:form action="/gz/gz_budget/budget_rule/definition">

<table width="700px" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="margin-top:60px;">
   	  <thead>
           <tr>
            	<td align="center" class="TableRow" nowrap width="10%">
					<input type="checkbox" name="selbox" onclick="batch_select(this,'budgetlistform.select');" title='<bean:message key="label.query.selectall"/>'>
	    		</td>         
            	<td align="center" class="TableRow" nowrap width="10%">
					<bean:message key="lable.menu.main.id"/>&nbsp;
	    		</td>
            	<td align="center" class="TableRow" nowrap width="40%">
					<bean:message key="gz.budget.Tabname"/>&nbsp;
	    		</td> 
            	<td align="center" class="TableRow" nowrap width="10%">
					<bean:message key="kq.emp.change.compare"/>&nbsp;
            	</td>
            	<td align="center" class="TableRow" nowrap width="10%">
					<bean:message key="label.gz.property"/>&nbsp;
            	</td>
            	<td align="center" class="TableRow" nowrap width="10%">
					<bean:message key="label.order"/>&nbsp;
            	</td>                  		        	        	        
           </tr>
   	  </thead>
   	  
          <hrms:extenditerate id="element" name="budgetDefForm" property="budgetlistform.list" indexes="indexes"  pagination="budgetlistform.pagination" pageCount="21" scope="session">
          <tr onclick='tr_onclick(this,"#F3F5FC");'>
          	
            <td align="center" class="RecordRow" nowrap>
            	<logic:equal name="element" property="isCheckbox" value="1">
   					<hrms:checkmultibox name="budgetDefForm" property="budgetlistform.select" value="true" indexes="indexes"/>
   				</logic:equal>
   				<logic:notEqual name="element" property="isCheckbox" value="1">
   					<hrms:checkmultibox name="budgetDefForm" property="budgetlistform.select" value="true" indexes="indexes" style="display:none;"/>
   				</logic:notEqual>
	    	</td>
	    	<input type="hidden" name="ids" value="<bean:write name='element' property='tab_id' filter='true'/>">
	    	<input type="hidden" name="tablename" value="<bean:write name='element' property='tab_name' filter='true'/>">            
            <td align="right" class="RecordRow" nowrap>
                   &nbsp;<bean:write name="element" property="number" filter="true"/>&nbsp;
	    	</td>
            <td align="left" class="RecordRow" nowrap>
                    &nbsp;<bean:write  name="element" property="tab_name" filter="true"/>&nbsp;
            </td>
            <td align="center" class="RecordRow" nowrap>
            	<logic:equal name="element" property="validFlag" value="1">
                    <img src="/images/cc1.gif" border=0>
                </logic:equal>
            </td>
            <td align="center" class="RecordRow" nowrap>
            	<logic:equal name="element" property="isProperty" value="1">
                   <img src="/images/edit.gif" onclick="javascript:setProperty('<bean:write name="element" property="tab_id" filter="true"/>');" border=0 style="cursor:hand" >
                </logic:equal>
            </td>
            <td align="center" class="RecordRow" nowrap>
            	<logic:equal name="element" property="number" value="1">
                	&nbsp;&nbsp;
                </logic:equal>
            	<logic:notEqual name="element" property="number" value="1">
                	<img src="/images/up01.gif" border=0 style="cursor:hand" onclick="javaScript:moveRecord('<bean:write name="element" property="tab_id" filter="true"/>','<bean:write name="element" property="seq" filter="true"/>','up')">
                </logic:notEqual>
                
                <logic:notEqual name="element" property="number" value="${budgetDefForm.count}">
                	<img src="/images/down01.gif" border=0 style="cursor:hand" onclick="javaScript:moveRecord('<bean:write name="element" property="tab_id" filter="true"/>','<bean:write name="element" property="seq" filter="true"/>','down')">
                </logic:notEqual>
                <logic:equal name="element" property="number" value="${budgetDefForm.count}">
                	&nbsp;&nbsp;
                </logic:equal>    	    		   
	    	</td>   
          </tr>
        </hrms:extenditerate>
        
</table>
<table  width="700px" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="budgetDefForm" property="budgetlistform.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="budgetDefForm" property="budgetlistform.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="budgetDefForm" property="budgetlistform.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="budgetDefForm" property="budgetlistform.pagination"
				nameId="budgetlistform" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
<table  width="700px" align="center">
          <tr>
            <td align="center">
         		<input type="button" class="mybutton" value="<bean:message key='button.insert'/>" onclick="addItem();">
         		<input type="button" class="mybutton" value="<bean:message key='button.rename'/>" onclick="renameItem();">
	 			<input type="button" class="mybutton" value="<bean:message key='button.saveas'/>" onclick="saveasItem();">
	 			<input type="button" class="mybutton" value="<bean:message key='button.delete'/>" onclick="deleteBudgetTable();">
	 			<input type="button" class="mybutton" value="<bean:message key='gz.premium.countformula'/>" onclick="Openformula()">
            </td>
          </tr>          
</table>

</html:form>
