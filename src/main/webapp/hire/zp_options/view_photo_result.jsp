<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	int i=0;
%>
<hrms:themes></hrms:themes>
<html:form action="/hire/zp_options/view_photo_result">
<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1">
          <hrms:paginationdb id="element" name="posFilterSetForm" sql_str="posFilterSetForm.strsql" table="" where_str="posFilterSetForm.strwhere" columns="posFilterSetForm.columns" order_by=" order by b0110,e0122,e01a1,A0000" page_id="pagination" pagerows="21">
          <%
          if(i%7==0)
          {
          %>
          <tr>
          <%
          }
          %>             
          <td align="center" NOWRAP>
          	<a href="/workbench/browse/showselfinfo.do?b_search=link&userbase=<bean:write name="posFilterSetForm" property="dbpre" filter="true"/>&a0100=<bean:write name="element" property="a0100" filter="true"/>&flag=notself&returnvalue=44">
          	<hrms:ole name="element" dbpre="posFilterSetForm.dbpre" a0100="a0100" scope="page" height="120" width="80"/>
          	</a>
          	<br><a href="/workbench/browse/showselfinfo.do?b_search=link&userbase=<bean:write name="posFilterSetForm" property="dbpre" filter="true"/>&a0100=<bean:write name="element" property="a0100" filter="true"/>&flag=notself&returnvalue=44"><bean:write name="element" property="a0101" filter="true"/>&nbsp;</a><br>
          	<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page"/>  	      
          	<a href="/workbench/browse/showselfinfo.do?b_search=link&userbase=<bean:write name="posFilterSetForm" property="dbpre" filter="true"/>&a0100=<bean:write name="element" property="a0100" filter="true"/>&flag=notself&returnvalue=44"><bean:write name="codeitem" property="codename" />&nbsp;</a><br>  
          	<hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
          	<a href="/workbench/browse/showselfinfo.do?b_search=link&userbase=<bean:write name="posFilterSetForm" property="dbpre" filter="true"/>&a0100=<bean:write name="element" property="a0100" filter="true"/>&flag=notself&returnvalue=44"><bean:write name="codeitem" property="codename" />&nbsp;</a>  
          </td> 
          <%
          if((i+1)%7==0)
          {%>
          </tr>
          <%
          }
          i++;          
          %>         
        </hrms:paginationdb>
        
</table>
<table  width="70%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="pagination" property="count" filter="true" />
				 <bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="posFilterSetForm" property="pagination" nameId="posFilterSetForm" scope="page">
				</hrms:paginationdblink>
		       </td>
		</tr>
</table>
<table  width="70%" align="center">
          <tr>
            <td align="left">
         	    <hrms:submit styleClass="mybutton" property="br_return">
            		<bean:message key="button.return"/>
	 	    </hrms:submit>
            </td>
          </tr>          
</table>
</html:form>
