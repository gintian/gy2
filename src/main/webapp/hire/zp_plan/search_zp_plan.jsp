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

<html:form action="/hire/zp_plan/search_zp_plan">

<br>
<table width="85%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.select"/>&nbsp;
            </td>           
            <td align="center" class="TableRow" nowrap>
		<bean:message key="lable.zp_plan.plan_id"/>&nbsp;
	    </td>
            <td width="10%" align="center" class="TableRow" nowrap>
		<bean:message key="lable.zp_plan.name"/>            	
	    </td>	    
            <td width="10%" align="center" class="TableRow" nowrap>
		<bean:message key="lable.resource_plan.org_id"/>            	
	    </td>
	    <td align="center" class="TableRow" nowrap>
		<bean:message key="lable.zp_plan.start_date"/>&nbsp;
            </td>           
            <td align="center" class="TableRow" nowrap>
		<bean:message key="lable.zp_plan.end_date"/>&nbsp;
	    </td>
            <td width="10%" align="center" class="TableRow" nowrap>
		<bean:message key="lable.zp_plan.budget_fee"/>            	
	    </td>
	     <td width="10%" align="center" class="TableRow" nowrap>
		<bean:message key="lable.zp_plan.plan_invite_amount"/>            	
	    </td>
	    <td width="10%" align="center" class="TableRow" nowrap>
		<bean:message key="lable.zp_plan.dept_id"/>            	
	    </td>	    
            <td width="10%" align="center" class="TableRow" nowrap>
		<bean:message key="lable.zp_plan.staff_id"/>            	
	    </td>
	    <td width="10%" align="center" class="TableRow" nowrap>
		<bean:message key="lable.zp_plan.domain"/>            	
	    </td>
	    <td width="10%" align="center" class="TableRow" nowrap>
		<bean:message key="lable.zp_plan.zp_object"/>            	
	    </td>
	    <td width="10%" align="center" class="TableRow" nowrap>
		<bean:message key="lable.zp_plan.status"/>            	
	    </td>	    
            <td width="10%" align="center" class="TableRow" nowrap>
              <hrms:priv func_id="240020101">
		<bean:message key="label.edit"/>    
	      </hrms:priv>         	
	    </td>
	    <td width="10%" align="center" class="TableRow" nowrap>
	      <hrms:priv func_id="240020102">
		<bean:message key="label.view"/> 
	      </hrms:priv>           	
	    </td>		    		        	        	        
           </tr>
   	  </thead>
   	   <hrms:extenditerate id="element" name="zpplanForm" property="zpplanForm.list" indexes="indexes"  pagination="zpplanForm.pagination" pageCount="10" scope="session">
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
                   <hrms:checkmultibox name="zpplanForm" property="zpplanForm.select" value="true" indexes="indexes"/>&nbsp;  		   
	    </td>            
            <td align="right" class="RecordRow" nowrap>
                   <bean:write name="element" property="string(plan_id)" filter="true"/>&nbsp;
	    </td>
	    <td align="left" class="RecordRow" nowrap>
                   <bean:write name="element" property="string(name)" filter="true"/>&nbsp;
	    </td>
	    <td align="left" class="RecordRow" nowrap>
               <hrms:codetoname codeid="UN" name="element" codevalue="string(org_id)" codeitem="codeitem" scope="page"/>  	      
               <bean:write name="codeitem" property="codename" />&nbsp;
	    </td>
	    <td align="left" class="RecordRow" nowrap>
                   <bean:write name="element" property="string(start_date)" filter="true"/>&nbsp;
	    </td>
	    <td align="right" class="RecordRow" nowrap>
                   <bean:write name="element" property="string(end_date)" filter="true"/>&nbsp;
	    </td>
	    <td align="right" class="RecordRow" nowrap>
                   <bean:write name="element" property="string(budget_fee)" filter="true"/>&nbsp;
	    </td>
	    <td align="right" class="RecordRow" nowrap>
                   <bean:write name="element" property="string(plan_invite_amount)" filter="true"/>&nbsp;
	    </td>
	    <td align="left" class="RecordRow" nowrap>
               <hrms:codetoname codeid="UM" name="element" codevalue="string(dept_id)" codeitem="codeitem" scope="page"/>  	      
               <bean:write name="codeitem" property="codename" />&nbsp;
	    </td>
	    <td align="left" class="RecordRow" nowrap>
                   <bean:write name="element" property="string(staff_id)" filter="true"/>&nbsp;
	    </td>
	    <td align="left" class="RecordRow" nowrap>
                   <bean:write name="element" property="string(domain)" filter="true"/>&nbsp;
	    </td>
	    <td align="left" class="RecordRow" nowrap>
                <logic:equal name="element" property="string(zp_object)" value="01">
                    <bean:message key="lable.zp_plan.zp_object0"/>&nbsp;
                </logic:equal>
                <logic:notEqual name="element" property="string(zp_object)" value="01">
                      <bean:message key="lable.zp_plan.zp_object1"/>&nbsp;
                </logic:notEqual> 
	    </td>
	    <td align="left" class="RecordRow" nowrap>
               <hrms:codetoname codeid="23" name="element" codevalue="string(status)" codeitem="codeitem" scope="page"/>  	      
               <bean:write name="codeitem" property="codename" />&nbsp;
	    </td>
	    <td align="center" class="RecordRow" nowrap>
	       <logic:equal name="element" property="string(status)" value="01">
	        <hrms:priv func_id="240020101">
                 <a href="/hire/zp_plan/add_zp_plan.do?b_query=link&plan_id_value=<bean:write name="element" property="string(plan_id)" filter="true"/>"><img src="/images/edit.gif" border=0></a>            	
	        </hrms:priv>
	      </logic:equal>
	    </td>
	    <td align="center" class="RecordRow" nowrap>
	      <hrms:priv func_id="240020102">
		<a href="/hire/zp_plan/view_zp_plan.do?b_query=link&plan_id_value=<bean:write name="element" property="string(plan_id)" filter="true"/>"><img src="/images/view.gif" border=0></a>            	
	      </hrms:priv>
	    </td>
       </tr>
       </hrms:extenditerate>   	    	    	
        
</table>
<table  width="70%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
					<bean:write name="zpplanForm" property="zpplanForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="zpplanForm" property="zpplanForm.pagination.count" filter="true" />
				<bean:message key="label.page.row"/>
					<bean:write name="zpplanForm" property="zpplanForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="zpplanForm" property="zpplanForm.pagination"
				nameId="zpplanForm" propertyId="zpplanProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
<table  width="70%" align="center">
          <tr>
          <logic:equal name="zpplanForm" property="userid" value="su">
            <td align="center">
         	<hrms:submit styleClass="mybutton" property="b_add">
            		<bean:message key="button.insert"/>
	 	</hrms:submit>
         	<hrms:submit styleClass="mybutton" property="b_delete" onclick="return ifdel()">
            		<bean:message key="button.delete"/>
	 	</hrms:submit>     
            </td>
            </logic:equal>
             <logic:notEqual name="zpplanForm" property="userid" value="su">
               <hrms:priv func_id="240020101">
               <td align="center">
         	<hrms:submit styleClass="mybutton" property="b_add">
            		<bean:message key="button.insert"/>
	 	</hrms:submit>
         	<hrms:submit styleClass="mybutton" property="b_delete" onclick="return ifdel()">
            		<bean:message key="button.delete"/>
	 	</hrms:submit>     
              </td>
             </hrms:priv>          
            </logic:notEqual>
          </tr>          
</table>
</html:form>
