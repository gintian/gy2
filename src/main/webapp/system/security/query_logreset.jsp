<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
   int i = 0;
%>

<html:form action="/system/security/query_log">
      <br>
      <br>
      <table width="700" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr>
            <td colspan="4">
               <table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1" bordercolor = "#223377"> 
               <tr> 
                  <td height="20" align="center" class="TableRow" nowrap colspan="4">日志查询</td>
               </tr>    
                  <tr class="trDeep1">
                     <td align="right" nowrap valign="center"><bean:message key="column.submit.username"/></td>
                     <td align="left"  nowrap valign="center">
                          &nbsp;<input type="text" name="commitor" maxlength="12" class="text6">&nbsp;
                      </td>
                     
                     <td align="right" nowrap valign="center"><bean:message key="column.submit.function"/></td>
                      <td align="left"  nowrap valign="center">
                          &nbsp;<input type="text" name="name" maxlength="12" class="text6">&nbsp;
                      </td>   
                   </tr>
                   <tr class="trDeep1">
                     <td align="right" nowrap valign="center"><bean:message key="column.submit.begindate"/></td>
                     <td align="left"  nowrap valign="center">
                          &nbsp;<input type="text" name="beginexectime" maxlength="12" class="text6">&nbsp;
                      </td>   
                     <td align="right" nowrap valign="center"><bean:message key="column.submit.enddate"/></td>
                     <td align="left"  nowrap valign="center">
                          &nbsp;<input type="text" name="endexectime" maxlength="12" class="text6">&nbsp;
                      </td>
                          
                   </tr>  
                   
 <tr>
  <td align="center" class="trDeep1" colspan="4" >
    &nbsp;&nbsp;
               <hrms:submit styleClass="mybutton" property="b_search"><bean:message key="button.query"/>
	 	</hrms:submit>
	 	<hrms:submit styleClass="mybutton" property="br_reset"><bean:message key="button.clear"/></hrms:submit>    
  </td>
 </tr>                                                        
          
          </table>     
        </td>
      </tr>         
           
          <tr class="list3">
            <td align="center" colspan="2">
		&nbsp           
            </td>
          </tr>   

          <tr>
          <table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
		 <bean:message key="column.select"/>&nbsp;
            </td>           
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.submit.functionID"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.submit.function"/>&nbsp;
	    </td>
	    <td align="center" class="TableRow" nowrap>
		<bean:message key="column.submit.begindate"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.submit.enddate"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.submit.execstate"/>            	
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.submit.romoteaddr"/>            	
	    </td> 
	    <td align="center" class="TableRow" nowrap>
		<bean:message key="column.submit.username"/>            	
	    </td>    	    		        	        	        
           </tr>
   	  </thead>
   	    <hrms:extenditerate id="element" name="queryLogForm" property="queryLogForm.list" indexes="indexes"  pagination="queryLogForm.pagination" pageCount="10" scope="session">
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
	   	 <hrms:checkmultibox name="queryLogForm" property="queryLogForm.select" value="true" indexes="indexes"/>&nbsp;
	    </td>            
         
            <td align="left" class="RecordRow" nowrap>
                    <bean:write  name="element" property="string(id)" filter="true"/>&nbsp;
	    </td>
            <td align="left" class="RecordRow" nowrap>
                    <bean:write  name="element" property="string(name)" filter="true"/>&nbsp;
	    </td>
            <td align="left" class="RecordRow" nowrap>
                    <bean:write  name="element" property="string(beginexectime)" filter="true"/>&nbsp;
	    </td> 	                
            <td align="left" class="RecordRow" nowrap>
                    <bean:write  name="element" property="string(endexectime)" filter="true"/>&nbsp;
	    </td>
	    <td align="left" class="RecordRow" nowrap>
                    <bean:write  name="element" property="string(execstatus)" filter="true"/>&nbsp;
	    </td>
            <td align="left" class="RecordRow" nowrap>
                    <bean:write  name="element" property="string(remoteaddr)" filter="true"/>&nbsp;
	    </td> 	                
            <td align="left" class="RecordRow" nowrap>
                    <bean:write  name="element" property="string(commitor)" filter="true"/>&nbsp;
	    </td>    	   	    
               		        	        	        
          </tr>
        </hrms:extenditerate>
        
     </table>
     <table  width="70%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor">第
					<bean:write name="queryLogForm" property="queryLogForm.pagination.current" filter="true" />
					页
					共
					<bean:write name="queryLogForm" property="queryLogForm.pagination.count" filter="true" />
					条
					共
					<bean:write name="queryLogForm" property="queryLogForm.pagination.pages" filter="true" />
					页
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="queryLogForm" property="queryLogForm.pagination"
				nameId="queryLogForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
    </table>
     <table  width="70%" align="center">
          <tr>
            <td align="center">
         	<hrms:submit styleClass="mybutton" property="b_clear">
            		<bean:message key="button.clearup"/>
	 	</hrms:submit>
         	<hrms:submit styleClass="mybutton" property="b_delete">
            		<bean:message key="button.delete"/>
	 	</hrms:submit>
        
            </td>
          </tr>          
    </table>
       
    </tr>        
 </table>
</html:form>
