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
<script language="javascript">

</script>

<html:form action="/hire/zp_job/search_zp_joblist">

<br>
<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.select"/>&nbsp;
            </td>           
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.zp_job.zp_job_id"/>&nbsp;
	    </td>
            <td width="10%" align="center" class="TableRow" nowrap>
		<bean:message key="label.zp_job.name"/>            	
	    </td>	    
            <td width="10%" align="center" class="TableRow" nowrap>
		<bean:message key="label.zp_job.plan_id"/>            	
	    </td>
	    <td align="center" class="TableRow" nowrap>
		<bean:message key="lable.zp_plan.start_date"/>&nbsp;
            </td>           
            <td align="center" class="TableRow" nowrap>
		<bean:message key="lable.zp_plan.end_date"/>&nbsp;
	    </td>
            <td width="10%" align="center" class="TableRow" nowrap>
		<bean:message key="lable.zp_plan.staff_id"/>            	
	    </td>
	     <td width="10%" align="center" class="TableRow" nowrap>
		<bean:message key="label.zp_job.attendee"/>            	
	    </td>	    
	    <td width="10%" align="center" class="TableRow" nowrap>
		<bean:message key="label.zp_resource.status"/>            	
	    </td>	    
            <td width="10%" align="center" class="TableRow" nowrap>
		<bean:message key="label.edit"/>            	
	    </td>
	    <td width="10%" align="center" class="TableRow" nowrap>
		<bean:message key="label.view"/>            	
	    </td>		    		        	        	        
           </tr>
   	  </thead>
   	   <hrms:extenditerate id="element" name="zpjobForm" property="zpjobForm.list" indexes="indexes"  pagination="zpjobForm.pagination" pageCount="10" scope="session">
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
          <td align="center" class="RecordRow" nowrap>
                   <hrms:checkmultibox name="zpjobForm" property="zpjobForm.select" value="true" indexes="indexes"/>&nbsp;  		   
	    </td>            
            <td align="right" class="RecordRow" nowrap>
                   <bean:write name="element" property="string(zp_job_id)" filter="true"/>&nbsp;
	    </td>
	    <td align="left" class="RecordRow" nowrap>
                   <bean:write name="element" property="string(name)" filter="true"/>&nbsp;
	    </td>
	    <td align="right" class="RecordRow" nowrap>
                   <bean:write name="element" property="string(plan_id)" filter="true"/>&nbsp;
	    </td>
	    <td align="center" class="RecordRow" nowrap>
                   <bean:write name="element" property="string(start_date)" filter="true"/>&nbsp;
	    </td>
	    <td align="center" class="RecordRow" nowrap>
                   <bean:write name="element" property="string(end_date)" filter="true"/>&nbsp;
	    </td>
	    <td align="left" class="RecordRow" nowrap>
                   <bean:write name="element" property="string(principal)" filter="true"/>&nbsp;
	    </td>
	    <td align="left" class="RecordRow" nowrap width="100" style="word-break:break-all">
                   <bean:write name="element" property="string(attendee)" filter="false"/>&nbsp;
	    </td>
	    <td align="left" class="RecordRow" nowrap>
               <hrms:codetoname codeid="23" name="element" codevalue="string(status)" codeitem="codeitem" scope="page"/>  	      
               <bean:write name="codeitem" property="codename" />&nbsp;
	    </td>
	    <td align="center" class="RecordRow" nowrap>
	      <logic:equal name="element" property="string(status)" value="01">
            
		<a href="/hire/zp_job/add_zp_job.do?b_query=link&a_id=<bean:write name="element" property="string(zp_job_id)" filter="true"/>"><img src="/images/edit.gif" border=0></a>            	
	      </logic:equal>
	    </td>
	    <td align="center" class="RecordRow" nowrap>
		<a href="/hire/zp_job/view_zp_job.do?b_query=link&a_id=<bean:write name="element" property="string(zp_job_id)" filter="true"/>"><img src="/images/view.gif" border=0></a>            	
	    </td>
       </tr>
       </hrms:extenditerate>   	    	    	
        
</table>
<table  width="70%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
					<bean:write name="zpjobForm" property="zpjobForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="zpjobForm" property="zpjobForm.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="zpjobForm" property="zpjobForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="zpjobForm" property="zpjobForm.pagination"
				nameId="zpjobForm" propertyId="zpjobProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
<table  width="70%" align="center">
          <tr>
          <logic:equal name="zpjobForm" property="userid" value="su">
            <td align="center">
         	<hrms:submit styleClass="mybutton" property="b_add">
            		<bean:message key="button.insert"/>
	 	</hrms:submit>
         	<hrms:submit styleClass="mybutton" property="b_delete" onclick="return ifdel()">
            		<bean:message key="button.delete"/>
	 	</hrms:submit>    
            </td>
            </logic:equal>
            <logic:notEqual name="zpjobForm" property="userid" value="su">
            <logic:notEqual name="zpjobForm" property="managepriv" value="">
               <td align="center">
         	<hrms:submit styleClass="mybutton" property="b_add">
            		<bean:message key="button.insert"/>
	 	</hrms:submit>
         	<hrms:submit styleClass="mybutton" property="b_delete" onclick="return ifdel()">
            		<bean:message key="button.delete"/>
	 	</hrms:submit>    
            </td>
             </logic:notEqual>          
            </logic:notEqual>
          </tr>          
</table>

</html:form>
