<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
int i=0;
%>
<script language="javascript">
  function adds()
  {
    	   target_url="/kq/options/add_feast.do?br_add=link";
    	   newwindow=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=260,left=320,width=496,height=204'); 
	  
  }

</script>

<html:form  action="/kq/options/feast_type_list">
 <br>
<br>
 <fieldset align="center" style="width:50%;">
 <legend ><bean:message key="kq.search_feast.holiday"/></legend>
  <table width="50%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
       <html:hidden name="kqFeastForm" property="feast_id"/>     	 
    <tr> 
      <td align="center" class="TableRow" nowrap><bean:message key="kq.search_feast.select"/></td>
      <td align="center" class="TableRow" nowrap><bean:message key="hmuster.label.year"/></td>
      <td align="center" class="TableRow" nowrap><bean:message key="kq.search_feast.month"/></td>
      <td align="center" class="TableRow" nowrap><bean:message key="kq.search_feast.day"/></td>
      </tr>
    <tr class="trShallow"> 
  </tr>
   <hrms:extenditerate id="element" name="kqFeastForm" property="kqFeastForm.list" indexes="indexes"  pagination="kqFeastForm.pagination" pageCount="10" scope="session">
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
               <hrms:checkmultibox name="kqFeastForm" property="kqFeastForm.select" value="true" indexes="indexes"/>&nbsp;
            </td>  
            <td align="left" class="RecordRow" nowrap>                
                   <bean:write  name="element" property="fyear" filter="true"/>&nbsp;
              </td>
              <td align="left" class="RecordRow" nowrap>                
                   <bean:write  name="element" property="fmonth" filter="true"/>&nbsp;
              </td>
               <td align="left" class="RecordRow" nowrap>                
                   <bean:write  name="element" property="fday" filter="true"/>&nbsp;
              </td>            	    		        	        	        
          </tr>
       </hrms:extenditerate>
  </table>
<table  width="50%" align="center">
          <tr>
           <td align="left">
              	<html:hidden name="kqFeastForm" property="tolastpageflag" value="no"/>
                <input type="button"  value="<bean:message key="button.insert"/>" class="mybutton" onclick="adds()">   
         	     <hrms:submit styleClass="mybutton" property="b_delete">
            		   <bean:message key="button.delete"/>
	   	          </hrms:submit>
	   	          <hrms:submit styleClass="mybutton" property="b_back">
            		<bean:message key="button.return"/>
	   	     </hrms:submit>
            </td>
          </tr>          
</table>
	</fieldset>

</html:form>