<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script language="javascript">
  function saves()
  {

    	   kqFeastForm.action="/kq/options/add_feast.do?b_save=link";
    	   kqFeastForm.submit();
    	   window.open("/kq/options/feast_type_list.do?b_query=link",'il_body');
         window.close();
  }
</script>
<html:form  action="/kq/options/add_feast">
<div class="fixedDiv3">
     <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		<!--  <td width=10 valign="top" class="tableft"></td>
       		<td width=130 align=center class="tabcenter">&nbsp;<bean:message key="kq.add_feast.style"/>&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="200"></td> --> 
       		<td align=center class="TableRow">&nbsp;<bean:message key="kq.add_feast.style"/>&nbsp;</td>            	      
          </tr> 
          <tr>
            <td  class="framestyle9">
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0"> 
               	 <tr class="list3">
                	  <td align="right" nowrap ><bean:message key="datestyle.year"/>:</td>
                      <td align="left" nowrap >
                	      	<html:text name="kqFeastForm" property="fyear" maxlength="4" size="4" value="1999"/>    	      
                         </td>
                      
                	      <td align="right" nowrap ><bean:message key="kq.search_feast.month"/>:</td>
                	      <td align="left" nowrap >
                	      	<html:text name="kqFeastForm" property="fmonth" maxlength="2" size="2" value="10"/> 	      
                         </td>
              
                	      <td align="right" nowrap ><bean:message key="kq.search_feast.day"/>:</td>
                	      <td align="left" nowrap >
                	      	<html:text name="kqFeastForm" property="fday" maxlength="2" size="2" value="01"/>    	      
                         </td>
                      </tr>                    
                </table>     
              </td>
          </tr>
          <tr class="list3">
            <td align="center">&nbsp;
		           
            </td>
          </tr>                                                   
          <tr class="list3">
            <td align="center" height="35">
            	<input type="button"  value="<bean:message key="button.save"/>" class="mybutton" onclick="saves()"> 
            	<input type="button"  value="<bean:message key="button.return"/>" class="mybutton" onclick="window.close();">  
            </td>
          </tr>          
      </table>
      </div>
</html:form>
