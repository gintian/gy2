<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript">
<!--
function ret()
{
          positionStatForm.action="/hire/zp_options/stat/positionstat/positionstat.do?b_query=link";
          positionStatForm.submit();
}
//-->
</script>

<html:form action="/hire/zp_options/stat/positionstat/person_wish">
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0">
<tr>
<td>
<table>
<tr>
<td align="left">${positionStatForm.zp_pos_name}&nbsp;&nbsp;<bean:message key="workdiary.message.total.person"/>: ${positionStatForm.count}
</td>
</tr>
</table>
</td>
</tr>
<tr>
<td>
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
	 <tr>
           			<td align="center" class="TableRow" nowrap><bean:message key="hire.apply.name"/></td> 
           			<td align="center" class="TableRow" nowrap><bean:message key="hire.wish"/></td>
           			
           </tr>
      </thead>
   
   <% int i=0; String className="trShallow"; %>
  	<hrms:paginationdb id="element" name="positionStatForm" sql_str="${positionStatForm.select_sql}" fromdict="1" where_str="${positionStatForm.where_sql}" columns="${positionStatForm.columns}" order_by="${positionStatForm.order_sql}" page_id="pagination" pagerows="15" indexes="indexes">
      
       
         <%
			  if(i%2==0)
			  	className="trShallow";
			  else
			  	className="trDeep";
			  i++;
			  
			  %>
	   <tr class='<%=className%>' >
          <td align="left" class="RecordRow" width="25%" nowrap>
         &nbsp;<bean:write name="element" property="a0101"/>&nbsp;
         </td>
   
          <td align="right" class="RecordRow" width="25%" nowrap>
          <bean:write name="element" property="thenumber"/>&nbsp;
         </td>      
            </tr>		    
	</hrms:paginationdb>
    </table>
    </td>
    </tr>
    <tr>
    <td>
    <table  width="100%"  class='RecordRowP'  align='center' >
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="hmuster.label.d"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="hmuster.label.paper"/>
					<bean:message key="hmuster.label.total"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.every.row"/>
					<bean:message key="hmuster.label.total"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="hmuster.label.paper"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="positionStatForm" property="pagination" nameId="positionStatForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
		
		
		
   	  
</table> 
</td>
</tr>
<tr>
<td>
<table width="100%" align="center">
<tr>
<td align="center">
<input type="button" name="back" class="mybutton" value="<bean:message key="button.return"/>" onclick="ret();"/>
</td>
</tr>
</table>
</td>
</tr>
</table>
</html:form>
    