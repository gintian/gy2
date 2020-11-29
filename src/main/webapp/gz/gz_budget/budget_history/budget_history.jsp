<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_budget.BudgetHistoryForm" %>
<%
	BudgetHistoryForm budgethistoryForm = (BudgetHistoryForm)session.getAttribute("budgethistoryForm");
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
<html:form action="/gz/gz_budget/budget_history">
<br>
<br>

<table width="60%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>       
            	<td align="center" class="TableRow" nowrap width="15%">
					预算年度
	    		</td>
            	<td align="center" class="TableRow" nowrap width="30%">
					预算类别
	    		</td>
            	<td align="center" class="TableRow" nowrap width="20%">
					创建日期
            	</td>
            	<td align="center" class="TableRow" nowrap width="20%">
					预算总额
            	</td>
            	<td align="center" class="TableRow" nowrap width="15%">
					查看
            	</td>                  		        	        	        
           </tr>
   	  </thead>
   	  
          <hrms:extenditerate id="element" name="budgethistoryForm" property="budgethistoryForm.list" indexes="indexes"  pagination="budgethistoryForm.pagination" pageCount="21" scope="session">
          <tr>         
            <td align="center" class="RecordRow" nowrap>
                   &nbsp;<bean:write name="element" property="yearNum" filter="true"/>&nbsp;
	    	</td>
            <td align="center" class="RecordRow" nowrap>
                    &nbsp;<bean:write  name="element" property="budgetType" filter="true"/>&nbsp;
            </td>
            <td align="center" class="RecordRow" nowrap>
					&nbsp;<bean:write  name="element" property="adjustDate" filter="true"/>&nbsp;
            </td>
            <td align="center" class="RecordRow" nowrap>
					&nbsp;<bean:write  name="element" property="ze" filter="true"/>&nbsp;
            </td>
            <td align="center" class="RecordRow" nowrap>
               	    <a href = "/gz/gz_budget/budget_examination.do?b_init=init&budget_id=<bean:write  name="element" property="budget_id" filter="true"/>&flag=2"><img src="/images/view.gif" border=0></a>		   
	    	</td>   
          </tr>
        </hrms:extenditerate>
        
</table>
<table  width="60%"  class='RecordRowP' align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="budgethistoryForm" property="budgethistoryForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="budgethistoryForm" property="budgethistoryForm.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="budgethistoryForm" property="budgethistoryForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="budgethistoryForm" property="budgethistoryForm.pagination"
				nameId="budgethistoryForm" propertyId="roleListProperty">
				</hrms:paginationlink></p>
			</td>
		</tr>
</table>
</html:form>
