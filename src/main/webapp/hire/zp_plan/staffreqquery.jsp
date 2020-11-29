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
<html:form action="/hire/zp_plan/add_zp_plan">

<br>
<table width="85%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.select"/>&nbsp;
            </td>           
            <td align="center" class="TableRow" nowrap>
		<bean:message key="lable.hiremanage.gather_id"/>&nbsp;
	    </td>
            <td width="10%" align="center" class="TableRow" nowrap>
		<bean:message key="lable.hiremanage.org_id"/>            	
	    </td>	    
            <td width="10%" align="center" class="TableRow" nowrap>
		<bean:message key="lable.hiremanage.dept_id"/>            	
	    </td>
	    <td align="center" class="TableRow" nowrap>
		<bean:message key="lable.hiremanage.valid_date"/>&nbsp;
            </td>           
            <td align="center" class="TableRow" nowrap>
		<bean:message key="lable.hiremanage.gather_type"/>&nbsp;
	    </td>
            <td width="10%" align="center" class="TableRow" nowrap>
		<bean:message key="lable.hiremanage.create_date"/>            	
	    </td>	    
            <td width="10%" align="center" class="TableRow" nowrap>
		<bean:message key="lable.hiremanage.staff_id"/>            	
	    </td>
	    <td width="10%" align="center" class="TableRow" nowrap>
		<bean:message key="lable.hiremanage.status"/>            	
	    </td>	    		    		        	        	        
           </tr>
   	  </thead>
   	   <hrms:extenditerate id="element" name="zpplanForm" property="zpgatherForm.list" indexes="indexes"  pagination="zpgatherForm.pagination" pageCount="10" scope="session">
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
                   <hrms:checkmultibox name="zpplanForm" property="zpgatherForm.select" value="true" indexes="indexes"/>&nbsp;  		   
	    </td>            
            <td align="left" class="RecordRow" nowrap>
                   <bean:write name="element" property="string(gather_id)" filter="true"/>&nbsp;
	    </td>
	    <td align="left" class="RecordRow" nowrap>
                   <bean:write name="element" property="string(org_id)" filter="true"/>&nbsp;
	    </td>
	    <td align="left" class="RecordRow" nowrap>
                   <bean:write name="element" property="string(dept_id)" filter="true"/>&nbsp;
	    </td>
	    <td align="left" class="RecordRow" nowrap>
                   <bean:write name="element" property="string(valid_date)" filter="true"/>&nbsp;
	    </td>
	    <td align="left" class="RecordRow" nowrap>
	         <logic:equal name="element" property="string(gather_type)" value="0">
                      <bean:message key="label.hiremanage.gather_type0"/>&nbsp;
                 </logic:equal>
                  <logic:notEqual name="element" property="string(gather_type)" value="0">
                      <bean:message key="label.hiremanage.gather_type1"/>&nbsp;
                 </logic:notEqual>
	    </td>
	    <td align="left" class="RecordRow" nowrap>
                   <bean:write name="element" property="string(create_date)" filter="true"/>&nbsp;
	    </td>
	    <td align="left" class="RecordRow" nowrap>
                   <bean:write name="element" property="string(staff_id)" filter="true"/>&nbsp;
	    </td>
	    <td align="left" class="RecordRow" nowrap>
	       <hrms:codetoname codeid="23" name="element" codevalue="string(status)" codeitem="codeitem" scope="page"/>  	      
               <bean:write name="codeitem" property="codename" />&nbsp; 
	    </td>
       </tr>
       </hrms:extenditerate>   	    	    	
        
</table>
<table  width="70%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
					<bean:write name="zpplanForm" property="zpgatherForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="zpplanForm" property="zpgatherForm.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="zpplanForm" property="zpgatherForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="zpplanForm" property="zpgatherForm.pagination"
				nameId="zpgatherForm" propertyId="zpgatherProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
<table  width="70%" align="center">
          <tr>
            <td align="center">
         	<hrms:submit styleClass="mybutton" property="br_return"><bean:message key="button.return"/></hrms:submit>    
        
            </td>
          </tr>          
</table>

</html:form>
