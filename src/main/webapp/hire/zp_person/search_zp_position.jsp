<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.hire.ZppersonForm"%>
<%
	int i=0;
%>
<hrms:themes></hrms:themes> 
<html:form action="/hire/zp_person/search_zp_position"> 
<br>
<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>     
            <td width="10%" align="center" class="TableRow" nowrap>
		&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="lable.zp_plan_detail.pos_id"/>&nbsp;&nbsp;&nbsp;&nbsp;            	
	    </td>     
	    <td width="10%" align="center" class="TableRow" nowrap>
		&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="lable.zp_plan_detail.domain"/>&nbsp;&nbsp;&nbsp;&nbsp;            	
	    </td>
           <td align="center" class="TableRow" nowrap>
		<bean:message key="lable.resource_plan.org_id"/>
	    </td>	    
            <td width="10%" align="center" class="TableRow" nowrap>
		&nbsp;&nbsp;&nbsp;<bean:message key="lable.zp_plan_detail.amount"/>&nbsp;&nbsp;&nbsp;            	
	    </td>            
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.zp_release_pos.valid_date"/>
	    </td> 
	    <td align="center" class="TableRow" nowrap>
		<bean:message key="label.zp_person.zp_person_count"/>
	    </td>     		    		        	        	        
           </tr>
   	  </thead>
   	  <hrms:extenditerate id="element" name="zppersonForm" property="zppersonForm.list" indexes="indexes"  pagination="zppersonForm.pagination" pageCount="20" scope="session">
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
               <a href="/hire/zp_person/search_pos_template.do?zp_pos_id=<bean:write name="element" property="string(zp_pos_id)" filter="true"/>&a0100=<bean:write name="element" property="string(pos_id)" filter="true"/>">
               <hrms:codetoname codeid="@K" name="element" codevalue="string(pos_id)" codeitem="codeitem" scope="page"/>  	      
               <bean:write name="codeitem" property="codename" /></a>
	    </td> 
	    </td>  
	    <td align="left" class="RecordRow" nowrap>
                   <bean:write name="element" property="string(domain)" filter="false"/>
	    </td>          
            <td align="left" class="RecordRow" nowrap>
                 <hrms:codetoname codeid="UN" name="element" codevalue="string(dept_id)" codeitem="codeitem" scope="page"/>  	      
                 <bean:write name="codeitem" property="codename" />
	    </td>
	    
	    <td align="right" class="RecordRow" nowrap>
                   <bean:write name="element" property="string(amount)" filter="true"/>
	    </td>
	    <td align="center" class="RecordRow" nowrap>
                   <bean:write name="element" property="string(valid_date)" filter="true"/>
	    </td>
	    <td align="right" class="RecordRow" nowrap>
                   <bean:write name="element" property="string(plan_id)" filter="true"/>
	    </td>	    
       </tr>
       </hrms:extenditerate>   	    	    	
        
</table>
</html:form>
