<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	int i=0;
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
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>

<html:form action="/hire/zp_interview/search_dept_poslist">

<br>
<table width="50%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>          
            <td align="center" class="TableRow" nowrap>
		<bean:message key="lable.zp_plan_detail.dept_id"/>
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="lable.zp_plan_detail.pos_id"/>            	
	    </td>	      		    		        	        	        
           </tr>
   	  </thead>
   	   <hrms:extenditerate id="element" name="zpInterviewForm" property="zpDeptPosForm.list" indexes="indexes"  pagination="zpDeptPosForm.pagination" pageCount="10" scope="session">
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
          <%
          }
          i++;          
          %>            
            <td align="left" class="RecordRow" nowrap>
                 <hrms:codetoname codeid="UM" name="element" codevalue="string(dept_id)" codeitem="codeitem" scope="page"/>  	      
                 <bean:write name="codeitem" property="codename" />&nbsp;
	    </td>
	    <td align="left" class="RecordRow" nowrap>
               <a href="/hire/zp_interview/interview_result.do?b_query=link&a_id=<bean:write name="element" property="string(zp_pos_id)" filter="true"/>"><hrms:codetoname codeid="@K" name="element" codevalue="string(pos_id)" codeitem="codeitem" scope="page"/>  	      
               <bean:write name="codeitem" property="codename" /></a>
	    </td>
       </tr>
       </hrms:extenditerate>   	    	    	
        
</table>
<table  width="50%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
					<bean:write name="zpInterviewForm" property="zpDeptPosForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="zpInterviewForm" property="zpDeptPosForm.pagination.count" filter="true" />
				  <bean:message key="label.page.row"/>
					<bean:write name="zpInterviewForm" property="zpDeptPosForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="zpInterviewForm" property="zpDeptPosForm.pagination"
				nameId="zpDeptPosForm" propertyId="zpDeptPosProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>

</html:form>
